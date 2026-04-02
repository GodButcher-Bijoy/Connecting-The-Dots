package org.example;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SelectionScene {

    public static Parent createView(Runnable onStandard, Runnable onPolar, Runnable onLibrary) {
        VBox root = new VBox(50);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #050505; -fx-padding: 50;"); // Pitch black background

        // =========================================================
        // 1. Dark Wrapper Block with a Strong Outline Shadow
        // =========================================================
        VBox plotYourselfBlock = new VBox();
        plotYourselfBlock.setAlignment(Pos.CENTER);

        plotYourselfBlock.maxWidthProperty().bind(root.widthProperty().multiply(0.8));
        plotYourselfBlock.spacingProperty().bind(root.heightProperty().multiply(0.05));
        plotYourselfBlock.paddingProperty().bind(javafx.beans.binding.Bindings.createObjectBinding(
                () -> new javafx.geometry.Insets(root.getHeight() * 0.05), root.heightProperty()
        ));

        // Pitch black inside, but with a subtle border so the edges are sharp
        plotYourselfBlock.setStyle(
                "-fx-background-color: #050505; " + // Matches the background
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: #2A2A2A; " + // Subtle dark grey border
                        "-fx-border-radius: 12; " +
                        "-fx-border-width: 2;"
        );

        // Strong, clearly visible grey shadow to separate it from the background
        DropShadow blockShadow = new DropShadow();
        blockShadow.setColor(Color.web("#555555", 0.6)); // Light grey shadow for clear visibility
        blockShadow.setRadius(60); // Wide blur
        blockShadow.setSpread(0.1); // Slightly dense so it's clearly visible
        blockShadow.setOffsetY(0); // Centered glow
        plotYourselfBlock.setEffect(blockShadow);

        // --- Title ---
        Label title = new Label("PLOT YOURSELF");
        title.setStyle(
                "-fx-text-fill: #E0E0E0; " +
                        "-fx-font-size: 38px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-letter-spacing: 6px;"
        );

        // --- Standard & Polar Buttons ---
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER);
        topBox.spacingProperty().bind(plotYourselfBlock.widthProperty().multiply(0.08));

        // Cyberpunk Cyan (Option 2)
        Button btnStandard = new Button("STANDARD");
        btnStandard.prefWidthProperty().bind(root.widthProperty().multiply(0.32));
        btnStandard.prefHeightProperty().bind(root.heightProperty().multiply(0.28));
        btnStandard.setOnAction(e -> onStandard.run());
        setupTintedGlassButton(btnStandard, "#00FFFF", 32);

        // Cyberpunk Red/Pink (Option 2)
        Button btnPolar = new Button("POLAR");
        btnPolar.prefWidthProperty().bind(root.widthProperty().multiply(0.32));
        btnPolar.prefHeightProperty().bind(root.heightProperty().multiply(0.28));
        btnPolar.setOnAction(e -> onPolar.run());
        setupTintedGlassButton(btnPolar, "#FF003C", 32);

        topBox.getChildren().addAll(btnStandard, btnPolar);
        plotYourselfBlock.getChildren().addAll(title, topBox);


        // =========================================================
        // 2. Bottom Section: Library Button (Option 2)
        // =========================================================
        Button btnLibrary = new Button("EXPERIENCE SOME CURVES");
        btnLibrary.maxWidthProperty().bind(plotYourselfBlock.maxWidthProperty());
        btnLibrary.prefHeightProperty().bind(root.heightProperty().multiply(0.12));
        btnLibrary.setOnAction(e -> onLibrary.run());

        // Electric Purple (Option 2)
        setupTintedGlassButton(btnLibrary, "#B026FF", 26);

        root.getChildren().addAll(plotYourselfBlock, btnLibrary);

        return root;
    }

    // =========================================================================
    // OPTION 2 LOGIC: TINTED GLASS WITH NEON TEXT (Inverts on click)
    // =========================================================================
    private static void setupTintedGlassButton(Button btn, String neonHex, int fontSize) {
        String normalStyle =
                "-fx-font-size: " + fontSize + "px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-letter-spacing: 2px; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: " + neonHex + "; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 3; " +
                        "-fx-background-color: #080808; " + // Pitch dark inside
                        "-fx-text-fill: " + neonHex + "; " + // Neon text
                        "-fx-text-alignment: center;";

        String hoverStyle = normalStyle.replace("#080808", "#151515"); // Slightly lighter inside when hovering

        String pressedStyle =
                "-fx-font-size: " + fontSize + "px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-letter-spacing: 2px; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: " + neonHex + "; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 3; " +
                        "-fx-background-color: " + neonHex + "; " + // Fills with solid neon color!
                        "-fx-text-fill: #000000; " +                // Text goes pitch black!
                        "-fx-text-alignment: center;";

        btn.setStyle(normalStyle);

        DropShadow neonGlow = new DropShadow();
        neonGlow.setColor(Color.web(neonHex, 0.5));
        neonGlow.setRadius(20);
        neonGlow.setSpread(0.1);
        btn.setEffect(neonGlow);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), btn);
        scaleUp.setToX(1.05); scaleUp.setToY(1.05);

        ScaleTransition scaleNormal = new ScaleTransition(Duration.millis(200), btn);
        scaleNormal.setToX(1.0); scaleNormal.setToY(1.0);

        ScaleTransition clickDown = new ScaleTransition(Duration.millis(100), btn);
        clickDown.setToX(0.98); clickDown.setToY(0.98);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            neonGlow.setColor(Color.web(neonHex, 0.8));
            neonGlow.setRadius(40);
            neonGlow.setSpread(0.3);
            scaleUp.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(normalStyle);
            neonGlow.setColor(Color.web(neonHex, 0.5));
            neonGlow.setRadius(20);
            neonGlow.setSpread(0.1);
            scaleNormal.playFromStart();
        });

        btn.setOnMousePressed(e -> {
            btn.setStyle(pressedStyle);
            neonGlow.setColor(Color.BLACK); // Shadow drops out to make color pop
            neonGlow.setRadius(10);
            neonGlow.setOffsetY(5);
            clickDown.playFromStart();
        });

        btn.setOnMouseReleased(e -> {
            btn.setStyle(hoverStyle);
            neonGlow.setColor(Color.web(neonHex, 0.8));
            neonGlow.setRadius(40);
            neonGlow.setOffsetY(0);
            scaleUp.playFromStart();
        });
    }
}