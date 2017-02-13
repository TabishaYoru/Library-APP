package org.iii.library.Base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;

import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.iii.library.Definition.Final.IBA;
import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.Final.fromRecord;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.list;
import static org.iii.library.Definition.InitViews.noData;

public class Record extends AppCompatActivity {

    private View v;
    private Bundle b;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        v = findViewById(R.id.record);

        b = getIntent().getExtras();

        if(b != null && b.containsKey("limit")) {
            b.getString("limit");
        }

        //呼叫存取資料
        setUser();

        //取得借閱紀錄
        new GetRecord().execute(getUser());

    }

    //設置帳號
    private void setUser(){
        SharedPreferences preferences = getSharedPreferences("primary",MODE_PRIVATE);
        user = preferences.getString("account",null);
    }

    //取得帳號
    private String getUser(){
        return user;
    }

    //取得紀錄
    protected class GetRecord extends AsyncTask<String,String,String>{
        boolean isConnect;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(Record.this),"","載入中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection connect = new Connection(1101,IBA,params[0]);//紀錄
            isConnect = connect.GET();

            String result;
            result = (isConnect)?connect.getData():connect.getMessage();

            return result;
        }

        @Override
        protected void onPostExecute(String result){
            pd.dismiss();

            if(isConnect) {//連結成功，處理資料
                setBook(result);
            }else{//連結失敗，顯示錯誤訊息
                errorMessageDialog(result);
            }
        }
    }

    //設置書籍
    void setBook(String data){
        //Json資料解析
        try {
            JSONArray ja = new JSONArray(data);//分析資料陣列
            final String[] book = new String[ja.length()];//書籍id
            final String[] bookName = new String[ja.length()];//書籍名稱

            SimpleAdapter sa;

            if(ja.toString().equals("[]")){//無借書紀錄，顯示訊息
                list(v).setVisibility(View.GONE);//隱藏清單
                noData(v).setVisibility(View.VISIBLE);//顯示訊息

            }else{
                list(v).setVisibility(View.VISIBLE);//顯示清單
                noData(v).setVisibility(View.GONE);//隱藏訊息

                for(int i = 0; i< ja.length(); i++){
                    book[i] = ja.getJSONObject(i).getString("b_id");
                    bookName[i] = ja.getJSONObject(i).getString("b_name");
                }

                //建立列表
                ArrayList<HashMap<String, Object>> list = new ArrayList<>();
                for(int i =0;i<book.length;i++){
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("image",R.drawable.no_image);//加入圖片陣列
                    map.put("name",bookName[i]);//加入書籍陣列
                    list.add(map);//列表裡加入陣列
                }
                //為ListView添加容器
                sa = new SimpleAdapter(Record.this, list,R.layout.history_item,
                        new String[]{"image","name"},new int[]{R.id.image,R.id.name}){
                    /**設置按鈕*/
                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent){
                        View v = super.getView(position,convertView,parent);

                        Button btn = (Button)v.findViewById(R.id.btn);//取得評論操控元件
                        //點擊按鈕的動作
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg) {
                                //儲存資料
                                savePreferences(book[position],bookName[position]);
                                //前往評論
                                NextPage(BookComment.class);
                            }
                        });

                        return v;
                    }
                };

                list(v).setAdapter(sa);
            }

        }catch(JSONException e){
            errorMessageDialog(JSON_ERROR);
        }

    }

    //儲存書籍資料
    private void savePreferences(String book, String bookName){
        SharedPreferences preferences = getSharedPreferences("book", MODE_PRIVATE);
        preferences.edit()
                .putString("book",book)
                .putString("bookName", bookName)
                .apply();

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
        b.putInt("fromRecord",fromRecord);

        startActivity(new Intent(this,link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }

    /**返回上一頁(個人書房-主頁)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Personal.class);
        return false;
    }

}
