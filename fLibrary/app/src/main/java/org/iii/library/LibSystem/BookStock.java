package org.iii.library.LibSystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import org.iii.library.R;

public class BookStock extends AppCompatActivity {

    //private View view;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_stock);

        //指定畫面：書籍盤點
        //view = findViewById(R.id.book_stock);

        //取得攜值
        bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("limit")){
            bundle.getString("limit");
        }

    }




    private void nextPage(Class link){
        startActivity(new Intent(this, link).putExtras(bundle));
        overridePendingTransition(0,0);
        finish();
    }

    /**返回上一頁(書籍清單)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            nextPage(Defend.class);
        }
        return false;
    }
}
