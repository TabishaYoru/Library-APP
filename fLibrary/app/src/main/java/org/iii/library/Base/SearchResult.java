package org.iii.library.Base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import org.iii.library.R;

import java.util.ArrayList;
import java.util.HashMap;

import static org.iii.library.Definition.InitViews.list;

public class SearchResult extends AppCompatActivity implements AdapterView.OnItemClickListener{

    //test

    private String[] book = {"9789868665187","9789868665187","9789868665187"};
    String[] name = {"name1","name2","name3"},
            auth = {"auth1","auth2","auth3"},
            state = {"測試中","測試中","測試中"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        View result = findViewById(R.id.search_result);
/*
        Bundle b = getIntent().getExtras();

        book = b.getStringArray("isbn");

        //宣告攜帶值
        String[] img = b.getStringArray("img"),
                name = b.getStringArray("name"),
                auth = b.getStringArray("auth"),
                state = b.getStringArray("state");
*/
        //取得清單
        SimpleAdapter sa = show(/*img,*/name,auth,state);

        //顯示清單
        list(result).setAdapter(sa);

        list(result).setOnItemClickListener(this);
    }


    //建立資料陣列
    private SimpleAdapter show(/*String[] img,*/ String[] name, String[] auth, String[] state){

        ArrayList<HashMap<String,Object>> list = new ArrayList<>();

        for(int i = 0;i<name.length;i++){
            HashMap<String,Object> map = new HashMap<>();
            //map.put("img",img[i]);
            map.put("name",name[i]);
            map.put("auth",auth[i]);
            map.put("state",state[i]);
            list.add(map);//加入進list陣列裡
        }

        return  new SimpleAdapter(this,list,R.layout.result_list,
                new String[]{/*"img",*/"name","auth","state"},
                new int[]{/*R.id.image,*/R.id.book_name,R.id.auth,R.id.state});
    }


    //點擊後的動作
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        String click = book[position];
        NextPage(bDetail.class,click);
    }







    //功能：攜值換頁
    private void NextPage(Class link,String put){
        if(put == null){
            startActivity(new Intent().setClass(SearchResult.this, link));
            overridePendingTransition(0,0);
            finish();
        }else {
            Bundle b = new Bundle();
            b.putString("book", put);
            startActivity(new Intent().setClass(SearchResult.this, link).putExtras(b));
            overridePendingTransition(0, 0);
        }
    }

    /**返回上一頁(查詢館藏)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Search.class,null);
        return false;
    }
}
