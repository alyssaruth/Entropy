package com.entropy.diordnayportne;

import util.Base64Interface;
import android.util.Base64;

public class Base64Android implements Base64Interface
{
	private static final int FLAGS = Base64.NO_WRAP;
	
	@Override
	public String encode(byte[] bytes)
	{
		byte[] encodedBytes = Base64.encode(bytes, FLAGS);
		return new String(encodedBytes);
	}

	@Override
	public byte[] decode(byte[] bytes) throws Exception
	{
		return Base64.decode(bytes, FLAGS);
	}

	@Override
	public byte[] decode(String str) throws Exception
	{
		return Base64.decode(str, FLAGS);
	}
	
}
