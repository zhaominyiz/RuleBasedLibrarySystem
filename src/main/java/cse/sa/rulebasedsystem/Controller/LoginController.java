package cse.sa.rulebasedsystem.Controller;

import cse.sa.rulebasedsystem.Engine.LoginEngine;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    private LoginEngine loginEngine;

    @Autowired
    public LoginController(LoginEngine loginEngine){
        this.loginEngine=loginEngine;
    }
    @RequestMapping(value="service/login.login", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String logincontroller(@RequestBody String jsonstr){
        String account="",password="";
        try{
            JSONObject jsonObject=new JSONObject(jsonstr);
            account=jsonObject.getString("account");
            password=jsonObject.getString("password");
            System.out.println(account+";"+password);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return loginEngine.doLogin(account,password);
    }

    @RequestMapping(value="service/signup.login", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public String signupcontroller(@RequestBody String jsonstr){
        String account="",password="",name="",id="";
        try{
            JSONObject jsonObject=new JSONObject(jsonstr);
            account=jsonObject.getString("account");
            password=jsonObject.getString("password");
            name=jsonObject.getString("name");
            //System.out.println("name="+name);
            id=jsonObject.getString("id");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return loginEngine.doSignup(account,password,name,id);
    }
}
