package fly.fish.dialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import fly.fish.aidl.OutFace;
import fly.fish.asdk.MyApplication;
import fly.fish.tools.MLog;
import fly.fish.tools.OthPhone;

public class DialgTool {
	
	
	public static String getpub(String fileName){
		
		String pub="";
		try {
			InputStream ins = MyApplication.context.getResources().getAssets()
					.open(fileName);
			pub = new BufferedReader(new InputStreamReader(ins)).readLine()
					.trim();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return pub;
		
	}
	
	

	/**
	 * GET协议请求
	 */
	public static String getWebMethod(String urls){
		

		String data="";
		HttpURLConnection connection = null;
        BufferedReader reader = null;
        try{
        	 
            URL url = new URL(urls);//新建URL
            connection = (HttpURLConnection)url.openConnection();//发起网络请求
            connection.setRequestMethod("GET");//请求方式
            connection.setConnectTimeout(8000);//连接最大时间
            connection.setReadTimeout(8000);//读取最大时间
            InputStream in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));//写入reader
            StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                response.append(line);
            }
             
            data=response.toString();
            MLog.a("返回结果------"+response.toString());

            JSONObject jsonObject =new JSONObject(data);
            boolean isrequ = jsonObject.getBoolean("isrequ");
            boolean ischeck = jsonObject.getBoolean("ischeck");

            OutFace.setCheckState(ischeck);
            if(isrequ){
				OutFace.setisreq(true);
                OthPhone.setisreq(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(reader != null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(connection != null){
                connection.disconnect();
            }
        }
        
        return data;
	
 
	}
			

}
