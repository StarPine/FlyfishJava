package fly.fish.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;
import org.keplerproject.luajava.LuaState;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;

import com.android.zs.volley.Request.Method;
import com.android.zs.volley.RequestQueue;
import com.android.zs.volley.Response.ErrorListener;
import com.android.zs.volley.Response.Listener;
import com.android.zs.volley.VolleyError;
import com.android.zs.volley.toolbox.JsonObjectRequest;
import com.android.zs.volley.toolbox.JsonRequest;
import com.android.zs.volley.toolbox.Volley;

import fly.fish.asdk.HttpAsynTask;
import fly.fish.asdk.KjavaAsynTask;
import fly.fish.asdk.MyActivity;
import fly.fish.asdk.MyApplication;
import fly.fish.config.Configs;

public class HttpUtils {
	/** 文件下载失败 */
	public final static int FILEDOWNERR = 1;
	/** 下载重连次数 */
	public final static int RECONNECTNUM = 5;
	/** 代理地址 */
	public static Proxy mProxy = null;
	public static String cookiestat = null;
	public final static String KEY = "48fhd5748sayuh12";
	private static String sTag = "HttpUtils";
	/**
	 * 数据包是否要升级po
	 * 
	 * @param gameno
	 * @return
	 */
	public static boolean isUpdate() {
		String gamenumber = MyApplication.getAppContext().getGameArgs().getPrefixx();
		return false;
	}

	/**
	 * 封装请求头信息
	 * 
	 * @return
	 * @throws IOException
	 */
	public static URLConnection headMethod(String urls, int flag) {
		URL url = null;
		URLConnection urlConn = null;
		detectProxy();
		try {
			url = new URL(urls);
			if (mProxy != null) {
				urlConn = (HttpURLConnection) url.openConnection(mProxy);
			} else {
				urlConn = (HttpURLConnection) url.openConnection();
			}
		} catch (MalformedURLException e1) {
			url = null;
			e1.printStackTrace();
		} catch (IOException e) {
			urlConn = null;
			e.printStackTrace();
		}
		if (url != null && urlConn != null) {
			LuaState Luastate = MyApplication.getAppContext().getmLuaState();
			switch (flag) {
			case 0:
				// 找准程序入口
				synchronized (Luastate) {
					Luastate.getGlobal("utils");
					int index = Luastate.getTop();

					Luastate.getField(index, "httphead");
					Luastate.pushJavaObject(urlConn);
					LuaTools.dbcall(Luastate, 1, 0);
				}
				break;
			case 1:
				// 设置通用的请求属性
				synchronized (Luastate) {
					urlConn.setRequestProperty("Cookie", cookiestat);
					Luastate.getGlobal("utils");
					int index = Luastate.getTop();
					Luastate.getField(index, "webgamehttphead");
					Luastate.pushJavaObject(urlConn);
					LuaTools.dbcall(Luastate, 1, 0);
				}

				// urlConn.setRequestProperty("Host", "download.cmgame.com");
				// urlConn.setRequestProperty("Connection", "keep-alive");
				// urlConn.setRequestProperty(
				// "User-Agent",
				// "Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 Nokia5230/10.0.021; Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413");
				// urlConn.setRequestProperty("Accept", "*/*");
				// urlConn.setRequestProperty("Range", " 0-20480");
				break;
			case 2:

				// 设置通用的请求属性
				synchronized (Luastate) {
					urlConn.setRequestProperty("Cookie", cookiestat);
					Luastate.getGlobal("utils");
					int index = Luastate.getTop();
					Luastate.getField(index, "webgamehttpdown");
					Luastate.pushJavaObject(urlConn);
					LuaTools.dbcall(Luastate, 1, 0);
				}
				break;
			default:
				break;
			}

			/*
			 * urlConn.addRequestProperty("moby_auth",
			 * PhoneTool.getIEMI(PhoneTool
			 * .getIMEI(MyApplication.getAppContext())));//--参数签名,由moby_imei加密而来
			 * urlConn.addRequestProperty("moby_imei",
			 * PhoneTool.getIMEI(MyApplication
			 * .getAppContext()));//--手机串号,在ios平台下似是mac地址
			 * 
			 * //urlConn.addRequestProperty("moby_auth",
			 * "5d6e9d921277f5e038c2febb248f859a");//--参数签名,由moby_imei加密而来
			 * //urlConn.addRequestProperty("moby_imei",
			 * "12345678901");//--手机串号,在ios平台下似是mac地址
			 * 
			 * urlConn.addRequestProperty("moby_sdk",
			 * "android");//--开发环境,表明用户手机操作系统的环境s60_5th/android/iphone
			 * urlConn.addRequestProperty("moby_op", "op");//--用户运营商,表明用户网络的提供商
			 * urlConn.addRequestProperty("moby_ua",
			 * "ua");//--用户代理,表明用户手机硬件/软件描述
			 * 
			 * MLog.s("GAMEID -----> " +
			 * MyApplication.getAppContext().getGameArgs
			 * ().getCpid()+MyApplication
			 * .getAppContext().getGameArgs().getGameno()); MLog.s("KEY -----> "
			 * + MyApplication.getAppContext().getGameArgs().getKey());
			 * 
			 * //精确到哪一款游戏的数据请求 urlConn.addRequestProperty("moby_gameid",
			 * MyApplication.getAppContext().getGameArgs().getCpid()+
			 * MyApplication
			 * .getAppContext().getGameArgs().getGameno());//--渠道标识,
			 * 表明用户来源(从哪个合作方而来)
			 * 
			 * urlConn.addRequestProperty("moby_bv",
			 * Configs.BV);//--平台版本,表明客户端的平台版本 MLog.s("SVSV -----> " +
			 * FileHeader.version); urlConn.addRequestProperty("moby_sv",
			 * FileHeader.version+"");//--包版本,表用用户上层lua的版本
			 * urlConn.addRequestProperty("moby_pb",
			 * "longyin_sunday_test");//--渠道标识,表明用户来源(从哪个合作方而来)
			 * 
			 * urlConn.addRequestProperty("moby_accid",MyApplication.getAppContext
			 * ().getGameArgs().getAccount_id()); //用户账号id,用户登录返回的用户账号,如果还未登录则为空
			 * urlConn
			 * .addRequestProperty("moby_sessid",MyApplication.getAppContext
			 * ().getGameArgs().getSession_id());//登录会话id,全局唯一,表明用户登录的会话id
			 * 
			 * urlConn.addRequestProperty("cpid",MyApplication.getAppContext().
			 * getGameArgs().getCpid());//商户ID
			 * 
			 * urlConn.setConnectTimeout(30000); urlConn.setReadTimeout(30000);
			 */

		}
		return urlConn;
	}

