package org.iii.library.LibSystem;

import org.iii.library.Network.Connection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ForgetTest{ //15 unit test

    private Forget forget;

    @Before
    public void setUp(){
        forget = new Forget();

    }

    @Test
    public void UserIsNull(){//帳號欄位為空值
        //arrange
        String user = "";

        //act
        boolean actual = forget.checkUser(user);//expected is false

        //assert
        assertFalse(actual);

    }

    @Test
    public void UserHasValue(){//帳號欄位有值
        //arrange
        String user = "a1234";

        //act
        boolean actual = forget.checkUser(user);//expected is true

        //assert
        assertTrue(actual);
    }

    @Test
    public void EmailIsNull(){
        //arrange
        String email = "";//email is null

        //act
        boolean actual = forget.checkEmail(email);//result is false
        //String message = getMsg();//get error message

        //assert
        assertFalse(actual);
        //assertEquals("請輸入email\n(ex：xxx@gamil.com)",message);
    }

    @Test
    public void EmailIsWrong(){
        //arrange
        String email = "eqg.fos.ocm";//email is wrong

        //act
        boolean actual = forget.checkEmail(email);//result is false
        //String message = getMsg();//get error message

        //assert
        assertFalse(actual);
        //assertEquals("請輸入正確的email格式\n(ex：xxx@gamil.com)",message);
    }

    @Test
    public void EmailHasValue(){
        //arrange
        String email = "ooo@gmail.com";//email is wrong

        //act
        boolean actual = forget.checkEmail(email);//result is true
        //String message = getMsg();//get message is null

        //assert
        assertTrue(actual);
        //assertEquals("",message);
    }

    @Test
    public void BothCheckAreTrue(){
        //arrange
        String user = "a1234",email = "ooo@gmail.com";

        //act
        boolean au = forget.checkUser(user);//true
        boolean ae = forget.checkEmail(email);//true

        //String actual = getMsg();//get message

        //assert
        assertTrue(au);
        assertTrue(ae);
        //assertEquals("",actual);

    }

    @Test
    public void CheckUserIsFalse(){
        //arrange
        String user = "",email = "ooo@gmail.com";

        //act
        boolean au = forget.checkUser(user);//false
        //String actual = getMsg();//get message for user
        boolean ae = forget.checkEmail(email);//true

        //assert
        assertFalse(au);
        //assertEquals("請輸入帳號",actual);
        assertTrue(ae);
    }

    @Test
    public void CheckEmailIsFalse(){
        //arrange
        String user = "a1234",email = "";

        //act
        boolean au = forget.checkUser(user);//true
        boolean ae = forget.checkEmail(email);//false
        //String actual = getMsg();//get message for email

        //assert
        assertTrue(au);
        assertFalse(ae);
        //assertEquals("請輸入email\n(ex：xxx@gamil.com)",actual);

    }

    @Test
    public void BothCheckAreFalse(){
        //arrange
        String user = "",email = "oda..et.";

        //act
        boolean au = forget.checkUser(user);//false
        //String euResult = getMsg();//get message for user
        boolean ae = forget.checkEmail(email);//false
        //String eeResult = getMsg();//get message for email

        //assert
        assertFalse(au);
        //assertEquals("請輸入帳號",euResult);
        assertFalse(ae);
        //assertEquals("請輸入正確的email格式\n(ex：xxx@gamil.com)",eeResult);

    }

    @Test
    public void CanConnect(){
        //arrange
        String user = "a1234";

        //act
        boolean ok = new Connection(1001,user).GET();//can connect

        //assert
        assertTrue(ok);

    }

    @Test
    public void NotConnect(){
        //arrange
        String user = "000";//user isn't exist

        //act
        boolean no = new Connection(1001,user).GET();//can't connect
    //    String actual = getMsg();//get message

        //assert
        assertFalse(no);
     //   assertEquals("資料有誤",actual);
    }

    @Test
    public void ConformTrue(){
        //arrange
        String user = "a1234",email = "anny60230@yahoo.com.tw";

        //act
        new Connection(1001,user).GET();//connect
       // String data = getMsg();
       // boolean actual = forget.Conform(data,user,email);//is conform

        //assert
     //   assertTrue(actual);

    }

    @Test
    public void ConformUserOrEmailIsMistake(){
        //arrange
        String user = "a1234",email = "ddd@yahoo.com.tw";//mistake input

        //act
        new Connection(1001,user).GET();//connect
     //   String data = getMsg();//connect data
    //    boolean no = forget.Conform(data,user,email);//is conform
    //    String actual = getMsg();//error message

        //assert
    //    assertFalse(no);
     //   assertEquals("資料有誤，請檢查輸入的資料為正確",actual);


    }

    @Test
    public void SendEmailTrue(){
        //arrange
        String user = "tester",email = "thunderwavela@gamil.com";

        //act
        forget.SendEmail(user,email);//is conform

        //assert

    }

    @Test
    public void SendEmailFalse(){
        //arrange
        String user = "tester",email = "thunderwavela@gamil.com";

        //act
        forget.SendEmail(user,email);//is conform

        //assert

    }
}