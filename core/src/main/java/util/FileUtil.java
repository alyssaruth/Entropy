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
	public static String getMd5Crc(String filePath)
	{
		String crc = null;
		
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(Files.readAllBytes(Paths.get(filePath)));
			byte[] digest = md.digest();
			
			crc = EncryptionUtil.base64Interface.encode(digest);
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t + " trying to get CRC of file");
		}
		
		return crc;
	}
	
	public static long getFileSize(String filePath)
	{
		long fileSize = -1;
		File f = new File(filePath);
		
		try (FileInputStream fis = new FileInputStream(f))
		{
			fileSize = fis.getChannel().size();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t, "Couldn't obtain file size for path " + filePath);
		}
		
		return fileSize;
	}
	
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
	
	public static String stripFileExtension(String filename)
	{
		int ix = filename.indexOf('.');
		return filename.substring(0, ix);
	}
}
