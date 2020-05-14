package dev.mcmc.mod;

/**
 * @author LatvianModder
 */
public class ThreadMCMCCrashHook extends Thread
{
	public static Runnable client = null;

	@Override
	public void run()
	{
		if (MCMCMod.hasCrashed)
		{
			if (client != null)
			{
				client.run();
			}
		}
	}
}