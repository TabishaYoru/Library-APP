package org.iii.library.Base;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by saloliszt on 2017/2/9.
 */
public class MainTest {

    @Test
    public void getAPIDataTest(){

        Main main = new Main();

        assertTrue(main.getIsHandle());


    }

}