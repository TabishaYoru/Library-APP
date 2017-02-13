package org.iii.library.Base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.iii.library.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class System extends AppCompatActivity {
    /**api連結測試用資料*/
    String api = "http://10.10.103.113:3000";

    /**正規法判斷*/
    Pattern pIp = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)");//ip格式
    Pattern pPort = Pattern.compile(":(\\d+)");//port格式
    Matcher mIp = pIp.matcher(api),
            mPort = pPort.matcher(api);

    Pattern noNum = Pattern.compile("(\\D+)");//非數字


    //ip數值判斷
    Pattern y = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])." +
            "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])." +
            "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])." +
            "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])$");

    /**元件*/
    private EditText ip,port;//ip、port欄位
    private Button comment;//連結按鈕
    private TextView message,errIp,errPort;//ip、port、連結訊息

    private Bundle b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system);

        b = getIntent().getExtras();

        if(b != null && b.containsKey("limit"))
            b.getString("limit");

        setValues();//取得元件

        if(mIp.find())ip.setText(mIp.group(0));//從api取得的ip(fake)
        if(mPort.find())port.setText(mPort.group(1));//從api取得的port(fake)

        ip.setOnKeyListener(KeyListener);//輸入ip時的判斷
        port.setOnKeyListener(KeyListener);//輸入port時的判斷

        comment.setOnClickListener(btnListener);//點擊連結測試
    }
    /**取得元件*/
    private void setValues(){
        ip = (EditText)findViewById(R.id.ip);//ip欄位
        errIp = (TextView)findViewById(R.id.errorIp);//ip欄位錯誤訊息
        port = (EditText)findViewById(R.id.port);//port欄位
        errPort = (TextView)findViewById(R.id.errorPort);//port欄位錯誤訊息
        comment = (Button)findViewById(R.id.comBtn);//連結測試按鈕
        message = (TextView)findViewById(R.id.message);//顯示連結後資訊
    }

    /**輸入欄位時的動作*/
    protected View.OnKeyListener KeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int i, KeyEvent keyEvent) {
            /**取得在欄位的輸入值*/
            String edIp = ip.getText().toString();//取得Ip欄位
            String edPort = port.getText().toString();//取得port欄位

            if(edIp.equals("") || edPort.equals("")){
                message.setText(R.string.system_empty);
            }else{
                int getPort = Integer.parseInt(edPort);//將port輸入值轉成整數
                /**matcher取得輸入值為其正規格式判斷*/
                Matcher yip = y.matcher(edIp);//根據ip輸入值判斷ip格式
                Matcher nPort = noNum.matcher(edPort);//根據port輸入值判斷port格式

                switch (v.getId()){
                    case R.id.ip:
                        if(yip.find())
                            errIp.setText("");
                        else
                            errIp.setText(R.string.format_err);
                        break;
                    case R.id.port:
                        if(nPort.find() || edPort.equals("")) //非數字
                            errPort.setText(R.string.format_err);
                        else if(getPort > 65535 || getPort< 0)
                            errPort.setText("超出範圍");
                        else
                            errPort.setText("");
                        break;
                }
            }
            return false;
        }
    };

    /**點擊連結測試按鈕的動作*/
    protected View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /**取得在欄位的輸入值，轉成字串*/
            String edIp = ip.getText().toString();//取得Ip欄位
            String edPort = port.getText().toString();//取得port欄位

            /**檢查與api端的連結*/
            if(edIp.equals(""))//ip空值
                errIp.setText(R.string.ip_empty);
            else if(edPort.equals(""))//port空值
                errPort.setText(R.string.port_empty);
            else{
                if(edIp.equals(mIp.group(0)) && edPort.equals(mPort.group(1))){
                    message.setText("連結正常");

                }
                else
                    message.setText("連結無效");
            }
        }
    };


    /**返回上一頁(首頁)*/
    //若按到返回鍵和防止重覆按到返回鍵時
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            startActivity(new Intent(this, Main.class).putExtras(b));
            overridePendingTransition(0,0);
            finish();
        }
        return false;
    }
}
