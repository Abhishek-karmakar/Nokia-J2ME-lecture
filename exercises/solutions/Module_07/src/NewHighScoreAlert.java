package thegame;

import javax.microedition.lcdui.*;

/**
 * Simple alert for asking if user wants to send his/her information to
 * server.
 */
public class NewHighScoreAlert extends Alert implements CommandListener {
    private Command yes;
    private Command no;
    private MainScreen host;
    private int score;

    /**
     * Initializes the Alert
     *
     * @param score score to be shown in alert
     * @param host reference to mainscreen s
     */
    public NewHighScoreAlert(int score, MainScreen host) {
        super("New Highscore!", "Your score was " + score + ". Want to send the score to cloud?", null, AlertType.INFO);
        this.host = host;
        this.score = score;
        yes = new Command("Send", Command.OK, 0);
        no = new Command("No thanks", Command.CANCEL, 1);
        addCommand(yes);
        addCommand(no);
        setTimeout(Alert.FOREVER);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if(c == yes) {
            host.saveScoreToCloud();

        } else if(c == no) {
            host.showMainScreen();
        }
    }
}
