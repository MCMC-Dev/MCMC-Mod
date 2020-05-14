package dev.mcmc.mod.screen;

import dev.mcmc.mod.MCMCMod;
import dev.mcmc.mod.data.ClientOptOutTask;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ConfigScreen extends Screen
{
	public final Screen parentScreen;
	private List<String> textList;
	public Button buttonNo, buttonYes;
	public int totalHeight;
	public int moreInfoWidth;
	public int moreInfoY;
	public String moreInfo;

	public ConfigScreen(Screen p)
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
		textList.add(I18n.format("mcmc.setup.info.1"));
		textList.add("");
		textList.add(I18n.format("mcmc.setup.info.2"));
		moreInfo = "[ " + I18n.format("mcmc.setup.info.more") + " ]";

		textList = font.listFormattedStringToWidth(String.join("\n", textList), width / 5 * 3);

		totalHeight = textList.size() * 10 + 50;
		moreInfoWidth = font.getStringWidth(moreInfo);
		moreInfoY = (height - totalHeight) / 2 + totalHeight - 40;

		int by = (height - totalHeight) / 2 + totalHeight - 20;

		addButton(buttonYes = new Button(width / 2 - 110, by, 100, 20, I18n.format("mcmc.opt_out"), this::optOutButton));
		addButton(buttonNo = new Button(width / 2 + 10, by, 100, 20, I18n.format("gui.back"), this::backButton));
	}

	private void optOutButton(Button button)
	{
		MCMCMod.queue(new ClientOptOutTask());
		minecraft.displayGuiScreen(parentScreen);
	}

	private void backButton(Button button)
	{
		minecraft.displayGuiScreen(parentScreen);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick)
	{
		renderBackground();

		for (int i = 0; i < textList.size(); i++)
		{
			drawCenteredString(font, textList.get(i), width / 2, (height - totalHeight) / 2 + i * 10, 0xFFFFFF);
		}

		if (mouseY >= moreInfoY - 2 && mouseY < moreInfoY + 12 && mouseX > (width - moreInfoWidth) / 2D && mouseX < (width - moreInfoWidth) / 2D + moreInfoWidth)
		{
			drawCenteredString(font, TextFormatting.UNDERLINE + moreInfo, width / 2, moreInfoY, 0xFFFFFF);
		}
		else
		{
			drawCenteredString(font, moreInfo, width / 2, moreInfoY, 0xFFFFFF);
		}

		super.render(mouseX, mouseY, partialTick);
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