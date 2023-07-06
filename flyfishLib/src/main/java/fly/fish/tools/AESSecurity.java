package fly.fish.tools;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import fly.fish.asdk.MyApplication;
import fly.fish.othersdk.JGSHaretools;

/**
 * AES加密类
 * 
 */
public class AESSecurity {

	public static String encrypt(String input, String key) {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
		} catch (Exception e) {
			MLog.b(e.toString());
		}
		return new String(Base64.encode(crypted));
	}

	public static String decrypt(String input, String key) {
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base64.decode(input));
		} catch (Exception e) {
			MLog.b(e.toString());
			return "";
		}
		return new String(output);
	}

	// 加密结果
	public static String encryptionResult(String data) {
		data = AESSecurity.encrypt(data, getKey());

		return data;
	}

	// 固定加密
	public static String constantEncryptionResult(String data, String key) {
		data = AESSecurity.encrypt(data, key);
		return data;

	}

	// 解密结果
	public static String decryptResult(String data) {
		data = AESSecurity.decrypt(data, getKey());
		MLog.a("AESSecurity",data + "<------ keykey ------> " + getKey());
		return data;
	}

	// 固定解密
	public static String constantdecryptResult(String data, String key) {
		data = AESSecurity.decrypt(data, key);
		MLog.a("AESSecurity-set",data + "<------ keykey ------> " + key);

		return data;
	}

	public static String getKey() {
		return MyApplication.getAppContext().getGameArgs().getKey();
	}
}
