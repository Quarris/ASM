package quarris.asmmod.client.tesr;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import quarris.asmmod.blocks.tiles.TileTextHologram;

import static net.minecraft.client.renderer.GlStateManager.*;

public class TESRTextHologram extends TileEntitySpecialRenderer<TileTextHologram> {

	@Override
	public void render(TileTextHologram te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		FontRenderer fontRenderer = getFontRenderer();
		float scale = 1 / 64f;
		int textWidth = fontRenderer.getStringWidth(te.text);
		int textHeight = fontRenderer.FONT_HEIGHT;
		pushMatrix(); {
			EnumFacing facing = te.facing;
			EnumFacing orientation = te.orientation;
			EnumFacing direction = EnumFacing.VALUES[te.getBlockMetadata()];
			double tx = x + 0.5f;
			double ty = y + 0.5f;
			double tz = z + 0.5f;
			translate(tx, ty, tz);
			rotate(orientation.getHorizontalIndex() * -90, 0, 1, 0);
			if (facing.getAxis().isVertical()) {
				rotate(facing.getAxisDirection().getOffset() * -90, 1, 0, 0);
			}
			int textX = -textWidth / 2;
			EnumFacing relative = getRelativeFacing(direction, facing, orientation);
			pushMatrix(); {
				scale(scale, -scale, scale);
				if (relative == EnumFacing.WEST) {
					textX = -textWidth;
				}
				else if (relative == EnumFacing.EAST) {
					textX = 0;
				}
				RenderHelper.enableGUIStandardItemLighting();
				drawHologram(textX - 10, -textHeight / 2 - 10, textWidth + 20, textHeight + 20);
				translate(0, 0, 1);
				RenderHelper.disableStandardItemLighting();
				fontRenderer.drawString(te.text, textX, -textHeight / 2, 0);

			}
			popMatrix();
			scale(-scale, -scale, scale);
			textX = -textWidth / 2;
			relative = getRelativeFacing(direction, facing, orientation.getOpposite());
			if (relative == EnumFacing.WEST) {
				textX = -textWidth;
			}
			else if (relative == EnumFacing.EAST) {
				textX = 0;
			}
			RenderHelper.enableGUIStandardItemLighting();
			drawHologram(textX - 10, -textHeight / 2 - 10, textWidth + 20, textHeight + 20);
			translate(0, 0, -1);
			RenderHelper.disableStandardItemLighting();
			fontRenderer.drawString(te.text, textX, -textHeight / 2, 0);
		}
		popMatrix();
	}

	public void drawHologram(int x, int y, int width, int height) {
		pushMatrix();
		int scale = 5;
		int thickness = 1;
		int border = scale * thickness;
		Gui.drawRect(x + border, y + border, x + width - border, y + height - border, 0x5500ff00);    // main
		Gui.drawRect(x, y, x + border, y + height, 0xaa000200);                                            // border left
		Gui.drawRect(x + width - border, y, x + width, y + height, 0xaa000200);                            // border right
		Gui.drawRect(x + border, y, x + width - border, y + border, 0xaa000200);                            // border top
		Gui.drawRect(x + border, y + height - border, x + width - border, y + height, 0xaa000200);                // border bottom
		popMatrix();
	}

	public EnumFacing getRelativeFacing(EnumFacing direction, EnumFacing facing, EnumFacing orientation) {
		Vec3i cross = direction.getDirectionVec().crossProduct(orientation.getDirectionVec());
		if (cross.getY() < 0) {
			return EnumFacing.WEST;
		}
		else if (cross.getY() > 0) {
			return EnumFacing.EAST;
		}
		else {
			return EnumFacing.NORTH;
		}
	}
}
