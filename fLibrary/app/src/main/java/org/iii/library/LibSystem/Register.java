package org.iii.library.LibSystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.iii.library.Base.Login;
import org.iii.library.Network.Connection;
import org.iii.library.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.iii.library.Definition.Final.EMAIL_EMPTY;
import static org.iii.library.Definition.Final.EMAIL_FAL;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.sendBtn;

public class Register extends AppCompatActivity implements View.OnClickListener{


    /**正規式判斷*/
    Pattern pName = Pattern.compile("[\\u4e00-\\u9fa5]{2,5}");//中文格式
    Pattern p = Pattern.compile("^[0-9]{1,}[a-z]{1,}$|^[a-z]{1,}[0-9]{1,}$");//帳號、密碼組合格式
    Pattern pEmail = Pattern.compile("\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}\\b");//電子信箱格式

    /**元件*/
    private EditText user,name,pw,check,email;//帳號、密碼、確認密碼、信箱欄位
    private TextView errUser,errName,errPw,errCheck,errEmail;//帳號、密碼、確認密碼、信箱錯誤訊息

    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        View v = findViewById(R.id.register);//取得畫面

        b = getIntent().getExtras();
        if(b != null && b.containsKey("limit"))
            b.getString("limit");


        setValues();//取得元件
        sendBtn(v).setOnClickListener(this);//監聽元件：送出按鈕
    }

    /**取得元件*/
    private void setValues() {
        /**EditText(輸入欄位)*/
        user = (EditText)findViewById(R.id.rUser);//帳號
        name = (EditText)findViewById(R.id.rName);//姓名
        pw = (EditText)findViewById(R.id.rPassword);//密碼
        check = (EditText)findViewById(R.id.rCheckPw);//確認密碼
        email = (EditText)findViewById(R.id.rEmail);//信箱

        /**TextView(錯誤訊息)*/
        errName = (TextView)findViewById(R.id.errorName);//姓名
        errUser = (TextView)findViewById(R.id.errorUser);//帳號
        errPw = (TextView)findViewById(R.id.errorPw);//密碼
        errCheck = (TextView)findViewById(R.id.errCheck);//確認密碼
        errEmail = (TextView)findViewById(R.id.errorEmail);//信箱

    }

    /**點擊按鈕的動作*/
    @Override
    public void onClick(View view){

        /**取得輸入值*/
        String
                su = user.getText().toString(),//帳號
                sn = name.getText().toString(),//姓名
                sp = pw.getText().toString(),//密碼
                sc = check.getText().toString(),//確認密碼
                se = email.getText().toString();//Email

        /**判斷欄位格式*/
        //Matcher mUser = p.matcher(su);//帳號格式
        Matcher mName = pName.matcher(sn);//姓名格式
        Matcher mPw = p.matcher(sp);//密碼格式
        Matcher mEmail = pEmail.matcher(se);//email格式

        /**判斷*/
        DecideAccount(su);

            /*帳號
            if(mUser.find() && su.length() > 7){
                errUser.setText("");
                errUser.setVisibility(View.GONE);
            }
            else if(su.trim().equals("")) {//有空格
                errUser.setVisibility(View.VISIBLE);
                errUser.setText("請輸入帳號");
            }
            else {//其他字元、大寫英文
                errUser.setVisibility(View.VISIBLE);
                errUser.setText(R.string.e_User_Pw);
            }*/

        //姓名
        if(mName.find() && sn.length() > 1) {
            errName.setText("");
            errName.setVisibility(View.GONE);
        }
        else {//中字以外的字元
            errName.setVisibility(View.VISIBLE);
            errName.setText("請輸入中文，2~5個字");
        }
        //密碼
        if(mPw.find() && sp.length() > 7) {
            errPw.setText("");
            errPw.setVisibility(View.GONE);
        }
        else if(sp.trim().equals("")){//空格
            errPw.setVisibility(View.VISIBLE);
            errPw.setText("請輸入密碼");
        }
        else {//其他字元、大寫英文
            errPw.setVisibility(View.VISIBLE);
            errPw.setText(R.string.e_User_Pw);
        }
        //確認密碼
        if(sc.equals(sp) && sc.length() > 1){
            errCheck.setText("");
            errCheck.setVisibility(View.GONE);
        }
        else if(sc.equals("")){//空格
            errCheck.setVisibility(View.VISIBLE);
            errCheck.setText("請輸入確認密碼");
        }
        else {//與密碼不同
            errCheck.setVisibility(View.VISIBLE);
            errCheck.setText("與密碼不符");
        }
        //電子信箱
        if(mEmail.find()) {
            errEmail.setText("");
            errEmail.setVisibility(View.GONE);
        }
        else if(se.equals("")){//空格
            errEmail.setVisibility(View.VISIBLE);
            errEmail.setText(EMAIL_EMPTY);
        }
        else {
            errEmail.setVisibility(View.VISIBLE);
            errEmail.setText(EMAIL_FAL);
        }
        /**取得錯誤訊息*/
        String
                seu = errUser.getText().toString(),//帳號
                sen = errName.getText().toString(),//姓名
                sep = errPw.getText().toString(),//密碼
                sec = errCheck.getText().toString(),//確認密碼
                see = errEmail.getText().toString();//Email

        //確認輸入資料無誤，傳送至資料庫
        if(seu.equals("") && sep.equals("") && sen.equals("") && sec.equals("") && see.equals("")){
            Api post = new Api();
            post.execute(
                    user.getText().toString(),
                    pw.getText().toString(),
                    name.getText().toString(),
                    email.getText().toString());
        }
    }

    public String DecideAccount(String user){
        String r ;

        /**判斷欄位格式*/
        Matcher mUser = p.matcher(user);//帳號格式

        /**判斷*/
        //帳號
        if(mUser.find() && user.length() > 7){
            r = "pass";
            //errUser.setText("");
            //errUser.setVisibility(View.GONE);
        }
        else if(user.trim().equals("")) {//有空格
            r = "請輸入帳號";
            //errUser.setVisibility(View.VISIBLE);
            //errUser.setText("請輸入帳號");
        }
        else {//其他字元、大寫英文
            r = "請輸入小寫英文與數字的組合，\n長度為8~12個字";
            //errUser.setVisibility(View.VISIBLE);
            //errUser.setText(R.string.e_User_Pw);
        }

        return r;
    }



    //連結API
    private class Api extends AsyncTask<String,String,String>{

        boolean isSuccess;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(Register.this),"","註冊中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1001);
            isSuccess = c.POST(params);

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String r){
            pd.dismiss();

            Toast.makeText(getApplicationContext(),r,Toast.LENGTH_SHORT).show();

            if(isSuccess){
                NextPage(Login.class);
            }
        }
    }

    //換頁
    private void NextPage(Class link){
        startActivity(new Intent(this, link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }

    /**返回上一頁(登入)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            NextPage(Login.class);
        }
        return false;
    }
}
