package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class FileServer extends Thread
{
    private ServerSocket server;
    private Socket socket;
    private int port;//监听端口号

    public FileServer(int port)
    {
        this.port = port;
    }


    @Override
    public void run()
    {
        try
        {
            server = new ServerSocket(this.port);
            System.out.println("文件服务器开始监听，端口号：" + this.port);
            while (true)//循环监听等待客户端的连接
            {
                Socket socket = server.accept();

                DataInputStream fileIn = new DataInputStream(socket.getInputStream());

                File file = new File("src/main/resources/avatar/" + fileIn.readUTF());//获取服务器传过来的文件名字
                System.out.println("path:" + file.toPath());
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
                System.out.println("文件接收完成");
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
