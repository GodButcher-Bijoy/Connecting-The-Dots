package org.example;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquationHandler {

    // ─── Custom trig functions exp4j does not provide natively ───────────────
    private static final Function SEC = new Function("sec", 1) {
        @Override public double apply(double... args) { return 1.0 / Math.cos(args[0]); }
    };
    private static final Function CSC = new Function("csc", 1) {
        @Override public double apply(double... args) { return 1.0 / Math.sin(args[0]); }
    };
    // "cosec" is the alternate spelling used in many textbooks
    private static final Function COSEC = new Function("cosec", 1) {
        @Override public double apply(double... args) { return 1.0 / Math.sin(args[0]); }
    };
    private static final Function COT = new Function("cot", 1) {
        @Override public double apply(double... args) { return Math.cos(args[0]) / Math.sin(args[0]); }
    };

    // All names that must NOT be treated as slider variables
    private static final Set<String> RESERVED_WORDS = Set.of(
            "x", "y", "t", "r",
            "sin",  "cos",  "tan",
            "asin", "acos", "atan",
            "sec",  "csc",  "cosec", "cot",
            "log",  "log10","log2",
            "sqrt", "cbrt", "abs",
            "pi",   "e",    "exp",
            "sinh", "cosh", "tanh",
            "signum", "ceil", "floor"
    );

    // ─── Helpers to attach custom functions to every builder ─────────────────
    /** Registers sec/csc/cosec/cot on any ExpressionBuilder. Public so that
     *  other classes (e.g. BoundaryCondition) can reuse the same set. */
    public static ExpressionBuilder withCustomFunctions(ExpressionBuilder builder) {
        return builder.functions(SEC, CSC, COSEC, COT);
    }

    // ─── "2x" → "2*x" implicit multiplication ────────────────────────────────
    public static String formatEquation(String eqStr) {
        String eq = eqStr.toLowerCase().replace(" ", "");

        eq = eq.replaceAll("(\\d)([a-z])", "$1*$2"); // 2x   -> 2*x
        eq = eq.replaceAll("(\\d)(\\()",   "$1*$2"); // 2(x) -> 2*(x)
        eq = eq.replaceAll("(\\))(\\()",   "$1*$2"); // (x)(y)-> (x)*(y)

        return eq;
    }

    // ─── Extract free variables for slider generation ─────────────────────────
    public static Set<String> extractVariables(String eq) {
        Set<String> foundVars = new HashSet<>();

        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(eq);

        while (m.find()) {
            String token = m.group();
            if (RESERVED_WORDS.contains(token)) continue;

            for (char c : token.toCharArray()) {
                String letter = String.valueOf(c);
                if (!RESERVED_WORDS.contains(letter)) {
                    foundVars.add(letter);
                }
            }
        }
        return foundVars;
    }

    // ─── Standard / inverse (one independent variable) ───────────────────────
    public static Expression buildExpression(String function, String independentVar,
                                             Map<String, Double> globalVariables) {
        ExpressionBuilder builder = withCustomFunctions(
                new ExpressionBuilder(function).variable(independentVar)
        );

        for (String var : globalVariables.keySet()) {
            if (!var.equals(independentVar)) builder.variable(var);
        }

        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            if (!entry.getKey().equals(independentVar))
                expr.setVariable(entry.getKey(), entry.getValue());
        }
        return expr;
    }

    // ─── Implicit (both x and y present) ─────────────────────────────────────
    public static Expression buildImplicitExpression(String function,
                                                     Map<String, Double> globalVariables) {
        ExpressionBuilder builder = withCustomFunctions(
                new ExpressionBuilder(function).variables("x", "y")
        );

        for (String var : globalVariables.keySet()) {
            if (!var.equals("x") && !var.equals("y")) builder.variable(var);
        }

        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            if (!entry.getKey().equals("x") && !entry.getKey().equals("y"))
                expr.setVariable(entry.getKey(), entry.getValue());
        }
        return expr;
    }
}
