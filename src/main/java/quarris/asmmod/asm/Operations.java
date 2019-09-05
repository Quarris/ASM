package quarris.asmmod.asm;

import quarris.asmmod.asm.exceptions.ExecuteException;
import quarris.asmmod.asm.operands.IOperand;
import quarris.asmmod.utils.NumberUtils;

import java.util.Arrays;
import java.util.regex.Pattern;


public class Operations {

	public enum Operation {
		ADD("(R|r)\\d\\s+(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		SUB("(R|r)\\d\\s+(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		MUL("(R|r)\\d\\s+(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		DIV("(R|r)\\d\\s+(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		MOD("(R|r)\\d\\s+(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		AND("(R|r)\\d\\s+(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		ORR("(R|r)\\d\\s+(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		XOR("(R|r)\\d\\s+(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		BIC("(R|r)\\d\\s+(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		MOV("(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		CMP("(R|r)\\d\\s+((R|r)\\d|#\\d+)"),
		LD("(R|r)\\d\\s+(R|r)\\d"),
		ST("(R|r)\\d\\s+(R|r)\\d"),
		B("\\S+"),
		BL("\\S+"),
		BB("");

		public final Pattern pattern;

		Operation(String regex) {
			this.pattern = Pattern.compile(regex);
		}

		public void operate(Processor processor, boolean setFlags, IOperand... operands) throws ExecuteException {
			switch (this) {
				case ADD: {
					boolean[] flags = new boolean[4];
					long op1 = operands[1].getData();
					long op2 = operands[2].getData();
					long res = op1+op2;
					System.out.println(res);

					if (setFlags) {
						boolean c63 = NumberUtils.getBit(op1, 63) && NumberUtils.getBit(op2, 63);
						boolean c62 = NumberUtils.getBit(op1, 62) && NumberUtils.getBit(op2, 62);

						flags[0] = res < 0;
						flags[1] = res == 0;
						flags[2] = c62 != c63;
						flags[3] = c63;
						processor.setFlags(flags);
					}
					operands[0].setData(res);
					break;
				}
				case SUB: {
					boolean[] flags = new boolean[4];
					long op1 = operands[1].getData();
					long op2 = operands[2].getData();
					long res = op1-op2;

					if (setFlags) {
						boolean c63 = NumberUtils.getBit(op1, 63) && NumberUtils.getBit(op2, 63);
						boolean c62 = NumberUtils.getBit(op1, 62) && NumberUtils.getBit(op2, 62);

						flags[0] = res < 0;
						flags[1] = res == 0;
						flags[2] = c62 != c63;
						flags[3] = !c63;
						processor.setFlags(flags);
					}
					operands[0].setData(res);
					break;
				}
				case MUL: {
					boolean[] flags = new boolean[4];
					long op1 = operands[1].getData();
					long op2 = operands[2].getData();
					long res = op1*op2;

					if (setFlags) {
						flags[0] = res < 0;
						flags[1] = res == 0;
						processor.setFlags(flags);
					}
					operands[0].setData(res);
					break;
				}
				case DIV: {
					boolean[] flags = new boolean[4];
					long op1 = operands[0].getData();
					long op2 = operands[1].getData();
					if (op2 == 0) {
						throw new ExecuteException("Division by 0");
					}
					long res = op1/op2;

					if (setFlags) {
						flags[0] = res < 0;
						flags[1] = res == 0;
						processor.setFlags(flags);
					}
					operands[0].setData(res);
					break;
				}
				case MOD: {
					operands[0].setData(operands[1].getData()%operands[2].getData());
					break;
				}
				case AND: {
					operands[0].setData(operands[1].getData()&operands[2].getData());
					break;
				}
				case ORR: {
					operands[0].setData(operands[1].getData()|operands[2].getData());
					break;
				}
				case XOR: {
					operands[0].setData(operands[1].getData()^operands[2].getData());
					break;
				}
				case BIC: {
					operands[0].setData(operands[1].getData()&(1-operands[2].getData()));
					break;
				}
				case MOV: {
					operands[0].setData(operands[1].getData());
					break;
				}
				case CMP: {
					boolean[] flags = new boolean[4];
					long op1 = operands[0].getData();
					long op2 = operands[1].getData();
					long res = op1-op2;

					boolean c63 = NumberUtils.getBit(op1, 63) && NumberUtils.getBit(op2, 63);
					boolean c62 = NumberUtils.getBit(op1, 62) && NumberUtils.getBit(op2, 62);

					flags[0] = res < 0;
					flags[1] = res == 0;
					flags[2] = c62 != c63;
					flags[3] = !c63;
					processor.setFlags(flags);
					break;
				}
				case LD: {

					break;
				}
				case ST: {

					break;
				}
				case B: {
					if (operands[0].getData() >= 0 && operands[0].getData() <= Integer.MAX_VALUE) {
						processor.setProgramCounter((int)operands[0].getData());
					}
					break;
				}
				case BL: {
					if (operands[0].getData() >= 0 && operands[0].getData() <= Integer.MAX_VALUE) {
						processor.setLinkRegister(processor.getProgramCounter());
						processor.setProgramCounter((int)operands[0].getData());
					}
					break;
				}
				case BB: {
					processor.setProgramCounter(processor.getLinkRegister());
					break;
				}

			}
		}
	}

	public enum Condition {
		AL,
		HI,
		LS,
		CC,
		CS,
		NE,
		EQ,
		OC,
		OS,
		PO,
		MI,
		GE,
		LT,
		GT,
		LE;

		public boolean matches(boolean[] flags) {
			switch (this) {
				case AL: return true;
				case HI: return !(flags[3] | flags[1]);
				case LS: return flags[3] | flags[1];
				case CC: return !flags[3];
				case CS: return flags[3];
				case NE: return !flags[1];
				case EQ: return flags[1];
				case OC: return !flags[2];
				case OS: return flags[2];
				case PO: return !flags[0];
				case MI: return flags[0];
				case GE: return !((!flags[0] && flags[2]) || (flags[0] && !flags[2]));
				case LT: return (!flags[0] && flags[2]) || (flags[0] && !flags[2]);
				case GT: return !((!flags[0] && flags[2]) || (flags[0] && !flags[2]) || flags[1]);
				case LE: return (!flags[0] && flags[2]) || (flags[0] && !flags[2]) || flags[1];
				default: return false;
			}
		}
	}
}
