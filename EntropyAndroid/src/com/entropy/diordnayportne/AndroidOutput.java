package com.entropy.diordnayportne;

import util.DebugOutput;

public class AndroidOutput implements DebugOutput
{
	private String logs = "";
	
	public AndroidOutput()
	{
		
	}
	
	@Override
	public void append(String text)
	{
		logs += text;
	}

	@Override
	public String getLogs()
	{
		return logs;
	}
	
}
