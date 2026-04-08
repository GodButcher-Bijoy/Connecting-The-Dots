package org.example;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.util.ArrayList;
import java.util.List;

/**
 * MathRenderer: Converts a raw equation string into a styled JavaFX TextFlow
 * that shows superscripts, symbols, and cleaned-up notation — purely cosmetic.
 *
 * The original TextField text is never touched; this is display-only.
 *
 * Supported transformations:
 *  x^2        →  x²  (superscript, any length after ^ or ^(...))
 *  sqrt(x)    →  √(x)
 *  abs(x)     →  |x|
 *  pi         →  π
 *  <=         →  ≤
 *  >=         →  ≥
 *  *          →  · (middle dot)
 *  sin/cos/tan etc. → rendered upright, argument in parens
 *  log(x)     →  log(x)
 *  {cond}     →  dimmed small text appended at the end
 */
public class MathRenderer {

    private static final double BASE = 13.5;

    private static final String[] NAMED_FUNCS = {
            "asin", "acos", "atan",
            "sinh", "cosh", "tanh",
            "sin", "cos", "tan",
            "sec", "csc", "cot",
            "cosec", "log", "exp", "ln", "cbrt"
    };

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Updates a TextFlow in-place with a rendered version of the equation string.
     * Call this every time the displayed equation changes.
     *
     * @param flow   The TextFlow to populate (its children will be cleared first)
     * @param raw    The raw equation string from the TextField
     * @param color  Base text color (usually Color.WHITE for dark theme)
     */
    public static void update(TextFlow flow, String raw, Color color) {
        flow.getChildren().clear();
        if (raw == null || raw.trim().isEmpty()) return;

        String eq = raw.trim();

        // Split off boundary condition { ... } — show it dimmed at the end
        int braceAt = findOpenBrace(eq);
        String condPart = "";
        if (braceAt > 0) {
            condPart = "  " + eq.substring(braceAt);
            eq = eq.substring(0, braceAt).trim();
        }

        flow.getChildren().addAll(parse(eq, color, BASE));

        if (!condPart.isEmpty()) {
            Color dimColor = Color.color(
                    color.getRed(), color.getGreen(), color.getBlue(), 0.4);
            flow.getChildren().add(txt(condPart, dimColor, BASE * 0.78));
        }
    }

    // ── Core recursive parser ─────────────────────────────────────────────────

