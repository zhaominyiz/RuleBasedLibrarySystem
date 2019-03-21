package cse.sa.rulebasedsystem.Knowleges;

import java.util.HashMap;
import java.util.Map;

public class BookRules {
    public Map<String,String> knowledges;
    public BookRules(){
        knowledges=new HashMap<String,String>();
        knowledges.put("BORROWSUCCESS","BOOK HAS REST AND USER NOT FULL");
        knowledges.put("RETURNSUCCESS","USER NOT ERROR");
        knowledges.put("SEARCH BORROW LIST BY ISBN","CONNECT BORROW LIST WITH BOOK AND ISBN EQUALS");
        knowledges.put("SEARCH BORROW LIST BY BOOK","CONNECT BORROW LIST WITH BOOK AND ISBN BOOKID");
    }
    public String searchrule(String rule){
        return knowledges.get(rule);
    }
}
