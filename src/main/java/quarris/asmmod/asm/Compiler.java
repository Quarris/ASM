package quarris.asmmod.asm;

import quarris.asmmod.asm.Operations.Condition;
import quarris.asmmod.asm.Operations.Operation;
import quarris.asmmod.asm.exceptions.CompilerException;

import java.util.*;
import java.util.regex.Matcher;

public class Compiler {

	public static Program compile(String[] raw) throws CompilerException {
		long lineCount = 0;		// The instruction count. Excluding comments, labels and empty lines
		List<Instruction> instructionList = new ArrayList<>();
		Map<String, Long> labelMap = new HashMap<>();

		// Iterate through each string in the raw code.
		for (int i = 0; i < raw.length; i++) {
			String rawLine = raw[i];
			int commentIndex = rawLine.indexOf(";");
			String code = commentIndex == -1 ? rawLine.trim() : rawLine.substring(0, commentIndex).trim();
			if (code.isEmpty()) {
				continue;
			}
			int firstSpace = code.indexOf(' ');

			if (firstSpace == -1) {
				if (code.equalsIgnoreCase("BB")) {
					firstSpace = 2;
				}
				else {
					labelMap.put(code, lineCount);
					continue;
				}
			}

			String composite = code.substring(0, firstSpace).trim();

			Operation operation = null;
			Condition condition = Condition.AL;
			boolean setFlags = false;

			Operation[] allOps = Operation.values();
			for (Operation op : allOps) {
				if (composite.toUpperCase().startsWith(op.name().toUpperCase())) {
					operation = op;
				}
			}

			if (operation == null) {
				throw new CompilerException("Invalid Operation on line " + i);
			}

			String conditionAndFlags = composite.substring(operation.name().length()).trim();

			if (!conditionAndFlags.isEmpty()) {
				if (conditionAndFlags.length() == 1 && conditionAndFlags.equalsIgnoreCase("S")) {
					setFlags = true;
				}
				else {
					condition = null;
					Condition[] allConds = Condition.values();
					for (Condition cond : allConds) {
						if (conditionAndFlags.toUpperCase().startsWith(cond.name().toUpperCase())) {
							condition = cond;
						}
					}

					if (condition == null) {
						throw new CompilerException("Invalid condition on line " + lineCount);
					}

					String setFlagString = conditionAndFlags.substring(condition.name().length()).trim();

					if (!setFlagString.isEmpty()) {
						setFlags = setFlagString.equalsIgnoreCase("S");
					}
				}
			}

			String operandString = code.substring(firstSpace).trim();
			Matcher matcher = operation.pattern.matcher(operandString);

			if (!matcher.matches()) {
				throw new CompilerException("Invalid arguments on line " + i);
			}

			String[] operands = operandString.split("\\s+");

			Instruction instruction = new Instruction(operation, condition, operands, setFlags);
			lineCount++;

			instructionList.add(instruction);
		}
		//System.out.println("Program compiled");
		if (instructionList.isEmpty()) {
			throw new CompilerException("No code found");
		}
		return new Program(raw, instructionList, labelMap);
	}

}
