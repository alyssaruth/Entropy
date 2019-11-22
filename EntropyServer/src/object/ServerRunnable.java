package object;


public interface ServerRunnable extends Runnable
{
	public abstract String getDetails();
	public abstract UserConnection getUserConnection();
}
