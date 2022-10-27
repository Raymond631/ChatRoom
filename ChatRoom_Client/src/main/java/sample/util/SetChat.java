package sample.util;

import javafx.scene.image.ImageView;

import java.util.TimerTask;

public class SetChat extends TimerTask//定时任务类,用于刷新聊天消息
{
    private Client client;

    public SetChat(Client client)
    {
        this.client = client;
    }

    @Override
    public void run()
    {
        if (client.getChatController() != null)
        {
            String contactName = client.getChatController().getContactName();//获取当前联系人
            if (client.getMessageReceiver().getMessageSet().get(contactName) != null)//如果消息集不为空
            {
                for (String msg : client.getMessageReceiver().getMessageSet().get(contactName))//加载聊天记录
                {
                    client.getChatController().setMessage(contactName, msg);//显示消息
                }
                client.getMessageReceiver().getMessageSet().remove(contactName);//加载后清除

                ImageView newMsg = new ImageView();
                newMsg.setFitWidth(70);
                newMsg.setFitHeight(70);
                client.setRemind(contactName, newMsg);
            }
        }
    }
}
