package thegame;

/**
 * For receiving http connection results
 */
public interface HttpConnectionListener {
    /**
     * When http connection is ready, this method is called
     *
     * @param alltext the results fetched via http connection
     */
    public void connectionReady(String alltext);
}
