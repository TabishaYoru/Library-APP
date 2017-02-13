package org.iii.library.Base;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class LoginTest{ // 8 unit test

    private Login login ;

    @Before
    public void setUp(){
        login = new Login();
    }


    //test: user and pass is null
    @Test
    public void testBothNull(){

        //arrange
        String user = "", pass = "";

        //act
        boolean actual = login.valid(user,pass);//error

        //assert
        assertFalse(actual);//false

    }

    //test: user is null
    @Test
    public void testUserNull(){

        //arrange
        String user = "", pass = "1111";

        //act
        boolean actual = login.valid(user,pass);//error

        //assert
        assertFalse(actual);//false


    }

    //test: pass is null
    @Test
    public void testPasswordNull(){

        //arrange
        String user = "a1234", pass = "";

        //act
        boolean actual = login.valid(user,pass);//error

        //assert
        assertFalse(actual);//false


    }

    //test: both user and pass are have values
    @Test
    public void testNoNull(){

        //arrange
        String user = "a1234", pass = "a1234";

        //act
        boolean actual = login.valid(user,pass);//correct

        //assert
        assertTrue(actual);//true

    }

    //test： get account is null
    @Test
    public void testGetAccountIsNull(){
        //arrange

        //act
        login.valid("","");//correct

        //assert
        assertNull(login.getAccount());//correct

    }

    //test： get secret is null
    @Test
    public void testGetSecretIsNull(){
        //arrange

        //act
        login.valid("","111");//correct

        //assert
        assertNull(null,login.getSecret());//correct
    }

    //test： get account is has value
    @Test
    public void testGetAccountNotNull(){
        //arrange

        //act
        login.valid("1234","1234");//correct

        //assert
        assertNotNull(login.getAccount());//correct

    }

    //test： get secret is has value
    @Test
    public void testGetSecretNotNull(){
        //arrange

        //act
        login.valid("1234","111");//correct

        //assert
        assertNotNull(login.getSecret());//correct
    }

}