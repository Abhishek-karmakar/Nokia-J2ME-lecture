package thegame;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Ticker;

/**
 * The screen for highscores
 *
 * Highscore screen displayes common highscore for all players. The
 * scores are fetched from web-server. Highscores are displayed as
 * CustomItem objects (HighScoreCustomItem)
 */
public class HighScoreScreen extends Form implements HttpConnectionListener {
   /** URL to server (MODIFY THIS!)**/
    private final String URL = "http://localhost/showall.php";

    /** Reference to host **/
    private MainScreen host;

 
    /**
     * Initializes the screen with given title and reference to host
     *
     * @param title Title of the Highscore screen
     * @param h Reference to host
     */
    public HighScoreScreen(String title, MainScreen h) {
        super(title);
        this.host = h;

        addCommand(new Command("Back", Command.BACK, 0));
        setCommandListener(new CommandListener() {

            public void commandAction(Command c, Displayable d) {
                host.showMainScreen();
            }

        });
    }

    /**
     * Opens connection to web-server
     *
     * Opens connection to web server, fetches the result in separate
     * thread. Shows a ticker while fetching the results. When done, the
     * separate thread makes a callback to method connectionReady() with
     * the results
     */
    public void open() {
         MyHttpConnection conn = new MyHttpConnection(URL, this);
         setTicker(new Ticker("Retrieving.."));
         conn.start();
    }

    /**
     * Appends new custom item to the form
     */
    private void append(String name, String score, String longitude, String latitude) {
        int width = getWidth();
        String location = "Longitude: " + longitude + " " + "Latitude: " + latitude;
        append(new HighScoreCustomItem(name, score, location, width));
    }

    /**
     * Parses the given String and transforms it to custom item
     */
    private void parse(String allhighscores) {
        String line = "";
        for(int i=0; i<allhighscores.length(); i++) {
            if(allhighscores.charAt(i) != ',') {
                line = line + allhighscores.charAt(i);
            }
            else {
                i++;
                parseLine(line);
                line = "";
            }
        }
    }

    /**
     * Parses each line of given http-result
     */
    private void parseLine(String line) {
        int firstIndex  = line.indexOf('|');
        int secondIndex = line.indexOf('|', firstIndex+1);
        int thirdIndex  = line.indexOf("|", secondIndex+1);

        String name      = line.substring(0, firstIndex);
        String score     = line.substring(firstIndex+1, secondIndex).trim();
        String longitude = line.substring(secondIndex+1, thirdIndex).trim();
        String latitude  = line.substring(thirdIndex+1, line.length()-2).trim();

        append(name, score, longitude, latitude);

    }

    /**
     * When connection is ready and highscores are fetched via Http
     * connection, this method starts to transform the given string to
     * Custom Item objects.
     *
     * @param alltext Http Connection results (highscores)
     */
    public void connectionReady(String alltext) {
        if(!alltext.equals("")) {
            deleteAll();
            parse(alltext);
            setTicker(null);
        } else {
            StringItem temp = new StringItem("Problem","Problem loading highscore");
            append(temp);
        }
    }

}