    private static List<Node> parse(String s, Color c, double sz) {
        List<Node> out = new ArrayList<>();
        int i = 0;

        while (i < s.length()) {

            // ── sqrt( ────────────────────────────────────────────────────────
            if (regionMatch(s, i, "sqrt(")) {
                int[] p = findClose(s, i + 4);
                if (p != null) {
                    out.add(txt("√", c, sz * 1.05));
                    out.add(txt("(", c, sz * 0.88));
                    out.addAll(parse(s.substring(p[0], p[1]), c, sz * 0.88));
                    out.add(txt(")", c, sz * 0.88));
                    i = p[1] + 1;
                    continue;
                }
            }

            // ── abs( ─────────────────────────────────────────────────────────
            if (regionMatch(s, i, "abs(")) {
                int[] p = findClose(s, i + 3);
                if (p != null) {
                    out.add(txt("|", c, sz));
                    out.addAll(parse(s.substring(p[0], p[1]), c, sz));
                    out.add(txt("|", c, sz));
                    i = p[1] + 1;
                    continue;
                }
            }

            // ── named functions (sin, cos, log, exp …) ──────────────────────
            String fn = matchNamedFunc(s, i);
            if (fn != null) {
                int fnLen = fn.length(); // e.g. "sin" → 3
                int[] p = findClose(s, i + fnLen); // points to opening '('
                if (p != null) {
                    // render function name upright
                    out.add(txtItalic(fn, c, sz));
                    out.add(txt("(", c, sz));
                    out.addAll(parse(s.substring(p[0], p[1]), c, sz));
                    out.add(txt(")", c, sz));
                    i = p[1] + 1;
                    continue;
                }
            }

            // ── pi → π ───────────────────────────────────────────────────────
            if (regionMatch(s, i, "pi") && !isAlpha(s, i + 2)) {
                out.add(txt("π", c, sz));
                i += 2;
                continue;
            }

            // ── <= → ≤ ───────────────────────────────────────────────────────
            if (regionMatch(s, i, "<=")) {
                out.add(txt(" ≤ ", c, sz));
                i += 2;
                continue;
            }

            // ── >= → ≥ ───────────────────────────────────────────────────────
            if (regionMatch(s, i, ">=")) {
                out.add(txt(" ≥ ", c, sz));
                i += 2;
                continue;
            }

            // ── ^ superscript ────────────────────────────────────────────────
            if (s.charAt(i) == '^') {
                i++;
                if (i < s.length()) {
                    if (s.charAt(i) == '(') {
                        // Multi-character superscript: ^(expr)
                        int[] p = findClose(s, i);
                        if (p != null) {
                            List<Node> supNodes = parse(s.substring(p[0], p[1]), c, sz * 0.68);
                            supNodes.forEach(n -> { if (n instanceof Text) ((Text) n).setTranslateY(-sz * 0.42); });
                            out.addAll(supNodes);
                            i = p[1] + 1;
                            continue;
                        }
                    } else {
                        // Single-character superscript: ^2, ^n, etc.
                        Text sup = txt(String.valueOf(s.charAt(i)), c, sz * 0.68);
                        sup.setTranslateY(-sz * 0.42);
                        out.add(sup);
                        i++;
                        continue;
                    }
                }
                continue; // bare ^ with nothing after it
            }

            // ── * → · (center dot) ───────────────────────────────────────────
            if (s.charAt(i) == '*') {
                out.add(txt("·", c, sz));
                i++;
                continue;
            }

            // ── ( ... ) — recurse to keep grouping intact ────────────────────
            if (s.charAt(i) == '(') {
                int[] p = findClose(s, i);
                if (p != null) {
                    out.add(txt("(", c, sz));
                    out.addAll(parse(s.substring(p[0], p[1]), c, sz));
                    out.add(txt(")", c, sz));
                    i = p[1] + 1;
                    continue;
                }
            }

            // ── default: emit character as-is ────────────────────────────────
            out.add(txt(String.valueOf(s.charAt(i)), c, sz));
            i++;
        }

        return out;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Finds the matching closing ')' for the '(' at openAt.
     * Returns [contentStart, contentEnd] (both exclusive of the parens).
     */
    private static int[] findClose(String s, int openAt) {
        if (openAt >= s.length() || s.charAt(openAt) != '(') return null;
        int depth = 0;
        for (int k = openAt; k < s.length(); k++) {
            char ch = s.charAt(k);
            if (ch == '(') depth++;
            else if (ch == ')') {
                depth--;
                if (depth == 0) return new int[]{openAt + 1, k};
            }
        }
        return null; // unmatched paren
    }

    /**
     * Finds the opening '{' that is NOT inside parentheses (boundary condition).
     */
    private static int findOpenBrace(String s) {
        int depth = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(') depth++;
            else if (ch == ')') depth--;
            else if (ch == '{' && depth == 0) return i;
        }
        return -1;
    }

    /**
     * Checks if s starting at index i matches prefix (case-insensitive).
     */
    private static boolean regionMatch(String s, int i, String prefix) {
        return s.regionMatches(true, i, prefix, 0, prefix.length());
    }

    /** Returns the matched function name if s[i] starts with "funcName(" */
    private static String matchNamedFunc(String s, int i) {
        for (String f : NAMED_FUNCS) {
            if (regionMatch(s, i, f + "(")) return f;
        }
        return null;
    }

    private static boolean isAlpha(String s, int i) {
        return i < s.length() && Character.isLetter(s.charAt(i));
    }

    // ── Text node factories ───────────────────────────────────────────────────

    private static Text txt(String s, Color c, double sz) {
        Text t = new Text(s);
        t.setFill(c);
        t.setFont(Font.font("Verdana", FontWeight.BOLD, sz));
        return t;
    }

    /** Slightly lighter weight for function names (sin, cos …) */
    private static Text txtItalic(String s, Color c, double sz) {
        Text t = new Text(s);
        t.setFill(c);
        t.setFont(Font.font("Verdana", FontWeight.NORMAL, sz));
        return t;
    }
}