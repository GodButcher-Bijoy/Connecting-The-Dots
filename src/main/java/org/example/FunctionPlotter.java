package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.objecthunter.exp4j.Expression;

public class FunctionPlotter {
    private final AppState appState;
    private final GraphicsContext gc;

    public FunctionPlotter(AppState appState, GraphicsContext gc) {
        this.appState = appState;
        this.gc = gc;
    }

    public void plotEquation(String eqStr, Color color, double cx, double cy, double width, double height) {
        gc.setStroke(color);
        gc.setLineWidth(2.5);

        String eq = EquationHandler.formatEquation(eqStr);

        try {
            if (eq.startsWith("y=") && !eq.contains("x=")) plotStandard(cx, cy, width, height, eq.substring(2));
            else if (eq.startsWith("x=") && !eq.contains("y=")) plotInverse(cx, cy, width, height, eq.substring(2));
            else if (eq.contains("=")) plotImplicit(cx, cy, width, height, eq);
            else plotStandard(cx, cy, width, height, eq);
        } catch (Exception e) {}
    }

    private void plotStandard(double cx, double cy, double width, double height, String function) {
        Expression expr = EquationHandler.buildExpression(function, "x", appState.getGlobalVariables());
        gc.beginPath();
        boolean first = true;

        // ১. পিক্সেল স্টেপ ছোট করা হয়েছে নিখুঁত ফলাফলের জন্য
        double step = 0.005;

        for (double screenX = 0; screenX < width; screenX += step) {
            double mathX = (screenX - cx) / appState.getScale();
            expr.setVariable("x", mathX);

            double mathY;
            try {
                mathY = expr.evaluate();
            } catch (Exception e) {
                first = true;
                continue;
            }

            if (Double.isNaN(mathY) || Double.isInfinite(mathY)) {
                first = true;
                continue;
            }

            double screenY = cy - (mathY * appState.getScale());

            // [CRITICAL FIX]: আসিম্পটোট (Asymptote) বা ইনফিনিটি হ্যান্ডলিং
            // এখানে screenY যদি স্ক্রিনের বাইরেও চলে যায়, আমরা লাইন ড্র করা বন্ধ করব না
            // বরং একে ক্ল্যাম্প (Clamp) করে দেব যাতে লাইনটি স্ক্রিনের টপ বা বটম পর্যন্ত পৌঁছায়।
            double clampedY = screenY;
            if (screenY < -100000) clampedY = -100000;
            if (screenY > height + 100000) clampedY = height + 100000;

            if (first) {
                gc.moveTo(screenX, clampedY);
                first = false;
            } else {
                // যদি খুব বড় জাম্প হয় (যেমন tan(x) এর এক পিরিয়ড থেকে অন্য পিরিয়ডে)
                // তাহলে লাইন ড্র না করে নতুন পাথ শুরু করবে
                double prevMathX = (screenX - step - cx) / appState.getScale();
                expr.setVariable("x", prevMathX);
                double prevMathY = expr.evaluate();

                // যদি ফাংশনটি কন্টিনিউয়াস না হয় (যেমন tan(x) এর π/2 তে)
                if (Math.abs(mathY - prevMathY) > (height / appState.getScale()) * 2) {
                    gc.stroke(); // বর্তমান পাথ শেষ করো
                    gc.beginPath(); // নতুন পাথ শুরু করো
                    gc.moveTo(screenX, clampedY);
                } else {
                    gc.lineTo(screenX, clampedY);
                }
            }
        }
        gc.stroke();
    }

