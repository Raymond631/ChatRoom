package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import sample.Home;
import sample.util.Client;

public class EnrollController
{

    @FXML
    private TextField name;
    @FXML
    private TextField password;
    @FXML
    private TextField confirmPassword;

    private Home home;

    private Client client;

    public EnrollController(Client client)
    {
        this.client = client;
    }

    public void Enroll(ActionEvent actionEvent)
    {
        String tempName = name.getText();
        String tempPassword = password.getText();
        String rePassword = confirmPassword.getText();
        if (!tempPassword.equals(rePassword))
        {
            //此处应在fxml显示
            System.out.println("密码不一致");
        }
        else if (client.register(tempName, tempPassword))
        {
            home.KeepName(tempName);
            home.ShowContact();//显示好友列表
        }
        else
        {
            //应在fxml显示
            System.out.println("注册失败！");
        }
    }

    public void ToLogin(ActionEvent actionEvent)
    {
        home.Login();
    }

    public void setApp(Home home)
    {
        this.home = home;
    }
}
