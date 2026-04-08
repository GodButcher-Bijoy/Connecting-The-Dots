package org.example;

import javafx.scene.paint.Color;

/**
 * EquationEntry: A single equation string paired with its own specific Color.
 * Used inside EquationPreset so every equation in a preset can have an
 * individually chosen color instead of being assigned from a palette.
 */
public class EquationEntry {

    private final String equation;
    private final Color color;

    public EquationEntry(String equation, Color color) {
        this.equation = equation;
        this.color = color;
    }

    /** Convenience factory — cleaner to write in long preset lists. */
    public static EquationEntry of(String equation, String hexColor) {
        return new EquationEntry(equation, Color.web(hexColor));
    }

    public String getEquation() { return equation; }
    public Color  getColor()    { return color; }

    @Override
    public String toString() {
        return equation;
    }
}
