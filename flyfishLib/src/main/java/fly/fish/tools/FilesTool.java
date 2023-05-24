package fly.fish.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import fly.fish.aidl.CallBackListener;
import fly.fish.asdk.MyApplication;
import fly.fish.beans.FileHeader;
import fly.fish.config.Configs;

public class FilesTool {

	/**
	 * bytes转int(小端)
	 * 
	 * @param buf
	 * @return
	 */
	public static int bytesToIntLittle(byte[] buf) {
		if (buf.length < 4) {
			return -1;
		}
		int yes = 0;
		for (int i = 0; i < 4; i++) {
			int n = (buf[i] < 0 ? buf[i] + 256 : buf[i]) << (8 * i);
			yes += n;
		}
		return yes;
	}

	/**
	 * bytes转int(大端)
	 * 
	 * @param buf
	 * @return
	 */
	public static int bytesToIntBig(byte[] buf) {
		if (buf.length < 4) {
			return -1;
		}
		int yes = 0;
		for (int i = 0; i < 4; i++) {
			int n = (buf[i] < 0 ? buf[i] + 256 : buf[i]) << (8 * (3 - i));
			yes += n;
		}
		return yes;
	}

	/**
	 * 删除文件cra
	 * 
	 * @param file
	 *            要删除cra和apk的根目录
	 */
	public static void deleteCraFile(File file) {
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				return;
			}
			for (File f : childFile) {
				if (f.getName().endsWith(".cra")) {
					f.delete();
				}
			}
		}
	}

	/**
	 * 递归删除文件和文件夹
	 * 
	 * @param file
	 *            要删除的根目录
	 */
	public static void RecursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {

				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}

	/**
	 * 拷贝asset文件到files
	 * 
	 * @param context
	 * @param assetName
	 * @param fileName
	 *            "/data/data/"+getPackageName()+"/files"
	 */
	public static void copyAssetsToFiles(String assetName, String fileName) {
		byte[] buf = new byte[1024];
		int byteread = 0;
		try {
			InputStream ins = MyApplication.context.getResources().getAssets()
					.open(assetName);
			FileOutputStream out = MyApplication.context.openFileOutput(
					fileName, Context.MODE_PRIVATE);
			while ((byteread = ins.read(buf)) != -1) {
				out.write(buf, 0, byteread);
			}
			out.close();
			ins.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		buf = null;
	}

	/**
	 * 拷贝asset文件到files,加上子渠道号
	 * 
	 * @param assetName
	 * @param fileName
	 * @param channel
	 */
	public static void copyAssetsPubToFiles(String assetName, String fileName,
			String channel) {
		try {
			InputStream ins = MyApplication.context.getResources().getAssets()
					.open(assetName);
			FileOutputStream out = MyApplication.context.openFileOutput(
					fileName, Context.MODE_PRIVATE);
			String child_channel = channel == null ? "" : ("_" + channel);
			out.write((new BufferedReader(new InputStreamReader(ins))
					.readLine().trim() + child_channel).getBytes());
			out.close();
			ins.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拷贝assets文件下txt到 "/data/data/"+getPackageName()+"/files"拷贝
	 * 
	 * @param context
	 * @param assetName
	 * @param fileName
	 */
	public static void copyAssetsTxtToSDFiles(String assetName, String fileName) {
		byte[] buf = new byte[1024];
		int byteread = 0;
		try {
			InputStream ins = MyApplication.context.getResources().getAssets()
					.open(assetName);
			FileOutputStream out = new FileOutputStream("/data/data/"
					+ MyApplication.context.getApplicationContext()
							.getPackageName() + "/files/" + fileName);
			while ((byteread = ins.read(buf)) != -1) {
				out.write(buf, 0, byteread);
			}
			out.close();
			ins.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		buf = null;

	}

	/**
	 * 拷贝assets下cra文件到SD卡,这种方法只能用于SD卡目录拷贝
	 * 不能用于"/data/data/"+getPackageName()+"/files"拷贝
	 * 
	 * @param context
	 * @param assetName
	 * @param fileName
	 */
	public static void copyAssetsToSDFiles(String assetName, String fileName) {
		byte[] buf = new byte[1024];
		int byteread = 0;
		try {
			InputStream ins = MyApplication.context.getResources().getAssets()
					.open(assetName);
			FileOutputStream out = new FileOutputStream(Configs.ASDKROOT
					+ fileName);
			while ((byteread = ins.read(buf)) != -1) {
				out.write(buf, 0, byteread);
			}
			out.close();
			ins.close();
		} catch (FileNotFoundException e) {
			Log.e("copyAssetsToSDFiles", "文件还不存在呢");
		} catch (IOException e) {
			e.printStackTrace();
		}
		buf = null;
	}

	/**
	 * 获取制定文件里的内容
	 * 
	 * @return content
	 */
	private static String channel = null;
	private static String pub = getPublisherString()[0];

	public static String getPublisherStringContent() {
		return pub == null ? "" : pub;
		// if(channel==null){
		// channel = getChannelInfo(MyApplication.context);
		// }
		// BufferedReader br = null;
		// String content = "";
		// try {
		// InputStream ins =
		// MyApplication.context.getResources().getAssets().open("AsdkPublisher.txt");
		// br = new BufferedReader(new InputStreamReader(ins));
		// content = br.readLine()+(channel == null ? "" : ("_" + channel));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// return content;

	}

	private final static String FILE_NAME = "META-INF/channel_";

	public static String getChannelInfo(Context context) {
		String result = null;
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		// 以zip文件打开
		Enumeration<?> entries = null;
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(sourceDir);
			entries = zipfile.entries();
			ZipEntry entry;
			while (entries.hasMoreElements()) {
				entry = ((ZipEntry) entries.nextElement());
				if (entry.getName().startsWith(FILE_NAME)) {
					String targetFilename = entry.getName();
					String[] splitArray = targetFilename.split("_"); // aaa,
																		// bbb中不会包含"_",
																		// [channel_aaa_bbb]
					result = splitArray[splitArray.length - 1];
					splitArray = null;
					break;
				}
			}
		} catch (IOException e) {
			result = null;
			e.printStackTrace();
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	/**
	 * 获取assets下某一文件里的内容
	 * 
	 * @return content
	 */

	public static String getPublisherStringContent(String filenane) {
		BufferedReader br = null;
		String content = "";
		try {
			InputStream ins = MyApplication.context.getResources().getAssets()
					.open(filenane);
			br = new BufferedReader(new InputStreamReader(ins));
			content = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;

	}

	/**
	 * 得到SDK标识,表明用户来源(从哪个合作方而来)
	 * 
	 * @return Publisher
	 */

	public static String getPublisherStringSDK() {
		String Publisher = "";
		// String[] string = new String[10];
		// try {
		// File file = new File("/data/data/" +
		// MyApplication.context.getPackageName() + "/files/" +
		// "AsdkPublisher.txt");
		// MLog.a(file.getAbsolutePath().toString());
		// if (!file.exists()) {
		// FilesTool.copyAssetsToFiles("AsdkPublisher.txt",
		// "AsdkPublisher.txt");
		// }
		// FileInputStream fis =
		// MyApplication.context.openFileInput("AsdkPublisher.txt");
		// BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		// Publisher = br.readLine().trim();
		// string = Publisher.split("\\_");
		// Publisher = string[0];
		//
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// Log.e("getPublisherString", "文件未找到");
		// }
		return Publisher;
	}

	/**
	 * 得到包名后缀
	 * 
	 * 
	 */

	public static String getPackageNameEndWith() {
		String Packagename = "";
		String[] string = new String[10];
		Packagename = MyApplication.getAppContext().getPackageName();
		string = Packagename.split("\\.");

		return string[string.length - 1];
	}

	/**
	 * 得到渠道标识,表明用户来源(从哪个合作方而来)
	 * 
	 * @return Publisher
	 */

	public static String[] getPublisherString() {
		MLog.a("FilesTool", "MyApplication.context---------------->"
				+ MyApplication.context);
		String pub = "";
		try {
			InputStream ins = MyApplication.context.getResources().getAssets()
					.open("AsdkPublisher.txt");
			pub = new BufferedReader(new InputStreamReader(ins)).readLine()
					.trim();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (channel == null) {
			if (!pub.startsWith("lbyx")) {
				channel = getChannelInfo(MyApplication.context);
			}
		}
		String Publisher = "";
		String[] string = new String[10];
		File file = new File("/data/data/"
				+ MyApplication.context.getPackageName() + "/files/"
				+ "AsdkPublisher.txt");
		MLog.a("FilesTool", file.getAbsolutePath().toString());
		if (!file.exists() || pub.startsWith("am") || pub.startsWith("kugou")
				|| pub.startsWith("kaopu") || pub.startsWith("oppo")
				|| pub.startsWith("kupad") || pub.startsWith("huawei")
				|| pub.startsWith("qq") || pub.startsWith("baidu")
				|| pub.startsWith("qihoo") || pub.startsWith("yyh")
				|| pub.startsWith("vivo") || pub.startsWith("tt")
				|| pub.startsWith("pyw") || pub.startsWith("wandou")
				|| pub.startsWith("anzhi") || pub.startsWith("youlong")
				|| pub.startsWith("pptv") || pub.startsWith("cmqq")) {
			FilesTool.copyAssetsPubToFiles("AsdkPublisher.txt",
					"AsdkPublisher.txt", channel);
		}
		try {
			FileInputStream fis = MyApplication.context
					.openFileInput("AsdkPublisher.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			Publisher = br.readLine().trim();
		} catch (Exception e) {
			e.printStackTrace();
			String child_channel = channel == null ? "" : ("_" + channel);
			Publisher = pub + child_channel;
		}
		string = Publisher.split("\\|");
		// 口袋 酷狗、靠谱更新，修改渠道号
		// string[0] = "kugou2sdk_kdygfk_001";//kugou2sdk_kdygfk_001
		if (string[0].startsWith("mmsdk")) {
			String[] abc = string[0].split("_");
			abc[2] = readXML("/mmiap.xml");
			string[0] = abc[0] + "_" + abc[1] + "_" + abc[2]
					+ (channel == null ? "" : ("_" + channel));
		}
		return string;
	}

	/**
	 * 拷贝文件到SD卡
	 * 
	 * @param context
	 * @param assetName
	 * @param fileName
	 */
	public static void copyFilesToSD(String files, String sdfile) {
		byte[] buf = new byte[1024];
		int byteread = 0;
		try {
			InputStream ins = MyApplication.context.openFileInput(files);
			FileOutputStream out = new FileOutputStream(Configs.ASDKROOT
					+ sdfile);
			while ((byteread = ins.read(buf)) != -1) {
				out.write(buf, 0, byteread);
			}
			out.close();
			ins.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		buf = null;
	}

	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean ExistSDCard() {
		// SD卡存在
//		if (android.os.Environment.getExternalStorageState().equals(
//				android.os.Environment.MEDIA_MOUNTED)) {
//			File path = Environment.getExternalStorageDirectory();
//			Configs.SDEXIST = true;
//			String name = null;
//			String pub = FilesTool.getPublisherStringContent();
//			if (pub.startsWith("asdk") && pub.endsWith("2000")) {
//				name = "guobi";
//			} else {
//				name = (pub.split("_"))[1];
//			}
//			File file = new File(path.getAbsolutePath() + File.separator
//					+ Configs.ASDK + File.separator);
//			if (!file.exists()) {
//				MLog.a("FilesTool", "创建flyfish_asdk目录------------begin");
//				file.mkdir();
//				MLog.a("FilesTool", "创建flyfish_asdk目录------------end");
//			} else {
//				MLog.a("FilesTool", "flyfish_asdk目录已存在");
//			}
//			File file2 = new File(path.getAbsolutePath() + File.separator
//					+ Configs.ASDK + File.separator + name + File.separator);
//			if (!file2.exists()) {
//				MLog.a("FilesTool", "创建" + Configs.ASDK + File.separator + name
//						+ "目录------------begin");
//				file2.mkdir();
//				MLog.a("FilesTool", "创建" + Configs.ASDK + File.separator + name
//						+ "目录------------end");
//			} else {
//				MLog.a("FilesTool", Configs.ASDK + File.separator + name
//						+ "目录已存在");
//			}
//			Configs.ASDKROOT = file2.getAbsolutePath() + File.separator;
//
//			return true;
//		} else { // SD卡不存在
			Configs.ASDKROOT = File.separator
					+ "data"
					+ File.separator
					+ "data"
					+ File.separator
					+ MyApplication.context.getApplicationContext()
							.getPackageName() + File.separator + "files"
					+ File.separator;
			return false;
		//}
	}

	/**
	 * 获取网址
	 * 
	 * @return
	 */
	public static String getDBContent(String key) {
		Dao dao = MyApplication.getAppContext().getDb();
		String string = dao.getInfos().get(key);
		return string;
	}

	/**
	 * 获取SD卡剩余大小
	 * 
	 * @return
	 */
	public static long getSDFreeSize() {
		// 取得SD卡文件路径
		// 返回SD卡空闲大小
				// return freeBlocks * blockSize; //单位Byte
				// return (freeBlocks * blockSize)/1024; //单位KB
		long size = 0;
		try {
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// 获取单个数据块的大小(Byte)
			long blockSize = sf.getBlockSize();
			// 空闲的数据块的数量
			long freeBlocks = sf.getAvailableBlocks();
			size = (freeBlocks * blockSize) / 1024 / 1024;
			Configs.SDSIZE = size;
			return size; 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size; // 单位MB
	}

	/**
	 * 返回asset文件流
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static InputStream getFileStream(Context context, String fileName,
			int flag) {
		try {
			if (flag == 1) {
				return context.getResources().getAssets().open(fileName);
			} else if (flag == 2) {// 可以读SD卡和内存
				return new FileInputStream(Configs.ASDKROOT + fileName);
			} else if (flag == 3) {// 读内存
				return context.openFileInput(fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			return context.getResources().getAssets().open(fileName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	/**
	 * 文件流转字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String readStream(InputStream is) {
		byte[] buf = new byte[1024];
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int ch = 0;
			while ((ch = is.read(buf, 0, 1024)) != -1) {
				bo.write(buf, 0, ch);
			}
			return bo.toString();
		} catch (IOException e) {
			Log.e("ReadStream", "读取文件流失败");
			return null;
		}
	}

	/**
	 * 解析数据头
	 */
	public static void loadDatFiles(int flag) {
		if (Configs.USEXPKG || Configs.ALLUSED) {
			if (MyApplication.getAppContext().logicmain.headerlist == null) {
				MyApplication.getAppContext().logicmain.headerlist = new ArrayList<FileHeader>();
			}
			List<FileHeader> filelist = MyApplication.getAppContext().logicmain.headerlist;

			String gamenumber = MyApplication.getAppContext().getGameArgs()
					.getPrefixx();
			InputStream is = getFileStream(MyApplication.context, gamenumber
					+ FileHeader.MYPKG, flag);
			byte[] buf = new byte[1024];
			int count = 0;
			int namelen = 0;
			try {
				count += is.read(buf, 0, 4);
				FileHeader.flag = new String(buf, 0, count);
				count += is.read(buf, 0, 4);
				int version = FilesTool.bytesToIntBig(buf);

				if (version == FileHeader.version) {
					return;
				} else {
					filelist.clear();
					FileHeader.version = version;
				}

				count += is.read(buf, 0, 4);
				FileHeader.fileCount = FilesTool.bytesToIntBig(buf);
				FileHeader header = null;
				for (int i = 0; i < FileHeader.fileCount; i++) {
					header = new FileHeader();
					count += is.read(buf, 0, 4);
					namelen = FilesTool.bytesToIntBig(buf);
					count += is.read(buf, 0, namelen);
					header.name = new String(buf, 0, namelen);
					count += is.read(buf, 0, 4);
					header.startoff = FilesTool.bytesToIntBig(buf);
					count += is.read(buf, 0, 4);
					header.size = FilesTool.bytesToIntBig(buf);
					count += is.read(buf, 0, 4);
					header.realsize = FilesTool.bytesToIntBig(buf);
					count += is.read(buf, 0, 16);
					header.md5 = MD5Util.bufferToHex(buf, 0, 16);
					filelist.add(header);
				}
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			buf = null;

			// 删除以new结尾的文件
			try {
				for (int i = 0; i < filelist.size(); i++) {
					File newf = new File(Configs.ASDKROOT + gamenumber
							+ filelist.get(i).name + "new");
					if (newf.exists()) {
						newf.delete();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 加载执行luac文件
	 * 
	 * @param filelist
	 * @param name
	 */
	public static boolean loadLuaScript(String name) {
		if (!MyApplication.getAppContext().lualist.contains(name)) {
			MyApplication.getAppContext().lualist.add(name);
			if (Configs.USEXPKG) {
				if (name.contains("/")) {
					String[] arr = name.split("/");
					name = arr[arr.length - 1];
				}
				byte[] bys = decompressBytes(name);
				if (bys != null) {
					// 加载
					MyApplication.getAppContext().getmLuaState()
							.LdoBuffer(bys, name);
					// 执行
					// MyApplication.getAppContext().getmLuaState().pcall(0,
					// LuaState.LUA_MULTRET,0);
					return true;
				} else {
					return false;
				}
			} else {
				MLog.s("start loadLuaScript -----> " + name);
				MyApplication
						.getAppContext()
						.getmLuaState()
						.LdoBuffer(
								FilesTool
										.readStream(
												FilesTool.getFileStream(
														MyApplication.context,
														name, 1)).getBytes(),
								name);
				return true;
			}
		}
		return false;
	}

	/**
	 * 解出指定文件的bytes数组（解压ZIP数组）
	 * 
	 * @param filelist
	 * @param name
	 * @return
	 */
	public static byte[] decompressBytes(String name) {

		// 如果为空，表示这里没有头信息，是不能解析出lua的
		if (MyApplication.getAppContext().logicmain.headerlist == null) {
			MyApplication.getAppContext().logicmain.headerlist = new ArrayList<FileHeader>();
			return null;
		}

		List<FileHeader> filelist = MyApplication.getAppContext().logicmain.headerlist;
		String gamenumber = MyApplication.getAppContext().getGameArgs()
				.getPrefixx();
		int startoff = 0;
		int size = 0;
		int realsize = 0;
		for (int i = 0; i < filelist.size(); i++) {
			if (filelist.get(i).name.equals(name)) {
				startoff = filelist.get(i).startoff;
				size = filelist.get(i).size;
				realsize = filelist.get(i).realsize;
				break;
			}
		}

		if (startoff != 0 && size != 0) {
			byte[] buf = new byte[size];
			byte[] bufmid = new byte[1024];
			byte[] backbytes = null;

			InputStream is = getFileStream(MyApplication.context, gamenumber
					+ FileHeader.MYPKG, 2);
			ByteArrayOutputStream byteaout = new ByteArrayOutputStream(realsize);
			try {
				is.skip(startoff);
				is.read(buf, 0, size);

				// 解压缩文件
				ByteArrayInputStream bat = new ByteArrayInputStream(buf);
				// 解压缩文件
				InputStream in = new InflaterInputStream(bat);
				int ch = 0;
				while ((ch = in.read(bufmid, 0, 1024)) != -1) {
					byteaout.write(bufmid, 0, ch);
				}

				is.close();
				in.close();
				bat.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			backbytes = byteaout.toByteArray();
			try {
				buf = null;
				bufmid = null;
				byteaout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// MLog.s("backbytes =======>" + backbytes + "");
			return backbytes;
		}
		return null;
	}

	/**
	 * 释放图片
	 * 
	 * @param map
	 */
	public static void releaseBitmap(Bitmap... bmp) {
		if (bmp != null) {
			for (int i = 0; i < bmp.length; i++) {
				if (bmp[i] != null) {
					if (!bmp[i].isRecycled()) {
						bmp[i].recycle();
						bmp[i] = null;
					}
				}
			}
		}
	}

	/**
	 * 释放图片
	 * 
	 * @param map
	 */
	public static void releaseBitmap(List<Bitmap> bmp) {
		if (bmp != null) {
			for (int i = 0; i < bmp.size(); i++) {
				if (bmp.get(i) != null) {
					if (!bmp.get(i).isRecycled()) {
						bmp.get(i).recycle();
					}
				}
			}
			bmp.clear();
		}
	}

	/**
	 * 查找指定文件是否存在SD卡
	 * 
	 * @param strPath
	 */
	public static boolean checkFileExist(File dir, String name) {
		File[] files = dir.listFiles();
		if (files == null)
			return false;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				checkFileExist(files[i], name);
			} else {
				if (files[i].getName().equals(name)) {
					if (files[i].length() < 2) {
						files[i].delete();
						return false;
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 查找指定文件是否存在files
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkFileExistActivity(String name) {
		File dir = new File("/data/data/"
				+ MyApplication.context.getPackageName() + "/files/");
		File[] files = dir.listFiles();
		if (files == null)
			return false;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				checkFileExist(files[i], name);
			} else {
				if (files[i].getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检查补丁文件的匹配
	 * 
	 * @param oldstr
	 * @return
	 */
	public static boolean checkPatchMatch(String oldstr) {
		String gamenumber = MyApplication.getAppContext().getGameArgs()
				.getPrefixx();
		List<FileHeader> filelist = MyApplication.getAppContext().logicmain.headerlist;
		String md5 = "";
		for (int i = 0; i < filelist.size(); i++) {
			if (filelist.get(i).name.equals(oldstr)) {
				md5 = filelist.get(i).md5;
				break;
			}
		}

		File oldf = new File(Configs.ASDKROOT + gamenumber + oldstr);
		try {
			String s = MD5Util.getFileMD5String(oldf);
			if (s.equals(md5)) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 应用补丁生成新文件SD卡
	 */
	public static Boolean applyPatch(String oldstr, String patchstr) {
		String gamenumber = MyApplication.getAppContext().getGameArgs()
				.getPrefixx();
		File oldf = new File(Configs.ASDKROOT + gamenumber + oldstr);
		File newf = new File(Configs.ASDKROOT + gamenumber + oldstr + "new");

		File patch = new File(Configs.ASDKROOT + "tmp/" + gamenumber + patchstr);

		byte[] buf = new byte[4];
		RandomAccessFile patchfile = null;
		RandomAccessFile oldfile = null;
		RandomAccessFile newfile = null;
		try {
			if (newf.exists()) {
				newf.delete();
			}
			oldfile = new RandomAccessFile(oldf, "r");
			newfile = new RandomAccessFile(newf, "rw");

			patchfile = new RandomAccessFile(patch, "r");
			patchfile.seek(4);
			patchfile.read(buf, 0, 4);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 原文件大小校验
		long oldsize = oldf.length();
		int checksize = FilesTool.bytesToIntLittle(buf);
		if (oldsize != checksize) {
			MLog.s("补丁文件不匹配啊");
			return false;
		}

		int key = 0;
		boolean bo = true;
		try {
			while ((key = patchfile.read()) != -1 && bo) {
				key = (key < 0 ? key + 256 : key);
				switch (key) {
				case 1:// 插入
					int size = patchfile.read();
					size = (size < 0 ? size + 256 : size);
					int count = 0;
					while (count != size) {
						int b = patchfile.read();
						newfile.write(b);
						count++;
					}
					break;
				case 2:// 拷贝
					patchfile.read(buf, 0, 4);
					int offset = FilesTool.bytesToIntLittle(buf);
					patchfile.read(buf, 0, 4);
					size = FilesTool.bytesToIntLittle(buf);

					oldfile.seek(offset);
					count = 0;
					while (count != size) {
						int b = oldfile.read();
						newfile.write(b);
						count++;
					}
					break;
				case 3:// 插入
					patchfile.read(buf, 0, 4);
					size = FilesTool.bytesToIntLittle(buf);
					count = 0;
					while (count != size) {
						int b = patchfile.read();
						newfile.write(b);
						count++;
					}
					break;
				default:
					bo = false;
					break;
				}
			}
			patchfile.close();
			oldfile.close();
			newfile.close();

			// 删除旧文件
			oldf.delete();
			// 删除补丁文件
			patch.delete();
			// 新文件改名为老文件
			newf.renameTo(oldf);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 应用补丁生成新文件SD卡
	 */
	public static boolean applyPatchFiles(String oldstr, String patchstr) {
		String gamenumber = MyApplication.getAppContext().getGameArgs()
				.getPrefixx();
		File oldf = new File(Configs.ASDKROOT + gamenumber + oldstr);
		File patch = new File(Configs.ASDKROOT + gamenumber + patchstr);
		FileOutputStream out = null;
		try {
			out = MyApplication.context.openFileOutput(gamenumber + oldstr
					+ "new", Context.MODE_PRIVATE);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		byte[] buf = new byte[4];
		RandomAccessFile patchfile = null;
		RandomAccessFile oldfile = null;
		try {
			oldfile = new RandomAccessFile(oldf, "r");
			patchfile = new RandomAccessFile(patch, "r");
			patchfile.seek(4);
			patchfile.read(buf, 0, 4);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 原文件大小校验
		long oldsize = oldf.length();
		int checksize = FilesTool.bytesToIntLittle(buf);
		if (oldsize != checksize) {
			MLog.s("补丁文件不匹配啊");
			return false;
		}

		int key = 0;
		boolean bo = true;
		try {
			while ((key = patchfile.read()) != -1 && bo) {
				key = (key < 0 ? key + 256 : key);
				switch (key) {
				case 1:// 插入
					int size = patchfile.read();
					size = (size < 0 ? size + 256 : size);
					int count = 0;
					while (count != size) {
						int b = patchfile.read();
						out.write(b);
						count++;
					}
					break;
				case 2:// 拷贝
					patchfile.read(buf, 0, 4);
					int offset = FilesTool.bytesToIntLittle(buf);
					patchfile.read(buf, 0, 4);
					size = FilesTool.bytesToIntLittle(buf);

					oldfile.seek(offset);
					count = 0;
					while (count != size) {
						int b = oldfile.read();
						out.write(b);
						count++;
					}
					break;
				case 3:// 插入
					patchfile.read(buf, 0, 4);
					size = FilesTool.bytesToIntLittle(buf);
					count = 0;
					while (count != size) {
						int b = patchfile.read();
						out.write(b);
						count++;
					}
					break;
				default:
					bo = false;
					break;
				}
			}
			patchfile.close();
			oldfile.close();
			out.close();

			// 删除旧文件
			oldf.delete();
			// 删除补丁文件
			patch.delete();
			// 新文件改名为老文件
			File newf = new File(Configs.ASDKROOT + gamenumber + oldstr + "new");
			newf.renameTo(oldf);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * 从资源包中解析图片
	 * 
	 * @param filelist
	 * @param name
	 * @return
	 */
	public static Bitmap getBitmap(String name, int flag) {
		if (Configs.USEXPKG) {
			if (name.contains("/")) {
				String[] arr = name.split("/");
				name = arr[arr.length - 1];
			}
			byte[] data = decompressBytes(name);
			if (data != null) {
				return BitmapFactory.decodeByteArray(data, 0, data.length);
			}
			return null;
		} else {
			return BitmapFactory.decodeStream(getFileStream(
					MyApplication.context, name, flag));
		}
	}

	/**
	 * 从资源包中解析文件流
	 * 
	 * @param filelist
	 * @param name
	 * @return
	 */
	public static InputStream getInputStreams(String name) {
		if (Configs.USEXPKG) {
			byte[] data = decompressBytes(name);
			if (data != null) {
				ByteArrayInputStream by = new ByteArrayInputStream(data);
				return by;
			}
			return null;
		} else {
			return getFileStream(MyApplication.context, name, 1);
		}
	}

	public static int index(String str, String indexStr, int start) {

		int position = str.indexOf(indexStr, start);
		return position;
	}

	/**
	 * 获取APK根目录下文件
	 * 
	 * @param class1当前class类
	 * @param fileName文件名
	 * @return 文件内容
	 */
	public static String readXML(String fileName) {
		String mmChanel = "0000000000";
		InputStream inStream = FilesTool.class.getResourceAsStream(fileName);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(inStream);

			Element root = dom.getDocumentElement();

			NodeList items = root.getElementsByTagName("data");// 查找所有person节点

			for (int i = 0; i < items.getLength(); i++) {
				// 得到第一个person节点
				Element personNode = (Element) items.item(i);

				// 获取person节点下的所有子节点(标签之间的空白节点和name/age元素)
				NodeList childsNodes = personNode.getChildNodes();

				for (int j = 0; j < childsNodes.getLength(); j++) {
					Node node = (Node) childsNodes.item(j); // 判断是否为元素类型

					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element childNode = (Element) node;

						// 判断是否name元素
						if ("channel".equals(childNode.getNodeName())) {
							// 获取name元素下Text节点,然后从Text节点获取数据
							mmChanel = childNode.getFirstChild().getNodeValue();
						}
					}
				}

			}

			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mmChanel;
	}

	public static void sdkLaunch(Activity activity,
			final CallBackListener mcallback, final boolean isHasExitBox) {
		final WindowManager wManager = (WindowManager) MyApplication.context
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.gravity = Gravity.FILL;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		MLog.a("FilesTool", "screenHeight----------------->" + screenHeight);
		MLog.a("FilesTool", "screenWidth----------------->" + screenWidth);
		layoutParams.format = PixelFormat.RGBA_8888;
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

		AssetManager am = MyApplication.context.getResources().getAssets();
		InputStream image_is = null;
		try {
			image_is = am.open("sdk_launch_res/launch_image_pic.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (image_is != null) {
			final RelativeLayout layout = new RelativeLayout(activity);
			layout.setBackground(new BitmapDrawable(MyApplication
					.getAppContext().getResources(), image_is));

			wManager.addView(layout, layoutParams);

			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					wManager.removeView(layout);
					mcallback.callback(0, isHasExitBox);
				}
			}, 1500);
		} else {
			mcallback.callback(0, isHasExitBox);
		}
	}
}
