package cse.sa.rulebasedsystem.Controller;

import cse.sa.rulebasedsystem.Engine.BookEngine;
import cse.sa.rulebasedsystem.Engine.LoginEngine;
import org.json.JSONObject;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
public class BookController {
    final private BookEngine bookEngine;

    @Autowired
    public BookController(BookEngine bookEngine){
        this.bookEngine=bookEngine;
    }

    @RequestMapping(value = "service/order.book")
    public String receive_order_request(MultipartHttpServletRequest request) {
        String account=request.getParameter("account");
        String file=request.getParameter("img");
        //System.out.println("Account="+account);
        String s= bookEngine.translateBorrowBook(account,file);
        System.out.println(s);
        return s;
    }

    @RequestMapping(value = "service/return.book")
    public String receive_return_request(MultipartHttpServletRequest request) {
        String account=request.getParameter("account");
        String file=request.getParameter("img");
        return bookEngine.translateReturnBook(account,file);
    }

    @RequestMapping(value = "service/bookin.book", produces = "application/json;charset=UTF-8")
    public String bookIn(MultipartHttpServletRequest request) {

        String name = request.getParameter("name");
        String publisher = request.getParameter("publisher");
        String author = request.getParameter("author");
        MultipartFile file=request.getFile("img");
        String isbn=request.getParameter("isbn");
        String description=request.getParameter("description");
        String type=request.getParameter("type");
        String position=request.getParameter("position");
        String version=request.getParameter("version");
        //System.out.println(publ);
        Integer num;
        try {
            num = Integer.parseInt(request.getParameter("num"));
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "ERROR_INPUTDATA");
            return jsonObject.toString();
        }
        String publishID=request.getParameter("publishID");

        return bookEngine.bookIn(name,publisher,author,file,isbn,description,type,position,version,num,publishID);
    }

    @RequestMapping(value="service/find.book", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String findBook(@RequestBody String jsonstr){
        String msg="",bookType="";
        try{
            JSONObject jsonObject=new JSONObject(jsonstr);
            msg=jsonObject.getString("msg");
            bookType=jsonObject.getString("type");
            System.out.println("MSg"+msg+"TYPE"+bookType);
        }catch (Exception ex){
            ex.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "ERROR_INPUTDATA");
            return jsonObject.toString();
        }
        return bookEngine.findBook(msg,bookType);
    }

    @RequestMapping(value="service/finddetail.book", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String findDetail(@RequestBody String jsonstr){
        int id=0;
        try{
            JSONObject jsonObject=new JSONObject(jsonstr);
            id=Integer.parseInt(jsonObject.getString("isbn"));
            System.out.println("ID="+id);
        }catch (Exception ex){
            ex.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "ERROR_INPUTDATA");
            return jsonObject.toString();
        }
        String s=bookEngine.findDetail(id);
        System.out.println(s);
        return s;
    }

    @RequestMapping(value="service/delete.manage", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String deleteBook(@RequestBody String jsonstr){
        String isbn="";
        try{
            JSONObject jsonObject=new JSONObject(jsonstr);
            isbn=jsonObject.getString("isbn");
        }catch (Exception ex){
            ex.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "ERROR_INPUTDATA");
            return jsonObject.toString();
        }
        return bookEngine.deleteBook(isbn);
    }

    @RequestMapping(value = "service/update.manage", produces = "application/json;charset=UTF-8")
    public String updateBook(MultipartHttpServletRequest request) {
        try {
            String name = request.getParameter("name");
            String publisher = request.getParameter("publisher");
            String author = request.getParameter("author");
            MultipartFile img = request.getFile("img");
            String isbn = request.getParameter("isbn");
            String description = request.getParameter("description");
            String type = request.getParameter("type");
            String position = request.getParameter("position");
            String version = request.getParameter("version");
            Integer num = Integer.parseInt(request.getParameter("num"));
            String publishID = request.getParameter("publishID");
            return bookEngine.updateBook(name,publisher,author,img,isbn,description,type,position,version,num,publishID);
        }catch (Exception ex){
            ex.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "ERROR_INPUTDATA");
            return jsonObject.toString();
        }
    }


    @RequestMapping(value="service/borrow.manage", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String receiveBorrowList(@RequestBody String jsonstr){
        String account="",isbn="",name="",searchmethod="";
        try{
            JSONObject jsonObject=new JSONObject(jsonstr);
            account=jsonObject.getString("account");
            isbn=jsonObject.getString("isbn");
            searchmethod=jsonObject.getString("searchmethod");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println(account+";"+isbn+";"+searchmethod);
        String s= bookEngine.dogetBorrowList(account,isbn,searchmethod);
        System.out.println(s);
        return s;
    }

    @RequestMapping(value="service/show.book", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String getindexinfo(@RequestBody String jsonstr){
        System.out.println(jsonstr);
        return bookEngine.getLastFourBook();
    }
}
