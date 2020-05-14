package dev.mcmc.mod.data;

import dev.mcmc.mod.Connection;
import dev.mcmc.mod.MCMCMod;
import dev.mcmc.mod.ThreadMCMCCrashHook;
import dev.mcmc.mod.client.MCMCModClient;
import net.minecraft.client.Minecraft;

/**
 * @author LatvianModder
 */
public class ClientOptOutTask implements Runnable
{
	@Override
	public void run()
	{
		try
		{
			Connection.createAPI("stats/client/opt-out").proxy(Minecraft.getInstance().getProxy()).connect();
			MCMCMod.setToken("-");
			MCMCModClient.launchId = "";
			ThreadMCMCCrashHook.client = null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}