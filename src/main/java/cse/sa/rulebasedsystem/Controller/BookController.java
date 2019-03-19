package cse.sa.rulebasedsystem.Controller;

import cse.sa.rulebasedsystem.Engine.BookEngine;
import cse.sa.rulebasedsystem.Engine.LoginEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
