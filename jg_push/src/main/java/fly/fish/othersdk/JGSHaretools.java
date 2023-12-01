package fly.fish.othersdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import cn.jiguang.share.android.api.AuthListener;
import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.PlatformConfig;
import cn.jiguang.share.android.api.ShareParams;
import cn.jiguang.share.android.model.AccessTokenInfo;
import cn.jiguang.share.android.model.BaseResponseInfo;
import cn.jiguang.share.qqmodel.QQ;
import cn.jiguang.share.qqmodel.QZone;
import cn.jiguang.share.wechat.Wechat;
import cn.jiguang.share.weibo.SinaWeibo;

public class JGSHaretools {


    private static Activity mactivity;

    private static String Wxappid=null,Wxghid=null,Wxghpath=null,QQkey=null;


    private static String JGwxappid,JGwxappsecret,JGQQappid,JGQQappkey,JGWBappkey,JGWBappScret,JGWBrdurl;

    private static String  TZlist  ,FXlist;

    public static void othshare(Activity activity, int code ){
        initData(activity);
        System.out.println("forasdk   JGshare==");

        try {
            JSONObject jsonObject = new JSONObject(TZlist);
            String qqun1=jsonObject.getString("id1");
            JSONObject jsonqqqun = new JSONObject(qqun1);
            QQkey=jsonqqqun.getString("qunkey");

            switch (code){
                case 1:
                    joinQQGroup(activity);
                    break;
                case 2:
                    String wx1=jsonObject.getString("id2");
                    JSONObject jswx1 = new JSONObject(wx1);
                    setWxappid(jswx1.getString("appid"),jswx1.getString("xcxid"),jswx1.getString("xcxpath"));
                    othsharetowx(activity);
                    break;
                case 3:
                    String wx2=jsonObject.getString("id3");
                    JSONObject jswx2 = new JSONObject(wx2);
                    setWxappid(jswx2.getString("appid"),jswx2.getString("xcxid"),jswx2.getString("xcxpath"));
                    othsharetowx(activity);
                    break;
                case 4:
                    String wx3=jsonObject.getString("id4");
                    JSONObject jswx3 = new JSONObject(wx3);
                    setWxappid(jswx3.getString("appid"),jswx3.getString("xcxid"),jswx3.getString("xcxpath"));
                    othsharetowx(activity);
                    break;
                case 5:
                    String wx4=jsonObject.getString("id5");
                    JSONObject jswx4 = new JSONObject(wx4);
                    setWxappid(jswx4.getString("appid"),jswx4.getString("xcxid"),jswx4.getString("xcxpath"));
                    othsharetowx(activity);

                    break;
                case 6:
                    String wx5=jsonObject.getString("id6");
                    JSONObject jswx5 = new JSONObject(wx5);
                    setWxappid(jswx5.getString("appid"),jswx5.getString("xcxid"),jswx5.getString("xcxpath"));
                    othsharetowx(activity);
                    break;
                default:
                    break;

            }

        }catch ( Exception e) {
            e.printStackTrace();
        }

    }

