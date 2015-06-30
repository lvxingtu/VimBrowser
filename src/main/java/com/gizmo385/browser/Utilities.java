package com.gizmo385.browser;

import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.web.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Utilities {

    public static final int DEFAULT_BUTTON_SIZE = 24;

    public static Button createButton(String filename) {
        return createButton(filename, DEFAULT_BUTTON_SIZE);
    }

    public static Button createButton(String filename, int buttonSize) {
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
}
