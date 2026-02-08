package org.example;

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FillTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // ১. রুট প্যান (Pane ব্যবহার করছি যাতে সব পজিশন নিজের মতো সেট করা যায়)
        Pane root = new Pane();

        // স্ক্রিনের সাইজ
        double width = 900;
        double height = 600;

        // ২. ব্যাকগ্রাউন্ড কালার সেটআপ (Royal Blue)
        // আপনি চাইলে hex code চেঞ্জ করে কালার গাঢ়/হালকা করতে পারেন
        root.setStyle("-fx-background-color: #002366;");

        // ৩. হরাইজন্টাল লাইন (নিচ থেকে ১০০ পিক্সেল উপরে)
        // Line(startX, startY, endX, endY)
        Line hLine = new Line(0, height - 100, width, height - 100);
        hLine.setStroke(Color.WHITE);
        hLine.setStrokeWidth(5); // ৪/৫ পিক্সেল মোটা
        hLine.setScaleX(0); // শুরুতে সাইজ ০ (অদৃশ্য)

        // ৪. ভার্টিক্যাল লাইন (শুরুতে ঠিক মাঝখানে থাকবে)
        Line vLine = new Line(width / 2, 0, width / 2, height);
        vLine.setStroke(Color.WHITE);
        vLine.setStrokeWidth(5);
        vLine.setScaleY(0); // শুরুতে সাইজ ০ (অদৃশ্য)

        // ৫. অ্যাপের নাম (Graphify)
        Text title = new Text("Graphify");
        title.setFont(Font.font("Pristina", FontWeight.BOLD, 85));

        // টেক্সট স্টাইলিং (শুরুতে শুধু বর্ডার দেখা যাবে)

        // Bad dilam

        title.setStroke(Color.WHITE);    // বর্ডার সাদা
        title.setStrokeWidth(0.3);         // বর্ডার চিকন


        title.setFill(Color.TRANSPARENT); // ভেতরটা ফাঁকা (স্বচ্ছ)
        title.setOpacity(0);             // শুরুতে অদৃশ্য

        // পজিশন সেটআপ
        title.setX((width / 2) - 120);
        title.setY(height / 2);

        // টাইটেল পজিশন (শুরুতে মাঝখানে)
        // টেক্সটের প্রস্থ আন্দাজে ২০০ পিক্সেল ধরে মাঝখানে আনা হয়েছে
        title.setX((width / 2) - 120);
        title.setY(height / 2);

        // সবকিছু স্ক্রিনে যোগ করা
        root.getChildren().addAll(hLine, vLine, title);

        // ================== অ্যানিমেশন পার্ট ==================

        // অ্যানিমেশন ১: হরাইজন্টাল লাইন বাম থেকে ডানে বড় হবে
        ScaleTransition hAnim = new ScaleTransition(Duration.seconds(1.5), hLine);
        hAnim.setFromX(0);
        hAnim.setToX(1);
        // লাইনটা যেন মাঝখান থেকে না বেড়ে, বাম দিক থেকে বাড়ে তাই Pivot সেট করা
        // কিন্তু Line এর ক্ষেত্রে ডিফল্ট পিভট কাজ করে, তাই এখানে আলাদা লজিক লাগছে না
        // তবে ScaleTransition ডিফল্টভাবে সেন্টার থেকে বড় হয়।
        // এটাকে বাম থেকে ডানে নিতে হলে পিভট সেট করতে হয় অথবা Translate ব্যবহার করতে হয়।
        // সহজ করার জন্য আমরা পুরো লাইনটাই আঁকছি, জাস্ট স্কেল করছি।

        // অ্যানিমেশন ২: ভার্টিক্যাল লাইন উপর থেকে নিচে নামবে
        ScaleTransition vAnim = new ScaleTransition(Duration.seconds(1), vLine);
        vAnim.setFromY(0);
        vAnim.setToY(1);

        // অ্যানিমেশন ৩: ভার্টিক্যাল লাইনটা স্লাইড করে বামে সরে যাবে (অক্ষ তৈরি হবে)
        TranslateTransition slideLine = new TranslateTransition(Duration.seconds(1), vLine);
        // বর্তমান পজিশন (মাঝখান) থেকে বিয়োগ করে বামে নিচ্ছি। ৫০ পিক্সেল গ্যাপ রাখছি।
        slideLine.setToX(-(width / 2) + 50);

        // অ্যানিমেশন ৪: টাইটেলটাও লাইনের সাথে বামে সরে যাবে?
        // নাকি টাইটেল মাঝখানেই থাকবে? তোমার বর্ণনায় টাইটেল সরা বলা হয়নি,
        // কিন্তু নামটা সুন্দর দেখানোর জন্য একটু উপরে তুলে দেওয়া যায়।

        // ETA USE KORI NAI.

        TranslateTransition moveTitleUp = new TranslateTransition(Duration.seconds(1), title);
        moveTitleUp.setByY(-100); // একটু উপরে উঠবে

        // অ্যানিমেশন ৫: নামটা ভেসে উঠবে (Fade In)
        FadeTransition textFade = new FadeTransition(Duration.seconds(1.5), title);
        textFade.setFromValue(0);
        textFade.setToValue(1);

        // অ্যানিমেশন ৬: টেক্সটের ভেতরটা সাদা রঙে ভরে যাবে (Outline -> Solid)
        // FillTransition টেক্সটের রঙ পরিবর্তন করে


        FillTransition textFill = new FillTransition(Duration.seconds(2), title);
        textFill.setFromValue(Color.TRANSPARENT); // শুরু হবে স্বচ্ছ থেকে
        textFill.setToValue(Color.WHITE);         // শেষ হবে সাদা রঙে

        // সব অ্যানিমেশন একটার পর একটা সাজানো (সিকোয়েন্স)

        ParallelTransition sequence2 = new ParallelTransition(slideLine,textFade,textFill);
        SequentialTransition sequence = new SequentialTransition(
                hAnim,      // ১. লাইন আসবে
                vAnim,      // ২. খাড়া লাইন আসবে
                sequence2

// SLIDE HOBAR POREI TITLE JNO UTHA START HOY

//                slideLine,  // ৩. খাড়া লাইন বামে সরবে
//                textFade,   // ৪. নামের বর্ডার ভেসে উঠবে
//                textFill    // ৫. নামের ভেতরে রঙ ভরাট হবে (নতুন)
        );
        // টাইটেল উপরে উঠা এবং ফেইড ইন একসাথে হলে সুন্দর লাগবে, তাই ParallelTransition ব্যবহার করা যেত
        // তবে আপাতত সহজ রাখার জন্য সিকোয়েন্স রাখলাম।

        sequence.play(); // অ্যাকশন শুরু!

        // ৬. স্ক্রিনে ক্লিক করলে কনসোলে মেসেজ দেখাবে
        // ৬. স্ক্রিনে ক্লিক করলে মেইন সিনে চলে যাবে
        root.setOnMouseClicked(event -> {
            System.out.println("Going to Main App...");
            Scene mainScene = createMainScene(stage); // নতুন সিন তৈরি হলো
            stage.setScene(mainScene); // স্টেজে সেট হলো
            stage.centerOnScreen(); // উইন্ডোটা মাঝখানে চলে আসবে
        });

        Scene scene = new Scene(root, width, height);
        stage.setTitle("Graphify Intro");
        stage.setScene(scene);
        stage.show();
    }



    // এই মেথডটি মেইন সিন (Graph Plotting UI) রিটার্ন করবে
    private Scene createMainScene(Stage stage) {
        // ১. মেইন লেআউট (BorderPane সেরা কারণ এতে Top, Bottom, Left, Center ভাগ করা যায়)
        BorderPane root = new BorderPane();

        // ================= SIDEBAR (বাম পাশ - ইনপুট এরিয়া) =================
        VBox sidebar = new VBox(20); // ২০ পিক্সেল গ্যাপ
        sidebar.setPadding(new Insets(30)); // চারপাশ থেকে প্যাডিং
        sidebar.setPrefWidth(400); // সাইডবার ৩০০ পিক্সেল চওড়া হবে
        sidebar.setStyle("-fx-background-color: #121212;");// গাঢ় নীল-ধূসর কালার
        sidebar.setAlignment(Pos.CENTER_LEFT);

        // টাইটেল
        Label inputLabel = new Label("Enter Function:");
        inputLabel.setTextFill(Color.DEEPPINK);
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        // ইকুয়েশন ইনপুট বক্স
        TextField equationInput = new TextField();
        equationInput.setPromptText("Ex: sin(x) + x^2"); // হালকা করে লেখা থাকবে
        equationInput.setPrefHeight(40); // একটু মোটা বক্স
        equationInput.setFont(Font.font(16));
        equationInput.setPadding(new Insets(20));
        // বক্সের ডিজাইন (CSS)
        equationInput.setStyle(
                "-fx-background-color: #1F1F1F; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #9D00FF; " +
                        "-fx-border-width: 5; " +
                        "-fx-border-radius: 10; " +
                        "-fx-text-fill: white; " +

                        // নতুন অংশ: ফন্ট স্টাইল
                        "-fx-font-size: 15px; " +           // সাইজ একটু বাড়ালাম (১৮ পিক্সেল)
                        "-fx-font-family: 'Verdana'; " +    // ফন্টের নাম (Verdana, Arial, বা Impact দিতে পারো)
                        "-fx-font-weight: bold;"            // লেখা বোল্ড হবে
        );

        // X-Range ইনপুট (অপশনাল - পরে কাজে লাগবে)
        Label rangeLabel = new Label("X Range (Min, Max):");
        rangeLabel.setTextFill(Color.LIGHTGRAY);

        HBox rangeBox = new HBox(10);
        TextField minInput = new TextField("-10");
        TextField maxInput = new TextField("10");
        minInput.setPrefWidth(100); maxInput.setPrefWidth(100);
        rangeBox.getChildren().addAll(minInput, maxInput);

        // প্লট বাটন
        Button plotBtn = new Button("PLOT GRAPH");
        plotBtn.setPrefWidth(Double.MAX_VALUE); // পুরো জায়গা জুড়ে থাকবে
        plotBtn.setPrefHeight(45);
        plotBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        plotBtn.setTextFill(Color.WHITE);
        // বাটনের কালার (একটু উজ্জ্বল কমলা বা সবুজ)
        plotBtn.setStyle("-fx-background-color: #BB86FC; -fx-background-radius: 10; -fx-cursor: hand;");

        // বাটন হোভার ইফেক্ট (মাউস নিলে কালার চেঞ্জ হবে)
        plotBtn.setOnMouseEntered(e -> plotBtn.setStyle("-fx-background-color: #C0392B; -fx-background-radius: 10;"));
        plotBtn.setOnMouseExited(e -> plotBtn.setStyle("-fx-background-color: #E74C3C; -fx-background-radius: 10;"));

        // সাইডবারে সব কিছু যোগ করা
        sidebar.getChildren().addAll(inputLabel, equationInput, rangeLabel, rangeBox, plotBtn);

        // ================= CENTER (ডান পাশ - গ্রাফ এরিয়া) =================
        Pane graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #ECF0F1;"); // হালকা ধূসর ব্যাকগ্রাউন্ড (গ্রাফের জন্য)

        // আপাতত একটা প্লেসহোল্ডার টেক্সট (পরে তোমার ফ্রেন্ড এখানে গ্রাফ বসাবে)
        Label placeholder = new Label("Graph will appear here...");
        placeholder.setFont(Font.font("Segoe UI", 20));
        placeholder.setTextFill(Color.GRAY);
        // টেক্সট মাঝখানে আনা (একটু ট্রিকি Pane এর জন্য)
        placeholder.layoutXProperty().bind(graphPane.widthProperty().subtract(placeholder.widthProperty()).divide(2));
        placeholder.layoutYProperty().bind(graphPane.heightProperty().subtract(placeholder.heightProperty()).divide(2));

        graphPane.getChildren().add(placeholder);

        // ================= LAYOUT SETTING =================
        root.setLeft(sidebar);
        root.setCenter(graphPane);

        // বাটনের কাজ (অ্যাকশন)
        plotBtn.setOnAction(e -> {
            String eqn = equationInput.getText();
            System.out.println("User wants to plot: " + eqn);
            // এখানেই পরে তোমার ফ্রেন্ডের লজিক কল করা হবে!
            // FriendClass.drawGraph(graphPane, eqn);
        });

        return new Scene(root, 1000, 700); // নতুন সিনের সাইজ একটু বড় দিলাম
    }

    public static void main(String[] args) {
        launch();
    }
}