    private static void initData(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("user_info", 0);
        String othersdkextdata4 = sharedPreferences.getString("othersdkextdata4", "");
        try {
            JSONObject jsonObject2 = new JSONObject(othersdkextdata4);
            String tzlist=jsonObject2.getString("tzlist");
            String fxlist=jsonObject2.getString("fxlist");
//            MLog.a("AESSecurity-set",  "<------ tzlist ------> " + tzlist);
//            MLog.a("AESSecurity-set",  "<------ fxlist ------> " + fxlist);
            setdata(tzlist,fxlist);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void othJgshare(Activity activity, int code ,File file){
        initData(activity);
        init(activity);


        try {
            JGinit(activity);
            switch (code){
                case 1:
                    JGshare(Wechat.Name,file);
                    break;
                case 2:
                    //JGshare(WechatMoments.Name,file);
                    Uri uri=Uri.fromFile(file);
                    Intent share= new Intent(Intent.ACTION_SEND);
                    try {
                        if(uri!=null){

                            share.putExtra(Intent.EXTRA_STREAM,uri);

                        }else
                            share.putExtra(Intent.EXTRA_STREAM,file);
                        share.setType("image/*");

                        share.setPackage("com.tencent.mm");

                        activity.startActivity(Intent.createChooser(share,"test"));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    JGshare(QQ.Name,file);
                    break;
                case 4:
                    JGshare(QZone.Name,file);
                    break;
                case 5:
                    JGshare(SinaWeibo.Name,file);
                    break;
                default:
                    break;
            }


        }catch (Exception e){

        }
    }

    public static void setdata(String tzlist,String fxlist){
        TZlist=tzlist;

        FXlist=fxlist;


        try {
            JSONObject jsonObject = new JSONObject(FXlist);
            JGwxappid=jsonObject.getString("weChatAppId");
            JGwxappsecret=jsonObject.getString("weChatAppSecret");;
            JGQQappid=jsonObject.getString("qqAppId");;
            JGQQappkey=jsonObject.getString("qqAppKey");;
            JGWBappkey=jsonObject.getString("sinaWeiboAppKey");
            JGWBappScret=jsonObject.getString("sinaWeiboAppSecret");
            JGWBrdurl=jsonObject.getString("sinaRedirectUri");

        }catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("JGSHaretools  TZlist=="+TZlist);
        System.out.println("JGSHaretools  FXlist=="+FXlist);


    }

    public static void setWxappid(String wxappid,String wxghid,String wxghpath){
        Wxappid=wxappid;
        Wxghid=wxghid;
        Wxghpath=wxghpath;
        System.out.println("JGSHaretools  Wxappid=="+Wxappid+" Wxghid="+Wxghid+" Wxghpath="+Wxghpath);


    }
    public static void setJGQQ(String jgqqid,String jgqqkey){
        JGQQappid=jgqqid;
        JGQQappkey=jgqqkey;
    }
    public static void setQQkey(String qkey){
        QQkey=qkey;
    }
    public static void setJGWX(String jgwxid,String jgwxsecret){
        JGwxappid=jgwxid;
        JGwxappsecret=jgwxsecret;
    }
    public static void setJGWB(String wbappkey,String wbsecret,String wbrdurl){
        JGWBappkey=wbappkey;
        JGWBappScret=wbsecret;
        JGWBrdurl=wbrdurl;
    }

    public static void JGinit(Context context){
        JShareInterface.setDebugMode(true);

        PlatformConfig platformConfig = new PlatformConfig()
                .setQQ(JGQQappid,JGQQappkey)
                .setWechat(JGwxappid,JGwxappsecret)
                .setSinaWeibo(JGWBappkey,JGWBappScret,JGWBrdurl);

        JShareInterface.init(context ,platformConfig);

    }


    public static void init(Activity activity){
        mactivity=activity;
        StrictMode.VmPolicy.Builder builder =new StrictMode.VmPolicy.Builder();

        StrictMode.setVmPolicy(builder.build());

        builder.detectFileUriExposure();
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//				|| ContextCompat.checkSelfPermission(this,
//						Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

        ) {
            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//					,Manifest.permission.READ_PHONE_STATE
                    ,Manifest.permission.CAMERA
            }, 1);
        }
    }
    public static void othsharetowx(Activity activity ){


       // String appId = "wxfa222634566235eb"; // 填移动应用(App)的 AppId，非小程序的 AppID
        IWXAPI api = WXAPIFactory.createWXAPI(activity, Wxappid);

        if(api.isWXAppInstalled()){
            WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
           // req.userName = "gh_fa2aa79a6445"; // 填小程序原始id
            req.userName = Wxghid;
              req.path = Wxghpath;                  ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
            api.sendReq(req);
        }else {
            Toast.makeText(activity,"微信未安装",Toast.LENGTH_LONG).show();
        }
    }

    public static boolean joinQQGroup(Activity key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + QQkey));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            key.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            e.printStackTrace();
            return false;
        }
    }
    public static void JGshare(String pacgname,File file) throws IOException {

        JGAu(pacgname);// Wechat.Name
        boolean iscvqq=JShareInterface.isClientValid(pacgname);
        System.out.println("JGSHaretools  isClientValid= "+iscvqq);

        boolean issaqq=JShareInterface.isSupportAuthorize(pacgname);
        System.out.println("JGSHaretools  isSupportAuthorize= "+issaqq);

        boolean isauqq=JShareInterface.isAuthorize(pacgname);
        System.out.println("JGSHaretools  isAuthorize= "+isauqq);
        if(isauqq){
            File media1 =file;
            Bitmap bitmap =  getimage(media1.getPath());

            File media = saveFile(bitmap,System.currentTimeMillis()+"share.jpg");

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                bitmap =  getimage(media1.getPath());
//                try {
//                    media =  saveFile(bitmap,"share.jpg");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).run();


            if( media1==null){
                System.out.println("jsshare  imageFile= null");
            }else {

                System.out.println("jsshare  imageFile=  11114");
                ShareParams shareParams = new ShareParams();
//        shareParams.setShareType(Platform.SHARE_WEBPAGE);
                shareParams.setShareType(Platform.SHARE_IMAGE);
//        shareParams.setTitle("share_title");
//        shareParams.setImagePath( MyApplication.ImagePath );
                shareParams.setImagePath(media.getAbsolutePath());

//        shareParams.setImageUrl("https://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1308/02/c0/24056523_1375430477597.jpg");
                JShareInterface.share( pacgname , shareParams, new PlatActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        System.out.println("jsshare  onComplete= "+isauqq);
                    }

                    @Override
                    public void onError(Platform platform, int i, int i1, Throwable throwable) {
                        System.out.println("jsshare  onError= "+isauqq);
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        System.out.println("jsshare  onCancel= "+isauqq);
                    }
                });
            }
        }

        //  File imageFile =  copyResurces( "mytest1.jpg", "test_img.jpg", 0);
