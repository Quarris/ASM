package quarris.asmmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import quarris.asmmod.blocks.tiles.TileTerminal;

public class PacketAttemptCompile implements IMessage, IMessageHandler<PacketAttemptCompile, IMessage> {

	private BlockPos tilePos;

	public PacketAttemptCompile() {
	}

	public PacketAttemptCompile(BlockPos tilePos) {
		this.tilePos = tilePos;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		packetBuffer.writeBlockPos(tilePos);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		tilePos = packetBuffer.readBlockPos();
	}

	@Override
	public IMessage onMessage(PacketAttemptCompile message, MessageContext ctx) {

		WorldServer world = ctx.getServerHandler().player.getServerWorld();
		world.addScheduledTask(() -> {
			TileTerminal tile = (TileTerminal)world.getTileEntity(message.tilePos);
			if (tile != null) {
				tile.compile();
			}
		});
		return null;
	}
}