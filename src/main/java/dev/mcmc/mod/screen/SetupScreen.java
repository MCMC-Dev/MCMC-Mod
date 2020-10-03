package dev.mcmc.mod.screen;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.mcmc.mod.Connection;
import dev.mcmc.mod.MCMCMod;
import dev.mcmc.mod.data.ClientLaunchTask;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class SetupScreen extends Screen
{
	public final Screen parentScreen;
	private List<String> textList;
	public Button buttonNo, buttonYes;
	public int totalHeight;
	public int moreInfoWidth;
	public int moreInfoY;
	public String moreInfo;

	public SetupScreen(Screen p)
	{
		super(new StringTextComponent("MCMC"));
		parentScreen = p;
	}

	@Override
	protected void init()
	{
		textList = new ArrayList<>();

		textList.add(TextFormatting.AQUA + "== MCMC Mod ==" + TextFormatting.RESET);
		textList.add("");
		textList.add(TextFormatting.YELLOW + I18n.format("mcmc.setup.question") + TextFormatting.RESET);
		textList.add("");
		textList.add(I18n.format("mcmc.setup.info.1"));
		textList.add("");
		textList.add(I18n.format("mcmc.setup.info.2"));
		moreInfo = "[ " + I18n.format("mcmc.setup.info.more") + " ]";

		//textList = font.listFormattedStringToWidth(String.join("\n", textList), width / 5 * 3);

		totalHeight = textList.size() * 10 + 50;
		moreInfoWidth = font.getStringWidth(moreInfo);
		moreInfoY = (height - totalHeight) / 2 + totalHeight - 40;

		int by = (height - totalHeight) / 2 + totalHeight - 20;

		addButton(buttonYes = new Button(width / 2 - 110, by, 100, 20, new TranslationTextComponent("gui.yes"), this::yesButton));
		addButton(buttonNo = new Button(width / 2 + 10, by, 100, 20, new TranslationTextComponent("gui.no"), this::noButton));
	}

	private void yesButton(Button button)
	{
		buttonYes.active = false;
		buttonNo.active = false;

		MCMCMod.queue(() -> {
			try
			{
				JsonObject r = Connection.createAPI("account/anonymous").proxy(minecraft.getProxy()).getJson(JsonObject.class);

				if (r != null && r.has("token"))
				{
					MCMCMod.setToken(r.get("token").getAsString());

					minecraft.execute(() -> {
						minecraft.displayGuiScreen(parentScreen);
						minecraft.getToastGui().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslationTextComponent("mcmc.connection_success"), null));
						MCMCMod.queue(new ClientLaunchTask(minecraft));
					});

					return;
				}
			}
			catch (Exception ex)
			{
			}

			minecraft.execute(() -> {
				buttonYes.active = true;
				buttonNo.active = true;
				minecraft.getToastGui().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslationTextComponent("mcmc.connection_fail"), null));
			});
		});
	}

	private void noButton(Button button)
	{
		MCMCMod.setToken("-");
		minecraft.displayGuiScreen(parentScreen);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick)
	{
		renderBackground(matrixStack);

		for (int i = 0; i < textList.size(); i++)
		{
			drawCenteredString(matrixStack, font, textList.get(i), width / 2, (height - totalHeight) / 2 + i * 10, 0xFFFFFF);
		}

		if (mouseY >= moreInfoY - 2 && mouseY < moreInfoY + 12 && mouseX > (width - moreInfoWidth) / 2D && mouseX < (width - moreInfoWidth) / 2D + moreInfoWidth)
		{
			drawCenteredString(matrixStack, font, TextFormatting.UNDERLINE + moreInfo, width / 2, moreInfoY, 0xFFFFFF);
		}
		else
		{
			drawCenteredString(matrixStack, font, moreInfo, width / 2, moreInfoY, 0xFFFFFF);
		}

		super.render(matrixStack, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button)
	{
		if (y >= moreInfoY - 2 && y < moreInfoY + 12 && x > (width - moreInfoWidth) / 2D && x < (width - moreInfoWidth) / 2D + moreInfoWidth)
		{
			try
			{
				Util.getOSType().openURI(new URI("https://mcmc.dev/about"));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return super.mouseClicked(x, y, button);
	}
}