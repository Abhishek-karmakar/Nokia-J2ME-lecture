package thegame;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStoreException;

public class MainScreen extends MIDlet implements CommandListener {

    private Display display;
    private List startupList;
    private Command exit;
    private String[] ELEMENTS = {"New", "Highscore", "Settings"};
    
    public MainScreen() {
        startupList = new List("The Game", List.IMPLICIT, ELEMENTS, null);
        exit = new Command("Exit", Command.EXIT, 0);
        startupList.addCommand(exit);
        startupList.setCommandListener(this);
    }

    public void startApp() {
        if(display == null) {
            display = Display.getDisplay(this);
        }

        display.setCurrent(new SplashScreen(this));
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        // Close connectionto recordstore
    }

    public void commandAction(Command c, Displayable d) {
        if (c == exit && d == startupList) {
            destroyApp(false);
            notifyDestroyed();
        } else if (c == List.SELECT_COMMAND && d == startupList) {

            String chosen = startupList.getString(startupList.getSelectedIndex());
            if(chosen.equals("Settings")) {
                display.setCurrent(new SettingsScreen(this));
            } else {
                Alert alert = new Alert("Title", chosen, null, AlertType.INFO);
                display.setCurrent(alert);
            }
        }
    }

    public void showMainScreen() {
        display.setCurrent(startupList);
    }
}
