package org.iii.library.LibSystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import org.iii.library.Base.Login;
import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.iii.library.Definition.Final.ACC_EMPTY;
import static org.iii.library.Definition.Final.DATA_ERROR;
import static org.iii.library.Definition.Final.DATA_INPUT_ERROR;
import static org.iii.library.Definition.Final.EMAIL_EMPTY;
import static org.iii.library.Definition.Final.EMAIL_FAL;
import static org.iii.library.Definition.Final.MAIL_ERROR;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.email;
import static org.iii.library.Definition.InitViews.errorEmail;
import static org.iii.library.Definition.InitViews.errorMsg;
import static org.iii.library.Definition.InitViews.errorUser;
import static org.iii.library.Definition.InitViews.sendBtn;
import static org.iii.library.Definition.InitViews.user;


/**2016/12/02 開發者：Kitty
 * 問題：只能使用內網傳遞，使用外網可能無法連結內網資料
 * */

public class Forget extends AppCompatActivity implements View.OnClickListener{

    private View view;
    private Bundle bundle;

    private Session session;
    ProgressDialog dialog = null;

    private String user,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);

        //主畫面：忘記密碼
        view = findViewById(R.id.forget);

        //確認攜值
        bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("limit"))
            bundle.getString("limit");

    }

    @Override
    protected void onStart(){
        super.onStart();

        sendBtn(view).setOnClickListener(this);//送出按鈕監聽
    }


    //取得帳號欄位資料
    public String getUser(){
        return user;
    }

    //設置帳號欄位資料
    private void setUser(String user){
        this.user = user;
    }

    //取得信箱欄位資料
    public String getEmail(){
        return email;
    }

    //設置信箱欄位資料
    private void setEmail(String email){
        this.email = email;
    }

    //點擊事件
    @Override
    public void onClick(View v) {

        //設置欄位值
        setUser(user(view).getText().toString());//帳號資料
        setEmail(email(view).getText().toString());//信箱資料

        //基本驗證
        boolean isCheckUser = checkUser(getUser()),//驗證帳號
                isCheckEmail = checkEmail(getEmail());//驗證信箱

        setErrorUser(isCheckUser);
        setErrorEmail(isCheckEmail);

        if(isCheckUser && isCheckEmail){//若為true才執行(has UT)
            new GetData().execute(getUser(),getEmail());//背景連線
        }

    }

    //驗證欄位：帳號(has UT)
    boolean checkUser(String user){
        //判斷是否有值
        return user.length() > 1;
    }

    //驗證欄位：信箱(has UT)
    boolean checkEmail(String email){
        TextView ee = errorEmail(view);

        //電子信箱 正規表達式
        String reg = "\\b[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}\\b";

        //比對資料與模範
        Matcher m = Pattern.compile(reg).matcher(email);//pattern 模範，matcher 資料

        //驗證 email
        if(m.find()) {
            ee.setText("");
            return true;
        }else if(email.equals("")){
            ee.setText(EMAIL_EMPTY);
        }else{
            ee.setText(EMAIL_FAL);
        }

        return false;
    }

    //設置帳號欄位錯誤訊息
    private void setErrorUser(boolean isError){
        if (!isError){
            errorUser(view).setText(ACC_EMPTY);
        }else{
            errorUser(view).setText("");
        }
    }

    //設置信箱欄位錯誤訊息
    private void setErrorEmail(boolean isError){
        if (!isError){
            setUser(null);
            user(view).setText(getUser());
            errorEmail(view).setText(EMAIL_EMPTY);
        }else{
            errorEmail(view).setText("");
        }
    }

    //取得帳戶資料
    private class GetData extends AsyncTask<String,String,String>{
        boolean isConform = false;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //進度條顯示：執行中
            dialog = ProgressDialog.show(context(Forget.this),"","資料檢查中…",true);
        }

        @Override
        protected String doInBackground(String... params) {
            //連線
            Connection c = new Connection(1001,params[0]);
            boolean isConnect = c.GET();

            if(isConnect){//未取得資料
                isConform = Conform(c.getData(), params[0], params[1]);//判斷
            }

            return c.getMessage();

        }

        @Override
        protected void onPostExecute(String result){
            //關閉進度框
            dialog.dismiss();

            if(isConform) {
                String user = user(view).getText().toString(),
                        email = email(view).getText().toString();

                SendEmail(user, email);
            }

            errorMsg(view).setText(result);
        }
    }

    //確認帳戶資料
    boolean Conform(String data, String user, String email){
        try{
            //JSON格式解析(only)
            JSONObject jo = new JSONObject(data);//json 物件
            String id = jo.getString("m_id"),//取得帳號
                    mail = jo.getString("m_email");//取得電子信箱

            if(user.equals(id) && email.equals(mail)){//確認完全符合
                return true;

            }else{
                errorMessageDialog(DATA_INPUT_ERROR);
            }

        }catch(JSONException e){
            errorMessageDialog(DATA_ERROR);
        }
        return false;
    }

    //寄信
    void SendEmail(String user,String email){
        /**解釋：隱藏的部分為連結Gmail伺服器之用途*/

        String smtp = "mail.iii.org.tw";//內
        //String gmail = "smtp.gmail.com";//Gmail

        //設定伺服器
        Properties pros = new Properties();
        pros.put("mail.smtp.host",smtp/*gmail*/);
        /*pros.put("mail.smtp.socketFactory.port","465");
        pros.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        pros.put("mail.smtp.auth","true");
        pros.put("mail.smtp.port","465");*/

        //驗證
        session = Session.getDefaultInstance(pros, null/**new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("thunderwavela@gmail.com","thunderwave");
            }
        }*/);

        new RetrieveFeedTask().execute(email,user,setRandom());

    }

    //設置亂碼
    private String setRandom(){
        return "1234567test";
    }

    //寄出信件設定
    private class RetrieveFeedTask extends AsyncTask<String,Void,String>{
        boolean isSend;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //進度條顯示：執行中
            dialog = ProgressDialog.show(context(Forget.this),"","寄信中...",true);

        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1001,params[1]);
            String[] group = {params[1],params[2]};
            boolean isPut = c.PUT(group);

            if(isPut){
                String rec = params[0],
                        subject = "來自圖書管理APP的通知",
                        textMsg = params[1]+" 您好！\n請您使用此亂碼作登入！\n\n" +
                                "密碼："+params[2]+"。\n\n" +
                                "請注意！\n" +
                                "該亂碼為你的新密碼，若要修改請至〈修改密碼〉進行變更。";

                try{
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("libraryapp@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rec));
                    message.setSubject(subject);
                    message.setContent(textMsg,"text/html; charset=utf-8");
                    Transport.send(message);

                    isSend = true;
                    return "寄出成功，請至信箱查看！";

                }catch(MessagingException e){
                    errorMessageDialog(MAIL_ERROR);
                }

                isSend = false;
            }

            return "error";
        }

        @Override
        protected void onPostExecute(String result){
            //關閉進度框
            dialog.dismiss();

            //錯誤訊息顏色
            errorMsg(view).setTextColor(Color.RED);

            if(isSend){//成功寄出才變綠色
                errorMsg(view).setTextColor(Color.rgb(50,220,50));
            }

            //顯示訊息
            errorMsg(view).setText(result);
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

    //換頁
    private void NextPage(Class link){
        startActivity(new Intent().setClass(Forget.this,link).putExtras(bundle));
        overridePendingTransition(0,0);
        finish();
    }

    /**返回上一頁(登入)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Login.class);
        return false;
    }
}
