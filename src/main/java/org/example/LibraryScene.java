package org.example;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LibraryScene {

    public static Parent createView(Runnable onBack, Consumer<EquationPreset> onPresetSelected) {

        // ── Root: black base ──
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #050505;");

        // ── 1. 3D grid + neon cubes (same as SelectionScene) ──
        root.getChildren().addAll(create3DBackground(root));

        // ── 2. Dark scroll panel sitting on top ──
        BorderPane panel = new BorderPane();
        // Semi-transparent so the 3D background peeks through
        panel.setStyle("-fx-background-color: rgba(5,5,5,0.72);");

        // --- Top: Back Button ---
        Button backBtn = new Button("⌂");
        String normalStyle = "-fx-font-size: 30px; -fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 10 0 10;";
        String hoverStyle  = "-fx-font-size: 30px; -fx-background-color: #222222; -fx-text-fill: #9D00FF; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 10 0 10;";

        backBtn.setStyle(normalStyle);
        backBtn.setOnAction(e -> onBack.run());
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(normalStyle));

        HBox topBar = new HBox(backBtn);
        topBar.setPadding(new Insets(10, 20, 0, 20));
        panel.setTop(topBar);

        // --- Center: Category & Preset Grid ---
        VBox categoriesBox = new VBox(40);
        categoriesBox.setPadding(new Insets(0, 50, 50, 50));
        categoriesBox.setAlignment(Pos.TOP_CENTER);

        EquationLibrary lib = new EquationLibrary();
        for (EquationCategory cat : lib.getCategories()) {
            Label catLabel = new Label(cat.getMenuLabel());
            catLabel.setStyle("-fx-text-fill: #16E004; -fx-font-size: 28px; -fx-font-weight: bold;");

            FlowPane presetsPane = new FlowPane(20, 20);
            presetsPane.setAlignment(Pos.CENTER);

            for (EquationPreset preset : cat.getPresets()) {
                Button pBtn = new Button(preset.getName());
                pBtn.setStyle("-fx-font-size: 18px; -fx-background-color: rgba(30,30,30,0.85); -fx-text-fill: #ddd; -fx-padding: 15 30; -fx-background-radius: 10; -fx-cursor: hand;");
                pBtn.setOnAction(e -> onPresetSelected.accept(preset));

                // Subtle neon glow on hover
                DropShadow glow = new DropShadow();
                glow.setColor(Color.web("#B026FF", 0.0));
                glow.setRadius(12);
                pBtn.setEffect(glow);

                pBtn.setOnMouseEntered(e -> {
                    pBtn.setStyle("-fx-font-size: 18px; -fx-background-color: rgba(60,60,60,0.9); -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 10; -fx-cursor: hand;");
                    glow.setColor(Color.web("#B026FF", 0.65));
                });
                pBtn.setOnMouseExited(e -> {
                    pBtn.setStyle("-fx-font-size: 18px; -fx-background-color: rgba(30,30,30,0.85); -fx-text-fill: #ddd; -fx-padding: 15 30; -fx-background-radius: 10; -fx-cursor: hand;");
                    glow.setColor(Color.web("#B026FF", 0.0));
                });

                presetsPane.getChildren().add(pBtn);
            }

            VBox section = new VBox(15, catLabel, presetsPane);
            section.setAlignment(Pos.CENTER);
            categoriesBox.getChildren().add(section);
        }

        ScrollPane scroll = new ScrollPane(categoriesBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        panel.setCenter(scroll);

        root.getChildren().add(panel);
        return root;
    }

    // =========================================================================
    // SHARED 3D BACKGROUND — same neon grid + popping cube effect as SelectionScene
    // =========================================================================
    private static List<javafx.scene.Node> create3DBackground(StackPane root) {
        Group group3D = new Group();
        group3D.getChildren().add(new AmbientLight(Color.WHITE));

        double gridSize = 6000;
        double step     = 150;
        double lineW    = 2.5;
        double lineH    = 1.5;

        PhongMaterial gridMat = new PhongMaterial(Color.web("#AAAAAA", 0.9));
        for (double i = -gridSize / 2; i <= gridSize / 2; i += step) {
            Box hLine = new Box(gridSize, lineH, lineW);
            hLine.setTranslateZ(i);
            hLine.setMaterial(gridMat);

            Box vLine = new Box(lineW, lineH, gridSize);
            vLine.setTranslateX(i);
            vLine.setTranslateY(lineH);
            vLine.setMaterial(gridMat);

            group3D.getChildren().addAll(hLine, vLine);
        }

        double cubeSize = step - 20;
        List<Group> cubePool = new ArrayList<>();
        Color[] neonColors = {
                Color.web("#00FFFF"), Color.web("#FF00FF"),
                Color.web("#39FF14"), Color.web("#B026FF")
        };

        for (int i = 0; i < 60; i++) {
            PhongMaterial edgeMat = new PhongMaterial(Color.TRANSPARENT);
            Group wireframe = createWireframeCube(cubeSize, edgeMat);
            wireframe.setScaleY(0.01);
            wireframe.setTranslateY(99999);
            wireframe.getProperties().put("available", true);
            wireframe.getProperties().put("edgeMat", edgeMat);
            cubePool.add(wireframe);
            group3D.getChildren().add(wireframe);
        }

        Timeline spawner = new Timeline(new KeyFrame(Duration.millis(80), e -> {
            Group cube = cubePool.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getProperties().get("available")))
                    .findFirst().orElse(null);
            if (cube == null) return;

            cube.getProperties().put("available", false);
            cube.setTranslateX(Math.floor((Math.random() * gridSize - gridSize / 2) / step) * step + step / 2);
            cube.setTranslateZ(Math.floor((Math.random() * gridSize - gridSize / 2) / step) * step + step / 2);
            cube.setTranslateY(0);

            Color glowColor = neonColors[(int) (Math.random() * neonColors.length)];
            PhongMaterial edgeMat = (PhongMaterial) cube.getProperties().get("edgeMat");
            edgeMat.setDiffuseColor(glowColor);
            edgeMat.setSpecularColor(glowColor.brighter());

            double targetHeight = 40 + Math.random() * 400;
            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(1.4), cube);
            scaleUp.setFromY(0.01);
            scaleUp.setToY(targetHeight / cubeSize);
            scaleUp.setAutoReverse(true);
            scaleUp.setCycleCount(2);

            TranslateTransition moveUp = new TranslateTransition(Duration.seconds(1.4), cube);
            moveUp.setFromY(0);
            moveUp.setToY(-targetHeight / 2.0);
            moveUp.setAutoReverse(true);
            moveUp.setCycleCount(2);

            scaleUp.setOnFinished(ev -> {
                edgeMat.setDiffuseColor(Color.TRANSPARENT);
                edgeMat.setSpecularColor(Color.TRANSPARENT);
                cube.setScaleY(0.01);
                cube.setTranslateY(99999);
                cube.getProperties().put("available", true);
            });

            Timeline glowAnim = new Timeline(
                    new KeyFrame(Duration.ZERO, ev ->
                            edgeMat.setDiffuseColor(glowColor.deriveColor(0, 1, 0.2, 1))),
                    new KeyFrame(Duration.seconds(1.4), ev -> {
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

        SubScene subScene3D = new SubScene(group3D, 1920, 1080, true, SceneAntialiasing.BALANCED);
        subScene3D.setFill(Color.TRANSPARENT);
        subScene3D.widthProperty().bind(root.widthProperty());
        subScene3D.heightProperty().bind(root.heightProperty());

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(15000);
        camera.setTranslateZ(-2500);
        camera.setTranslateY(-800);
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(-20);
        subScene3D.setCamera(camera);

        Rectangle edgeFade = new Rectangle();
        edgeFade.widthProperty().bind(root.widthProperty());
        edgeFade.heightProperty().bind(root.heightProperty());
        edgeFade.setMouseTransparent(true);
        edgeFade.setFill(new RadialGradient(
                0, 0, 0.5, 0.5, 0.7, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.web("#050505", 1.0))
        ));

        return List.of(subScene3D, edgeFade);
    }

    private static Group createWireframeCube(double size, PhongMaterial edgeMat) {
        Group cube = new Group();
        double t = 4, hs = size / 2;
        PhongMaterial blackMat = new PhongMaterial(Color.web("#050505"));
        Box fill = new Box(size - t * 2, size, size - t * 2);
        fill.setMaterial(blackMat);
        cube.getChildren().add(fill);

        for (int dx : new int[]{-1, 1})
            for (int dz : new int[]{-1, 1}) {
                Box e = new Box(t, size, t);
                e.setTranslateX(dx * (hs - t / 2));
                e.setTranslateZ(dz * (hs - t / 2));
                e.setMaterial(edgeMat);
                cube.getChildren().add(e);
            }

        Box tx1 = new Box(size,t,t); tx1.setTranslateY(-hs+t/2); tx1.setTranslateZ(-hs+t/2); tx1.setMaterial(edgeMat);
        Box tx2 = new Box(size,t,t); tx2.setTranslateY(-hs+t/2); tx2.setTranslateZ( hs-t/2); tx2.setMaterial(edgeMat);
        Box tz1 = new Box(t,t,size); tz1.setTranslateY(-hs+t/2); tz1.setTranslateX(-hs+t/2); tz1.setMaterial(edgeMat);
        Box tz2 = new Box(t,t,size); tz2.setTranslateY(-hs+t/2); tz2.setTranslateX( hs-t/2); tz2.setMaterial(edgeMat);
        Box bx1 = new Box(size,t,t); bx1.setTranslateY( hs-t/2); bx1.setTranslateZ(-hs+t/2); bx1.setMaterial(edgeMat);
        Box bx2 = new Box(size,t,t); bx2.setTranslateY( hs-t/2); bx2.setTranslateZ( hs-t/2); bx2.setMaterial(edgeMat);
        Box bz1 = new Box(t,t,size); bz1.setTranslateY( hs-t/2); bz1.setTranslateX(-hs+t/2); bz1.setMaterial(edgeMat);
        Box bz2 = new Box(t,t,size); bz2.setTranslateY( hs-t/2); bz2.setTranslateX( hs-t/2); bz2.setMaterial(edgeMat);
        cube.getChildren().addAll(tx1,tx2,tz1,tz2,bx1,bx2,bz1,bz2);
        return cube;
    }
}