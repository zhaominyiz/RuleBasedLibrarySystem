package cse.sa.rulebasedsystem.Knowleges;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginRules {
    public Map<String,String> knowledges;
    public LoginRules(){
        knowledges=new HashMap<String,String>();
        knowledges.put("LOGINSUCCESS","PASSWORD IN DATABASE EQUALS INPUT");
        knowledges.put("SIGNUPSUCCESS","ACCOUNT IN DATABASE NOT REPEAT");
    }
    public String searchrule(String rule){
        return knowledges.get(rule);
    }
}
