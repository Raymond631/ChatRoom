package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sample.Home;
import sample.util.Client;

public class LoginController
{

    @FXML
    private TextField name;
    @FXML
    private PasswordField password;
    @FXML
    private Label message;

    private Home home;
    private Client client;

    public LoginController(Client client)
    {
        this.client = client;
    }

    public void Enroll(ActionEvent actionEvent)
    {
        //页面跳转
        home.ShowEnroll();
    }

    public void Login(ActionEvent actionEvent)
    {
        message.setText("Logging...");

        //获取账号和密码信息
        String tempName = name.getText();
        String tempPassword = password.getText();

        //下面用来检验是否存在该用户且用户名和密码是否正确
        if (client.logIn(tempName, tempPassword))
        {
            //下面先保存一下账号
            home.KeepName(tempName);
            //页面跳转
            home.ShowContact();//显示好友列表
        }
        else
        {
            //应在fxml显示
            System.out.println("登录失败！");
        }
    }

    public void setApp(Home home)
    {
        this.home = home;
    }

}