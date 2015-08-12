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

    private FlowPane flowPane;
    private List<WebBuffer> buffers;

    private String homepage;
    private int tabWidth, tabHeight;

    public BrowserTab(String homepage, int tabWidth, int tabHeight) {
        this.homepage = homepage;
        this.tabWidth = tabWidth;
        this.tabHeight = tabHeight;

        // TODO: Create initial WebBuffer
    }

    public void addSplit(WebBuffer source, SplitType splitType) {
        // Get information about the source web buffer
        int width = source.getBufferWidth();
        int height = source.getBufferHeight();
        String homepage = source.getHomepage();

        if(splitType == SplitType.HORIZONTAL) {
            height /= 2;
        } else {
            width /= 2;
        }

        // Create the new buffer and resize the old buffer
        source.setBufferHeight(height);
        source.setBufferWidth(width);
        WebBuffer newBuffer = new WebBuffer(homepage, width, height, this, null);

        buffers.add(newBuffer);
        flowPane.getChildren().add(newBuffer);
    }
}