//        String filename = "/myJGtest.jpg";
////        String mediaPath = Environment.getExternalStorageDirectory() + filename;
//        String mediaPath = Environment.getExternalStorageDirectory().getAbsolutePath() + filename;

    }


    public static void JGAu (String pacgname){
        JShareInterface.authorize(pacgname, new AuthListener() {
            @Override
            public void onComplete(Platform platform, int action, BaseResponseInfo data) {
//                Logger.dd(TAG, "onComplete:" + platform + ",action:" + action + ",data:" + data);
                String toastMsg = null;
                switch (action) {
                    case Platform.ACTION_AUTHORIZING:
                        if (data instanceof AccessTokenInfo) {        //授权信息
                            String token = ((AccessTokenInfo) data).getToken();//token
                            long expiration = ((AccessTokenInfo) data).getExpiresIn();//token有效时间，时间戳
                            String refresh_token = ((AccessTokenInfo) data).getRefeshToken();//refresh_token
                            String openid = ((AccessTokenInfo) data).getOpenid();//openid
                            //授权原始数据，开发者可自行处理
                            String originData = data.getOriginData();
                            toastMsg = "授权成功:" + data.toString();
                            System.out.println("jsshare  授权成功:" + data.toString());
//                            Logger.dd(TAG, "openid:" + openid + ",token:" + token + ",expiration:" + expiration + ",refresh_token:" + refresh_token);
//                            Logger.dd(TAG, "originData:" + originData);

                            System.out.println("jsshare  openid:" + openid + ",token:" + token + ",expiration:" + expiration + ",refresh_token:" + refresh_token);
                            System.out.println("jsshare  originData= "+originData);
                        }
                        break;
                }
            }

            @Override
            public void onError(Platform platform, int action, int errorCode, Throwable error) {
                String toastMsg = null;
                System.out.println("jsshare  授权失败:"  );
                switch (action) {
                    case Platform.ACTION_AUTHORIZING:
                        toastMsg = "授权失败";
                        break;
                }
            }

            @Override
            public void onCancel(Platform platform, int action) {
//                Logger.dd(TAG, "onCancel:" + platform + ",action:" + action);
                String toastMsg = null;
                System.out.println("jsshare  取消授权:"  );
                switch (action) {
                    case Platform.ACTION_AUTHORIZING:
                        toastMsg = "取消授权";
                        break;
                }
            }
        });
    }


    /**
     * 图片按比例大小压缩方法
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public  static Bitmap getimage(String srcPath) {


        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }
    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static   Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
    public static File saveFile(Bitmap bm, String fileName) throws IOException {//将Bitmap类型的图片转化成file类型，便于上传到服务器
        String path = Environment.getExternalStorageDirectory() + "/Ask";
        if (Build.VERSION.SDK_INT > 29) {
            path = mactivity.getExternalFilesDir(null).getAbsolutePath() + "/appaudio/";

        } else {
            path = Environment.getExternalStorageDirectory().getPath()  ;

        }
        path =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().toString();
        File filesDir = new File(path + "/yymxj/" + fileName);
        File parentDir = filesDir.getParentFile();
        System.out.println("jsshare  parentDir=:"+parentDir  );
        if(!parentDir.exists()){
            parentDir.mkdirs();
        }
//        File dirFile = new File(path);
//        if(!dirFile.exists()){
//            dirFile.mkdir();
//        }

//        File myCaptureFile = new File(getFilesDir(),fileName);

        if(!filesDir.exists()){
            filesDir.createNewFile();
        }

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filesDir));
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        FileOutputStream bos = new FileOutputStream(myCaptureFile);
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return filesDir;

    }
}
