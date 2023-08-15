package fly.fish.config;

public class Configs {
	/** 随版本发布的BV */
	public static String BV = "20170331";
	/** use XPKG */
	public static boolean USEXPKG = true;
	/** 要解析数据头 */
	public static boolean ALLUSED = true;
	/** 数据包目录 */
	public static String ASDK = "flyfish_asdk";
	/** SD是否存在 */
	public static boolean SDEXIST = false;
	/** SD剩余大小 */
	public static long SDSIZE = 0;
	/** ASDK根目录 */
	public static String ASDKROOT = null;
	/** 注册成功码 */
	public static int REGISTERSUCCESS = 100;
	/** 注册失败码 */
	public static int REGISTERFAILURE = 101;
	/** 登陆成功码 */
	public static int LOGINSUCCESS = 102;
	/** 登陆失败码 */
	public static int LOGINFAILURE = 103;
	/** 充值成功码 */
	public static int CHARGESUCCESS = 104;
	/** 充值失败码 */
	public static int CHARGEAILURE = 105;
	/** 8种基本数据类型 */
	public static Class<Double> tdouble = Double.TYPE;
	public static Class<Float> tfloat = Float.TYPE;
	public static Class<Long> tlong = Long.TYPE;
	public static Class<Integer> tint = Integer.TYPE;
	public static Class<Short> tshort = Short.TYPE;
	public static Class<Character> tchar = Character.TYPE;
	public static Class<Byte> tbyte = Byte.TYPE;
	public static Class<Boolean> tboolean = Boolean.TYPE;

	/** 对象类型 */
	public static Class<String> tstring = String.class;
	/** 客服qq*/
	public static String qqContactWay = "";

}
