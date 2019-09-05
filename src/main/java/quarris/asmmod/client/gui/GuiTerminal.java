package quarris.asmmod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
import quarris.asmmod.blocks.tiles.TileTerminal;
import quarris.asmmod.client.gui.components.GuiButtonCompile;
import quarris.asmmod.network.PacketAttemptCompile;
import quarris.asmmod.network.PacketHandler;
import quarris.asmmod.network.PacketSyncTerminalCode;
import quarris.asmmod.utils.ModUtils;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.input.Keyboard.*;

public class GuiTerminal extends GuiScreen {

	private static final ResourceLocation TEX = ModUtils.createRes("textures/gui/terminal.png");
	private static final int GUI_WIDTH = 256;
	private static final int GUI_HEIGHT = 206;
	private static final int TERMINAL_WIDTH = GUI_WIDTH - 18;
	private static final int TERMINAL_HEIGHT = GUI_HEIGHT - 18;
	private static final int TERMINAL_LINES = 16;

	private List<StringBuilder> code;

	// World vars
	public final BlockPos tilePos;
	public final World world;

	// Terminal positions
	private int x, y;
	private int terminalX, terminalY;
	private int cursorX, cursorY;        // Pixel X Position, Line Y position. In code		(Absolute)
	private int viewX, viewY;            // Pixel X Position, Line Y Position. In display	(Relative)

	// Gui Buttons
	private GuiButtonCompile compileButton;

	public GuiTerminal(World world, BlockPos tilePos) {
		TileTerminal tile = (TileTerminal) world.getTileEntity(tilePos);
		code = tile.getCode();
		if (code.isEmpty()) {
			code.add(new StringBuilder());
		}
		this.tilePos = tilePos;
		this.world = world;
	}

	@Override
	public void initGui() {
		x = (this.width - GUI_WIDTH) / 2;
		y = (this.height - GUI_HEIGHT) / 2 - 15;
		terminalX = x + 9;
		terminalY = y + 9;

		compileButton = new GuiButtonCompile(0, terminalX - 1, terminalY + TERMINAL_HEIGHT - 19, 50, 20, "Compile", 0xff00ff00);

		addButton(compileButton);

		enableRepeatEvents(true);
	}

