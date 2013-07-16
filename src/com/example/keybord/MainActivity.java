package com.example.keybord;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity implements View.OnTouchListener,View.OnClickListener {

    private EditText etNormal;
    private EditText etMyself;
    private EditText etPassword;
    private PopupWindow mInputWindow;
//    private Button btnHideInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etNormal = (EditText) findViewById(R.id.et_system);
        etMyself = (EditText) findViewById(R.id.et_myself);
        etMyself.setOnTouchListener(this);
    }

    


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.et_myself) {
            showMyInputMethod();
            hideSystemInputMethod();
            //不拿焦点的话 看到效果就是就是光标在上一个ET里面，而输入的密码却在这个里面
            v.requestFocus();
            return true;
        }
        return false;
    }

    //hide system
    private void hideSystemInputMethod() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        IBinder ib = getCurrentFocus().getWindowToken();
        int flag = InputMethodManager.HIDE_NOT_ALWAYS;
        im.hideSoftInputFromWindow(ib, flag);
    }

    private void showMyInputMethod() {
        if(mInputWindow == null){
            intiInputWindow();
        }
        GridView gvInput = (GridView) ((mInputWindow.getContentView()).findViewById(R.id.gv_input));
        ((Adapter)(gvInput.getAdapter())).distrubeTextOrder();

        mInputWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM,0,0);

    }

    private void intiInputWindow() {
        View inputView = getLayoutInflater().inflate(R.layout.view_input,null);
        GridView gvInput = (GridView) inputView.findViewById(R.id.gv_input);
        etPassword = (EditText)inputView.findViewById(R.id.et_password);


        Adapter adapter = new Adapter();
        gvInput.setAdapter(adapter);
//        gvInput.setOnItemClickListener(adapter);

        mInputWindow = new PopupWindow(inputView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mInputWindow.setBackgroundDrawable(new BitmapDrawable());//没有这句下面那句就不起作用，甚是怪异
        mInputWindow.setOutsideTouchable(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_input_hide:
                mInputWindow.dismiss();
                break;
        }
    }

    class Adapter extends BaseAdapter implements View.OnClickListener{

        private ArrayList<String> btnTextList;


        public Adapter(){
            btnTextList = new ArrayList<String>();

        }

        @Override
        public int getCount() {
            return btnTextList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.btn_input_item,null);
            }

            ((Button)convertView).setText(btnTextList.get(position));
            convertView.setOnClickListener(this);
            return convertView;
        }

        //每次打乱后都被remove没了，所以每次都要重新装入
        private void resetNumberList(){
            btnTextList.clear();
            String[] strs = getResources().getStringArray(R.array.input_btn_text);
            for(String str : strs){
                btnTextList.add(str);
            }
        }

        //取得随机排列的数字按钮并重新排列
        public void distrubeTextOrder(){
            resetNumberList();
            Random random = new Random();
            ArrayList<String> newList = new ArrayList<String>();
            int index;
            for(int i = 0; i < 10 ; i++){//十个数字
                index = Math.abs(random.nextInt()) % btnTextList.size();
                newList.add(btnTextList.get(index));
                btnTextList.remove(btnTextList.get(index));
            }
            newList.add(8,"删除");
            newList.add("确定");
            btnTextList = newList;
            notifyDataSetChanged();
        }


        @Override
        public void onClick(View v) {
            Button btn = (Button)v;
            String pwStr = etPassword.getText().toString();
            if("确定".equals(btn.getText())){
                etMyself.setText(etPassword.getText().toString());
                mInputWindow.dismiss();
            } else if("删除".equals(btn.getText())){

                if(!"".equals(pwStr)){
                    etPassword.setText(pwStr.substring(0,pwStr.length()-1));
                }

            } else {
                if(pwStr.length() < 6){//默认密码长度为6
                    etPassword.setText(pwStr+btn.getText());
                }

            }
        }
    }


}

