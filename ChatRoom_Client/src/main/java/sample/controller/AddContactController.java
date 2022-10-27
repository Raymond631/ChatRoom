package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.Home;
import sample.util.Client;

import java.util.ArrayList;

public class AddContactController
{

    private Home home;

    @FXML
    private Label whetherFriend;

    @FXML
    private Label name;

    @FXML
    private Label selfIntroduction;

    private Client client;

    public AddContactController(Client client)
    {
        this.client = client;
        this.client.setAddContactController(this);
    }

    public void setApp(Home home)
    {
        this.home = home;
    }

    public void getContactName(String contactName)
    {
        name.setText(contactName);
    }

    public void setUserInfo(ArrayList<String> list)
    {
        String user = list.get(0);
        String intro = list.get(1);

        name.setText(user);
        selfIntroduction.setText(intro);
    }

    public void Add(ActionEvent actionEvent)
    {
        String user = name.getText();
        client.addFriend(home.GetName(), user);//前者是自己，后者是好友
    }

    public void setWhetherFriend(String s)
    {
        whetherFriend.setText(s);
    }
}
