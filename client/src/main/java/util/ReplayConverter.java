package util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import screen.ProgressDialog;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReplayConverter implements Registry
{
	public static final int REPLAY_VERSION = 1;
	
	public static void startReplayConversionIfNecessary()
	{
		String replayDirectory = prefs.get(PREFERENCES_STRING_REPLAY_DIRECTORY, null);
		if (replayDirectory == null)
		{
			//Never ok'd the preferences dialog - basically a clean install. Nothing to do.
			Debug.append("Replay directory has never been set - will skip conversion and save replay version [" + REPLAY_VERSION + "]");
			instance.putInt(INSTANCE_INT_REPLAY_CONVERSION, REPLAY_VERSION);
			return;
		}
		
		int lastConvertedVersion = instance.getInt(INSTANCE_INT_REPLAY_CONVERSION, 0);
		if (lastConvertedVersion >= REPLAY_VERSION)
		{
			Debug.append("Replays are up to date (" + REPLAY_VERSION + ")");
			return;
		}
		
		DialogUtil.showInfo("Your replay files are in an out of date format - these will be converted now.");
		Debug.appendBanner("Starting Replay Conversion to Verison " + REPLAY_VERSION);
		
		boolean success = convertReplaysInSeparateThread(ReplayFileUtil.FOLDER_PERSONAL_REPLAYS);
		if (success)
		{
			success = convertReplaysInSeparateThread(ReplayFileUtil.FOLDER_IMPORTED_REPLAYS);
		}
		
		if (success)
		{
			Debug.appendBanner("Finished Replay Conversion");
			instance.putInt(INSTANCE_INT_REPLAY_CONVERSION, REPLAY_VERSION);
			DialogUtil.showInfo("Conversion finished successfully!");
		}
		else
		{
			DialogUtil.showError("Replay conversion failed - logs have been sent for investigation");
		}
	}
	
	private static boolean convertReplaysInSeparateThread(final String folder)
	{
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<?> result = service.submit(new Runnable()
		{
			@Override
			public void run()
			{
				convertReplays(folder);
			}
		});
		
		try
		{
			result.get();
		}
		catch (Throwable t)
		{
			Debug.appendBanner("Failing Conversion");
			Debug.stackTraceSilently(t);
			return false;
		}
		
		return true;
	}
	
	private static void convertReplays(final String folder)
	{
		//Get the replays
		String directoryStr = ReplayFileUtil.getDirectoryFromPreferences() + "//Replays//" + folder;
		final File[] replayFiles = new File(directoryStr).listFiles();
		if (replayFiles == null)
		{
			Debug.append("No " + folder + " replays to convert");
			return;
		}
		
		int size = replayFiles.length;
		
		String progressStr = folder + " replays to convert";
		ProgressDialog dialog = ProgressDialog.factory("ReplayConversion", progressStr, size);
		dialog.setVisibleLater();
		
		//Track what happens to each file
		ArrayList<File> filesConverted = new ArrayList<>();
		ArrayList<File> filesSkipped = new ArrayList<>();
		ArrayList<File> filesFailed = new ArrayList<>();
		
		//Loop through and convert each file, adding it to the appropriate list
		Debug.append("Converting " + replayFiles.length + " " + folder + " replays...");
		for (int i=0; i<replayFiles.length; i++)
		{
			convertReplay(replayFiles[i], filesConverted, filesSkipped, filesFailed);
			dialog.incrementProgressLater();
		}
		
		//Log out the results
		Debug.append("Finished conversion, results:");
		Debug.appendWithoutDate("Successful: " + filesConverted.size());
		Debug.appendWithoutDate("Skipped: " + filesSkipped.size());
		Debug.appendWithoutDate("Failed: " + filesFailed.size());
		
		//If we have failed replays, show an error and email examples
		int failedSize = filesFailed.size();
		if (failedSize > 0)
		{
			// TODO: Dump failed replay files somehow. Upload to S3?
			DialogUtil.invokeInfoLaterAndWait(failedSize + " replays failed to be converted.");
		}
		
		dialog.disposeLater();
	}
	
	private static void convertReplay(File replayFile, ArrayList<File> filesConverted, ArrayList<File> filesSkipped,
	  ArrayList<File> filesFailed)
	{
		String filePath = replayFile.getAbsolutePath();
		int version = getReplayVersion(filePath);
		if (version == -1)
		{
			filesFailed.add(replayFile);
			return;
		}
		
		if (version == REPLAY_VERSION)
		{
			filesSkipped.add(replayFile);
			return;
		}
		
		//Iteratively convert the file from 0 -> 1 -> 2...
		boolean success = true;
		while (version < REPLAY_VERSION
		  && success)
		{
			success = convertReplay(filePath, version);
			version = getReplayVersion(filePath);
		}
		
		if (success)
		{
			filesConverted.add(replayFile);
		}
		else
		{
			filesFailed.add(replayFile);
		}
	}
	
	public static int getReplayVersion(String filePath)
	{
		Document doc = ReplayFileUtil.getXmlDocumentFromFile(filePath);
		if (doc == null)
		{
			//This replay is corrupt, don't attempt to convert it
			return -1;
		}
		
		Element rootElement = doc.getDocumentElement();
		return XmlUtil.getAttributeInt(rootElement, ReplayFileUtil.XML_REPLAY_INT_VERSION, 0);
	}
	
	public static boolean convertReplay(String filePath, int version)
	{
		Document xml = ReplayFileUtil.getXmlDocumentFromFile(filePath);
		Element root = xml.getDocumentElement();
		
		boolean success = false;
		if (version == 0)
		{
			success = ReplayConverterVersion0.convertReplay(root);
		}
		else
		{
			Debug.stackTrace("Trying to convert from unexpected version: " + version);
		}
		
		//Increment the version and save the replay
		if (success)
		{
			root.setAttribute(ReplayFileUtil.XML_REPLAY_INT_VERSION, "" + (version+1));
			String xmlStr = XmlUtil.getStringFromDocument(xml);
			FileUtil.encodeAndSaveToFile(Paths.get(filePath), xmlStr);
		}
		
		return success;
	}
	
	
	
	/**
	 * Helpers
	 */
	public static boolean replayIsOldVersion(String filePath)
	{
		int version = getReplayVersion(filePath);
		return version < REPLAY_VERSION;
	}
}
