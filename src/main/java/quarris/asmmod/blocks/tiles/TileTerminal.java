package quarris.asmmod.blocks.tiles;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.common.util.Constants;
import quarris.asmmod.asm.Compiler;
import quarris.asmmod.asm.exceptions.CompilerException;
import quarris.asmmod.blocks.tiles.base.TileProcessor;
import quarris.asmmod.utils.ModUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TileTerminal extends TileProcessor {

	public String compilerResult;
	private List<StringBuilder> code;
	public List<UUID> editor = new ArrayList<>();

	public TileTerminal() {
		this.code = new ArrayList<>();
	}

	public List<StringBuilder> getCode() {
		return code;
	}

	public void setCode(List<StringBuilder> code) {
		this.code = code;
	}

	public void compile() {
		System.out.println("Compiling");
		String[] raw = new String[code.size()];
		for (int i = 0; i < code.size(); i++) {
			raw[i] = code.get(i).toString();
		}
		try {
			loadProgram(Compiler.compile(raw));
			compilerResult = null;
			System.out.println("Compiled");

		} catch (CompilerException e) {
			compilerResult = e.getMessage();
			System.out.println("Failed " + compilerResult);
		}
		finally {
			ModUtils.dispatchTEToNearbyPlayers(this);
		}
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList codeTag = new NBTTagList();
		for (StringBuilder s : code) {
			codeTag.appendTag(new NBTTagString(s.toString()));
		}
		compound.setTag("Code", codeTag);
		compound.setString("CompileResult", compilerResult == null ? "null" : compilerResult);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		code.clear();
		NBTTagList codeTag = compound.getTagList("Code", Constants.NBT.TAG_STRING);
		for (NBTBase base : codeTag) {
			code.add(new StringBuilder(((NBTTagString)base).getString()));
		}
		String compRes = compound.getString("CompileResult");
		this.compilerResult = compRes.equals("null") ? null : compRes;
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
