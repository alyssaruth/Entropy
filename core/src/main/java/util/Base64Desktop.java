package util;

import java.util.Base64;

public class Base64Desktop implements Base64Interface
{
	@Override
	public String encode(byte[] bytes)
	{
		return Base64.getEncoder().encodeToString(bytes);
	}

	@Override
	public byte[] decode(byte[] bytes) throws Exception
	{
		return Base64.getDecoder().decode(bytes);
	}
	
	@Override
	public byte[] decode(String str) throws Exception
	{
		return Base64.getDecoder().decode(str);
	}
}
