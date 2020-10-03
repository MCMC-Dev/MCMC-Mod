package dev.mcmc.mod.client;

import dev.mcmc.mod.MCMCMod;
import dev.mcmc.mod.MCMCModCommon;
import dev.mcmc.mod.data.ClientLaunchTask;
import dev.mcmc.mod.data.ClientSession;
import dev.mcmc.mod.data.ClientStartTask;
import dev.mcmc.mod.data.ClientStopTask;
import dev.mcmc.mod.screen.MCMCButton;
import dev.mcmc.mod.screen.SetupScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

/**
 * @author LatvianModder
 */
public class MCMCModClient extends MCMCModCommon
{
	public static String launchId = "";
	public static ClientSession currentSession = null;
	private static Style setupModpackStyle;

	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(MCMCModClient.class);
		setupModpackStyle = Style.EMPTY
				.setUnderlined(true)
				.setColor(Color.fromTextFormatting(TextFormatting.BLUE))
				.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcmc.dev/modpack-setup"));
	}

	@Override
	public void launch()
	{
		String token = MCMCMod.getToken();

		if (token.isEmpty() || token.equals("-"))
		{
			return;
		}

		Minecraft.getInstance().execute(() -> MCMCMod.queue(new ClientLaunchTask(Minecraft.getInstance())));
	}

	@SubscribeEvent
	public static void screenOpened(GuiOpenEvent event)
	{
		if ((event.getGui() instanceof MultiplayerScreen || event.getGui() instanceof WorldSelectionScreen) && MCMCMod.getToken().isEmpty())
		{
			event.setGui(new SetupScreen(event.getGui()));
		}
	}

	@SubscribeEvent
	public static void screenInit(GuiScreenEvent.InitGuiEvent.Post event)
	{
		if (event.getGui() instanceof OptionsScreen && (event.getGui().getMinecraft().world == null || GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS))
		{
			event.addWidget(new MCMCButton(4, event.getGui().height / 2 - 8, 16, 16));
		}
	}

	@SubscribeEvent
	public static void loggedIn(ClientPlayerNetworkEvent.LoggedInEvent event)
	{
		if (MCMCMod.modpackProperties.version.startsWith("unknown+"))
		{
			Minecraft.getInstance().ingameGUI.func_238450_a_(ChatType.CHAT, new TranslationTextComponent("mcmc.nopack", new StringTextComponent("mcmc.dev/modpack-setup").setStyle(setupModpackStyle)), Util.DUMMY_UUID);
		}

		MCMCMod.hasCrashed = false;
		currentSession = null;

		String token = MCMCMod.getToken();

		if (!token.isEmpty() && !token.equals("-"))
		{
			if (!launchId.isEmpty())
			{
				MCMCMod.queue(new ClientStartTask(launchId, Minecraft.getInstance().getProxy()));
			}
		}
	}

	@SubscribeEvent
	public static void loggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event)
	{
		if (currentSession != null && event.getPlayer() != null)
		{
			MCMCMod.queue(new ClientStopTask(launchId, Minecraft.getInstance().getProxy(), currentSession.getData()));
			currentSession = null;
		}
	}

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getInstance();

		if (currentSession != null && event.phase == TickEvent.Phase.START && mc.player != null)
		{
			currentSession.ticks++;

			NetworkPlayerInfo info = mc.player.connection.getPlayerInfo(mc.player.getUniqueID());

			if (info != null && (info.getGameType() == GameType.CREATIVE || info.getGameType() == GameType.SPECTATOR))
			{
				currentSession.creativeTicks++;
			}

			if (mc.getIntegratedServer() != null)
			{
				currentSession.multiplayer = false;
				currentSession.lan = mc.getIntegratedServer().getPublic();
				currentSession.hardcore = mc.getIntegratedServer().isHardcore();
			}
			else if (mc.getCurrentServerData() != null)
			{
				currentSession.multiplayer = true;
				currentSession.lan = mc.getCurrentServerData().isOnLAN();
				currentSession.hardcore = mc.world.getWorldInfo().isHardcore();
			}
		}
	}

	@SubscribeEvent
	public static void renderTick(TickEvent.RenderTickEvent event)
	{
		if (currentSession != null && event.phase == TickEvent.Phase.END)
		{
			currentSession.frames++;
		}
	}
}