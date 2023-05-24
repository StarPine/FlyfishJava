package fly.fish.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.org.suspension.model.JXResUtils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhoneCode extends RelativeLayout {

    private Context context;
    private TextView tv_code1;
    private TextView tv_code2;
    private TextView tv_code3;
    private TextView tv_code4;
    private EditText et_code;
    private List<String> codes = new ArrayList<String>();
    private InputMethodManager imm;

    public PhoneCode(Context context) {
        super(context);
        this.context = context;
        loadView();
    }

    public PhoneCode(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        loadView();
    }

    private void loadView() {
    	int phone_code_layid = JXResUtils.getLayoutId("layout_phonecode");
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = LayoutInflater.from(context).inflate(phone_code_layid, this);
        initView(view);
        initEvent();
    }

    private void initView(View view) {
        tv_code1 = (TextView) view.findViewById(JXResUtils.getId("tv_code1"));
        tv_code2 = (TextView) view.findViewById(JXResUtils.getId("tv_code2"));
        tv_code3 = (TextView) view.findViewById(JXResUtils.getId("tv_code3"));
        tv_code4 = (TextView) view.findViewById(JXResUtils.getId("tv_code4"));
        et_code = (EditText) view.findViewById(JXResUtils.getId("et_code"));
        et_code.requestFocus();
        et_code.setImeOptions(268435456);
        et_code.setLongClickable(true);//支持长按
    }

    private void initEvent() {
        //验证码输入
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && editable.length() > 0) {
                    et_code.setText("");
                    if (codes.size() < 4) {
                        String data = editable.toString().trim();
                        if (data.length() >= 4) {
                            //将string转换成List
                            List<String> list = Arrays.asList(data.split(""));
                            //Arrays.asList没有实现add和remove方法，继承的AbstractList，需要将list放进java.util.ArrayList中
                            codes = new ArrayList<String>(list);
//                            if (EmptyUtils.isNotEmpty(codes) && codes.size() > 6) {
//                                //使用data.split("")方法会将""放进第一下标里需要去掉
//                                codes.remove(0);
//                            }
                        } else {
                            codes.add(data);
                        }
                        showCode();
                    }
                }
            }
        });
        // 监听验证码删除按键
        et_code.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (codes.size()>0) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN && codes.size() > 0) {
                        codes.remove(codes.size() - 1);
                        showCode();
                        return true;
                    }
                }
                return false;
            }
        });

//        final XPopup.Builder builder = new XPopup.Builder(getContext()).watchView(et_code);
//        et_code.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                builder.asAttachList(new String[]{"粘贴"}, null, new OnSelectListener() {
//                    @Override
//                    public void onSelect(int selectPostion, String text) {
//                        //剪贴板中允许四种类型，Html, String,Uri,Intent，不能直接获取ClipData.getItemAt(0).getText()这样仅是获取剪贴板中第一项是文本的，如果第一项不是文本，获取null
//                        String data = ClipboardUtils.getTextFromText();//获取文本
//                        if(EmptyUtils.isEmpty(data)){
//                            return;
//                        }
//                        if (data.length() >= 6) {
//                            //将string转换成List
//                            List<String> list = Arrays.asList(data.split(""));
//                            //Arrays.asList没有实现add和remove方法，继承的AbstractList，需要将list放进java.util.ArrayList中
//                            codes = new ArrayList<>(list);
//                            if (EmptyUtils.isNotEmpty(codes) && codes.size() > 6) {
//                                //使用data.split("")方法会将“”放进第一下标里需要去掉
//                                codes.remove(0);
//                            }
//                        } else {
//                            codes.add(data);
//                        }
//                        showCode();
//                    }
//                }).show();
//                return true;
//            }
//        });
    }

    /**
     * 显示输入的验证码
     */
    private void showCode() {
        String code1 = "";
        String code2 = "";
        String code3 = "";
        String code4 = "";
        if (codes.size() >= 1) {
            code1 = codes.get(0);
        }
        if (codes.size() >= 2) {
            code2 = codes.get(1);
        }
        if (codes.size() >= 3) {
            code3 = codes.get(2);
        }
        if (codes.size() >= 4) {
            code4 = codes.get(3);
        }
        tv_code1.setText(code1);
        tv_code2.setText(code2);
        tv_code3.setText(code3);
        tv_code4.setText(code4);
        //setColor();//设置高亮颜色
        callBack();//回调
    }

    /**
     * 显示输入的验证码
     */
    public void showEmptyCode() {
        tv_code1.setText("");
        tv_code2.setText("");
        tv_code3.setText("");
        tv_code4.setText("");
        codes.clear();
        et_code.setText("");
    }

    /**
     * 设置高亮颜色
     */
    /*private void setColor(){
        int color_default = Color.parseColor("#999999");
        int color_focus = Color.parseColor("#3F8EED");
        v1.setBackgroundColor(color_default);
        v2.setBackgroundColor(color_default);
        v3.setBackgroundColor(color_default);
        v4.setBackgroundColor(color_default);
        if(codes.size()==0){
            v1.setBackgroundColor(color_focus);
        }
        if(codes.size()==1){
            v2.setBackgroundColor(color_focus);
        }
        if(codes.size()==2){
            v3.setBackgroundColor(color_focus);
        }
        if(codes.size()>=3){
            v4.setBackgroundColor(color_focus);
        }
    }*/

    /**
     * 回调
     */
    private void callBack() {
        if (onInputListener == null) {
            return;
        }
        if (codes.size() == 4) {
            onInputListener.onSucess(getPhoneCode());
        } else {
            onInputListener.onInput();
        }
    }

    //定义回调
    public interface OnInputListener {
        void onSucess(String codes);

        void onInput();
    }
    private OnInputListener onInputListener;
    public void setOnInputListener(OnInputListener onInputListener) {
        this.onInputListener = onInputListener;
    }

    /**
     * 显示键盘
     */
    public void showSoftInput() {
        //显示软键盘
        if (imm != null && et_code != null) {
            et_code.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imm.showSoftInput(et_code, 0);
                }
            }, 1500);
        }
    }
    
    public void hideSoftInput() {
    	if (imm != null && et_code != null) {
    		imm.hideSoftInputFromWindow(et_code.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    	}
    }

    /**
     * 获得手机号验证码
     *
     * @return 验证码
     */
    public String getPhoneCode() {
        StringBuilder sb = new StringBuilder();
        for (String codes_item : codes) {
            sb.append(codes_item);
        }
        return sb.toString();
    }
}
