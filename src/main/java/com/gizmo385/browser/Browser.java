package com.gizmo385.browser;

import java.util.Optional;

import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import javafx.geometry.Orientation;
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

    private static final int PREF_WIDTH = 1400, PREF_HEIGHT = 950;
    private TabPane pane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("VimBrowser");

        this.pane = new TabPane();
        this.pane.setPrefWidth(PREF_WIDTH);
        this.pane.setPrefHeight(PREF_HEIGHT);

        // Listen to see if the last tab has closed. If it has, close the window
        ListChangeListener<Tab> lastTabClosedListener = c -> {
            if(this.pane.getTabs().isEmpty()) {
                System.exit(0);
            }
        };
        this.pane.getTabs().addListener(lastTabClosedListener);
        this.pane.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if(event.isControlDown() && code == KeyCode.T) {
                newTab();
            }
        });

        // Set up the initial tab
        Tab initialTab = new Tab();
        WebBuffer initialBuffer = new WebBuffer(null, initialTab, PREF_WIDTH, PREF_HEIGHT, "http://www.google.com");
        initialTab.setContent(initialBuffer);
        initialTab.setText("Tab");
        this.pane.getTabs().add(initialTab);

        // Set the stage
        primaryStage.setScene(new Scene(this.pane, PREF_WIDTH, PREF_HEIGHT));
        Image spider = new Image(ClassLoader.getSystemClassLoader().getResourceAsStream("images/spider.png"));
        primaryStage.getIcons().add(spider);
        primaryStage.show();
    }

    public void newTab() {
        Tab initialTab = new Tab();
        WebBuffer initialBuffer = new WebBuffer(null, initialTab, PREF_WIDTH, PREF_HEIGHT, "http://www.google.com");
        initialTab.setContent(initialBuffer);
        initialTab.setText("Tab");
        this.pane.getTabs().add(initialTab);
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

    private class WebBuffer extends SplitPane {

        private WebView buffer;
        private WebEngine engine;
        private ToolBar navigation;
        private Button backButton, forwardButton, homeButton, refreshButton, menuButton,
                horizontalSplitButton, verticalSplitButton, exitButton;
        private TextField locationBar;
        private SplitPane container;
        private Tab tab;

        private String homepage;
        private int bufferWidth, bufferHeight;

        public WebBuffer(SplitPane container, Tab tab, int bufferWidth, int bufferHeight, String homepage) {
            this.container = container;
            this.tab = tab;
            this.bufferWidth = bufferWidth;
            this.bufferHeight = bufferHeight;
            this.homepage = homepage;
            this.buffer = new WebView();
            this.engine = this.buffer.getEngine();
            gotoUrl(homepage);

            // Create the toolbar
            this.locationBar = new TextField();
            this.locationBar.autosize();
            this.locationBar.setText(this.homepage);

            // Create the toolbar buttons
            this.backButton = createButton("images/backward.png");
            this.forwardButton = createButton("images/forward.png");
            this.refreshButton = createButton("images/refresh.png");
            this.homeButton = createButton("images/home.png");
            this.horizontalSplitButton = createButton("images/horiz_split.png");
            this.verticalSplitButton = createButton("images/vert_split.png");
            this.menuButton = createButton("images/menu.png");
            this.exitButton = createButton("images/exit.png");
            ToolBar toolbar = new ToolBar(backButton, forwardButton, refreshButton, homeButton,
                    locationBar, horizontalSplitButton, verticalSplitButton, exitButton,
                    menuButton);

            // Add all the listeners
            addListeners();

            // Add items to the pane
            BorderPane borderPane = new BorderPane();
            VBox topContainer = new VBox();
            topContainer.getChildren().add(toolbar);
            borderPane.setTop(topContainer);
            borderPane.setCenter(this.buffer);

            // Add the initial content pane
            this.getItems().add(borderPane);
        }

        public void closeTab() {
            Browser.this.pane.getTabs().remove(this.tab);
        }

        public void gotoUrl(String url) {
            if( url.matches(".+\\.\\w+.*") ) {
                if( ! url.startsWith("http://") ) {
                    url = "http://" + url;
                }

                engine.load(url);
            } else {
                engine.load("https://www.google.com/search?q=" + url);
            }
        }

        public void resizeBuffer() {
            this.locationBar.autosize();
        }

        private void addSplit(WebBuffer buffer) {
            this.getItems().add(buffer);
            this.setResizableWithParent(buffer, false);
            this.locationBar.setText(engine.getLocation());
            resizeBuffer();
        }

        private void addHorizontalSplit() {
            WebBuffer buffer = new WebBuffer(this, tab, this.bufferWidth, this.bufferHeight,
                    locationBar.getCharacters().toString());
            buffer.setOrientation(Orientation.VERTICAL);
            this.setPrefHeight(this.getPrefHeight() / 2);
            this.setOrientation(Orientation.VERTICAL);

            addSplit(buffer);
        }

        private void addVerticalSplit() {
            WebBuffer buffer = new WebBuffer(this, tab, this.bufferWidth, this.bufferHeight,
                    locationBar.getCharacters().toString());
            buffer.setOrientation(Orientation.HORIZONTAL);
            this.setPrefHeight(this.getPrefHeight() / 2);
            this.setOrientation(Orientation.HORIZONTAL);

            addSplit(buffer);
        }

        private void removeSplit(WebBuffer buffer) {
            this.getItems().remove(buffer);
        }

        private void addListeners() {
            // Listener for location bar to go a location
            this.locationBar.setOnAction(event -> {
                String location = locationBar.getCharacters().toString();

                //this.engine.load(location);
                gotoUrl(location);
                this.locationBar.setText(location);
            });

            this.widthProperty().addListener((value, oldWidth, newWidth) -> resizeBuffer());
            this.heightProperty().addListener((value, oldHeight, newHeight) -> resizeBuffer());

            // Button actions
            this.homeButton.setOnAction(event -> gotoUrl(this.homepage));
            this.refreshButton.setOnAction(event -> engine.reload());
            this.exitButton.setOnAction(event -> {
                if( this.container != null ) {
                    this.container.getItems().remove(this);
                }
            });

            this.backButton.setOnAction(event -> {
                try {
                    engine.getHistory().go(-1);
                    String location = locationBar.getCharacters().toString();
                    locationBar.setText(location);
                } catch( IndexOutOfBoundsException ioobe ) { }
            });

            this.forwardButton.setOnAction(event -> {
                try {
                    engine.getHistory().go(1);
                    String location = locationBar.getCharacters().toString();
                    locationBar.setText(location);
                } catch( IndexOutOfBoundsException ioobe ) { }
            });

            // Splitting buttons
            this.horizontalSplitButton.setOnAction(event ->  addHorizontalSplit());
            this.verticalSplitButton.setOnAction(event ->  addVerticalSplit());


            // Change the location bar width
            this.widthProperty().addListener((value, oldWidth, newWidth) -> {
                this.locationBar.setPrefWidth(newWidth.intValue() * .7);
            });
            this.locationBar.setOnKeyPressed(event -> {
                if( event.getCode() == KeyCode.ESCAPE ) {
                    locationBar.setText(engine.getLocation());
                }
            });

            // Handling key presses
            this.setOnKeyPressed(event -> {
                if(event.isControlDown()) {
                    KeyCode code = event.getCode();

                    if( code == KeyCode.L ) {
                        locationBar.requestFocus();
                    } else if( code == KeyCode.R ) {
                        engine.reload();
                    } else if( code == KeyCode.W ) {
                        if( event.isShiftDown() ) {
                            System.exit(0);
                        } else {
                            closeTab();
                        }
                    } else if( code == KeyCode.E ) {
                        addHorizontalSplit();
                    } else if( code == KeyCode.O ) {
                        addVerticalSplit();
                    }
                }
            });


            // Handle load success and failure
            this.engine.getLoadWorker().stateProperty().addListener((value, oldState, newState) -> {
                String location = this.locationBar.getCharacters().toString();

                if( newState == State.SUCCEEDED ) {
                    locationBar.setText(engine.getLocation());
                } else if( newState == State.FAILED ) {
                    // TODO: Alert failure
                }
            });
        }
    }
}
