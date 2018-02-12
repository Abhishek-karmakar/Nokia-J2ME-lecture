package thegame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.*;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

// Touch UI
import com.nokia.mid.ui.gestures.GestureEvent;
import com.nokia.mid.ui.gestures.GestureInteractiveZone;
import com.nokia.mid.ui.gestures.GestureListener;
import com.nokia.mid.ui.gestures.GestureRegistrationManager;


/**
 * The game screen and it's logic
 */
public class GameScreen extends GameCanvas implements Runnable, GestureListener  {

    // Direction for the stone, left = false, right = true
    private boolean rockDirection;

    // Horizontal speed for the rock
    private int rockXSpeed;

    // Vertical speed for the rock
    private int rockYSpeed;

    // Gravity affecting vertical speed
    private final int gravity = 2;

    // Paths to images
    private final String GRASS_IMAGE = "/grass.png";
    private final String MR_SMITH_IMAGE = "/mrsmith.png";
    private final String ROCK_IMAGE = "/rock.png";
    private final String BIRD_IMAGE = "/bird.png";
    private final String EXPLOSION_IMAGE = "/explosion.png";

    // The Sprites used in the game

    // Grass
    private TiledLayer grassBackground;
    
    // The character sprite
    private Sprite mrSmith;

    // Rock sprite
    private Sprite rock;

    // Bird sprite
    private Sprite bird;

    // Explosion sprite
    private Sprite explosion;

    // Layermanager for sprites
    private LayerManager layerManager;

    // Characters direction
    private final static boolean DIRECTION_LEFT = false;
    private final static boolean DIRECTION_RIGHT = true;

    // The direction which the character is facing
    private boolean direction;

    // Is rock in the air
    private boolean rockIsThrown = false;

    // Is game on
    private boolean gameIsOn = false;

    // Was bird hit by a stone
    private boolean birdHitByStone = false;

    // Game area's height
    private int height;

    // Game area's width
    private int width;

    // Bird's pixel increment
    int birdSpeedInPixels;

    // Player's score
    private int score = 0;

    // Reference to host
    private MainScreen host;

    // Sounds
    private Player birdSound;
    private Player explosionSound;
    private Player throwRockSound;
    private Player backgroundSound;
    private Player deathSound;

