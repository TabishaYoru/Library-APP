package org.iii.library.Network;


import org.iii.library.Definition.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.iii.library.Definition.Final.B;
import static org.iii.library.Definition.Final.C;
import static org.iii.library.Definition.Final.DEL_FAL;
import static org.iii.library.Definition.Final.DEL_SCS;
import static org.iii.library.Definition.Final.INTERNAL_ERROR;
import static org.iii.library.Definition.Final.IO;
import static org.iii.library.Definition.Final.M;
import static org.iii.library.Definition.Final.N;
import static org.iii.library.Definition.Final.NEW_FAL;
import static org.iii.library.Definition.Final.NEW_SCS;
import static org.iii.library.Definition.Final.NOT_FOUND;
import static org.iii.library.Definition.Final.R;
import static org.iii.library.Definition.Final.UPDATE_FAL;
import static org.iii.library.Definition.Final.UPDATE_SCS;
import static org.iii.library.Definition.Final.URL_ERROR;


public class Connection {
    private int info;//table name
    private String sub;//sub stratum
    private String single;//one data
    private String error,digital;

    //建構子(多筆)
    public Connection(int info){
        this.info = info;
    }
    //建構子(單筆)
    public Connection(int info, String single){
        this.info = info;
        this.single = single;
    }
    //建構子
    public Connection(int info, String sub,String single){
        this.info = info;
        this.sub = sub;
        this.single = single;
    }

    //取得訊息
    public String getMessage(){
        return error;
    }

    //設置訊息
    private void setMessage(String message){
        error = message;
    }

    //取得資料
    public String getData(){
        return digital;
    }

    //設置資料
    private void setData(String data){
        digital = data;
    }

    //取得URL
    private URL getUrl(){
        try {
            return new URL(setUrl());
        }catch (MalformedURLException e){
            setMessage(URL_ERROR);
        }
        return null;
    }

    //設置URL
    private String setUrl(){
        String api = null;
        Config cf = new Config();

        switch(info){
            case 1001://member
                api = M;
                break;
            case 1010://news
                api = N;
                break;
            case 1011://book
                api = B;
                break;
            case 1100://comment
                api = C;
                break;
            case 1101://record
                api = R;
                break;

        }

        if(single != null) {
            if(sub != null)
                return "http://" + cf.ip + ":" + cf.port + "/" + api +"/"+ sub + "/" + single;
            else
                return "http://" + cf.ip + ":" + cf.port + "/" + api + "/" + single;
        }
        else
            return "http://"+cf.ip+":"+cf.port+"/"+api+"/";

    }

    //資料連線
    private HttpURLConnection Connect(URL url,String method) throws IOException{
        //connect
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();

        //add request header
        huc.setRequestMethod(method);//GET、POST、PUT、DELETE
        huc.setReadTimeout(5000);//限制讀取時間
        huc.setConnectTimeout(5000);//限制連線時間

        return huc;
    }


    //查詢
    public boolean GET(){
        BufferedReader reader = null;
        HttpURLConnection huc = null;

        try{
            huc = Connect(getUrl(),"GET");//Http連線
            int state = huc.getResponseCode();//連線狀態

            switch (state){
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_NOT_MODIFIED:
                    reader = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                    StringBuilder sb = new StringBuilder();

                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        sb.append(line);//取得資料
                    }

                    setData(sb.toString());
                    return true;

                case HttpURLConnection.HTTP_NOT_FOUND:
                    setMessage(NOT_FOUND);
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    setMessage(INTERNAL_ERROR);
                    break;
            }

        }catch(IOException e){
            setMessage(IO);
        }finally {
            if(huc != null){
                huc.disconnect();
            }
            ReaderClose(reader);
        }

