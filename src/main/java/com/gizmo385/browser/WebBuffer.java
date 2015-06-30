package com.gizmo385.browser;

import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.web.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class WebBuffer extends BorderPane {

    private WebView webView;
    private WebEngine engine;
    private ToolBar navigation;
    private Button backButton, forwardButton, homeButton, refreshButton, menuButton,
            horizontalSplitButton, verticalSplitButton, exitButton;
    private TextField locationBar;

    private String homepage;
    private int bufferWidth, bufferHeight;

    public WebBuffer(String homepage, int bufferWidth, int bufferHeight) {
        this.homepage = homepage;
        this.bufferWidth = bufferWidth;
        this.bufferHeight = bufferHeight;

        initComponents();
        initListeners();
        addComponents();

        gotoUrl(this.homepage);
    }

    private void initComponents() {
        // Create the web view
        this.webView = new WebView();
        this.engine = this.webView.getEngine();

        // The location bar
        this.locationBar = new TextField();
        this.locationBar.autosize();

        // Toolbar buttons
        this.backButton             = Utilities.createButton("images/backward.png");
        this.forwardButton          = Utilities.createButton("images/forward.png");
        this.refreshButton          = Utilities.createButton("images/refresh.png");
        this.homeButton             = Utilities.createButton("images/home.png");
        this.horizontalSplitButton  = Utilities.createButton("images/horiz_split.png");
        this.verticalSplitButton    = Utilities.createButton("images/vert_split.png");
        this.menuButton             = Utilities.createButton("images/menu.png");
        this.exitButton             = Utilities.createButton("images/exit.png");

        // Create and populate the toolbar
        this.navigation = new ToolBar(backButton, forwardButton, refreshButton, homeButton,
                locationBar, horizontalSplitButton, verticalSplitButton, exitButton,
                menuButton);
    }

    private void initListeners() {
        // Listener for location bar to go a location
        this.locationBar.setOnAction(event -> {
            String location = locationBar.getCharacters().toString();

            //this.engine.load(location);
            gotoUrl(location);
            this.locationBar.setText(location);
        });

        this.homeButton.setOnAction(event -> gotoUrl(this.homepage));
        this.refreshButton.setOnAction(event -> engine.reload());
        this.backButton.setOnAction(event -> moveInHistory(-1));
        this.forwardButton.setOnAction(event -> moveInHistory(1));

        // Pressing escape in the location bar will reset the URL to be your current page
        this.locationBar.setOnKeyPressed(event -> {
            if( event.getCode() == KeyCode.ESCAPE ) {
                locationBar.setText(engine.getLocation());
            }
        });

        // Keyboard shortcuts
        this.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            if( event.isControlDown() ) {
                switch(code) {
                    case L:
                        this.locationBar.requestFocus();
                        break;
                    case R:
                        this.engine.reload();
                        break;
                }
            } else if( event.isAltDown() ) {
                switch(code) {
                    case LEFT:
                        moveInHistory(-1);
                        break;
                    case RIGHT:
                        moveInHistory(1);
                        break;
                }
            }
        });
    }

    private void addComponents() {
        this.setTop(this.navigation);
        this.setCenter(this.webView);
    }

    private void moveInHistory(int offset) {
        try {
            engine.getHistory().go(offset);
            String location = locationBar.getCharacters().toString();
            locationBar.setText(location);
        } catch( IndexOutOfBoundsException ioobe ) { }
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
}
