package thegame;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStoreException;

public class MainScreen extends MIDlet implements CommandListener, HttpConnectionListener {
    private static final String URL         = "http://localhost/save.php";

    private Display display;
    private List startupList;
    private GameScreen gamescreen;
    private SettingsScreen settingsScreen;
    private Command exit;
    private String[] ELEMENTS = {"New", "Highscore", "Settings"};
    private PersistentStorage ps;
    private boolean firstTime;
    private HighScoreScreen highscoreScreen;
    
    public MainScreen() {
        startupList = new List("The Game", List.IMPLICIT, ELEMENTS, null);
        ps = new PersistentStorage();

        // Create the highscore screen
        highscoreScreen = new HighScoreScreen("Highscores", this);

        // Opens record store, if does not exist, will create new
        // and initialize it with default values. Will return true,
        // if the record store was empty when launching.

        firstTime = ps.openConnection();
        settingsScreen = new SettingsScreen(this, ps);

        gamescreen = new GameScreen(this);
        exit = new Command("Exit", Command.EXIT, 0);
        startupList.addCommand(exit);
        startupList.setCommandListener(this);
    }

    // SplashScreen calls this method
    public void initialize() {
        // If record store was empty
        if (firstTime) {
            Alert alert = new Alert("First time?", "It seems that this is the first time you play this game. Want to set the settings first?", null, AlertType.INFO);
            Command ok = new Command("Ok", Command.OK, 0);
            Command dismiss = new Command("No thanks!", Command.CANCEL, 1);
            alert.addCommand(ok);
            alert.addCommand(dismiss);
            alert.setTimeout(Alert.FOREVER);
            alert.setCommandListener(new CommandListener() {

                public void commandAction(Command c, Displayable d) {
                    if (c.getLabel().equals("Ok")) {
                        display.setCurrent(settingsScreen);
                        
                    } else {
                        display.setCurrent(startupList);

                    }
                }
            });

            display.setCurrent(alert);

        } else {
            showMainScreen();
        }
    }



    public void startApp() {
        if (display == null) {
            display = Display.getDisplay(this);
        }

        display.setCurrent(new SplashScreen(this));
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        // Close connectionto recordstore
        ps.closeConnection();
    }

    public void commandAction(Command c, Displayable d) {
        if (c == exit && d == startupList) {
            destroyApp(false);
            notifyDestroyed();
        } else if (c == List.SELECT_COMMAND && d == startupList) {

            String chosen = startupList.getString(startupList.getSelectedIndex());
            if (chosen.equals("Settings")) {
                display.setCurrent(settingsScreen);
                try {
                    settingsScreen.openSettings();
                } catch (RecordStoreException ex) {
                    ex.printStackTrace();
                }

            } else if (chosen.equals("New")) {
                display.setCurrent(gamescreen);
                gamescreen.start();
            } else {
                display.setCurrent(highscoreScreen);
                highscoreScreen.open();
            }
        }
    }

    public void showMainScreen() {
        display.setCurrent(startupList);
    }

   
    public void endGame(int score) {
        try {

            // result is true if new highscore was saved
            boolean wasNewHighScore = ps.saveHighScore(score);

            if(wasNewHighScore) {
                // Set new high score to settings.
                settingsScreen.setScore(score);
            }

            String title = "Game Over!";

            boolean saveAutomatically = settingsScreen.saveAutomatically();
            String userName           = settingsScreen.getName();

            if(wasNewHighScore) {
                // Ask if player wants to send the score to server
                if(!saveAutomatically) {
                    NewHighScoreAlert scoreAlert = new NewHighScoreAlert(score, this);
                    display.setCurrent(scoreAlert, startupList);
                // Send automatically to server
                } else {
                    Alert scoreInfo = new Alert("New Highscore!!", "New Highscore: " + score + "!", null, AlertType.INFO);
                    display.setCurrent(scoreInfo, startupList);

                    saveScoreToCloud();
                }

            } else {
                Alert scoreInfo = new Alert("Game Over", "Your score was " + score, null, AlertType.INFO);
                scoreInfo.setTimeout(Alert.FOREVER);
                display.setCurrent(scoreInfo, startupList);
            }

        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }


    }


    public void saveScoreToCloud() {
        showMainScreen();

        String name = settingsScreen.getName().trim();
        int score   = settingsScreen.getScore();

        String url;
        Ticker ticker;

        url = URL + "?name=" + name + "&score=" + score + "&longitude=NoLoc&latitude=NoLoc";
        ticker = new Ticker("Saving Score and Name to cloud");
        startupList.setTicker(ticker);

        MyHttpConnection conn = new MyHttpConnection(url, this);
        conn.start();

    }

    public void connectionReady(String text) {
        // If server side sent "1"
        if(text.equals("1")) {
            Alert a = new Alert("Info", "Highscore was saved to server.", null, AlertType.INFO);
            display.setCurrent(a, startupList);

        } else {
            Alert a = new Alert("Problem", "Highscore was not saved to server.", null, AlertType.ERROR);
            display.setCurrent(a, startupList);
        }
        startupList.setTicker(null);
    }
}
