package org.example;

import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.List;

/**
 * CoolShapesEquations: Geometric and abstract cool shapes.
 * Every equation carries its own individually chosen color via EquationEntry.
 * Palette theme: electric neon — maximum contrast and visual impact.
 */
public class CoolShapesEquations extends EquationCategory {

    private static final Color[] PALETTE = {
            Color.web("#FF1493"), Color.web("#00BFFF"), Color.web("#39FF14"),
            Color.web("#FF6600"), Color.web("#FF00FF"), Color.web("#FFD700"),
            Color.web("#00FFFF"), Color.web("#FF4500"),
    };

    @Override public String getCategoryName() { return "Cool Shapes"; }
    @Override public String getCategoryEmoji() { return "💎"; }
    @Override public Color[] getColors()       { return PALETTE; }

    @Override
    public List<EquationPreset> getPresets() {
        return Arrays.asList(
                star(),
                checkerboard(),
                sineWave()
        );
    }

    // 5-pointed star
    private EquationPreset star() {
        return new EquationPreset(
                "Star ⭐", "5-pointed star shape with inner pentagon", 55.0,
                EquationEntry.of("((5-3)*cos(t)+5*cos((5-3)/3*t),(5-3)*sin(t)-5*sin((5-3)/3*t))","#000000")
        );
    }

    // Checkerboard pattern (inequality shading)
    private EquationPreset checkerboard() {
        return new EquationPreset(
                "Checkerboard ♟", "Checkerboard shading via sin inequality", 35.0,
                EquationEntry.of("sin(pi*x)*sin(pi*y)>=0", "#000000")     // neon deep pink
        );
    }

    // Complex sine wave  y = sin(x) + 0.5·sin(3x) + 0.25·sin(5x)
    private EquationPreset sineWave() {
        return new EquationPreset(
                "Sine Wave 〰", "Rich harmonic sine wave (Fourier approximation of square wave)", 55.0,
                EquationEntry.of("y=sin(x)+0.5*sin(3*x)+0.25*sin(5*x)", "#6e151e")   // neon lime
        );
    }

}
