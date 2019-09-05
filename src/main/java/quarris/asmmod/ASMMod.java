package quarris.asmmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;
import quarris.asmmod.asm.Compiler;
import quarris.asmmod.asm.exceptions.CompilerException;
import quarris.asmmod.asm.Program;
import quarris.asmmod.network.PacketHandler;
import quarris.asmmod.proxy.CommonProxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Mod(modid = ASMMod.MODID, name = ASMMod.NAME, version = ASMMod.VERSION)
public class ASMMod
{
    public static final String MODID = "asmmod";
    public static final String NAME = "Assembly Stuffed Machinery";
    public static final String VERSION = "0.1";

    private static Logger logger;

    public static Program defaultProgram;
    public static File programFile;

	static {
		programFile = new File("file.txt");

		try {
			System.out.println("Started Loading Program");
			BufferedReader reader = new BufferedReader(new FileReader(programFile));
			String[] lines = reader.lines().toArray(String[]::new);
			defaultProgram = Compiler.compile(lines);
		} catch (FileNotFoundException | CompilerException e) {
			e.printStackTrace();
		}
		System.out.println(defaultProgram.getInstructions());
	}

	@SidedProxy(clientSide = "quarris.asmmod.proxy.ClientProxy", serverSide = "quarris.asmmod.proxy.CommonProxy")
	public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        new ASMContent();
		PacketHandler.initPackets();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		proxy.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {

    }
}
