package quarris.asmmod.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import quarris.asmmod.asm.exceptions.MemoryAccessException;
import quarris.asmmod.blocks.tiles.base.TileMemory;
import quarris.asmmod.utils.NumberUtils;

import javax.annotation.Nullable;

public class TileTextHologram extends TileMemory {

	public String text;
	public EnumFacing facing;
	public EnumFacing orientation;
	public static final int MAX_CHARS = 16;
	public static final int MAX_LINES = 4;

	public boolean modified;

	public TileTextHologram() {
		text = "";
	}

	@Override
	public void tick() {
		if (modified) {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < MAX_LINES * MAX_CHARS; i++) {
				b.append(getFormattedChar(i));
			}
			text = ChatAllowedCharacters.filterAllowedCharacters(b.toString());
			modified = false;
		}
	}

	@Override
	public int getMemorySize() {
		return MAX_CHARS * MAX_LINES + 1;
	}

	@Override
	public void onMemoryWrite(int address, long old) {
		modified = true;
	}

	private String getFormattedChar(int index) {
		try {
			index++;    // address for the text starts at 1
			long data = getData(index);
			char c = (char) (data & 256);

			int color = (int) ((data >> 8) & 256);
			int format = (int) ((data >> 16) & 256);

			StringBuilder formats = new StringBuilder();
			if (NumberUtils.getBit(format, 0)) {
				formats.append(TextFormatting.OBFUSCATED);
			}
			if (NumberUtils.getBit(format, 1)) {
				formats.append(TextFormatting.BOLD);
			}
			if (NumberUtils.getBit(format, 2)) {
				formats.append(TextFormatting.STRIKETHROUGH);
			}
			if (NumberUtils.getBit(format, 3)) {
				formats.append(TextFormatting.UNDERLINE);
			}
			if (NumberUtils.getBit(format, 4)) {
				formats.append(TextFormatting.ITALIC);
			}

			if (color >= 16) {
				color = 0;
			}
			StringBuilder ret = new StringBuilder()
					.append(TextFormatting.fromColorIndex(color).toString())
					.append(formats)
					.append(String.valueOf(c))
					.append(TextFormatting.RESET);

			return ret.toString();
		} catch (MemoryAccessException e) {
			e.printStackTrace();
		}
		return "";
	}


	// Technical Tile stuffings \/
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("Facing", facing.getIndex());
		compound.setInteger("Orientation", orientation.getHorizontalIndex());
		compound.setString("Text", text);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		facing = EnumFacing.VALUES[compound.getInteger("Facing")];
		orientation = EnumFacing.HORIZONTALS[compound.getInteger("Orientation")];
		text = compound.getString("Text");
		super.readFromNBT(compound);
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 39, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
}
