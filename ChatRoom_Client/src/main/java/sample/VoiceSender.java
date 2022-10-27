package sample;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
public class VoiceSender implements Runnable {
    private DatagramSocket socket;

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    private DatagramPacket packet;
    //语音是否进行的标记，false表示断开
    public static boolean flag = false;
    TargetDataLine targetDataLine;
    private String hostAddress;
    private static VoiceSender instance = null;

    private VoiceSender() {
    }

    public static synchronized VoiceSender getInstance() {
        if (instance == null) {
            instance = new VoiceSender();
        }
        return instance;
    }

    @Override
    public void run() {
        flag = true;
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 1, 2, 8000, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            if (!targetDataLine.isOpen()) {
                targetDataLine.open(format, targetDataLine.getBufferSize());
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            flag = true;
            System.out.println("说明可能是已经在运行中而被占用，故尝试设置flag为true然后退出");
            return;
        }

        int length = 512;
        byte[] data = new byte[length];
        int readLen = 0;
        targetDataLine.start();
        while (flag) {
            readLen = targetDataLine.read(data, 0, data.length);
            try {
                //发送出去
                send(data, readLen);
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
        targetDataLine.stop();
        targetDataLine.close();
        targetDataLine = null;
        socket.close();
        socket = null;
        flag = false;
        System.out.println("退出sender run");
    }

    public void send(byte[] data, int length) throws Exception {
        if (length > 512) {
            //数据长度限制在512字节内
            throw new Exception();
        }
        packet = new DatagramPacket(data, length, InetAddress.getByName(this.hostAddress), 8765);
        socket.send(packet);
    }

    public boolean isFlag() {
        return flag;
    }

    public void setHostAddress(String hostAddr) {
        this.hostAddress = hostAddr;
    }

    /**
     * 关闭语音
     */
    public void shutDown() {
        this.flag = false;
    }

}
