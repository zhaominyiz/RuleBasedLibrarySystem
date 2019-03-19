package cse.sa.rulebasedsystem.Controller;

import cse.sa.rulebasedsystem.Engine.BookEngine;
import cse.sa.rulebasedsystem.Engine.LoginEngine;
import org.json.JSONObject;
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

    @RequestMapping(value = "service/order.book", produces = "application/json;charset=UTF-8")
    public String receive_order_request(MultipartHttpServletRequest request) {
        String account=request.getParameter("account");
        MultipartFile file=request.getFile("img");
        return bookEngine.translateBorrowBook(account,file);
    }

    @RequestMapping(value = "service/return.book", produces = "application/json;charset=UTF-8")
    public String receive_return_request(MultipartHttpServletRequest request) {
        String account=request.getParameter("account");
        MultipartFile file=request.getFile("img");
        return bookEngine.translateReturnBook(account,file);
    }

    @RequestMapping(value = "bookin.book", produces = "application/json;charset=UTF-8")
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

    @RequestMapping(value="find.book", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String findBook(@RequestBody String jsonstr){
        String msg="",bookType="";
        try{
            JSONObject jsonObject=new JSONObject(jsonstr);
            msg=jsonObject.getString("msg");
            bookType=jsonObject.getString("type");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return bookEngine.findBook(msg,bookType);
    }

    @RequestMapping(value="finddetail.book", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String findDetail(@RequestBody String jsonstr){
        String isbn="";
        try{
            JSONObject jsonObject=new JSONObject(jsonstr);
            isbn=jsonObject.getString("isbn");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return bookEngine.findDetail(isbn);
    }

    @RequestMapping(value="delete.manage", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String deleteBook(@RequestBody String jsonstr){
        String isbn="";
        try{
            JSONObject jsonObject=new JSONObject(jsonstr);
            isbn=jsonObject.getString("isbn");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return bookEngine.deleteBook(isbn);
    }

    @RequestMapping(value = "update.manage", produces = "application/json;charset=UTF-8")
    public String updateBook(MultipartHttpServletRequest request) {
        String name = request.getParameter("name");
        String publisher = request.getParameter("publisher");
        String author = request.getParameter("author");
        MultipartFile img = request.getFile("img");
        String isbn=request.getParameter("isbn");
        String description=request.getParameter("description");
        String type=request.getParameter("type");
        String position=request.getParameter("position");
        String version=request.getParameter("version");
        String num=request.getParameter("num");
        String publishID=request.getParameter("publishID");
        return bookEngine.updateBook(name,publisher,author,img,isbn,description,type,position,version,num,publishID);
    }
}
