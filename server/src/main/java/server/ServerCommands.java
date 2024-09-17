package server;

public interface ServerCommands
{
	public static final String COMMAND_DUMP_THREADS = "threads";
	public static final String COMMAND_DUMP_THREAD_STACKS = "stacks";
	public static final String COMMAND_DUMP_USERS = "dump users";
	public static final String COMMAND_POOL_STATS = "pool stats";
	public static final String COMMAND_SET_CORE_POOL_SIZE = "set core size ";
	public static final String COMMAND_SET_MAX_POOL_SIZE = "set max size ";
	public static final String COMMAND_SET_KEEP_ALIVE_TIME = "set alive time ";
	public static final String COMMAND_MEMORY = "memory";
	public static final String COMMAND_NOTIFICATION_LOGGING = "notification logging";
	public static final String COMMAND_NOTIFY_USER = "notify ";
}
