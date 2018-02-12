package thegame;

import javax.microedition.lcdui.*;

public class SplashScreen extends Canvas implements Runnable {
    private final static String TITLE = "The Game!";

    private MainScreen host;

    public SplashScreen(MainScreen host) {
        this.host = host;

        new Thread(this).start();
        setFullScreenMode(true);
    }

    public void paint(Graphics g) {
        int width  = getWidth();
        int height = getHeight();

        int rectangleheight = height - 10;
        int rectanglewidth = width - 10;

        g.setColor(255, 0, 0);
        g.fillRect(0, 0, width, height);

        g.setColor(255, 255, 255);
        g.setStrokeStyle(Graphics.DOTTED);
        g.drawRect(5, 5, rectanglewidth, rectangleheight);

        g.setColor(255,255,255);
        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, Font.SIZE_LARGE));

        g.drawString(TITLE, width/2, height/2, Graphics.BOTTOM | Graphics.HCENTER);
    }

    public void dismiss() {
        if (isShown()) {
            host.showMainScreen();
        }
    }

    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dismiss();
    }

    public void keyReleased(int keyCode) {
        dismiss();
    }

    public void pointerReleased(int x, int y) {
        dismiss();
    }
}
