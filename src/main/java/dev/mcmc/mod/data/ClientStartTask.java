package dev.mcmc.mod.data;

import dev.mcmc.mod.Connection;
import dev.mcmc.mod.MCMCMod;
import dev.mcmc.mod.client.MCMCModClient;

import java.net.Proxy;

/**
 * @author LatvianModder
 */
public class ClientStartTask implements Runnable
{
	private final String id;
	private final Proxy proxy;

	public ClientStartTask(String i, Proxy p)
	{
		id = i;
		proxy = p;
	}

	@Override
	public void run()
	{
		try
		{
			Connection.Response r = Connection.createAPI("stats/client/start/" + id).proxy(proxy).connect();

			if (r.isOK())
			{
				MCMCModClient.currentSession = new ClientSession();
			}
		}
		catch (Exception ex)
		{
			MCMCMod.LOGGER.error("Failed to connect: " + ex);
		}
	}
}