    /**
     * Initializes the game screen.
     *
     * Loads sounds and sets the game to full screen. Game does not
     * start yet. To start the game, use start() - method.
     *
     * @param host reference to MainScreen
     */
    public GameScreen(MainScreen host) {
        super(false);
        this.host = host;

	// Create the GestureInteractiveZone
	GestureInteractiveZone giz =  new GestureInteractiveZone(GestureInteractiveZone.GESTURE_TAP);
	// Register it
	GestureRegistrationManager.register(this, giz);
	// Set the listener
	GestureRegistrationManager.setListener(this, this);

        
        try {
            InputStream in1 = getClass().getResourceAsStream("/bird.wav");
            birdSound = Manager.createPlayer(in1, "audio/x-wav");
            birdSound.prefetch();

            InputStream in2 = getClass().getResourceAsStream("/explosion.wav");
            explosionSound = Manager.createPlayer(in2, "audio/x-wav");
            explosionSound.prefetch();

            InputStream in3 = getClass().getResourceAsStream("/throwRock.wav");
            throwRockSound = Manager.createPlayer(in3, "audio/x-wav");
            throwRockSound.prefetch();

            InputStream in4 = getClass().getResourceAsStream("/backgroundmusic.mid");
            backgroundSound = Manager.createPlayer(in4, "audio/mid");
            backgroundSound.prefetch();

            /*InputStream in5 = getClass().getResourceAsStream("/death.wav");
            deathSound = Manager.createPlayer(in5, "audio/x-wav");
            deathSound.prefetch();*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        setFullScreenMode(true);
    }

    /**
     * Creates Sprites
     */
    private void createSprites() {
        createBackground();
        createRock();
        createMrSmith();
        createBird();
        createExplosion();
    }

    /**
     * Starts the game
     * 
     * Initializes the game to starting point and starts appropriate threads
     */
    public void start() {

        // Initialize game variables to starting point.

        score = 0;
        birdSpeedInPixels = 5;
        rockDirection = DIRECTION_RIGHT;
        direction = DIRECTION_RIGHT;
        
        // Get the height of the game area
        height = getHeight();
        // Get the width of the game area
        width = getWidth();

        // Setting the layermanager
        layerManager = new LayerManager();
        layerManager.setViewWindow(0, 0, width, height);

        createSprites();

        gameIsOn = true;

        // Start the game thread
        new Thread(this).start();

        // Start bird thread
        new BirdThread().start();

        
        backgroundSound.setLoopCount(1000);

        try {
            backgroundSound.start();
        } catch (MediaException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Creates the main character sprite
     */
    private void createMrSmith() {
        mrSmith = new Sprite(loadImage(MR_SMITH_IMAGE), 30, 30);
        int xpos = width / 2;
        int ypos = height - mrSmith.getHeight() - grassBackground.getHeight();
        mrSmith.setPosition(xpos, ypos);
        mrSmith.defineReferencePixel(15, 15);
        layerManager.append(mrSmith);
    }

    /**
     * Creates the explosion sprite
     */
    private void createExplosion() {
        explosion = new Sprite(loadImage(EXPLOSION_IMAGE), 75, 75);
        explosion.defineReferencePixel(37, 37);
        explosion.setVisible(false);
        layerManager.append(explosion);
    }

    /**
     * Creates the rock sprite
     */
    private void createRock() {
        rock = new Sprite(loadImage(ROCK_IMAGE), 12, 12);
        rock.defineReferencePixel(6, 6);
        rock.setVisible(false);
        layerManager.append(rock);
    }

    /**
     * Creates the bird sprite
     */
    private void createBird() {
        bird = new Sprite(loadImage(BIRD_IMAGE), 33, 31);
        bird.defineReferencePixel(15, 15);
        bird.setPosition(0, 30);
        layerManager.append(bird);
    }

    /**
     * Creates background
     */
    private void createBackground() {
        Image backgroundimage = loadImage(GRASS_IMAGE);
        grassBackground = new TiledLayer(8, 1, backgroundimage, 32, 32);

        int numberOfTiles = width / 32 + 1;

        for (int i = 0; i < numberOfTiles; i++) {
            grassBackground.setCell(i, 0, 1);
        }

        grassBackground.setPosition(0, height - grassBackground.getHeight());
        layerManager.append(grassBackground);
    }

    /**
     * Creates image from path
     *
     * @param path path to image file
     * @return created Image object from path
     */
    private Image loadImage(String path) {
        Image image = null;

        try {
            image = Image.createImage(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return image;
    }

    /**
     * Updates screen
     *
     * This method is called from separate thread.
     */
    private void updateScreen() {
        Graphics g = getGraphics();
        g.setColor(255, 255, 255);
        g.fillRect(0, 0, width, height);
        g.setColor(0, 0, 0);

        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        g.drawString("Score: " + score, 2, 2, Graphics.TOP | Graphics.LEFT);
        layerManager.paint(g, 0, 0);
        flushGraphics();
    }


    class BirdThread extends Thread {

        private int birdsSpeedInMilliseconds = 50;

        public void run() {
            while (gameIsOn) {
                // If the bird was hit by a stone
                if (birdHitByStone) {
                    explodeBird();

                } else { // otherwise keep moving the bird
                    moveBird();
                }

                try {
                    sleep(birdsSpeedInMilliseconds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * Move the bird
         */
        private void moveBird() {
            // Move the bird

            bird.move(birdSpeedInPixels, 0);
            bird.nextFrame();

            // Change direction
            if (bird.getX() < 0) {
                birdSpeedInPixels = birdSpeedInPixels * -1;
                bird.move(0, +10);
                bird.setTransform(Sprite.TRANS_NONE);
            } else if (bird.getX() > width - bird.getWidth()) {
                birdSpeedInPixels = birdSpeedInPixels * -1;
                bird.move(0, +10);
                bird.setTransform(Sprite.TRANS_MIRROR);
            }
        }

        /**
         * Animate bird explosion
         */
        private void explodeBird() {
            // Initialize the explosion sprite
            if (bird.isVisible()) {

                try {
                    birdSound.stop();
                    explosionSound.start();
                } catch (MediaException ex) {
                    ex.printStackTrace();
                }

                bird.setVisible(false);
                explosion.setPosition(bird.getX() - bird.getWidth() / 2, bird.getY() - bird.getHeight() / 2);
                explosion.setVisible(true);
                explosion.setFrame(0);
            } else {
                if (explosion.getFrame() < 11) {
                    explosion.nextFrame();
                } else {
                    explosion.nextFrame();
                    explosion.setVisible(false);
                    birdHitByStone = false;
                    bird.setVisible(true);
                    bird.setPosition(0, 30);

                    bird.setTransform(Sprite.TRANS_NONE);

                    if(birdsSpeedInMilliseconds >= 20)
                        birdsSpeedInMilliseconds -= 5;

                    if(birdSpeedInPixels < 0)
                        birdSpeedInPixels = birdSpeedInPixels * -1;
                    if(birdSpeedInPixels <= 15)
                        birdSpeedInPixels += 1;
                   
                    score++;

                }
            }
        }
    }


    /**
     * Getting user input
     */
    private void getUserInput() {
        int ks = getKeyStates();
        if ((ks & RIGHT_PRESSED) != 0) {
            if (mrSmith.getX() < width - mrSmith.getWidth()) {
                mrSmith.move(3, 0);
                mrSmith.setTransform(Sprite.TRANS_NONE);
                mrSmith.nextFrame();
                direction = DIRECTION_RIGHT;
            }
        }
        if ((ks & LEFT_PRESSED) != 0) {
            if (mrSmith.getX() > 0) {
                mrSmith.setTransform(Sprite.TRANS_MIRROR);
                mrSmith.nextFrame();
                mrSmith.move(-3, 0);
                direction = DIRECTION_LEFT;
            }
        }
        if ((ks & FIRE_PRESSED) != 0) {
            if (rockIsThrown == false) {
                try {
                    throwRockSound.start();
                } catch (MediaException ex) {
                    ex.printStackTrace();
                }

                rockIsThrown = true;
                rock.setPosition(mrSmith.getX() + rock.getWidth() / 2, mrSmith.getY() + rock.getHeight() / 2);
                rock.setVisible(true);

                rockYSpeed = getRandom(10) + 25;
                rockXSpeed = getRandom(4) + 3;
                rockDirection = direction;
            }
        }
    }

    /**
     * Move the rock
     */
    private void moveRock() {
        // Stone's speed is reduced by the gravity
        if (rockYSpeed > -25) {
            rockYSpeed = rockYSpeed - gravity;
        }

        if ( rockDirection == DIRECTION_LEFT ) {
            rock.move(-rockXSpeed, -rockYSpeed);
        } else {
            rock.move(rockXSpeed, -rockYSpeed);
        }

        if ( grassBackground.getY() - rock.getRefPixelY() <= 0 ) {
            rockIsThrown = false;
            rock.setVisible(false);
        }
    }



    /**
     * Plays birdsound with every given milliseconds
     *
     * @param ticks number of ticks
     * @param milliseconds number of milliseconds
     * */
    private void playBirdSoundEvery(int ticks, int milliseconds) {
        if (ticks % milliseconds == 0) {
            try {
                birdSound.start();
            } catch (MediaException ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * The game thread
     */
    public void run() {

        int ticks = 0;

        // Repeat while the game is on. Game will end
        // when Mr Smith dies (gets hit by the bird)
        while (gameIsOn) {

            // Plays bird sound every 4th second
            playBirdSoundEvery(ticks, 4000);
            
            // Get user input
            getUserInput();

            // If rock was thrown, move the rock
            if (rockIsThrown) {
                moveRock();
            }

            // Wait for 20 milliseconds
            try {
                Thread.sleep(20);
                ticks += 20;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            // Update the screen
            updateScreen();

            // If bird collides with mr smith
            if (bird.collidesWith(mrSmith, true) && !birdHitByStone) {
                gameIsOn = false;
            }

            // If rock collided with bird
            if (rock.collidesWith(bird, true) && !birdHitByStone) {
                birdHitByStone = true;
            }

        }

        // Game ended
        try {
            //deathSound.start();
            backgroundSound.stop();
            birdSound.stop();

        } catch (MediaException ex) {
            ex.printStackTrace();
        }
        
        // Back to the main screen
        host.endGame(score);
    }
	
	// Touch UI Gesture Action
    public void gestureAction(Object container, GestureInteractiveZone giz, GestureEvent event) {
        
     // If we get a TAP gesture, we just throw a rock!
     switch(event.getType()) {

        case GestureInteractiveZone.GESTURE_TAP:
			if (rockIsThrown == false) {
					try {
						throwRockSound.start();
					} catch (MediaException ex) {
						ex.printStackTrace();
					}

					rockIsThrown = true;
					rock.setPosition(mrSmith.getX() + rock.getWidth() / 2, mrSmith.getY() + rock.getHeight() / 2);
					rock.setVisible(true);

					rockYSpeed = getRandom(10) + 25;
					rockXSpeed = getRandom(4) + 3;
					rockDirection = direction;
				}        
	    break;
        }
    }
    
    private Random random = new Random(System.currentTimeMillis());

    
    private int getRandom(int bound) {
        return Math.abs(random.nextInt() % bound);
    }
}



