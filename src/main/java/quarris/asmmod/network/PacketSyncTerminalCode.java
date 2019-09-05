package quarris.asmmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import quarris.asmmod.blocks.tiles.TileTerminal;
import quarris.asmmod.utils.ModUtils;

import java.util.ArrayList;
import java.util.List;

public class PacketSyncTerminalCode implements IMessage, IMessageHandler<PacketSyncTerminalCode, IMessage> {

	private List<StringBuilder> code;
	private BlockPos pos;

	public PacketSyncTerminalCode() {
		this.code = new ArrayList<>();
	}

	public PacketSyncTerminalCode(List<StringBuilder> code, BlockPos pos) {
		this.code = code;
		this.pos = pos;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(code.size());
		for (StringBuilder s : code) {
			buffer.writeString(s.toString());
		}
		buffer.writeBlockPos(pos);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		int len = buffer.readInt();
		for (int i = 0; i < len; i++) {
			this.code.add(new StringBuilder(buffer.readString(Short.MAX_VALUE)));
		}
		this.pos = buffer.readBlockPos();
	}

	@Override
	public IMessage onMessage(PacketSyncTerminalCode message, MessageContext ctx) {

		ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
			World world = ctx.getServerHandler().player.getServerWorld();
			TileTerminal terminal = (TileTerminal)world.getTileEntity(message.pos);
			if (terminal != null) {
				terminal.setCode(message.code);
				ModUtils.dispatchTEToNearbyPlayers(terminal);
			}
		});

		return null;
	}
}
