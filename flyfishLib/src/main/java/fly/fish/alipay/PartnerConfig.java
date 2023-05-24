package fly.fish.alipay;

public class PartnerConfig {

	// 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	// public static final String PARTNER = "2088901327790323";
	// 商户收款的支付宝账号
	// public static final String SELLER = "bjzxt2013@163.COM";
	// 商户（RSA）私钥
	// public static final String RSA_PRIVATE =
	// "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEA1sheNXPJDph8radA" +
	// "imKE2AlJ9lPucqu6sFgFiLgt7E89CgFd984UFKXl7G+1rmImQ16ztTdVslHoYILl" +
	// "UJQz6QIDAQABAkEAvBKqA+5GcURvcHQTHNpV7wJ7RHqMQtdDW2VIO0bj15aTmRm5"
	// + "CJqeRlrmXO7b05fLLqnXYKqCLcT4nl9I3Z2onQIhAPpFvP09MSOHiJ3fQ6ax+4mA" +
	// "KGOwQniHnxAj6Tr20jsXAiEA27K1GKbu1GX6TxZMVsc+9uqGvRrdXCRKwqF/jM5m" +
	// "aP8CIEiOn1PrwatDR+A9MifJwdsDRLJiD2NSLlNHlf56QEjRAiEAluY6SQJvzGd2" +
	// "89dm+7vC3ancfgrzvBQZXXG7wCUMZlUCIElzFqC+M8Z/oXDS0HMpS7ZfprxpgLTX"
	// + "NNfVqyBKNLd1";
	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	// public static final String RSA_ALIPAY_PUBLIC =
	// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANbIXjVzyQ6YfK2nQIpihNgJSfZT7nKrurBYBYi4LexPPQoBXffOFBSl5exvta5iJkNes7U3VbJR6GCC5VCUM+kCAwEAAQ==";
	// 支付宝安全支付服务APK的名称，必须与assets目录下的APK名称一致
	// public static final String ALIPAY_PLUGIN_NAME =
	// "alipay_plugin_20120428msp.apk";

	public static String PARTNER = "";
	public static String SELLER = "";
	public static String RSA_PRIVATE = "";
	public static String RSA_ALIPAY_PUBLIC = "";
	public static String ALIPAY_PLUGIN_NAME = "";

	public static String getPARTNER2() {
		return PARTNER;
	}

	public static void setPARTNER2(String pARTNER2) {
		PARTNER = pARTNER2;
	}

	public static String getSELLER2() {
		return SELLER;
	}

	public static void setSELLER2(String sELLER2) {
		SELLER = sELLER2;
	}

	public static String getRSA_PRIVATE2() {
		return RSA_PRIVATE;
	}

	public static void setRSA_PRIVATE2(String rSA_PRIVATE2) {
		RSA_PRIVATE = rSA_PRIVATE2;
	}

	public static String getRSA_ALIPAY_PUBLIC2() {
		return RSA_ALIPAY_PUBLIC;
	}

	public static void setRSA_ALIPAY_PUBLIC2(String rSA_ALIPAY_PUBLIC2) {
		RSA_ALIPAY_PUBLIC = rSA_ALIPAY_PUBLIC2;
	}

	public static String getALIPAY_PLUGIN_NAME2() {
		return ALIPAY_PLUGIN_NAME;
	}

	public static void setALIPAY_PLUGIN_NAME2(String aLIPAY_PLUGIN_NAME2) {
		ALIPAY_PLUGIN_NAME = aLIPAY_PLUGIN_NAME2;
	}

}
