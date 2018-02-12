package thegame;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStoreException;

/**
 * The starting point for the application
 *
 * The main screen gives the user three options to choose from:
 * 1) Start a new game, 2) see highscores and 3) go to settings screen
 */
public class MainScreen extends MIDlet implements CommandListener, HttpConnectionListener {
    // MODIFY THIS!
    private static final String URL         = "http://localhost/save.php";

    private static final String TITLE       = "The Game";
    private static final String NEW         = "New";
    private static final String HIGHSCORE   = "Highscore";
    private static final String SETTINGS    = "Settings";
    private static final String [] ELEMENTS = {NEW, HIGHSCORE, SETTINGS};
    private static final String EXIT        = "Exit";


    private Display display;
    private List startupList;
    private Command exit;
    private SettingsScreen settingsScreen;
    private HighScoreScreen highscoreScreen;
    
    private GameScreen gameScreen;
    private PersistentStorage ps;

    private boolean firstTime;

    /**
     * Initializes the main screen
     */
    public MainScreen() {
        ps = new PersistentStorage();
        firstTime = ps.openConnection();

        // Create the highscore screen
        highscoreScreen = new HighScoreScreen("Highscores", this);
           
        // Create Settings Screen
        settingsScreen = new SettingsScreen(this, ps);

        // Create the list
        startupList = new List(TITLE, List.IMPLICIT, ELEMENTS, null);
        
        // Create the commands
        exit = new Command(EXIT, Command.EXIT, 0);

        // Add commands to List and set listeners
        startupList.addCommand(exit);
        startupList.setCommandListener(this);
    }

    /**
     * When starting for the first time, a splash screen
     * is displayed. 
     */
    public void startApp() {
         // Create the Game Screen
        gameScreen = new GameScreen(this);
        
        if(display == null) {
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
        if(c == exit && d == startupList) {
            destroyApp(false);
            notifyDestroyed();
        } else if(c == List.SELECT_COMMAND && d == startupList) {

            String chosen = startupList.getString(startupList.getSelectedIndex());

            if(chosen.equals(SETTINGS)) {
                display.setCurrent(settingsScreen);
                try {
                    settingsScreen.openSettings();
                } catch (RecordStoreException ex) {
                    ex.printStackTrace();
                }
            } 
            else if(chosen.equals(NEW)) {
                display.setCurrent(gameScreen);
                gameScreen.start();
            }
            else if(chosen.equals(HIGHSCORE)) {
                display.setCurrent(highscoreScreen);
                highscoreScreen.open();
            }


        }
    }

    /**
     * Initialize the main screen
     *
     * If the app was opened for the first time, an alert is displayed for
     * the user to choose if he/she would want to change the settings first.
     * If user decides to choose this, the settings screen is shown. Otherwise
     * the main screen with the list is shown.
     */
    public void initialize() {
        if(firstTime) {
            Alert alert = new Alert("First time?", "It seems that this is the first time you play this game. Want to set the settings first?", null, AlertType.INFO);
            Command ok = new Command("Ok", Command.OK, 0);
            Command dismiss = new Command("No thanks!", Command.CANCEL, 1);
            alert.addCommand(ok);
            alert.addCommand(dismiss);
            alert.setTimeout(Alert.FOREVER);
            alert.setCommandListener(new CommandListener() {
                public void commandAction(Command c, Displayable d) {
                    if(c.getLabel().equals("Ok")) {
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

    /**
     * Shows the list of main choices (New, settings, highscore)
     */
    public void showMainScreen() {
        display.setCurrent(startupList);
    }

    /**
     * Saves the new highscore to server and record store
     *
     * When the game ends, this method is called. The given score
     * is saved to record store if the score was a new highscore. Also
     * the new highscore is sent to server if user chooses so.
     *
     * @param score the score player got
     **/
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

                    getLocationAndSaveToServer();
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

    /**
     * Retrieves location in a new thread
     *
     * Retrieves a location in a separate thread and makes callback
     * to either locationFix or locationError.
     */
    public void getLocationAndSaveToServer() {
        (new LocationRetriever(this)).start();
    }

    /**
     * When location is retrieved
     * 
     * When the LocationRetriever has a fix on location, this method
     * is called with the current location. After receiving the location,
     * the location is saved to server in separate thread.
     * 
     * @param longitude the longitude
     * @param latitude the latitude
     */
    public void locationFix(double longitude, double latitude) {
        saveScoreAndLocationToCloud(longitude, latitude, false);
    }

    /**
     * If location was not received
     */
    public void locationError() {
        saveScoreAndLocationToCloud(0, 0, true);
    }

    /**
     * Saves the score and location to server
     *
     * Saves the score and loction to server. The score is retrieved from
     * settings screen and location information is given in parameters
     *
     * @param longitude the longitude
     * @param latitude the latitude
     */
    private void saveScoreAndLocationToCloud(double longitude, double latitude, boolean error) {
        showMainScreen();

        String name = settingsScreen.getName().trim();
        int score   = settingsScreen.getScore();

        String url;
        Ticker ticker;

        if(!error) {
            url = URL + "?name=" + name + "&score=" + score + "&longitude=" + longitude + "&latitude=" + latitude;
            ticker = new Ticker("Saving highscore to server");
        }
        else {
            url = URL + "?name=" + name + "&score=" + score + "&longitude=NoLoc&latitude=NoLoc";
            ticker = new Ticker("No GPS.");
        }

        startupList.setTicker(ticker);
        
        MyHttpConnection conn = new MyHttpConnection(url, this);
        conn.start();

    }

    /**
     * When http connection is finished
     *
     * When http connection is finished, the HttpConnectionListener makes
     * callback to this method with the results. User is informed if score
     * and location was saved to server. 
     *
     * @param text the text retrieved via http connection
     */
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
