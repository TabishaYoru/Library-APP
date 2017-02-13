package org.iii.library.Base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import org.iii.library.R;

import static org.iii.library.Definition.InitViews.homeBtn;
import static org.iii.library.Definition.InitViews.personalBtn;
import static org.iii.library.Definition.InitViews.searchBtn;
import static org.iii.library.Definition.InitViews.text;

public class nDetail extends AppCompatActivity implements View.OnClickListener{

    private Bundle b;
    View v;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.n_detail);

        v = findViewById(R.id.news_detail);
        b = getIntent().getExtras();

        if(b != null && b.containsKey("only"))
            content = b.getString("only");

        text(v).setText(content);


        //按鈕事件
        homeBtn(v).setOnClickListener(this);
        personalBtn(v).setOnClickListener(this);
        searchBtn(v).setOnClickListener(this);


    }

    //點擊按鈕的動作
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.homeBtn://首頁
                NextPage(Main.class);
                break;
            case R.id.personalBtn://到個人書房
                NextPage(Personal.class);
                break;
            case R.id.searchBtn://到圖書館藏
                NextPage(Search.class);
                break;
        }
    }

    //換頁
    private void NextPage(Class link){
        b.remove("only");
        startActivity(new Intent().setClass(this,link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }

    /**返回上一頁(最新消息)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            NextPage(News.class);
        }
        return false;
    }

}
