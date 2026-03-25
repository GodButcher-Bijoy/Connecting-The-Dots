package org.example;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EquationPreset: A named collection of equations that draw a specific shape.
 * Each equation carries its own individual color via EquationEntry.
 * A suggested zoom scale is also stored so the graph centers nicely on load.
 */
public class EquationPreset {

    private final String name;
    private final String description;
    private final List<EquationEntry> entries;
    private final double suggestedScale;

    // -------------------------------------------------------------------
    // Primary constructor: each equation has its own specific color
    // -------------------------------------------------------------------
    public EquationPreset(String name, String description, double suggestedScale,
                          EquationEntry... entries) {
        this.name = name;
        this.description = description;
        this.suggestedScale = suggestedScale;
        this.entries = Arrays.asList(entries);
    }

    // -------------------------------------------------------------------
    // Backward-compatible constructor: plain strings → default purple
    // Used by any code that has not yet switched to EquationEntry.
    // -------------------------------------------------------------------
    public EquationPreset(String name, String description, double suggestedScale,
                          String... equations) {
        this.name = name;
        this.description = description;
        this.suggestedScale = suggestedScale;
        this.entries = new ArrayList<>();
        for (String eq : equations) {
            this.entries.add(new EquationEntry(eq, Color.web("#9D00FF")));
        }
    }

    // Backward-compat without explicit scale
    public EquationPreset(String name, String description, String... equations) {
        this(name, description, 50.0, equations);
    }

    // -------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------
    public String getName()                 { return name; }
    public String getDescription()          { return description; }
    public List<EquationEntry> getEntries() { return entries; }
    public double getSuggestedScale()       { return suggestedScale; }

    /** Convenience: just the equation strings (for backward compatibility). */
    public List<String> getEquations() {
        List<String> result = new ArrayList<>(entries.size());
        for (EquationEntry e : entries) result.add(e.getEquation());
        return result;
    }

    /** Convenience: just the colors in entry order. */
    public List<Color> getColors() {
        List<Color> result = new ArrayList<>(entries.size());
        for (EquationEntry e : entries) result.add(e.getColor());
        return result;
    }

    @Override
    public String toString() { return name; }
}
