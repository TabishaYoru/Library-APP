package org.iii.library.Base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import org.iii.library.R;

import static org.iii.library.Definition.Final.fu;

public class Logo extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);
        /**啟動app的初始畫面*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle b = new Bundle();
                b.putString("limit",fu);
                startActivity(new Intent(Logo.this,Main.class).putExtras(b));
                overridePendingTransition(0,0);
                finish();
            }
        },2000);//顯示Logo頁面2秒後，跳轉到主頁
    }

}
