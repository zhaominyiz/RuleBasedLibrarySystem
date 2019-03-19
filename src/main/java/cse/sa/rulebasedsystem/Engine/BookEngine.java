package cse.sa.rulebasedsystem.Engine;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import cse.sa.rulebasedsystem.DaoImpl.AccountImpl;
import cse.sa.rulebasedsystem.DaoImpl.BookImpl;
import cse.sa.rulebasedsystem.DaoImpl.BookRecordImpl;
import cse.sa.rulebasedsystem.Entities.BookEntity;
import cse.sa.rulebasedsystem.Entities.BookrecordEntity;
import cse.sa.rulebasedsystem.Knowleges.BookRules;
import cse.sa.rulebasedsystem.Knowleges.LoginRules;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.zxing.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;

@Service
public class BookEngine {
    private AccountImpl accountDB;
    private BookImpl bookDB;
    private BookRecordImpl bookrecordDB;
    @Autowired
    public  BookEngine(AccountImpl account,BookImpl book,BookRecordImpl bookRecord){

        this.accountDB=account;
        this.bookDB=book;
        this.bookrecordDB=bookRecord;
    }

    public String translateBorrowBook(String account,MultipartFile file){
        JSONObject result=new JSONObject();
        if(account==null||file==null){
            result.put("msg","ERROR_INPUTDATA");
            return result.toString();
        }
        try {
            String rule=new BookRules().searchrule("BORROWSUCCESS");
            if(rule.equals("BOOK HAS REST AND USER NOT FULL")) {
                return doSolveBorrowBook(account,file);
            }else{
                result.put("msg","ERROR_SERVER");
                return result.toString();
            }

        }catch (Exception ex){
            ex.printStackTrace();
            result.put("msg","ERROR_IMG");
            return result.toString();
        }
    }
    public String doSolveBorrowBook(String account,MultipartFile file){
        JSONObject result=new JSONObject();
        int cnt=accountDB.getUserEntitiesByAccountName(account).size();
        if(cnt==0){
            result.put("msg","ERROR_ACCOUNT");
            return result.toString();
        }
        List<BookrecordEntity>list=bookrecordDB.getRecordByUserName(account);
        if(((List) list).size()>=10){
            result.put("msg","ERROR_FULL");
            return result.toString();
        }else{
            try {
                String bar = decodeBar(file.getInputStream());
                List<BookEntity>booklist=bookDB.getBookEntitiesByPublisherID(bar);
                if(booklist==null||booklist.size()==0){
                    result.put("msg","ERROR_NOBOOK");
                    return result.toString();
                }
                BookEntity book=booklist.get(0);
                if(book.getRes()==0){//没有书了
                    result.put("msg","ERROR_NOBOOK");
                    return result.toString();
                }
                book.setRes(book.getRes()-1);
                bookDB.save(book);
                BookrecordEntity bookrecordEntity=new BookrecordEntity();
                bookrecordEntity.setBookId(book.getId());
                Timestamp d = new Timestamp(System.currentTimeMillis());
                bookrecordEntity.setDate(d);
                bookrecordEntity.setUserName(account);
                bookrecordDB.save(bookrecordEntity);
                result.put("msg","SUCCESS");
                return result.toString();
            }catch (Exception ex){
                result.put("msg","ERROR_IMG");
                return result.toString();
            }
        }
    }

    public String translateReturnBook(String username,MultipartFile file){
        JSONObject result=new JSONObject();
        if(username==null||file==null){
            result.put("msg","ERROR_INPUTDATA");
            return result.toString();
        }
        try {
            String rule=new BookRules().searchrule("RETURNSUCCESS");
            if(rule.equals("HAS RECORD")) {
                return doSolveReturnBook(username,file);
            }else{
                result.put("msg","ERROR_SERVER");
                return result.toString();
            }

        }catch (Exception ex){
            ex.printStackTrace();
            result.put("msg","ERROR_IMG");
            return result.toString();
        }
    }
    public String doSolveReturnBook(String account,MultipartFile file){
        JSONObject result=new JSONObject();
        int cnt=accountDB.getUserEntitiesByAccountName(account).size();
        if(cnt==0){
            result.put("msg","ERROR_ACCOUNT");
            return result.toString();
        }
        List<BookrecordEntity>list=bookrecordDB.getRecordByUserName(account);
        try {
            String bar = decodeBar(file.getInputStream());
            List<BookEntity>booklist=bookDB.getBookEntitiesByPublisherID(bar);
            if(booklist==null||booklist.size()==0){
                    result.put("msg","ERROR_NOBOOK");
                    return result.toString();
            }
            BookEntity book=booklist.get(0);
            int book_id=book.getId();
            List<BookrecordEntity>recordlist=bookrecordDB.getRecordByUserNameANDBookID(account,book_id);
            if(recordlist.size()==0){
                result.put("msg","ERROR_NOTBORROW");
                return result.toString();
            }
            for (BookrecordEntity a:
                 recordlist) {
                bookrecordDB.delete(a);
            }
            book.setRes(book.getRes()+1);
            bookDB.save(book);
            result.put("msg","SUCCESS");
            return result.toString();
        }catch (Exception ex){
                result.put("msg","ERROR_IMG");
                return result.toString();
            }
    }
    //解析
    public  String decodeBar(String imgPath){
        BufferedImage image = null;
        Result result = null;
        try {
            image = ImageIO.read(new File(imgPath));
            if (image == null) {
                System.out.println("the decode image may be not exit.");
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            result = new MultiFormatReader().decode(bitmap,hints);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
    public  String decodeBar(InputStream stream)throws Exception{
        BufferedImage image = null;
        Result result = null;
        try {
            image = ImageIO.read(stream);
            if (image == null) {
                System.out.println("the decode image may be not exit.");
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            result = new MultiFormatReader().decode(bitmap,hints);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
}
