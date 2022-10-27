package sample.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import sample.Home;
import sample.model.Contact;
import sample.util.Client;

import java.io.IOException;

public class ContactController
{

    private Home home;

    //这个是获取的用户昵称（不是联系人）
    private String Name;

    //下面是用来获取用户搜索（添加）的联系人
    @FXML
    private TextField contactName;
    @FXML
    private Button photo;
    private String ContactName;

    //这个标签是用来展示用户的账号（昵称）的
    @FXML
    private Label name;
    @FXML
    private TableView<Contact> contactTable;
    private ObservableList<Contact> ContactData = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Contact, ImageView> photoColumn;
    @FXML
    private TableColumn<Contact, String> contentColumn;

    private TableColumn<Contact, ImageView> alarmColum;//新消息提醒

    private Client client;

    public ContactController(Client client)
    {
        this.client = client;
    }

    public void setContactData(Contact contact)//配置好友列表
    {
        ContactData.add(contact);
    }

    public void clearContactData()//清空上次登录的好友信息
    {
        try
        {
            ContactData.clear();
        }
        catch (NullPointerException ignored)
        {

        }
    }

    public ObservableList<Contact> getContactData()
    {
        return ContactData;
    }

    @FXML
    private void initialize()
    {
        photoColumn = new TableColumn<>("头像");
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));//映射
        photoColumn.setPrefWidth(70);
        photoColumn.setStyle("-fx-background-color: transparent;");

        contentColumn = new TableColumn<>("好友");
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        contentColumn.setPrefWidth(285);
        contentColumn.setStyle("-fx-background-color: transparent; -fx-font-size: 25; -fx-font-family:'MV Boli';");

        alarmColum = new TableColumn<>("消息提醒");
        alarmColum.setCellValueFactory(new PropertyValueFactory<>("alarm"));//映射
        alarmColum.setCellValueFactory(param ->
        {
            param.getValue().status().addListener(
                    (observable, oldValue, newValue) -> Platform.runLater(() -> param.getValue().setAlarm(newValue))
            );
            return new SimpleObjectProperty<>(param.getValue().getAlarm());
        });
        alarmColum.setPrefWidth(70);
        alarmColum.setStyle("-fx-background-color: transparent;-fx-font-size: 20;-fx-font-family:quot;MV:Aliquot;");

        contactTable.getColumns().addAll(photoColumn, contentColumn, alarmColum);
        contactTable.setItems(getContactData());

        // Listen for selection changes and show the person details when changed.
        contactTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                {
                    try
                    {
                        Contact(newValue.getContent());
                    }
                    catch (NullPointerException ignored)
                    {
                    }
                });//这个地方将本人的名字和对面的人的名字传入
    }

    public void Contact(String contactName)
    {
        home.ShowChat(contactName);
    }

    //读取账号
    public void getName()
    {
        Name = home.GetName();
        name.setText(Name);
    }

    public void setApp(Home home)
    {
        this.home = home;
    }

    public void SelfCenter(ActionEvent actionEvent)
    {
        home.ShowSelfCenter();
    }

    public void Search(ActionEvent actionEvent)
    {
        ContactName = contactName.getText();
        client.searchUser(ContactName);//搜索用户
    }

    public TextField getContactName()
    {
        return contactName;
    }

    public void Logout(ActionEvent actionEvent)
    {
        client.logOut();
        client.getContactController().clearContactData();//清空上次登录的好友信息
        home.Login();
        try
        {
            client.getFileReceiver().getFileServer().close();//关闭文件接收端口
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Button getPhoto()
    {
        return photo;
    }
}