	/**
	 * 封装请求头信息
	 * 
	 * @return
	 * @throws IOException
	 */
	// public static URLConnection headWebMethod(String urls) {
	// URL url = null;
	// URLConnection urlConn = null;
	// try {
	// url = new URL(urls);
	// if(mProxy!=null){
	// urlConn=(HttpURLConnection)url.openConnection(mProxy);
	// }else{
	// urlConn = (HttpURLConnection)url.openConnection();
	// }
	// } catch (MalformedURLException e1) {
	// url = null;
	// e1.printStackTrace();
	// } catch (IOException e) {
	// urlConn = null;
	// e.printStackTrace();
	// }
	// if (url != null && urlConn != null) {
	// // 找准程序入口
	// LuaState Luastate = MyApplication.getAppContext().getmLuaState();
	// synchronized (Luastate) {
	// Luastate.getGlobal("utils");
	// int index = Luastate.getTop();
	// Luastate.getField(index, "webgamehttphead");
	// Luastate.pushJavaObject(urlConn);
	// LuaTools.dbcall(Luastate, 1, 0);
	// }
	// }
	// return urlConn;
	// }

	/**
	 * 得到最原始的连接，没有任何封装
	 * 
	 * @return
	 * @throws IOException
	 */
	// public static URLConnection headMethodRaw(String urls) {
	// URL url = null;
	// URLConnection urlConn = null;
	// detectProxy();
	// try {
	// url = new URL(urls);
	// if (mProxy != null) {
	// urlConn = (HttpURLConnection) url.openConnection(mProxy);
	// } else {
	// urlConn = (HttpURLConnection) url.openConnection();
	// }
	//
	// } catch (MalformedURLException e1) {
	// url = null;
	// e1.printStackTrace();
	// } catch (IOException e) {
	// urlConn = null;
	// e.printStackTrace();
	// }
	// return urlConn;
	// }

