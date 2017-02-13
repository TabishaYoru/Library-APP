package org.iii.library.Definition;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.iii.library.R;

public class InitViews extends AppCompatActivity{

    //關聯
    public static Context context(Context context){
        return context;
    }


    /**MAIN*/
    //跑馬燈
    public static TextView marquee(View v){
        return (TextView)v.findViewById(R.id.news_run);
    }

    //按鈕
    public static Button baseBtn(View v){
        return (Button)v.findViewById(R.id.btn);
    }

    /**NEWS*/
    //文字
    public static TextView text(View v){
        return (TextView)v.findViewById(R.id.text);
    }
    //首頁按鈕
    public static Button homeBtn(View v){
        return (Button)v.findViewById(R.id.home_btn);
    }
    //個人書房按鈕
    public static Button personalBtn(View v){
        return (Button)v.findViewById(R.id.personal_btn);
    }


    /**LOGIN*/
    //帳號欄位
    public static EditText user(View v){
        return (EditText)v.findViewById(R.id.user);
    }

    //密碼欄位
    public static EditText pass(View v){
        return (EditText)v.findViewById(R.id.pass);
    }

    //登入按鈕
    public static Button loginBtn(View v){
        return (Button)v.findViewById(R.id.login_btn);
    }

    //註冊按鈕
    public static Button registerBtn(View v){
        return (Button)v.findViewById(R.id.register_btn);
    }

    //忘記密碼連結
    public static TextView forgetLink(View v){
        return (TextView) v.findViewById(R.id.forget_link);
    }

    /**REGISTER*/
    //姓名
    public static TextView name(View v){
        return (TextView)v.findViewById(R.id.name);
    }

    /**FORGET*/
    //信箱欄位
    public static EditText email(View v){
        return (EditText)v.findViewById(R.id.email);
    }

    //錯誤訊息文字(總)
    public static TextView errorMsg(View v){
        return (TextView) v.findViewById(R.id.error);
    }

    //錯誤訊息文字：帳號
    public static TextView errorUser(View v){
        return (TextView)v.findViewById(R.id.error_user);
    }

    //錯誤訊息文字：信箱
    public static TextView errorEmail(View v){
        return (TextView)v.findViewById(R.id.error_email);
    }

    //送出按鈕
    public static Button sendBtn(View v){
        return (Button)v.findViewById(R.id.send_btn);
    }

    /**EDIT SECRET*/
    //錯誤訊息文字：密碼
    public static TextView errorPw(View v){
        return (TextView)v.findViewById(R.id.error_pw);
    }

    //新密碼欄位
    public static EditText newPw(View v){
        return (EditText)v.findViewById(R.id.new_pw);
    }

    //錯誤訊息文字：新密碼
    public static TextView errorNewPw(View v){
        return (TextView)v.findViewById(R.id.error_new_pw);
    }

    //確認密碼欄位
    public static EditText check(View v){
        return (EditText)v.findViewById(R.id.check);
    }

    //錯誤訊息文字：確認密碼
    public static TextView errorCheck(View v){
        return (TextView)v.findViewById(R.id.error_check);
    }

    /**SEARCH*/
    //下拉式選單
    public static Spinner spinner(View v){
        return (Spinner)v.findViewById(R.id.spinner);
    }

    //輸入查詢
    public static TextView input(View v){
        return (TextView)v.findViewById(R.id.input);
    }

    //查詢按鈕
    public static Button searchBtn(View v){
        return (Button)v.findViewById(R.id.search_btn);
    }

    //掃描按鈕
    public static Button isbnBtn(View v){
        return (Button)v.findViewById(R.id.isbn_btn);
    }

    //清單
    public static ListView list(View v){
        return (ListView)v.findViewById(R.id.list);
    }


    /**PERSONAL*/
    //無資料
    public static TextView noData(View v){
        return (TextView)v.findViewById(R.id.text_data_empty);
    }
    //評論內容
    public static EditText commentText(View v){
        return (EditText)v.findViewById(R.id.text_comment);
    }

    /**BOOK INFO*/
    //圖片
    public static ImageView image(View v){
        return (ImageView)v.findViewById(R.id.image);
    }
    //書名
    public static TextView bookName(View v){
        return (TextView)v.findViewById(R.id.book_name);
    }
    //作者
    public static TextView author(View v){
        return (TextView)v.findViewById(R.id.author);
    }
    //isbn文字
    public static TextView ISBN(View v){
        return (TextView)v.findViewById(R.id.isbn);
    }
    //類別
    public static TextView sort(View v){
        return (TextView)v.findViewById(R.id.sort);
    }
    //狀態
    public static TextView state(View v){
        return (TextView)v.findViewById(R.id.state);
    }
    //評論按鈕
    public static Button commentBtn(View v){
        return (Button)v.findViewById(R.id.comment_btn);
    }
    //借閱按鈕
    public static Button borrowBtn(View v){
        return (Button)v.findViewById(R.id.borrow_btn);
    }


    /**ID MANAGE*/
    //會員版面
    public static ListView mList(View v){
        return (ListView) v.findViewById(R.id.m_list);
    }

    //管理員版面
    public static ListView aList(View v){
        return (ListView) v.findViewById(R.id.a_list);
    }

    //修改按鈕
    public static Button editBtn(View v){
        return (Button)v.findViewById(R.id.edit_btn);
    }

    //刪除按鈕
    public static Button deleteBtn(View v){
        return (Button)v.findViewById(R.id.delete_btn);
    }

    /**NEWS MANAGE*/
    //欄位
    public static EditText editText(View v){
        return (EditText)v.findViewById(R.id.edit_text);
    }


    /**BOOK MANAGE*/
    //還書按鈕
    public static Button returnBtn(View v){
        return (Button)v.findViewById(R.id.return_btn);
    }
    //新增按鈕
    public static Button newBtn(View v){
        return (Button)v.findViewById(R.id.new_btn);
    }
    //條碼欄位
    public static EditText isbnTxv(View v){
        return (EditText) v.findViewById(R.id.isbn_text);
    }
    //書籍編號
    public static EditText idTxv(View v){
        return (EditText) v.findViewById(R.id.id_text);
    }
    //書名
    public static EditText bookNameTxv(View v){
        return (EditText) v.findViewById(R.id.book_name_text);
    }
    //作者
    public static EditText authorTxv(View v){
        return (EditText)v.findViewById(R.id.author_text);
    }



    /**LEND LIST*/
    //借閱者
    public static TextView userTxv(View v){
        return (TextView) v.findViewById(R.id.user_text);
    }
    //姓名
    public static TextView nameTxv(View v){
        return (TextView) v.findViewById(R.id.name_text);
    }
    //總數
    public static TextView total(View v){
        return (TextView) v.findViewById(R.id.book_total);
    }

}
