package org.iii.library.task;

import android.os.AsyncTask;

import org.iii.library.Network.Connection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static org.iii.library.Definition.Final.JSON_ERROR;


public class GetTask extends AsyncTask<String,String,String> {

    private String mResult;
    private int api;
    private boolean isConnected;

    public GetTask(int api){
        this.api = api;
    }

    //取得連結狀態
    public boolean getIsConnected(){
        return isConnected;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        //連結時顯示處理狀態
    }

    @Override
    protected String doInBackground(String... params) {
        //處理連結
        Connection connection = new Connection(api);
        isConnected = connection.GET();

        //回傳連結資料 or 其他訊息
        return (isConnected) ? connection.getData() : connection.getMessage();
    }

    @Override
    protected void onPostExecute(String result){
        dataHandle(result);
    }

    private void dataHandle(String data){
        try{
            String all = new JSONObject(data).getString("readNewsAll");
            JSONArray ja = new JSONArray(all);//取得資料組
            final String[] sub = new String[ja.length()];//設置資料陣列

            for(int i = 0;i <ja.length();i++){
                sub[i] = ja.getJSONObject(i).getString("n_con");
            }
            setResult(sub[0]);

        }catch(JSONException e){
            setResult(JSON_ERROR);
        }
    }

    //設置結果
    private void setResult(String data){
        mResult = data;
    }

    //取得結果
    public String getResult(){
        return mResult;
    }
}
