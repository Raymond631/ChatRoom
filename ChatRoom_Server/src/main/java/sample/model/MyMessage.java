package sample.model;

import java.io.Serializable;

public class MyMessage implements Serializable
{
    public static final long serialVersionUID = 19990701L;
    public static final int LOGIN = 0, LOGOUT = 1, REGISTRATION = 2, MESSAGE = 3, UPDATE = 4, ERROR = 5, FriendList = 6, getRecord = 7, getUserInfo = 8, addFriend = 9, searchUser = 10, sendFile = 11, getHistory = 12, voice = 13, getPhoto = 14;

    private int messageType;
    private String sender;
    private String trueSender;//用于群聊转发时，存储实际的发送者
    private String getter;
    private Object message;
    private String time;

    public MyMessage()
    {
    }

    public void setMessageType(int messageType)
    {
        this.messageType = messageType;
    }

    public void setMessage(Object message)
    {
        this.message = message;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public void setGetter(String getter)
    {
        this.getter = getter;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public Object getMessage()
    {
        return message;
    }

    public int getMessageType()
    {
        return messageType;
    }

    public String getSender()
    {
        return sender;
    }

    public String getGetter()
    {
        return getter;
    }

    public String getTime()
    {
        return time;
    }

    public void setTrueSender(String trueSender)
    {
        this.trueSender = trueSender;
    }

    public String getTrueSender()
    {
        return trueSender;
    }

    @Override
    public String toString()
    {
        return "Message{" +
                "messageType=" + messageType +
                ", getter=" + getter +
                ", sender=" + sender +
                ", message=" + message +
                ", time=" + time +
                '}';
    }
}
