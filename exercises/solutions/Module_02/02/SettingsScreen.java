package thegame;

import javax.microedition.lcdui.*;

public class SettingsScreen extends Form implements CommandListener {

    private final String TITLE = "Settings";
    private final String SAVETOSERVER = "Store name and location automatically to server";
    private final String NAME = "Your Name: ";
    private final String BACK = "Back";
    private final String HIGHSCORE = "Highscore:";

    private TextField name;
    private ChoiceGroup saveToServer;
    private StringItem highScore;
    private Command back;
    private MainScreen host;
  
    public SettingsScreen(MainScreen host) {
        super("");
        setTitle(TITLE);
        this.host = host;

        String[] items = {SAVETOSERVER};

        name = new TextField(NAME, "Untitled", 50, TextField.ANY);
        saveToServer = new ChoiceGroup("", Choice.MULTIPLE, items, null);
        highScore = new StringItem(HIGHSCORE, "0");
        back = new Command(BACK, Command.BACK, 0);

        append(name);
        append(saveToServer);
        append(highScore);

        addCommand(back);
        setCommandListener(this);

    }

    public void commandAction(Command c, Displayable d) {
        if (c == back && d == this) {
            host.showMainScreen();
        }
    }
}
