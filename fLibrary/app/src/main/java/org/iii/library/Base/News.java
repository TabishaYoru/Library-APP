package org.iii.library.Base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.Final.fromNews;
import static org.iii.library.Definition.Final.fu;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.homeBtn;
import static org.iii.library.Definition.InitViews.list;
import static org.iii.library.Definition.InitViews.personalBtn;
import static org.iii.library.Definition.InitViews.searchBtn;

public class News extends AppCompatActivity implements View.OnClickListener{

    //test
    int[] imgId = {R.drawable.pb00007671,R.drawable.pb00007674,R.drawable.shark};//圖片排序

    private View v;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        v = findViewById(R.id.news);

        b = getIntent().getExtras();
        if(b != null && b.containsKey("limit"))
            b.getString("limit");

        //取得消息
        new GetNews().execute();

        //按鈕事件
        homeBtn(v).setOnClickListener(this);
        personalBtn(v).setOnClickListener(this);
        searchBtn(v).setOnClickListener(this);

    }

    //點擊按鈕的動作
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_btn://回首頁
                NextPage(Main.class, null, false);
                break;

            case R.id.personal_btn://到個人書房
                String decide = b.getString("limit");

                if(decide != null) {
                    if (decide.equals(fu))//一般使用者
                        NextPage(Login.class, null, false);//需先登入
                    else//已登入
                        NextPage(Personal.class, null, false);
                }
                break;

            case R.id.search_btn://到圖書館藏
                NextPage(Search.class, null, false);
                break;
        }
    }


    public class GetNews extends AsyncTask<String,String,String> {
        boolean isConnect;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(News.this),"","讀取中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1010);
            isConnect = c.GET();

            if(isConnect){
                return c.getData();
            }

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(isConnect){
                setNewsData(result);//消息列表

                /**書籍條列清單*/
                GridView gvFrame = (GridView)findViewById(R.id.bookFrame);//書籍框架

                List<HashMap<String, Object>> data = new ArrayList<>();
                for(int e:imgId){
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("image",e);//加入圖片陣列
                    data.add(map);//清單裡加入陣列
                }
                //為ListView添加容器
                SimpleAdapter saBook = new SimpleAdapter(News.this,
                        data,R.layout.list,new String[]{"image"},new int[]{R.id.bImg});
                gvFrame.setAdapter(saBook);

                //點擊書籍清單的動作
                gvFrame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String isbn;
                        if(i == 0)//測試資料
                            isbn = "9789866421174";
                        else
                            isbn = "9789868665187";

                        NextPage(bDetail.class,isbn,true);

                    }
                });

                pd.dismiss();
            }

        }
    }

    //設置消息資料
    void setNewsData(String data){
        try{
            String all = new JSONObject(data).getString("readNewsAll");
            JSONArray ja = new JSONArray(all);//取得資料組
            final String[] sub = new String[ja.length()];//設置資料陣列

            for(int i =0;i<ja.length();i++){
                sub[i] = ja.getJSONObject(i).getString("n_con");//消息內容
            }

            final ArrayList<HashMap<String,Object>> array = new ArrayList<>();

            for(String e:sub){
                HashMap<String,Object> map = new HashMap<>();
                map.put("news",e);
                array.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(this,array,R.layout.news_list,
                    new String[]{"news"},new int[]{R.id.news_txv}) {
                //設置消息連結
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);

                    TextView text = (TextView) v.findViewById(R.id.news_txv);//取得評論操控元件

                    //點擊清單的動作
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NextPage(nDetail.class, sub[position], false);
                        }
                    });

                    return v;
                }
            };

            list(v).setAdapter(adapter);



        }catch(JSONException e){
            errorMessageDialog(JSON_ERROR);
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

    //功能換頁
    private void NextPage(Class link,String only,boolean from){

        if(only != null){//有攜值
            b.putString("only",only);
            if(from)//需記住啟始位置
                b.putInt("from",fromNews);
        }

        startActivity(new Intent().setClass(this,link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }

    /**返回上一頁(首頁)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Main.class,null,false);

        return false;
    }

}
