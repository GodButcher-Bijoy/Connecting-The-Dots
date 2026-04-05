package org.example;

import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.List;

/**
 * ArtEquations: Famous mathematical art curves — Lissajous, spirographs, and more.
 * Every equation carries its own individually chosen color via EquationEntry.
 * Palette theme: deep jewel tones and sophisticated hues.
 */
public class ArtEquations extends EquationCategory {

    private static final Color[] PALETTE = {
            Color.web("#4B0082"), Color.web("#008080"), Color.web("#9400D3"),
            Color.web("#B8860B"), Color.web("#2F4F4F"), Color.web("#8B008B"),
            Color.web("#483D8B"), Color.web("#006400"),
    };

    @Override public String getCategoryName() { return "Math Art"; }
    @Override public String getCategoryEmoji() { return "🎨"; }
    @Override public Color[] getColors()       { return PALETTE; }

    @Override
    public List<EquationPreset> getPresets() {
        return Arrays.asList(
                lemniscate(),
                lissajous(),
                spirograph(),
                Pumpkin(),
                Among_Us(),
                Yin_Yang_simble(),
                hypotrochoid()
        );
    }

    // Lemniscate of Bernoulli  (x²+y²)² = 2(x²−y²)
    private EquationPreset lemniscate() {
        return new EquationPreset(
                "Lemniscate ∞", "Lemniscate of Bernoulli: (x²+y²)² = 2(x²−y²)", 60.0,
                EquationEntry.of("(x^2+y^2)^2=2*(x^2-y^2)", "#4B0082")   // deep indigo
        );
    }

    // Lissajous figure (3:2 ratio)
    private EquationPreset lissajous() {
        return new EquationPreset(
                "Lissajous 🎵", "Lissajous figure with 3:2 frequency ratio", 95.0,
                EquationEntry.of("(sin(3*t),sin(2*t))", "#008080")         // deep teal
        );
    }

    // Spirograph  R=8, r=1  →  7-pointed star
    private EquationPreset spirograph() {
        return new EquationPreset(
                "Spirograph 🌀", "Spirograph (hypotrochoid) R=8, r=1", 12.0,
                EquationEntry.of("(7*cos(t)+cos(7*t),7*sin(t)-sin(7*t))", "#9400D3")  // vivid violet
        );
    }

    private EquationPreset Pumpkin() {
        return new EquationPreset("Halloween Pumpkin", "Halloween Pumpkin", 40.0,
                EquationEntry.of("x²/9+y²/16 = 1{-1.5<x<1.5,y<0}", "#ff9500"),
                EquationEntry.of("x²/9+y²/16 = 1{-2.2<x<2.2,y>0}", "#ff9500"),
                EquationEntry.of("(x-2)²/9+y²/16 = 1{x>1}", "#ff9500"),
                EquationEntry.of("(x+2)²/9+y²/16 = 1{x<-1}", "#ff9500"),
                EquationEntry.of("y = 10(x+1)²+4{-1<x<-.5}", "#00ff00"),
                EquationEntry.of("y = 10(-x+1)²+4{.5<x<1}", "#00ff00"),
                EquationEntry.of("y=6.5{-.5<x<.5}", "#00ff00"),
                EquationEntry.of("y-1 ≤ x + 2.5{-2.5≤x≤-1.5,y-0.5 ≥(x + 1)/ -3}", "#000000"),
                EquationEntry.of("y-0.5 ≤ -3(x + 1){-1.5≤x≤-1,y-0.5 ≥(x + 1)/ -3}", "#000000"),
                EquationEntry.of("y-0.5 =(x + 1)/ -3{-2.5≤x≤-1}", "#000000"),
                EquationEntry.of("y-1 ≤- x + 2.5{1.5≤x≤2.5,y-0.5 ≥(-x + 1)/ -3}", "#000000"),
                EquationEntry.of("y-0.5 ≤ -3(-x + 1){1≤x≤1.5,y-0.5 ≥(-x + 1)/ -3}", "#000000"),
                EquationEntry.of("y-0.5 =(-x + 1)/ -3{1≤x≤2.5}", "#000000"),
                EquationEntry.of("y ≤ 0.08x²-0.5{-2.5≤x≤2.5,y ≥ 0.4x²-2.5}", "#000000"),
                EquationEntry.of("y = 0.4x²-2.5{-2.5≤x≤2.5}", "#000000"),
                EquationEntry.of("x = .5{-1≤y≤-0.5}", "#ffffff"),
                EquationEntry.of("-x = .5{-1≤y≤-0.5}", "#ffffff"),
                EquationEntry.of("x =1{-1≤y≤-0.5}", "#ffffff"),
                EquationEntry.of("-x =1{-1≤y≤-0.5}", "#ffffff"),
                EquationEntry.of("y ≥ -1{0.5<x<1,y ≤ 0.08x²-0.5}", "#ffffff"),
                EquationEntry.of("y ≥-1{-1<x<-0.5,y ≤ 0.08x²-0.5}", "#ffffff")
        );
    }

