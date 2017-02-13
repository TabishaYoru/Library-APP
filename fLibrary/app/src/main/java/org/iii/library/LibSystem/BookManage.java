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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.Final.fromBookManage;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.deleteBtn;
import static org.iii.library.Definition.InitViews.editBtn;
import static org.iii.library.Definition.InitViews.isbnBtn;
import static org.iii.library.Definition.InitViews.list;
import static org.iii.library.Definition.InitViews.newBtn;
import static org.iii.library.Definition.InitViews.returnBtn;

public class BookManage extends AppCompatActivity implements View.OnClickListener,DialogInterface.OnClickListener{

    private View v;
    private Bundle b;

    private String scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_manage);

        v = findViewById(R.id.book_manage);

        b = getIntent().getExtras();

        if(b != null && b.containsKey("limit"))
            b.getString("limit");

        new GetBook().execute();//執行：書籍

    }

    @Override
    protected void onStart(){
        super.onStart();

        //點擊監聽：按鈕
        returnBtn(v).setOnClickListener(this);
        newBtn(v).setOnClickListener(this);
        isbnBtn(v).setOnClickListener(this);
    }

    //設置按鈕動作
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.return_btn://還書功能
                NextPage(BookReturn.class, null);
                break;
            case R.id.new_btn://手動新增書籍
                NextPage(BookUpdate.class, null);
                break;
            case R.id.isbn_btn://isbn掃描
                //直接掃描
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
                break;
        }
    }

    //取得書籍資料
    public class GetBook extends AsyncTask<String,String,String> {
        boolean isGet;
        ProgressDialog pd = null;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(context(BookManage.this),"","載入中...",true);
        }

        @Override
        protected String doInBackground(String... params) {

            //確認連線並取得資料
            Connection c = new Connection(1011);
            isGet = c.GET();

            if(isGet){
                return c.getData();
            }

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if (isGet){
                setBookData(result);

            }else{
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            }

            pd.dismiss();
        }
    }


    void setBookData(String data){
        try{
            String all = new JSONObject(data).getString("readBookAll");
            JSONArray ja = new JSONArray(all);//取得資料組
            //圖片陣列
            final String[] sub = new String[ja.length()];//編號陣列
            //final String[] isbn = new String[ja.length()];//isbn陣列
            String[] name = new String[ja.length()];//書名陣列


            for(int i =0;i<ja.length();i++){
                //圖片
                sub[i] = ja.getJSONObject(i).getString("b_id");//編號
                //isbn[i] = ja.getJSONObject(i).getString("ISBN");//isbn
                name[i] = ja.getJSONObject(i).getString("b_name");//書籍名稱
            }

            final ArrayList<HashMap<String,Object>> array = new ArrayList<>();

            for(String e:name){
                HashMap<String,Object> map = new HashMap<>();
                map.put("name",e);
                array.add(map);
            }

            //書籍資料列表
            SimpleAdapter sa = new SimpleAdapter(BookManage.this,array,R.layout.book_manage_list,
                    new String[]{"name"},
                    new int[]{R.id.book_name}){

                /**設置按鈕*/
                @Override
                public View getView(final int position, View convert, ViewGroup parent){
                    View v = super.getView(position,convert,parent);

                    //點擊變更按鈕的動作
                    editBtn(v).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg) {
                            AlertDialog.Builder edt = new AlertDialog.Builder(BookManage.this);
                            edt.setMessage("確定要修改此消息嗎？"+sub[position]);
                            edt.setTitle("修改書籍");
                            edt.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //確定修改
                                    b.putString("only",sub[position]);
                                    NextPage(BookManageEdit.class,null);
                                }
                            });

                            edt.setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            edt.create().show();
                        }
                    });

                    //點擊刪除按鈕的動作
                    deleteBtn(v).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder del = new AlertDialog.Builder(BookManage.this);
                            del.setMessage("確定要刪除此消息嗎？ "+sub[position]);
                            del.setTitle("刪除書籍");
                            del.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //確定刪除
                                    new Del().execute(sub[position]);
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

        }catch (JSONException e){
            errorMessageDialog(JSON_ERROR);
        }
    }



    //掃描結果處理
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (requestCode != 0 && resultCode == RESULT_OK) {

            scan = scanResult.getContents();

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("掃到條碼為： "+scan)
                    .setItems(R.array.work, this).create().show();

        } else {
            Toast.makeText(getApplicationContext(), "取消掃描", Toast.LENGTH_SHORT).show();
        }
    }

    //掃描處理動作
    @Override
    public void onClick(DialogInterface dialog, int which){
        switch(which){
            case 0:
                NextPage(BookUpdate.class,scan);
                break;
            case 1:
                NextPage(BookManageEdit.class,scan);
                break;
            case 2:
                AlertDialog.Builder del = new AlertDialog.Builder(BookManage.this);
                del.setTitle("刪除")
                        .setMessage("請問要刪除該書籍嗎？\n(注意：所有相同ISBN的書籍會一併刪除)")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Del().execute(scan);
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                break;

        }
    }

    //Http Delete Request
    protected class Del extends AsyncTask<String,String,String>{
        boolean isDel;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(BookManage.this),"","刪除中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1011,params[0]);
            isDel = c.DELETE();

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String r){
            Toast.makeText(getApplicationContext(),r,Toast.LENGTH_SHORT).show();

            if(isDel)
                NextPage(null,null);
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
    private void NextPage(Class link,String isbn){
        if(link == null){
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
        }else {
            if(isbn != null) {
                b.putString("isbn", isbn);
                b.putInt("from", fromBookManage);
            }
            startActivity(new Intent().setClass(this, link).putExtras(b));
            overridePendingTransition(0, 0);
            finish();
        }
    }

    //返回上一頁
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Defend.class, null);

        return false;
    }
}
