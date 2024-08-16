package util;

public interface OnlineConstants 
{
	//Used to make sure client/server are in sync
	public static final String SERVER_VERSION = "19";
	
	//Used in the AboutDialog and for sending logs
	public static final String ENTROPY_VERSION_NUMBER = "v7.0.0";

	public static final String ENTROPY_REPOSITORY_URL = "https://api.github.com/repos/alyssaruth/Entropy";
	public static final String ENTROPY_MANUAL_DOWNLOAD_URL = "https://github.com/alyssaruth/Entropy/releases";
	
	//Filenames - for automatic updates
	public static final String FILE_NAME_ENTROPY_JAR = "EntropyLive.jar";
	
	//Port numbers
	//Live
	public static final int SERVER_PORT_NUMBER_LOWER_BOUND = 1142;
	public static final int SERVER_PORT_NUMBER_UPPER_BOUND = 1152;
	public static final int SERVER_PORT_NUMBER_DOWNLOAD = 1153; //Also in EntropyUpdater
	
	public static final String LOBBY_ID = "Lobby";
}
