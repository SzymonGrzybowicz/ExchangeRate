package exchange_rate.utils;

public class StringUtils {

	public static boolean isNumber(String string) {
		try {
			Double.parseDouble(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
