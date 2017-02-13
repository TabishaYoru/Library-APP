package org.iii.library.Base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SimpleAdapter;

import org.iii.library.R;

import java.util.ArrayList;
import java.util.HashMap;

import static org.iii.library.Definition.InitViews.list;

public class Discuss extends AppCompatActivity {

    //test
    String[] user = {"test1","test2","test3","test4","test5","test6","test7","test8","test9"};
    String[] content = {"good!","not bad!","so-so...","good!","not bad!","so-so...","good!","not bad!","so-so..."};

    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discuss);

        View v = findViewById(R.id.discuss);

        b = getIntent().getExtras();
        if(b != null && b.containsKey("only"))
            b.containsKey("only");

        //execute (fixing)
        //new getComments().execute(isbn);


        //顯示清單
        list(v).setAdapter(show());

    }
/*
    private class getComments extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String result){

        }
    }*/

    //建立資料陣列
    private SimpleAdapter show(){

        ArrayList<HashMap<String,Object>> list = new ArrayList<>();

        for(int i = 0;i<user.length;i++){
            HashMap<String,Object> map = new HashMap<>();
            //map.put("img",img[i]);
            map.put("name",user[i]);
            map.put("content",content[i]);
            list.add(map);//加入進list陣列裡
        }

        return  new SimpleAdapter(this,list,R.layout.comment_list,
                new String[]{"name","content"},
                new int[]{R.id.user,R.id.content});
    }

    //功能：攜值換頁
    private void NextPage(Class link){
        b.remove("comment");//移除
        startActivity(new Intent().setClass(this,link).putExtras(b));
        overridePendingTransition(0, 0);
        finish();
    }

    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(bDetail.class);

        return false;
    }

}
