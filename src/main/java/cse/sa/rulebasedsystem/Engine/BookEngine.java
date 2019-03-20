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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.google.zxing.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

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


    public String bookIn(String name,String publisher,String author,MultipartFile file,String isbn,String description,
                           String type,String position,String version,Integer num,String publishID){
        BookEntity newBook=new BookEntity();
        newBook.setName(name);
        newBook.setPublisher(publisher);
        newBook.setAuthor(author);
        String outurl="";
        String fileName = file.getOriginalFilename();
        String filePath = ("src/main/resources/static/book_img");//request.getSession().getServletContext().getRealPath("/uploader");
        List<BookEntity> booklist = bookDB.getBookEntitiesByPublisherID(publishID);
        if (booklist.size() != 0) {
            JSONObject result = new JSONObject();
            result.put("msg", "ERROR_SAVED");
            return result.toString();
        }
        try {
            File targetFile = new File(filePath);
            if(!targetFile.exists()){
                targetFile.mkdirs();
            }
            String fileurl=filePath+"\\"+fileName;
            outurl="src/main/resources/static/book_img"+fileName;
            FileOutputStream out = new FileOutputStream(fileurl);
            out.write(file.getBytes());
            out.flush();
            out.close();
            System.out.print("hahah"+outurl);
        }catch (Exception e){
            e.printStackTrace();
            outurl="ERROR";
        }
        newBook.setImg(outurl);
        newBook.setIsbn(isbn);
        newBook.setDescription(description);
        newBook.setType(type);
        newBook.setPosition(position);
        newBook.setVersion(version);
        newBook.setNum(num);
        Timestamp d = new Timestamp(System.currentTimeMillis());
        newBook.setAddTime(d);
        newBook.setPublishId(publishID);

        try{
            bookDB.save(newBook);
            if (outurl.equals("ERROR")) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg", "WARNING_IMG");
                return jsonObject.toString();
            }
            else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg", "SUCCESS");
                return jsonObject.toString();
            }
        }catch (Exception e){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("msg","ERROR_SERVER");
            return jsonObject.toString();
        }
    }
    public String findBook(String msg,String bookType){
        JSONObject re=new JSONObject();
        JSONArray booklistbooklist=new JSONArray();
        try{
            int cnt=0;
            List<BookEntity> tmpList=bookDB.findName(msg,bookType);
            cnt+=tmpList.size();
            System.out.println(cnt);
            for(BookEntity bookTmp:tmpList){
                JSONObject reTmp=new JSONObject();
                reTmp.put("id",bookTmp.getId());
                reTmp.put("bookname",bookTmp.getName());
                reTmp.put("img",bookTmp.getImg());
                reTmp.put("description",bookTmp.getDescription());
                reTmp.put("type",bookTmp.getType());
                reTmp.put("position",bookTmp.getPosition());
                reTmp.put("num",bookTmp.getNum());
                reTmp.put("res",bookTmp.getRes());
                reTmp.put("author",bookTmp.getAuthor());
                reTmp.put("publisher",bookTmp.getPublisher());
                reTmp.put("isbn",bookTmp.getIsbn());
                reTmp.put("version",bookTmp.getVersion());
                booklistbooklist.put(reTmp);
            }
            List<BookEntity> tmpList2=bookDB.findAuthor(msg,bookType);
            cnt+=tmpList2.size();
            System.out.println(cnt);
            for(BookEntity bookTmp:tmpList2){
                JSONObject reTmp=new JSONObject();
                reTmp.put("id",bookTmp.getId());
                reTmp.put("bookname",bookTmp.getName());
                reTmp.put("img",bookTmp.getImg());
                reTmp.put("description",bookTmp.getDescription());
                reTmp.put("type",bookTmp.getType());
                reTmp.put("position",bookTmp.getPosition());
                reTmp.put("num",bookTmp.getNum());
                reTmp.put("res",bookTmp.getRes());
                reTmp.put("author",bookTmp.getAuthor());
                reTmp.put("publisher",bookTmp.getPublisher());
                reTmp.put("isbn",bookTmp.getIsbn());
                reTmp.put("version",bookTmp.getVersion());
                booklistbooklist.put(reTmp);
            }
            List<BookEntity> tmpList3=bookDB.findPublisher(msg,bookType);
            cnt+=tmpList3.size();
            System.out.println(cnt);
            for(BookEntity bookTmp:tmpList3){
                JSONObject reTmp=new JSONObject();
                reTmp.put("id",bookTmp.getId());
                reTmp.put("bookname",bookTmp.getName());
                reTmp.put("img",bookTmp.getImg());
                reTmp.put("description",bookTmp.getDescription());
                reTmp.put("type",bookTmp.getType());
                reTmp.put("position",bookTmp.getPosition());
                reTmp.put("num",bookTmp.getNum());
                reTmp.put("res",bookTmp.getRes());
                reTmp.put("author",bookTmp.getAuthor());
                reTmp.put("publisher",bookTmp.getPublisher());
                reTmp.put("isbn",bookTmp.getIsbn());
                reTmp.put("version",bookTmp.getVersion());
                booklistbooklist.put(reTmp);
            }
            List<BookEntity> tmpList4=bookDB.findIsbn(msg,bookType);
            cnt+=tmpList4.size();
            System.out.println(cnt);
            for(BookEntity bookTmp:tmpList4){
                JSONObject reTmp=new JSONObject();
                reTmp.put("id",bookTmp.getId());
                reTmp.put("bookname",bookTmp.getName());
                reTmp.put("img",bookTmp.getImg());
                reTmp.put("description",bookTmp.getDescription());
                reTmp.put("type",bookTmp.getType());
                reTmp.put("position",bookTmp.getPosition());
                reTmp.put("num",bookTmp.getNum());
                reTmp.put("res",bookTmp.getRes());
                reTmp.put("author",bookTmp.getAuthor());
                reTmp.put("publisher",bookTmp.getPublisher());
                reTmp.put("isbn",bookTmp.getIsbn());
                reTmp.put("version",bookTmp.getVersion());
                booklistbooklist.put(reTmp);
            }
            re.put("msg","SUCCESS");
            re.put("cnt",cnt);
            re.put("booklist",booklistbooklist);
            return re.toString();
        }catch (Exception e){
            e.printStackTrace();
            re.put("msg","ERROR_SERVER");
            return re.toString();
        }
    }
    public String findDetail(String isbn){
        JSONObject re=new JSONObject();
        try {
            BookEntity currentBook = bookDB.findByIsbn(isbn);
            if(currentBook==null){
                re.put("msg","ERROR_NOTFOUND");
                return re.toString();
            }
            re.put("msg","SUCCESS");
            re.put("bookname",currentBook.getName());
            re.put("img",currentBook.getImg());
            re.put("description",currentBook.getDescription());
            re.put("type",currentBook.getType());
            re.put("position",currentBook.getPosition());
            re.put("num",currentBook.getNum());
            re.put("res",currentBook.getRes());
            re.put("author",currentBook.getAuthor());
            re.put("publisher",currentBook.getPublisher());
            re.put("isbn",currentBook.getIsbn());
            re.put("version",currentBook.getVersion());
            return re.toString();
        }catch (Exception e){
            e.printStackTrace();
            re.put("msg","ERROR_SERVER");
            return re.toString();
        }
    }
    public String deleteBook(String isbn){
        return "1";
    }
    public String updateBook(String name,String publisher,String author,MultipartFile img,String isbn,String description,
                         String type,String position,String version,String num,String publishID){
        return "1";
    }
}
