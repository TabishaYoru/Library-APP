package org.iii.library.Base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import org.iii.library.Definition.Final;
import org.iii.library.LibSystem.Forget;
import org.iii.library.LibSystem.Register;
import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONException;
import org.json.JSONObject;

import static org.iii.library.Definition.Final.DATA_ERROR;
import static org.iii.library.Definition.Final.MEMBER_ERROR;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.forgetLink;
import static org.iii.library.Definition.InitViews.loginBtn;
import static org.iii.library.Definition.InitViews.pass;
import static org.iii.library.Definition.InitViews.registerBtn;
import static org.iii.library.Definition.InitViews.user;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private View view;//主畫面：登入
    private String account, pass;//帳號、隱私
    private String limit;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        bundle = getIntent().getExtras();

        if(bundle != null && bundle.containsKey("limit"))
            bundle.getString("limit");

        view = findViewById(R.id.login);

        //設定事件
        loginBtn(view).setOnClickListener(this);//登入按鈕
        registerBtn(view).setOnClickListener(this);//註冊按鈕
        forgetLink(view).setOnClickListener(this);//忘記密碼連結

    }

    //取得帳號資料(has UT)
    String getAccount(){
        return account;
    }

    //設置欄位內容：帳號
    private void setAccount(String user){
        account = user;
    }

    //取得隱私資料(has UT)
    String getSecret(){
        return pass;
    }

    //設置欄位內容：隱私
    private void setSecret(String secret){
        pass = secret;
    }

    //點擊後的動作
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn://登入
                //取得欄位資料
                setAccount(user(view).getText().toString());//帳號
                setSecret(pass(view).getText().toString());//隱私
                //驗證資料
                boolean isValid = valid(account, pass);
                decide(isValid);
                break;
            case R.id.register_btn://註冊畫面
                NextPage(Register.class);
                break;
            case R.id.forget_link://忘記密碼畫面
                NextPage(Forget.class);
                break;
        }
    }

    //判斷帳密是否為空值(has UT)
    boolean valid(String user, String pass){
        if(user.trim().equals("") || pass.trim().equals("")){
            //清除欄位資料
            setAccount(null);//帳號
            setSecret(null);//隱私
            return false;
        }else{
            //取得欄位資料
            setAccount(user);//帳號
            setSecret(pass);//隱私
            return true;
        }
    }

    //取得欄位資料判斷
    void decide(boolean isValid){
        if(isValid){
            new DoLogin().execute(getAccount(),getSecret());
        }else{
            user(view).setText(getAccount());
            pass(view).setText(getSecret());

            dialogMessage("請輸入帳號或密碼");
        }
    }

    //錯誤訊息對話框
    void dialogMessage(String errorMessage){
        AlertDialog.Builder exit = new AlertDialog.Builder(this);//創建訊息方塊
        exit.setMessage(errorMessage);//內文
        exit.setTitle("錯誤訊息");//標題
        //確定離開選項
        exit.setPositiveButton("確定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //dismiss為關閉dialog，Activity還會保留dialog的狀態
            }
        });
        exit.create().show();
    }

    //connect api
    class DoLogin extends AsyncTask<String,String,String>{
        boolean isLogin;
        ProgressDialog pd = null;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(context(Login.this),"","登入中...",true);
        }

        @Override
        protected String doInBackground(String... params) {

            //確認連線並取得資料
            Connection c = new Connection(1001,params[0]);
            boolean isGet = c.GET();

            if(isGet){//取得資料
                //確認身分
                isLogin = Check(c.getData(),params[0],params[1]);
            }

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(isLogin) {
                NextPage(Main.class);
            }else {
                user(view).setText("");
                pass(view).setText("");
                dialogMessage("帳號或密碼資料有誤");

            }
            pd.dismiss();

        }
    }

    //取得身分
    boolean Check(String data, String user, String pass){
        try{
            //JSON格式解析(only)
            JSONObject jo = new JSONObject(data);//json 物件
            String account = jo.getString("m_id"),//取得帳號
                    cipher = jo.getString("m_pass");//取得密碼

            if(user.equals(account) && pass.equals(cipher)){//確認完全符合
                int get = jo.getInt("m_role");//取得權限

                savePreferences(account, cipher);

                Final f = new Final(get);
                limit = f.getLimit();
                return true;

            }else{
                errorMessageDialog(MEMBER_ERROR);
            }

        }catch(JSONException e){
            errorMessageDialog(DATA_ERROR);
        }
        return false;
    }

    //儲存資料
    private void savePreferences(String... secrets){
        //取得SharedPreference設定( store 為設定檔名稱)
        SharedPreferences preferences = getSharedPreferences("primary",MODE_PRIVATE);
        preferences.edit()
                .putString("account",secrets[0])
                .putString("secret",secrets[1])
                .apply();
    }

    //錯誤訊息對話框
    private void errorMessageDialog(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle("錯誤訊息");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    //換頁
    private void NextPage(Class link){
        bundle.putString("limit",limit);
        startActivity(new Intent().setClass(this,link).putExtras(bundle));
        overridePendingTransition(0,0);
        finish();
    }

    //返回上一頁(首頁)
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //若按到返回鍵和防止重覆按到返回鍵時
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Main.class);

        return false;
    }
}
