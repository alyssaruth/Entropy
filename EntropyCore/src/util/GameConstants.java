package util;

public final class GameConstants 
{
	public static final int GAME_MODE_ENTROPY = 1;
	public static final int GAME_MODE_VECTROPY = 2;
	public static final int GAME_MODE_ENTROPY_ONLINE = 3;
	public static final int GAME_MODE_VECTROPY_ONLINE = 4;
	
	public static String getGameModeDesc(int mode)
	{
		switch (mode)
		{
			case GAME_MODE_ENTROPY:
			case GAME_MODE_ENTROPY_ONLINE:
				return "Entropy";
			case GAME_MODE_VECTROPY:
			case GAME_MODE_VECTROPY_ONLINE:
				return "Vectropy";
			default:
				Debug.stackTrace("Unexpected game mode: " + mode);
				return null;
		}
	}
}
