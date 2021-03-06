package com.oliverr.jfmt.formatter;

import com.oliverr.jfmt.util.NotNull;
import com.oliverr.jfmt.util.Replace;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Formatter extends ReplaceEntities {

    private static String format = "yyyy-MM-dd";
    private static String time = "hh:mm:ss";

    public static String getTimeFormat() { return time; }
    public static void setTimeFormat(String time) { Formatter.time = time; }

    public static String getDateFormat() { return format; }
    public static void setDateFormat(String format) { Formatter.format = format; }

    private static char decimalSeperator = '.';
    public static char getDecimalSeperator() { return decimalSeperator; }
    public static void setDecimalSeperator(char decimalSeperator) { Formatter.decimalSeperator = decimalSeperator; }

    private static final SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat(getTimeFormat());

    /**
     * This function provides you the ability to format strings more easily.
     * @param text the text you want to format
     * @param args the arguments
     */
    public static String stringf(@NotNull String text, Object... args) {
        if(args == null) return text;

        ReplaceEntities re = new ReplaceEntities();
        text = re.entitiesAndSymbols(text, getDateFormat(), getTimeFormat());

        if(args.length == 0) return text;

        ArrayList<String> fmtChars = new ArrayList<>();
        for(int i = 0; i < text.length() - 1; i++) {
            if(text.charAt(i) == '%') {
                if(text.charAt(i + 1) == 'v') fmtChars.add("%v");
                else if(text.charAt(i + 1) == 's') fmtChars.add("%s");
                else if(text.charAt(i + 1) == 'S') fmtChars.add("%S");
                else if(text.charAt(i + 1) == 'b') fmtChars.add("%b");
                else if(text.charAt(i + 1) == 'B') fmtChars.add("%B");
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
                else if(text.charAt(i + 1) == 'R') fmtChars.add("%R");
                else if(text.charAt(i + 1) == 't') fmtChars.add("%t");
                else if(text.charAt(i + 1) == 'i') fmtChars.add("%i");
                else if(text.charAt(i + 1) == 'q') fmtChars.add("%q");
            }
        }

        String res = text;

        for(int i = 0; i < fmtChars.size(); i++) {
            if(i < args.length) {
                if(args[i] == null) break;

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

                if(fmtChars.get(i).equals("%B")) {
                    if(args[i] instanceof Boolean) {
                        if(((Boolean) args[i])) res = Replace.first(res, "%B", "TRUE");
                        else res = Replace.first(res, "%B", "FALSE");
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
                            if(num == 0) {
                                res = Replace.first(res, "%f"+num, Math.round(Double.parseDouble(args[i].toString()))+"");
                            } else {
                                res = Replace.first(res, "%f"+num, formatDecimal(Double.parseDouble(args[i].toString()), num));
                            }
                        }
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%r")) {
                    res = Replace.first(res, "%r", reverse(args[i].toString()));
                    continue;
                }

                if(fmtChars.get(i).equals("%R")) {
                    res = Replace.first(res, "%R", reverse(args[i].toString()).toUpperCase());
                    continue;
                }

                if(fmtChars.get(i).equals("%t")) {
                    if(args[i] instanceof Date) {
                        res = Replace.first(res, "%t", sdf.format((Date)args[i]));
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%i")) {
                    if(isBinary(args[i].toString())) {
                        res = Replace.first(res, "%i", Integer.parseInt(args[i].toString(), 2)+"");
                    }
                    continue;
                }

                if(fmtChars.get(i).equals("%q")) {
                    res = Replace.first(res, "%q", "\""+args[i].toString()+"\"");
                    //continue;
                }
            }
        }

        return res;
    }

    /**
     * This method provides you the ability to print formatted strings more easily.
     * @param text the text you want to format
     * @param args the arguments
     */
    public static void printf(@NotNull String text, Object... args) { System.out.print(stringf(text, args)); }

    /**
     * This method provides you the ability to print formatted strings more easily.
     * @param text the text you want to format
     * @param args the arguments
     */
    public static void printfln(@NotNull String text, Object... args) { System.out.println(stringf(text, args)); }

    /**
     * This method will align the string, using a space character as the fill character.
     * @param text the text you want to fill
     * @param length the length of the final string
     */
    public static String align(@NotNull String text, @NotNull int length) { return align(text, length, ' '); }


    /**
     * This method will align the string, using a specified character as the fill character.
     * @param text the text you want to fill
     * @param length the length of the final string
     * @param character the character you want to fill with
     */
    public static String align(@NotNull String text, @NotNull int length, char character) { return align(text, length, character, Align.LEFT); }

    /**
     * This method will align the string, using a specified character as the fill character.
     * @param text the text you want to fill
     * @param length the length of the final string
     * @param align the alignment you want to use
     */
    public static String align(@NotNull String text, @NotNull int length, Align align) { return align(text, length, ' ', align); }

    /**
     * This method will align the string, using a specified character as the fill character.
     * @param text the text you want to fill
     * @param length the length of the final string
     * @param character the character you want to fill with
     * @param align the alignment you want to use
     */
    public static String align(@NotNull String text, @NotNull int length, char character, Align align) {
        if(text.length() >= length) return text.substring(0, length);
        String fill = String.valueOf(character).repeat(length - text.length());
        if(align == Align.LEFT) return text + fill;
        if(align == Align.RIGHT) return fill + text;
        return text;
    }

    private static String formatDecimal(Double number, int num) {
        String sb = "0." + "0".repeat(Math.max(0, num));
        DecimalFormat df = new DecimalFormat(sb);
        return Replace.all(df.format(Double.parseDouble(number.toString())), ".", decimalSeperator+"");
    }

    private static String reverse(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i = s.length() - 1; i >= 0; i--) sb.append(s.charAt(i));
        return sb.toString();
    }

    private static boolean isBinary(String n) {
        for(int i = 0; i < n.length(); i++) if(n.charAt(i) != '0' && n.charAt(i) != '1') return false;
        return true;
    }

}
