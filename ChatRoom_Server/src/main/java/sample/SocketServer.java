package sample;

import sample.model.MyMessage;
import sample.util.MyDatabase;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class SocketServer
{
    private int port;//监听端口号
    private HashMap<String, ServerThread> activeClient;//已连接的客户端

    public SocketServer(int port)
    {
        this.port = port;
        activeClient = new HashMap<>();
    }

    public static void main(String[] args)
    {
        SocketServer server1 = new SocketServer(8088);//监听普通消息
        server1.start();
    }

    public void start()
    {
        MyDatabase.initDB();//连接数据库
        try
        {
            ServerSocket serverSocket = new ServerSocket(this.port);// 创建服务端socket
            System.out.println("消息服务器开始监听,端口号：" + this.port);

            FileServer server2 = new FileServer(8090);//监听文件消息
            server2.start();

            while (true)//循环监听等待客户端的连接
            {
                Socket socket = serverSocket.accept();
                System.out.println("新用户接入" + socket);
                ServerThread thread = new ServerThread(this, socket);//加入this是为了方便线程调用sendMessage()给其他客户端发消息
                thread.start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addClient(ServerThread client)
    {
        activeClient.put(client.thisUsername, client);
        System.out.println("客户端: " + client.thisUsername + " 上线");
    }

    public void removeClient(ServerThread client)
    {
        activeClient.remove(client.thisUsername);
        System.out.println("Client: " + client.thisUsername + " 已下线");
    }

    public void sendMessage(String getter, MyMessage message)
    {
        if (getter.equals("聊天室"))
        {
            message.setTrueSender(message.getSender());
            message.setSender("聊天室");
            for (ServerThread client : activeClient.values())
            {
                client.sendMessage(message);
            }
        }
        else
        {
            ServerThread client = activeClient.get(getter);
            if (client != null)
            {
                client.sendMessage(message);
            }
        }
    }

    public String getIP(String getter)
    {
        ServerThread getterClient = activeClient.get(getter);
        String getterIP;
        if (getterClient != null)
        {
            getterIP = String.valueOf(getterClient.getSocket().getInetAddress()).substring(1);//去掉第一个字符"/"
        }
        else
        {
            getterIP = null;
        }
        return getterIP;
    }
}