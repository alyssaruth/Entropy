package util;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LogDeobfuscator
{
	private static final String CLASS_MEMBER_PREFIX = "    ";
	private static final String STACK_LINE_PREFIX = "	at ";
	
	private static ConcurrentHashMap<String, ArrayList<DeobfuscatedMethodWrapper>> hmStackTraceStringToMethodWrapper = new ConcurrentHashMap<>();
	
	/**
	 * Parse the obfuscation log file into a static HashMap to use for deobfuscating logs
	 */
	public static void parseObfuscationMapFile(Path mapFilePath)
	{
		String line = "";
		
		try
		{
			Charset charset = Charset.forName("US-ASCII");
			List<String> mapLines = Files.readAllLines(mapFilePath, charset);
			int obfuscatedMethods = 0;
			
			String packages = "";
			String className = "";
			String obfuscatedPackageAndClass = "";
			for (int i=0; i<mapLines.size(); i++)
			{
				line = mapLines.get(i);
				if (!line.startsWith(CLASS_MEMBER_PREFIX))
				{
					//This is a new package/class combination
					int index = line.indexOf(' ');
					String packageAndClass = line.substring(0, index);
					
					ArrayList<String> toks = StringUtil.getListFromDelims(packageAndClass, ".");
					className = toks.get(toks.size() - 1);
					packages = packageAndClass.replace("." + className, "");
					
					int secondSpaceIndex = line.indexOf(' ', index+1);
					obfuscatedPackageAndClass = line.substring(secondSpaceIndex+1, line.length() - 1);
					
					//Add the <init> method to our hashmap
					String initKey = obfuscatedPackageAndClass + ".<init>";
					DeobfuscatedMethodWrapper wrapper = new DeobfuscatedMethodWrapper(packages, className, "<init>", -1, -1);
					addEntryToHashMap(initKey, wrapper);
					obfuscatedMethods++;
					continue;
				}
				
				//Strip off the leading space
				line = line.replace(CLASS_MEMBER_PREFIX, "");
				if (!Character.isDigit(line.charAt(0)))
				{
					//Doesn't start with a numeric, so skip this line (it's a variable not a method)
					continue;
				}
				
				//Get the line start/end from the start of the line
				ArrayList<String> colonTokens = StringUtil.getListFromDelims(line, ":");
				int lineStart = Integer.parseInt(colonTokens.get(0));
				int lineEnd = Integer.parseInt(colonTokens.get(1));
				
				//Now split by spaces. The last element is the obfuscated method, the third to last is the method
				ArrayList<String> spaceTokens = StringUtil.getListFromDelims(line, " ");
				String obfuscatedMethodName = spaceTokens.get(spaceTokens.size() - 1);
				String methodName = spaceTokens.get(spaceTokens.size() - 3);
				int bracketIndex = methodName.indexOf('(');
				methodName = methodName.substring(0, bracketIndex);
				
				DeobfuscatedMethodWrapper wrapper = new DeobfuscatedMethodWrapper(packages, className, methodName, lineStart, lineEnd);
				
				String key = obfuscatedPackageAndClass + "." + obfuscatedMethodName;
				addEntryToHashMap(key, wrapper);
				obfuscatedMethods++;
			}
			
			Debug.append("Populated obfuscation map with " + obfuscatedMethods + " methods");
		}
		catch (Throwable t)
		{
			Debug.append("Failed to parse line " + line);
			Debug.stackTrace(t, "Failed to parse obfuscation map: " + mapFilePath);
			hmStackTraceStringToMethodWrapper = null;
		}
	}
	private static void addEntryToHashMap(String key, DeobfuscatedMethodWrapper wrapper)
	{
		ArrayList<DeobfuscatedMethodWrapper> values = hmStackTraceStringToMethodWrapper.get(key);
		if (values == null)
		{
			values = new ArrayList<>();
			hmStackTraceStringToMethodWrapper.put(key, values);
		}
		
		values.add(wrapper);
	}
	
	/**
	 * Deobfuscating
	 */
	public static String deobfuscateStackTraces(String log)
	{
		//If we failed to parse the obfuscation map on start-up, don't try to do this.
		if (hmStackTraceStringToMethodWrapper == null)
		{
			return log;
		}
		
		StringBuilder sb = new StringBuilder();
		String[] logLines = log.split("\n");
		for (int i=0; i<logLines.length; i++)
		{
			String logLine = logLines[i];
			if (!shouldDeobfuscateLine(logLine))
			{
				sb.append("\n");
				sb.append(logLine);
				continue;
			}
			
			String deobfuscatedLine = deobfuscateStackLine(logLine);
			sb.append(deobfuscatedLine);
		}
		
		return sb.toString();
	}
	private static boolean shouldDeobfuscateLine(String logLine)
	{
		return logLine.startsWith(STACK_LINE_PREFIX)
		  && !logLine.contains("Unknown Source")
		  && !logLine.contains("Native Method");
	}
	private static String deobfuscateStackLine(String stackLine)
	{
		String ret = "";
		try
		{
			//"	at c.s.c(MainScreen.java:810)\r" -> "c.s.c(MainScreen.java:810)\r"
			ret += "\n" + STACK_LINE_PREFIX;
			stackLine = stackLine.replace(STACK_LINE_PREFIX, "");
			
			//Tokenise on : to get "810)\r", then remove the trailing rubbish
			ArrayList<String> colonToks = StringUtil.getListFromDelims(stackLine, ":");
			String lineStr = colonToks.get(colonToks.size()-1);
			lineStr = lineStr.replace(")\r", "");
			int lineNo = Integer.parseInt(lineStr);
			
			//Get everything up to the open bracket, so "c.s.c". Look up in hm with lineNumber
			int bracketIndex = stackLine.indexOf('(');
			String obfuscatedKey = stackLine.substring(0, bracketIndex);
			String newKey = getReplacementLineForStackLine(obfuscatedKey, lineNo);
			
			//"	at screen.MainScreen.processDevModeCommand(MainScreen.java:810)\r"
			stackLine = stackLine.replace(obfuscatedKey, newKey);
			ret += stackLine;
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t, "Failed to deobfuscate line [" + stackLine + "]");
			return STACK_LINE_PREFIX + stackLine;
		}
		
		return ret;
	}
	private static String getReplacementLineForStackLine(String obfuscatedKey, int lineNo)
	{
		ArrayList<DeobfuscatedMethodWrapper> list = hmStackTraceStringToMethodWrapper.get(obfuscatedKey);
		for (int i=0; i<list.size(); i++)
		{
			DeobfuscatedMethodWrapper wrapper = list.get(i);
			if (wrapper.matchesLineNumber(lineNo))
			{
				return wrapper.getReplacementStackTraceLine();
			}
		}
		
		Debug.stackTrace("Failed to find deobfuscated line for " + obfuscatedKey + " (line: " + lineNo + ")");
		Debug.dumpList("DeobfuscatedMethodWrappers", list);
		return obfuscatedKey;
	}
}
