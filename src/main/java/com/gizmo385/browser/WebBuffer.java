package com.gizmo385.browser;

import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.web.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class WebBuffer extends BorderPane {

    // Rendering components
    private WebView webView;
    private WebEngine engine;

    // Components in the buffer
    private ToolBar navigation;
    private Button backButton, forwardButton, homeButton, refreshButton, menuButton,
            horizontalSplitButton, verticalSplitButton, exitButton;
    private TextField locationBar;

    // Settings
    private String homepage;
    private int bufferWidth, bufferHeight;
    private BrowserTab tab;
    private WebBuffer sourceBuffer;

    /**
     * Creates a root web buffer which was not originally split from another buffer.
     */
    public WebBuffer(String homepage, int bufferWidth, int bufferHeight, BrowserTab tab) {
        this(homepage, bufferWidth, bufferHeight, tab, null);
    }

    /**
     * Creates a new web buffer, which represents a single web page open in a tab.
     *
     * @param homepage The page that this buffer should open to
     * @param bufferWidth The width of this buffer in the tab
     * @param bufferHeight The height of this buffer in the tab
     * @param tab The tab that contains this buffer
     * @param sourceBuffer The buffer that this buffer was split from. The root buffer should be
     * null.
     */
    public WebBuffer(String homepage, int bufferWidth, int bufferHeight, BrowserTab tab, WebBuffer
            sourceBuffer) {
        this.homepage = homepage;
        this.bufferWidth = bufferWidth;
        this.bufferHeight = bufferHeight;
        this.tab = tab;
        this.sourceBuffer = sourceBuffer;

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

        // Add the listeners for the navigation buttons
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

    public void horizontalSplit() {
        this.tab.addSplit(this, SplitType.HORIZONTAL);
    }

    public void verticalSplit() {
        this.tab.addSplit(this, SplitType.VERTICAL);
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getHomepage() {
        return this.homepage;
    }

    public void setBufferHeight(int height) {
        this.bufferHeight = height;
    }

    public int getBufferHeight() {
        return this.bufferHeight;
    }

    public void setBufferWidth(int width) {
        this.bufferWidth = width;
    }

    public int getBufferWidth() {
        return this.bufferWidth;
    }
}
