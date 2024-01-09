package fly.fish.tools;

import java.text.DecimalFormat;

public class StringUtils {
    public static String formatKeepTwo(Object data) {
        if (data instanceof String){
            data = Double.parseDouble((String) data);
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0.00"); // 设置要显示的小数位数
        return decimalFormat.format(data);

    }
}
