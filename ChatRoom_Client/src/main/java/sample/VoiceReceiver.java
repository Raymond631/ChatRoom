package sample;


import java.io.IOException;
import java.net.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


public class VoiceReceiver implements Runnable {
    private static VoiceReceiver instance = null;
    private String localHostAddress = getLocalHostAddress();
    private DatagramSocket socket;
    private DatagramPacket packet;
    private boolean flag = false;
    final int bufSize = 16384;
    SourceDataLine sourceDataLine;

    public static VoiceReceiver getInstance() {
        if (instance == null) {
            instance = new VoiceReceiver();
        }
        return instance;
    }
    @Override
    public void run() {
        flag = true;
        try {
            socket = new DatagramSocket(8765);
        } catch (BindException e) {
            e.printStackTrace();
            flag = true;
            System.out.println("socket被占用，说明语音接收线程正阻塞在最后一次循环正准备退出，\n"
                    + "此时，本线程只需要设置flag的值为true，让该线程继续运行，而本线程直接退出即可");
            return;
        } catch (SocketException e) {
            e.printStackTrace();
            flag = false;
            return;
        }
        byte data[] = new byte[512];
        packet = new DatagramPacket(data, data.length);
        // 8000 好像是语音清晰度标准
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 1, 2, 8000, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.open(format, bufSize);
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
            flag = false;
            return;
        }
        sourceDataLine.start();
        while (flag) {
            try {
                socket.receive(packet);
                InetAddress sendIP = packet.getAddress();
                String hostAddress = sendIP.getHostAddress();
                if((!hostAddress.equals(localHostAddress))){
                    sourceDataLine.write(packet.getData(), 0, packet.getLength());
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        if (flag) {
            sourceDataLine.drain();
        }
        sourceDataLine.stop();
        sourceDataLine.close();
        sourceDataLine = null;
        socket.close();
        socket = null;
        flag = false;
        System.out.println("退出receiver run");
    }

    /**
     * 获取IP地址
     */
    public static String getLocalHostAddress(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }
    public boolean isFlag() {
        return flag;
    }
}

