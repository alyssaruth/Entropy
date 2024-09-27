package util;

import java.util.Base64;

public class Base64Desktop
{
	public String encode(byte[] bytes)
	{
		return Base64.getEncoder().encodeToString(bytes);
	}

	public byte[] decode(byte[] bytes) throws Exception
	{
		return Base64.getDecoder().decode(bytes);
	}

	public byte[] decode(String str) throws Exception
	{
		return Base64.getDecoder().decode(str);
	}
}
