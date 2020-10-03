package dev.mcmc.mod.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.mcmc.mod.MCMCMod;
import dev.mcmc.mod.data.ClientOptOutTask;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ConfigScreen extends Screen
{
	public final Screen parentScreen;
	private List<IReorderingProcessor> textList;
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
		List<ITextComponent> textList1 = new ArrayList<>();

		textList1.add(new StringTextComponent("== MCMC Mod ==").mergeStyle(TextFormatting.AQUA));
		textList1.add(StringTextComponent.EMPTY);
		textList1.add(new TranslationTextComponent("mcmc.setup.info.1"));
		textList1.add(StringTextComponent.EMPTY);
		textList1.add(new TranslationTextComponent("mcmc.setup.info.2"));
		moreInfo = "[ " + I18n.format("mcmc.setup.info.more") + " ]";

		textList = new ArrayList<>();

		for (ITextComponent component : textList1)
		{
			textList.addAll(font.trimStringToWidth(component, width / 5 * 3));
		}

		totalHeight = textList.size() * 10 + 50;
		moreInfoWidth = font.getStringWidth(moreInfo);
		moreInfoY = (height - totalHeight) / 2 + totalHeight - 40;

		int by = (height - totalHeight) / 2 + totalHeight - 20;

		addButton(buttonYes = new Button(width / 2 - 110, by, 100, 20, new TranslationTextComponent("mcmc.opt_out"), this::optOutButton));
		addButton(buttonNo = new Button(width / 2 + 10, by, 100, 20, new TranslationTextComponent("gui.back"), this::backButton));
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
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick)
	{
		renderBackground(matrixStack);

		for (int i = 0; i < textList.size(); i++)
		{
			font.drawStringWithShadow(matrixStack, "ABC", (width - font.func_243245_a(textList.get(i))) / 2F, (height - totalHeight) / 2F + i * 10F, 0xFFFFFF);
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