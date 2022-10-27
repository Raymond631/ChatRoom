package sample;

import sample.model.MyMessage;
import sample.model.User;
import sample.util.MyDatabase;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerThread extends Thread
{
    private SocketServer server;//方便调用服务器的sendMessage()给其他客户端发消息
    private Socket socket;//记录客户端socket
    public String thisUsername;//记录客户端用户名
    private ObjectInputStream in;
    private ObjectOutputStream out;

    ServerThread(SocketServer server, Socket socket)
    {
        try
        {
            this.server = server;
            this.socket = socket;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            try
            {
                while (true)
                {
                    MyMessage msg = (MyMessage) in.readObject();
                    System.out.println(msg);
                    switch (msg.getMessageType())
                    {
                        case MyMessage.LOGIN:
                            User userLog = (User) (msg.getMessage());
                            String username = userLog.getUsername();
                            String password = userLog.getPassword();

                            MyMessage msgR = new MyMessage();
                            if (MyDatabase.toLogin(username, password))
                            {
                                msgR.setMessageType(MyMessage.LOGIN);
                                thisUsername = username;//记录用户名
                                server.addClient(this);//加入在线名单
                            }
                            else
                            {
                                msgR.setMessageType(MyMessage.ERROR);
                                msgR.setMessage("Login failed!");
                            }
                            out.writeObject(msgR);
                            break;
                        case MyMessage.LOGOUT:
                            server.removeClient(this);
                            //记录登出时间,方便下次登录时加载离线留言
                            Date date = new Date();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String outTime = formatter.format(date);

                            String timeSql = "update user set outTime ='" + outTime + "'where username = '" + thisUsername + "'";
                            MyDatabase.toLogOut(timeSql);
                            break;
                        case MyMessage.REGISTRATION:
                            User userReg = (User) (msg.getMessage());
                            String regusername = userReg.getUsername();
                            String regpassword = userReg.getPassword();

                            MyMessage regResponse = new MyMessage();
                            if (MyDatabase.toRegister(regusername, regpassword))
                            {
                                regResponse.setMessageType(MyMessage.REGISTRATION);
                                thisUsername = regusername;//记录用户名
                                server.addClient(this);//加入在线名单
                            }
                            else
                            {
                                regResponse.setMessageType(MyMessage.ERROR);
                                regResponse.setMessage("Registration failed!");
                            }
                            out.writeObject(regResponse);
                            break;
                        case MyMessage.MESSAGE:
                            String senders = "'" + msg.getSender() + "'";
                            String getters = "'" + msg.getGetter() + "'";
                            String content = "'" + (String) msg.getMessage() + "'";
                            String time = "'" + msg.getTime() + "'";
                            String sql = "insert into record (time,sender,recipient,content) values(" + time + "," + senders + "," + getters + "," + content + ")";

                            if (MyDatabase.toMessage(sql))
                            {//发给对应的客户端
                                server.sendMessage(msg.getGetter(), msg);
                            }
                            else
                            {
                                MyMessage msgResponse = new MyMessage();//错误反馈
                                msgResponse.setMessageType(MyMessage.ERROR);
                                msgResponse.setMessage("send failed!");
                                out.writeObject(msgResponse);//反馈发送者
                            }
                            break;
                        case MyMessage.FriendList:
                            String friendSQL = (String) msg.getMessage();
                            String sender = msg.getSender();

                            ArrayList<String> list = MyDatabase.toFriendList(friendSQL, sender);

                            MyMessage rMsg = new MyMessage();
                            rMsg.setMessageType(MyMessage.FriendList);
                            rMsg.setMessage(list);
                            out.writeObject(rMsg);
                            break;
                        case MyMessage.getRecord:
                            ArrayList<MyMessage> record = MyDatabase.getRecord(msg.getSender(), thisUsername);//获取离线留言
                            if (record.isEmpty())
                            {
                                MyMessage rec = new MyMessage();
                                rec.setMessageType(MyMessage.ERROR);
                                out.writeObject(rec);
                            }
                            else
                            {
                                for (MyMessage e : record)
                                {
                                    out.writeObject(e);
                                }
                            }
                            break;
                        case MyMessage.UPDATE:
                            MyDatabase.updateInfo((String) msg.getMessage());
                            break;
                        case MyMessage.getUserInfo:
                            ArrayList<String> userInfoList = MyDatabase.getUserInfo((String) msg.getMessage());
                            MyMessage userInfoMsg = new MyMessage();
                            userInfoMsg.setMessageType(MyMessage.getUserInfo);
                            userInfoMsg.setMessage(userInfoList);
                            out.writeObject(userInfoMsg);
                            break;
                        case MyMessage.searchUser:
                            ArrayList<String> searchUser = MyDatabase.getUserInfo((String) msg.getMessage());
                            MyMessage searchUserMsg = new MyMessage();
                            if (searchUser == null)
                            {
                                searchUserMsg.setMessageType(MyMessage.ERROR);
                            }
                            else
                            {
                                searchUserMsg.setMessageType(MyMessage.searchUser);
                                searchUserMsg.setMessage(searchUser);
                            }
                            out.writeObject(searchUserMsg);
                            break;
                        case MyMessage.addFriend:
                            MyMessage addFriendMsg = new MyMessage();
                            addFriendMsg.setMessageType(MyMessage.addFriend);
                            if (MyDatabase.addFriend(msg.getSender(), msg.getGetter(), (String) msg.getMessage()))//假借sender来传输插入检查语句
                            {
                                addFriendMsg.setMessage("添加成功");
                            }
                            else
                            {
                                addFriendMsg.setMessage("请勿重复添加！");
                            }
                            out.writeObject(addFriendMsg);
                            break;
                        case MyMessage.sendFile:
                            String getter = msg.getGetter();
                            String getterIP = server.getIP(getter);

                            MyMessage ipMsg = new MyMessage();
                            ipMsg.setMessageType(MyMessage.sendFile);
                            if (getterIP != null)
                            {
                                ipMsg.setMessage(getterIP);
                            }
                            else
                            {
                                ipMsg.setMessage("-1");
                            }
                            out.writeObject(ipMsg);
                            break;
                        case MyMessage.getHistory:
                            String s = (String) msg.getMessage();
                            String history = MyDatabase.getHistory(s);

                            MyMessage historyMsg = new MyMessage();
                            historyMsg.setMessageType(MyMessage.getHistory);
                            historyMsg.setMessage(history);
                            out.writeObject(historyMsg);
                            break;
                        case MyMessage.getPhoto:
                            String c = msg.getSender();
                            String u = msg.getGetter();
                            String path = "src/main/resources/avatar/" + u + ".jpg";
                            File avatar = new File(path);
                            if (avatar.exists())
                            {
                                sendFile(server.getIP(c), avatar);
                            }
                            break;
                        case MyMessage.voice:
                            String geter = msg.getGetter();
                            String geterIP = server.getIP(geter);

                            MyMessage IPMsg = new MyMessage();
                            IPMsg.setMessageType(MyMessage.voice);
                            if (geterIP != null)
                            {
                                IPMsg.setMessage(geterIP);
                            }
                            else
                            {
                                IPMsg.setMessage("-1");
                            }
                            out.writeObject(IPMsg);
                            break;

                    }
                }
            }
            catch (SocketException e)//若in.readObject()抛出异常,则客户端已下线
            {
                server.removeClient(this);
                //记录登出时间,方便下次登录时加载离线留言
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String outTime = formatter.format(date);

                String timeSql = "update user set outTime ='" + outTime + "'where username = '" + thisUsername + "'";
                MyDatabase.toLogOut(timeSql);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(MyMessage message)
    {
        try
        {
            out.writeObject(message);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void sendFile(String getterIP, File avatar)//用于发送头像
    {
        Socket fileSocket = null;
        DataOutputStream fileOut = null;
        DataInputStream fileIn = null;
        try
        {
            //和客户端文件端口 建立连接
            fileSocket = new Socket(getterIP, 8089);
            fileOut = new DataOutputStream(fileSocket.getOutputStream());
            fileIn = new DataInputStream(Files.newInputStream(avatar.toPath()));

            //传头像标志
            fileOut.writeUTF("avatar");
            fileOut.flush();
            //文件名和长度
            fileOut.writeUTF(avatar.getName());
            fileOut.flush();
            fileOut.writeLong(avatar.length());
            fileOut.flush();
            // 开始传输文件
            int length;
            byte[] buff = new byte[1024];
            while ((length = fileIn.read(buff)) > 0)// 读取到的文件长度>0
            {
                fileOut.write(buff, 0, length);
                fileOut.flush();
            }
            System.out.println("头像发送完成");
            fileSocket.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (NullPointerException ignored)
        {
        }
    }
}