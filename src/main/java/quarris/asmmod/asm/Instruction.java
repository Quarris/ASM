package quarris.asmmod.asm;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import quarris.asmmod.asm.Operations.Condition;
import quarris.asmmod.asm.Operations.Operation;

import java.util.Arrays;

public class Instruction {

	public Operation operation;
	public Condition condition;
	public String[] operands;
	public boolean setFlags;

	public Instruction(Operation operation, Condition condition, String[] operands, boolean setFlags) {
		this.operation = operation;
		this.condition = condition;
		this.operands = operands;
		this.setFlags = setFlags;
	}

	@Override
	public String toString() {
		return "Instruction{" +
				"operation=" + operation +
				", condition=" + condition +
				", operands=" + Arrays.toString(operands) +
				'}';
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("Operation", operation.ordinal());
		compound.setInteger("Condition", condition.ordinal());
		NBTTagList operandList = new NBTTagList();
		for (String s : operands) {
			operandList.appendTag(new NBTTagString(s));
		}
		compound.setTag("Operands", operandList);
		compound.setBoolean("Flags", setFlags);
		return compound;
	}

	public static Instruction fromNBT(NBTTagCompound compound) {
		Operation operation = Operation.values()[compound.getInteger("Operation")];
		Condition condition = Condition.values()[compound.getInteger("Condition")];
		NBTTagList operandList = compound.getTagList("Operands", Constants.NBT.TAG_STRING);
		String[] operands = new String[operandList.tagCount()];
		for (int i = 0; i < operandList.tagCount(); i++) {
			operands[i] = operandList.getStringTagAt(i);
		}
		boolean setFlags = compound.getBoolean("Flags");
		return new Instruction(operation, condition, operands, setFlags);
	}
}