	@Override
	public void updateScreen() {
		try {
			TileTerminal tile = (TileTerminal) world.getTileEntity(tilePos);
			code = tile.getCode();
			if (this.code.isEmpty()) {
				this.code.add(new StringBuilder());
			}
			updateScrollViews();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		try {
			drawDefaultBackground();
			mc.getTextureManager().bindTexture(TEX);
			drawTexturedModalRect(x, y, 0, 0, GUI_WIDTH, GUI_HEIGHT);
			drawLineNumber();
			super.drawScreen(mouseX, mouseY, partialTicks);
			drawCompilerError();
			drawText();
			if (mc.world.getTotalWorldTime() % 20 < 10) {
				drawCursor();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void updateScrollViews() {
		if (viewX > cursorX) {
			viewX = cursorX;
		}
		else {
			StringBuilder s = getLine();
			if (cursorX > TERMINAL_WIDTH) {
				viewX = cursorX - getStringWidth(trimString(s.substring(0, getXInString(cursorX, cursorY)), TERMINAL_WIDTH, true));
			}
		}
		if (viewY > cursorY) {
			viewY = cursorY;
		}
		else {
			if (cursorY - viewY > TERMINAL_LINES - 1) {
				viewY = cursorY - (TERMINAL_LINES - 1);
			}
		}
	}

	public void drawCursor() {
		drawVerticalLine(
				terminalX - 1 + getStringWidth(getViewableString().substring(0, getXInString(cursorX, cursorY) - getXInString(viewX, cursorY))),    // X
				terminalY - 1 + (cursorY - viewY) * (fontRenderer.FONT_HEIGHT + 1),                                          // Y top
				terminalY - 1 + (cursorY - viewY) * (fontRenderer.FONT_HEIGHT + 1) + fontRenderer.FONT_HEIGHT, 0xff00ff00);  // Y bot
	}

	private void drawCompilerError() {
		TileTerminal tile = (TileTerminal) world.getTileEntity(tilePos);
		String res = tile.compilerResult;
		if (res != null) {
			fontRenderer.drawSplitString(res, terminalX + 57, terminalY + TERMINAL_HEIGHT - 18, 128, 0xffcc2222);
		}
	}

	public void drawText() {
		for (int i = 0; i < TERMINAL_LINES; i++) {
			int yIndex = viewY + i;
			if (yIndex >= code.size()) {
				break;
			}
			String s = code.get(yIndex).toString();

			if (yIndex == cursorY) {
				fontRenderer.drawString(getViewableString(), terminalX, terminalY + i * (fontRenderer.FONT_HEIGHT + 1), 0xff00ff00);
			}
			else {
				fontRenderer.drawString(trimString(s, TERMINAL_WIDTH), terminalX, terminalY + i * (fontRenderer.FONT_HEIGHT + 1), 0xff00ff00);
			}
		}
	}

	public void drawLineNumber() {
		String c = "Char " + getXInString(cursorX, cursorY);
		String l = "Line " + cursorY;
		fontRenderer.drawString(l, terminalX + TERMINAL_WIDTH - 48, terminalY + TERMINAL_HEIGHT - fontRenderer.FONT_HEIGHT * 2, 0xff00ff00);
		fontRenderer.drawString(c, terminalX + TERMINAL_WIDTH - 48, terminalY + TERMINAL_HEIGHT - fontRenderer.FONT_HEIGHT + 1, 0xff00ff00);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (clickedMouseButton == 0) {
			boolean flag = mouseX >= this.x && mouseX < this.x + GUI_WIDTH && mouseY >= this.y && mouseY < this.y + GUI_HEIGHT;
			if (flag) {
				int guiMouseX = mouseX - this.terminalX;
				int guiMouseY = mouseY - this.terminalY;
				if (guiMouseX >= 0 && guiMouseX <= TERMINAL_WIDTH && guiMouseY >= 0 && guiMouseY <= TERMINAL_HEIGHT - 21) {
					cursorY = viewY + guiMouseY / 10;
					cursorX = viewX + guiMouseX;
					if (cursorY > code.size() - 1) {
						cursorY = code.size() - 1;
					}
					int w = getStringWidth(getLine().toString());
					if (cursorX > w) {
						cursorX = w;
					}
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseButton == 0) {
			boolean flag = mouseX >= this.x && mouseX < this.x + GUI_WIDTH && mouseY >= this.y && mouseY < this.y + GUI_HEIGHT;
			if (flag) {
				int guiMouseX = mouseX - this.terminalX;
				int guiMouseY = mouseY - this.terminalY;
				if (guiMouseX >= 0 && guiMouseX <= TERMINAL_WIDTH && guiMouseY >= 0 && guiMouseY <= TERMINAL_HEIGHT - 21) {
					cursorY = viewY + guiMouseY / 10;
					cursorX = viewX + guiMouseX;
					if (cursorY > code.size() - 1) {
						cursorY = code.size() - 1;
					}
					int w = getStringWidth(getLine().toString());
					if (cursorX > w) {
						cursorX = w;
					}
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == compileButton) {
			PacketHandler.INSTANCE.sendToServer(new PacketAttemptCompile(tilePos));
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		try {
			switch (keyCode) {
				case KEY_UP: {            // Up Arrow - Cursor up
					handleCursorUp();
					break;
				}
				case KEY_DOWN: {        // Down Arrow - Cursor down
					handleCursorDown();
					break;
				}
				case KEY_LEFT: {        // Left Arrow - Cursor left
					handleCursorLeft();
					break;
				}
				case KEY_RIGHT: {        // Right Arrow - Cursor right
					handleCursorRight();
					break;
				}
				case KEY_HOME: {        // Home - Go to start
					handleCursorStart();
					break;
				}
				case KEY_END: {            // End - Go to end
					handleCursorEnd();
					break;
				}
				case KEY_BACK: {         // Backspace - Delete character back
					handleDeletePrev();
					break;
				}
				case KEY_DELETE: {        // Delete - Delete character forwards
					handleDeleteNext();
					break;
				}
				case KEY_RETURN: {        // Enter/Return - New line
					handleNewLine();
					break;
				}
				case KEY_TAB: {            // Tab - Indent
					handleIndent();
					break;
				}
				default: {
					handleDefaultKey(typedChar);
				}
			}
			if (isKeyComboCtrlV(keyCode)) {
				String clipboard = getClipboardString().replaceAll("\t", "  ");

				String[] split = clipboard.split("\n");
				insertLines(split, cursorX, cursorY);
			}
			saveToTile();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int wheel = Mouse.getDWheel() / 120;
		if (wheel != 0) {    // Scroll Down
			viewX = 0;
			cursorY -= wheel;
			if (cursorY < 0) {
				cursorY = 0;
			}
			if (cursorY > code.size() - 1) {
				cursorY = code.size() - 1;
			}
		}
	}

	private void insertLines(String[] lines, int lineX, int lineY) {
		if (lines.length == 1) {
			code.get(lineY).insert(lineX, lines[0]);
			return;
		}
		for (int i = 0; i < lines.length; i++) {
			if (i == 0) {
				StringBuilder s = code.get(lineY);
				String spare = s.substring(lineX);
				s.insert(lineX, lines[i]).delete(lineX + lines[i].length(), s.length());
				lines[lines.length - 1] = lines[lines.length - 1] + spare;
			}
			else {
				StringBuilder builder = new StringBuilder(lines[i]);
				code.add(lineY + i, builder);
			}
		}
		cursorY += lines.length - 1;
		cursorX = getStringWidth(lines[lines.length - 1]);
	}

	public void handleCursorUp() {
		viewX = 0;
		if (cursorY > 0) {
			cursorY--;
		}
		else if (cursorY == 0) {
			cursorX = 0;
		}
	}

	public void handleCursorDown() {
		StringBuilder s = getLine();
		viewX = 0;
		if (cursorY < code.size() - 1) {
			cursorY++;
		}
		else if (cursorY == code.size() - 1) {
			cursorX = getStringWidth(s.toString());
		}
	}

	public void handleCursorLeft() {
		int index = getXInString(cursorX, cursorY);
		cursorX = getStringWidth(getLine().substring(0, index));
		if (index > 0) {
			StringBuilder line = getLine();
			if (index == line.length()) {
				cursorX = getStringWidth(line.substring(0, line.length() - 1));
			}
			else {
				cursorX = getStringWidth(line.substring(0, index - 1));
			}
		}
		else {
			if (cursorY > 0) {
				cursorY--;
				cursorX = getStringWidth(getLine().toString());
			}
		}
	}

	public void handleCursorRight() {
		int index = getXInString(cursorX, cursorY);
		if (index < getLine().length()) {
			cursorX = getStringWidth(getLine().substring(0, index + 1));
		}
		else {
			if (cursorY < code.size() - 1) {
				cursorY++;
				cursorX = 0;
			}
		}
	}

	public void handleCursorStart() {
		cursorX = 0;
	}

	public void handleCursorEnd() {
		cursorX = getStringWidth(getLine().toString());
	}

	public void handleDeleteNext() {
		StringBuilder s = getLine();
		int cx = getXInString(cursorX, cursorY);
		if (cx < s.length()) {
			s.deleteCharAt(cx);
		}
		else if (cursorY < code.size() - 1) {
			StringBuilder old = code.remove(cursorY + 1);
			s.append(old);
		}
	}

	public void handleDeletePrev() {
		StringBuilder s = getLine();
		int index = getXInString(cursorX, cursorY) - 1;
		if (index >= 0) {
			s.deleteCharAt(index);
			cursorX = getStringWidth(s.substring(0, index));
		}
		else if (cursorY > 0) {
			StringBuilder old = code.remove(cursorY);
			cursorY--;
			s = getLine();
			cursorX = getStringWidth(s.toString());
			s.append(old);
		}
	}

	public void handleNewLine() {
		StringBuilder old = getLine();
		String enterString = old.substring(getXInString(cursorX, cursorY));
		old.delete(getXInString(cursorX, cursorY), old.length());
		code.add(cursorY + 1, new StringBuilder(enterString));
		cursorY++;
		cursorX = 0;
		viewX = 0;
	}

	public void handleIndent() {
		StringBuilder s = getLine();
		if (isShiftKeyDown()) {
			for (int i = 0; i < 2; i++) {
				if (s.charAt(0) == ' ') {
					s.deleteCharAt(0);
					cursorX -= getStringWidth(" ");
				}
			}
		}
		else {
			s.insert(getXInString(cursorX, cursorY), "  ");
			cursorX += getStringWidth("  ");
		}
	}

	public void handleDefaultKey(char typedChar) {
		if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
			getLine().insert(getXInString(cursorX, cursorY), typedChar);
			cursorX += getStringWidth(String.valueOf(typedChar));
		}
	}

	/**
	 * Gets the current line as a StringBuilder.
	 */
	public StringBuilder getLine() {
		int lineY = MathHelper.clamp(cursorY, 0, code.size() - 1);
		return code.get(lineY);
	}

	/**
	 * Get the pixel position clamped to the closest character position.
	 */
	public int getPixelXClamped(int lineX, int lineY) {
		StringBuilder s = code.get(lineY);
		return getStringWidth(s.substring(0, getXInString(lineX, lineY)));
	}

	public int getXInString(int x, int lineY) {
		lineY = MathHelper.clamp(lineY, 0, code.size() - 1);
		StringBuilder b = code.get(lineY);
		int lineX = getStringLengthFromWidth(b.toString(), x);
		if (lineX >= b.length()) {
			lineX = b.length();
		}
		else {
			int chatW = getStringWidth(String.valueOf(b.charAt(lineX)));
			lineX = getStringLengthFromWidth(b.toString(), x + chatW / 2);
		}
		return lineX;
	}

	private boolean canEdit() {
		return ((TileTerminal) world.getTileEntity(tilePos)).editor.equals(Minecraft.getMinecraft().player.getUniqueID());
	}

	/**
	 * Gets the viewable string based on where the cursor currently is,
	 * constrained to the size of the terminal.
	 */
	public String getViewableString() {
		return trimString(getLine().substring(getXInString(viewX, cursorY)), TERMINAL_WIDTH);
	}

	public int getStringWidth(String s) {
		return fontRenderer.getStringWidth(s);
	}

	/**
	 * Trims the string to a particular pixel width;
	 */
	public String trimString(String s, int width) {
		return fontRenderer.trimStringToWidth(s, width);
	}

	/**
	 * Trims the string to a particular pixel width (with reverse);
	 */
	public String trimString(String s, int width, boolean reverse) {
		return fontRenderer.trimStringToWidth(s, width, reverse);
	}

	/**
	 * Gets the character count in a string which fits the given width.
	 */
	public int getStringLengthFromWidth(String s, int width) {
		return trimString(s, width).length();
	}

	/**
	 * Gets the character count in a string which fits the given width (with reverse).
	 */
	public int getStringLengthFromWidth(String s, int width, boolean reverse) {
		return trimString(s, width, reverse).length();
	}

	public void saveToTile() {
		PacketHandler.INSTANCE.sendToServer(new PacketSyncTerminalCode(code, tilePos));
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		enableRepeatEvents(false);
	}
}