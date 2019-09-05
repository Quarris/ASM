package quarris.asmmod.asm;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {

	private String[] raw;
	private List<Instruction> instructions;
	private Map<String, Long> labels;

	public Program(String[] raw, List<Instruction> instructions, Map<String, Long> labels) {
		this.raw = raw;
		this.instructions = instructions;
		this.labels = labels;
	}

	public Program(List<Instruction> instructions, Map<String, Long> labels) {
		this.instructions = instructions;
		this.labels = labels;
	}

	public String[] getRaw() {
		return raw;
	}

	public Instruction get(int index) {
		return instructions.get(index);
	}

	public int size() {
		return instructions.size();
	}

	public long getLabelIndex(String label) {
		return labels.get(label);
	}

	public List<Instruction> getInstructions() {
		return instructions;
	}

	public Map<String, Long> getLabels() {
		return labels;
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList instructionList = new NBTTagList();
		for (Instruction i : instructions) {
			instructionList.appendTag(i.toNBT());
		}
		compound.setTag("Instructions", instructionList);
		NBTTagList labelList = new NBTTagList();
		for (Map.Entry<String, Long> entry : labels.entrySet()) {
			NBTTagCompound entryTag = new NBTTagCompound();
			entryTag.setString("Name", entry.getKey());
			entryTag.setLong("Value", entry.getValue());
			labelList.appendTag(entryTag);
		}
		compound.setTag("Labels", labelList);
		return compound;
	}

	public static Program fromNBT(NBTTagCompound compound) {
		NBTTagList instructionList = compound.getTagList("Instructions", Constants.NBT.TAG_COMPOUND);
		List<Instruction> instructions = new ArrayList<>();
		for (int i = 0; i < instructionList.tagCount(); i++) {
			instructions.add(i, Instruction.fromNBT(instructionList.getCompoundTagAt(i)));
		}

		NBTTagList labelList = compound.getTagList("Labels", Constants.NBT.TAG_COMPOUND);
		Map<String, Long> labels = new HashMap<>();
		for (int i = 0; i < labelList.tagCount(); i++) {
			NBTTagCompound entryTag = labelList.getCompoundTagAt(i);
			labels.put(entryTag.getString("Name"), entryTag.getLong("Value"));
		}
		return new Program(instructions, labels);
	}
}
