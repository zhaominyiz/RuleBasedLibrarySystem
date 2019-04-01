package cse.sa.rulebasedsystem.Engine;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import cse.sa.rulebasedsystem.DaoImpl.AccountImpl;
import cse.sa.rulebasedsystem.DaoImpl.BookImpl;
import cse.sa.rulebasedsystem.DaoImpl.BookRecordImpl;
import cse.sa.rulebasedsystem.Entities.BookEntity;
import cse.sa.rulebasedsystem.Entities.BookrecordEntity;
import cse.sa.rulebasedsystem.Entities.UserEntity;
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

import static cse.sa.rulebasedsystem.Controller.TestController.Base64ToImage;

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

    public String translateBorrowBook(String account,String file){
        //JSONObject result=new JSONObject();
        if(account==null||file==null){
            return "ERROR_INPUTDATA";
        }
        try {
            String rule=new BookRules().searchrule("BORROWSUCCESS");
            if(rule.equals("BOOK HAS REST AND USER NOT FULL")) {
                return doSolveBorrowBook(account,file);
            }else{
                return "ERROR_SERVER";
            }

        }catch (Exception ex){
            ex.printStackTrace();
            return "ERROR_IMG";
        }
    }
    public String doSolveBorrowBook(String account,String file){
        //JSONObject result=new JSONObject();
        int cnt=accountDB.getUserEntitiesByAccountName(account).size();
        if(cnt==0){
            return "ERROR_ACCOUNT";
        }
        List<BookrecordEntity>list=bookrecordDB.getRecordByUserName(account);
        if(((List) list).size()>=10){
            return "ERROR_FULL";
        }else{
            try {
                String bar="";
                if(Base64ToImage(file,"tmp.jpg"))
                    bar=decodeBar("tmp.jpg");
                System.out.println("FIND BAR="+bar);
                List<BookEntity>booklist=bookDB.getBookEntitiesByPublisherID(bar);
                if(booklist==null||booklist.size()==0){
                    return "ERROR_NOBOOK";
                }
                BookEntity book=booklist.get(0);
                System.out.println(book.getName());
                int id=book.getId();
                if(list!=null&&list.size()!=0)
                    for(BookrecordEntity f :list){
                        if(f.getBookId()==id){
                            //System.out.println("BORROWED");
                            return "BORROWED";
                        }
                    }
                if(book.getRes()==0){//没有书了
                    return "ERROR_NOBOOK";
                }
                book.setRes(book.getRes()-1);
                bookDB.save(book);
                System.out.println("HERE");
                BookrecordEntity bookrecordEntity=new BookrecordEntity();
                bookrecordEntity.setBookId(book.getId());
                Timestamp d = new Timestamp(System.currentTimeMillis());
                bookrecordEntity.setDate(d);
                bookrecordEntity.setUserName(account);
                bookrecordDB.save(bookrecordEntity);
                return "SUCCESS";
            }catch (Exception ex){
                return "ERROR_IMG";
            }
        }
    }

    public String translateReturnBook(String username,String file){
        //JSONObject result=new JSONObject();
        if(username==null||file==null){
            return "ERROR_INPUTDATA";
        }
        try {
            String rule=new BookRules().searchrule("RETURNSUCCESS");
            if(rule.equals("HAS RECORD")) {
                return doSolveReturnBook(username,file);
            }else{
                return "ERROR_SERVER";
            }

        }catch (Exception ex){
            ex.printStackTrace();
            return "ERROR_IMG";
        }
    }
    public String doSolveReturnBook(String account,String file){
        int cnt=accountDB.getUserEntitiesByAccountName(account).size();
        if(cnt==0){
            return "ERROR_ACCOUNT";
        }
        List<BookrecordEntity>list=bookrecordDB.getRecordByUserName(account);
        try {
            String bar = "";
            if(Base64ToImage(file,"tmp.jpg"))
                bar=decodeBar("tmp.jpg");
            System.out.println("获得条形码"+bar);
            List<BookEntity>booklist=bookDB.getBookEntitiesByPublisherID(bar);
            if(booklist==null||booklist.size()==0){
                return "ERROR_NOBOOK";
            }
            BookEntity book=booklist.get(0);
            int book_id=book.getId();
            System.out.println("借的书的ID"+book_id);
            List<BookrecordEntity>recordlist=bookrecordDB.getRecordByUserNameANDBookID(account,book_id);
            if(recordlist.size()==0){
                return "ERROR_NOTBORROW";
            }
            System.out.println("借书记录"+recordlist.size());
            for (BookrecordEntity a:
                 recordlist) {
                bookrecordDB.delete(a);
            }
            book.setRes(book.getRes()+1);
            bookDB.save(book);
            return "SUCCESS";
        }catch (Exception ex){
            return "ERROR_IMG";
            }
    }
    //解析
    public  String decodeBar(String imgPath)throws Exception{
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
            //hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            result = new MultiFormatReader().decode(bitmap,hints);
            return result.getText();
        } catch (Exception e) {
            throw  e;
        }
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

    //录入书籍
    public String bookIn(String name,String publisher,String author,MultipartFile file,String isbn,String description,
                           String type,String position,String version,Integer num,String publishID){
        BookEntity newBook=new BookEntity();
        newBook.setName(name);
        newBook.setPublisher(publisher);
        newBook.setAuthor(author);
        String outurl="";
        String fileurl="";
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
            fileurl=filePath+"\\"+fileName;
            outurl="src/main/resources/static/book_img"+fileName;
            FileOutputStream out = new FileOutputStream(fileurl);
            out.write(file.getBytes());
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            outurl="ERROR";
        }
        newBook.setImg("book_img/"+fileName);
        newBook.setIsbn(isbn);
        newBook.setDescription(description);
        newBook.setType(type);
        newBook.setPosition(position);
        newBook.setVersion(version);
        newBook.setNum(num);
        newBook.setRes(num);
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

    //寻找一本书
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
                reTmp.put("publishID",bookTmp.getPublishId());
                booklistbooklist.put(reTmp);
            }
            List<BookEntity> tmpList2=bookDB.findAuthor(msg,bookType);
            cnt+=tmpList2.size();
            System.out.println(cnt);
            for(BookEntity bookTmp:tmpList2){
                JSONObject reTmp=new JSONObject();
                reTmp.put("id",bookTmp.getId());
                reTmp.put("bookname",bookTmp.getName());
                reTmp.put("publishID",bookTmp.getPublishId());
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
                reTmp.put("publishID",bookTmp.getPublishId());
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
                reTmp.put("publishID",bookTmp.getPublishId());
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

    //寻找一本书的详细信息
    public String findDetail(int isbn){
        JSONObject re=new JSONObject();
        try {
            BookEntity currentBook = bookDB.findById(isbn);
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
            re.put("publishID",currentBook.getPublishId());
            return re.toString();
        }catch (Exception e){
            e.printStackTrace();
            re.put("msg","ERROR_SERVER");
            return re.toString();
        }
    }
    public String deleteBook(String isbn){
        BookEntity currentBook=bookDB.findByIsbn(isbn);
        JSONObject re=new JSONObject();
        if(currentBook==null){
            re.put("msg","ERROR_NOTFOUND");
            return re.toString();
        }
        try{
            bookDB.delete(currentBook);
            re.put("msg","SUCCESS");
            return re.toString();
        }catch (Exception e){
            e.printStackTrace();
            re.put("msg","ERROR_SERVER");
            return re.toString();
        }
    }
    public String updateBook(String name,String publisher,String author,MultipartFile file,String isbn,String description,
                         String type,String position,String version,Integer num,String publishID){
        BookEntity newBook=bookDB.findByIsbn(isbn);
        JSONObject re=new JSONObject();
        //System.out.println("GA?");
        if(newBook==null){
            re.put("msg","ERROR_NOTFOUND");
            return re.toString();
        }
        if(!name.equals(""))
            newBook.setName(name);
        if(!publisher.equals(""))
            newBook.setPublisher(publisher);
        if(!author.equals(""))
            newBook.setAuthor(author);
        //System.out.println("GA??"+newBook.getDescription());
        if(!description.equals("")) {
            newBook.setDescription(description);
            //System.out.println("DUDURU");
        }
        newBook.setRes(num);
        if(!type.equals(""))
            newBook.setType(type);
        if(!position.equals(""))
            newBook.setPosition(position);
        if(!version.equals(""))
            newBook.setVersion(version);
        if(!num.equals(""))
            newBook.setNum(num);
        if(!publishID.equals(""))
            newBook.setPublishId(publishID);
        try{
            System.out.println(newBook.getDescription());
            bookDB.save(newBook);
            re.put("msg","SUCCESS");
            return re.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            re.put("msg","ERROR_SERVER");
            return re.toString();
        }
    }

    public String dogetBorrowList(String account, String isbn, String searchmethod) {
        JSONObject result=new JSONObject();
        if(searchmethod==null){
            result.put("msg","ERROR_INPUTDATA");
            return result.toString();
        }
        if(searchmethod.equals("BYBOOK")){
            if(isbn==null){
                result.put("msg","ERROR_INPUTDATA");
                return result.toString();
            }
            String rule=new BookRules().searchrule("SEARCH BORROW LIST BY ISBN");
            if(rule.equals("CONNECT BORROW LIST WITH BOOK AND ISBN EQUALS")) {
                return getBookListByISBN(isbn);
            }else{
                result.put("msg","ERROR_SERVER");
                return result.toString();
            }

        }else{
            if(account==null){
                result.put("msg","ERROR_INPUTDATA");
                return result.toString();
            }
            String rule=new BookRules().searchrule("SEARCH BORROW LIST BY BOOK");
            if(rule.equals("CONNECT BORROW LIST WITH BOOK AND ISBN BOOKID")) {
                return getBookListByUserID(account);
            }else{
                result.put("msg","ERROR_SERVER");
                return result.toString();
            }
        }

    }

    public String getBookListByISBN(String isbn){
        JSONObject result=new JSONObject();
        JSONArray tmparr=new JSONArray();
        BookEntity book=bookDB.findByIsbn(isbn);
        if(book==null){
            result.put("msg","ERROR_ISBN");
            return result.toString();
        }
        int id=book.getId();
        List<BookrecordEntity>f=bookrecordDB.getRecordByBookId(id);

        for(BookrecordEntity tmp : f){
            JSONObject tt=new JSONObject();
            tt.put("id",book.getId());
            tt.put("isbn",book.getIsbn());
            tt.put("name",book.getName());
            tt.put("user",tmp.getUserName());
            tt.put("datetime",tmp.getDate());
            tt.put("img",book.getImg());
            tt.put("publisher",book.getPublisher());
            tt.put("author",book.getAuthor());
            tmparr.put(tt);
        }
        result.put("cnt",f.size());
        result.put("msg","SUCCESS");
        result.put("booklist",tmparr);
        return result.toString();
    }

    public String getBookListByUserID(String userid){
        JSONObject result=new JSONObject();
        JSONArray tmparr=new JSONArray();
        result.put("msg","SUCCESS");
        List<BookrecordEntity>f=bookrecordDB.getRecordByUserName(userid);
        if(f==null||f.size()==0){
            return  result.toString();
        }
        //System.out.println("DRDDR");
        for (BookrecordEntity b:
             f) {
            List<BookEntity> list=bookDB.getBookEntitiesByID(b.getBookId());
            if(list==null||list.size()==0)continue;
            BookEntity book=list.get(0);
            JSONObject tt=new JSONObject();
            tt.put("id",book.getId());
            tt.put("isbn",book.getIsbn());
            tt.put("name",book.getName());
            tt.put("user",b.getUserName());
            tt.put("datetime",b.getDate());
            tt.put("img",book.getImg());
            tt.put("publisher",book.getPublisher());
            tt.put("author",book.getAuthor());
            System.out.println(book.getName());
            tmparr.put(tt);
        }
        result.put("cnt",f.size());
        result.put("msg","SUCCESS");
        result.put("booklist",tmparr);
        System.out.println(tmparr.toString());
        return result.toString();
    }

    public String getLastFourBook() {
        JSONObject result=new JSONObject();
        JSONArray tmparr=new JSONArray();
        List<BookEntity>bookEntityList=bookDB.getAllbooks();
        int cnt=0;
        for(int i=bookEntityList.size()-1;i>=bookEntityList.size()-4;i--){
            if(i==-1)break;
            cnt++;
            BookEntity bookTmp=bookEntityList.get(i);
            JSONObject tt=new JSONObject();
            tt.put("id",bookTmp.getId());
            tt.put("bookname",bookTmp.getName());
            tt.put("img",bookTmp.getImg());
            tt.put("description",bookTmp.getDescription());
            tt.put("type",bookTmp.getType());
            tt.put("position",bookTmp.getPosition());
            tt.put("num",bookTmp.getNum());
            tt.put("res",bookTmp.getRes());
            tt.put("author",bookTmp.getAuthor());
            tt.put("publisher",bookTmp.getPublisher());
            tt.put("isbn",bookTmp.getIsbn());
            tt.put("version",bookTmp.getVersion());
            tmparr.put(tt);
        }
        result.put("cnt",cnt);
        result.put("booklist",tmparr);
        result.put("msg","SUCCESS");
        return result.toString();
    }
}