	/*********************************************** GET *****************************************************/
	/**
	 * GET协议请求
	 */
	public static HttpURLConnection getMethod(String urls) {
		HttpURLConnection urlConn = null;
		MLog.s(urls);
		urlConn = (HttpURLConnection) headMethod(urls, 0);
		if (urlConn != null) {
			// 设置以GET方式
			try {
				urlConn.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			// 是否跟随重定向
			urlConn.setInstanceFollowRedirects(true);
		}
		return urlConn;
	}

	/**
	 * GET协议请求
	 */
	public static String getWebMethod(String urls, int flag) {
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection urlConn = null;
		urlConn = (HttpURLConnection) headMethod(urls, flag);

		MLog.s("getWebMethod1-----> " + urlConn);

		if (urlConn != null) {
			// 设置以GET方式
			try {
				urlConn.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			// 是否跟随重定向
			urlConn.setInstanceFollowRedirects(true);
			try {
				if (urlConn.getResponseCode() == 200) {
					MLog.s("getWebMethod2-----> " + urlConn);

					// Get the cookie
					String cookie = urlConn.getHeaderField("set-cookie");

					if (cookie != null && cookie.length() > 0) {
						cookiestat = cookie;
					}

					String temp = null;
					InputStream in = urlConn.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
					while ((temp = br.readLine()) != null) {
						buffer.append(temp);
					}
					br.close();
					in.close();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		MLog.s("getWebMethod3-----> " + urlConn.getContentType() + buffer.toString());
		return urlConn.getContentType() + buffer.toString();
	}
	
	/**
	 * 
	 */

	public static String getWebMethod01(String urls,int flag) {
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection urlConn = null;
		urlConn = (HttpURLConnection) headMethod(urls, flag);

		MLog.s("getWebMethod1-----> " + urlConn);

		if (urlConn != null) {
			// 设置以GET方式
			try {
				urlConn.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			// 是否跟随重定向
			urlConn.setInstanceFollowRedirects(true);
			try {
				if (urlConn.getResponseCode() == 200) {
					MLog.s("getWebMethod2-----> " + urlConn);

					// Get the cookie
					String cookie = urlConn.getHeaderField("set-cookie");

					if (cookie != null && cookie.length() > 0) {
						cookiestat = cookie;
					}

					String temp = null;
					InputStream in = urlConn.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
					while ((temp = br.readLine()) != null) {
						buffer.append(temp);
					}
					br.close();
					in.close();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		MLog.s("getWebMethod3-----> " + urlConn.getContentType() + buffer.toString());
		return buffer.toString();
	}
	
	
	

	/**
	 * GET协议请求模拟下载
	 */
	public static String getDownLoad(String urls, int flag) {
		HttpURLConnection urlConn = null;
		urlConn = (HttpURLConnection) headMethod(urls, flag);
		if (urlConn != null) {
			// 设置以GET方式
			try {
				urlConn.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			// 是否跟随重定向
			urlConn.setInstanceFollowRedirects(true);
			try {
				if (urlConn.getResponseCode() == 200) {
					if (urlConn.getContentLength() >= 10240) {
						return "900";
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * 获取url中文件名
	 * 
	 * @param urls
	 * @return
	 */
	public static String getUrlFileName(String urls) {
		String name = urls;
		if (name.contains("/")) {
			String[] arr = urls.split("/");
			name = arr[arr.length - 1];
		}
		if (name.contains("?")) {
			name = name.split("?")[0];
		}
		return name;
	}

	/**
	 * GET json字符串
	 * 
	 * @param urls
	 * @param params
	 * @param encode
	 * @return
	 */
	public static String getJsonString(String urls, Map<String, String> params, String encode) {
		StringBuffer buffer = new StringBuffer();
		StringBuilder pars = null;
		if (params != null && !params.isEmpty()) {
			pars = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				try {
					pars.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), encode)).append('&');
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			pars.deleteCharAt(pars.length() - 1);
		}
		if (pars != null) {
			urls += pars.toString();
		}
		HttpURLConnection urlConn = getMethod(urls);
		try {

			if (urlConn.getResponseCode() == 200) {
				String temp = null;
				InputStream in = urlConn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
				}
				br.close();
				in.close();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	/**
	 * 下载并验证服务器
	 * 
	 * @param urls
	 * @return
	 */
	public static Object getAPK(String urls) {
		if (urls.contains("|")) {
			String url = urls.split("\\|")[0];
			String md5 = urls.split("\\|")[1];
			File file = getFileServer(url);
			if (file != null) {
				try {
					String t_md5 = MD5Util.getFileMD5String(file);
					MLog.s("down apk md5 ----> " + t_md5);
					MLog.s("server apk md5 ----> " + md5);
					if (md5.equals(t_md5)) {
						return 1;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 从服务器获取文件
	 * 
	 * @param urls
	 * 
	 * @return file
	 */
	public static File getFileServer(String urls) {
		String name = getUrlFileName(urls);
		String gamenumber = MyApplication.getAppContext().getGameArgs().getPrefixx();

		MLog.s("downfile begin");

		detectProxy();

		// 用来获取网络文件长度
		URL urltmp = null;
		HttpURLConnection conn = null;
		try {
			urltmp = new URL(urls);
			if (mProxy != null) {
				conn = (HttpURLConnection) urltmp.openConnection(mProxy);
			} else {
				conn = (HttpURLConnection) urltmp.openConnection();
			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// 用来下载文件
		HttpURLConnection urlConn = getMethod(urls);
		MLog.s("downfile begin------------------------------> " + urls);
		File file = null;
		if (urlConn != null && conn != null) {
			// SD卡存在
			byte[] buffer = new byte[1024];
			int count = -1;

			if (Configs.SDEXIST) {

				// 下载临时文件（补丁或APK）
				if (name.endsWith(".patch") || name.endsWith(".apk")) {
					File dr = new File(Configs.ASDKROOT + "tmp/");
					if (!dr.exists()) {
						dr.mkdirs();
					}
					file = new File(Configs.ASDKROOT + "tmp/" + gamenumber + name);
				} else {// 下载成品文件
					file = new File(Configs.ASDKROOT + gamenumber + name);
				}

				// 删除已存在的文件 ，重新下载
				file.delete();

				try {

					// 咱不走断点续传
					int length = conn.getContentLength();
					length = 0;

					long filelen = file.length();
					RandomAccessFile out = new RandomAccessFile(file, "rw");

					if (length > 0) {
						// 设置当前线程下载的起点，终点 （续传来着）
						// MLog.s(filelen+" ========> " + length);
						// urlConn.setRequestProperty("Range", "bytes=" +
						// filelen + "-" + length);

						if (urlConn.getResponseCode() == 200) {// 服务器返回成功
							InputStream is = urlConn.getInputStream();
							out.seek(filelen);
							// ByteArrayOutputStream byteaout = new
							// ByteArrayOutputStream();
							while ((count = is.read(buffer, 0, 1024)) != -1) {
								// byteaout.write(buffer, 0, count);
								out.write(buffer, 0, count);
							}
							// back = byteaout.toString();
							is.close();
							// byteaout.close();
							out.close();
							MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " done sd1");
						} else {
							MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " failure sd1");
							file = null;
						}
					} else {
						if (urlConn.getResponseCode() == 200) {// 服务器返回成功
							InputStream is = urlConn.getInputStream();
							while ((count = is.read(buffer, 0, 1024)) != -1) {
								out.write(buffer, 0, count);
							}
							is.close();
							out.close();
							MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " done sd2");
						} else {

							MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " failure sd2");
							file = null;
						}
					}

				} catch (ConnectTimeoutException e) {// 连接超时
					e.printStackTrace();
					return null;
				} catch (SocketTimeoutException e) {// 读取超时
					e.printStackTrace();
					return null;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			} else {// SD不存在

				try {
					if (name.endsWith(".apk")) {
						file = new File(Configs.ASDKROOT + gamenumber);
						FilesTool.deleteCraFile(file);
					}

					FileOutputStream out = MyApplication.context.openFileOutput(gamenumber + name, Context.MODE_PRIVATE);

					MLog.s("urlConn.getResponseCode()------------------->" + urlConn.getResponseCode());
					if (urlConn.getResponseCode() == 200) {// 服务器返回成功
						InputStream is = urlConn.getInputStream();
						while ((count = is.read(buffer, 0, 1024)) != -1) {
							out.write(buffer, 0, count);
						}
						is.close();
						out.close();
						MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " done files");
						file = new File(Configs.ASDKROOT + gamenumber + name);
					} else {
						MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " failure files");
						file = null;
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}

			}
			urlConn.disconnect();
			conn.disconnect();
		}
		return file;
	}

	/**
	 * 从服务器获取文件
	 * 
	 * @param urls
	 * 
	 * @return file
	 */
	public static File getFileDataServer(String urls) {
		String name = getUrlFileName(urls);
		String gamenumber = MyApplication.getAppContext().getGameArgs().getPrefixx();

		MLog.a(sTag,"downfile begin");

		detectProxy();

		// 用来获取网络文件长度
		URL urltmp = null;
		HttpURLConnection conn = null;
		try {
			urltmp = new URL(urls);
			if (mProxy != null) {
				conn = (HttpURLConnection) urltmp.openConnection(mProxy);
			} else {
				conn = (HttpURLConnection) urltmp.openConnection();
			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// 用来下载文件
		HttpURLConnection urlConn = getMethod(urls);
		MLog.a(sTag,"downfile begin------------------------------> " + urls);
		File file = null;
		MLog.a(sTag,"urlConn是不是为空------------------------------> " + (urlConn == null));
		if (urlConn != null && conn != null) {
			// SD卡存在
			MLog.a(sTag,"urlConn是不是为空------------------------------> 走这里了1");
			byte[] buffer = new byte[1024];
			int count = -1;

			if (Configs.SDEXIST) {
				MLog.a(sTag,"urlConn是不是为空------------------------------> 走这里了2");
				// 下载临时文件（补丁或APK）
				if (name.endsWith(".patch") || name.endsWith(".apk") || name.endsWith(".zip")) {
					File dr = new File(Configs.ASDKROOT + "tmp/");
					if (!dr.exists()) {
						dr.mkdirs();
					}
					file = new File(Configs.ASDKROOT + "tmp/" + gamenumber + name);
				} else {// 下载成品文件
					file = new File(Configs.ASDKROOT + gamenumber + name);
				}

				// 删除已存在的文件 ，重新下载
				file.delete();

				try {
					MLog.a(sTag,"urlConn是不是为空------------------------------> 走这里了3");
					// 咱不走断点续传
					int length = conn.getContentLength();
					length = 0;

					long filelen = file.length();
					RandomAccessFile out = new RandomAccessFile(file, "rw");

					if (length > 0) {
						// 设置当前线程下载的起点，终点 （续传来着）
						// MLog.s(filelen+" ========> " + length);
						// urlConn.setRequestProperty("Range", "bytes=" +
						// filelen + "-" + length);

						if (urlConn.getResponseCode() == 200) {// 服务器返回成功
							MLog.a(sTag,"urlConn是不是为空------------------------------> 走这里了4");
							InputStream is = urlConn.getInputStream();
							out.seek(filelen);
							// ByteArrayOutputStream byteaout = new
							// ByteArrayOutputStream();
							while ((count = is.read(buffer, 0, 1024)) != -1) {
								// byteaout.write(buffer, 0, count);
								out.write(buffer, 0, count);
							}
							// back = byteaout.toString();
							is.close();
							// byteaout.close();
							out.close();
							MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " done sd1");
						} else {
							MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " failure sd1");
							file = null;
						}
					} else {
						MLog.a(sTag,"urlConn是不是为空------------------------------> 走这里了5");
						if (urlConn.getResponseCode() == 200) {// 服务器返回成功
							InputStream is = urlConn.getInputStream();
							while ((count = is.read(buffer, 0, 1024)) != -1) {
								out.write(buffer, 0, count);
							}
							is.close();
							out.close();
							MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " done sd2");
						} else {

							MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " failure sd2");
							file = null;
						}
					}

				} catch (ConnectTimeoutException e) {// 连接超时
					e.printStackTrace();
					return null;
				} catch (SocketTimeoutException e) {// 读取超时
					e.printStackTrace();
					return null;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			} else {// SD不存在
				MLog.a(sTag,"urlConn是不是为空------------------------------> 走这里了6");
				try {
					if (name.endsWith(".apk")) {
						file = new File(Configs.ASDKROOT + gamenumber);
						FilesTool.deleteCraFile(file);
					}

					FileOutputStream out = MyApplication.context.openFileOutput(gamenumber + name, Context.MODE_PRIVATE);

					MLog.a(sTag,"urlConn.getResponseCode()------------------->" + urlConn.getResponseCode());
					if (urlConn.getResponseCode() == 200) {// 服务器返回成功
						InputStream is = urlConn.getInputStream();
						while ((count = is.read(buffer, 0, 1024)) != -1) {
							out.write(buffer, 0, count);
						}
						is.close();
						out.close();
						MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " done files");
						file = new File(Configs.ASDKROOT + gamenumber + name);
					} else {
						MLog.s("download file： " + Configs.ASDKROOT + gamenumber + name + " failure files");
						file = null;
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}

			}
			urlConn.disconnect();
			conn.disconnect();
		}
		MLog.a(sTag,"urlConn.getResponseCode()------------------->" + file);
		return file;
	}

	/**
	 * 检查代理，是否cnwap接入
	 */
	private static void detectProxy() {
		ConnectivityManager cm = (ConnectivityManager) MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable() && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			MLog.s("proxyHost =========> " + proxyHost);
			MLog.s("proxyPort =========> " + port);
			if (proxyHost != null) {
				final InetSocketAddress sa = new InetSocketAddress(proxyHost, port);
				mProxy = new Proxy(Proxy.Type.HTTP, sa);
				return;
			}
		}
		mProxy = null;
	}

	/**
	 * 从服务器获取普通文件
	 * 
	 * @param urls
	 * @param flag
	 * @return
	 */
	public static File getFileServerOther(String urls, String cachePath) {

		MLog.s("downfile begin");

		detectProxy();

		// ///////////////////////////////////////用来获取网络文件长度\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
		URL urltmp = null;
		HttpURLConnection conn = null;
		try {
			urltmp = new URL(urls);
			if (mProxy != null) {
				conn = (HttpURLConnection) urltmp.openConnection(mProxy);
			} else {
				conn = (HttpURLConnection) urltmp.openConnection();
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// ///////////////////////////////////////用来下载文件\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
		HttpURLConnection urlConn = getMethod(urls);
		File file = null;
		if (urlConn != null && conn != null) {

			byte[] buffer = new byte[1024];
			int count = -1;
			int length = conn.getContentLength();

			// File cacheDir = MyApplication.getAppContext().getCacheDir();
			// final String cachePath = cacheDir.getAbsolutePath() + "/"+ name;

			file = new File(cachePath);
			long filelen = file.length();

			try {

				RandomAccessFile out = new RandomAccessFile(file, "rw");
				if (length > 0) {

					// 设置当前线程下载的起点，终点 （续传来着）
					urlConn.setRequestProperty("Range", "bytes=" + filelen + "-" + length);
					if (urlConn.getResponseCode() == 200) {// 服务器返回成功
						InputStream is = urlConn.getInputStream();
						out.seek(filelen);
						while ((count = is.read(buffer, 0, 1024)) != -1) {
							out.write(buffer, 0, count);
						}
						is.close();
						out.close();
						MLog.s("download file： " + cachePath + " done xu1");
					} else {
						MLog.s("download file： " + cachePath + " failure xu1");
						file = null;
					}

				} else {

					if (urlConn.getResponseCode() == 200) {// 服务器返回成功
						InputStream is = urlConn.getInputStream();
						while ((count = is.read(buffer, 0, 1024)) != -1) {
							out.write(buffer, 0, count);
						}
						is.close();
						out.close();
						MLog.s("download file： " + cachePath + " done no-xu2");
					} else {
						MLog.s("download file： " + cachePath + " failure no-xu2");
						file = null;
					}

				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				file = null;
			} catch (IOException e) {
				e.printStackTrace();
				file = null;
			}
			urlConn.disconnect();
			conn.disconnect();
		}
		return file;
	}

	/**
	 * 执行SV更新
	 * 
	 * @param act
	 *            当前上下文
	 * @param urls
	 *            update urls
	 * 
	 * @return 1 补丁更新成功 2 补丁更新失败 else 文件下载失败
	 */
	public static int patchFiles(MyActivity act, String urls) {
		String gamenumber = MyApplication.getAppContext().getGameArgs().getPrefixx();
		String downurl = null;
		// List<String> downname = new ArrayList<String>();
		// List<String> update = new ArrayList<String>();
		// List<String> delete = new ArrayList<String>();
		int cn = 0;
		MLog.s("Configs.ASDKROOT -----> " + Configs.ASDKROOT);
		// 补丁描述文件
		File despatch = new File(Configs.ASDKROOT + gamenumber + getUrlFileName(urls));

		try {
			FileReader fr = new FileReader(despatch);
			// StringReader sr = new StringReader(str);
			BufferedReader br = new BufferedReader(fr);

			for (String line = br.readLine(); line != null; line = br.readLine(), cn++) {
				MLog.s("br.readLine() -----> " + line);
				if (line.startsWith("http://")) {
					downurl = line;

				} else if (line.startsWith("+")) {// 新加文件
					// downname.add(line.split("\\|")[1]);
					// downname.add(line);

					File file = getFileServer(downurl + line.split("\\|")[1]);

					// Toast.makeText(act,"新增文件: " +
					// file.getName(),Toast.LENGTH_SHORT).show();
					Message ss = new Message();
					ss.what = 1;
					ss.obj = file.getName();
					act.myhand.sendMessage(ss);

					int count = RECONNECTNUM;
					while (count > 0) {

						if (file != null) {
							try {
								String md5 = MD5Util.getFileMD5String(file);

								MLog.s(md5 + "  === addfile download success ===  " + line.split("\\|")[2]);

								if (md5.equals(line.split("\\|")[2])) {
									MLog.s(file.getAbsolutePath() + " MD5 SUCCESS");
									break;
								} else {
									MLog.s(file.getAbsolutePath() + " MD5 FAILURE");
									count--;
									continue;
								}
							} catch (IOException e) {
								e.printStackTrace();
								return 0;
							}
						} else {
							count--;
							continue;
						}
					}
					if (count == 0) {
						// dealWithError(act,FILEDOWNERR);
						return 0;
					}

				} else if (line.startsWith("*")) {// 补丁文件
					// String[] b = line.split("\\|");
					// update.add(line);
					File file = getFileServer(line.split("\\|")[3]);

					// Toast.makeText(act,"修改文件: " +
					// file.getName(),Toast.LENGTH_SHORT).show();
					Message ss = new Message();
					ss.what = 2;
					ss.obj = file.getName();
					act.myhand.sendMessage(ss);

					int count = RECONNECTNUM;
					while (count > 0) {

						if (file != null) {
							try {
								String md5 = MD5Util.getFileMD5String(file);

								MLog.s(md5 + "  === updatefiles download success ===  " + line.split("\\|")[4]);

								if (md5.equals(line.split("\\|")[4])) {
									MLog.s(file.getAbsolutePath() + " MD5 SUCCESS");

								} else {
									MLog.s(file.getAbsolutePath() + " MD5 FAILURE");
									count--;
									continue;
								}
							} catch (IOException e) {
								e.printStackTrace();
								return 0;
							}

							// 补丁与原文件是否匹配（匹配才打补丁，MD5一样还打个毛线补丁啊）
							MLog.s("checkPatchMatch start");
							if (!FilesTool.checkPatchMatch(line.split("\\|")[1])) {
								MLog.s("applyPatch start");
								if (Configs.SDEXIST) {
									MLog.s("applyPatch");
									boolean bo = FilesTool.applyPatch(line.split("\\|")[1], line.split("\\|")[2]);
									if (!bo) {
										return 2;
									}
								} else {
									MLog.s("applyPatchFiles");
									boolean bo = FilesTool.applyPatchFiles(line.split("\\|")[1], line.split("\\|")[2]);
									if (!bo) {
										return 2;
									}
								}
								MLog.s("applyPatch end");
							}
							MLog.s("checkPatchMatch end");

							break;

						} else {
							count--;
							continue;
						}
					}
					if (count == 0) {
						// dealWithError(act,FILEDOWNERR);
						return 0;
					}

				} else if (line.startsWith("-")) {
					// delete.add(line.split("\\|")[1]);
					// delete.add(line);

					File file = new File(Configs.ASDKROOT + gamenumber + line.split("\\|")[1]);

					Message ss = new Message();
					ss.what = 2;
					ss.obj = file.getName();
					act.myhand.sendMessage(ss);
					// Toast.makeText(act,"删除文件: " +
					// file.getName(),Toast.LENGTH_SHORT).show();

					file.delete();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

		// 删除补丁描述文件
		despatch.delete();

		return 1;
	}

	/************************************************** POST ****************************************************/

	/**
	 * POST协议请求
	 */
	public static String postMethod(String urls, Map<String, String> params, String encode) {
		HttpURLConnection urlConn = null;
		MLog.s("POST ARGS: \n" + params);
		StringBuffer buffer = new StringBuffer();
		StringBuilder pars = null;
		if (params != null && !params.isEmpty()) {
			pars = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				try {
					pars.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), encode)).append('&');
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			pars.deleteCharAt(pars.length() - 1);
		}
		try {
			urlConn = (HttpURLConnection) headMethod(urls, 0);
			// 因为这个是post请求,设立需要设置为true
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			// 设置以POST方式
			urlConn.setRequestMethod("POST");

			// Post 请求不能使用缓存
			urlConn.setUseCaches(false);
			// 是否跟随重定向
			urlConn.setInstanceFollowRedirects(true);

			/****************************** 提交配置 **************************************/
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			// urlConn.connect();
			// DataOutputStream流

			if (pars != null && !pars.equals("")) {
				byte[] by = pars.toString().getBytes();
				// byte[] by = AESSecurity.encrypt(pars.toString(),
				// MyApplication.getAppContext().getGameArgs().getKey()).getBytes();
				// 配置数据长度
				urlConn.setRequestProperty("Content-Length", String.valueOf(by.length));
				// 参数输出流
				DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
				// 将要上传的内容写入流中
				out.write(by);
				// 刷新、关闭
				out.flush();
				out.close();
			}

			if (urlConn.getResponseCode() == 200) {
				String temp = null;
				InputStream in = urlConn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
				}

				br.close();
				in.close();
			}
		} catch (ConnectTimeoutException e) {// 连接超时
			e.printStackTrace();
			return null;
		} catch (SocketTimeoutException e) {// 读取超时
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return buffer.toString();
	}

	// 显示网络上的图片
	public static Bitmap getbitmap(String imageUri) {
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/**
	 * 写入文件
	 */
	public static void method1(String conent) {
		File dr = new File(Configs.ASDKROOT + "tmp/");
		if (!dr.exists()) {
			dr.mkdirs();
		}
		File file = new File(dr + File.separator + "响应时间.txt");
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			out.write(conent + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * POST协议请求
	 */
	public static String postMethod(String urls, String params, String encode) {
		HttpURLConnection urlConn = null;
		MLog.a(sTag,"Start  POST------>  " + urls);
		MLog.a(sTag,"POST ARGS: ------>  " + params);
		// 判断是否使用以下3个协议
		boolean isconstant = false;
		
//		urls=urls.replace("xxhd-tech.com", "heart-game.com");
		
		MLog.a(sTag,"POST ARGS: ------>urls---" + urls);
		
		if (urls.contains("update") || urls.contains("getaddr") || urls.contains("submitbug") || urls.contains("replace")) {
			isconstant = true;
		} else {
			isconstant = false;
		}

		StringBuffer buffer = new StringBuffer();
		try {
			urlConn = (HttpURLConnection) headMethod(urls, 0);
			// 因为这个是post请求,设立需要设置为true
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			// 设置以POST方式
			urlConn.setRequestMethod("POST");

			// Post 请求不能使用缓存
			urlConn.setUseCaches(false);
			// 是否跟随重定向
			urlConn.setInstanceFollowRedirects(true);

			/****************************** 提交配置 **************************************/
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			// urlConn.connect();
			// DataOutputStream流
			if (params != null && !params.equals("")) {
				if (isconstant) {
					params = AESSecurity.constantEncryptionResult(params, KEY);
				} else {
					params = AESSecurity.encryptionResult(params);
				}
				byte[] by = params.getBytes();
				// byte[] by = AESSecurity.encrypt(params,
				// MyApplication.getAppContext().getGameArgs().getKey()).getBytes();

				// 配置数据长度
				urlConn.setRequestProperty("Content-Length", String.valueOf(by.length));
				// 参数输出流
				DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
				// 将要上传的内容写入流中
				out.write(by);
				// 刷新、关闭
				out.flush();
				out.close();
			}

			int result = urlConn.getResponseCode();
			
			if (result == 200) {
				String temp = null;
				InputStream in = urlConn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
				}
				br.close();
				in.close();
			} else {
				return "exception" + result;
			}
		} catch (ConnectTimeoutException e) {// 连接超时
			e.printStackTrace();
			return "exception ConnectTimeout";
		} catch (SocketTimeoutException e) {// 读取超时
			e.printStackTrace();
			return "exception SocketTimeout";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "exception MalformedURL";
		} catch (IOException e) {
			e.printStackTrace();
			return "exception IO";
		} catch (Exception e) {
			e.printStackTrace();
			return "exception";
		}
		MLog.s("End Http Request");
		if (buffer.toString() == null || buffer.toString().contains("error") || buffer.toString().contains("exception") || buffer.toString().contains("Fatal")) {
			MLog.a(sTag,buffer.toString());
			return "exception Net";
		}
		if (isconstant) {
			return AESSecurity.constantdecryptResult(buffer.toString(), KEY);
		}
		return AESSecurity.decryptResult(buffer.toString());
	}

	/**
	 * POST协议请求
	 */
	public static String postWebMethod(String urls, int flag) {
		MLog.s("post请求 5");
		HttpURLConnection urlConn = null;
		StringBuffer buffer = new StringBuffer();
		try {
			urlConn = (HttpURLConnection) headMethod(urls, flag);
			// 因为这个是post请求,设立需要设置为true
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			// 设置以POST方式
			urlConn.setRequestMethod("POST");

			// Post 请求不能使用缓存
			urlConn.setUseCaches(false);
			// 是否跟随重定向
			urlConn.setInstanceFollowRedirects(true);

			/****************************** 提交配置 **************************************/
			urlConn.setRequestProperty("Content-Type", "text/plain");

			byte[] by = "900 Success".getBytes();
			// 配置数据长度
			// urlConn.setRequestProperty("Content-Length",String.valueOf(by.length));
			// 参数输出流
			DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
			// 将要上传的内容写入流中
			out.write(by);
			// 刷新、关闭
			out.flush();
			out.close();

			// 已主动发起response了
			// MLog.s("post请求 52----> "+urlConn.getContentType());

			int result = urlConn.getResponseCode();
			if (result == 200) {
				String temp = null;
				InputStream in = urlConn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
				}
				br.close();
				in.close();
			} else {
				return "error" + result;
			}
		} catch (ConnectTimeoutException e) {// 连接超时
			e.printStackTrace();
			return "exception ConnectTimeout";
		} catch (SocketTimeoutException e) {// 读取超时
			e.printStackTrace();
			return "exception SocketTimeout";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "exception MalformedURL";
		} catch (IOException e) {
			e.printStackTrace();
			return "exception IO";
		}
		MLog.s("postresult--->" + buffer.toString());
		if (buffer.toString() == null || buffer.toString() == "") {
			return buffer.toString();
		}
		return AESSecurity.decryptResult(buffer.toString());
	}

	
	
	static RequestQueue requestQueue = null;
	/**
	 * http对外发送接口
	 * 
	 * @param urls
	 * @param params
	 * @param encode
	 */
	public static void startPost(final String urls, final String params, String encode) {
		MLog.s("Start Http Request");
//		String publisher = FilesTool.getPublisherStringContent();
		if(true){
			// 判断是否使用以下3个协议
			boolean isconstant = false;
			if (urls.contains("update") || urls.contains("getaddr") || urls.contains("submitbug")) {
				isconstant = true;
			} else {
				isconstant = false;
			}
			String params_ =null;
			if (params != null && !params.equals("")) {
				if (isconstant) {
					params_ = AESSecurity.constantEncryptionResult(params, KEY);
				} else {
					params_ = AESSecurity.encryptionResult(params);
				}
			}
			
			requestQueue = (requestQueue ==null)? Volley.newRequestQueue(MyApplication.context):requestQueue;
			JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(isconstant,Method.POST, urls, params_, new Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					callback(urls,response.toString(),params);
				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
//					final String s = error.getMessage();
//					
//					if(OutFace.getInstance(null).getmActivity()!=null){
//		            	
//		            	OutFace.getInstance(null).getmActivity().runOnUiThread(new Runnable() {
//		            		
//		            		@Override
//		            		public void run() {
//		            			
//		            			AlertDialog.Builder builder =new Builder(OutFace.getInstance(null).getmActivity());
//		            			builder.setTitle("提示");  
//		            			builder.setMessage(s);
//		            			builder.setPositiveButton("确定",
//		            					new android.content.DialogInterface.OnClickListener() {
//		            				public void onClick(DialogInterface dialog, int which) {
//		            					dialog.dismiss();
//		            				}
//		            			});
//		            			builder.create().show();
//		            		}
//		            	});
//		            }
					callback(urls,"exception",params);
				}
			});
			requestQueue.add(jsonRequest);
		}else{
			HttpAsynTask async = new HttpAsynTask();
			async.execute(urls, params, encode);
		}
	}
	
	private static void callback(String urls, String response, String params) {
		String flag = null;
		if (urls.contains("=")) {
			int sh = urls.lastIndexOf("=");
			flag = urls.substring(sh + 1, urls.length());
		} else {
			flag = "httptest";
		}
		synchronized (MyApplication.getAppContext().getmLuaState()) {
			MyApplication.getAppContext().httpback.httpcallback(flag, response, params);
		}
		
		 
	}

	/**
	 * kjava对外发送接口
	 * 
	 * @param urls
	 * @param params
	 * @param encode
	 */
	public static void startKjava(String urls, String step, String encode) {
		MLog.s("Start Kjava Request");
		KjavaAsynTask async = new KjavaAsynTask();
		async.execute(urls, step, encode);
	}

	/**
	 * http对外发送接口
	 * 
	 * @param urls
	 * @param params
	 * @param encode
	 */
	public static void startWebPost(final String urls) {
		MLog.s("Start-----Net-----startWebPost");

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				LuaState Luastate = MyApplication.getAppContext().getmLuaState();
				synchronized (Luastate) {
					Luastate.getGlobal("utils");
					int index = Luastate.getTop();
					Luastate.getField(index, "getphonenumNetWap");
					Luastate.pushString(urls);
					Luastate.pushInteger(1);
					LuaTools.dbcall(Luastate, 2, 3);
				}

				/*
				 * String result = null; //获取下载游戏下载地址 int flag = 1; result =
				 * getWebMethod(urls, 1); result = subStr(result, flag);
				 * MLog.s("result---" + flag + result);
				 * 
				 * 
				 * //得到JAD 根据（ontime） flag = 2; result = result.replace("amp;",
				 * ""); result = getWebMethod("http://g.10086.cn" + result, 1);
				 * MLog.s("result-==" + result);
				 * 
				 * 
				 * //得到JAD result = subStr(result, flag); result =
				 * result.replace("amp;", ""); result = getWebMethod(result, 1);
				 * MLog.s("*********result---" + flag + result);
				 */

				// MIDlet-Jar-URL地址
				/*
				 * flag = 3; MLog.s("result---" + flag +
				 * getWebMethod(subStr(result, flag), 1));
				 * 
				 * //访问MIDlet-Install-Notify地址 flag = 4; result=subStr(result,
				 * flag); result = postWebMethod(result, 1); MLog.s("JAD访问结束" +
				 * result); // String mobile= //访问网游登录页面得到梦网认证的userId等参数
				 * 
				 * 
				 * flag=5; result=subStr("", 5); result =
				 * getWebMethod(result,1); MLog.s("---result---"+flag+result);
				 * 
				 * //服务器返回结果 flag=6; result=subStr(result, 6);
				 * MLog.s("---result---"+flag+result);
				 * 
				 * //扣钱接口 flag=7; // result = getWebMethod(subStr(result, 7),1);
				 * // result=getWebMethod(
				 * "http://115.238.129.135:9000/buy.ashx?userId=600$49479&key=912$ab441fef105feadd74097f955e$2d6&uid=200000&toolIdx=000$065619$004&mobile=13800138000"
				 * , // 1); MLog.s("扣费" + result);
				 */
			}
		});
		thread.start();
	}

	/**
	 * 充值SMS,KJAVA走起
	 */
	public static void charge_Sms_Kjava() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LuaState Luastate = MyApplication.getAppContext().getmLuaState();
					synchronized (Luastate) {
						Luastate.getGlobal("net");
						int index = Luastate.getTop();
						Luastate.getField(index, "charge_sms_kjava");
						LuaTools.dbcall(Luastate, 0, 0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*
				 * Runnable th = new Runnable() {
				 * 
				 * @Override public void run() { try { LuaState Luastate =
				 * MyApplication.getAppContext().getmLuaState(); synchronized
				 * (Luastate) { Luastate.getGlobal("net"); int index =
				 * Luastate.getTop(); Luastate.getField(index,
				 * "charge_sms_kjava"); LuaTools.dbcall(Luastate, 0, 0); }
				 * 
				 * } catch (Exception e) { MLog.err("不知道可不可以");
				 * e.printStackTrace(); } } };
				 */

				/*
				 * Callable<Integer> call = new Callable<Integer>(){
				 * 
				 * @Override public Integer call() throws Exception { try {
				 * LuaState Luastate =
				 * MyApplication.getAppContext().getmLuaState(); synchronized
				 * (Luastate) { Luastate.getGlobal("net"); int index =
				 * Luastate.getTop(); Luastate.getField(index,
				 * "charge_sms_kjava"); LuaTools.dbcall(Luastate, 0, 0); }
				 * 
				 * } catch (Exception e) { MLog.err("不知道可不可以");
				 * e.printStackTrace(); } return 0; }
				 * 
				 * };
				 * 
				 * long timeOut = 20000;// 任务必须在设定时间内完成，否则任务将被强制关闭 TimeUnit
				 * timeUnit = TimeUnit.MILLISECONDS;// 时间单位 ExecutorService
				 * executor = Executors.newSingleThreadExecutor();// 高级并发API
				 * Future<Integer> future = executor.submit(call); try {
				 * future.get(timeOut, timeUnit); } catch (InterruptedException
				 * e) { MLog.err("线程中断出错。"); e.printStackTrace();
				 * future.cancel(true); } catch (ExecutionException e) {
				 * MLog.err("线程服务出错。"); e.printStackTrace();
				 * future.cancel(true); } catch (TimeoutException e) {
				 * MLog.err("超时。"); e.printStackTrace(); future.cancel(true); }
				 * finally{ MLog.err("线程服务关闭。"); executor.shutdown(); } }
				 */
			}
		});

		// 设置成后台线程
		// thread.setDaemon(true);
		thread.start();

	}

	/**
	 * http对外发送接口
	 * 
	 * @param urls
	 * @param params
	 * @param encode
	 */
	public static void startNetCharges() {
		MLog.s("Start-----Net-----Game-----startNetCharges");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				LuaState Luastate = MyApplication.getAppContext().getmLuaState();
				synchronized (Luastate) {
					Luastate.getGlobal("utils");
					int index = Luastate.getTop();
					Luastate.getField(index, "netcharge");
					Luastate.pushString("");
					Luastate.pushInteger(1);
					Luastate.pushString("");
					LuaTools.dbcall(Luastate, 3, 0);
				}
			}
		});
		thread.start();
	}

	/**
	 * 
	 * @param str
	 * @param flag
	 * @return
	 */
	/*
	 * public static String subStr(String str, int flag) { String subResult;
	 * LuaState Luastate = MyApplication.getAppContext().getmLuaState();
	 * synchronized (Luastate) { Luastate.getGlobal("utils"); int index =
	 * Luastate.getTop(); Luastate.getField(index, "subStr");
	 * Luastate.pushString(str); Luastate.pushInteger(flag);
	 * LuaTools.dbcall(Luastate, 2, 1); subResult = Luastate.toString(-1); }
	 * return subResult; }
	 */

	/**************************************************** 工具方法 ******************************************************/
	/**
	 * APK文件检测处理（版本不一样删除）
	 * 
	 * @param patchname
	 */
	public static void checkAPKFile(String url) {
		String name = getUrlFileName(url);
		File dir = null;
		if (Configs.SDEXIST) {
			dir = new File(Configs.ASDKROOT + "tmp/");
		} else {
			dir = new File(Configs.ASDKROOT);
		}
		File[] files = dir.listFiles();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				// checkAPKFile(name);
			} else {
				if (files[i].getName().endsWith(".apk")) {
					if (!files[i].getName().equals(name)) {
						files[i].delete();
					}
				}
			}
		}
	}

	/**
	 * 错误处理函数（这个函数在文件处理线程里面）
	 * 
	 * @param error
	 */
	public static void dealWithError(MyActivity handleractivity, int error) {
		switch (error) {
		case FILEDOWNERR:
			Message ms = new Message();
			ms.what = FILEDOWNERR;
			handleractivity.getMyhand().sendMessage(ms);
			break;

		default:
			break;
		}
	}

	/**
	 * 通过KJAVA获得手机号码
	 */
	// public static void getPhoneNumKjava() {
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// String str =
	// "http://g.10086.cn/gamecms/wap/game/wyinfo/700144309000?channelId=12068000";
	// HttpURLConnection urlConn = (HttpURLConnection) headMethodRaw(str);
	// urlConn.setInstanceFollowRedirects(true);
	// urlConn.setRequestProperty("Cookie", null);
	// urlConn.setRequestProperty("Host", "download.cmgame.com");
	// urlConn.setRequestProperty("Connection", "keep-alive");
	// urlConn.setRequestProperty(
	// "User-Agent",
	// "Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 Nokia5230/10.0.021; Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413");
	// urlConn.setRequestProperty("Accept", "*/*");
	//
	// StringBuffer buffer = new StringBuffer();
	// try {
	// int result = urlConn.getResponseCode();
	// if (result == 200) {
	// String temp = null;
	// InputStream in = urlConn.getInputStream();
	// BufferedReader br = new BufferedReader(
	// new InputStreamReader(in, "utf-8"));
	// while ((temp = br.readLine()) != null) {
	// buffer.append(temp);
	// }
	//
	// // Get the cookie
	// String cookie = urlConn.getHeaderField("set-cookie");
	// if (cookie != null && cookie.length() > 0) {
	// urlConn.setRequestProperty("Cookie", cookie);
	// MLog.s("Cookie -------> " + cookie);
	// }
	//
	// br.close();
	// in.close();
	// } else {
	// return;
	// }
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }
	// }).start();
	// }
	
	public static int getAppCode(){
		try {
			PackageInfo info = MyApplication.context.getPackageManager().getPackageInfo(MyApplication.context.getPackageName(), 0);
			return info.versionCode; // 版本号
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
		
	}
}
