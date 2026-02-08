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
import javafx.scene.control.ScrollPane; // এটার জন্য এরর দিচ্ছে
import javafx.scene.layout.Priority;    // VBox.setVgrow এর জন্য এটা লাগবে
import javafx.scene.input.KeyCode;      // Enter বাটন চেনার জন্য এটা লাগবে

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
        BorderPane root = new BorderPane();

        // ---------------- SIDEBAR ----------------
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30));
        sidebar.setPrefWidth(400);
        sidebar.setAlignment(Pos.TOP_LEFT); // টপ থেকে শুরু হবে

        // আপনার দেওয়া এক্সাক্ট সাইডবার স্টাইল
        sidebar.setStyle(
                "-fx-background-color: #121212; " +
                        "-fx-border-color: Purple; " +
                        "-fx-border-width: 4px; " +
                        "-fx-border-style: solid inside;"
        );

        // হেডার লেভেল
        Label inputLabel = new Label("Enter Function:");
        inputLabel.setTextFill(Color.DEEPPINK); // আপনার কালার
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        // --- SCROLLABLE INPUT AREA START ---

        // ১. একটা কন্টেইনার বানাই যেটা সব ইনপুট বক্স ধরে রাখবে
        VBox functionContainer = new VBox(15); // বক্সগুলোর মাঝে ১৫ পিক্সেল গ্যাপ
        functionContainer.setStyle("-fx-background-color: transparent;");

        // ২. শুরুতে ৩টা বা ৫টা ফাঁকা ইনপুট বক্স দিয়ে দিই
        for(int i=0; i<3; i++) {
            addFunctionInputBox(functionContainer);
        }

        // ৩. স্ক্রলপ্যান সেটআপ
        ScrollPane scrollPane = new ScrollPane(functionContainer);
        scrollPane.setFitToWidth(true); // সাইডবারের সমান চওড়া
        // স্ক্রলপ্যানের ব্যাকগ্রাউন্ড ট্রান্সপারেন্ট করছি যাতে আপনার কালো ব্যাকগ্রাউন্ড দেখা যায়
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // নিচের বারের দরকার নেই

        // --- SCROLLABLE INPUT AREA END ---

        // X-Range (নিচে থাকবে)
        Label rangeLabel = new Label("X Range (Min, Max):");
        rangeLabel.setTextFill(Color.LIGHTGRAY);
        rangeLabel.setPadding(new Insets(10, 0, 5, 0));

        HBox rangeBox = new HBox(10);
        TextField minInput = new TextField("-10");
        TextField maxInput = new TextField("10");
        minInput.setPrefWidth(100); maxInput.setPrefWidth(100);

        // রেঞ্জ ইনপুটের স্টাইল (আপনার আগের কোড অনুযায়ী একটু মডিফাই করা)
        String rangeStyle = "-fx-background-color: #1F1F1F; -fx-text-fill: white; -fx-border-color: gray; -fx-border-radius: 5; -fx-background-radius: 5;";
        minInput.setStyle(rangeStyle);
        maxInput.setStyle(rangeStyle);

        rangeBox.getChildren().addAll(minInput, maxInput);

        // সাইডবারে সব অ্যাড করা (Plot Button বাদ দিয়েছি)
        // VBox.setVgrow দিয়ে স্ক্রলপ্যানকে বলছি বাকি সব জায়গা নিয়ে নিতে
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        sidebar.getChildren().addAll(inputLabel, scrollPane, rangeLabel, rangeBox);


        // ---------------- GRAPH AREA ----------------
        Pane graphPane = new Pane();
        // আপনার দেওয়া গ্রাফ প্যান স্টাইল
        graphPane.setStyle(
                "-fx-background-color: #ECF0F1; " +
                        "-fx-border-color: Purple; " +
                        "-fx-border-width: 4px; " +
                        "-fx-border-style: solid inside;"
        );

        Label placeholder = new Label("Graph will appear here...");
        placeholder.setFont(Font.font("Segoe UI", 20));
        placeholder.setTextFill(Color.GRAY);

        placeholder.layoutXProperty().bind(graphPane.widthProperty().subtract(placeholder.widthProperty()).divide(2));
        placeholder.layoutYProperty().bind(graphPane.heightProperty().subtract(placeholder.heightProperty()).divide(2));

        graphPane.getChildren().add(placeholder);

        root.setLeft(sidebar);
        root.setCenter(graphPane);

        return new Scene(root, 1000, 700);
    }

    // =========================================================
    // HELPER METHOD: ডাইনামিক এবং স্টাইলিশ ইনপুট বক্স তৈরি করা
    // =========================================================
    private void addFunctionInputBox(VBox container) {
        TextField inputBox = new TextField();
        inputBox.setPromptText("y = ...");
        inputBox.setPrefHeight(60); // আপনার হাইট
        inputBox.setPadding(new Insets(5, 10, 5, 10));

        // 🔥 আপনার স্পেশাল স্টাইল কোড এখানেই বসানো হয়েছে 🔥
        inputBox.setStyle(
                "-fx-background-color: White; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #9D00FF; " +  // সেই বেগুনি বর্ডার
                        "-fx-border-width: 3; " +        // ৫ পিক্সেল বর্ডার
                        "-fx-border-radius: 10; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 15px; " +
                        "-fx-font-family: 'Verdana'; " + // ভারদানা ফন্ট
                        "-fx-font-weight: bold;"
        );

        // লাইভ ডেটা লিসেনার (টাইপ করার সাথে সাথে ভ্যালু পাওয়া যাবে)
        inputBox.textProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Input Updated: " + newVal);
            // এখানেই পরে গ্রাফ আঁকার মেথড কল হবে
        });

        // ENTER KEY লজিক
        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                int index = container.getChildren().indexOf(inputBox);

                // যদি এটা শেষ বক্স হয়, নতুন বক্স বানাবে
                if (index == container.getChildren().size() - 1) {
                    addFunctionInputBox(container);
                }

                // পরের বক্সে ফোকাস নিয়ে যাবে
                if (index + 1 < container.getChildren().size()) {
                    container.getChildren().get(index + 1).requestFocus();
                }
            }
        });

        container.getChildren().add(inputBox);
        // নতুন বক্স তৈরি হলে সেটাতে অটো ফোকাস যাবে
        inputBox.requestFocus();
    }
    public static void main(String[] args) {
        launch();
    }
}