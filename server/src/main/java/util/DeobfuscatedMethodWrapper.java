package util;

public class DeobfuscatedMethodWrapper
{
	private String packages = "";
	private String className = "";
	private String methodName = "";
	private int lineStart = -1;
	private int lineEnd = -1;
	
	public DeobfuscatedMethodWrapper(String packages, String className, String methodName, int lineStart, int lineEnd)
	{
		this.packages = packages;
		this.className = className;
		this.methodName = methodName;
		this.lineStart = lineStart;
		this.lineEnd = lineEnd;
	}
	
	public boolean matchesLineNumber(int lineNumber)
	{
		if (methodName.equals("<init>"))
		{
			//<init> doesn't appear in the map file. There'll only be one, so don't need to match on line number
			return true;
		}
		
		return lineStart <= lineNumber 
		  && lineNumber <= lineEnd;
	}
	
	public String getReplacementStackTraceLine()
	{
		return packages + "." + className + "." + methodName;
	}
	
	@Override
	public String toString()
	{
		return packages + "." + className + "." + methodName + "(" + lineStart + ":" + lineEnd + ")";
	}
}
