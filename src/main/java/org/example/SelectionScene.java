package org.example;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class SelectionScene {

    public static Parent createView(Runnable onStandard, Runnable onPolar, Runnable onLibrary) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #050505;"); // Base pitch black

        // =========================================================
        // 1. TRUE 3D LAYER: Grid & Popping Cubes
        // =========================================================
        Group group3D = new Group();

        // Add Ambient Light so the 3D materials are visible
        AmbientLight light = new AmbientLight(Color.WHITE);
        group3D.getChildren().add(light);

        // --- Build the 3D Floor Grid ---
        Group grid = new Group();
        double gridSize = 6000;
        double step = 150; // Distance between grid lines

        for (double i = -gridSize / 2; i <= gridSize / 2; i += step) {
            // Horizontal lines
            Line hLine = new Line(-gridSize / 2, i, gridSize / 2, i);
            hLine.setStroke(Color.web("#AAAAAA", 0.9));
            hLine.setStrokeWidth(3.5);

            // Vertical lines
            Line vLine = new Line(i, -gridSize / 2, i, gridSize / 2);
            vLine.setStroke(Color.web("#AAAAAA", 0.9));
            vLine.setStrokeWidth(3.5);

            grid.getChildren().addAll(hLine, vLine);
        }

        // Rotate the 2D lines 90 degrees on the X-axis so they lie flat like a floor
        grid.setRotationAxis(Rotate.X_AXIS);
        grid.setRotate(90);
        group3D.getChildren().add(grid);

        // --- Build the 3D Popping Cubes Pool ---
        double cubeSize = step - 20; // 130px — matches grid cell width
        List<Group> cubePool = new ArrayList<>();
        Color[] neonColors = {
                Color.web("#00FFFF"),
                Color.web("#FF00FF"),
                Color.web("#39FF14"),
                Color.web("#B026FF")
        };

        for (int i = 0; i < 60; i++) {
            PhongMaterial edgeMat = new PhongMaterial(Color.TRANSPARENT);
            Group wireframe = createWireframeCube(cubeSize, edgeMat);
            wireframe.setScaleY(0.01);
            wireframe.getProperties().put("available", true);
            wireframe.getProperties().put("edgeMat", edgeMat);
            cubePool.add(wireframe);
            group3D.getChildren().add(wireframe);
        }

        Timeline spawner = new Timeline(new KeyFrame(Duration.millis(80), e -> {
            Group availableCube = cubePool.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getProperties().get("available")))
                    .findFirst().orElse(null);
            if (availableCube == null) return;

            availableCube.getProperties().put("available", false);

            // Snap to grid CELL center (+step/2 offset aligns with cell interior, not grid line)
            double x = Math.floor((Math.random() * gridSize - gridSize / 2) / step) * step + step / 2;
            double z = Math.floor((Math.random() * gridSize - gridSize / 2) / step) * step + step / 2;
            availableCube.setTranslateX(x);
            availableCube.setTranslateZ(z);
            availableCube.setTranslateY(0);

            Color glowColor = neonColors[(int) (Math.random() * neonColors.length)];
            PhongMaterial edgeMat = (PhongMaterial) availableCube.getProperties().get("edgeMat");
            edgeMat.setDiffuseColor(glowColor);
            edgeMat.setSpecularColor(glowColor.brighter());

            double targetHeight = 40 + Math.random() * 400;
            double targetScaleY = targetHeight / cubeSize;

            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(1.4), availableCube);
            scaleUp.setFromY(0.01);
            scaleUp.setToY(targetScaleY);
            scaleUp.setAutoReverse(true);
            scaleUp.setCycleCount(2);

            TranslateTransition moveUp = new TranslateTransition(Duration.seconds(1.4), availableCube);
            moveUp.setFromY(0);
            moveUp.setToY(-targetHeight / 2.0);
            moveUp.setAutoReverse(true);
            moveUp.setCycleCount(2);

            scaleUp.setOnFinished(ev -> {
                edgeMat.setDiffuseColor(Color.TRANSPARENT);
                edgeMat.setSpecularColor(Color.TRANSPARENT);
                availableCube.setScaleY(0.01);
                availableCube.setTranslateY(0);
                availableCube.getProperties().put("available", true);
            });

            double animDur = 1.4;
            Timeline glowAnim = new Timeline(
                    new KeyFrame(Duration.ZERO, ev2 ->
                            edgeMat.setDiffuseColor(glowColor.deriveColor(0, 1, 0.2, 1))),
                    new KeyFrame(Duration.seconds(animDur), ev2 -> {
                        edgeMat.setDiffuseColor(glowColor.brighter().brighter());
                        edgeMat.setSpecularColor(Color.WHITE);
                    })
            );
            glowAnim.setAutoReverse(true);
            glowAnim.setCycleCount(2);
            glowAnim.play();

            scaleUp.play();
            moveUp.play();
        }));
        spawner.setCycleCount(Animation.INDEFINITE);
        spawner.play();

        // --- Setup the 3D SubScene & Camera ---
        SubScene subScene3D = new SubScene(group3D, 1920, 1080, true, SceneAntialiasing.BALANCED);
        subScene3D.setFill(Color.TRANSPARENT); // Let the StackPane's black background show through
        subScene3D.widthProperty().bind(root.widthProperty());
        subScene3D.heightProperty().bind(root.heightProperty());

        PerspectiveCamera camera = new PerspectiveCamera(true);
        // CRITICAL FIX: The default farClip is 100. We pulled the camera back to -2000.
        // Setting farClip to 15000 ensures the 6000px grid is actually rendered!
        camera.setNearClip(0.1);
        camera.setFarClip(15000);

        camera.setTranslateZ(-2500); // Pull camera back
        camera.setTranslateY(-800);  // Lift camera up into the air
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(-20);       // Tilt camera down to look at the grid
        subScene3D.setCamera(camera);

        // =========================================================
        // 2. VIGNETTE OVERLAY: Fade the grid out towards the edges
        // =========================================================
        Rectangle edgeFade = new Rectangle();
        edgeFade.widthProperty().bind(root.widthProperty());
        edgeFade.heightProperty().bind(root.heightProperty());
        edgeFade.setMouseTransparent(true); // Don't block clicks

        RadialGradient fadeGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 0.7, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),        // Clear in the middle
                new Stop(1, Color.web("#050505", 1.0)) // Solid black at edges
        );
        edgeFade.setFill(fadeGradient);

        // =========================================================
        // 3. UI LAYER: Floating Buttons on the Right
        // =========================================================
        VBox buttonsBox = new VBox(40);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsBox.setPadding(new Insets(0, 100, 0, 0));
        buttonsBox.setPickOnBounds(false); // Let empty space pass clicks down if needed

        Button btnStandard = new Button("STANDARD");
        Button btnPolar = new Button("POLAR");
        Button btnLibrary = new Button("CURVES LIBRARY");

        setupFloatingButton(btnStandard, "#00FFFF", onStandard);
        setupFloatingButton(btnPolar, "#FF003C", onPolar);
        setupFloatingButton(btnLibrary, "#B026FF", onLibrary);

        buttonsBox.getChildren().addAll(btnStandard, btnPolar, btnLibrary);

        BorderPane uiLayer = new BorderPane();
        uiLayer.setRight(buttonsBox);
        uiLayer.setPickOnBounds(false);

        // Stack the layers: 1. 3D Scene -> 2. Edge Fade -> 3. UI Buttons
        root.getChildren().addAll(subScene3D, edgeFade, uiLayer);

        return root;
    }

    // =========================================================================
    // FLOATING & SINKING BUTTON LOGIC
    // =========================================================================
    private static void setupFloatingButton(Button btn, String neonHex, Runnable action) {
        String baseStyle =
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-letter-spacing: 2px; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: " + neonHex + "; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: " + neonHex + "; " +
                        "-fx-alignment: center; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-color: #080808; " +
                        "-fx-padding: 20 40;";

        btn.setStyle(baseStyle);
        btn.setPrefWidth(260);

        // Neon Glow
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web(neonHex, 0.4));
        glow.setRadius(15);
        glow.setSpread(0.1);
        btn.setEffect(glow);

        // Floating Animation
        TranslateTransition bob = new TranslateTransition(Duration.millis(1200 + Math.random() * 600), btn);
        bob.setByY(8);
        bob.setAutoReverse(true);
        bob.setCycleCount(Animation.INDEFINITE);
        bob.setInterpolator(Interpolator.EASE_BOTH);
        bob.play();

        // Sinking Effect (Click)
        ScaleTransition sink = new ScaleTransition(Duration.millis(100), btn);
        sink.setToX(0.85);
        sink.setToY(0.85);

        ScaleTransition rise = new ScaleTransition(Duration.millis(200), btn);
        rise.setToX(1.0);
        rise.setToY(1.0);

        btn.setOnMousePressed(e -> {
            sink.playFromStart();
            btn.setStyle(baseStyle.replace("#080808", "#000000"));
            glow.setRadius(5);
        });

        btn.setOnMouseReleased(e -> {
            rise.playFromStart();
            btn.setStyle(baseStyle);
            glow.setRadius(15);
            if(action != null) action.run();
        });

        // Hover Effect
        btn.setOnMouseEntered(e -> {
            glow.setRadius(25);
            glow.setColor(Color.web(neonHex, 0.7));
        });

        btn.setOnMouseExited(e -> {
            glow.setRadius(15);
            glow.setColor(Color.web(neonHex, 0.4));
        });
    }
    private static Group createWireframeCube(double size, PhongMaterial edgeMat) {
        Group cube = new Group();
        double t = 4;   // edge thickness
        double hs = size / 2;

        // Black fill so faces match background
        PhongMaterial blackMat = new PhongMaterial(Color.web("#050505"));
        Box fill = new Box(size - t * 2, size, size - t * 2);
        fill.setMaterial(blackMat);
        cube.getChildren().add(fill);

        // 4 vertical edges (corners)
        for (int dx : new int[]{-1, 1}) {
            for (int dz : new int[]{-1, 1}) {
                Box e = new Box(t, size, t);
                e.setTranslateX(dx * (hs - t / 2));
                e.setTranslateZ(dz * (hs - t / 2));
                e.setMaterial(edgeMat);
                cube.getChildren().add(e);
            }
        }

        // Top ring  (y = -hs)
        Box tx1 = new Box(size, t, t); tx1.setTranslateY(-hs + t/2); tx1.setTranslateZ(-hs + t/2); tx1.setMaterial(edgeMat);
        Box tx2 = new Box(size, t, t); tx2.setTranslateY(-hs + t/2); tx2.setTranslateZ( hs - t/2); tx2.setMaterial(edgeMat);
        Box tz1 = new Box(t, t, size); tz1.setTranslateY(-hs + t/2); tz1.setTranslateX(-hs + t/2); tz1.setMaterial(edgeMat);
        Box tz2 = new Box(t, t, size); tz2.setTranslateY(-hs + t/2); tz2.setTranslateX( hs - t/2); tz2.setMaterial(edgeMat);

        // Bottom ring  (y = +hs)
        Box bx1 = new Box(size, t, t); bx1.setTranslateY( hs - t/2); bx1.setTranslateZ(-hs + t/2); bx1.setMaterial(edgeMat);
        Box bx2 = new Box(size, t, t); bx2.setTranslateY( hs - t/2); bx2.setTranslateZ( hs - t/2); bx2.setMaterial(edgeMat);
        Box bz1 = new Box(t, t, size); bz1.setTranslateY( hs - t/2); bz1.setTranslateX(-hs + t/2); bz1.setMaterial(edgeMat);
        Box bz2 = new Box(t, t, size); bz2.setTranslateY( hs - t/2); bz2.setTranslateX( hs - t/2); bz2.setMaterial(edgeMat);

        cube.getChildren().addAll(tx1, tx2, tz1, tz2, bx1, bx2, bz1, bz2);
        return cube;
    }
}