
package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Debug implements CoreRegistry
{
	private static final long ERROR_MESSAGE_DELAY_MILLIS = 10000; //10s

	private static long lastErrorMillis = -1;
	
	private static DebugOutput output = null;
	private static DebugExtension debugExtension = null;
	private static boolean logToSystemOut = false;

	private static ThreadFactory loggerFactory = new ThreadFactory()
	{
		@Override
		public Thread newThread(Runnable r)
		{
			return new Thread(r, "Logger");
		}
	};
	private static ExecutorService logService = Executors.newFixedThreadPool(1, loggerFactory);
	

	public static void append(String text)
	{
		append(text, true);
	}
	public static void append(String text, boolean logging)
	{
		append(text, logging, true);
	}
	private static void append(final String text, boolean logging, final boolean includeDate)
	{
		append(text, logging, includeDate, null);
	}
	private static void append(final String text, boolean logging, final boolean includeDate, final BooleanWrapper haveStackTraced)
	{
		if (!logging)
		{
			return;
		}
		
		Runnable logRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				String time = "";
				if (includeDate)
				{
					time = getCurrentTimeForLogging();
				}
				
				output.append("\n" + time + text);
				
				if (logToSystemOut)
				{
					System.out.println(time + text);
				}
				
				if (haveStackTraced != null)
				{
					haveStackTraced.setValue(true);
				}
			}
		};
		
		logService.execute(logRunnable);
	}
	
	public static void appendWithoutDate(String text)
	{
		appendWithoutDate(text, true);
	}
	public static void appendWithoutDate(String text, boolean logging)
	{
		append("                                      " + text, logging, false);
	}
	
	public static void appendTabbed(String text)
	{
		appendWithoutDate("	" + text);
	}
	
	public static void appendBanner(String text)
	{
		appendBanner(text, true);
	}
	
	public static void appendBannerWithoutDate(String text)
	{
		int length = text.length();
		
		String starStr = "";
		for (int i=0; i<length + 4; i++)
		{
			starStr += "*";
		}
		
		appendWithoutDate(starStr);
		appendWithoutDate(text);
		appendWithoutDate(starStr);
	}
	
	public static void appendBanner(String text, boolean logging)
	{
		if (logging)
		{
			int length = text.length();
			
			String starStr = "";
			for (int i=0; i<length + 4; i++)
			{
				starStr += "*";
			}
			
			append(starStr, true);
			append(text, true);
			append(starStr, true);
		}
	}
	
	/**
	 * Stack Trace methods
	 */
	public static void stackTrace(String reason)
	{
		Throwable t = new Throwable();
		stackTrace(t, reason);
	}
	public static void stackTrace(Throwable t)
	{
		stackTrace(t, "");
	}
	public static void stackTrace(Throwable t, String message)
	{
		stackTrace(t, message, false);
	}
	public static void stackTraceNoError(Throwable t)
	{
		stackTrace(t, "", true);
	}
	public static void stackTraceNoError(Throwable t, String message)
	{
		stackTrace(t, message, true);
	}
	public static void stackTrace(Throwable t, String message, boolean suppressError)
	{
		if (debugExtension != null
		  && !suppressError)
		{
			boolean showError = System.currentTimeMillis() - lastErrorMillis > ERROR_MESSAGE_DELAY_MILLIS;
			debugExtension.exceptionCaught(showError);
			if (showError)
			{
				lastErrorMillis = System.currentTimeMillis();
			}
		}
		
		String datetime = getCurrentTimeForLogging();
		
		String trace = "";
		if (!message.equals(""))
		{
			trace += datetime + message + "\n";
		}
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		trace += datetime + sw.toString(); 
		
		BooleanWrapper haveAppendedStackTrace = new BooleanWrapper(false);
		append(trace, true, false, haveAppendedStackTrace);
	}

	public static void stackTraceSilently(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String trace = sw.toString(); 
		t.printStackTrace();
		
		append(trace, true);
	}
	
	public static void newLine()
	{
		appendWithoutDate("");
	}
	
	public static void dumpList(String name, List<?> list)
	{
		String s = name;
		if (list == null)
		{
			s += ": null";
			appendWithoutDate(s);
			return;
		}
		
		s += "(size: " + list.size() + "): ";
		
		for (int i=0; i<list.size(); i++)
		{
			if (i > 0)
			{
				s += "\n";
			}
			
			s += list.get(i);
		}
		
		appendWithoutDate(s);
	}
	
	public static String getCurrentTimeForLogging()
	{
		long time = System.currentTimeMillis();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm:ss.SSS");
		return sdf.format(time) + "   ";
	}
	
	public static void initialise(DebugOutput output)
	{
		Debug.output = output;
	}
	
	public static void setDebugExtension(DebugExtension debugExtension)
	{
		Debug.debugExtension = debugExtension;
	}
	public static void setLogToSystemOut(boolean logToSystemOut)
	{
		Debug.logToSystemOut = logToSystemOut;
	}
}