package com.gizmo385.browser;

import java.util.function.Optional;

import javafx.concurrent.Worker.State;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.web.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Browser extends Application {

    private ToolBar toolbar;
    private TextField locationBar;
    private Button homeButton, backButton, forwardButton;

    private static final int PREF_WIDTH = 1400, PREF_HEIGHT = 950;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("VimBrowser");

        WebBuffer b = new WebBuffer(PREF_WIDTH, PREF_HEIGHT, "http://www.google.com");

        primaryStage.setScene(new Scene(b, PREF_WIDTH, PREF_HEIGHT));
        primaryStage.show();
    }

    private static Button createButton(String filename) {
        int buttonSize = 24;

        Image image = new Image(ClassLoader.getSystemClassLoader().getResourceAsStream(filename));
        ImageView view = new ImageView(image);
        view.setFitWidth(buttonSize);
        view.setFitHeight(buttonSize);

        Button b = new Button();
        b.setPrefWidth(buttonSize);
        b.setPrefHeight(buttonSize);
        b.setGraphic(view);

        return b;
    }

    private class WebBuffer extends BorderPane {

        private WebView buffer;
        private WebEngine engine;
        private ToolBar navigation;
        private Button backButton, forwardButton, homeButton, refreshButton, menuButton;
        private TextField locationBar;

        public WebBuffer(int bufferWidth, int bufferHeight, String startUrl) {
            this.buffer = new WebView();
            this.engine = this.buffer.getEngine();
            this.engine.load(startUrl);

            // Create the toolbar
            this.locationBar = new TextField();
            this.locationBar.setPrefWidth(bufferWidth * .7);

            // Create the toolbar buttons
            this.backButton = createButton("images/backward.png");
            this.forwardButton = createButton("images/forward.png");
            this.refreshButton = createButton("images/refresh.png");
            this.homeButton = createButton("images/home.png");
            this.menuButton = createButton("images/menu.png");
            ToolBar toolbar = new ToolBar(backButton, forwardButton, refreshButton, homeButton,
                    locationBar, menuButton );

            // Add all the listeners
            addListeners();

            // Add items to the pane
            VBox topContainer = new VBox();
            topContainer.getChildren().add(toolbar);
            this.setTop(topContainer);
            this.setCenter(this.buffer);
        }

        public void resizeBuffer(int newBufferWidth, int newBufferHeight) {
            this.locationBar.setPrefWidth(newBufferWidth * .7);
        }

        private void addListeners() {
            // Listener for location bar to go a location
            this.locationBar.setOnAction(event -> {
                String location = locationBar.getCharacters().toString();

                if( !location.startsWith("http://") ) {
                    location = "http://" + location;
                }

                this.engine.load(location);
                this.locationBar.setText(location);
            });

            // Button actions
            this.homeButton.setOnAction(event -> engine.load("http://google.com"));
            this.refreshButton.setOnAction(event -> engine.reload());

            // Change the location bar width
            this.widthProperty().addListener((value, oldWidth, newWidth) -> {
                this.locationBar.setPrefWidth(newWidth.intValue() * .7);
            });

            // Handling key presses
            this.setOnKeyPressed(event -> {
                if(event.isControlDown()) {
                    KeyCode code = event.getCode();

                    if( code == KeyCode.L ) {
                        locationBar.requestFocus();
                    } else if( code == KeyCode.R ) {
                        engine.reload();
                    }
                }
            });


            // Handle load success and failure
            this.engine.getLoadWorker().stateProperty().addListener((value, oldState, newState) -> {
                String location = this.locationBar.getCharacters().toString();

                if( newState == State.SUCCEEDED ) {
                    locationBar.setText(engine.getLocation());
                } else if( newState == State.FAILED ) {
                    engine.load("https://www.google.com/search?q=" + location);
                }
            });
        }

        private void showDialog() {
            Optional<String> response = Dialogs.create()
                .owner(stage)
                .title("Text Input Dialog")
                .masthead("Look, a Text Input Dialog")
                .message("Please enter your name:")
                .showTextInput("walter");

            // One way to get the response value.
            if (response.isPresent()) {
                System.out.println("Your name: " + response.get());
            }
        }
    }
}
