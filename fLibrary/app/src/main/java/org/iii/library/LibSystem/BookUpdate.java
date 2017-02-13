package org.iii.library.LibSystem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.iii.library.Network.Connection;
import org.iii.library.R;

import static org.iii.library.Definition.InitViews.authorTxv;
import static org.iii.library.Definition.InitViews.bookNameTxv;
import static org.iii.library.Definition.InitViews.deleteBtn;
import static org.iii.library.Definition.InitViews.idTxv;
import static org.iii.library.Definition.InitViews.isbnTxv;
import static org.iii.library.Definition.InitViews.sendBtn;
import static org.iii.library.Definition.InitViews.spinner;

/**2016/10/27 Kitty
 * miss function：上傳圖片
 */

public class BookUpdate extends AppCompatActivity implements View.OnClickListener/*,TextWatcher*/{

    //private TextView n_err,a_err,i_err;//error message

    private View v;//畫面：新增書籍
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_update);

        v = findViewById(R.id.book_update);

        b = getIntent().getExtras();
        if(b != null && b.containsKey("limit"))
            b.getString("limit");

        //initView();

        /**將資料加進下拉式選單中*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.cls,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner(v).setAdapter(adapter);

    }

    @Override
    public void onStart(){
        super.onStart();

        deleteBtn(v).setOnClickListener(this);
        sendBtn(v).setOnClickListener(this);

        //idTxv(v).addTextChangedListener(this);
    }

    /*
    //初始化元件
    private void initView(){
        n_err = (TextView)findViewById(R.id.book_name_err);
        a_err = (TextView)findViewById(R.id.auth_err);
        i_err = (TextView)findViewById(R.id.isbn_err);
    }

    //取得編號欄位資料
    private String getId(){
        return idTxv(v).getText().toString();
    }

    //取得書名欄位資料
    private String getName(){
        return bookNameTxv(v).getText().toString();
    }

    //取得作者欄位資料
    private String getAuthor(){
        return authorTxv(v).getText().toString();
    }

    //取得isbn欄位資料
    private String getIsbn(){
        return isbnTxv(v).getText().toString();
    }
*/
    //取得分類欄位資料
    private String getSort(){
        String item = spinner(v).getSelectedItem().toString();
        switch(item){
            case "資訊類":
                return "1";
            case "語文類":
                return "2";
            case "生活類":
                return "3";
        }
        return null;
    }

    //點擊事件
    @Override
    public void onClick(View view) {

        //欄位判斷
        /*
        if(n.length()>30)//書名
            n_err.setVisibility(View.VISIBLE);
        else
            n_err.setVisibility(View.GONE);

        if(a.length()>20)//作者
            a_err.setVisibility(View.VISIBLE);
        else
            a_err.setVisibility(View.GONE);

        if(i.length()>13)//isbn
            i_err.setVisibility(View.VISIBLE);
        else
            i_err.setVisibility(View.GONE);
        */

        //圖片
        switch (view.getId()){
            case R.id.image://新增圖片
                Toast.makeText(getApplicationContext(),
                        "開發中，暫無此功能",Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete_btn://刪除圖片
                //判斷圖片是否存在
                /*if(p.length()>0){//存在，刪除圖檔後，隱藏刪除按鈕
                    img.setText("");
                    deleteBtn(v).setVisibility(View.GONE);
                }*/
                Toast.makeText(getApplicationContext(),
                        "預期功能：刪除圖片",Toast.LENGTH_SHORT).show();

                break;
            case R.id.send_btn://送出消息資料
                //取得輸入資料
                String bid = idTxv(v).getText().toString(),
                        bn = bookNameTxv(v).getText().toString(),
                        ba = authorTxv(v).getText().toString(),
                        bi = isbnTxv(v).getText().toString(),
                        //bp = img.getText().toString(),
                        bc = getSort();

                //轉換編號


                new SendData().execute(bid,bi,bn,ba,""/*bp*/,bc);//新增書籍資料
                break;
        }
    }
/*
    //輸入監聽事件
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(getName().length()>30){
            n_err.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
*/

    //connect api
    private class SendData extends AsyncTask<String,String,String>{

        Boolean isSend = false;

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1011);
            isSend = c.POST(params);

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){

            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

            if(isSend){
                NextPage(BookManage.class);
            }
        }
    }

    private void NextPage(Class link){
        startActivity(new Intent(this, link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }


    /**返回上一頁(書籍管理)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(BookManage.class);

        return false;
    }
}
