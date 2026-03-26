package org.example;

import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.List;

/**
 * CollegeEquations: College / university abbreviations drawn with math equations.
 * Every letter is constructed from line-segment equations (with boundary conditions)
 * and semicircles so they fit neatly on the standard graph view.
 *
 * Each college uses its real institutional color(s). Every equation in a preset
 * carries its own individually assigned color via EquationEntry.
 *
 * Letter geometry uses a 3-unit-tall, 2-unit-wide grid per character,
 * separated by 1-unit gaps. Characters are centred as a group on the origin.
 */
public class CollegeEquations extends EquationCategory {

    private static final Color[] PALETTE = {
            Color.web("#003087"), Color.web("#FFC72C"), Color.web("#8B0000"),
            Color.web("#1A5276"), Color.web("#117A65"), Color.web("#6C3483"),
            Color.web("#A93226"), Color.web("#1F618D"),
    };

    @Override public String getCategoryName() { return "College"; }
    @Override public String getCategoryEmoji() { return "🎓"; }
    @Override public Color[] getColors()       { return PALETTE; }

    @Override
    public List<EquationPreset> getPresets() {
        return Arrays.asList(
                ndc(), hcc(), vnc(), mit(), iit(), buet(), du()
        );
    }

    // =========================================================================
    // NDC — Navy Blue (institutional color)
    // =========================================================================
    private EquationPreset ndc() {
        return new EquationPreset(
                "NDC", "NDC college sign drawn with equations", 35.0,
                // --- N ---
                EquationEntry.of("y = 0 {-3 <= x <= 3}",            "#fca90f"),
                EquationEntry.of("y = 0.5 {-3 <= x <= 3}",      "#fca90f"),
                EquationEntry.of("3>=x >= -3 {0 <= y <= 0.5}",            "#fca90f"),
                // --- D ---
                EquationEntry.of("x = 3 {0 <= y <= 0.5}",            "#fca90f"),
                EquationEntry.of("y = -7*x - 20.5 {-3.5 <= x <= -3}",       "#fca90f"),
                // --- C ---
                EquationEntry.of("y = 7*x - 20.5 {3 <= x <= 3.5}",        "#fca90f"),
                EquationEntry.of(".5<=y <= -x + 0.5 {-3.5 <= x <= -1.5,y >= -7*x - 20.5}",        "#fca90f"),
                EquationEntry.of(".5<=y <= x + 0.5 {1.5 <= x <= 3.5, y >= 7*x - 20.5 }",        "#fca90f"),
                EquationEntry.of("0.5<=y <= 2*x + 5 {-1.5 <= x <= 0}",        "#fca90f"),
                EquationEntry.of("0.5<=y <= -2*x + 5 {0 <= x <= 1.5}",        "#fca90f"),
                EquationEntry.of("x^2 + (y - 5.3)^2 <= 0.1",        "#fca90f"),
                EquationEntry.of("(x + 3.5)^2 + (y - 4.3)^2 <= 0.1",        "#fca90f"),
                EquationEntry.of("(x - 3.5)^2 + (y - 4.3)^2 <= 0.1",        "#fca90f"),
                EquationEntry.of("x^2 + (y - 0.25)^2 <= 0.04",        "#54bf41"),
                EquationEntry.of("(x - 1.5)^2 + (y - 0.25)^2 <= 0.04",        "#0000ff"),
                EquationEntry.of("(x + 1.5)^2 + (y - 0.25)^2 <= 0.04",        "#0000ff"),
                EquationEntry.of("x^2/.25+(y-2)^2/.5 <= 1",        "#ff0000")
        );
    }

    // =========================================================================
    // HCC — Maroon (classic college maroon)
    // =========================================================================
    private EquationPreset hcc() {
        return new EquationPreset(
                "HCC", "HCC college sign drawn with equations", 35.0,
                // --- H ---
                EquationEntry.of("x=-8{-1.5<=y<=1.5}",            "#8B0000"),
                EquationEntry.of("y=0{-8<=x<=-6}",                "#A52A2A"),
                EquationEntry.of("x=-6{-1.5<=y<=1.5}",            "#8B0000"),
                // --- C1 ---
                EquationEntry.of("x^2+y^2=2.25{x<=0}",            "#C0392B"),
                // --- C2 ---
                EquationEntry.of("(x-7)^2+y^2=2.25{x<=7}",        "#C0392B")
        );
    }

    // =========================================================================
    // VNC — Forest Green
    // =========================================================================
    private EquationPreset vnc() {
        return new EquationPreset(
                "VNC", "VNC college sign drawn with equations", 20.0,
                EquationEntry.of("y=-x+16{12<y<13}",            "#27AE60"),
                EquationEntry.of("x=4{8<y<12}",            "#27AE60"),
                EquationEntry.of("y=-x+6{1<x<2}",             "#27AE60"),
                EquationEntry.of("y=x+14{11<y<13}",        "#27AE60"),
                EquationEntry.of("y=13{-1<x<3}",        "#27AE60"),
                EquationEntry.of("y=x+4{1<x<4}",        "#27AE60"),
                EquationEntry.of("x=-2{4<y<6}",        "#27AE60"),
                EquationEntry.of("y=x+8{-2<x<1}",        "#27AE60"),
                EquationEntry.of("y=10{-2<x<1}",        "#27AE60"),
                EquationEntry.of("y=-x+8{10<y<11}",        "#27AE60"),
                EquationEntry.of("x=1{9<y<10}",        "#27AE60"),
                EquationEntry.of("(x-1)^2/.75+(y-11.5)^2/.25<=1",        "#f74402"),
                EquationEntry.of("(x-1)^2/.03+(y-11.5)^2/.25<=1",        "#000000"),
                EquationEntry.of("x^2/33 + (y-3.8)^2/3.25 = 1 {y <= 5.5}","#27AE60"),
                EquationEntry.of("x^2/50 + (y-1.5)^2/5 = 1 {y <= 3}",        "#27AE60"),
                EquationEntry.of("x^2/68 + (y+1)^2/5= 1 {y <= .5}",        "#27AE60"),
                EquationEntry.of("y=11{-4<x<-3}",        "#000000"),
                EquationEntry.of("y=-x+7{-4.5<x<-4}",        "#000000"),
                EquationEntry.of("y=x+15{-4.5<x<-4}",        "#000000")
        );
    }

