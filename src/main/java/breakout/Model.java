package breakout;
import java.util.ArrayList;

import java.util.List;
import javafx.scene.paint.*;
import javafx.application.Platform;
import sun.audio.*;

// The model represents all the actual content and functionality of the app
// For Breakout, it manages all the game objects that the View needs
// (the bat, ball, bricks, and the score), provides methods to allow the Controller
// to move the bat (and a couple of other fucntions - change the speed or stop
// the game), and runs a background process (a 'thread') that moves the ball
// every 20 milliseconds and checks for collisions

public class Model {
    // First,a collection of useful values for calculating sizes and layouts etc.

    public int B = 6; // Border round the edge of the panel
    public int M = 40; // Height of menu bar space at the top

    public int BALL_SIZE = 30; // Ball size
    public int BAT_WIDTH = 100;
    public int BAT_HEIGHT = 5;
    //	public int BRICK_WIDTH = 40; // Brick size - Width 0 - 120
    //	public int BRICK_HEIGHT = 30; // Brick size - Height 0 -

    public int BRICK_HEIGHT = 100; // Brick size - Height 0 -
    public int BRICK_WIDTH = 300; // Brick size - Width 0 - 120

    public int BoardHeight = 800;
    public int BoardWidth = 600;

    public int BAT_MOVE = 8; // Distance to move bat on each keypress // Changed from 5 to 8.. a bit faster
    // movement
    public int BALL_MOVE = 3; // Units to move the ball on each step // Ok here i see that this variable we
    // can edit for advanced levels

    public int HIT_BRICK = 50; // Score for hitting a brick // First we need to generate bricks
    public int HIT_BOTTOM = -200; // Score (penalty) for hitting the bottom of the screen

    private int brickNumber = 24;
    private int levelNumber = 1;

    // The other parts of the model-view-controller setup
    View view;
    Controller controller;
    GameObj GameObj;

    // The game 'model' - these represent the state of the game
    // and are used by the View to display it
    public GameObj ball; // The ball // viewable
    public List<GameObj> bricks; // The bricks // not viewable
    public GameObj bat; // The bat // viewable
    public int score = 0; // The score // viewable

    // variables that control the game
    public boolean gameRunning = true; // Set false to stop the game // There is key for stopping the game
    public boolean fast = false; // Set true to make the ball go faster // Same here, I wonder if we can display
    // game speed in window

    // initialisation parameters for the model
    public int width; // Width of game
    public int height; // Height of game

    // CONSTRUCTOR - needs to know how big the window will be
    public Model(int w, int h) {
        Debug.trace("Model::<constructor>");
        width = w;
        height = h;

        initialiseLevel(1);
    }

    public int BoardHeight() {
        return BoardHeight;
    }

    public int BoardWidth() {
        return BoardWidth;
    }

    public int BRICK_HEIGHT() {
        return BRICK_HEIGHT;
    }

    public int BRICK_WIDTH() {
        return BRICK_WIDTH;
    }

    // Initialise the game - reset the score and create the game objects
    public void initialiseLevel(int levelNumber) {
        switch (levelNumber) {
            case 2:
                level2();
                break;
            default:
                level1();
                score = 0;
                break;
        }
        ball = new GameObj(width / 2, height / 2, BALL_SIZE, BALL_SIZE, Color.RED);
        bat = new GameObj(width / 2, 770, BAT_WIDTH, BAT_HEIGHT, Color.WHITE);
        bricks = new ArrayList<>(); // Yes i see, I need to create code here.. let's DO IT ! :]

        // *[1]******************************************************[1]*
        // * Fill in code to add the bricks to the arrayList *
        // **************************************************************
        /*
         * int pos = 10;
         *
         * for (int i=0; i<BoardWidth()*0.2; i += BRICK_WIDTH()) for (int j=0;
         * j<BoardHeight()-15; j += BRICK_WIDTH()-5) { bricks.add(new GameObj(pos, 50,
         * BRICK_WIDTH, BRICK_HEIGHT, Color.BLUE)); pos = pos +60; }
         */

        int x = 5;
        int y = 50;

        // int brickNumber = 24;

        for (int i = 0; i < brickNumber; i++) {
            bricks.add(new GameObj(x, y, BRICK_WIDTH, BRICK_HEIGHT, Color.BLUE));
            x += BRICK_WIDTH + BRICK_WIDTH / 4;

            if (x + BRICK_WIDTH >= BoardWidth) {
                x = 5;
                y += BRICK_HEIGHT + BRICK_HEIGHT / 4;
            }
        }

    }
    // Animating the game
    // The game is animated by using a 'thread'. Threads allow the program to do
    // two (or more) things at the same time. In this case the main program is
    // doing the usual thing (View waits for input, sends it to Controller,
    // Controller sends to Model, Model updates), but a second thread runs in
    // a loop, updating the position of the ball, checking if it hits anything
    // (and changing direction if it does) and then telling the View the Model
    // changed.

    // When we use more than one thread, we have to take care that they don't
    // interfere with each other (for example, one thread changing the value of
    // a variable at the same time the other is reading it). We do this by
    // SYNCHRONIZING methods. For any object, only one synchronized method can
    // be running at a time - if another thread tries to run the same or another
    // synchronized method on the same object, it will stop and wait for the
    // first one to finish.

