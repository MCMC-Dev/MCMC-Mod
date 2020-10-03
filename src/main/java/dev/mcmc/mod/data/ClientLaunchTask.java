package dev.mcmc.mod.data;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import dev.mcmc.mod.Connection;
import dev.mcmc.mod.MCMCMod;
import dev.mcmc.mod.ThreadMCMCCrashHook;
import dev.mcmc.mod.client.ARBCaps;
import dev.mcmc.mod.client.EXTCaps;
import dev.mcmc.mod.client.MCMCModClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import java.net.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ClientLaunchTask implements Runnable
{
	private final Proxy proxy;
	private final Map<String, Object> data;

	public ClientLaunchTask(Minecraft mc)
	{
		proxy = mc.getProxy();
		data = new LinkedHashMap<>();

		if (!System.getProperty("java.version").equals("1.8.0_51"))
		{
			data.put("java", System.getProperty("java.version"));
		}

		if (!System.getProperty("java.vendor").equals("Oracle Corporation"))
		{
			data.put("java_vendor", System.getProperty("java.vendor"));
		}

		data.put("load_time", MCMCMod.loadTime);

		switch (Util.getOSType())
		{
			case WINDOWS:
				break;
			case LINUX:
				data.put("os", 1);
				break;
			case OSX:
				data.put("os", 2);
				break;
			default:
				data.put("os", 3);
				break;
		}

		data.put("memory_total", Runtime.getRuntime().totalMemory());
		data.put("memory_max", Runtime.getRuntime().maxMemory());
		data.put("cpu_cores", Runtime.getRuntime().availableProcessors());

		if (!System.getProperty("os.arch").equals("amd64"))
		{
			data.put("arch", System.getProperty("os.arch"));
		}

		Session.Type sessionType = ObfuscationReflectionHelper.getPrivateValue(Session.class, mc.getSession(), "field_152429_d");

		if (sessionType == null || sessionType == Session.Type.LEGACY)
		{
			data.put("legacy", 1);
		}

		if (mc.getMainWindow().getRefreshRate() != 60)
		{
			data.put("refresh_rate", mc.getMainWindow().getRefreshRate());
		}

		if (mc.gameSettings.vsync)
		{
			data.put("vsync", 1);
		}

		if (mc.getMainWindow().isFullscreen())
		{
			data.put("fullscreen", 1);
		}

		if (mc.gameSettings.language != null && !mc.gameSettings.language.equals("en_us"))
		{
			data.put("language", mc.gameSettings.language);
		}

		int resourcePacks = 0;

		for (ResourcePackInfo pack : mc.getResourcePackList().getEnabledPacks())
		{
			if (!pack.isAlwaysEnabled() && !pack.isOrderLocked())
			{
				resourcePacks++;
			}
		}

		if (resourcePacks > 0)
		{
			data.put("resource_packs", resourcePacks);
		}

		if (mc.gameSettings.guiScale != 0)
		{
			data.put("gui", Integer.toString(mc.gameSettings.guiScale));
		}

		GLCapabilities caps = GL.getCapabilities();

		data.put("gl_arb_caps", ARBCaps.get(caps));
		data.put("gl_ext_caps", EXTCaps.get(caps));

		if (!GlStateManager.getString(GL11.GL_VENDOR).equals("NVIDIA Corporation"))
		{
			data.put("gl_vendor", GlStateManager.getString(GL11.GL_VENDOR));
		}

		data.put("gl_renderer", GlStateManager.getString(GL11.GL_RENDERER));
		data.put("gl_version", GlStateManager.getString(GL11.GL_VERSION));

		for (int i = 0x4000; i > 0; i >>= 1)
		{
			GlStateManager.texImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null);
			if (GlStateManager.getTexLevelParameter(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH) != 0)
			{
				if (i != 16384)
				{
					data.put("gl_max_texture_size", i);
				}

				break;
			}
		}

		// For server: GraphicsEnvironment.isHeadless()
	}

	@Override
	public void run()
	{
		try
		{
			MCMCMod.LOGGER.debug("Sending analytics data: " + data);

			JsonObject r = Connection.createAPI("stats/client/launch/" + MCMCMod.modpackProperties.version).data(data).proxy(proxy).getJson(JsonObject.class);

			if (r != null && r.has("id"))
			{
				MCMCModClient.launchId = r.get("id").getAsString();
				ThreadMCMCCrashHook.client = new ClientCrashTask(MCMCModClient.launchId, proxy);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}