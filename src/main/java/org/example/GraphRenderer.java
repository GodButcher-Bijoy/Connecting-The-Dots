package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import net.objecthunter.exp4j.Expression;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphRenderer {
    private final AppState appState;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final VBox functionContainer;

    // Helper classes
    private final AxesRenderer axesRenderer;
    private final FunctionPlotter functionPlotter;

    // For panning (dragging) the graph
    private double lastMouseX;
    private double lastMouseY;

    public GraphRenderer(AppState appState, Canvas canvas, VBox functionContainer) {
        this.appState = appState;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.functionContainer = functionContainer;

        this.axesRenderer = new AxesRenderer(appState, gc);
        this.functionPlotter = new FunctionPlotter(appState, gc);

        this.canvas.widthProperty().addListener(evt -> drawGraph());
        this.canvas.heightProperty().addListener(evt -> drawGraph());

        this.canvas.setOnScroll(this::handleZoom);

        this.canvas.setOnMousePressed(e -> {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            canvas.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });

        this.canvas.setOnMouseDragged(e -> {
            double deltaX = e.getX() - lastMouseX;
            double deltaY = e.getY() - lastMouseY;
            appState.setOffsetX(appState.getOffsetX() + deltaX);
            appState.setOffsetY(appState.getOffsetY() + deltaY);
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            drawGraph();
        });

        this.canvas.setOnMouseReleased(e -> canvas.setCursor(javafx.scene.Cursor.DEFAULT));

        this.canvas.setOnMouseClicked(e -> {
            double cx = canvas.getWidth() / 2.0 + appState.getOffsetX();
            double cy = canvas.getHeight() / 2.0 + appState.getOffsetY();
            double scale = appState.getScale();

            for (Point2D pinnedPoint : new java.util.ArrayList<>(appState.getPinnedPoints())) {
                double screenX = cx + pinnedPoint.getX() * scale;
                double screenY = cy - pinnedPoint.getY() * scale;
                if (Math.hypot(screenX - e.getX(), screenY - e.getY()) < 10) {
                    appState.togglePinnedPoint(pinnedPoint);
                    drawGraph();
                    return;
                }
            }

            for (Point2D tempPoint : appState.getTemporaryPoints()) {
                double screenX = cx + tempPoint.getX() * scale;
                double screenY = cy - tempPoint.getY() * scale;
                if (Math.hypot(screenX - e.getX(), screenY - e.getY()) < 10) {
                    appState.togglePinnedPoint(tempPoint);
                    drawGraph();
                    return;
                }
            }
        });
    }

    public void drawGraph() {
        if (canvas == null || gc == null) return;
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (width == 0 || height == 0) return;

        gc.clearRect(0, 0, width, height);

        axesRenderer.drawSmartGridLines(width, height);
        axesRenderer.drawAxes(width, height);

        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();

        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node;
                Color rowColor = (Color) mainRow.getUserData();
                StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                VBox fieldAndPrompt = (VBox) inputWrapper.getChildren().get(0);
                TextField inputBox = (TextField) fieldAndPrompt.getChildren().get(0);

                String equation = inputBox.getText();
                String eq = equation.trim().replace(" ", "");

                if (eq.startsWith("(") && eq.endsWith(")")) {
                    try {
                        String[] parts = eq.substring(1, eq.length() - 1).split(",");
                        if (parts.length == 2) {
                            double pX = EquationHandler.buildExpression(parts[0], "x", appState.getGlobalVariables()).evaluate();
                            double pY = EquationHandler.buildExpression(parts[1], "x", appState.getGlobalVariables()).evaluate();
                            double screenX = centerX + (pX * appState.getScale());
                            double screenY = centerY - (pY * appState.getScale());
                            gc.setFill(rowColor);
                            gc.fillOval(screenX - 5, screenY - 5, 10, 10);
                            gc.setFill(Color.BLACK);
                            gc.fillText("(" + formatNumber(pX) + ", " + formatNumber(pY) + ")", screenX + 10, screenY - 10);
                        }
                    } catch (Exception ex) {}
                } else {
                    functionPlotter.plotEquation(equation, rowColor, centerX, centerY, width, height);
                }
            }
        }

        axesRenderer.drawGridLabels(width, height);
        calculateTemporaryPoints(width, height, centerX, centerY);
        validatePinnedPoints();
        drawPoints(centerX, centerY);
    }

    private void handleZoom(ScrollEvent event) {
        event.consume();
        double zoomFactor = 1.1;
        double oldScale = appState.getScale();
        double newScale = oldScale;

        if (event.getDeltaY() > 0) newScale *= zoomFactor;
        else newScale /= zoomFactor;

        if (newScale < 0.000001) newScale = 0.000001;
        if (newScale > 10000000) newScale = 10000000;

        double mx = event.getX();
        double my = event.getY();
        double cx = canvas.getWidth() / 2.0;
        double cy = canvas.getHeight() / 2.0;

        double oldOffsetX = appState.getOffsetX();
        double oldOffsetY = appState.getOffsetY();

        double newOffsetX = mx - cx - (mx - cx - oldOffsetX) * (newScale / oldScale);
        double newOffsetY = my - cy - (my - cy - oldOffsetY) * (newScale / oldScale);

        appState.setScale(newScale);
        appState.setOffsetX(newOffsetX);
        appState.setOffsetY(newOffsetY);

        drawGraph();
    }

    private String formatNumber(double val) {
        double rounded = Math.round(val * 1e8) / 1e8;
        if (Math.abs(rounded - Math.round(rounded)) < 1e-9) return String.valueOf((long) Math.round(rounded));
        return String.format("%.5f", rounded).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    // --- INTERSECTION LOGIC ---
    private class EqWrapper {
        int type;
        Expression expr;

        EqWrapper(int axisType) { this.type = axisType; }

        EqWrapper(String eqStr, Map<String, Double> globals) {
            String eq = EquationHandler.formatEquation(eqStr);
            if (eq.startsWith("y=") && !eq.contains("x=")) { type = 0; expr = EquationHandler.buildExpression(eq.substring(2), "x", globals); }
            else if (eq.startsWith("x=") && !eq.contains("y=")) { type = 1; expr = EquationHandler.buildExpression(eq.substring(2), "y", globals); }
            else if (eq.contains("=")) { type = 2; String[] parts = eq.split("="); String expressionStr = (parts.length == 2) ? parts[0] + "-(" + parts[1] + ")" : eq; expr = EquationHandler.buildImplicitExpression(expressionStr, globals); }
            else { type = 0; expr = EquationHandler.buildExpression(eq, "x", globals); }
        }

        double evaluateDifference(double x, double y) {
            if (type == 3) return y;
            if (type == 4) return x;
            try {
                if (type == 0) { expr.setVariable("x", x); return y - expr.evaluate(); }
                else if (type == 1) { expr.setVariable("y", y); return x - expr.evaluate(); }
                else { expr.setVariable("x", x); expr.setVariable("y", y); return expr.evaluate(); }
            } catch (Exception e) { return Double.NaN; }
        }
    }

    private void addTempPoint(double x, double y) {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) return;
        Point2D p = new Point2D(x, y);
        double pixelThreshold = 10.0 / appState.getScale();
        for (Point2D existing : appState.getTemporaryPoints()) {
            if (existing.distance(p) < pixelThreshold) return;
        }
        appState.getTemporaryPoints().add(p);
    }

    private boolean crossesBox(EqWrapper eq, double xMin, double xMax, double yMin, double yMax) {
        if (eq == null) return false;
        double midX = (xMin + xMax) / 2.0; double midY = (yMin + yMax) / 2.0;
        double vBL = eq.evaluateDifference(xMin, yMin); double vBR = eq.evaluateDifference(xMax, yMin);
        double vTL = eq.evaluateDifference(xMin, yMax); double vTR = eq.evaluateDifference(xMax, yMax);
        double vC  = eq.evaluateDifference(midX, midY); double vT = eq.evaluateDifference(midX, yMax);
        double vB = eq.evaluateDifference(midX, yMin); double vL = eq.evaluateDifference(xMin, midY);
        double vR = eq.evaluateDifference(xMax, midY);

        boolean hasPos = vBL > 0 || vBR > 0 || vTL > 0 || vTR > 0 || vC > 0 || vT > 0 || vB > 0 || vL > 0 || vR > 0;
        boolean hasNeg = vBL < 0 || vBR < 0 || vTL < 0 || vTR < 0 || vC < 0 || vT < 0 || vB < 0 || vL < 0 || vR < 0;

        double epsilon = 1e-7;
        if (Math.abs(vBL) < epsilon || Math.abs(vBR) < epsilon || Math.abs(vTL) < epsilon || Math.abs(vTR) < epsilon || Math.abs(vC) < epsilon || Math.abs(vT) < epsilon || Math.abs(vB) < epsilon || Math.abs(vL) < epsilon || Math.abs(vR) < epsilon) return true;
        return hasPos && hasNeg;
    }

    private void refineBox(EqWrapper eq1, EqWrapper eq2, double x1, double x2, double y1, double y2, int depth) {
        if (depth > 12) {
            double midX = (x1 + x2) / 2.0; double midY = (y1 + y2) / 2.0;
            double val1 = eq1.evaluateDifference(midX, midY); double val2 = eq2.evaluateDifference(midX, midY);
            if (Math.abs(val1) < 1000.0 && Math.abs(val2) < 1000.0) addTempPoint(midX, midY);
            return;
        }
        double midX = (x1 + x2) / 2.0; double midY = (y1 + y2) / 2.0;
        if (crossesBox(eq1, x1, midX, y1, midY) && crossesBox(eq2, x1, midX, y1, midY)) refineBox(eq1, eq2, x1, midX, y1, midY, depth + 1);
        if (crossesBox(eq1, midX, x2, y1, midY) && crossesBox(eq2, midX, x2, y1, midY)) refineBox(eq1, eq2, midX, x2, y1, midY, depth + 1);
        if (crossesBox(eq1, x1, midX, midY, y2) && crossesBox(eq2, x1, midX, midY, y2)) refineBox(eq1, eq2, x1, midX, midY, y2, depth + 1);
        if (crossesBox(eq1, midX, x2, midY, y2) && crossesBox(eq2, midX, x2, midY, y2)) refineBox(eq1, eq2, midX, x2, midY, y2, depth + 1);
    }

    private void calculateTemporaryPoints(double width, double height, double cx, double cy) {
        appState.getTemporaryPoints().clear();
        int focusedIdx = appState.getFocusedEquationIndex();
        if (focusedIdx == -1) return;

        List<EqWrapper> otherExprs = new ArrayList<>();
        EqWrapper focusedEq = null;

        int index = 0;
        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node; StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                VBox fieldAndPrompt = (VBox) inputWrapper.getChildren().get(0); TextField inputBox = (TextField) fieldAndPrompt.getChildren().get(0);
                String eq = inputBox.getText();
                if (!eq.trim().isEmpty() && !(eq.startsWith("(") && eq.endsWith(")"))) {
                    try {
                        EqWrapper wrapper = new EqWrapper(eq, appState.getGlobalVariables());
                        if (index == focusedIdx) focusedEq = wrapper; else otherExprs.add(wrapper);
                    } catch (Exception ignored) {}
                }
            }
            index++;
        }
        if (focusedEq == null) return;
        otherExprs.add(new EqWrapper(3)); otherExprs.add(new EqWrapper(4));

        double scale = appState.getScale(); double startX = -cx / scale; double endX = (width - cx) / scale;
        double startY = (cy - height) / scale; double endY = cy / scale;
        double step = 10.0 / scale;

        for (double x = startX; x < endX; x += step) {
            for (double y = startY; y < endY; y += step) {
                if (crossesBox(focusedEq, x, x + step, y, y + step)) {
                    for (EqWrapper other : otherExprs) {
                        if (crossesBox(other, x, x + step, y, y + step)) refineBox(focusedEq, other, x, x + step, y, y + step, 0);
                    }
                }
            }
        }
    }

    private void validatePinnedPoints() {
        if (appState.getPinnedPoints().isEmpty()) return;
        List<EqWrapper> allExprs = new ArrayList<>();
        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node; StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                VBox fieldAndPrompt = (VBox) inputWrapper.getChildren().get(0); TextField inputBox = (TextField) fieldAndPrompt.getChildren().get(0);
                String eq = inputBox.getText();
                if (!eq.trim().isEmpty() && !(eq.startsWith("(") && eq.endsWith(")"))) {
                    try { allExprs.add(new EqWrapper(eq, appState.getGlobalVariables())); } catch (Exception ignored) {}
                }
            }
        }
        allExprs.add(new EqWrapper(3)); allExprs.add(new EqWrapper(4));
        double threshold = 5.0 / appState.getScale();
        appState.getPinnedPoints().removeIf(p -> {
            int crossingCount = 0;
            for (EqWrapper eq : allExprs) {
                if (crossesBox(eq, p.getX() - threshold, p.getX() + threshold, p.getY() - threshold, p.getY() + threshold)) crossingCount++;
            }
            return crossingCount < 2;
        });
    }

    private void drawPoints(double cx, double cy) {
        double scale = appState.getScale();
        gc.setFill(Color.web("#888888", 0.7));
        for (Point2D p : appState.getTemporaryPoints()) {
            double screenX = cx + p.getX() * scale; double screenY = cy - p.getY() * scale;
            gc.fillOval(screenX - 4, screenY - 4, 8, 8); gc.setStroke(Color.WHITE); gc.setLineWidth(1); gc.strokeOval(screenX - 4, screenY - 4, 8, 8);
        }
        gc.setFont(Font.font("Arial", 12));
        for (Point2D p : appState.getPinnedPoints()) {
            double screenX = cx + p.getX() * scale; double screenY = cy - p.getY() * scale;
            gc.setFill(Color.web("#007AFF")); gc.fillOval(screenX - 5, screenY - 5, 10, 10);
            gc.setStroke(Color.WHITE); gc.strokeOval(screenX - 5, screenY - 5, 10, 10);
            String label = String.format("(%.2f, %.2f)", p.getX(), p.getY());
            gc.setFill(Color.web("#222222", 0.8)); gc.fillRoundRect(screenX + 12, screenY - 20, 80, 22, 5, 5);
            gc.setFill(Color.WHITE); gc.fillText(label, screenX + 15, screenY - 4);
        }
    }
}