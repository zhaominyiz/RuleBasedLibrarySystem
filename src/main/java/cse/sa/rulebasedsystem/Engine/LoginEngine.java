package cse.sa.rulebasedsystem.Engine;

import cse.sa.rulebasedsystem.DaoImpl.AccountImpl;
import cse.sa.rulebasedsystem.Entities.UserEntity;
import cse.sa.rulebasedsystem.Knowleges.LoginRules;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;

@Service
public class LoginEngine {
    private AccountImpl accountDB;

    @Autowired
    public  LoginEngine(AccountImpl account){
        this.accountDB=account;
    }

    public String doLogin(String account,String password){
        JSONObject result=new JSONObject();
        String rule=new LoginRules().searchrule("LOGINSUCCESS");
        if(rule.equals("PASSWORD IN DATABASE EQUALS INPUT")){
            List<UserEntity> userEntityList=accountDB.getUserEntitiesByAccountName(account);
            if(userEntityList.size()==0){
                result.put("msg","ERROR_ACCOUNT");
                return result.toString();
            }else{
                UserEntity user=userEntityList.get(0);
                if(user.getPassword().equals(password)){
                    result.put("msg","SUCCESS");
                    result.put("name",user.getName());
                    result.put("type",user.getType());
                    return result.toString();
                }else{
                    result.put("msg","ERROR_PWD");
                    return result.toString();
                }
            }
        }else{
            result.put("msg","ERROR_SERVER");
            return result.toString();
        }
    }


}
