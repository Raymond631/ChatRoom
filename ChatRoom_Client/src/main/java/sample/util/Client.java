package sample.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Home;
import sample.controller.AddContactController;
import sample.controller.ChatController;
import sample.controller.ContactController;
import sample.controller.SelfCenterController;
import sample.model.Contact;
import sample.model.MyMessage;
import sample.model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client
{
    private Home home;
    private String thisUser;
    private String address;//IP地址
    private int port;//端口号
    private Socket connection;//套接字
    private ObjectOutputStream out;//输出流
    private ObjectInputStream in;//输入流

    //接收线程
    private messageReceiver messageReceiver;
    private fileReceiver fileReceiver;
    private ChatController chatController;//将消息显示到聊天界面
    private SelfCenterController selfCenterController;//配置个人中心
    private AddContactController addContactController;//加载搜索信息
    private ContactController contactController;

    public Client(String address, int port)
    {
        this.address = address;
        this.port = port;
        connectToServer();
    }

    private void connectToServer()//连接服务器
    {
        try
        {
            connection = new Socket(address, port);
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        }
        catch (IOException iOe)
        {
            iOe.printStackTrace();
        }
    }

    public boolean logIn(String username, String password)
    {
        //打包登录信息
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        MyMessage msgLog = new MyMessage();
        msgLog.setMessageType(MyMessage.LOGIN);
        msgLog.setMessage(user);
        try
        {
            out.writeObject(msgLog);
            //接收反馈
            MyMessage msgIn = (MyMessage) in.readObject();
            if (msgIn.getMessageType() == MyMessage.LOGIN)
            {
                this.thisUser = username;
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void logOut()
    {
        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.LOGOUT);
        try
        {
            out.writeObject(msg);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean register(String username, String password)
    {
        //打包注册信息
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        MyMessage msgReg = new MyMessage();
        msgReg.setMessageType(MyMessage.REGISTRATION);
        msgReg.setMessage(user);
        try
        {
            out.writeObject(msgReg);
            //接收反馈
            MyMessage msgR = (MyMessage) in.readObject();
            if (msgR.getMessageType() == MyMessage.REGISTRATION)
            {
                this.thisUser = username;
                return true;
            }
        }
        catch (ClassNotFoundException | IOException cle)
        {
            cle.printStackTrace();
        }
        return false;
    }

    //获取好友列表
    public void getFriendList(String name)
    {
        String sql = "select * from friend where personA ='" + name + "' or personB ='" + name + "'";//双向好友关系
        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.FriendList);
        msg.setSender(name);
        msg.setMessage(sql);
        try
        {
            out.writeObject(msg);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void getUserInfo(String name)
    {
        String sql = "select * from user where username ='" + name + "'";
        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.getUserInfo);
        msg.setMessage(sql);
        try
        {
            out.writeObject(msg);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void searchUser(String contactName)
    {
        String sql = "select * from user where username ='" + contactName + "'";
        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.searchUser);
        msg.setMessage(sql);
        try
        {
            out.writeObject(msg);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setHome(Home home)
    {
        this.home = home;
    }

    public Home getHome()
    {
        return home;
    }

    public String getThisUser()
    {
        return thisUser;
    }

    public sample.util.messageReceiver getMessageReceiver()
    {
        return messageReceiver;
    }

    public sample.util.fileReceiver getFileReceiver()
    {
        return fileReceiver;
    }

    public Socket getConnection()
    {
        return connection;
    }

    //为了将消息显示到聊天界面
    public void setChatController(ChatController chatController)
    {
        this.chatController = chatController;
    }

    public void setSelfCenterController(SelfCenterController selfCenterController)
    {
        this.selfCenterController = selfCenterController;
    }

    public void setAddContactController(AddContactController addContactController)
    {
        this.addContactController = addContactController;
    }

    public ChatController getChatController()
    {
        return chatController;
    }

    public SelfCenterController getSelfCenterController()
    {
        return selfCenterController;
    }

    public AddContactController getAddContactController()
    {
        return addContactController;
    }

    public void setContactController(ContactController contactController)
    {
        this.contactController = contactController;
    }

    public ContactController getContactController()
    {
        return contactController;
    }

    public void startReceiver()//启动接收线程
    {
        messageReceiver = new messageReceiver(this, in);
        Thread receiver1 = new Thread(messageReceiver);
        receiver1.start();

        //要同时运行2个客户端，其中1个客户端必须注释掉下面的代码
        fileReceiver = new fileReceiver(this, 8089);
        Thread receiver2 = new Thread(fileReceiver);
        receiver2.start();
    }

    public void sendMessage(MyMessage message)//发送消息
    {
        try
        {
            out.writeObject(message);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void getRecord(String contactName)
    {
        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.getRecord);
        msg.setSender(contactName);//当前好友的昵称
        try
        {
            out.writeObject(msg);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void updateInfo(String sql)
    {
        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.UPDATE);
        msg.setMessage(sql);
        try
        {
            out.writeObject(msg);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void uploadAvatar(File photo)//上传头像
    {
        Socket fileSocket = null;
        DataOutputStream fileOut = null;
        DataInputStream fileIn = null;
        try
        {
            //和服务器文件端口 建立连接
            fileSocket = new Socket(address, 8090);
            fileOut = new DataOutputStream(fileSocket.getOutputStream());
            fileIn = new DataInputStream(Files.newInputStream(photo.toPath()));

            // 文件名和长度
            fileOut.writeUTF(getThisUser() + ".jpg");//重命名为 用户名+后缀
            fileOut.flush();
            fileOut.writeLong(photo.length());
            fileOut.flush();
            // 开始传输文件
            int length;
            byte[] buff = new byte[1024];
            while ((length = fileIn.read(buff)) > 0)// 读取到的文件长度>0
            {
                fileOut.write(buff, 0, length);
                fileOut.flush();
            }
            System.out.println("文件发送完成");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (NullPointerException ignored)
        {
        }
    }

    public void addFriend(String personA, String personB)
    {
        if (!personA.equals(personB))
        {
            String check1 = "select * from friend where personA ='" + personA + "' and personB ='" + personB + "'";
            String check2 = "select * from friend where personA ='" + personB + "' and personB ='" + personA + "'";
            String sql = "insert into friend (personA,personB) values ('" + personA + "','" + personB + "')";
            MyMessage msg = new MyMessage();
            msg.setMessageType(MyMessage.addFriend);
            msg.setMessage(sql);
            msg.setSender(check1);//假借sender来传输插入检查语句
            msg.setGetter(check2);//假借getter来传输插入检查语句
            try
            {
                out.writeObject(msg);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            getAddContactController().setWhetherFriend("不能添加自己为好友！");
        }
    }

    public void sendFile(String getterIP)
    {
        Socket fileSocket = null;
        DataOutputStream fileOut = null;
        DataInputStream fileIn = null;
        try
        {
            //和好友端建立连接
            fileSocket = new Socket(getterIP, 8089);
            fileOut = new DataOutputStream(fileSocket.getOutputStream());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择文件");
            Stage fileStage = new Stage();
            File file = fileChooser.showOpenDialog(fileStage);
            fileIn = new DataInputStream(Files.newInputStream(file.toPath()));

            // 文件名和长度
            fileOut.writeUTF(file.getName());
            fileOut.flush();
            fileOut.writeLong(file.length());
            fileOut.flush();
            // 开始传输文件
            int length;
            byte[] buff = new byte[1024];
            while ((length = fileIn.read(buff)) > 0)// 读取到的文件长度>0
            {
                fileOut.write(buff, 0, length);
                fileOut.flush();
            }
            System.out.println("文件发送完成");
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

    public void receiveFile(Socket socket) throws IOException
    {
        DataInputStream fileIn = new DataInputStream(socket.getInputStream());
        String fileName = fileIn.readUTF();
        String path;
        if (fileName.equals("avatar"))//传头像标志
        {
            fileName = fileIn.readUTF();
            path = "src/main/resources/sample/avatar";
        }
        else
        {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("选择保存路径");
            Stage fileStage = new Stage();
            path = directoryChooser.showDialog(fileStage).getPath();//选择的文件夹路径
        }
        File file = new File(path + "/" + fileName);//获取服务器传过来的文件名字
        DataOutputStream fileOut = new DataOutputStream(Files.newOutputStream(file.toPath()));

        //开始接收文件
        long allLength = fileIn.readLong();//文件总长度
        int tempLength;
        byte[] buff = new byte[1024];
        while ((tempLength = fileIn.read(buff)) > 0)
        {
            fileOut.write(buff, 0, tempLength);
            if (file.length() == allLength)
                break;
        }
        fileOut.flush();
        System.out.println("接收完毕");
    }

    public void getHistory(String sql)
    {
        MyMessage msg = new MyMessage();
        msg.setMessageType(MyMessage.getHistory);
        msg.setMessage(sql);
        try
        {
            out.writeObject(msg);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setRemind(String sender, ImageView newMsg)//设置消息提醒
    {
        ObservableList<Contact> contactData = getContactController().getContactData();//旧表
        ObservableList<Contact> newData = FXCollections.observableArrayList();//中间转换表
        for (Contact contact : contactData)//拷贝
        {
            if (contact.getContent().equals(sender))//修改
            {
                Contact newCon = new Contact(contact.getPhoto(), contact.getContent());
                newCon.setAlarm(newMsg);
                contact = newCon;
            }
            newData.add(contact);
        }
        contactData.clear();//清空
        contactData.addAll(newData);//赋值
    }
}