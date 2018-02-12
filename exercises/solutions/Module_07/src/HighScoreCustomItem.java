package thegame;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * One item in Highscore list (form)
 *
 * Highscores are shown in a form. Form consists of several
 * items that presents the player's name and score. This
 * class draws the name and score.
 */
public class HighScoreCustomItem extends CustomItem {

    private int width;
    private String name;
    private String score;
    private String location;
    private Font fontForName;
    private Font fontForOther;
    private int height;

    /**
     * Initalizes the item
     *
     * @param name player's name
     * @param score player's score
     * @param location player's location
     * @param width the width of the item in pixels
     */
    public HighScoreCustomItem(String name, String score, String location, int width) {
        super("");
        this.width = width;
        this.name = name;
        this.score = score;
        this.location = location;

        fontForName = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
        fontForOther = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);

        height = fontForName.getHeight() + fontForOther.getHeight() + fontForOther.getHeight() + 10;
    }

    protected int getMinContentWidth() {
        return this.width;
    }

    protected int getPrefContentHeight(int width) {
        return this.height;
    }

    protected int getPrefContentWidth(int height) {
        return this.width;
    }

    protected void paint(Graphics g, int w, int h) {

        g.setFont(fontForName);
        int he = fontForName.getHeight();

        g.drawString(name, 0, 0, Graphics.TOP | Graphics.LEFT);

        g.setFont(fontForOther);

        g.drawString("Score: " + score, 0, he, Graphics.TOP | Graphics.LEFT);

        he = he + fontForName.getHeight();

        g.drawString(location, 0, he, Graphics.TOP | Graphics.LEFT);
    }

    protected int getMinContentHeight() {
        return this.height;
    }
}
