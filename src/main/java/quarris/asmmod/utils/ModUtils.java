package quarris.asmmod.utils;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import quarris.asmmod.ASMMod;

public class ModUtils {

	public static ResourceLocation createRes(String name) {
		return new ResourceLocation(ASMMod.MODID, name);
	}

	//Don't call from the client.
	public static void dispatchTEToNearbyPlayers(TileEntity tile) {
		WorldServer world = (WorldServer) tile.getWorld();
		PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(tile.getPos().getX() >> 4, tile.getPos().getZ() >> 4);

		if (entry == null) {
			return;
		}

		for (EntityPlayerMP player : entry.getWatchingPlayers())
			player.connection.sendPacket(tile.getUpdatePacket());

	}
}
