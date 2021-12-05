package com.oliverr.jfmt;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Formatter {

    private static DecimalFormat df = new DecimalFormat("0.00");
    private static String format = "yyyy-MM-dd";
    private static String time = "hh:mm:ss";

    public static String getTimeFormat() { return time; }
    public static void setTimeFormat(String time) { Formatter.time = time; }

    public static String getDateFormat() { return format; }
    public static void setDateFormat(String format) { Formatter.format = format; }

    private static SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
    private static SimpleDateFormat sdf2 = new SimpleDateFormat(getTimeFormat());

    public static String stringf(String text, Object... args) {
        if(args == null) return  text;

        text = Replace.all(text, "&n", "\n");
        text = Replace.all(text, "&N", "\n\n");
        text = Replace.all(text, "&t", sdf2.format(new Date()));
        text = Replace.all(text, "&d", sdf.format(new Date()));

        text = Replace.all(text, "$c", "©");
        text = Replace.all(text, "$r", "®");
        text = Replace.all(text, "$e", "∈");
        text = Replace.all(text, "$p", "∏");
        text = Replace.all(text, "$s", "∑");
        text = Replace.all(text, "$tm", "™");
        text = Replace.all(text, "$Ua", "↑");
        text = Replace.all(text, "$Da", "↓");
        text = Replace.all(text, "$La", "←");
        text = Replace.all(text, "$Ra", "→");

        if(args.length == 0) return  text;

        ArrayList<String> fmtChars = new ArrayList<>();

        for(int i = 0; i < text.length() - 1; i++) {
            if(text.charAt(i) == '%') {
                if(text.charAt(i + 1) == 'v') fmtChars.add("%v");
                else if(text.charAt(i + 1) == 's') fmtChars.add("%s");
                else if(text.charAt(i + 1) == 'S') fmtChars.add("%S");
                else if(text.charAt(i + 1) == 'b') fmtChars.add("%b");
                else if(text.charAt(i + 1) == 'd') fmtChars.add("%d");
                else if(text.charAt(i + 1) == 'o') fmtChars.add("%o");
                else if(text.charAt(i + 1) == 'f') {
                    if(i + 2 < text.length() && "0123456789".contains(text.charAt(i + 2) + "")) {
                        fmtChars.add("%f"+text.charAt(i + 2));
                    } else {
                        fmtChars.add("%f");
                    }
                }
                else if(text.charAt(i + 1) == 'r') fmtChars.add("%r");
                else if(text.charAt(i + 1) == 't') fmtChars.add("%t");
                else if(text.charAt(i + 1) == 'a') fmtChars.add("%a");
            }
        }

        String res = text;

        for(int i = 0; i < fmtChars.size(); i++) {
            if(i < args.length) {
                if(fmtChars.get(i).equals("%v")) {
                    res = Replace.first(res, "%v", args[i].toString());
                    continue;
                }

                if(fmtChars.get(i).equals("%s")) {
                    if(args[i] instanceof String) {
                        res = Replace.first(res, "%s", (String)args[i]);
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%S")) {
                    if(args[i] instanceof String) {
                        res = Replace.first(res, "%S", ((String)args[i]).toUpperCase());
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%b")) {
                    if(args[i] instanceof Boolean) {
                        if(((Boolean) args[i])) res = Replace.first(res, "%b", "true");
                        else res = Replace.first(res, "%b", "false");
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%d")) {
                    if(args[i] instanceof Number) {
                        if(args[i].toString().contains(".")) res = Replace.first(res, "%d", args[i].toString().split("\\.")[0]);
                        else res = Replace.first(res, "%d", args[i].toString());
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%o")) {
                    if(args[i] instanceof Number) {
                        int num;
                        if(args[i].toString().contains(".")) num = Integer.parseInt(args[i].toString().split("\\.")[0]);
                        else num = Integer.parseInt(args[i].toString());
                        res = Replace.first(res, "%o", Integer.toBinaryString(num));
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%f")) {
                    if(args[i] instanceof Number) {
                        res = Replace.first(res, "%f", args[i].toString());
                    }
                    continue;
                }

                if(fmtChars.get(i).startsWith("%f") && fmtChars.get(i).length() == 3) {
                    if(args[i] instanceof Number) {
                        if("0123456789".contains(fmtChars.get(i).charAt(2)+"")) {
                            int num = Integer.parseInt(fmtChars.get(i).charAt(2)+"");
                            StringBuilder sb = new StringBuilder();
                            if(num == 0) {
                                res = Replace.first(res, "%f"+num, Math.round(Double.parseDouble(args[i].toString()))+"");
                            } else {
                                sb.append("0.");
                                sb.append("0".repeat(Math.max(0, num)));
                                df = new DecimalFormat(sb.toString());
                                res = Replace.first(res, "%f"+num, df.format(Double.parseDouble(args[i].toString())));
                            }
                        }
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%r")) {
                    res = Replace.first(res, "%r", reverse(args[i].toString()));
                    continue;
                }

                if(fmtChars.get(i).equals("%t")) {
                    if(args[i] instanceof Date) {
                        res = Replace.first(res, "%t", sdf.format((Date)args[i]));
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%a")) {
                    if(args[i] instanceof ArrayList) {
                        ArrayList<Object> al = new ArrayList<>((ArrayList<Object>)args[i]);
                        res = Replace.first(res, "%a", printArray(al));
                    }
                    else if(args[i] instanceof List) {
                        List<Object> l = new ArrayList<>((List<Object>)args[i]);
                        res = Replace.first(res, "%a", printArray(l));
                    }
                    else if(args[i] instanceof Set) {
                        Set<Object> s = new HashSet<>((Set<Object>)args[i]);
                        res = Replace.first(res, "%a", printArray(s));
                    }
                    else if(args[i] instanceof HashSet) {
                        HashSet<Object> hs = new HashSet<>((HashSet<Object>)args[i]);
                        res = Replace.first(res, "%a", printArray(hs));
                    }
                    //continue;
                }
            }
        }

        return res;
    }

    public static void printf(String text, Object... args) { System.out.print(stringf(text, args)); }

    public static void printfln(String text, Object... args) { System.out.println(stringf(text, args)); }

    private static String reverse(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i = s.length() - 1; i >= 0; i--) sb.append(s.charAt(i));
        return sb.toString();
    }

    private static String printArray(ArrayList<Object> array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for(Object s : array) sb.append("\"").append(s.toString()).append("\" ");
        sb.append("]");
        return sb.toString();
    }

    private static String printArray(List<Object> array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for(Object s : array) sb.append("\"").append(s.toString()).append("\" ");
        sb.append("]");
        return sb.toString();
    }

    private static String printArray(Set<Object> array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for(Object s : array) sb.append("\"").append(s.toString()).append("\" ");
        sb.append("]");
        return sb.toString();
    }

    private static String printArray(HashSet<Object> array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for(Object s : array) sb.append("\"").append(s.toString()).append("\" ");
        sb.append("]");
        return sb.toString();
    }

}
