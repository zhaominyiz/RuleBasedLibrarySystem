package cse.sa.rulebasedsystem.Knowleges;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginRules {
    public Map<String,String> knowledges;
    public LoginRules(){
        knowledges=new HashMap<String,String>();
        knowledges.put("LOGINSUCCESS","PASSWORD IN DATABASE EQUALS INPUT");
    }
    public String searchrule(String rule){
        return knowledges.get(rule);
    }
}