        return false;
    }

    //新增
    public boolean POST(String... params){

        HttpURLConnection huc = null;
        Writer wr = null;

        try{
            huc = Connect(getUrl(),"POST");//Http連線

            //post request
            huc.setDoOutput(true);

            wr = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream(),"UTF-8"));
            String param = NEW(params);

            if(param != null)
                wr.write(param);

            wr.flush();
            wr.close();

            int state = huc.getResponseCode();
            switch(state){
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_NOT_MODIFIED:
                    setMessage(NEW_SCS);
                    return true;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    setMessage(NEW_FAL);
                    break;
                default:
                    setMessage(INTERNAL_ERROR);
                    break;
            }

        }catch(IOException e){
            setMessage(IO);
        }finally {
            if(huc != null){
                huc.disconnect();
                if(wr != null) {
                    try {
                        wr.close();//safe close
                    } catch (IOException e) {
                        setMessage(IO);
                    }
                }
            }


        }

        return false;
    }

    //修改
    public boolean PUT(String... params){

        HttpURLConnection huc = null;
        Writer wr = null;

        try {
            huc = Connect(getUrl(),"PUT");//Http連線

            //put request
            huc.setDoInput(true);
            huc.setDoOutput(true);
            huc.setRequestProperty("Content-Type", "application/json");

            wr = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream(),"UTF-8"));

            String urlParams = EDIT(params);

            if(urlParams != null)
                wr.write(urlParams);

            wr.flush();
            wr.close();

            int state = huc.getResponseCode();
            switch(state){
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_NOT_MODIFIED:
                    setMessage(UPDATE_SCS);
                    return true;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    setMessage(UPDATE_FAL);
                    break;
                default:
                    setMessage(INTERNAL_ERROR);
                    break;
            }

        } catch (IOException e) {
            setMessage(IO);
        } finally {
            if(huc != null){
                huc.disconnect();

                if(wr != null){
                    try{
                        wr.close();
                    }catch (IOException e){
                        setMessage(IO);
                    }
                }
            }


        }

        return false;
    }

    //刪除
    public boolean DELETE(){
        HttpURLConnection huc = null;

        try{
            huc = Connect(getUrl(),"DELETE");//Http連線
            int state = huc.getResponseCode();//連線狀態

            switch (state){
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_NOT_MODIFIED:
                    setMessage(DEL_SCS);
                    return true;

                case HttpURLConnection.HTTP_NOT_FOUND:
                    setMessage(DEL_FAL);
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    setMessage(INTERNAL_ERROR);
                    break;
            }

        }catch(IOException e){
            setMessage(IO);
        }finally {
            if(huc != null){
                huc.disconnect();
            }
        }

        return false;
    }


    //new data
    private String NEW(String... data){

        switch(info){
            case 1001://member
                //註冊會員
                return "m_id="+data[0]+"&m_pass="+data[1]+"&m_name="+data[2]+"&m_email="+data[3];

            case 1010://news
                return "n_con="+data[0];//新增消息

            case 1011://book
                return "b_id="+data[0]+"&ISBN="+data[1]+"&b_name="+data[2]+
                        "&b_auth="+data[3]+"&b_img="+data[4]+"&belongs="+data[5];//新增書籍
            case 1100://comment
                return "b_id="+data[0]+"&c_con="+data[1]+"&m_id="+data[2];//新增評論
            case 1101://record
                return "b_id="+data[0]+"&m_id="+data[1];//還書功能

        }
        return null;
    }

    //edit data
    private String EDIT(String... data){

        switch(info){
            case 1001://member
                if(sub != null)
                    return "{\"m_id\": \""+data[0]+"\"}";//變更權限
                else
                    return "{\"m_id\": \""+data[0]+"\",\"m_pass\": \""+data[1]+"\"}";//修改密碼

            case 1010://news
                return "{\"n_id\": "+data[0]+",\"n_con\": \""+data[1]+"\"}";

            case 1011://book
                return "{\"b_id\": \""+data[0]+"\",\"ISBN\": \""+data[1]+"\"," +
                        "        \"b_name\": \""+data[2]+"\",\"b_auth\": \""+data[3]+"\"," +
                        "        \"b_img\": \""+data[4]+"\",\"belongs\": \""+data[5]+"\"," +
                        "      \"bookstatus\": \""+data[6]+"\"}";
/*
            case 1100://isbn(暫無)
                return "_isbn="+data[0];*/

        }
        return null;
    }


    //關閉讀取串流
    private void ReaderClose(BufferedReader reader){
        if(reader != null){
            try{
                reader.close();
            }catch (IOException e){
                setMessage(IO);
            }

        }
    }

}
