package server;

public interface ServerCommands
{
	public static final String COMMAND_DUMP_THREADS = "threads";
	public static final String COMMAND_DUMP_THREAD_STACKS = "stacks";
	public static final String COMMAND_DUMP_USERS = "dump users";
	public static final String COMMAND_TRACE_USER = "trace user ";
	public static final String COMMAND_TRACE_MESSAGE_AND_RESPONSE = "trace message ";
	public static final String COMMAND_TRACE_ALL = "trace all";
	public static final String COMMAND_TRACE_USEFUL = "trace useful";
	public static final String COMMAND_SHUT_DOWN = "shut down";
	public static final String COMMAND_DUMP_VARIABLES = "dump var ";
	public static final String COMMAND_RESET = "reset room ";
	public static final String COMMAND_CLEAR_ROOMS = "clear rooms";
	public static final String COMMAND_DUMP_STATS = "stats";
	public static final String COMMAND_MESSAGE_STATS = "message stats";
	public static final String COMMAND_DECRYPTION_LOGGING = "decryption logging";
	public static final String COMMAND_CLEAR_STATS = "clear stats";
	public static final String COMMAND_LAUNCH_DAY = "launch day";
	public static final String COMMAND_POOL_STATS = "pool stats";
	public static final String COMMAND_SET_CORE_POOL_SIZE = "set core size ";
	public static final String COMMAND_SET_MAX_POOL_SIZE = "set max size ";
	public static final String COMMAND_SET_KEEP_ALIVE_TIME = "set alive time ";
	public static final String COMMAND_FAKE_USERS = "fake users ";
	public static final String COMMAND_DUMP_BLACKLIST = "dump blacklist";
	public static final String COMMAND_BLACKLIST_TIME = "set blacklist time ";
	public static final String COMMAND_BLACKLIST_FULL = "set blacklist full";
	public static final String COMMAND_BLACKLIST_OFF = "set blacklist off";
	public static final String COMMAND_BLACKLIST = "blacklist ";
	public static final String COMMAND_SET_BLACKLIST_THRESHOLD = "set blacklist threshold ";
	public static final String COMMAND_USED_KEYS = "used keys";
	public static final String COMMAND_DUMP_HASH_MAPS = "dump hm";
	public static final String COMMAND_MEMORY = "memory";
	public static final String COMMAND_NOTIFICATION_LOGGING = "notification logging";
	public static final String COMMAND_NOTIFY_USER = "notify ";
	public static final String COMMAND_SERVER_VERSION = "version";
	public static final String COMMAND_SERVER_RESET_CLIENT_VERSION = "reset version";
}
