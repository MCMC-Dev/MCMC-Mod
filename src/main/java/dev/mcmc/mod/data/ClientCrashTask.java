package dev.mcmc.mod.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.mcmc.mod.Connection;
import dev.mcmc.mod.MCMCMod;
import net.minecraft.util.Util;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class ClientCrashTask implements Runnable
{
	public static final Pattern STACK_LINE = Pattern.compile("^(.*\\(.*\\)).*$");

	private final String id;
	private final Proxy proxy;

	public ClientCrashTask(String i, Proxy p)
	{
		id = i;
		proxy = p;
	}

	@Override
	public void run()
	{
		try
		{
			JsonObject json = new JsonObject();

			List<String> log = Files.readAllLines(FMLPaths.GAMEDIR.get().resolve("logs/latest.log"), StandardCharsets.UTF_8);

			if (log.isEmpty())
			{
				return;
			}

			JsonArray stack = new JsonArray();

			for (int i = log.size() - 1; i >= 0; i--)
			{
				if (log.get(i).startsWith("Description: "))
				{
					String type = log.get(i).substring(13).trim();
					String error = log.get(i + 2).trim();

					if (type.equals("ThisIsFake") || type.equals("Manually triggered debug crash") || error.equals("java.lang.Exception: dummy"))
					{
						return;
					}

					json.addProperty("type", type);
					json.addProperty("error", error);

					String line;

					while ((line = log.get(i + 3).trim()).startsWith("at "))
					{
						Matcher matcher = STACK_LINE.matcher(line.substring(3).trim());

						if (matcher.find())
						{
							stack.add(matcher.group(1));
						}

						i++;
					}

					break;
				}
			}

			if (stack.size() == 0 || !json.has("type"))
			{
				return;
			}

			json.add("stack", stack);

			JsonObject mods = new JsonObject();

			for (ModInfo info : ModList.get().getMods())
			{
				mods.addProperty(info.getModId(), info.getVersion().toString());
			}

			json.add("mods", mods);

			JsonArray coremods = new JsonArray();

			for (Map<String, String> coremod : FMLLoader.modLauncherModList())
			{
				JsonObject o = new JsonObject();
				o.addProperty("file", coremod.getOrDefault("file", "nofile"));
				o.addProperty("name", coremod.getOrDefault("name", "missing"));
				o.addProperty("type", coremod.getOrDefault("type", "NOTYPE"));
				coremods.add(o);
			}

			json.add("coremods", coremods);

			JsonObject r = Connection.createAPI("stats/client/crash/" + id).proxy(proxy).data(json).getJson(JsonObject.class);

			if (r != null && r.has("id"))
			{
				try
				{
					Util.getOSType().openURI(new URI("https://mcmc.dev/crashes/client/" + r.get("id").getAsString()));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		catch (Exception ex)
		{
			MCMCMod.LOGGER.error("Failed to connect: " + ex);
		}
	}
}
