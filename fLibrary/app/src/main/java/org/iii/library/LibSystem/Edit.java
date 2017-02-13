package org.iii.library.LibSystem;

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
import android.widget.Toast;

import org.iii.library.Base.Main;
import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.iii.library.Definition.Final.F;
import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.Final.SECRET_INCONSISTENT;
import static org.iii.library.Definition.InitViews.check;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.errorCheck;
import static org.iii.library.Definition.InitViews.errorNewPw;
import static org.iii.library.Definition.InitViews.errorPw;
import static org.iii.library.Definition.InitViews.newPw;
import static org.iii.library.Definition.InitViews.pass;
import static org.iii.library.Definition.InitViews.sendBtn;

public class Edit extends AppCompatActivity implements View.OnClickListener{

    private View view;//畫面：修改密碼
    private Bundle bundle;
    private String secret;//新隱私資料
    private String user;//會員
    private String oSecret;//舊隱私資料

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        //指定畫面
        view = findViewById(R.id.edit);

        bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("limit")) {
            bundle.getString("limit");
        }

        callPreferences();

        //取得連結資料
        new GetPw().execute(getUser(), getOldSecret());

        //點擊事件
        sendBtn(view).setOnClickListener(this);

    }

    //呼叫資料
    private void callPreferences(){
        SharedPreferences preferences = getSharedPreferences("primary",MODE_PRIVATE);
        String strName = preferences.getString("account",null),
                strIdentity = preferences.getString("secret",null);

        setUser(strName);
        setOldSecret(strIdentity);

    }

    //設置
    private void setUser(String acc){
        user = acc;
    }

    //設置
    private void setOldSecret(String oSec){
        oSecret = oSec;
    }

    //取得
    private String getUser(){
        return user;
    }

    //取得
    private String getOldSecret(){
        return oSecret;
    }


    @Override
    public void onClick(View v) {
        //取得輸入值
        String old = pass(view).getText().toString(),//舊密碼
                newly = newPw(view).getText().toString(),//新密碼
                check = check(view).getText().toString(),//確認密碼
                //錯誤訊息
                errOld = errorPw(view).getText().toString(),//舊密碼
                errNew = errorNewPw(view).getText().toString(),//新密碼
                errCheck = errorCheck(view).getText().toString();//確認密碼

        //欄位判斷
        Pattern p = Pattern.compile("[^a-z0-9]");//非密碼組合格式
        Matcher mNew = p.matcher(newly);//新密碼

        //舊密碼驗證
        if(old.equals(getSecret()))
            errorPw(view).setText("");
        else if(old.equals(""))
            errorPw(view).setText("請輸入舊密碼");
        else
            errorPw(view).setText("密碼輸入錯誤");

        //新密碼驗證
        if(newly.equals(""))
            errorNewPw(view).setText("請輸入新密碼");
        else if(mNew.find() || newly.length() <8)
            errorNewPw(view).setText(R.string.e_User_Pw);
        else errorNewPw(view).setText("");

        //確認密碼驗證
        if(newly.equals(check)) errorCheck(view).setText("");
        else errorCheck(view).setText("與新密碼不符");

        //錯誤訊息全清空時
        if(errOld.isEmpty() && errNew.isEmpty() && errCheck.isEmpty()){
            setSecret(newly);
            new EditPw().execute(getUser(),getSecret());
        }

    }


    private class GetPw extends AsyncTask<String,String,String>{
        boolean isConnect;

        @Override
        protected String doInBackground(String... params) {
            Connection connect = new Connection(1001,F,params[0]);
            isConnect = connect.GET();

            if(isConnect){
                return connect.getData();
            }

            return connect.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(isConnect){
                setData(result);
            }else{
                errorPw(view).setText(SECRET_INCONSISTENT);
            }
        }
    }

    //處理資料
    private void setData(String data){
        try{
            JSONObject jo = new JSONObject(data);
            String secret = jo.getString("m_pass");
            setSecret(secret);

        }catch(JSONException e){
            errorMessageDialog(JSON_ERROR);
        }

    }

    //取得隱私資料
    public String getSecret(){
        return secret;
    }

    //設置隱私資料
    private void setSecret(String secret){
        this.secret = secret;
    }

    //修改資料
    private class EditPw extends AsyncTask<String,String,String>{
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(Edit.this),"","處理中...",true);
        }
        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1001,params[0]);
            c.PUT(params);

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            pd.dismiss();
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
        }

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

    /**返回上一頁(首頁)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            startActivity(new Intent(this, Main.class).putExtras(bundle));
            overridePendingTransition(0,0);
            finish();
        }
        return false;
    }


}