    // =========================================================================
    // MIT — Cardinal Red (MIT's actual color #A31F34)
    // =========================================================================
    private EquationPreset mit() {
        return new EquationPreset(
                "MIT", "MIT drawn with line-segment equations", 35.0,
                // --- M ---
                EquationEntry.of("x=-9{-1.5<=y<=1.5}",            "#A31F34"),
                EquationEntry.of("y=-1.5*x-12{-9<=x<=-8}",        "#A31F34"),
                EquationEntry.of("y=1.5*x+12{-8<=x<=-7}",         "#A31F34"),
                EquationEntry.of("x=-7{-1.5<=y<=1.5}",            "#A31F34"),
                // --- I ---
                EquationEntry.of("x=0{-1.5<=y<=1.5}",             "#C0392B"),
                EquationEntry.of("y=1.5{-0.5<=x<=0.5}",           "#C0392B"),
                EquationEntry.of("y=-1.5{-0.5<=x<=0.5}",          "#C0392B"),
                // --- T ---
                EquationEntry.of("y=1.5{5<=x<=9}",                "#E74C3C"),
                EquationEntry.of("x=7{-1.5<=y<=1.5}",             "#E74C3C")
        );
    }

    // =========================================================================
    // IIT — Deep Blue (IIT blue #003366)
    // =========================================================================
    private EquationPreset iit() {
        return new EquationPreset(
                "IIT", "IIT drawn with line-segment equations", 35.0,
                // --- I1 ---
                EquationEntry.of("x=-7{-1.5<=y<=1.5}",            "#003366"),
                EquationEntry.of("y=1.5{-7.5<=x<=-6.5}",          "#003366"),
                EquationEntry.of("y=-1.5{-7.5<=x<=-6.5}",         "#003366"),
                // --- I2 ---
                EquationEntry.of("x=0{-1.5<=y<=1.5}",             "#1A5276"),
                EquationEntry.of("y=1.5{-0.5<=x<=0.5}",           "#1A5276"),
                EquationEntry.of("y=-1.5{-0.5<=x<=0.5}",          "#1A5276"),
                // --- T ---
                EquationEntry.of("y=1.5{5<=x<=9}",                "#2E86C1"),
                EquationEntry.of("x=7{-1.5<=y<=1.5}",             "#2E86C1")
        );
    }

    // =========================================================================
    // BUET — Engineering Blue-Green (#00695C)
    // =========================================================================
    private EquationPreset buet() {
        return new EquationPreset(
                "BUET", "BUET drawn with equations", 28.0,
                // --- B ---
                EquationEntry.of("x=-11{-1.5<=y<=1.5}",                          "#00695C"),
                EquationEntry.of("(x+11)^2+(y-0.75)^2=0.5625{x>=-11,y>=0}",     "#00695C"),
                EquationEntry.of("(x+11)^2+(y+0.75)^2=0.5625{x>=-11,y<=0}",     "#00695C"),
                EquationEntry.of("y=0{-11<=x<=-10.25}",                           "#00695C"),
                // --- U ---
                EquationEntry.of("x=-5{0<=y<=1.5}",                               "#00897B"),
                EquationEntry.of("x=-3{0<=y<=1.5}",                               "#00897B"),
                EquationEntry.of("(x+4)^2+y^2=1{y<=0}",                           "#00897B"),
                // --- E ---
                EquationEntry.of("x=1{-1.5<=y<=1.5}",                             "#26A69A"),
                EquationEntry.of("y=1.5{1<=x<=3}",                                "#26A69A"),
                EquationEntry.of("y=0{1<=x<=2.5}",                                "#26A69A"),
                EquationEntry.of("y=-1.5{1<=x<=3}",                               "#26A69A"),
                // --- T ---
                EquationEntry.of("y=1.5{5<=x<=9}",                                "#4DB6AC"),
                EquationEntry.of("x=7{-1.5<=y<=1.5}",                             "#4DB6AC")
        );
    }

    // =========================================================================
    // DU — Dhaka University Green (#006400) and Gold (#DAA520)
    // =========================================================================
    private EquationPreset du() {
        return new EquationPreset(
                "DU", "DU (Dhaka University) drawn with equations", 50.0,
                // --- D ---
                EquationEntry.of("x=-3{-1.5<=y<=1.5}",            "#006400"),  // bar (green)
                EquationEntry.of("(x+3)^2+y^2=2.25{x>=-3}",       "#006400"),  // arc (green)
                // --- U ---
                EquationEntry.of("x=3{0<=y<=1.5}",                "#DAA520"),  // left bar (gold)
                EquationEntry.of("x=5{0<=y<=1.5}",                "#DAA520"),  // right bar (gold)
                EquationEntry.of("(x-4)^2+y^2=1{y<=0}",           "#DAA520")   // bottom arc (gold)
        );
    }
}
