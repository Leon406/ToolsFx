package lianzhang;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/* loaded from: BurpSuiteCnV2.0.jar:lianzhang/Translator.class */

/** javac -encoding UTF-8 Translator.java -Xlint:unchecked */
public class Translator {
    static boolean debug;
    Map<String, String> literal = new HashMap<>(2048);
    static List<String> white = new ArrayList<>(512);
    Map<Pattern, String> regexp = new HashMap<>(256);
    private static Map<String, Translator> map = new HashMap<>(2);
    static final Pattern pattern = Pattern.compile(".*\\$\\d.*");
    static final Pattern patternChinese = Pattern.compile("[\u4e00-\u9fa5]+");
    static final Pattern patternBypass =
            Pattern.compile("^[^a-zA-Z]+$|^[A-Z]:|^/|[A-Z]{3,} /.*? HTTP/|HTTP/[\\d.]+ \\d+");

    private static String keyText = "JTextComponent";
    static final Pattern patternBypassInput =
            Pattern.compile("Selection|processInputMethodEvent|componentResized");

    static {
        debug = Translator.class.getResource("/debug") != null;
        System.out.println("^^^^^^^  debug = " + debug);
        String whiteListFile = "white.txt";

        InputStream is = Translator.class.getClassLoader().getResourceAsStream(whiteListFile);
        if (is == null) {
            try {
                is = new FileInputStream(whiteListFile);
            } catch (FileNotFoundException e) {
                System.err.println("Could not load white language file: " + whiteListFile);
            }
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            br.lines().forEach(white::add);
        } catch (Exception e) {
            System.err.println("Could not add white string");
            e.printStackTrace();
        }
    }

    public static String translate(String lang, String str) throws Exception {
        if (str == null || str.length() < 2) {
            return str;
        }
        if (white.contains(str)) {
            return str;
        }
        if (lang.contains("cn") && patternChinese.matcher(str).find()) {
            if (debug) System.err.println("\t\tpatternChinese " + str);
            return str;
        }
        if (patternBypass.matcher(str).find()) {
            if (debug) System.err.println("\t\tpatternBypass " + str);
            return str;
        }
        Translator translator = map.get(lang);
        if (translator == null) {
            translator = new Translator(lang);
            map.put(lang, translator);
        }

        // test code
        //        if (str.contains("Proxy") || str.contains("Welcome") || str.contains("injection"))
        // {
        //            System.out.println(Thread.currentThread().getName() + "___________________ \t
        // " + str);
        //
        // Arrays.stream(Thread.currentThread().getStackTrace()).forEach(System.out::println);
        //            System.out.println("___________________");
        //        }
        boolean isjTextComponent = false;
        if (Thread.currentThread().getName().contains("AWT-EventQueue")) {
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                String s = stackTraceElement.toString();
                if (s.contains(keyText)) {
                    isjTextComponent = true;
                    continue;
                }
                if (isjTextComponent && patternBypassInput.matcher(s).find()) {
                    return str;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String str3 : str.split("\n")) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(translator.translate(str3));
        }
        String tr = sb.toString();
        if (debug) {
            if (!tr.contains("个请求(")) {
                System.out.println("========================");
                System.err.println(str + "~~~" + tr);
            }
        } else if (str.contains(tr)) {
            System.out.println("========================");
            System.err.println(str + "~~~" + tr);
        }
        return tr;
    }

    public Translator(String str) throws Exception {
        String translateFile = str + ".txt";
        InputStream is = getClass().getClassLoader().getResourceAsStream(translateFile);

        System.out.println(
                "\u001b[32;4mBy:LianZhang github:https://github.com/hackxx/BurpSuiteCn\u001b[0m");
        if (is == null) {
            try {
                is = new FileInputStream(translateFile);
            } catch (FileNotFoundException e) {
                System.err.println("Could not load language file: " + translateFile);
                throw e;
            }
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            br.lines()
                    .forEach(
                            s -> {
                                if (debug) {
                                    System.out.println("_______ " + s);
                                }
                                if (s.isBlank() || s.startsWith("###")) {
                                    return;
                                }
                                String[] split = s.split("\t", 2);
                                if (split.length != 2) {
                                    System.err.println("Invalid line: " + s);
                                }
                                if (pattern.matcher(split[1]).matches()
                                        || split[0].contains("(?i)")) {
                                    try {
                                        this.regexp.put(
                                                Pattern.compile("(?m)^" + split[0] + "$"),
                                                split[1].replace("\"", "\\\""));
                                    } catch (PatternSyntaxException e2) {
                                        System.err.println("PatternSyntaxException: " + split[0]);
                                    }
                                } else {
                                    this.literal.put(split[0], split[1]);
                                }
                            });
        } catch (Exception e) {
            System.err.println("Could not translate string");
            e.printStackTrace();
        }
    }

    public String translate(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }
        String str2 = this.literal.get(str.trim());
        if (str2 == null) {
            str2 = str;
            for (Map.Entry<Pattern, String> entry : this.regexp.entrySet()) {
                Matcher matcher = entry.getKey().matcher(str2);
                if (matcher.matches()) {
                    str2 = matcher.replaceAll(entry.getValue());
                }
            }
        }
        return str2;
    }
}
