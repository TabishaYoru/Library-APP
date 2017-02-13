package org.iii.library.Base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import org.iii.library.LibSystem.Defend;
import org.iii.library.LibSystem.Edit;
import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.Final.ad;
import static org.iii.library.Definition.Final.fa;
import static org.iii.library.Definition.Final.fm;
import static org.iii.library.Definition.Final.fu;
import static org.iii.library.Definition.Final.guest;
import static org.iii.library.Definition.Final.mem;
import static org.iii.library.Definition.InitViews.baseBtn;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.marquee;

public class Main extends AppCompatActivity {

    private View v;//主畫面：首頁
    private Bundle b;

    private String decide;

    private boolean isHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //指定畫面
        v = findViewById(R.id.main);

        b = getIntent().getExtras();

        if(b !=null && b.containsKey("limit")) //如果有攜帶值
            decide = b.getString("limit");


        //設置跑馬燈
        new Marquee().execute();

        //設置功能按鈕
        setButton(decide);

    }

    public class Marquee extends AsyncTask<String,String,String> {
        boolean isConnect = false;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(Main.this),"","讀取中，請稍後...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1010);//連線
            isConnect = c.GET();//取得消息資料

            return (isConnect) ? c.getData() : c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(isConnect){
                setMarquee(result);//設定跑馬燈
            }else{
                marquee(v).setText(result);
            }

            pd.dismiss();
        }
    }

    //設置跑馬燈(維修中)
    void setMarquee(String news){
        if(news != null){
            try{
                String all = new JSONObject(news).getString("readNewsAll");
                JSONArray ja = new JSONArray(all);//取得資料組
                final String[] sub = new String[ja.length()];//設置資料陣列

                for(int i = 0;i <ja.length();i++){
                    sub[i] = ja.getJSONObject(i).getString("n_con");
                }

                marquee(v).setSelected(true);
                marquee(v).setText(sub[0]);

                isHandle = true;

            }catch(JSONException e){
                marquee(v).setText(JSON_ERROR);
            }
        }else{
            marquee(v).setText("解析有誤");
        }

        isHandle = false;
    }


    boolean getIsHandle(){
        return isHandle;
    }

    //維修中
    /*private String getNews(String... news){
        String one = "";

        marquee(main).setText(news[0]);

        //每幾秒更換一次資訊

        return one;
    }*/


    //按鈕清單
    private void setButton(final String role){
        String[] user;
        int column;

        //判斷權限
        if(!role.equals(fa)){
            if(!role.equals(fm)){
                user = guest;
                column = 2;
            }else{
                user = mem;
                column = 3;
            }
        }else{
            user = ad;
            column = 3;
        }

        //list添加內容
        List<Map<String,Object>> list = new ArrayList<>();

        for (String e:user) {
            Map<String, Object> map = new HashMap<>();
            map.put("image",e);//按鈕名稱
            list.add(map);//清單裡加入陣列
        }

        //取得元件
        GridView gvBtn = (GridView)findViewById(R.id.gvBtn);

        //將按鈕清單放入grid容器裡
        SimpleAdapter sa = new SimpleAdapter(Main.this, list, R.layout.img_btn,
                new String[] {"image"}, new int[] {R.id.btn}){

            /**設置按鈕*/
            @Override
            public View getView(final int position, View convertView, ViewGroup parent){
                View v = super.getView(position,convertView,parent);

                //點擊按鈕的動作
                baseBtn(v).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg) {
                        getButton(role,position);

                    }
                });

                return v;
            }
        };
        gvBtn.setAdapter(sa);
        gvBtn.setNumColumns(column);
    }

    //取得按鈕
    private void getButton(String decide,int position){
        //點擊按鈕動作
        switch(position){
            case 0://最新消息
                NextPage(News.class);
                break;
            case 1://查詢館藏
                NextPage(Search.class);
                break;
            case 2:
                if(decide.equals(fu))//登入
                    NextPage(Login.class);
                else//登出
                    NextPage(Main.class);
                break;
            case 3://系統設定
                NextPage(System.class);
                break;
            case 4://個人書房
                if(!decide.equals(fu))
                    NextPage(Personal.class);
                break;
            case 5://修改密碼
                if(!decide.equals(fu))
                    NextPage(Edit.class);
                break;
            case 6://系統維護
                if(decide.equals(fa))
                    NextPage(Defend.class);
                break;
        }

    }

    //換頁
    private void NextPage(Class link){
        if(link == Main.class){//登出
            b.putString("limit",fu);
        }

        startActivity(new Intent().setClass(this,link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }


    /**彈出「是否離開」的訊息視窗*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            dialog();//彈出視窗
        return false;
    }

    //彈出確認視窗
    public void dialog() {
        AlertDialog.Builder exit = new AlertDialog.Builder(Main.this);//創建訊息方塊
        exit.setMessage("要離開了嗎？");//內文
        exit.setTitle("啊，碰到返回鍵了！");//標題
        //確定離開選項
        exit.setPositiveButton("沒錯！", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //dismiss為關閉dialog，Activity還會保留dialog的狀態
                clearPreferences();
                finish();//關閉Activity
            }
        });
        //取消離開選項
        exit.setNegativeButton("不是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//關閉dialog
            }
        });
        exit.create().show();
    }

    //清除SharedPreference設定
    private void clearPreferences(){
        SharedPreferences settings = getSharedPreferences("primary",MODE_PRIVATE);
        settings.edit()
                .clear()
                .apply();

        settings = getSharedPreferences("book",MODE_PRIVATE);
        settings.edit()
                .clear()
                .apply();
    }
}
