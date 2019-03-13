package cse.sa.rulebasedsystem.Controller;

import cse.sa.rulebasedsystem.DaoImpl.AccountImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final AccountImpl accountDB;
    @Autowired
    TestController(AccountImpl accountDB){
        this.accountDB=accountDB;
    }
    @RequestMapping(value="testdatabase")
    public String Dotestdatabase(){
        return accountDB.getAllUser().get(0).getAccount();
    }
}
