package cse.sa.rulebasedsystem.Knowleges;

import java.util.HashMap;
import java.util.Map;

public class BookRules {
    public Map<String,String> knowledges;
    public BookRules(){
        knowledges=new HashMap<String,String>();
        knowledges.put("BORROWSUCCESS","BOOK HAS REST AND USER NOT FULL");
        knowledges.put("RETURNSUCCESS","USER NOT ERROR");
    }
    public String searchrule(String rule){
        return knowledges.get(rule);
    }
}
