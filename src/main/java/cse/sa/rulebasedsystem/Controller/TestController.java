package cse.sa.rulebasedsystem.Controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import cse.sa.rulebasedsystem.DaoImpl.AccountImpl;
import cse.sa.rulebasedsystem.Engine.BookEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Decoder;

import java.io.*;

@RestController
public class TestController {
    private final AccountImpl accountDB;
    private  final BookEngine bookEngine;
    @Autowired
    TestController(AccountImpl accountDB,BookEngine bookEngine){
        this.accountDB=accountDB;
        this.bookEngine=bookEngine;
    }
    @RequestMapping(value="testdatabase")
    public String Dotestdatabase(){
        return accountDB.getAllUser().get(0).getAccount();
    }

    @RequestMapping(value="testbar")
    public String Dotestbar(){
        try {
            return bookEngine.decodeBar("C:\\Users\\10447\\Desktop\\tmp.jpg");
        }catch (Exception ex){
            ex.printStackTrace();
            return "??";
        }
    }
    @RequestMapping(value = "service/getbar.book")
    public String receive_order_request(MultipartHttpServletRequest request) {

        String file=request.getParameter("img");
        try{
            if(Base64ToImage(file,"tmp.jpg")){
                String s= bookEngine.decodeBar("tmp.jpg");
                //System.out.println(s+"!!!!!!!!!!!!");
                return s;
            }else{
                return "???";
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return "照片不清晰";
        }
    }

    public static boolean Base64ToImage(String imgStr,String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片



        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }

            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            return false;
        }

    }

}
