package com.gizmo385.browser;

import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.web.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.ArrayList;

public class BrowserTab extends Tab {

    private SplitPane buffers;
    private List<WebBuffer> horizontalSplits;
    private List<WebBuffer> verticalSplits;

    public BrowserTab(String homepage, int paneWidth, int paneHeight) {
        this.homepage = homepage;
        this.paneWidth = paneWidth;
        this.paneHeight = paneHeight;

        // TODO: Create initial WebBuffer
    }

    public void addHorizontalSplit(String destination) {
        // TODO: Figure out how splits should be rendered
    }

    public void addVerticalSplit(String destination) {
        // TODO: Figure out how splits should be rendered
    }


}
