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
        if(account.equals("")||password.equals("")){
            result.put("msg","ERROR_INPUTDATA");
            return result.toString();
        }
        String rule=new LoginRules().searchrule("LOGINSUCCESS");
        if(rule.equals("PASSWORD IN DATABASE EQUALS INPUT")){
            return checkPWDfromDB(account,password);
        }else{
            result.put("msg","ERROR_SERVER");
            return result.toString();
        }
    }

    public String checkPWDfromDB(String account,String password){
        JSONObject result=new JSONObject();
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
    }

    public String doSignup(String account,String pwd,String name,String id){
        JSONObject result=new JSONObject();
        if(account.equals("")||pwd.equals("")||name.equals("")||id.equals("")){
            result.put("msg","ERROR_INPUTDATA");
            return result.toString();
        }
        String rule=new LoginRules().searchrule("SIGNUPSUCCESS");
        if(rule.equals("ACCOUNT IN DATABASE NOT REPEAT")){
            return doInsertAccount(account,pwd,name,id);
        }else{
            result.put("msg","ERROR_SERVER");
            return result.toString();
        }
    }

    private String doInsertAccount(String account, String pwd, String name, String id) {
        JSONObject result=new JSONObject();
        int cnt=accountDB.getUserEntitiesByAccountName(account).size();
        if(cnt==1){
            result.put("msg","ERROR_ACCOUNT");
            return result.toString();
        }else{
            UserEntity userEntity=new UserEntity();
            userEntity.setAccount(account);
            userEntity.setIdNo(id);
            userEntity.setPassword(pwd);
            userEntity.setName(name);
            userEntity.setType("user");
            accountDB.save(userEntity);
            result.put("msg","SUCCESS");
            return result.toString();
        }
    }
}
