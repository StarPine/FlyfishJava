package fly.fish.asdk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.keplerproject.luajava.LuaState;

import fly.fish.aidl.OutFace;
import fly.fish.beans.FileHeader;
import fly.fish.beans.GameArgs;
import fly.fish.config.Configs;
import fly.fish.impl.HttpCallBack;
import fly.fish.tools.FilesTool;
import fly.fish.tools.MLog;

public class LogicMain {
	/** http回调接口 */
	public HttpCallBack httpback = null;
	/** 文件头集合 */
	public List<FileHeader> headerlist;
	/** Lua操作对象 */
	public LuaState mLuaState;
	/** 后台更新线程 */
	public HttpThread htback = null;
	/** 我的应用 */
	private MyApplication app;

	/**
	 * 构造方法
	 * 
	 * @param mLuaState
	 */
	public LogicMain(LuaState mLuaState) {
		this.mLuaState = mLuaState;
		// headerlist = new ArrayList<FileHeader>();
		this.app = MyApplication.getAppContext();

	}

	public LogicMain() {
		// headerlist = new ArrayList<FileHeader>();
		this.app = MyApplication.getAppContext();
	}

	/**
	 * 游戏升级初始化
	 */
	public void initUpdate(MyActivity act) {

		// int updateornot =
		// MyApplication.getAppContext().getGameArgs().getUpdateornot();

		/*
		 * if(act instanceof AsdkActivity){ int s = ((AsdkActivity)act).result;
		 * if(s == 2){ act.myhand.sendEmptyMessage(596);//sv }else if(s ==3){
		 * act.myhand.sendEmptyMessage(597);//bv } }else{
		 * 
		 * }
		 */

		MLog.s("Create thread thread");
		if (htback == null) {
			htback = new HttpThread();
		}
		htback.setAct(act);

		if (!htback.isAlive()) {
			htback.start();
			MLog.s("Start thread thread");
		}
		htback.th_notify();

		// System.out.println("result -----> " + updateornot);

		// 走升级程序
		/*
		 * if(!MyApplication.getAppContext().entrylua){
		 * MyApplication.getAppContext().entrylua=true; act.isNeedReload =
		 * false; new HttpThread(this).start(); }else{//走UI程序
		 * if(act.isNeedReload){ act.getMyhand().sendEmptyMessage(0);
		 * act.isNeedReload = false; } }
		 */

	}

	/**
	 * 解析数据头头头头头头头头头
	 */
	public void getGameCPinfo() {
		// 通用版本存在不存在
		File dir = new File(Configs.ASDKROOT);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!FilesTool.checkFileExist(dir, FileHeader.MYPKG)) {
			if (Configs.SDEXIST) {
				FilesTool.copyAssetsToSDFiles(FileHeader.MYPKG, FileHeader.MYPKG);// 走Assets到SD
			} else {
				FilesTool.copyAssetsToFiles(FileHeader.MYPKG, FileHeader.MYPKG);// 走Assets到files
			}
		} else {
			String gamenumber="";
			try {
				gamenumber = MyApplication.getAppContext().getGameArgs().getPrefixx();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			InputStream assets_is = FilesTool.getFileStream(MyApplication.context, gamenumber + FileHeader.MYPKG, 1);
			InputStream local_is = FilesTool.getFileStream(MyApplication.context, gamenumber + FileHeader.MYPKG, 2);
			byte[] buf = new byte[1024];
			try {
				assets_is.read(buf, 0, 4);
				assets_is.read(buf, 0, 4);
				int assets_version = FilesTool.bytesToIntBig(buf);

				local_is.read(buf, 0, 4);
				local_is.read(buf, 0, 4);
				int local_version = FilesTool.bytesToIntBig(buf);
				System.out.println("--------assets_cra_version-------->" + assets_version);
				System.out.println("--------local_cra_version--------->" + local_version);
				if (assets_version != local_version) {
					FilesTool.deleteCraFile(dir);
					if (Configs.SDEXIST) {
						FilesTool.copyAssetsToSDFiles(FileHeader.MYPKG, FileHeader.MYPKG);// 走Assets到SD
					} else {
						FilesTool.copyAssetsToFiles(FileHeader.MYPKG, FileHeader.MYPKG);// 走Assets到files
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			GameArgs gameargs = MyApplication.getAppContext().getGameArgs();

			gameargs.setCpid(gameargs.getCpid() == null ? "" : gameargs.getCpid());
			String publisher = OutFace.getInstance(null).getPublisher();
			gameargs.setPublisher(publisher);
			gameargs.setPublisher(gameargs.getPublisher() == null ? "" : gameargs.getPublisher());
			gameargs.setGameno(gameargs.getGameno() == null ? "" : gameargs.getGameno());
			gameargs.setPrefixx(gameargs.getCpid() + "x" + gameargs.getGameno());

			if (specialPKG(gameargs.getCpid(), gameargs.getGameno())) {
				gameargs.setPrefixx(gameargs.getCpid() + "x" + gameargs.getGameno());
			} else {
				gameargs.setPrefixx("");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FilesTool.loadDatFiles(2);

	}

	/**
	 * 是否使用特种风格
	 * 
	 * @param cpid
	 * @param gameno
	 * @return
	 */
	public boolean specialPKG(String cpid, String gameno) {
		if (cpid != null && gameno != null) {
			// 检测包存不存在
			File dir = new File(Configs.ASDKROOT);
			boolean exist = FilesTool.checkFileExist(dir, cpid + "x" + gameno + FileHeader.MYPKG);
			return exist;
		}
		return false;
	}

	public void setmLuaState(LuaState mLuaState) {
		this.mLuaState = mLuaState;
	}

	public List<FileHeader> getHeaderlist() {
		return headerlist;
	}

	public void setHeaderlist(List<FileHeader> headerlist) {
		this.headerlist = headerlist;
	}

}
