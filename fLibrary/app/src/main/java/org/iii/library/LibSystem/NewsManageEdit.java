package org.iii.library.LibSystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import org.iii.library.Network.Connection;
import org.iii.library.R;

import static org.iii.library.Definition.Final.NEWS_LENGTH_OVER;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.editText;
import static org.iii.library.Definition.InitViews.errorMsg;
import static org.iii.library.Definition.InitViews.sendBtn;

public class NewsManageEdit extends AppCompatActivity implements View.OnClickListener{

    private View v;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_manage_edit);

        v = findViewById(R.id.news_manage_edit);
        b = getIntent().getExtras();

        if(b != null && b.containsKey("content")){
            String content = b.getString("content");//取得修改資料
            editText(v).setText(content);//寫入欄位中
        }

    }

    @Override
    protected void onStart(){
        super.onStart();

        sendBtn(v).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String id = b.getString("only");
        String content = editText(v).getText().toString();

        //驗證字數是否>100個字
        if(content.length()>100){//超過會顯示error
            errorMsg(v).setVisibility(View.VISIBLE);
            errorMsg(v).setText(NEWS_LENGTH_OVER);

        }else{//沒有則送出修改資料
            new SendEdit().execute(id,content);//執行
        }
    }

    //執行修改
    private class SendEdit extends AsyncTask<String,String,String>{
        Boolean isSuccess = false;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(NewsManageEdit.this),"","更新中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1010,params[0]);
            isSuccess = c.PUT(params);

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

            if(isSuccess){//修改成功後的動作
                NextPage(NewsManage.class);
            }
        }

    }

    //換頁
    private void NextPage(Class link){
        b.remove("content");
        startActivity(new Intent(NewsManageEdit.this,link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }


    /**返回上一頁(消息管理)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(NewsManage.class);


        return false;
    }
}
