package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import sample.Home;
import sample.model.Emoji;
import sample.util.Client;

public class EmojiController
{

    private Home home;

    private Client client;

    @FXML
    private TableView<Emoji> emojiTable;
    @FXML
    private TableColumn<Emoji, String> one;


    @FXML
    private void initialize()
    {

        // Initialize the person table with the two columns.
        one.setCellValueFactory(cellData -> cellData.getValue().oneProperty());


        // Listen for selection changes and show the person details when changed.
        emojiTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                {
                    try
                    {
                        SendEmoji(newValue.getOne());
                    }
                    catch (NullPointerException ignored)
                    {
                    }
                });//这个地方将表情包传到homepage

        emojiTable.setRowFactory(tv ->
        {
            TableRow<Emoji> row = new TableRow<>();
            row.setOnMouseClicked(event ->
            {
                if (event.getClickCount() == 1 && (!row.isEmpty()))
                {
                    Emoji rowData = row.getItem();
                    emojiTable.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
    }

    public void setClient(Client client)
    {
        this.client = client;
    }

    void SendEmoji(String emoji)
    {

        client.getChatController().getEmoji(emoji);
    }


    public void setApp(Home home)
    {
        this.home = home;
        // Add observable list data to the table
        emojiTable.setItems(home.getEmojiData());
    }


}
