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
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static org.iii.library.Definition.Final.CI;
import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.list;
import static org.iii.library.Definition.InitViews.total;

public class BorrowList extends AppCompatActivity{

    private View lendList;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrow_list);

        //指定畫面：借出清單
        lendList = findViewById(R.id.lend_list);

        //取得攜值
        bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("limit"))
            bundle.getString("limit");

        new GetList().execute();//取得借出清單資料
        new GetCount().execute();//取得借出書籍總數
    }

    //背景處理資料
    private class GetList extends AsyncTask<String,String,String> {
        boolean isConnected = false;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute(){
            dialog = ProgressDialog.show(context(BorrowList.this),"","讀取中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection connect = new Connection(1101);//連結紀錄資料
            isConnected = connect.GET();//取得資料

            if(isConnected){//若連線成功
                return connect.getData();
            }

            return connect.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            dialog.dismiss();//關閉對話框

            if(isConnected){//連線成功
                setRecordData(result);//處理資料
            }else{//連線失敗
                errorMessageDialog(result);//顯示錯誤訊息對話框
            }
        }
    }

    //取得連結總數
    private class GetCount extends AsyncTask<String,String,String>{
        boolean isConnected;

        @Override
        protected String doInBackground(String... params) {
            Connection connect = new Connection(1011,CI);//連結紀錄資料
            isConnected = connect.GET();//取得資料

            if(isConnected){//若連線成功
                return connect.getData();
            }

            return connect.getMessage();
        }

        @Override
        protected void onPostExecute(String result){

            if(isConnected){//連線成功
                try{
                    JSONObject jo = new JSONObject(result);
                    final String total = jo.getString("inborrow");
                    total(lendList).setText(total);

                }catch(JSONException e){
                    errorMessageDialog(JSON_ERROR);
                }
            }else{//連線失敗
                errorMessageDialog(result);//顯示錯誤訊息對話框
            }
        }
    }

    //處理紀錄資料
    public void setRecordData(String data){
        try{
            String all = new JSONObject(data).getString("readrecordAll");
            JSONArray ja = new JSONArray(all);//取得資料組
            //取得資料陣列
            final String[] borrower = new String[ja.length()];//帳號
            final String[] name = new String[ja.length()];//姓名
            final int[] count = new int[ja.length()];//借閱數量

            for(int i =0;i<ja.length();i++){
                borrower[i] = ja.getJSONObject(i).getString("m_id");
                name[i] = ja.getJSONObject(i).getString("m_name");
                count[i] = ja.getJSONObject(i).getInt("Quantity");
            }

            //將陣列按順序放入arrayList
            ArrayList<HashMap<String,Object>> arrayList = new ArrayList<>();
            for(int i = 0;i<borrower.length;i++){
                HashMap<String,Object> map = new HashMap<>();
                map.put("borrower",borrower[i]);
                map.put("name",name[i]);
                map.put("count",count[i]);
                arrayList.add(map);
            }

            //設置adapter
            SimpleAdapter adapter = new SimpleAdapter(this,arrayList,R.layout.borrow_member_list,
                    new String[]{"borrower","name","count"},
                    new int[]{R.id.borrow_acc,R.id.borrow_name,R.id.borrow_count});

            //顯示借出清單列表
            list(lendList).setAdapter(adapter);

            //點擊清單事件
            list(lendList).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //攜值換頁
                    NextPage(BorrowListDetail.class,borrower[position],name[position]);
                }
            });

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


    /**切換畫面*/
    private void NextPage(Class link,String borrower,String name){
        //判斷是否有攜值
        if(borrower != null && name != null) {
            bundle.putString("borrower",borrower);//帳號
            bundle.putString("name",name);//姓名
        }

        System.gc();//垃圾回收
        startActivity(new Intent(this, link).putExtras(bundle));
        overridePendingTransition(0,0);
        finish();
    }

    /**返回上一頁(系統維護)*/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //若按到返回鍵和防止重覆按到返回鍵時
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Defend.class,null,null);

        return false;
    }

}
