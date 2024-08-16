
package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Debug implements CoreRegistry
{
	private static DebugOutput output = null;
	private static boolean logToSystemOut = false;

	private static ThreadFactory loggerFactory = r -> new Thread(r, "Logger");
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
		append("                                      " + text, true, false);
	}
	
	public static void appendBanner(String text)
	{
		appendBanner(text, true);
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
	public static void stackTraceNoError(Throwable t)
	{
		stackTrace(t, "");
	}
	public static void stackTrace(Throwable t, String message)
	{
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

	public static void setLogToSystemOut(boolean logToSystemOut)
	{
		Debug.logToSystemOut = logToSystemOut;
	}
}