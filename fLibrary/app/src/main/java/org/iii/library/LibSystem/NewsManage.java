package org.iii.library.LibSystem;

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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.deleteBtn;
import static org.iii.library.Definition.InitViews.editBtn;
import static org.iii.library.Definition.InitViews.list;
import static org.iii.library.Definition.InitViews.newBtn;

public class NewsManage extends AppCompatActivity implements View.OnClickListener{

    private View v;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_manage);

        v = findViewById(R.id.news_manage);
        b = getIntent().getExtras();

        if(b != null && b.containsKey("limit"))
            b.getString("limit");

        //新增消息按鈕
        newBtn(v).setOnClickListener(this);

        new GetNews().execute();//執行
    }

    //背景作業
    public class GetNews extends AsyncTask<String,String,String> {
        boolean isGet;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(context(NewsManage.this),"","讀取中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1010);
            isGet = c.GET();

            if(isGet){
                return c.getData();
            }

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(isGet){
                setNewsData(result);
            }

            pd.dismiss();
        }
    }

    //設置消息資料
    void setNewsData(String news){
        try{
            String all = new JSONObject(news).getString("readNewsAll");
            JSONArray ja = new JSONArray(all);//取得資料組
            //取得資料陣列
            final String[] only = new String[ja.length()];//id
            final String[] sub = new String[ja.length()];//con

            for(int i =0;i<ja.length();i++){
                only[i] = ja.getJSONObject(i).getString("n_id");
                sub[i] = ja.getJSONObject(i).getString("n_con");
            }

            //設置陣列
            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
            for(String e:sub){
                HashMap<String,Object> map = new HashMap<>();
                map.put("content",e);//名字
                list.add(map);
            }

            SimpleAdapter sa = new SimpleAdapter(NewsManage.this,list,R.layout.news_manage_list,
                    new String[]{"content"}, new int[]{R.id.content}){

                /**設置按鈕*/
                @Override
                public View getView(final int position, View convert, ViewGroup parent){
                    View v = super.getView(position,convert,parent);

                    //點擊變更按鈕的動作
                    editBtn(v).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg) {
                            b.putString("only",only[position]);//單筆資料
                            b.putString("content",sub[position]);//內容

                            NextPage(NewsManageEdit.class);

                        }
                    });

                    //點擊刪除按鈕的動作
                    deleteBtn(v).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder del = new AlertDialog.Builder(NewsManage.this);
                            del.setMessage("確定要刪除此消息嗎？");
                            del.setTitle("刪除");
                            del.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //確定刪除
                                    new Del().execute(only[position]);
                                }
                            });

                            del.setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            del.create().show();
                        }
                    });

                    return v;
                }

            };

            list(v).setAdapter(sa);

        }catch(JSONException e){
            errorMessageDialog("DATA ERROR");
        }

    }

    //Http Delete Request
    private class Del extends AsyncTask<String,String,String>{
        boolean isDel;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(NewsManage.this),"","刪除中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0] != null) {
                Connection c = new Connection(1010, params[0]);
                isDel = c.DELETE();
                return c.getMessage();

            }else{
                errorMessageDialog("未執行");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            pd.dismiss();

            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            NextPage(null);

        }

    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.new_btn:
                NextPage(NewsManageAdd.class);
                break;
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

    //換頁
    private void NextPage(Class link){
        if(link == null){
            finish();
            overridePendingTransition(0,0);
            startActivity(new Intent(getIntent()).putExtras(b));
            overridePendingTransition(0,0);
        }else{
            startActivity(new Intent(this,link).putExtras(b));
            overridePendingTransition(0,0);
            finish();
        }
    }

    /**返回上一頁(系統維護)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Defend.class);

        return false;
    }
}
