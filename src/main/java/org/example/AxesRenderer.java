package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AxesRenderer {
    private final AppState appState;
    private final GraphicsContext gc;

    public AxesRenderer(AppState appState, GraphicsContext gc) {
        this.appState = appState;
        this.gc = gc;
    }

    // --- Grid Lines Only ---
    public void drawSmartGridLines(double width, double height) {
        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();
        double scale = appState.getScale();

        Color majorColor = Color.web("#999999");
        Color minorColor = Color.web("#E0E0E0");

        double targetGridPixelWidth = 100;
        double minStep = (width / scale) * (targetGridPixelWidth / width);
        double magnitude = Math.pow(10, Math.floor(Math.log10(minStep)));
        double residual = minStep / magnitude;

        double majorStep;
        if (residual > 5) majorStep = 10 * magnitude;
        else if (residual > 2) majorStep = 5 * magnitude;
        else if (residual > 1) majorStep = 2 * magnitude;
        else majorStep = magnitude;

        int subdivisions = (Math.abs(majorStep / magnitude - 2) < 0.001) ? 4 : 5;
        double minorStep = majorStep / subdivisions;

        // Vertical Lines
        double startX = Math.floor(-centerX / scale / minorStep) * minorStep;
        for (double i = startX; i * scale + centerX < width; i += minorStep) {
            double screenX = centerX + (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor) { gc.setStroke(majorColor); gc.setLineWidth(1.0); }
            else { gc.setStroke(minorColor); gc.setLineWidth(0.7); }
            gc.strokeLine(screenX, 0, screenX, height);
        }

        // Horizontal Lines
        double startY = Math.floor((centerY - height) / scale / minorStep) * minorStep;
        double endY = Math.ceil(centerY / scale / minorStep) * minorStep;
        for (double i = startY; i <= endY; i += minorStep) {
            double screenY = centerY - (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor) { gc.setStroke(majorColor); gc.setLineWidth(1.0); }
            else { gc.setStroke(minorColor); gc.setLineWidth(0.7); }
            gc.strokeLine(0, screenY, width, screenY);
        }
    }

    public void drawAxes(double width, double height) {
        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(0, centerY, width, centerY);
        gc.strokeLine(centerX, 0, centerX, height);
    }

    // --- Number Labels (Drawn on top) ---
    public void drawGridLabels(double width, double height) {
        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();
        double scale = appState.getScale();

        gc.setFont(new Font("Arial", 12));
        Color textColor = Color.web("#444444");
        Color bgWhite = Color.web("#FFFFFF", 0.85);

        double targetGridPixelWidth = 100;
        double minStep = (width / scale) * (targetGridPixelWidth / width);
        double magnitude = Math.pow(10, Math.floor(Math.log10(minStep)));
        double residual = minStep / magnitude;

        double majorStep;
        if (residual > 5) majorStep = 10 * magnitude;
        else if (residual > 2) majorStep = 5 * magnitude;
        else if (residual > 1) majorStep = 2 * magnitude;
        else majorStep = magnitude;

        int subdivisions = (Math.abs(majorStep / magnitude - 2) < 0.001) ? 4 : 5;
        double minorStep = majorStep / subdivisions;

        // X-Axis Numbers
        double startX = Math.floor(-centerX / scale / minorStep) * minorStep;
        double labelY = Math.max(20, Math.min(height - 10, centerY + 20));

        for (double i = startX; i * scale + centerX < width; i += minorStep) {
            double screenX = centerX + (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor && Math.abs(i) > 0.0000001) { // precision adjust kora holo
                String label = formatNumber(i); // [FIX] Notun formatter use kora holo

                gc.setFill(bgWhite);
                gc.fillRect(screenX - 8, labelY - 11, label.length() * 8, 14);
                gc.setFill(textColor);
                gc.fillText(label, screenX - 6, labelY);
            }
        }

        // Y-Axis Numbers
        double startY = Math.floor((centerY - height) / scale / minorStep) * minorStep;
        double endY = Math.ceil(centerY / scale / minorStep) * minorStep;
        double labelX = Math.max(10, Math.min(width - 35, centerX + 8));

        for (double i = startY; i <= endY; i += minorStep) {
            double screenY = centerY - (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor && Math.abs(i) > 0.0000001) { // precision adjust kora holo
                String label = formatNumber(i); // [FIX] Notun formatter use kora holo

                gc.setFill(bgWhite);
                gc.fillRect(labelX - 2, screenY - 6, label.length() * 8, 14);
                gc.setFill(textColor);
                gc.fillText(label, labelX, screenY + 5);
            }
        }
        // --- Origin (0,0) Numbering (Desmos Style) ---
        // Center (intersection) jodi screen er vitore thake, tobei 0 draw korbe
        if (centerX >= 0 && centerX <= width && centerY >= 0 && centerY <= height) {
            String label = "0";

            // Ektu bame ar niche soriye draw korchi jate axis line er sathe overlap na hoy
            double zeroX = centerX - 12;
            double zeroY = centerY + 14;

            // Background shadow
            gc.setFill(bgWhite);
            gc.fillRect(zeroX - 2, zeroY - 11, 10, 14);

            // Draw '0'
            gc.setFill(textColor);
            gc.fillText(label, zeroX, zeroY);
        }
    }

    // [NEW] Smart Number Formatter to fix 0.0 bug
    private String formatNumber(double val) {
        // Precision theek korar jonno round kora
        double rounded = Math.round(val * 1e8) / 1e8;
        if (Math.abs(rounded - Math.round(rounded)) < 1e-9) {
            return String.valueOf((long) Math.round(rounded)); // Integer hole purno sonkha dekhabe
        }
        // Doshomik er por extra 0 gula kete dibe
        return String.format("%.5f", rounded).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}