package sample;

import sample.model.MyMessage;
import sample.util.Client;
import sun.net.httpserver.HttpServerImpl;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * 语音通话启动类,绑定按钮调用该类
 */
public class Voice {
    String clientIP;
    public Voice(String clientIP)
    {
        this.clientIP=clientIP;
    }
    public void startVoice() throws SocketException {
        System.out.println("开始启动语音。。。");
        this.startVoiceSender();
    }

    public void startVoiceSender() throws SocketException {
        VoiceSender voiceSender = VoiceSender.getInstance();
        DatagramSocket socket = new DatagramSocket();
//        // 255.255.255.255 采用广播形式，同局域网都能听到，实现群聊
//        voiceSender.setHostAddress("255.255.255.255");
        // 采用一对一形式，输入对方局域网内的ip地址,这里需要获取对方的ip地址。
        voiceSender.setHostAddress(clientIP);

        voiceSender.setSocket(socket);
        System.out.println("开始启动语音发送线程。。。");
        new Thread(voiceSender).start();
        this.startVoiceReceiver();
    }

    /**
     * 开启语音接收
     */
    public void startVoiceReceiver() {
        VoiceReceiver voiceReceiver = VoiceReceiver.getInstance();
        if (!voiceReceiver.isFlag()) {
            System.out.println("开始启动语音接收线程。。。");
            new Thread(voiceReceiver).start();
        }
    }


}