package dev.mcmc.mod.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.mcmc.mod.MCMCMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.glfw.GLFW;

/**
 * @author LatvianModder
 */
public class MCMCButton extends AbstractButton
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(MCMCMod.MOD_ID, "textures/mcmc_button.png");

	public MCMCButton(int x, int y, int w, int h)
	{
		super(x, y, w, h, "MCMC");
	}

	@Override
	public void onPress()
	{
		if (Screen.hasControlDown() && GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS)
		{
			throw new RuntimeException("dummy thicc crash");
		}
		else if (Screen.hasControlDown() && GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS)
		{
			MCMCMod.setToken("");
			Minecraft.getInstance().shutdown();
		}
		else if (Minecraft.getInstance().world != null)
		{
			return;
		}

		if (MCMCMod.getToken().isEmpty() || MCMCMod.getToken().equals("-"))
		{
			Minecraft.getInstance().displayGuiScreen(new SetupScreen(Minecraft.getInstance().currentScreen));
		}
		else
		{
			Minecraft.getInstance().displayGuiScreen(new ConfigScreen(Minecraft.getInstance().currentScreen));
		}
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks)
	{
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1F, 1F, 1F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		if (isHovered())
		{
			blit(x, y, 16, 0, 16, 16, 32, 16);
		}
		else
		{
			blit(x, y, 0, 0, 16, 16, 32, 16);
		}
	}

	@Override
	public void renderToolTip(int x, int y)
	{
		Minecraft.getInstance().currentScreen.renderTooltip("MCMC Mod", x, y);
	}
}
