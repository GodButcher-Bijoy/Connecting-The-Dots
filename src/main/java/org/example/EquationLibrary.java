package org.example;

import java.util.Arrays;
import java.util.List;

/**
 * EquationLibrary: Central manager that holds every equation category.
 *
 * Usage:
 *   EquationLibrary lib = new EquationLibrary();
 *   for (EquationCategory cat : lib.getCategories()) { ... }
 *
 * To add a new category, create a class that extends EquationCategory,
 * then add it to the list returned by buildCategories().
 */
public class EquationLibrary {

    private final List<EquationCategory> categories;

    public EquationLibrary() {
        this.categories = buildCategories();
    }

    private List<EquationCategory> buildCategories() {
        return Arrays.asList(
                new CartoonEquations(),
                new CollegeEquations(),
                new NatureEquations(),
                new ArtEquations(),
                new CoolShapesEquations()
        );
    }

    /** All registered categories (in display order). */
    public List<EquationCategory> getCategories() {
        return categories;
    }

    /**
     * Convenience: look up a category by name (case-insensitive).
     * Returns null if not found.
     */
    public EquationCategory getCategoryByName(String name) {
        return categories.stream()
                .filter(c -> c.getCategoryName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Convenience: look up a preset anywhere in the library.
     * Returns null if not found.
     */
    public EquationPreset getPresetByName(String name) {
        return categories.stream()
                .flatMap(c -> c.getPresets().stream())
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
