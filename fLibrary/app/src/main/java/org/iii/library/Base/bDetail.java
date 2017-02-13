package org.iii.library.Base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import org.iii.library.Network.Connection;
import org.iii.library.R;
import org.json.JSONException;
import org.json.JSONObject;

import static org.iii.library.Definition.Final.I;
import static org.iii.library.Definition.Final.JSON_ERROR;
import static org.iii.library.Definition.Final.fromNews;
import static org.iii.library.Definition.Final.fromSearch;
import static org.iii.library.Definition.Final.fu;
import static org.iii.library.Definition.InitViews.ISBN;
import static org.iii.library.Definition.InitViews.author;
import static org.iii.library.Definition.InitViews.bookName;
import static org.iii.library.Definition.InitViews.borrowBtn;
import static org.iii.library.Definition.InitViews.commentBtn;
import static org.iii.library.Definition.InitViews.context;
import static org.iii.library.Definition.InitViews.sort;
import static org.iii.library.Definition.InitViews.state;

public class bDetail extends AppCompatActivity implements View.OnClickListener{

    private View v;//畫面：書籍詳細資訊
    private Bundle b;
    private String decide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_detail);

        v = findViewById(R.id.book_info);

        //接收攜帶值
        b = getIntent().getExtras();

        if(b != null && b.containsKey("only")) {
            decide = b.getString("limit");
            String isbn = b.getString("only");

            new BookInfo().execute(isbn);
        }

        //點擊按鈕
        commentBtn(v).setOnClickListener(this);//評論
        borrowBtn(v).setOnClickListener(this);//借閱
    }

    public class BookInfo extends AsyncTask<String,String,String>{
        boolean isGet;
        ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(context(bDetail.this),"","載入中...",true);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection c = new Connection(1011,I,params[0]);
            isGet = c.GET();

            return c.getMessage();
        }

        @Override
        protected void onPostExecute(String result){
            if(isGet){
                setInfo(result);
            }else{
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            }

            pd.dismiss();
        }
    }


    //設置書籍資料
    private void setInfo(String book){
        try{
            JSONObject jo = new JSONObject(book);//json 物件
            String /*bp = jo.getString("b_img"),*/
                    bn = jo.getString("b_name"),
                    ba = jo.getString("b_auth"),
                    bi = jo.getString("ISBN"),
                    bs = jo.getString("bookstatus");

            int sortID = jo.getInt("belongs");//分類代號

            //設置圖片(測試)
            /*Resources res = getResources();
            Bitmap bit = BitmapFactory.decodeResource(res,R.drawable.pb00007674);
            image(v).setImageBitmap(bit);*/

            //圖片
            bookName(v).setText(bn);//書名
            author(v).setText(ba);//作者
            ISBN(v).setText(bi);//isbn
            state(v).setText(bs);//狀態

            String cls = null;
            switch (sortID){
                case 1:
                    cls = "資訊類";
                    break;
                case 2:
                    cls = "語文類";
                    break;
                case 3:
                    cls = "生活類";
                    break;
            }

            sort(v).setText(cls);//分類

        }catch(JSONException e){
            errorMessageDialog(JSON_ERROR);
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.comment_btn:
                NextPage(Discuss.class,false);
                break;
            case R.id.borrow_btn:
                if(decide.equals(fu))
                    NextPage(Login.class,false);
                else{
                    AlertDialog.Builder borrow = new AlertDialog.Builder(this);
                    borrow.setMessage("確定要借閱嗎？");
                    borrow.setTitle("借閱確認");
                    //確定借閱動作
                    borrow.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"預期功能：會員可借閱",Toast.LENGTH_SHORT).show();
                        }
                    });
                    //取消借閱動作
                    borrow.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    borrow.create().show();
                }
                break;
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


    //功能：攜值換頁
    private void NextPage(Class link, boolean back){
        if(back){
            b.remove("only");
        }
        startActivity(new Intent().setClass(this,link).putExtras(b));
        overridePendingTransition(0, 0);
        finish();
    }

    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (b.getInt("from") == fromNews)//從最新消息進來
                NextPage(News.class, true);
            else if(b.getInt("from") == fromSearch)//從查詢館藏進來
                NextPage(Search.class, true);
            else//返回查詢結果頁面
                NextPage(SearchResult.class, false);
        }
        return false;
    }
}
