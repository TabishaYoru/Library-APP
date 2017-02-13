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

import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.Final.NOT_RUN;
import static org.iii.library.Definition.Final.ra;
import static org.iii.library.Definition.Final.rm;
import static org.iii.library.Definition.InitViews.aList;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.deleteBtn;
import static org.iii.library.Definition.InitViews.editBtn;
import static org.iii.library.Definition.InitViews.mList;

public class IdManage extends AppCompatActivity {


    private View v;//畫面：帳戶管理
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_manage);

        v = findViewById(R.id.id_manage);
        b = getIntent().getExtras();

        new GetMember().execute();

    }


    //取得資料
    private class GetMember extends AsyncTask<String,String,String> {
        private boolean isGet;
        private ProgressDialog pd = null;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(IdManage.this),"","讀取中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1001);
            isGet = c.GET();

            if(isGet){
                return c.getData();
            }

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(isGet) {
                setAdminData(result);//設置管理員列表
                setMemData(result);//設置管理員列表
            }

            pd.dismiss();

        }
    }


    //管理員帳號
    void setAdminData(String data){
        try{
            String all = new JSONObject(data).getString("readMemberAll");
            JSONArray ja = new JSONArray(all);//取得資料組
            //取得資料陣列
            final String[] user = new String[ja.length()];//admin帳號
            final String[] name = new String[ja.length()];//admin姓名

            int count = 0;
            for(int i =0;i<ja.length();i++){
                if(ja.getJSONObject(i).getInt("m_role") == 1) {
                    user[i] = ja.getJSONObject(i).getString("m_id");
                    name[i] = ja.getJSONObject(i).getString("m_name");
                    count++;
                }
            }

            //設置陣列
            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
            for(int i = 0;i<count;i++){
                HashMap<String,Object> map = new HashMap<>();
                map.put("user", user[i]);//帳號
                map.put("name", name[i]);//名字
                list.add(map);
            }

            //加入陣列
            aList(v).setAdapter(sa(list,rm,user,0));


        }catch(JSONException e){
            errorMessageDialog(JSON_ERROR);
        }
    }

    //設置會員列表
    void setMemData(String data){
        try{
            String all = new JSONObject(data).getString("readMemberAll");
            JSONArray ja = new JSONArray(all);//取得資料組
            //取得資料陣列
            final String[] user = new String[ja.length()];//帳號
            final String[] name = new String[ja.length()];//姓名

            for(int i =0;i<ja.length();i++){
                if(ja.getJSONObject(i).getInt("m_role") == 2) {
                    user[i] = ja.getJSONObject(i).getString("m_id");
                    name[i] = ja.getJSONObject(i).getString("m_name");
                }
            }

            //設置陣列
            int empty = 0;
            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
            for(int i = 0;i<user.length;i++){
                HashMap<String,Object> map = new HashMap<>();
                if(user[i] == null && name[i] == null){
                    empty++;
                }else {
                    map.put("user", user[i]);//帳號
                    map.put("name", name[i]);//名字
                    list.add(map);
                }
            }

            //加入陣列
            mList(v).setAdapter(sa(list,ra,user,empty));


        }catch(JSONException e){
            errorMessageDialog(JSON_ERROR);
        }

    }


    //列表介面
    SimpleAdapter sa(ArrayList<HashMap<String, Object>> list,final String decide,final String[] user,final int empty){
        return new SimpleAdapter(IdManage.this,list,R.layout.id_list,
                new String[]{"user","name"},new int[]{R.id.db_id,R.id.db_name}){

            /**設置按鈕*/
            @Override
            public View getView(final int p, View convert, ViewGroup parent){
                View v = super.getView(p,convert,parent);

                setButton(v,decide,user[p+empty]);

                return v;
            }

        };
    }

    //設置按鈕
    void setButton(View v, final String decide ,final String action){
        //點擊變更按鈕的動作
        editBtn(v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                AlertDialog.Builder edit = new AlertDialog.Builder(IdManage.this);
                edit.setMessage("請問要變更該帳號權限嗎？");
                edit.setTitle("變更權限");
                //點擊"是"，執行變更為會員的動作
                edit.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Change().execute(decide,action);
                    }
                });
                //點擊"否"，取消動作
                edit.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                edit.create().show();
            }
        });

        //點擊刪除按鈕的動作
        deleteBtn(v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder del = new AlertDialog.Builder(IdManage.this);
                del.setMessage("請問要刪除該帳號嗎？");
                del.setTitle("刪除");
                del.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Del().execute(action);
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
    }

    //變更權限
    private class Change extends AsyncTask<String,String,String>{
        ProgressDialog pd;
        boolean isChange;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(IdManage.this),"","修改中...",true);
        }

        @Override
        protected String doInBackground(String... params) {

            if(params[1] != null){
                Connection c = new Connection(1001, params[0], params[1]);
                isChange = c.PUT(params[1]);
            }else {
                errorMessageDialog(NOT_RUN);
            }

            return "error";
        }

        @Override
        protected void onPostExecute(String r){
            pd.dismiss();

            Toast.makeText(getApplicationContext(),r,Toast.LENGTH_SHORT).show();
            NextPage(null);

        }
    }

    //刪除帳號
    private class Del extends AsyncTask<String,String,String>{
        private boolean isDel;
        private ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(IdManage.this),"","刪除中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            String message = "";
            if(params[0] != null) {
                Connection c = new Connection(1001, params[0]);
                isDel = c.DELETE();

                message = c.getMessage();
            }

            return message;
        }

        @Override
        protected void onPostExecute(String result){
            pd.dismiss();

            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

            if(isDel){
                NextPage(null);
            }

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
    private void NextPage(Class link){
        if(link == null){
            finish();
            overridePendingTransition(0,0);
            startActivity(new Intent(getIntent()));
            overridePendingTransition(0,0);
        }else {
            startActivity(new Intent().setClass(IdManage.this,link).putExtras(b));
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
