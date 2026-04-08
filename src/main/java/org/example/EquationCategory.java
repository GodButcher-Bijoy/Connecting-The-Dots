package org.example;

import javafx.scene.paint.Color;
import java.util.List;

/**
 * EquationCategory: Abstract base class for every equation category.
 * Each concrete subclass must implement getCategoryName(), getCategoryEmoji(),
 * getPresets(), AND getColors() — a fixed palette used for that category's graphs.
 */
public abstract class EquationCategory {

    /** Human-readable category name shown in the dropdown menu. */
    public abstract String getCategoryName();

    /** Emoji icon displayed next to the category name. */
    public abstract String getCategoryEmoji();

    /** All presets belonging to this category. */
    public abstract List<EquationPreset> getPresets();

    /**
     * Fixed color palette for this category.
     * When a preset from this category is loaded, each equation row is assigned
     * colors[i % colors.length] — no random cycling from AppState.
     */
    public abstract Color[] getColors();

    /** Full label shown on the menu: emoji + space + name. */
    public final String getMenuLabel() {
        return getCategoryEmoji() + "  " + getCategoryName();
    }
}
