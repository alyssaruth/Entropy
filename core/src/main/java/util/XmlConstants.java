package util;

/**
 * All message names live here, across applications
 */
public interface XmlConstants 
{
	//Client
	public static final String ROOT_TAG_HEARTBEAT					= "Heartbeat";
	public static final String ROOT_TAG_DISCONNECT_REQUEST			= "DisconnectRequest";
	public static final String ROOT_TAG_NEW_CHAT 					= "NewChat";
	public static final String ROOT_TAG_ROOM_JOIN_REQUEST 			= "RoomJoinRequest";
	public static final String ROOT_TAG_CLOSE_ROOM_REQUEST 			= "CloseRoomRequest";
	public static final String ROOT_TAG_OBSERVER_REQUEST 			= "ObserverRequest";
	public static final String ROOT_TAG_NEW_GAME_REQUEST 			= "NewGameRequest";
	public static final String ROOT_TAG_BID 						= "Bid";
	public static final String ROOT_TAG_LEADERBOARD_REQUEST 		= "LeaderboardRequest";
	
	//Notification Sockets
	public static final String SOCKET_NAME_SUFFIX					= "Socket";
	public static final String SOCKET_NAME_GAME						= "Game" + SOCKET_NAME_SUFFIX;
	public static final String SOCKET_NAME_CHAT						= "Chat" + SOCKET_NAME_SUFFIX;
	public static final String SOCKET_NAME_LOBBY					= "Lobby" + SOCKET_NAME_SUFFIX;
	
	//Server notifications
	public static final String RESPONSE_TAG_CHAT_NOTIFICATION = "ChatNotification";
	public static final String RESPONSE_TAG_PLAYER_NOTIFICATION = "PlayerNotification";
	public static final String RESPONSE_TAG_BID_NOTIFICATION = "BidNotification";
	public static final String RESPONSE_TAG_NEW_ROUND_NOTIFICATION = "NewRoundNotification";
	public static final String RESPONSE_TAG_GAME_OVER_NOTIFICATION = "GameOverNotification";
	public static final String RESPONSE_TAG_STATISTICS_NOTIFICATION = "StatisticsNotification";
	
	//Server responses
	public static final String RESPONSE_TAG_KICK_OFF = "KickOff";
	public static final String RESPONSE_TAG_LEADERBOARD = "LeaderboardResponse";
	public static final String RESPONSE_TAG_JOIN_ROOM_RESPONSE = "JoinRoomAck";
	public static final String RESPONSE_TAG_CLOSE_ROOM_RESPONSE = "CloseRoomAck";
	public static final String RESPONSE_TAG_OBSERVER_RESPONSE = "ObserverResponse";
	public static final String RESPONSE_TAG_NEW_GAME = "NewGameResponse";
	
	public static final String RESPONSE_TAG_ACKNOWLEDGEMENT = "Acknowledgement";
	public static final String RESPONSE_TAG_STACK_TRACE = "StackTrace";
	public static final String RESPONSE_TAG_SOCKET_TIME_OUT = "SocketTimeOut";
	
	public static final String REMOVAL_REASON_FAILED_USERNAME_CHECK = "There has been an authentication error.";
}
