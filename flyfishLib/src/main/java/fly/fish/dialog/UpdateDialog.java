package fly.fish.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class UpdateDialog extends Dialog {

    Context context;
    private String title;
    private String updateContent;
    private int status;

    private TextView textUpdateContent;
    private TextView textUpdatetitle;
    private Button buttonCofirm;
    private Button buttonCancel;

    private ClickInterface clickInterface;

    public void setClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    /**
     * 自定义控件需要重写构造函数,传入需要要的值
     */
    public UpdateDialog(Context context, String title, String updateContent, int status) {
        super(context, context.getResources().getIdentifier("MyDialog", "style", context.getPackageName()));
        this.context = context;
        this.title = title;
        this.updateContent = updateContent;
        this.status = status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    /**
     * 初始化控件
     * 1.加载布局
     * 2.指定窗口的大小
     * 3.文本的设置
     */
    private void initView() {
        int privacydialog_layout_id = context.getResources().getIdentifier("update_dialog_layout", "layout", context.getPackageName());
        View view = LayoutInflater.from(context).inflate(privacydialog_layout_id, null);
        setContentView(view);

        int text_title_id = context.getResources().getIdentifier("text_title", "id", context.getPackageName());
        textUpdatetitle = (TextView) view.findViewById(text_title_id);
        textUpdatetitle.setMovementMethod(new ScrollingMovementMethod());
        if (!TextUtils.isEmpty(title))
            textUpdatetitle.setText(title);

        int text_content_id = context.getResources().getIdentifier("text_update_content", "id", context.getPackageName());
        textUpdateContent = (TextView) view.findViewById(text_content_id);
        textUpdateContent.setMovementMethod(new ScrollingMovementMethod());
        if (!TextUtils.isEmpty(title))
            textUpdateContent.setText(updateContent);

        int privacy_yes_id = context.getResources().getIdentifier("button_cofirm", "id", context.getPackageName());
        int privacy_no_id = context.getResources().getIdentifier("button_cancel", "id", context.getPackageName());
        buttonCofirm = (Button) view.findViewById(privacy_yes_id);
        buttonCancel = (Button) view.findViewById(privacy_no_id);
        if (status == 2) {
            buttonCancel.setVisibility(View.GONE);
        }

        buttonCofirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (clickInterface != null) {
                    clickInterface.doCofirm();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
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

    public interface ClickInterface {
        void doCofirm();

        void doCancel();
    }
}