    private void plotInverse(double cx, double cy, double width, double height, String function) {
        Expression expr = EquationHandler.buildExpression(function, "y", appState.getGlobalVariables());
        gc.beginPath();
        boolean first = true;
        double step = 0.005; // নিখুঁত গ্রাফের জন্য ছোট স্টেপ

        for (double screenY = 0; screenY < height; screenY += step) {
            double mathY = (cy - screenY) / appState.getScale();
            expr.setVariable("y", mathY);

            double mathX;
            try {
                mathX = expr.evaluate();
            } catch (Exception e) {
                first = true;
                continue;
            }

            if (Double.isNaN(mathX) || Double.isInfinite(mathX)) {
                first = true;
                continue;
            }

            double screenX = cx + (mathX * appState.getScale());

            // [FIX]: স্ক্রিনের সীমানার বাইরে অনেক দূর পর্যন্ত লাইন ক্ল্যাম্প করা
            double clampedX = Math.max(-100000, Math.min(width + 100000, screenX));

            if (first) {
                gc.moveTo(clampedX, screenY);
                first = false;
            } else {
                // Continuity Check: আসিম্পটোট (যেমন x = tan(y)) এ বিশাল জাম্প হলে পাথ ব্রেক করবে
                double prevMathY = (cy - (screenY - step)) / appState.getScale();
                expr.setVariable("y", prevMathY);
                double prevMathX = expr.evaluate();

                // যদি x এর মান স্ক্রিন উইডথ এর দ্বিগুণ বেশি লাফ দেয়, তবে ধরে নিতে হবে এটি ডিসকন্টিনিউয়াস
                if (Math.abs(mathX - prevMathX) > (width / appState.getScale()) * 2) {
                    gc.stroke();
                    gc.beginPath();
                    gc.moveTo(clampedX, screenY);
                } else {
                    gc.lineTo(clampedX, screenY);
                }
            }
        }
        gc.stroke();
    }

    private void plotImplicit(double cx, double cy, double width, double height, String eq) {
        gc.setLineWidth(2.0);
        String[] parts = eq.split("=");
        String expressionStr = (parts.length == 2) ? parts[0] + "-(" + parts[1] + ")" : eq;

        Expression expr = EquationHandler.buildImplicitExpression(expressionStr, appState.getGlobalVariables());

        int res = 4; // গ্রিড রেজোলিউশন
        for (double x = 0; x < width; x += res) {
            for (double y = 0; y < height; y += res) {
                double vBL = evaluate(expr, x, y + res, cx, cy, appState.getScale());
                double vBR = evaluate(expr, x + res, y + res, cx, cy, appState.getScale());
                double vTR = evaluate(expr, x + res, y, cx, cy, appState.getScale());
                double vTL = evaluate(expr, x, y, cx, cy, appState.getScale());

                // [FIX]: আসিম্পটোট ফিল্টার
                // যদি কোনো সেলের কর্নারে ভ্যালু অনেক বড় হয় (Infinity এর কাছাকাছি),
                // তবে সেই সেলে লাইন ড্র করা ইগনোর করবে যাতে ভুল কানেকশন না হয়।
                double limit = 1e10;
                if (Math.abs(vBL) > limit || Math.abs(vBR) > limit ||
                        Math.abs(vTR) > limit || Math.abs(vTL) > limit) continue;

                double[][][] hits = new double[4][2][];
                int hitCount = 0;

                // Linear Interpolation calculation
                if (isSignDifferent(vBL, vTL)) hits[hitCount++] = new double[][]{{x, y + res - (-vBL / (vTL - vBL) * res)}};
                if (isSignDifferent(vBL, vBR)) hits[hitCount++] = new double[][]{{x + (-vBL / (vBR - vBL) * res), y + res}};
                if (isSignDifferent(vBR, vTR)) hits[hitCount++] = new double[][]{{x + res, y + res - (-vBR / (vTR - vBR) * res)}};
                if (isSignDifferent(vTR, vTL)) hits[hitCount++] = new double[][]{{x + res - (-vTR / (vTL - vTR) * res), y}};

                if (hitCount == 2) {
                    gc.strokeLine(hits[0][0][0], hits[0][0][1], hits[1][0][0], hits[1][0][1]);
                } else if (hitCount == 4) {
                    // স্যাডল পয়েন্ট হ্যান্ডলিং
                    gc.strokeLine(hits[0][0][0], hits[0][0][1], hits[1][0][0], hits[1][0][1]);
                    gc.strokeLine(hits[2][0][0], hits[2][0][1], hits[3][0][0], hits[3][0][1]);
                }
            }
        }
    }

    private double evaluate(Expression expr, double screenX, double screenY, double cx, double cy, double scale) {
        double epsilon = 0.0001; // Avoid divide by zero error for implicit equations
        expr.setVariable("x", (screenX - cx + epsilon) / scale);
        expr.setVariable("y", (cy - screenY + epsilon) / scale);
        try { return expr.evaluate(); } catch (Exception e) { return Double.NaN; }
    }

    private boolean isSignDifferent(double v1, double v2) {
        if (Double.isNaN(v1) || Double.isNaN(v2)) return false;
        return (v1 > 0 && v2 <= 0) || (v1 <= 0 && v2 > 0);
    }
}