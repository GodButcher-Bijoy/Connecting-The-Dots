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
        title.setStroke(Color.WHITE);    // বর্ডার সাদা
        title.setStrokeWidth(2);         // বর্ডার চিকন
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
        ScaleTransition hAnim = new ScaleTransition(Duration.seconds(1), hLine);
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
        TranslateTransition slideLine = new TranslateTransition(Duration.seconds(1.5), vLine);
        // বর্তমান পজিশন (মাঝখান) থেকে বিয়োগ করে বামে নিচ্ছি। ৫০ পিক্সেল গ্যাপ রাখছি।
        slideLine.setToX(-(width / 2) + 50);

        // অ্যানিমেশন ৪: টাইটেলটাও লাইনের সাথে বামে সরে যাবে?
        // নাকি টাইটেল মাঝখানেই থাকবে? তোমার বর্ণনায় টাইটেল সরা বলা হয়নি,
        // কিন্তু নামটা সুন্দর দেখানোর জন্য একটু উপরে তুলে দেওয়া যায়।
        TranslateTransition moveTitleUp = new TranslateTransition(Duration.seconds(1), title);
        moveTitleUp.setByY(-100); // একটু উপরে উঠবে

        // অ্যানিমেশন ৫: নামটা ভেসে উঠবে (Fade In)
        FadeTransition textFade = new FadeTransition(Duration.seconds(1), title);
        textFade.setFromValue(0);
        textFade.setToValue(1);

        // অ্যানিমেশন ৬: টেক্সটের ভেতরটা সাদা রঙে ভরে যাবে (Outline -> Solid)
        // FillTransition টেক্সটের রঙ পরিবর্তন করে


        FillTransition textFill = new FillTransition(Duration.seconds(1.5), title);
        textFill.setFromValue(Color.TRANSPARENT); // শুরু হবে স্বচ্ছ থেকে
        textFill.setToValue(Color.WHITE);         // শেষ হবে সাদা রঙে

        // সব অ্যানিমেশন একটার পর একটা সাজানো (সিকোয়েন্স)
        SequentialTransition sequence = new SequentialTransition(
                hAnim,      // ১. লাইন আসবে
                vAnim,      // ২. খাড়া লাইন আসবে
                slideLine,  // ৩. খাড়া লাইন বামে সরবে
                textFade,   // ৪. নামের বর্ডার ভেসে উঠবে
                textFill    // ৫. নামের ভেতরে রঙ ভরাট হবে (নতুন)
        );
        // টাইটেল উপরে উঠা এবং ফেইড ইন একসাথে হলে সুন্দর লাগবে, তাই ParallelTransition ব্যবহার করা যেত
        // তবে আপাতত সহজ রাখার জন্য সিকোয়েন্স রাখলাম।

        sequence.play(); // অ্যাকশন শুরু!

        // ৬. স্ক্রিনে ক্লিক করলে কনসোলে মেসেজ দেখাবে
        root.setOnMouseClicked(event -> {
            System.out.println("User clicked! Going to Next Scene...");
            // এখানে পরে আমরা সিন চেঞ্জ করার কোড বসাবো
        });

        Scene scene = new Scene(root, width, height);
        stage.setTitle("Graphify Intro");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}