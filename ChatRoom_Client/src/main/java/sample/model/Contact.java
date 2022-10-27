package sample.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;

public class Contact    //联系人
{
    private ImageView photo;

    private String content;

    private ImageView alarm;//新消息提醒

    public Contact(ImageView photo, String content)
    {
        this.photo = photo;
        this.content = content;
        alarm = new ImageView();
    }

    public ImageView getPhoto()
    {
        return photo;
    }

    public String getContent()
    {
        return content;
    }

    public void setAlarm(ImageView alarm)
    {
        this.alarm = alarm;
    }

    public ImageView getAlarm()
    {
        return alarm;
    }

    public ObservableValue<ImageView> status()
    {
        return new SimpleObjectProperty<>(alarm);
    }
}