    // Start the animation thread
    public void startGame() {

        Thread t = new Thread(this::runGame); // create a thread running the runGame method
        t.setDaemon(true); // Tell system this thread can die when it finishes
        t.start(); // Start the thread running
    }

    // The main animation loop

    public void runGame() {
        try {
            // set gameRunning true - game will stop if it is set false (eg from main
            // thread)
            setGameRunning(true);
            while (getGameRunning()) {
                updateGame(); // update the game state
                modelChanged(); // Model changed - refresh screen
                Thread.sleep(getFast() ? 10 : 20); // wait a few milliseconds
            }
        } catch (Exception e) {
            Debug.error("Model::runAsSeparateThread error: " + e.getMessage());
        }
    }

    // updating the game - this happens about 50 times a second to give the
    // impression of movement
    public synchronized void updateGame() {
        // If we have finished level 1, start level 2
        if (bricks == null || bricks.isEmpty()) {
            if (levelNumber == 1)
            {
                levelNumber = 2;
                initialiseLevel(2);
            } else {
                setGameRunning(false);
            }
        }

        // move the ball one step (the ball knows which direction it is moving in)
        ball.moveX(BALL_MOVE);
        ball.moveY(BALL_MOVE);
        // get the current ball position (top left corner)
        int x = ball.topX;
        int y = ball.topY;
        // Deal with possible edge of board hit
        if (x >= width - B - BALL_SIZE)
            ball.changeDirectionX();
        if (x <= 0 + B)
            ball.changeDirectionX();
        if (y >= height - B - BALL_SIZE) // Bottom
        {
            ball.changeDirectionY();
            addToScore(HIT_BOTTOM); // score penalty for hitting the bottom of the screen
        }
        if (y <= 0 + M)
            ball.changeDirectionY();

        // *[3]******************************************************[3]*
        // * Fill in code to check if a visible brick has been hit *
        // * The ball has no effect on an invisible brick *
        // * If a brick has been hit, change its 'visible' setting to *
        // * false so that it will 'disappear' *
        // **************************************************************

        // check whether ball has hit the bat
        if (ball.hitBy(bat)) {
            ball.changeDirectionY();
            // play sound *
        }

        for (GameObj brick : bricks) {
            if (brick.hitBy(ball) && brick.visible && brick.colour != Color.PINK) {

                addToScore(HIT_BRICK);
                ball.changeDirectionY();
                brick.colour = Color.PINK;
                break; // without break statement ball is going trough multiple bricks at once
            } else if (brick.hitBy(ball) && brick.visible) {
                addToScore(HIT_BRICK);
                ball.changeDirectionY();
                brick.visible = false;
                break; // so I added break statements to ensure that if ball hit brick it will change
                // direction
            }
        }
    }

    // This is how the Model talks to the View
    // Whenever the Model changes, this method calls the update method in
    // the View. It needs to run in the JavaFX event thread, and Platform.runLater
    // is a utility that makes sure this happens even if called from the
    // runGame thread

    public synchronized void modelChanged() {
        Platform.runLater(view::update);
    }

    // Methods for accessing and updating values
    // these are all synchronized so that the can be called by the main thread
    // or the animation thread safely

    // Change game running state - set to false to stop the game
    public synchronized void setGameRunning(Boolean value) {
        gameRunning = value;
    }

    // Return game running state
    public synchronized Boolean getGameRunning() {
        return gameRunning;
    }

    // Change game speed - false is normal speed, true is fast
    public synchronized void setFast(Boolean value) {
        fast = value;
    }

    // Return game speed - false is normal speed, true is fast
    public synchronized Boolean getFast() {
        return (fast);
    }

    // Return bat object
    public synchronized GameObj getBat() {
        return (bat);
    }

    // return ball object
    public synchronized GameObj getBall() {
        return (ball);
    }

    // return bricks
    public synchronized List<GameObj> getBricks() {
        return (bricks);
    }

    public synchronized void SetBrickNumber(int newNumber) {
        brickNumber = newNumber;
    }

    public synchronized void level1() {
        brickNumber = 1;
    }

    public synchronized void level2() {
        brickNumber = 2;
        view.level1 = false;
    }

    // return score
    public synchronized int getScore() {
        return (score);
    }

    // update the score
    public synchronized void addToScore(int n) {
        score += n;
    }

    // move the bat one step - -1 is left, +1 is right
    public synchronized void moveBat(int direction) {
        // Actual distance to move
        // Debug.trace( "Model::moveBat: Move bat = " + dist );

        // Deal with possible edge of board hit
        if ((bat.topX <= 0)) { // if bat hit x 0
            BAT_MOVE = 0; // will stop movement
            bat.topX = 1;
        } else if ((bat.topX + bat.width >= BoardWidth)) {
            BAT_MOVE = 0;
            bat.topX = BoardWidth - bat.width - 1;
        } else {
            BAT_MOVE = 8;
        }
        int dist = direction * BAT_MOVE; // go that direction and multiply it by bat_move if its zero then it doesnt
        // move..
        bat.moveX(dist); // move bat by distance
        int y = ball.topY;
        if (y >= height - B - BALL_SIZE) // Bottom
            Debug.trace("Model::moveBat: x pos =" + bat.topX);
    }
}