package sample.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Emoji {
    private final StringProperty one;

    public Emoji(String one)
    {
        this.one = new SimpleStringProperty(one);
    }


    public ObservableValue<String> oneProperty() {
        return one;
    }


    public String getOne()
    {
        return one.get();
    }

}
