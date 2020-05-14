package dev.mcmc.mod.data;

import dev.mcmc.mod.Connection;
import dev.mcmc.mod.MCMCMod;

import java.net.Proxy;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ClientStopTask implements Runnable
{
	private final String id;
	private final Proxy proxy;
	private final Map<String, Object> data;

	public ClientStopTask(String i, Proxy p, Map<String, Object> d)
	{
		id = i;
		proxy = p;
		data = d;
	}

	@Override
	public void run()
	{
		try
		{
			Connection.createAPI("stats/client/stop/" + id).proxy(proxy).data(data).connect();
		}
		catch (Exception ex)
		{
			MCMCMod.LOGGER.error("Failed to connect: " + ex);
		}
	}
}