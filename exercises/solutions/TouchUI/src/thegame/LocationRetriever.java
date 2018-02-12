package thegame;


import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.location.*;


/**
 * The LocationRetriever class retrieves the location (gps) from
 * the user.
 */
public class LocationRetriever extends Thread {

    /** Reference to host **/
    private MainScreen host;

    /**
     * Initializes the LocationRetriever
     *
     * @param host reference to host
     */
    public LocationRetriever(MainScreen host) {
        this.host = host;
    }

    /**
     * Starts to fetch location in separate thread
     */
    public void run() {
        try {
            checkLocation();
        } catch (Exception ex) {
            //ex.printStackTrace();
            host.locationError();
        }
    }

    /**
     * Fetches the user's location.
     */
    private void checkLocation() throws Exception {
        Location l;
        LocationProvider lp;
        Coordinates c;
  
        Criteria cr = new Criteria();
        cr.setHorizontalAccuracy(2000);
        cr.setVerticalAccuracy(2000);

        lp = LocationProvider.getInstance(cr);

        l = lp.getLocation(60);

        c = l.getQualifiedCoordinates();

        if (c != null) {
            double lat = c.getLatitude();
            double lon = c.getLongitude();
            host.locationFix(lat,lon);
        } else {
            host.locationError();
        }
    }
}
