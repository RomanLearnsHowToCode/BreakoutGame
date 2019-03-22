package breakout;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class Civ6MenuApp extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    
    private Model theModel;
    private View theView;
    
    private Stage theStage;
    private Stage theStage2;

    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("Single Player", () -> {theView.level1(theStage);theModel.startGame();}),
            new Pair<String, Runnable>("Introduction", () -> {theView.introduction(theStage2);}),
            new Pair<String, Runnable>("Credits", () -> {}),
            new Pair<String, Runnable>("Exit to Desktop", Platform::exit)
    );

    private Pane root = new Pane();
    private VBox menuBox = new VBox(-5);
    private Line line;

    private Parent createContent() {
        addBackground();
        addTitle();

        double lineX = WIDTH / 2 - 80;
        double lineY = HEIGHT / 3 + 50;

        addLine(lineX, lineY);
        addMenu(lineX + 5, lineY + 4);

        startAnimation();

        return root;
    }

    private void addBackground() {
        Image i = new Image(getClass().getResource("../MainMenu.png").toExternalForm());
        ImageView imageView = new ImageView(i);
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);

        root.getChildren().add(imageView);
    }

    private void addTitle() {
        Civ6Title title = new Civ6Title("Breakout game");
        title.setTranslateX(WIDTH / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3);

        root.getChildren().add(title);
    }

    private void addLine(double x, double y) {
        line = new Line(x, y, x, y + 150);
        line.setStrokeWidth(3);
        line.setStroke(Color.color(1, 1, 1, 0.75));
        line.setEffect(new DropShadow(5, Color.BLACK));
        line.setScaleY(0);

        root.getChildren().add(line);
    }

    private void startAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), line);
        st.setToY(1);
        st.setOnFinished(e -> {

            for (int i = 0; i < menuBox.getChildren().size(); i++) {
                Node n = menuBox.getChildren().get(i);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                tt.setToX(0);
                tt.setOnFinished(e2 -> n.setClip(null));
                tt.play();
            }
        });
        st.play();
    }

    private void addMenu(double x, double y) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);
        menuData.forEach(data -> {
            Civ6MenuItem item = new Civ6MenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        });

        root.getChildren().add(menuBox);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        
        theStage = primaryStage;
        primaryStage.setTitle("Breakout");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        int H = 800;         // Height of window pixels 
        int W = 600;         // Width  of window pixels 

        // set up debugging and print initial debugging message
        Debug.set(true);  //Turn on and turn off debugger (important for us to see if program is working as it should, also for setting breakpoints          
        Debug.trace("breakoutJavaFX starting"); 
        Debug.trace("Main::start"); 

        // Create the Model, View and Controller objects
        // Pretty simple to understand what is going on under the good.. so far it give some sense.. 
        // I just wonder how to add menu at the start of the application 
        Model model = new Model(W,H);
        View  view  = new View(W,H);
        Controller controller  = new Controller();
        
        theView = view;
        theModel = model;
        

        // Link them together so they can talk to each other
        // Each one has instance variables for the other two
        model.view = view;
        model.controller = controller;
        controller.model = model;
        controller.view = view;
        view.model = model;
        view.controller = controller;

        // start up the GUI (view), and then tell the model to initialise itself
        // and start the game running
        //view.level1(primaryStage); // renamed START to LEVEL1 for additional levels
        //model.startGame();

        // application is now running
        Debug.trace("breakoutJavaFX running"); 
    }

    public static void main(String[] args) {
        launch(args);
    }
}