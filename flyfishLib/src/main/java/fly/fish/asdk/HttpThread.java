package fly.fish.asdk;

import java.util.Map;

import fly.fish.tools.LuaTools;
import fly.fish.tools.MLog;

public class HttpThread extends Thread {
	/**我的应用*/
	private MyApplication app;
	/**循环控制*/
	private boolean repeat;
	/**主体activity*/
	private MyActivity act = null;
	
	public HttpThread(){
		this.repeat = true;
		this.app =  MyApplication.getAppContext();
	}
	
	@Override
	public void run() {
		while(repeat){

			/*if(!MyApplication.getAppContext().luainitok){
				//解析头文件
				app.parseData();
				//加载升级文件 
				LuaTools.loadUpdate();
				
		        //走升级程序（这里不能使用rootview）一遍足以（升级返回结果）
				
				synchronized (app.getmLuaState()) {
					app.getmLuaState().getGlobal("update");
			        int index = app.getmLuaState().getTop();
			    	app.getmLuaState().getField(index, "startup");
			    	LuaTools.dbcall(app.getmLuaState(),0,1);
				}

		    	
		        //取检查结果
		        int re = app.getmLuaState().toInteger(-1);
		    	//如果网络出错了怎么办
		    	if(re==0 || re==1){
		    		act.getMyhand().sendEmptyMessage(602);
		    	}else{
			    	MyApplication.getAppContext().luainitok = true;
			    	MLog.s("gothrough thread luainitok over!!!");
		    	}
		    	
			}else{
				//升级没有走
				if(!MyApplication.getAppContext().luaupdateok){
					
					synchronized (app.getmLuaState()) {
						app.getmLuaState().getGlobal("update");
				        int index = app.getmLuaState().getTop();
				    	app.getmLuaState().getField(index, "analyseresult");
				    	LuaTools.dbcall(app.getmLuaState(),0,0);
					}
					
			    	
			    	MLog.s("gothrough thread luaupdateok begin!!!");
				}else{
					//走UI程序
					act.getMyhand().sendEmptyMessage(0);
				}
			}*/
			
			
			if(!MyApplication.getAppContext().luainitok){
				//解析头文件
				app.parseData();
				//加载升级文件 （启动加载）
				LuaTools.loadUpdate();
				
				synchronized (app.getmLuaState()) {
					app.getmLuaState().getGlobal("update");
			        int index = app.getmLuaState().getTop();
			        app.getmLuaState().getField(index, "init");
			        app.getmLuaState().pushString(app.re1);
			        app.getmLuaState().pushString(app.re2);
			        app.getmLuaState().pushString(app.re3);
			    	LuaTools.dbcall(app.getmLuaState(),3,0);
				}
				
				MLog.s(app.re1+" url *************************** url");
				MLog.s(app.re2+" url *************************** url");
				MLog.s(app.re3+" url *************************** url");
				
				MyApplication.getAppContext().luainitok = true;
			}
			
			//只有走这里时才升级
			if(act instanceof AsdkActivity){
				AsdkActivity a = ((AsdkActivity)act);
				a.startUpdateThread();
			}else{
				//走UI程序
				act.getMyhand().sendEmptyMessage(0);
				MLog.s("ENTRY--------THREAD RUN");
			}
			
			th_wait();
			
		}
	}
	

	public MyActivity getAct() {
		return act;
	}

	public void setAct(MyActivity act) {
		this.act = act;
	}
	
    /**
     * 线程休眠
     */
    public void th_wait(){
    	synchronized (app) {
			try {
				MLog.s("Wait thread thread");
				app.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * 线程唤醒
     */
    public void th_notify(){
		synchronized (app) {
			MLog.s("Notify thread thread");
			app.notify();
		}
    }
    
    /**
     * 线程死亡
     */
	public void endThread(){
		synchronized (app) {
			repeat = false;
			app.notify();
		}
	}
	
}
