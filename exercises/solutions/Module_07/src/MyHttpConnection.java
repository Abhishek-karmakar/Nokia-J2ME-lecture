package thegame;


import javax.microedition.lcdui.*;
import java.io.*;
import javax.microedition.io.*;

/**
 * Class for making http connections
 *
 * This is a general class for making http connection. The connection and
 * retrieving server side data is done in a separate thread. After successfully
 * retrieval, the thread calls it's hosts connectionReady(String text) method
 * via the HttpConnectionListener interface.
 */
public class MyHttpConnection extends Thread {

    private String url;
    private String text;

    private HttpConnectionListener host;

    /**
     * Initializes the HttpConnection
     *
     * @param url The url which connection is made to
     * @param host The host that is informed when retrieval is ready
     */
    public MyHttpConnection(String url, HttpConnectionListener host) {
        this.url = url;
        this.host = host;
    }

    /**
     * Fetches the results from given url
     *
     * Method makes connection to given url, fetches the results and
     * calls it's host connectionReady(String text) method.
     */
    public void run() {
        HttpConnection hc = null;
        DataInputStream in = null;

        try {
            hc = (HttpConnection) Connector.open(this.url);

            in = new DataInputStream(hc.openInputStream());
            int ch;
            String alltext = new String("");
            while ((ch = in.read()) != -1) {
                alltext += ((char) ch);
            }

            host.connectionReady(alltext);

        } catch (Exception e) {
            e.printStackTrace();
            host.connectionReady("");
        } finally {
            try {
                in.close();
                hc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}