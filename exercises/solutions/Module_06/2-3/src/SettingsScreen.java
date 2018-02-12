package thegame;

import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;

/**
 * Settings Screen
 *
 * The Settings screen contains a form with three items:
 * 1) user's name, 2) Checkbox for confirming if user wants to automatically
 * send his/her information to server and 3) User's highscore
 */
public class SettingsScreen extends Form implements CommandListener {
    private final String TITLE         = "Settings";
    private final String SAVETOSERVER  = "Store name and location automatically to server";
    private final String NAME          = "Your Name: ";
    private final String BACK          = "Back";
    private final String HIGHSCORE     = "Highscore:";

    private TextField name;
    private ChoiceGroup saveToServer;
    private StringItem highScore;

    private Command back;
    private MainScreen host;

    private PersistentStorage ps;

    /**
     * Initializes the setting screen
     *
     * @param reference to host
     * @param reference to Persistent Storage for retrieving and saving settings
     */
    public SettingsScreen(MainScreen host, PersistentStorage ps) {
        super("");
        setTitle(TITLE);
        this.host = host;
        this.ps   = ps;


        String [] items = {SAVETOSERVER};

        name               = new TextField(NAME, "Untitled", 50, TextField.ANY);
        saveToServer       = new ChoiceGroup("", Choice.MULTIPLE, items, null);
        highScore          = new StringItem(HIGHSCORE, "0");
        back               = new Command(BACK, Command.BACK, 0);

        append(name);
        append(saveToServer);
        append(highScore);

        addCommand(back);
        setCommandListener(this);

    }

    /**
     * If user has selected to save automatically his/her information to
     * server
     *
     * @return true if checked, otherwise false
     */
    public boolean saveAutomatically() {
        return saveToServer.isSelected(0);
    }

    /**
     * Get user's name
     *
     * @return user's name
     */
    public String getName() {
        return name.getString();
    }

    /**
     * Get user's score
     *
     * @return score
     */
    public int getScore(){
        int score = Integer.parseInt(highScore.getText());
        return score;
    }

    /**
     * Set the score
     *
     * @param score score to be set.
     */
    public void setScore(int score) {
        String scoreString = "" + score;
        highScore.setText(scoreString);
    }


    public void commandAction(Command c, Displayable d) {
        if(c == back && d == this) {
            try {
                saveSettings();
                host.showMainScreen();
            } catch (RecordStoreException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Saves the settings to record store
     */
    private void saveSettings() throws RecordStoreException {
        ps.saveSettings(name.getString(), saveToServer.isSelected(0));
    }

    /**
     * Opens the settings from recordstore
     */
    public void openSettings() throws RecordStoreException {
        String [] temp = ps.retrieveSettings();
        name.setString(temp[0]);
        if(temp[1].equals("true")) {
            saveToServer.setSelectedIndex(0, true);
        } else {
            saveToServer.setSelectedIndex(0, false);
        }
        highScore.setText(temp[2]);
    }
}
