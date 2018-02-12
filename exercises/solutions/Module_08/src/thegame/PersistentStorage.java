package thegame;

import javax.microedition.rms.*;

/**
 * PersistentStorage is responsible for saving settings to Record Store
 */
public class PersistentStorage {
    private RecordStore rs;

    // The id's for each record
    private final int NAME             = 1;
    private final int SAVENAMETOSERVER = 2;
    private final int HIGHSCORE        = 3;


    /**
     * Opens the record store
     *
     * Opens the record store. If record store does not exist, a
     * new record store is created.
     *
     * @return boolean true if new record store was created, otherwise false
     */
    public boolean openConnection() {
        if(rs == null) {
            try {
                rs = RecordStore.openRecordStore("TheGame", true);
                if (rs.getNumRecords() < 1) {
                    initialize();
                    return true;
                }

            } catch (RecordStoreException e) {
                e.printStackTrace();
                return false;
            }
       }
       return false;
    }

    /**
     * Closes the connection to record store
     */
    public void closeConnection() {
        try {
            if(rs != null) {
                rs.closeRecordStore();
                rs = null;
            }
        } catch(RecordStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the record store.
     *
     * Three records are created to store with values name = untitled,
     * saveName = false, and score = 0
     *
     * @throws RecordStoreException if initializing failes
     */
    private void initialize() throws RecordStoreException {

        String name           = "Untitled";
        String saveName       = "false";
        String score          = "0";

        rs.addRecord(name.getBytes(),          0, name.getBytes().length);
        rs.addRecord(saveName.getBytes(),      0, saveName.getBytes().length);
        rs.addRecord(score.getBytes(),         0, score.getBytes().length);

    }


    /**
     * Save name and save-info to record store
     *
     * Saves given parameter to recordstore
     *
     * @param name name to be saved
     * @param save information about is score and location saved automatically
     * @throws RecordStoreException if saving fails
     */
    public void saveSettings(String name, boolean save) throws RecordStoreException {
        openConnection();
        String saveNameToServer = "" + save;
        
        rs.setRecord(NAME,              name.getBytes(),             0, name.getBytes().length);
        rs.setRecord(SAVENAMETOSERVER,  saveNameToServer.getBytes(), 0, saveNameToServer.getBytes().length);
  
    }

    /**
     * Retrieves the name and save-info from the record store
     *
     * @return records in String array
     * @throws RecordStoreException if retrieval fails
     */
    public String[] retrieveSettings() throws RecordStoreException  {
        openConnection();

        String [] settings = new String[3];

        settings[0] = new String(rs.getRecord(NAME));
        settings[1] = new String(rs.getRecord(SAVENAMETOSERVER));
        settings[2] = new String(rs.getRecord(HIGHSCORE));
             
        return settings;
    }


    /**
     * Saves highscore to record store
     *
     * @param score score to be saved
     * @return true if new highscore was created, otherwise false
     * @throws RecordStoreException if saving failes
     *
     */
    public boolean saveHighScore(int score) throws RecordStoreException {
        openConnection();
        String scoreTemp = "" + score;

        String previousScore = new String(rs.getRecord(HIGHSCORE));
        int previousScoreInt = Integer.parseInt(previousScore);
        if(score > previousScoreInt) {
             rs.setRecord(HIGHSCORE, scoreTemp.getBytes(), 0, scoreTemp.getBytes().length);
             return true;
        } else {
            return false;
        }
    }

    /**
     * Retrieves highscore
     *
     * @return highscore saved to record store
     * @throws RecordStoreException if retrieval failes
     */
    public int retrieveHighScore() throws RecordStoreException {
        openConnection();
        int score = - 1;

        byte [] temp = rs.getRecord(HIGHSCORE);
        String string = new String(temp);
        score = Integer.parseInt(string);
        return score;
    }
}
