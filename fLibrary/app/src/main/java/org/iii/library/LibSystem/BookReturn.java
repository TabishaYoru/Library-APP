package org.iii.library.LibSystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.iii.library.Network.Connection;
import org.iii.library.R;

import static org.iii.library.Definition.Final.EMPTY;
import static org.iii.library.Definition.Final.RD;
import static org.iii.library.Definition.InitViews.isbnBtn;
import static org.iii.library.Definition.InitViews.isbnTxv;
import static org.iii.library.Definition.InitViews.sendBtn;
import static org.iii.library.Definition.InitViews.user;

public class BookReturn extends AppCompatActivity implements View.OnClickListener{

    private View view;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_return);

        view = findViewById(R.id.book_return);

        bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("limit"))
            bundle.getString("limit");

        sendBtn(view).setOnClickListener(this);//還書
        isbnBtn(view).setOnClickListener(this);//isbn掃描

    }

    //取得借閱人帳號
    private String getUser(){
        return user(view).getText().toString();
    }

    //點擊按鈕動作
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.send_btn://還書
                String isbn = isbnTxv(view).getText().toString();

                //判斷是否有值
                if(isbn.equals("")) {//若為空值則顯示訊息
                    errorMessageDialog(EMPTY);
                }else{//有值則進行連結
                    new ReturnBook().execute(isbn,getUser());
                }
                break;
            case R.id.isbn_btn://掃碼
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
                break;
        }
    }

    //當掃描完畢回傳結果
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (requestCode != 0 && resultCode == RESULT_OK) {

            String scan = scanResult.getContents();
            isbnTxv(view).setText(scan);

        } else {
            Toast.makeText(getApplicationContext(), "取消掃描", Toast.LENGTH_SHORT).show();
        }
    }

    //還書功能
    private class ReturnBook extends AsyncTask<String,String,String> {
        private boolean isPosted;

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1101,RD);//修改書籍狀態
            isPosted = c.POST(params);

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(isPosted) {
                user(view).setText(null);
                isbnTxv(view).setText(null);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }else{
                errorMessageDialog(result);
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

    /**返回上一頁(書籍管理)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            startActivity(new Intent(this, BookManage.class).putExtras(bundle));
            overridePendingTransition(0,0);
            finish();
        }
        return false;
    }

}
