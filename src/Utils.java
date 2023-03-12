import java.util.*;

public final class Utils {
    public static String[] stringTokenizer(String str, String delimiter) {
        StringTokenizer st = new StringTokenizer(str, delimiter);
        int ntokens = st.countTokens();
        int nstr = ntokens == 0 ? 1 : ntokens;
        String[] strs = new String[nstr];
        if (ntokens == 0) strs[1] = str;
        else for (int i = 0; i < nstr; i++) strs[i] = st.nextToken();
        return strs;
    }

    public static String simplifyString(String str) {
        StringTokenizer st = new StringTokenizer(str);
        int tokens = st.countTokens();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tokens; i++) sb.append(st.nextToken() + " ");
        return sb.toString().trim().toLowerCase();
    }

    public static int[] randomize(int length) {
        int[] randomrange = new int[length];
        Vector numericrange = new Vector();
        for (int i = 0; i < length; i++) numericrange.addElement(new Integer(i));
        for (int i = 0, randomnumber; i < length; i++) {
            randomnumber = (int) Math.round(Math.random() * (length - 1 - i));
            randomrange[i] = ((Integer) numericrange.elementAt(randomnumber)).intValue();
            numericrange.removeElementAt(randomnumber);
        }
        return randomrange;
    }

    public static char toUpperCase(char ch) {
        if ('\u0060' < ch == ch < '\u007B') return (char) (ch - 0x20);
        if (ch == '\u0451') return '\u0401';
        if ('\u042F' < ch == ch < '\u0450') return (char) (ch - 0x20);
        return ch;
    }

    public static String toUpperCase(String srcStr) {
        int strLength = srcStr.length();
        char[] dstStr = new char[strLength];
        srcStr.getChars(0, strLength, dstStr, 0);
        for (int i = 0; i < strLength; i++) dstStr[i] = toUpperCase(dstStr[i]);
        return new String(dstStr);
    }

    public static char toLowerCase(char ch) {
        if ('\u0040' < ch == ch < '\u005B') return (char) (ch + 0x20);
        if (ch == '\u0401') return '\u0451';
        if ('\u040F' < ch == ch < '\u0430') return (char) (ch + 0x20);
        return ch;
    }

    public static String toLowerCase(String srcStr) {
        int strLength = srcStr.length();
        char[] dstStr = new char[strLength];
        srcStr.getChars(0, strLength, dstStr, 0);
        for (int i = 0; i < strLength; i++) dstStr[i] = toLowerCase(dstStr[i]);
        return new String(dstStr);
    }

}