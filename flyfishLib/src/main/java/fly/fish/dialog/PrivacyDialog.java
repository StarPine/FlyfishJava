package fly.fish.dialog;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
 
import android.widget.Button;
import android.widget.TextView;



public class PrivacyDialog extends Dialog{

    Context context;
    private String json_fw;
    private String json_zc;
    private String json_qx;

    private TextView btnfw;
    private TextView btnxy;
    private TextView text_qx;
    private TextView privacy_welcom;

    private TextView text_title;

    private Button button_yes;
    private TextView button_no;

    private ClickInterface clickInterface;
    /**
     * 点击事件的监听接口
     */
    public interface ClickInterface{
        void  doCofirm();
        void doCancel();
    }

    /**
     * 自定义控件需要重写构造函数,传入需要要的值
     */
    public PrivacyDialog(Context context,String json1,String json2,String json3,int style_id){
        super(context,style_id);
//        int MyDialog_id = context.getResources().getIdentifier("MyDialog", "style", context.getPackageName());
//        super(context,style_id);
       // super(context,R.style.MyDialog_id);
        this.context=context;
        this.json_fw=json1;
        this.json_zc=json2;
        this.json_qx=json3;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDta();
        initView();


    }

    private void initDta() {



    }

    /**
     * 初始化控件
     * 1.加载布局
     * 2.指定窗口的大小
     * 3.文本的设置
     */
    private void initView() {
        int privacydialog_layout_id = context.getResources().getIdentifier("privacydialog_layout", "layout", context.getPackageName());
        View view = LayoutInflater.from(context).inflate(privacydialog_layout_id,null);
        setContentView(view);



        int game_fy_id = context.getResources().getIdentifier("game_fy", "id", context.getPackageName());
        int game_xy_id = context.getResources().getIdentifier("game_xy", "id", context.getPackageName());

        btnfw = (TextView) view.findViewById(game_fy_id);
        btnxy = (TextView) view.findViewById(game_xy_id);


        int privacy_yes_id = context.getResources().getIdentifier("privacy_yes", "id", context.getPackageName());
        int privacy_no_id = context.getResources().getIdentifier("privacy_no", "id", context.getPackageName());
        button_yes = (Button) view.findViewById(privacy_yes_id);
        button_no = (TextView) view.findViewById(privacy_no_id);


        int text_qx_id = context.getResources().getIdentifier("text_qx", "id", context.getPackageName());
        text_qx= (TextView) view.findViewById(text_qx_id);
        text_qx.setText(json_qx);
        text_qx.setMovementMethod(new ScrollingMovementMethod());
        
        int text_we_id = context.getResources().getIdentifier("privacy_welcom", "id", context.getPackageName());
        privacy_welcom=(TextView) view.findViewById(text_we_id);
        privacy_welcom.setMovementMethod(new ScrollingMovementMethod());
        //        webView_qx = (WebView) view.findViewById(web_qx_id);
//        webView_qx.getSettings().setJavaScriptEnabled(true);
//        //设置自适应屏幕，两者合用
//        webView_qx.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
//        webView_qx.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        webView_qx.loadUrl(json_qx);

        int text_ti_id = context.getResources().getIdentifier("privacy_title", "id", context.getPackageName());
        text_title= (TextView) view.findViewById(text_ti_id);
        text_title.setMovementMethod(new ScrollingMovementMethod());

        btnfw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DetailsActivity.class);
//                intent.putExtra("key",1);
                intent.putExtra("url",json_fw);
                context.startActivity(intent);
            }
        });

        btnxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DetailsActivity.class);
//                intent.putExtra("key",2);
                intent.putExtra("url",json_zc);
                context.startActivity(intent);
            }
        });

//        button_yes.setOnClickListener(new ClickListener());
//        button_no.setOnClickListener(new ClickListener());

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (clickInterface != null) {
                        clickInterface.doCofirm();
                }
            }
        });

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                   if (clickInterface != null) {
                        clickInterface.doCancel();
                    }
            }
        });


        Window dialogWindow = getWindow();

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    /**
     * 点击事件的设置-----接口回调
     * 设置点击事件,需要重写按钮的确定和取消要执行的操作
     */
    public void setClickListener(ClickInterface clickInterface){
        this.clickInterface=clickInterface;
    }

//    /**
//     * 系统的点击事件的自动监听
//     */
//    private class ClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {

//            int id = v.getId();
//            switch (id) {
//                case R.id.privacy_no:
//                    if (clickInterface != null) {
//                        clickInterface.doCancel();
//                    }
//                    break;
//                case R.id.privacy_yes:
//                    if (clickInterface != null) {
//                        clickInterface.doCofirm();
//                    }
//                    break;
//            }
//        }
//    }
}

