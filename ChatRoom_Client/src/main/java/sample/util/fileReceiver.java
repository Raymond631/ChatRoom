package sample.util;

import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class fileReceiver implements Runnable
{
    private Client client;
    private ServerSocket fileServer;
    private int port;

    public fileReceiver(Client client, int port)
    {
        this.client = client;
        this.port = port;
    }

    @Override
    public void run()
    {
        try
        {
            fileServer = new ServerSocket(this.port);// 开启文件接收监听端口

            while (true)//循环监听等待文件传输连接
            {
                Socket socket = fileServer.accept();
                System.out.println("文件传入：。。。。。。");
                
                Platform.runLater(() ->//为了在此线程中 修改ui界面
                {
                    try
                    {
                        client.receiveFile(socket);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ServerSocket getFileServer()
    {
        return fileServer;
    }
}
