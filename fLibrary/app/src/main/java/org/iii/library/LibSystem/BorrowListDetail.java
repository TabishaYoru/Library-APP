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
import android.widget.SimpleAdapter;

import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.iii.library.Definition.Final.IBA;
import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.list;
import static org.iii.library.Definition.InitViews.nameTxv;
import static org.iii.library.Definition.InitViews.userTxv;

public class BorrowListDetail extends AppCompatActivity {

    private View view;
    private Bundle bundle;
    private String borrower;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrow_list_detail);

        view = findViewById(R.id.borrow_list_detail);

        //取得攜帶值
        bundle = getIntent().getExtras();

        //判斷有無攜值
        if(bundle != null && bundle.containsKey("limit")) {
            setBorrower(bundle.getString("borrower"));
            setName(bundle.getString("name"));
        }

        //連結借閱人之借閱紀錄
        new Record().execute(getBorrower());
    }

    @Override
    public void onStart(){
        super.onStart();

        //顯示借閱人簡易資訊
        userTxv(view).setText(getBorrower());
        nameTxv(view).setText(getName());

    }

    //設置借閱人
    private void setBorrower(String borrower){
        this.borrower = borrower;
    }

    //取得借閱人
    String getBorrower(){
        return borrower;
    }

    //設置姓名
    private void setName(String name){
        this.name = name;
    }

    //取得姓名
    String getName(){
        return name;
    }

    //背景處理借閱紀錄
    private class Record extends AsyncTask<String,String,String>{
        private ProgressDialog progressDialog = null;
        private boolean isConnect;

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context(BorrowListDetail.this),
                    "",
                    "讀取中...",//顯示目前狀態
                    true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection connect = new Connection(1101,IBA,params[0]);
            isConnect = connect.GET();

            if(isConnect){//若連線成功
                return connect.getData();
            }

            return connect.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            progressDialog.dismiss();

            if(isConnect){//連線成功，處理資料
                setRecordData(result);
            }else{//若連結失敗，顯示錯誤訊息
                errorMessageDialog(result);
            }

        }
    }

    //處理紀錄資料(修改中)
    private void setRecordData(String data){
        try{
            JSONArray ja = new JSONArray(data);//取得資料組

            //取得資料陣列
            final String[] books = new String[ja.length()];//書籍陣列

            for(int i = 0; i < ja.length(); i++){
                books[i] = ja.getJSONObject(i).getString("b_name");
            }

            //設置內容陣列
            ArrayList<HashMap<String,Object>> list = new ArrayList<>();
            for(String e : books){
                HashMap<String,Object> map = new HashMap<>();
                //map.put("image",image[i]);
                map.put("book",e);
                list.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(this,list,R.layout.owner_book_list,
                    new String[]{/*"image",*/"book"},
                    new int[]{/*R.id.image,*/R.id.book_name});

            //顯示書籍清單
            list(view).setAdapter(adapter);

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

    private void NextPage(Class link){
        System.gc();//垃圾回收
        startActivity(new Intent(this, link).putExtras(bundle));
        overridePendingTransition(0, 0);
        finish();
    }

    /**返回上一頁(借出清單)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(BorrowList.class);
        return false;
    }
}
