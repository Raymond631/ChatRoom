package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.controller.*;
import sample.model.Emoji;
import sample.util.Client;
import sample.util.SetChat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Timer;

public class Home extends Application
{
    private Stage primaryStage;
    private AnchorPane anchorPane;
    private String name;//这里用来记录账号

    private Client client;

    private static ObservableList<Emoji> EmojiData = FXCollections.observableArrayList();

    public Home()
    {
        AddImage();
    }

    public static void main(String[] args)
    {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        this.primaryStage = stage;
        Login();
    }

    //跳转到登录界面
    public void Login()
    {
        try
        {
            //client = new Client("localhost", 8088);//连接本地服务器
            client = new Client("192.168.195.87", 8088);//连接局域网服务器
            client.setHome(this);
            LoginController lc = new LoginController(client);
            FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("Login.fxml"));
            fxmlLoader.setController(lc);

            anchorPane = (AnchorPane) fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            primaryStage.setTitle("LoginPage");
            primaryStage.setScene(scene);
            primaryStage.show();

            LoginController loginController = fxmlLoader.getController();
            loginController.setApp(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //跳转到注册界面
    public void ShowEnroll()
    {
        try
        {
            EnrollController ec = new EnrollController(this.client);
            FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("Enroll.fxml"));
            fxmlLoader.setController(ec);

            anchorPane = (AnchorPane) fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            primaryStage.setTitle("EnrollPage");
            primaryStage.setScene(scene);
            primaryStage.show();

            EnrollController enrollController = fxmlLoader.getController();
            enrollController.setApp(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //跳转到联系人界面
    public void ShowContact()
    {
        try
        {
            client.startReceiver();//启动接收线程

            ContactController ConC = new ContactController(this.client);
            client.setContactController(ConC);//方便client调用ContactController修改ui
            FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("Contact.fxml"));
            fxmlLoader.setController(ConC);

            anchorPane = (AnchorPane) fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            primaryStage.setTitle("ContactPage");
            primaryStage.setScene(scene);
            primaryStage.show();

            ConC.setApp(this);
            ConC.getName();

            //配置个人头像
            String path = "'sample/avatar/" + GetName() + ".jpg'";
            ConC.getPhoto().setStyle("-fx-background-image:url(" + path + "); -fx-background-size: 80px;");
            //加载好友列表
            client.getFriendList(name);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void ShowChat(String contactName)
    {
        try
        {
            ChatController cc = new ChatController(this.client);
            FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("Chat.fxml"));
            fxmlLoader.setController(cc);

            anchorPane = (AnchorPane) fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            Stage chat = new Stage();

            chat.setTitle("ChatPage");
            chat.setScene(scene);
            chat.show();

            cc.setApp(this);
            cc.setContactName(contactName);//传入聊天对象username

            //传入chatController
            client.setChatController(cc);
            //定时更新消息
            Timer timer = new Timer();
            SetChat myTask = new SetChat(client);
            timer.schedule(myTask, 100L, 500L);

            chat.setOnCloseRequest(event ->
                    client.setChatController(null)
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //跳转到个人中心
    public void ShowSelfCenter()
    {
        try
        {
            SelfCenterController sc = new SelfCenterController(this.client);
            FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("SelfCenter.fxml"));
            fxmlLoader.setController(sc);

            anchorPane = (AnchorPane) fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            Stage self = new Stage();

            self.setTitle("SelfCenterPage");
            self.setScene(scene);
            self.show();

            sc.setApp(this);

            client.getUserInfo(name);//加载个人信息
            //配置个人头像
            String path = "'sample/avatar/" + GetName() + ".jpg'";
            sc.getPhoto().setStyle("-fx-background-image:url(" + path + "); -fx-background-size: 100px;");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void ShowAddContactController(String contactName)
    {
        try
        {
            AddContactController ac = new AddContactController(this.client);
            FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("AddContact.fxml"));
            fxmlLoader.setController(ac);

            anchorPane = (AnchorPane) fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            Stage add = new Stage();

            add.setTitle("AddContactControllerPage");
            add.setScene(scene);
            add.show();

            ac.setApp(this);

            ac.getContactName(contactName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //表情包界面
    public void ShowEmoji()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("Emoji.fxml"));
            anchorPane = (AnchorPane) fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            Stage emoji = new Stage();

            emoji.setScene(scene);
            emoji.show();

            EmojiController emojiController = fxmlLoader.getController();
            emojiController.setApp(this);
            emojiController.setClient(client);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //保存账号（密码暂时没保存）
    public void KeepName(String name)
    {
        this.name = name;
    }

    //供给给其他页面获取账号
    public String GetName()
    {
        return name;
    }

    public ObservableList<Emoji> getEmojiData()
    {
        return EmojiData;
    }


    private void AddImage()
    {
        byte[] emojiBytes1 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x81};
        String emojiAsString1 = new String(emojiBytes1, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString1));
        byte[] emojiBytes2 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x82};
        String emojiAsString2 = new String(emojiBytes2, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString2));
        byte[] emojiBytes3 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x83};
        String emojiAsString3 = new String(emojiBytes3, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString3));
        byte[] emojiBytes4 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x84};
        String emojiAsString4 = new String(emojiBytes4, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString4));
        byte[] emojiBytes5 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x85};
        String emojiAsString5 = new String(emojiBytes5, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString5));
        byte[] emojiBytes6 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x86};
        String emojiAsString6 = new String(emojiBytes6, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString6));
        byte[] emojiBytes7 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x87};
        String emojiAsString7 = new String(emojiBytes7, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString7));
        byte[] emojiBytes8 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x88};
        String emojiAsString8 = new String(emojiBytes8, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString8));
        byte[] emojiBytes9 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x89};
        String emojiAsString9 = new String(emojiBytes9, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString9));
        byte[] emojiBytes10 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x8A};
        String emojiAsString10 = new String(emojiBytes10, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString10));
        byte[] emojiBytes11 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x8B};
        String emojiAsString11 = new String(emojiBytes11, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString11));
        byte[] emojiBytes12 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x8C};
        String emojiAsString12 = new String(emojiBytes12, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString12));
        byte[] emojiBytes13 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x8D};
        String emojiAsString13 = new String(emojiBytes13, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString13));
        byte[] emojiBytes14 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x8E};
        String emojiAsString14 = new String(emojiBytes14, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString14));
        byte[] emojiBytes15 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x8F};
        String emojiAsString15 = new String(emojiBytes15, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString15));
        byte[] emojiBytes16 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x91};
        String emojiAsString16 = new String(emojiBytes16, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString16));
        byte[] emojiBytes17 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x9B};
        String emojiAsString17 = new String(emojiBytes17, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString17));
        byte[] emojiBytes18 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x92};
        String emojiAsString18 = new String(emojiBytes18, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString18));
        byte[] emojiBytes19 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x93};
        String emojiAsString19 = new String(emojiBytes19, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString19));
        byte[] emojiBytes20 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x94};
        String emojiAsString20 = new String(emojiBytes20, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString20));
        byte[] emojiBytes21 = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x95};
        String emojiAsString21 = new String(emojiBytes21, Charset.forName("UTF-8"));
        EmojiData.add(new Emoji(emojiAsString21));
    }

}