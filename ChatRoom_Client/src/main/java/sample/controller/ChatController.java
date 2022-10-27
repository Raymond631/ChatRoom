package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import sample.Home;
import sample.VoiceSender;
import sample.model.MyMessage;
import sample.util.Client;

import sample.VoiceSender;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatController
{
    private Home home;
    private String Name;//我的username
    @FXML
    private Label name;//当前联系人的username标签
    @FXML
    private TextArea msgDisplay;
    @FXML
    private TextArea msgSend;

    private Client client;

    public ChatController(Client client)
    {
        this.client = client;
        this.client.setChatController(this);
    }

    //读取账号
    public String getName()
    {
        Name = home.GetName();
        return Name;
    }

    public void setApp(Home home)
    {
        this.home = home;
    }

    public void setContactName(String contactName)//设置聊天对象username
    {
        name.setText(contactName);
    }

    public String getContactName()
    {
        return name.getText();
    }

    //发送信息
    @FXML
    public void Send(ActionEvent actionEvent)
    {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(date);

        String senders = getName();
        String getters = name.getText();
        String content = msgSend.getText();

        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.MESSAGE);
        msg.setMessage(content);
        msg.setSender(senders);
        msg.setGetter(getters);
        msg.setTime(time);

        client.sendMessage(msg);
        msgSend.clear();//发送之后清空输入框

        if (!getters.equals("聊天室"))//非群聊直接显示自己发的消息
        {
            setMessage(senders, content);
        }
    }

    @FXML
    void sendFile(ActionEvent event)
    {
        if (getContactName().equals("聊天室"))
        {
            System.out.println("群聊不支持发文件");
            return;
        }
        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.sendFile);//获取对方IP地址
        msg.setGetter(name.getText());
        client.sendMessage(msg);
    }

    @FXML
    void getHistory(ActionEvent event)
    {
        String s = "select * from record where sender ='" + getContactName() + "' and recipient ='" + getName() + "' or sender ='" + getName() + "' and recipient ='" + getContactName() + "'";
        if (getContactName().equals("聊天室"))
        {
            s = "select * from record where recipient ='聊天室'";
        }
        client.getHistory(s);
    }

    public void setMessage(String sender, String message)
    {
        if (sender.equals("聊天室"))
        {
            msgDisplay.appendText(message + "\n");
        }
        else
        {
            msgDisplay.appendText(sender + ":" + message + "\n");
        }
    }

    public void setMessage(String history)//重载
    {
        msgDisplay.setText(history);
    }
    @FXML
    void sendvoice(ActionEvent event)
    {
        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.voice);//获取对方IP地址
        msg.setGetter(name.getText());
        client.sendMessage(msg);
    }

    @FXML
    void shutdown(ActionEvent event){
        VoiceSender.flag = false;
    }
    @FXML
    public void OpenEmoji(ActionEvent actionEvent)
    {
        home.ShowEmoji();
    }

    public void getEmoji(String emoji) {
        msgSend.appendText(emoji);
    }
}
