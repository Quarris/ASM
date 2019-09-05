package quarris.asmmod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import quarris.asmmod.client.gui.GuiTerminal;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

	@Nullable
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Nullable
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID <= GuiID.values().length) {
			GuiID guiID = GuiID.values()[ID];
			switch (guiID) {
				case TERMINAL: return new GuiTerminal(world, new BlockPos(x, y, z));
				default: return null;
			}
		}
		return null;
	}

	public enum GuiID {
		TERMINAL
	}

}
