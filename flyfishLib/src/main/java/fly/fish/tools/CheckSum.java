package fly.fish.tools;

public class CheckSum {
	public static boolean isNumber(String str) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile("[0-9]*");
		java.util.regex.Matcher match = pattern.matcher(str.trim());
		return match.matches();
	}

	public static boolean isBigDecimal(String str) {
		java.util.regex.Matcher match = null;
		if (isNumber(str) == true) {
			if(str.trim().indexOf("0")==0){
				return false;
			}
			if (Double.parseDouble(str) > 5000 || Double.parseDouble(str) < 1) {
				return false;
			}
			java.util.regex.Pattern pattern = java.util.regex.Pattern
					.compile("[0-9]*");
			match = pattern.matcher(str.trim());
		} else {
			if (str.trim().indexOf(".") == -1) {
				java.util.regex.Pattern pattern = java.util.regex.Pattern
						.compile("^[+-]?[0-9]*");
				match = pattern.matcher(str.trim());
			} else {
				
				java.util.regex.Pattern pattern = java.util.regex.Pattern
						.compile("^[+-]?[0-9]+(\\.\\d{1,100}){1}");
				match = pattern.matcher(str.trim());
			}
		}
		return match.matches();
	}
}
