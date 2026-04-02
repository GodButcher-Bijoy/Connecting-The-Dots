package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class SelectionScene {

    public static Parent createView(Runnable onStandard, Runnable onPolar, Runnable onLibrary) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #222222; -fx-padding: 30;");

        // =========================================================
        // NEW: Transparent Block wrapping Title and the Two Buttons
        // =========================================================
        VBox plotYourselfBlock = new VBox(20);
        plotYourselfBlock.setAlignment(Pos.CENTER);
        VBox.setVgrow(plotYourselfBlock, Priority.ALWAYS); // Takes up remaining vertical space

        // Semi-transparent background with a subtle border (Glassmorphism effect)
        plotYourselfBlock.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.08); " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                        "-fx-border-radius: 20; " +
                        "-fx-border-width: 2; " +
                        "-fx-padding: 30;"
        );

        // --- Title ---
        Label title = new Label("PLOT YOURSELF");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 40px; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        // --- Standard & Polar Buttons (Inside the block) ---
        HBox topBox = new HBox(20);
        topBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(topBox, Priority.ALWAYS); // Forces the HBox to expand inside the transparent block
        // Get the direct URL from the resources folder
        String standardImg = SelectionScene.class.getResource("/sine_wave_icon_square.png").toExternalForm();
        String polarImg = SelectionScene.class.getResource("/polar_flower_icon_square.png").toExternalForm();

        Button btnStandard = createModeButton("Standard", standardImg, onStandard);
        Button btnPolar = createModeButton("Polar", polarImg, onPolar);

        topBox.getChildren().addAll(btnStandard, btnPolar);

        // Add title and the HBox to our transparent block
        plotYourselfBlock.getChildren().addAll(title, topBox);


        // =========================================================
        // Bottom 30/40% Section: Library Button (Unchanged)
        // =========================================================
        Button btnLibrary = new Button("Experience Some curves");
        btnLibrary.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnLibrary.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-background-color: #4B0082; -fx-text-fill: white; -fx-background-radius: 15;");
        btnLibrary.setOnAction(e -> onLibrary.run());

        // Force the library button to take exactly 35% of the total height
        btnLibrary.prefHeightProperty().bind(root.heightProperty().multiply(0.35));

        // Add the block and the library button to the main screen
        root.getChildren().addAll(plotYourselfBlock, btnLibrary);

        return root;
    }

    private static Button createModeButton(String text, String imgPath, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(btn, Priority.ALWAYS); // Take 50% width each

        // Setup CSS for background image fallback and styling
        String style = "-fx-font-size: 35px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 15; " +
                "-fx-background-color: #444444; " +
                "-fx-background-image: url('" + imgPath + "'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center center;";

        btn.setStyle(style);
        btn.setOnAction(e -> action.run());

        // Simple hover effect
        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }
}