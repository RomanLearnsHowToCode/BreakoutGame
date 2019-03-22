package breakout;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.*;
import javafx.scene.web.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import java.io.*;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.paint.*;



// The View class creates and manages the GUI for the application.
// It doesn't know anything about the game itself, it just displays
// the current state of the Model, and handles user input
public class View implements EventHandler<KeyEvent>
{
    // variables for components of the user interface
    public int width;       // width of window
    public int height;      // height of window

    // user interface objects
    public Pane pane;       // basic layout pane
    public Canvas canvas;   // canvas to draw game on
    public Label infoText;  // info at top of screen

    // The other parts of the model-view-controller setup
    public Controller controller;
    public Model model;

    public GameObj   bat;            // The bat
    public GameObj   ball;           // The ball
    public List<GameObj> bricks;     // The bricks // which are not displaying
    public int       score =  0;     // The score
    public boolean visible  = true;

    public boolean level1 = true;
    public Image background = new Image(getClass().getResource("../Level1.png").toExternalForm());
    public Image background2 = new Image(getClass().getResource("../Level2.png").toExternalForm());

    // we don't really need a constructor method, but include one to print a
    // debugging message if required
    public View(int w, int h)
    {
        Debug.trace("View::<constructor>");
        width = w;
        height = h;
    }

    // start is called from Main, to start the GUI up
    // Note that it is important not to create controls etc here and
    // not in the constructor (or as initialisations to instance variables),
    // because we need things to be initialised in the right order
    public void level1(Stage window) // renamed START as LEVEL1 for additional levels
    {
        // breakout is basically one big drawing canvas, and all the objects are
        // drawn on it as rectangles, except for the text at the top - this
        // is a label which sits 'on top of' the canvas.

        pane = new Pane();       // a simple layout pane
        pane.setId("Breakout");  // Id to use in CSS file to style the pane if needed

        // canvas object - we set the width and height here (from the constructor),
        // and the pane and window set themselves up to be big enough
        canvas = new Canvas(width,height);
        pane.getChildren().add(canvas);     // add the canvas to the pane

        // infoText box for the score - a label which we position on
        //the canvas with translations in X and Y coordinates
        infoText = new Label("BreakOut: Score = " + score);
        infoText.setTranslateX(50);
        infoText.setTranslateY(10);
        pane.getChildren().add(infoText);  // add label to the pane

        // add the complete GUI to the scene
        Scene scene = new Scene(pane);
        scene.getStylesheets().add("breakout.css"); // tell the app to use our css file



        // Add an event handler for key presses. We use the View object itself
        // and provide a handle method to be called when a key is pressed.
        scene.setOnKeyPressed(this);


        // put the scene in the window and display it
        window.setScene(scene);
        window.show();
    }

    public void introduction(Stage window2)
    {
        if(window2 == null) {
            System.out.println("Window 2 is null");
            return;
        }
        pane = new Pane();
        pane.setId("Introduction");


        canvas = new Canvas(width, height);
        pane.getChildren().add(canvas);

        // infoText box for the score - a label which we position on
        //the canvas with translations in X and Y coordinates
       /* infoText = new Label("BreakOut: Score = " + score);
        infoText.setTranslateX(50);
        infoText.setTranslateY(10);
        pane.getChildren().add(infoText);  // add label to the pane*/

        // add the complete GUI to the scene
        Scene scene = new Scene(pane);


        window2.setScene(scene);
        window2.show();
    }

    /*private void addBackground() {
        ImageView imageView = new ImageView(new Image(getClass().getResource("../../res/clouds.png").toExternalForm()));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        pane.getChildren().add(imageView);
    }*/

    // Event handler for key presses - it just passes the event to the controller
    public void handle(KeyEvent event)
    {
        // send the event to the controller
        controller.userKeyInteraction( event );
    }


    // drawing the game
    public void drawPicture()
    {
        // the ball movement is running 'i the background' so we have
        // add the following line to make sure
        synchronized( Model.class )   // Make thread safe (because the ball
        {


            GraphicsContext gc = canvas.getGraphicsContext2D();

            // clear the canvas to redraw
            //gc.setFill( Color.WHITE );

            //if level 1 use background, if level 2 use background2
            if (level1) {
                gc.drawImage(background, 0, 0, width, height);
            }else {
                gc.drawImage(background2, 0, 0, width, height);
            }

            // gc.fillRect( 0, 0, width, height );

            //ImageView imageView = new ImageView(new Image(getClass().getResource("../../res/clouds.png").toExternalForm()));

            // update score
            infoText.setText("Score = " + score);
            /*infoText2.setText("Welcome");*/

            // update GameObj
            displayGameObj( gc, ball );   // Display the Ball
            displayGameObj( gc, bat  );   // Display the Bat

            for (GameObj brick : bricks)
            {
                if (bricks != null)
                {
                    displayGameObj ( gc, brick );
                }
            }


        }
        // *[3]****************************************************[3]*
        // * Display the bricks that make up the game                 *
        // * Fill in code to display bricks from the ArrayList        *
        // * Remember only a visible brick is to be displayed         *
        // ************************************************************
        // DONE
           /*if (brick.hitBy(ball))
        	   brick.visible = false;
        }*/
    }

    // Display a game object - it is just a rectangle on the canvas
    public void displayGameObj( GraphicsContext gc, GameObj go )
    {
        gc.setFill( go.colour );
        gc.fillRect( go.topX, go.topY, go.width, go.height );

        /*super.paint();
        ImageIcon img = new ImageIcon(this.getClass().getResource(bg));
        Image image = img.getImage();
        drawImage(image, 0, 0, null);*/



    }



    // This is how the Model talks to the View
    // This method gets called BY THE MODEL, whenever the model changes
    // It has to do whatever is required to update the GUI to show the new model status

    public void update()
    {
        // Get from the model the ball, bat, bricks & score
        ball    = model.getBall();              // Ball
        bricks  = model.getBricks();            // Bricks
        bat     = model.getBat();               // Bat
        score   = model.getScore();             // Score


        for(GameObj brick : bricks){
            if(brick.hitBy(ball) && brick.visible == false){
                {
                    bricks.remove(brick);
                    break;
                }
            }
        }




        //Debug.trace("Update");
        drawPicture();                     // Re-draw game
    }
}