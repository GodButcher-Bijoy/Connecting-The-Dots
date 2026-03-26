package org.example;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class IntroScene {

    private static final String APP_NAME = "GRAPHIFY";

    public static Scene create(Stage stage, Runnable onComplete) {

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:#4B5563;");

        Group content3D = new Group();
        // Set height to 700
        SubScene subScene = new SubScene(content3D, 1000, 700, true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(root.widthProperty());
        subScene.heightProperty().bind(root.heightProperty());

        // --- 1. Centered Ground (Middle of the screen) ---
        // 700 / 2 = 350 is the middle
        double groundLevel = 350;
        Rectangle ground = new Rectangle(4000, 1500);
        ground.setTranslateX(-2000);
        ground.setTranslateY(groundLevel);

        LinearGradient groundGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#CCCCCC", 0.3)),
                new Stop(1, Color.web("#000000", 0.0))
        );
        ground.setFill(groundGradient);

        // --- 2. Centered Grid ---
        Group graphGrid = new Group();
        graphGrid.setOpacity(0.0);
        graphGrid.setTranslateZ(-1);

        // Vertical lines
        for (int i = -2000; i <= 2000; i += 60) {
            Rectangle vLine = new Rectangle(2, 1500, Color.web("#FFFFFF", 0.15));
            vLine.setTranslateX(i);
            vLine.setTranslateY(groundLevel);
            graphGrid.getChildren().add(vLine);
        }

        // Horizontal lines starting from middle and going down
        for (int i = 0; i <= 1500; i += 60) {
            Rectangle hLine = new Rectangle(4000, 2, Color.web("#FFFFFF", 0.15));
            hLine.setTranslateX(-2000);
            hLine.setTranslateY(groundLevel + i);
            graphGrid.getChildren().add(hLine);
        }

        Rectangle floorLine = new Rectangle(4000, 2);
        floorLine.setTranslateX(-2000);
        floorLine.setTranslateY(groundLevel);
        floorLine.setFill(Color.web("#FFFFFF", 0.5));

        // --- 3. Cube Settings (Adjusted for middle) ---
        double cubeSize = 70;
        double startX = -350;
        double restY = groundLevel - (cubeSize / 2.0);

        Group cubeGroup = createCustomColoredCube(cubeSize);
        cubeGroup.setTranslateX(startX);
        cubeGroup.setTranslateY(-600);
        cubeGroup.setRotationAxis(Rotate.Z_AXIS);

        // --- 4. Text Setup (Positioned relative to middle) ---
        HBox textContainer = new HBox(10);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.setMaxWidth(800);
        // Positioned slightly above the middle floor
        textContainer.setTranslateY(groundLevel - 440);
        textContainer.setTranslateX(50);

        List<Label> letters = new ArrayList<>();
        for (char c : APP_NAME.toCharArray()) {
            Label l = new Label(String.valueOf(c));
            l.setFont(Font.font("Montserrat", FontWeight.BOLD, 85));
            l.setTextFill(Color.WHITE);
            l.setOpacity(0.0);
            letters.add(l);
            textContainer.getChildren().add(l);
        }

        content3D.getChildren().addAll(ground, graphGrid, floorLine, cubeGroup);
        root.getChildren().addAll(subScene, textContainer);

        // --- 5. Camera (Higher Y to look at the middle) ---
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        camera.setTranslateZ(-1200);
        camera.setTranslateY(-50); // Adjusted camera height
        camera.getTransforms().addAll(
                new Rotate(-10, Rotate.X_AXIS), // Flatter angle to see the grid better
                new Rotate(0, Rotate.Y_AXIS)
        );
        subScene.setCamera(camera);

        content3D.getChildren().add(new AmbientLight(Color.web("#FFFFFF", 0.8)));

        // --- 6. Animation ---
        TranslateTransition drop = new TranslateTransition(Duration.seconds(0.8), cubeGroup);
        drop.setToY(restY);
        drop.setInterpolator(Interpolator.EASE_IN);

        TranslateTransition bounce = new TranslateTransition(Duration.seconds(0.2), cubeGroup);
        bounce.setByY(-30);
        bounce.setCycleCount(2);
        bounce.setAutoReverse(true);

        SequentialTransition rollingSequence = new SequentialTransition();
        double currentX = startX;
        double currentAngle = 0;
        int totalRolls = letters.size() + 15;

        for (int i = 0; i < totalRolls; i++) {
            final double cX = currentX;
            final double cA = currentAngle;

            Transition rollStep = new Transition() {
                {
                    setCycleDuration(Duration.seconds(0.4));
                    setInterpolator(Interpolator.LINEAR);
                }
                @Override
                protected void interpolate(double frac) {
                    double theta = frac * 90;
                    double rad = Math.toRadians(theta);
                    double r = cubeSize / 2.0;
                    double px = cX + r;
                    double py = restY + r;
                    double nx = px - r * Math.cos(rad) + r * Math.sin(rad);
                    double ny = py - r * Math.sin(rad) - r * Math.cos(rad);
                    cubeGroup.setTranslateX(nx);
                    cubeGroup.setTranslateY(ny);
                    cubeGroup.setRotate(cA + theta);
                }
            };

            if (i < letters.size()) {
                FadeTransition reveal = new FadeTransition(Duration.seconds(0.4), letters.get(i));
                reveal.setToValue(1.0);
                ParallelTransition sync = new ParallelTransition(rollStep, reveal);
                rollingSequence.getChildren().add(sync);
            } else {
                rollingSequence.getChildren().add(rollStep);
            }
            currentX += cubeSize;
            currentAngle += 90;
        }

        FadeTransition showGraphPaper = new FadeTransition(Duration.seconds(1.5), graphGrid);
        showGraphPaper.setToValue(1.0);

        SequentialTransition masterSequence = new SequentialTransition(
                drop, bounce, new PauseTransition(Duration.seconds(0.2)),
                rollingSequence, showGraphPaper
        );

        masterSequence.setOnFinished(e -> { if(onComplete != null) onComplete.run(); });
        masterSequence.play();

        return new Scene(root, 1000, 700, true);
    }

    private static Group createCustomColoredCube(double size) {
        Group group = new Group();
        double r = size / 2.0;
        PhongMaterial sideMat = new PhongMaterial(Color.BLACK);
        PhongMaterial frontMat = new PhongMaterial(Color.web("#4169E1"));
        Box front = new Box(size, size, 1); front.setTranslateZ(-r); front.setMaterial(frontMat);
        Box back = new Box(size, size, 1); back.setTranslateZ(r); back.setMaterial(frontMat);
        Box left = new Box(1, size, size); left.setTranslateX(-r); left.setMaterial(sideMat);
        Box right = new Box(1, size, size); right.setTranslateX(r); right.setMaterial(sideMat);
        Box top = new Box(size, 1, size); top.setTranslateY(-r); top.setMaterial(sideMat);
        Box bottom = new Box(size, 1, size); bottom.setTranslateY(r); bottom.setMaterial(sideMat);
        group.getChildren().addAll(front, back, left, right, top, bottom);
        return group;
    }
}