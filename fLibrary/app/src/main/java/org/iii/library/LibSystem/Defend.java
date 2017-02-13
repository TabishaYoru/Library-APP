package org.iii.library.LibSystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.iii.library.Base.Main;
import org.iii.library.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Defend extends AppCompatActivity {

    private Bundle b;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defend);//系統維護列表元件

        b = getIntent().getExtras();

        if(b != null && b.containsKey("limit"))
            b.getString("limit");

        ListView defendList = (ListView)findViewById(R.id.defend_list);

        String[] List = {"帳號管理","消息管理","書籍管理","借出清單","書籍盤點"};//列表內容

        /*設置容器加入列表內容*/
        ArrayList<HashMap<String,Object>> list = new ArrayList<>();

        for(String e:List){
            HashMap<String,Object> map = new HashMap<>();
            map.put("defend",e);
            list.add(map);
        }

        SimpleAdapter sa = new SimpleAdapter(this, list,R.layout.list_item,
                new String[]{"defend"},new int[]{R.id.txv_item});
        defendList.setAdapter(sa);

        /*點擊列表的動作*/
        defendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NextPage(position);
            }
        });
    }

    //功能換頁
    private void NextPage(int position){

        Class cls = null;

        switch(position){
            case 0://帳號管理
                cls = IdManage.class;
                break;
            case 1://消息管理
                cls = NewsManage.class;
                break;
            case 2://書籍管理
                cls = BookManage.class;
                break;
            case 3://借出清單
                cls = BorrowList.class;
                break;
            case 4://書籍盤點
                cls = BookStock.class;
                break;
            case 5://首頁
                cls = Main.class;
                break;
        }

        startActivity(new Intent(this,cls).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }


    /**返回上一頁(首頁)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(5);
        return false;
    }

}
