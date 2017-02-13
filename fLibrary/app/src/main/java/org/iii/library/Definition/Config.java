package org.iii.library.Definition;

public class Config {

    public String ip = "10.10.103.113",//Dead code
            port = "3000";


    /*public String Setting(){
        Properties p = new Properties();
        FileInputStream fs;

        try{
            fs = new FileInputStream("app/src/config.properties");
            p.load(fs);
            ip = p.getProperty("IP");
            port = p.getProperty("PORT");
            m = p.getProperty("M");
            b = p.getProperty("B");
            n = p.getProperty("N");
            ra = p.getProperty("RA");
            rm = p.getProperty("RM");

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            return "I/O ERROR";
        }finally {
            p.clear();
        }
        return null;
    }*/

}
