package quarris.asmmod.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonCompile extends GuiButton {

	private int color;

	public GuiButtonCompile(int buttonId, int x, int y, int width, int height, String buttonText, int color) {
		super(buttonId, x, y, width, height, buttonText);
		this.color = color;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
	}
}
