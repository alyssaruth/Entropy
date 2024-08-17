package util;

import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFileChooser;

public class FileUtil
{
	public static void saveTextToFile(String text, Path destinationPath)
	{
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedWriter writer = Files.newBufferedWriter(destinationPath, charset)) 
		{
			String[] values = text.split("\n");
		    for (String word : values) 
		    {
		        writer.write(word);
		        writer.newLine();
		    }
		}
		catch (IOException x) 
		{
			Debug.stackTrace(x);
		}
	}
	
	public static String getBase64DecodedFileContentsAsString(File file)
	{
		try
		{
			Path filePath = file.toPath();
			byte[] bytes = Files.readAllBytes(filePath);
			byte[] decodedBytes = EncryptionUtil.base64Interface.decode(bytes);
			
			return new String(decodedBytes, "UTF-8");
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t, "Failed to decode contents of file " + file);
			return null;
		}
	}

	public static void encodeAndSaveToFile(Path destinationPath, String stringToWrite)
	{
		String encodedStringToWrite = EncryptionUtil.base64Interface.encode(stringToWrite.getBytes());
		saveTextToFile(encodedStringToWrite, destinationPath);
	}
}
