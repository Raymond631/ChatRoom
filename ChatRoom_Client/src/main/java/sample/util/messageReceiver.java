package sample.util;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.Voice;
import sample.model.Contact;
import sample.model.MyMessage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class messageReceiver implements Runnable
{
    private Client client;
    private ObjectInputStream in;
    private MyMessage msg;

    private Map<String, ArrayList<String>> messageSet;


    public messageReceiver(Client client, ObjectInputStream in)
    {
        this.client = client;
        this.in = in;
        messageSet = new HashMap<>();
    }

    public Map<String, ArrayList<String>> getMessageSet()
    {
        return messageSet;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                msg = (MyMessage) in.readObject();
                System.out.println(msg);
                if (msg.getMessageType() == MyMessage.MESSAGE || msg.getMessageType() == MyMessage.getRecord)
                {
                    String sender = msg.getSender();
                    String message = (String) msg.getMessage();
                    if (sender.equals("聊天室"))
                    {
                        message = msg.getTrueSender() + ":" + message;
                    }

                    if (messageSet.containsKey(sender))
                    {
                        messageSet.get(sender).add(message);
                    }
                    else
                    {
                        ArrayList list = new ArrayList();
                        list.add(message);
                        messageSet.put(sender, list);
                    }
                    //设置消息提醒
                    String path = "src/main/resources/sample/image/newMessage.gif";
                    File photo = new File(path);
                    ImageView newMsg = new ImageView(new Image(photo.toURI().toURL().toString()));
                    newMsg.setFitWidth(70);
                    newMsg.setFitHeight(70);
                    client.setRemind(sender, newMsg);
                }
                else if (msg.getMessageType() == MyMessage.sendFile)
                {
                    String getterIP = (String) msg.getMessage();
                    if (!getterIP.equals("-1"))
                    {
                        Platform.runLater(() ->//为了在此线程中 修改ui界面
                        {
                            client.sendFile(getterIP);
                        });
                    }
                    else
                    {
                        System.out.println("对方不在线,无法发送图片和文件");
                    }
                }
                else if(msg.getMessageType()==MyMessage.voice)
                {
                    String getterIP = (String) msg.getMessage();
                    if (!getterIP.equals("-1"))
                    {
                        Platform.runLater(() ->//为了在此线程中 修改ui界面
                        {
                            Voice voice = new Voice(getterIP);
                            try {
                                voice.startVoice();
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    else
                    {
                        System.out.println("对方不在线,无法语音通话");
                    }
                }
                else if (msg.getMessageType() == MyMessage.FriendList)
                {
                    client.getContactController().clearContactData();//清空原有列表
                    ArrayList<String> list = (ArrayList<String>) msg.getMessage();
                    list.add("聊天室");
                    for (String user : list)
                    {
                        //加载头像
                        String path = "src/main/resources/sample/avatar/" + user + ".jpg";
                        File avatar = new File(path);

                        //从服务器更新头像
                        MyMessage avatarMsg = new MyMessage();
                        avatarMsg.setMessageType(MyMessage.getPhoto);
                        avatarMsg.setSender(client.getThisUser());
                        avatarMsg.setGetter(user);
                        client.sendMessage(avatarMsg);

                        if (!avatar.exists())
                        {
                            path = "src/main/resources/sample/avatar/默认头像.jpg";
                            avatar = new File(path);
                        }
                        ImageView photo = new ImageView(new Image(avatar.toURI().toURL().toString()));
                        photo.setFitWidth(70);
                        photo.setFitHeight(70);

                        Contact contact = new Contact(photo, user);
                        client.getContactController().setContactData(contact);

                        client.getRecord(user);//获取离线留言
                    }
                }
                else if (msg.getMessageType() == MyMessage.getUserInfo)
                {
                    ArrayList<String> list = (ArrayList<String>) msg.getMessage();
                    Platform.runLater(() ->//为了在此线程中 修改ui界面
                    {
                        client.getSelfCenterController().setUserInfo(list);
                    });
                }
                else if (msg.getMessageType() == MyMessage.searchUser)
                {
                    ArrayList<String> list = (ArrayList<String>) msg.getMessage();
                    Platform.runLater(() ->//为了在此线程中 修改ui界面
                    {
                        client.getHome().ShowAddContactController(client.getContactController().getContactName().getText());
                        client.getAddContactController().setUserInfo(list);
                    });
                }
                else if (msg.getMessageType() == MyMessage.addFriend)
                {
                    Platform.runLater(() ->//为了在此线程中 修改ui界面
                    {
                        client.getAddContactController().setWhetherFriend((String) msg.getMessage());
                        client.getFriendList(client.getThisUser());
                    });
                }
                else if (msg.getMessageType() == MyMessage.getHistory)
                {
                    String history = (String) msg.getMessage();
                    Platform.runLater(() ->//为了在此线程中 修改ui界面
                    {
                        client.getChatController().setMessage(history);
                    });
                }
            }
        }
        catch (ClassNotFoundException | IOException cle)
        {
            cle.printStackTrace();
        }
    }
}