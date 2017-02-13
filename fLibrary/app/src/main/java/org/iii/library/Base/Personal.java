package org.iii.library.Base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import org.iii.library.Network.Connection;
import org.iii.library.R;

import java.util.ArrayList;
import java.util.HashMap;

import static org.iii.library.Definition.Final.SH;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.list;

public class Personal extends AppCompatActivity implements AdapterView.OnItemClickListener{


    private Bundle b;
    private ProgressDialog dialog = ProgressDialog.show(context(this),"","處理中",true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal);

        View v = findViewById(R.id.personal);//畫面：個人書房
        b = getIntent().getExtras();

        if(b != null && b.containsKey("limit"))
            b.getString("limit");

        new HistoryTask().execute(callUser());

        /*設置容器加入列表內容*/
        ArrayList<HashMap<String,Object>> array = new ArrayList<>();
        String[] List = {"歷史紀錄","借閱中書籍"};//列表內容

        //設置陣列
        for(String e : List){
            HashMap<String,Object> map = new HashMap<>();
            map.put("list",e);
            array.add(map);
        }

        //加入內容
        SimpleAdapter sa = new SimpleAdapter(this, array,R.layout.list_item,
                new String[]{"list"},new int[]{R.id.txv_item});
        list(v).setAdapter(sa);

        //點擊列表
        list(v).setOnItemClickListener(this);
    }


    private String callUser(){
        SharedPreferences preferences = getSharedPreferences("primary",MODE_PRIVATE);
        return preferences.getString("account",null);
    }

    private class HistoryTask extends AsyncTask<String,String,String>{
        private boolean isConnected;

        @Override
        protected String doInBackground(String... params) {

            String result = null;
            if(params[0] != null) {
                Connection connection = new Connection(1101, SH, params[0]);
                isConnected = connection.GET();
                result = (isConnected)? connection.getData():connection.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result){

        }
    }

    //點擊列表動作
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0://歷史紀錄
                NextPage(History.class);
                break;
            default://借閱中記錄
                NextPage(Record.class);
                break;
        }
    }

    //換頁
    private void NextPage(Class link){
        startActivity(new Intent(this, link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }



    /**返回上一頁(首頁)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Main.class);

        return false;
    }

}
