package quarris.asmmod.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import quarris.asmmod.ASMMod;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ASMMod.MODID);

	public static void initPackets() {
		int id = 0;
		INSTANCE.registerMessage(PacketSyncTerminalCode.class, PacketSyncTerminalCode.class, id++, Side.SERVER);
		INSTANCE.registerMessage(PacketAttemptCompile.class, PacketAttemptCompile.class, id++, Side.SERVER);
	}

}