    private EquationPreset Among_Us() {
        return new EquationPreset(
                "ANGEL HALO ", "AMONG US ANGEL HALO Character", 40.0,
                EquationEntry.of("(x-.3)²/16+(y-1.1)²/30=1{-2.5<x<3.736,y>3}", "#0000ff"),
                EquationEntry.of("(x-.4)²/12+(y+0.5)²/100=1{-4.235<y<5.03,x<-2}", "#0000ff"),
                EquationEntry.of("(x+1.4)²/2+(y+4.3)²/5=1{y<-4}", "#0000ff"),
                EquationEntry.of("(x-2.4)²/1.7+(y+3.7)²/5=1{y<-3.538, 1.12<x}", "#0000ff"),
                EquationEntry.of("(x-1)²/7+(y+1.9)²/5=1{0<x<2.3,y<-3}", "#0000ff"),
                EquationEntry.of("(x-1.8)²/7.3+(y-2.6)²/3.5<1", "#0000ff"),
                EquationEntry.of("(x+2.6)²/5+(y+.4)²/10=1{-2.87>x}", "#0000ff"),
                EquationEntry.of("(x-.1)²/9+(y-8.53)²/1≤1{(x-.1)²/4+(y-8.6)²/.2>1}", "#ffa408"),
                EquationEntry.of("(x-.1)²/4+(y-8.6)²/.2=1", "#ffa408"),
                EquationEntry.of("(x-.4)²/12+(y+.5)²/100=1{-3.6<y<1.3,x>2}", "#0000ff"),
                EquationEntry.of("(x-.9)²/16+(y-3.3)²/40=1{-2.976<x<3.861,y<4}", "#0000ff")
        );
    }

    // Serpentine  y = x/(x²+1)
    private EquationPreset Yin_Yang_simble() {
        return new EquationPreset(
                "Yin_Yang_simble ࿊", "Yin_Yang_simble curve)", 55.0,
                EquationEntry.of("4.74^2 >= (x-0)^2 + (y-0)^2{2.37^2 <= (x-0)^2 + (y-2.37)^2,0<=x,.6^2 <= (x-0)^2 + (y+2.4)^2}", "#000000"),
                EquationEntry.of(".6^2 >= (x-0)^2 + (y-2.4)^2", "#000000"),
                EquationEntry.of(".6^2 = (x-0)^2 + (y+2.4)^2", "#000000"),
                EquationEntry.of("2.37^2 = (x-0)^2 + (y-2.37)^2{x>0}", "#000000"),
                EquationEntry.of("2.37^2 = (x-0)^2 + (y+2.37)^2{x<0}", "#000000"),
                EquationEntry.of("4.74^2 >= (x-0)^2 + (y-0)^2{2.37^2 >= (x-0)^2 + (y+2.37)^2,-2.37<=x<=0,.6^2 <= (x-0)^2 + (y+2.4)^2}", "#000000"),
                EquationEntry.of("4.74^2 = (x-0)^2 + (y-0)^2", "#000000")
        );
    }

    // Hypotrochoid  R=5, r=3, d=5
    private EquationPreset hypotrochoid() {
        return new EquationPreset(
                "Hypotrochoid 🌟", "Hypotrochoid: R=5, r=3, d=5", 15.0,
                EquationEntry.of("((5-3)*cos(t)+5*cos((5-3)/3*t),(5-3)*sin(t)-5*sin((5-3)/3*t))", "#483D8B") // slate blue
        );
    }
}
