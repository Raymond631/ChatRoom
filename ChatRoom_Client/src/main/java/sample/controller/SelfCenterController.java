package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Home;
import sample.util.Client;

import java.io.File;
import java.util.ArrayList;

public class SelfCenterController
{

    Home home;

    @FXML
    private TextField address;

    @FXML
    private Button submit;

    @FXML
    private TextField phone;

    @FXML
    private TextField introduction;

    @FXML
    private TextField username;

    @FXML
    private Label photo;

    private Client client;

    public SelfCenterController(Client client)
    {
        this.client = client;
        this.client.setSelfCenterController(this);
    }

    @FXML
    void Submit(ActionEvent event) //更新个人信息
    {
        String name = username.getText();
        String intro = introduction.getText();
        String adr = address.getText();
        String pho = phone.getText();

        String sql = "update user set username ='" + name + "',introduction ='" + intro + "',address='" + adr + "',phone='" + pho + "' where username ='" + home.GetName() + "'";
        client.updateInfo(sql);
    }

    @FXML
    void uploadAvatar(ActionEvent event)//上传头像
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择头像");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Images", "*.jpg;*.jpeg;*.png"));
        Stage fileStage = new Stage();
        File file = fileChooser.showOpenDialog(fileStage);
        client.uploadAvatar(file);
    }

    public void setApp(Home home)
    {
        this.home = home;
    }

    public void setUserInfo(ArrayList<String> list)
    {
        String name = list.get(0);
        String intro = list.get(1);
        String adr = list.get(2);
        String pho = list.get(3);

        username.setText(name);
        introduction.setText(intro);
        address.setText(adr);
        phone.setText(pho);
    }

    public Label getPhoto()
    {
        return photo;
    }
}
