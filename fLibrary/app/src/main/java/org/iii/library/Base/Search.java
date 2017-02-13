package org.iii.library.Base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.iii.library.Network.Connection;
import org.iii.library.R;

import static org.iii.library.Definition.Final.KEYWORD_EMPTY;
import static org.iii.library.Definition.Final.fromSearch;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.errorMsg;
import static org.iii.library.Definition.InitViews.input;
import static org.iii.library.Definition.InitViews.isbnBtn;
import static org.iii.library.Definition.InitViews.searchBtn;
import static org.iii.library.Definition.InitViews.spinner;


public class Search extends AppCompatActivity implements View.OnClickListener{

    private View v;
    private Bundle b;
    private String condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        //指定元件
        v = findViewById(R.id.search);//主頁面：查詢館藏

        b = getIntent().getExtras();
        if(b != null && b.containsKey("limit"))
            b.getString("limit");

        /**將資料加進下拉式選單中*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.term,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner(v).setAdapter(adapter);

    }

    @Override
    protected void onStart(){
        super.onStart();

        //點擊監聽事件
        searchBtn(v).setOnClickListener(this);//查詢按鈕
        isbnBtn(v).setOnClickListener(this);//掃描條碼按鈕
    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.search_btn:

                //取得查詢條件
                int term = (int)(spinner(v).getSelectedItemId());
                setCondition(term);

                //取得查詢資料
                String in = input(v).getText().toString();

                //執行
                if(in.equals("")){
                    errorMsg(v).setText(KEYWORD_EMPTY);
                }
                else{
                    errorMsg(v).setText("");
                    //NextPage(SearchResult.class,null,false);

                    new Find().execute(getCondition(),in);//查詢
                }

                break;

            case R.id.isbn_btn:
                //直接掃描
                IntentIntegrator scanIntegrator = new IntentIntegrator(Search.this);
                scanIntegrator.initiateScan();
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (requestCode != 0 && resultCode == RESULT_OK) {

            String scan = scanResult.getContents();//取得掃描資料
            NextPage(bDetail.class,scan,true);//前往書籍詳細頁面

        } else {
            Toast.makeText(getApplicationContext(), "取消掃描", Toast.LENGTH_SHORT).show();
        }
    }

    //取得查詢條件
    private String getCondition(){
        return condition;
    }

    //設置查詢條件
    private void setCondition(int term){
        switch (term){
            case 0:
                condition = "name";
            case 1:
                condition =  "isbn";
            case 2:
                condition = "class";//維修中
        }
    }

    //執行查詢
    private class Find extends AsyncTask<String,String,String> {
        boolean isConnect;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(context(Search.this), "", "搜尋中...", true);
        }

        @Override
        protected String doInBackground(String... params) {

            Connection c = new Connection(1011, params[0], params[1]);

            //連線
            isConnect = c.GET();
            if (isConnect) {
                return c.getData();
            }

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            pd.dismiss();

            if (isConnect) {
                NextPage(SearchResult.class, result, false);
            }else{
                errorMsg(v).setText(result);
            }
        }
    }

    //功能換頁
    private void NextPage(Class link, String data, boolean from){
        if(data != null) {
            b.putString("only", data);
            if(from) {
                b.putInt("from", fromSearch);
            }else{
                b.remove("from");
            }
        }

        startActivity(new Intent().setClass(this,link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }

    /**返回上一頁(首頁)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(Main.class,null,false);//回到首頁
        return false;
    }

}
