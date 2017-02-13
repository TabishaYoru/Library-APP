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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONException;
import org.json.JSONObject;

import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.Final.fromBookManage;
import static org.iii.library.Definition.InitViews.authorTxv;
import static org.iii.library.Definition.InitViews.bookNameTxv;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.deleteBtn;
import static org.iii.library.Definition.InitViews.image;
import static org.iii.library.Definition.InitViews.isbnTxv;
import static org.iii.library.Definition.InitViews.sendBtn;
import static org.iii.library.Definition.InitViews.spinner;

public class BookManageEdit extends AppCompatActivity implements View.OnClickListener{

    private Bundle b;
    private View v;//畫面：編輯書籍

    private String only,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_manage_edit);

        v = findViewById(R.id.book_manage_edit);

        b = getIntent().getExtras();

        //String isbn = null;

        if(b != null && b.containsKey("limit")) {
            //isbn = b.getString("isbn");
            only = b.getString("only");
        }
        //將資料加進下拉式選單中
        /**將資料加進下拉式選單中*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.cls,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner(v).setAdapter(adapter);

        //取得書籍資料
        new Work().execute(only);

    }

    @Override
    protected void onStart(){
        super.onStart();

        //點擊事件
        image(v).setOnClickListener(this);
        deleteBtn(v).setOnClickListener(this);
        sendBtn(v).setOnClickListener(this);
    }

    //get api
    private class Work extends AsyncTask<String,String,String>{
        boolean isGet = false;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            pd = ProgressDialog.show(context(BookManageEdit.this),"","讀取中...",true);

        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0] != null) {
                Connection c = new Connection(1011, params[0]);
                isGet = c.GET();

                if(isGet){
                    return c.getData();
                }else{
                    return c.getMessage();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(isGet) {
                //資料加入欄位
                setData(result);
            }else{
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            }

            pd.dismiss();

        }
    }

    //點擊按鈕動作
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.delete_btn://刪除圖片
                Toast.makeText(getApplicationContext(), "預期功能：刪除圖片",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.image://編輯圖片
                Toast.makeText(getApplicationContext(), "預期功能：觸碰可以新增圖片",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.send_btn://送出消息資料
                //取得輸入資料
                String  bn = bookNameTxv(v).getText().toString(),
                        ba = authorTxv(v).getText().toString(),
                        //bp = "",
                        bi = isbnTxv(v).getText().toString();
                //圖片
                String bc = null;//分類

                //轉換編號
                String item = spinner(v).getSelectedItem().toString();
                switch(item){
                    case "語文類":
                        bc = "1";
                        break;
                    case "資訊類":
                        bc = "2";
                        break;
                    case "生活類":
                        bc = "3";
                        break;
                }

                new Edit().execute(only,bi,bn,ba/*,bp*/,"",bc,status);//新增書籍資料
                break;
        }
    }

    //取得資料
    private void setData(String data){
        try{
            JSONObject jo = new JSONObject(data);//json 物件
            String bn = jo.getString("b_name"),//書名
                    ba = jo.getString("b_auth"),//作者
                    bi = jo.getString("ISBN");//isbn
            status = jo.getString("bookstatus");//狀態
            int bc = jo.getInt("belongs");//分類

            String[] group = {bn,ba,bi};
            getData(group);

            spinner(v).setSelection(bc-1);


        }catch(JSONException e){
            errorMessageDialog(JSON_ERROR);
        }
    }

    void getData(String... data){
        bookNameTxv(v).setText(data[0]);
        authorTxv(v).setText(data[1]);
        isbnTxv(v).setText(data[2]);
    }

    //put api
    private class Edit extends AsyncTask<String,String,String> {

        Boolean isSend = false;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(BookManageEdit.this),"","修改中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1011,params[0]);
            isSend = c.PUT(params);
            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

            if(isSend){
                NextPage(BookManage.class);
            }

            pd.dismiss();
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
        startActivity(new Intent().setClass(this,link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }


    /**返回上一頁*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if(b.getInt("from") == fromBookManage)
                NextPage(BookManage.class);
            else
                NextPage(IsbnScan.class);
        }
        return false;
    }
}
