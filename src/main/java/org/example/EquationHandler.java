package org.example;


import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquationHandler {

    private static final Set<String> RESERVED_WORDS = Set.of(
            "x", "y",
            "sin", "cos", "tan", "log", "sqrt", "abs", "pi", "e", "exp",
            "asin", "acos", "atan", "atan2",
            "sinh", "cosh", "tanh",
            "log2", "log10", "cbrt");

    // "2x" কে "2*x" এ কনভার্ট করার লজিক
    public static String formatEquation(String eqStr) {
        String eq = eqStr.toLowerCase().replace(" ", "");
        // digit before letter: 2x → 2*x
        eq = eq.replaceAll("(\\d)([a-z])", "$1*$2");
        // standalone digit(s) before open-paren: 2( → 2*(  but NOT log10( (digit preceded by letter)
        eq = eq.replaceAll("(?<![a-z0-9])(\\d+)\\(", "$1*(");
        // single-letter variable before open-paren: x( → x*(  but NOT sin( (letter preceded by letter)
        eq = eq.replaceAll("(?<![a-z])([a-z])\\(", "$1*(");
        // close-paren before letter/digit/open-paren: )x → )*x, )2 → )*2, )( → )*(
        eq = eq.replaceAll("\\)([a-z0-9(])", ")*$1");
        return eq;
    }

    // ইকুয়েশন থেকে স্লাইডারের জন্য ভেরিয়েবল (a, b, c ইত্যাদি) বের করার লজিক
    public static Set<String> extractVariables(String eq) {
        Set<String> foundVars = new HashSet<>();
        // Match whole letter-words to avoid extracting letters from function names like sin, cos, asin
        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(eq.toLowerCase());

        while (m.find()) {
            String token = m.group();
            // Skip tokens that are exactly a known function/constant name
            if (RESERVED_WORDS.contains(token)) continue;
            // Mark positions that are part of an embedded function name (e.g. "cos" in "bcos")
            boolean[] isFunctionLetter = new boolean[token.length()];
            for (String reserved : RESERVED_WORDS) {
                if (reserved.length() < 2) continue; // single-char reserves handled separately
                int idx = token.indexOf(reserved);
                while (idx != -1) {
                    for (int k = idx; k < idx + reserved.length(); k++) {
                        isFunctionLetter[k] = true;
                    }
                    idx = token.indexOf(reserved, idx + 1);
                }
            }
            // Extract only unmarked letters (they are standalone slider variables like a, b, c)
            for (int i = 0; i < token.length(); i++) {
                if (!isFunctionLetter[i]) {
                    String var = String.valueOf(token.charAt(i));
                    if (!RESERVED_WORDS.contains(var)) {
                        foundVars.add(var);
                    }
                }
            }
        }
        return foundVars;
    }

    // স্ট্যান্ডার্ড বা ইনভার্স ইকুয়েশন (শুধু x অথবা y) এর জন্য Expression তৈরি
    public static Expression buildExpression(String function, String independentVar, Map<String, Double> globalVariables) {
        ExpressionBuilder builder = new ExpressionBuilder(function).variable(independentVar);
        for (String var : globalVariables.keySet()) {
            builder.variable(var);
        }
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            expr.setVariable(entry.getKey(), entry.getValue());
        }
        return expr;
    }

    // ইমপ্লিসিট ইকুয়েশন (x ও y দুটোই থাকলে) এর জন্য Expression তৈরি
    public static Expression buildImplicitExpression(String function, Map<String, Double> globalVariables) {
        ExpressionBuilder builder = new ExpressionBuilder(function).variables("x", "y");
        for (String var : globalVariables.keySet()) {
            builder.variable(var);
        }
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            expr.setVariable(entry.getKey(), entry.getValue());
        }
        return expr;
    }
}