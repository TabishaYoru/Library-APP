package org.iii.library.LibSystem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.iii.library.Network.Connection;
import org.iii.library.R;

import static org.iii.library.Definition.Final.NEWS_LENGTH_OVER;
import static org.iii.library.Definition.InitViews.sendBtn;

public class NewsManageAdd extends AppCompatActivity {

    private EditText content;//消息內容
    private TextView error;//錯誤訊息

    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_manage_add);


        View v = findViewById(R.id.news_manage_add);
        b = getIntent().getExtras();

        if(b != null && b.containsKey("limit"))
            b.getString("limit");

        initViews();

        //點擊按鈕的動作
        sendBtn(v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String news = content.getText().toString();

                //消息欄位驗證
                if(news.length()>100){
                    error.setVisibility(View.VISIBLE);
                    error.setText(NEWS_LENGTH_OVER);
                }
                else{
                    new sendPost().execute(news);
                }

            }
        });

    }

    //Http POST request
    private class sendPost extends AsyncTask<String,String,String>{

        boolean isSuccess;

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1010);
            isSuccess = c.POST(params);

            return c.getMessage();

        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

            if(isSuccess){
                startActivity(new Intent(NewsManageAdd.this,NewsManage.class));
                overridePendingTransition(0, 0);
                finish();
            }
        }
    }


    /**初始化元件*/
    private void initViews(){
        content = (EditText)findViewById(R.id.comment_text);//消息內容
        error = (TextView)findViewById(R.id.news_add_error);//錯誤訊息
    }

    /**返回上一頁(消息管理)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            startActivity(new Intent(NewsManageAdd.this,NewsManage.class).putExtras(b));
            finish();
        }

        return false;
    }
}
