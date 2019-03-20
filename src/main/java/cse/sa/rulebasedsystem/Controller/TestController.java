package cse.sa.rulebasedsystem.Controller;

import cse.sa.rulebasedsystem.DaoImpl.AccountImpl;
import cse.sa.rulebasedsystem.Engine.BookEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value="testScanfImg")
    public String DotestScan(){
        return bookEngine.decodeBar("E:\\go.jpg");
    }
}
