package quarris.asmmod.utils;

public class NumberUtils {

	public static boolean getBit(long number, int bit) {
		return ((number >> bit) & 1) == 1;
	}

	public static boolean getBit(int number, int bit) {
		return ((number >> bit) & 1) == 1;
	}

	public static boolean getBit(short number, int bit) {
		return ((number >> bit) & 1) == 1;
	}

	public static boolean getBit(byte number, int bit) {
		return ((number >> bit) & 1) == 1;
	}

}
