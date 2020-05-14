package dev.mcmc.mod;

import dev.mcmc.mod.client.MCMCModClient;
import dev.mcmc.mod.data.ModpackProperties;
import net.minecraftforge.fml.CrashReportExtender;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

@Mod(MCMCMod.MOD_ID)
public class MCMCMod
{
	public static final int API_VERSION = 1;
	public static final String MOD_ID = "mcmc";
	public static final Logger LOGGER = LogManager.getLogger("MCMC");

	public static MCMCModCommon proxy;
	private static Preferences userPreferences;
	public static ModpackProperties modpackProperties;
	public static boolean hasCrashed;
	private static ExecutorService executorService;

	private static final long startTime = System.currentTimeMillis();
	public static long loadTime = 0L;

	public MCMCMod()
	{
		userPreferences = Preferences.userRoot().node("dev/mcmc/mod");
		hasCrashed = false;
		executorService = Executors.newSingleThreadExecutor();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		//noinspection Convert2MethodRef
		proxy = DistExecutor.runForDist(() -> () -> new MCMCModClient(), () -> () -> new MCMCModCommon());
		Runtime.getRuntime().addShutdownHook(new ThreadMCMCCrashHook());
		CrashReportExtender.registerCrashCallable("MCMC Modpack Version", MCMCMod::getModpackVersionForCrash);
		proxy.init();
	}

	private void setup(FMLCommonSetupEvent event)
	{
		modpackProperties = new ModpackProperties();

		Path path = FMLPaths.CONFIGDIR.get().resolve("mcmc-modpack-properties.json");

		if (Files.exists(path))
		{
			try (BufferedReader reader = Files.newBufferedReader(path))
			{
				modpackProperties = Utils.GSON.fromJson(reader, ModpackProperties.class);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private void loadComplete(FMLLoadCompleteEvent event)
	{
		loadTime = System.currentTimeMillis() - startTime;
		proxy.launch();
	}

	public static String getToken()
	{
		return userPreferences.get("token", "");
	}

	public static void setToken(String token)
	{
		userPreferences.put("token", token);

		try
		{
			userPreferences.sync();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		LOGGER.info("Token updated!");
	}

	public static boolean getReportCrashes()
	{
		return userPreferences.getBoolean("report_crashes", true);
	}

	public static void setReportCrashes(boolean val)
	{
		userPreferences.putBoolean("report_crashes", val);
	}

	private static String getModpackVersionForCrash()
	{
		hasCrashed = true;
		return modpackProperties.version;
	}

	public static void queue(Runnable task)
	{
		executorService.submit(task);
	}
}
