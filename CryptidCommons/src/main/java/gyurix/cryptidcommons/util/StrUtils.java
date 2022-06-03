package gyurix.cryptidcommons.util;

import java.text.DecimalFormat;
import java.util.*;

public class StrUtils {
    private static final String brightColors = "9abcde";
    public static DecimalFormat DF = new DecimalFormat("###,###,###,###,##0.##");
    public static Random rand = new Random();

    public static List<String> colorize(List<String> list) {
        ArrayList<String> out = new ArrayList<>();
        list.forEach(s -> out.add(colorize(s)));
        return out;
    }

    public static String colorize(String s) {
        return s.replaceAll("&([0-9a-fk-or])", "§$1");
    }

    public static String decolorize(String s) {
        return s.replaceAll("§([0-9a-fk-or])", "&$1");
    }

    public static String fillVariables(String s, Object... vars) {
        if (s == null || s.isEmpty())
            return s;
        String key = null;
        for (Object o : vars) {
            if (key == null)
                key = String.valueOf(o);
            else {
                s = s.replace("<" + key + ">", String.valueOf(o));
                key = null;
            }
        }
        return s;
    }

    public static List<String> fillVariables(List<String> list, Object... vars) {
        if (list == null || list.isEmpty())
            return list;
        list = new ArrayList<>(list);
        int len = list.size();
        for (int i = 0; i < len; ++i)
            list.set(i, fillVariables(list.get(i), vars));
        return list;
    }

    public static List<String> filterStart(Collection<String> list, String prefix) {
        String prefixLower = prefix.toLowerCase();
        return list.stream().filter(el -> el.toLowerCase().startsWith(prefixLower)).sorted().toList();
    }

    public static String formatNum(double am) {
        if (am < 1000L)
            return DF.format(am);
        if (am < 1000000L)
            return DF.format(am / 1000.0) + "k";
        if (am < 1000000000L)
            return DF.format(am / 1000000.0) + "M";
        if (am < 1000000000000L)
            return DF.format(am / 1000000000.0) + "B";
        return DF.format(am / 1000000000000.0) + "T";
    }

    public static String formatTime(long time) {
        time /= 1000;
        if (time < 0)
            time = 0L;
        int w = (int) (time / 604800);
        int d = (int) (time % 604800 / 86400);
        int h = (int) (time % 86400 / 3600);
        int m = (int) (time % 3600 / 60);
        int s = (int) (time % 60);
        StringBuilder sb = new StringBuilder();
        String sep = ", ";
        if (w > 0)
            sb.append(w).append('w').append(sep);
        if (d > 0)
            sb.append(d).append('d').append(sep);
        if (h > 0)
            sb.append(h).append('h').append(sep);
        if (m > 0)
            sb.append(m).append('m').append(sep);
        if (sb.length() == 0 || s > 0)
            sb.append(s).append('s').append(sep);
        return sb.substring(0, sb.length() - sep.length());
    }

    public static String randomColor() {
        return "§" + brightColors.charAt(rand.nextInt(brightColors.length()));
    }

    public static String setLength(String in, int len) {
        return in.length() > len ? in.substring(0, len) : in;
    }

    public static String[] specialSplit(String in) {
        if (in.length() < 17)
            return new String[]{in, ""};
        StringBuilder formats = new StringBuilder();
        int prev = ' ';
        int splitAt = in.charAt(15) == '§' ? 15 : 16;
        for (int i = 0; i < splitAt; ++i) {
            char c = in.charAt(i);
            if (prev == '§') {
                if (c >= '0' && c <= '9' || c >= 'a' && c <= 'f')
                    formats.setLength(0);
                formats.append('§').append(c);
            }
            prev = c;
        }
        return new String[]{in.substring(0, splitAt), setLength(formats + in.substring(splitAt), 16)};
    }

    public static String toCamelCase(String name) {
        StringBuilder sb = new StringBuilder();
        for (String s : name.split("[ _]")) {
            if (s.isEmpty())
                continue;
            sb.append(' ').append(Character.toUpperCase(s.charAt(0))).append(s.substring(1).toLowerCase());
        }
        return sb.length() == 0 ? sb.toString() : sb.substring(1);
    }

    public static long toTime(String in) {
        in = in.replace(" ", "").replace(",", "");
        long out = 0;
        long cur = 0;
        HashMap<String, Long> multipliers = new HashMap<>();
        multipliers.put("w", 604800L);
        multipliers.put("d", 86400L);
        multipliers.put("h", 3600L);
        multipliers.put("m", 60L);
        StringBuilder curP = new StringBuilder();
        for (char c : in.toCharArray()) {
            if (c > 47 && c < 58) {
                if (curP.length() > 0) {
                    out += cur * multipliers.getOrDefault(curP.toString(), 0L);
                    curP.setLength(0);
                    cur = 0;
                }
                cur = cur * 10 + (c - 48);
            } else
                curP.append(c);
        }
        if (curP.length() > 0) {
            out += cur * multipliers.getOrDefault(curP.toString(), 0L);
            cur = 0;
        }
        return (out + cur) * 1000L;
    }
}
