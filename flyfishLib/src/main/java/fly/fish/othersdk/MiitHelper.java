package fly.fish.othersdk;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.JLibrary;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.supplier.IIdentifierListener;
import com.bun.supplier.IdSupplier;

import fly.fish.tools.MLog;
import fly.fish.tools.PhoneTool;

/**
 * Created by zheng on 2019/8/22.
 */

public class MiitHelper {

    public static void InitEntry(Application application){
    	try {
			JLibrary.InitEntry(application);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    static long timeb = 0;
    public static void getDeviceIds(Context cxt){
        timeb=System.currentTimeMillis();
        int nres = CallFromReflect(cxt);
        System.out.println("nres---"+nres);
        if(nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT){//不支持的设备
        	
        }else if( nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE){//加载配置文件出错
        	
        }else if(nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT){//不支持的设备厂商
        	
        }else if(nres == ErrorCode.INIT_ERROR_RESULT_DELAY){//获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
        	
        }else if(nres == ErrorCode.INIT_HELPER_CALL_ERROR){//反射调用出错
        	
        }
        Log.d("ASDK","return value: "+String.valueOf(nres));

    }


    /*
    * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
    *
    * */
    private static int CallFromReflect(Context cxt){
        return MdidSdkHelper.InitSdk(cxt,true,new IIdentifierListener() {
			
			@Override
			public void OnSupport(boolean isSupport, IdSupplier _supplier) {
				MLog.a("isSupport--"+isSupport);
		    	MLog.a("_supplier--"+_supplier);
		        if(_supplier==null) {
		            return;
		        }
		        MLog.a("_supplier.isSupported()--"+_supplier.isSupported());
		        if(!_supplier.isSupported()){
		            return;
		        }
		        String oaid=_supplier.getOAID();
		        int count_0 = getCount(oaid, "0");
		        if(count_0<10){//判断oaid包含0的个数，小于指定数字则为正常
		        	PhoneTool.setOAID(oaid);
		        }
		        long timee=System.currentTimeMillis();
		        long offset=timee-timeb;
		        Log.d("ASDK","offset: "+offset);
		        System.out.println("oaid---"+oaid);
			}


		});
    }
    private static int getCount(String str,String key){  
    	if(str == null || key == null || "".equals(str.trim()) || "".equals(key.trim())){  
    		return 0;  
    	}  
    	int count = 0;  
    	int index = 0;  
    	while((index=str.indexOf(key,index))!=-1){  
	    	index = index+key.length();  
	    	count++;  
    	}  
    	return count;  
    }
    //以下是华为使用
    public static void getOaid(Context cxt){
    	timeb=System.currentTimeMillis();
        int nres = CallFromReflect_huawei(cxt);
        System.out.println("nres---"+nres);
        if(nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT){//不支持的设备
        	
        }else if( nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE){//加载配置文件出错
        	
        }else if(nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT){//不支持的设备厂商
        	
        }else if(nres == ErrorCode.INIT_ERROR_RESULT_DELAY){//获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
        	
        }else if(nres == ErrorCode.INIT_HELPER_CALL_ERROR){//反射调用出错
        	
        }
        Log.d("ASDK","return value: "+String.valueOf(nres));
    }
    private static String oaid = "";
    public static String getOAID(String str){
		return oaid;
	}
    private static int CallFromReflect_huawei(Context cxt){
        return MdidSdkHelper.InitSdk(cxt,true,new IIdentifierListener() {
			
			@Override
			public void OnSupport(boolean isSupport, IdSupplier _supplier) {
				MLog.a("isSupport--"+isSupport);
		    	MLog.a("_supplier--"+_supplier);
		        if(_supplier==null) {
		            return;
		        }
		        MLog.a("_supplier.isSupported()--"+_supplier.isSupported());
		        if(!_supplier.isSupported()){
		            return;
		        }
		        oaid=_supplier.getOAID();
		        long timee=System.currentTimeMillis();
		        long offset=timee-timeb;
		        Log.d("ASDK","offset: "+offset);
		        System.out.println("oaid---"+oaid);
			}


		});
    }

    /*
    * 直接java调用，如果这样调用，在android 9以前没有题，在android 9以后会抛找不到so方法的异常
    * 解决办法是和JLibrary.InitEntry(cxt)，分开调用，比如在A类中调用JLibrary.InitEntry(cxt)，在B类中调用MdidSdk的方法
    * A和B不能存在直接和间接依赖关系，否则也会报错
    *
    * */
//    private int DirectCall(Context cxt){
//        MdidSdk sdk = new MdidSdk();
//        return sdk.InitSdk(cxt,this);
//    }
//    @Override
//    public void OnSupport(boolean isSupport, IdSupplier _supplier) {
//    	MLog.a("isSupport--"+isSupport);
//    	MLog.a("_supplier--"+_supplier);
//        if(_supplier==null) {
//            return;
//        }
//        MLog.a("_supplier.isSupported()--"+_supplier.isSupported());
//        if(!_supplier.isSupported()){
//            return;
//        }
//        String oaid=_supplier.getOAID();
//        PhoneTool.setOAID(oaid);
//        System.out.println("oaid---"+oaid);
//        _supplier.shutDown();
//    }

}
