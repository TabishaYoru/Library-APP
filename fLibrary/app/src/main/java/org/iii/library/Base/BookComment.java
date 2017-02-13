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
import android.widget.Toast;

import org.iii.library.Network.Connection;
import org.iii.library.R;

import static org.iii.library.Definition.Final.fromHistory;
import static org.iii.library.Definition.Final.fromRecord;
import static org.iii.library.Definition.InitViews.bookName;
import static org.iii.library.Definition.InitViews.commentText;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.errorMsg;
import static org.iii.library.Definition.InitViews.sendBtn;

public class BookComment extends AppCompatActivity implements View.OnClickListener{

    View v;//畫面：書籍評論
    Bundle b;

    private String user;
    private String book;
    private String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_comment);

        v = findViewById(R.id.book_comment);//指定畫面

        b = getIntent().getExtras();

        if(b != null && b.containsKey("limit")) {
            b.getString("limit");
        }

        //呼叫資料
        callBook();
        callUser();

        //點擊評論按鈕的動作
        sendBtn(v).setOnClickListener(this);

    }

    //呼叫儲存書籍資料
    private void callBook(){
        SharedPreferences preferences = getSharedPreferences("book",MODE_PRIVATE);
        setBook(preferences.getString("book",null));
        setBookName(preferences.getString("bookName",null));
    }

    private void callUser(){
        SharedPreferences preferences = getSharedPreferences("primary",MODE_PRIVATE);
        setUser(preferences.getString("account",null));
    }

    //設置書籍編號
    private void setBook(String book){
        this.book = book;
    }

    //取得書籍編號
    private String getBook(){
        return book;
    }

    //設置書籍名稱
    private void setBookName(String name){
        bookName(v).setText(name);
    }

    //設置會員
    private void setUser(String user){
        this.user = user;
    }

    //取得會員
    private String getUser(){
        return user;
    }

    @Override
    public void onClick(View view) {
        setComment();
        boolean isCorrected = decide();

        if(isCorrected){//驗證成功，新增評論
            new CommentTask().execute(getBook(), getComment(), getUser());
        }
    }

    //設置評論內容
    private void setComment(){
        comment = commentText(v).getText().toString();
    }

    //取得評論內容
    private String getComment(){
        return comment;
    }

    //驗證評論內容
    private boolean decide(){
        if(getComment().length()<=100){
            errorMsg(v).setVisibility(View.INVISIBLE);
            return true;
        }else{
            errorMsg(v).setVisibility(View.VISIBLE);
        }
        return false;
    }

    //新增書籍評論
    private class CommentTask extends AsyncTask<String,String,String>{
        boolean isConnect;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(BookComment.this),"","新增中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection connect = new Connection(1100);//紀錄
            isConnect = connect.POST(params);

            String result;
            result = (isConnect)?connect.getData():connect.getMessage();

            return result;
        }

        @Override
        protected void onPostExecute(String result){
            pd.dismiss();

            if(isConnect) {//連結成功，處理資料
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            }else{//連結失敗，顯示錯誤訊息
                errorMessageDialog(result);
            }
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

    @Override
    public void onStop(){
        super.onStop();

        SharedPreferences preferences = getSharedPreferences("book",MODE_PRIVATE);
        preferences.edit()
                .remove("book")
                .remove("bookName")
                .apply();
    }

    //換頁
    private void NextPage(Class link){
        startActivity(new Intent(this,link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }


    /**返回上一頁*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if (b.getInt("fromRecord") == fromRecord) {//從借閱中記錄進來
                NextPage(Record.class);
            }else if(b.getInt("fromHistory") == fromHistory)//從歷史記錄進來
                NextPage(History.class);
        }
        return false;
    }

}
