package org.iii.library.LibSystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import org.iii.library.R;

import static org.iii.library.Definition.InitViews.ISBN;
import static org.iii.library.Definition.InitViews.deleteBtn;
import static org.iii.library.Definition.InitViews.editBtn;
import static org.iii.library.Definition.InitViews.newBtn;

public class IsbnScan extends AppCompatActivity implements View.OnClickListener{

    private Bundle b;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.isbn_scan);

        v = findViewById(R.id.isbn_scan);

        b = getIntent().getExtras();

        String scan = null;
        if(b != null && b.containsKey("limit")){
            b.getString("limit");
            scan = b.getString("isbn");
        }

        ISBN(v).setText(scan);

        //點擊事件
        newBtn(v).setOnClickListener(this);
        editBtn(v).setOnClickListener(this);
        deleteBtn(v).setOnClickListener(this);

        //直接掃描
        /*IntentIntegrator scanIntegrator = new IntentIntegrator(IsbnScan.this);
        scanIntegrator.initiateScan();*/

    }

    //掃描結果
    /*public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (requestCode != 0 && resultCode == RESULT_OK) {

            String scan = scanResult.getContents();


        } else {
            Toast.makeText(getApplicationContext(), "取消掃描", Toast.LENGTH_SHORT).show();
        }
    }*/

    //點擊按鈕動作
    @Override
    public void onClick(View view){
        final String isbn = ISBN(v).getText().toString();

        switch (view.getId()){
            case R.id.new_btn://編輯書籍(新增)
                NextPage(BookUpdate.class,isbn);
                break;

            case R.id.edit_btn://編輯書籍(修改)
                NextPage(BookManageEdit.class,isbn);
                break;

            case R.id.delete_btn://刪除書籍
                AlertDialog.Builder del = new AlertDialog.Builder(IsbnScan.this);
                del.setTitle("刪除")
                        .setMessage("請問要刪除該書籍嗎？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //new Del().execute(isbn);
                        dialog.dismiss();
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

    //刪除書籍
    /*private class Del extends AsyncTask<String,String,String>{
        ProgressDialog pd;
        boolean isDel;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(IsbnScan.this),"","刪除中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1011,params[0]);
            isDel = c.DELETE();

            return getMsg();
        }

        @Override
        protected void onPostExecute(String result){
            if(isDel){
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                NextPage(BookManage.class,null);
            }

            pd.dismiss();
        }
    }*/

    //換頁
    private void NextPage(Class link,String isbn){

        if(isbn != null) {//有掃描到時
            b.putString("isbn", isbn);
        }

        startActivity(new Intent().setClass(this, link).putExtras(b));
        overridePendingTransition(0,0);
        finish();
    }

    /**返回上一頁(書籍管理)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            NextPage(BookManage.class,null);
        return false;
    }
}
