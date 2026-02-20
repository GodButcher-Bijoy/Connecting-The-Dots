
package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.geometry.Insets;



public class MainApp1 extends Application {

    @Override
    public void start(Stage stage) {
        // IntroScene তৈরি ও শো করা হচ্ছে। ক্লিক করলে মেইন সিন ওপেন হবে।
        Scene introScene = IntroScene.create(stage, () -> {
            Scene mainScene = createMainScene();
            stage.setScene(mainScene);
            stage.centerOnScreen();
        });

        stage.setTitle("Graphify");
        stage.setScene(introScene);
        stage.show();
    }

    private Scene createMainScene() {
        BorderPane root = new BorderPane();
        // ডার্ক ব্যাকগ্রাউন্ড, যাতে গ্রাফের চারপাশের ২০ পিক্সেল গ্যাপটা অ্যাপের থিমের সাথে মিশে যায়
        root.setStyle("-fx-background-color: #333333;");

        // ১. AppState তৈরি (ডেটা ম্যানেজ করার জন্য)
        AppState appState = new AppState();

        // ২. Canvas ও Graph Pane সেটআপ
        Pane graphPane = new Pane();
        // বর্ডার মুছে background-radius অ্যাড করলাম যাতে কোণাগুলো সুন্দর গোল হয় (Floating Island)
        graphPane.setStyle("-fx-background-color: #ECF0F1; -fx-background-radius: 12;");
        // চারপাশে ২০ পিক্সেলের একটা সুন্দর গ্যাপ তৈরি করবে
        BorderPane.setMargin(graphPane, new Insets(20));

        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(graphPane.widthProperty());
        canvas.heightProperty().bind(graphPane.heightProperty());
        graphPane.getChildren().add(canvas);

        // --- বাগ ফিক্স: Circular Dependency সলভ করা হলো ---
        // একটি ফাঁকা অ্যাকশন তৈরি করে রাখছি, যা পরে আপডেট করে দেবো
        Runnable[] redrawAction = new Runnable[1];

        // ৩. UIManager তৈরি (একটাই বানাবো এবার)
        UIManager uiManager = new UIManager(appState, () -> {
            if (redrawAction[0] != null) {
                redrawAction[0].run();
            }
        });

        // ৪. GraphRenderer তৈরি
        GraphRenderer graphRenderer = new GraphRenderer(appState, canvas, uiManager.getFunctionContainer());

        // GraphRenderer তৈরি হয়ে গেলে এবার অ্যাকশনটার ভেতরে আসল কমান্ড দিয়ে দিচ্ছি
        redrawAction[0] = () -> graphRenderer.drawGraph();
        // ------------------------------------------------

        // ৫. লেআউটে যোগ করা
        root.setLeft(uiManager.createSidebar());
        root.setCenter(graphPane);

        // প্রথমবার গ্রাফ আঁকার জন্য
        graphRenderer.drawGraph();

        return new Scene(root, 1100, 750);
    }

    public static void main(String[] args) {
        launch(args);
    }
}