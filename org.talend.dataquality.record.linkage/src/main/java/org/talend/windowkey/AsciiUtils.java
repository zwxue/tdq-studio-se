// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.windowkey;

import java.io.UnsupportedEncodingException;

/**
 * @author scorreia
 * 
 * code from http://www.rgagnon.com/javadetails/java-0456.html
 */
public final class AsciiUtils {

    /**
     * List of characters to transform. Correspondance should be maintained with {@link AsciiUtils#PLAIN_ASCII} field.
     */
    public static final String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9" //$NON-NLS-1$
            + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD" //$NON-NLS-1$
            + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" //$NON-NLS-1$
            + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" //$NON-NLS-1$
            + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" + "\u00C5\u00E5" + "\u00C7\u00E7"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * List of replacement characters. Correspondance should be maintained with {@link AsciiUtils#UNICODE} field.
     */
    public static final String PLAIN_ASCII = "AaEeIiOoUu" // grave //$NON-NLS-1$
            + "AaEeIiOoUuYy" // acute //$NON-NLS-1$
            + "AaEeIiOoUuYy" // circumflex //$NON-NLS-1$
            + "AaEeIiOoUuYy" // tilde //$NON-NLS-1$
            + "AaEeIiOoUuYy" // umlaut //$NON-NLS-1$
            + "Aa" // ring //$NON-NLS-1$
            + "Cc" // cedilla //$NON-NLS-1$
    ;

    // private constructor, can't be instanciated!
    private AsciiUtils() {
    }

    /**
     * Removes accentued from a string and replace with ascii equivalent.
     * 
     * @param s the input string
     * @return the output string (new string) in which accents are removed.
     */
    public static String unaccent(final String s) {
        return replaceCharacters(s, UNICODE, PLAIN_ASCII);
    }

    /**
     * Method "replaceCharacters".
     * 
     * @param s the string to convert
     * @param charsToRemove the list of characters to remove
     * @param replacementChars the replacement characters
     * @return the new string
     */
    public static String replaceCharacters(final String s, final String charsToRemove, final String replacementChars) {
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            int pos = charsToRemove.indexOf(c);
            if (pos > -1) {
                sb.append(replacementChars.charAt(pos));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Removes diacritical mark from a character.
     * 
     * @param ch a character
     * @return the same input character without the diacritical mark if any.
     */
    public static char removeDiacriticalMark(char c) {

        if (c < 192) {
            return c;
        }
        if (c >= 192 && c <= 197) {
            return 'A';
        }
        if (c == 199) {
            return 'C';
        }
        if (c >= 200 && c <= 203) {
            return 'E';
        }
        if (c >= 204 && c <= 207) {
            return 'I';
        }
        if (c == 208) {
            return 'D';
        }
        if (c == 209) {
            return 'N';
        }
        if ((c >= 210 && c <= 214) || c == 216) {
            return 'O';
        }
        if (c >= 217 && c <= 220) {
            return 'U';
        }
        if (c == 221) {
            return 'Y';
        }
        if (c >= 224 && c <= 229) {
            return 'a';
        }
        if (c == 231) {
            return 'c';
        }
        if (c >= 232 && c <= 235) {
            return 'e';
        }
        if (c >= 236 && c <= 239) {
            return 'i';
        }
        if (c == 240) {
            return 'd';
        }
        if (c == 241) {
            return 'n';
        }
        if ((c >= 242 && c <= 246) || c == 248) {
            return 'o';
        }
        if (c >= 249 && c <= 252) {
            return 'u';
        }
        if (c == 253 || c == 255) {
            return 'y';
        }

        return c;
    }

    /**
     * Removes diacritical marks from a string.
     * 
     * @param st a string
     * @return a new string without the diacritical mark if any.
     */
    public static String removeDiacriticalMarks(String st) {

        int len = st.length();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < len; i++) {
            sb.append(removeDiacriticalMark(st.charAt(i)));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String toReplace = (args != null && args.length > 0) ? args[0] : UNICODE;

        System.out.println(toReplace);
        System.out.println(AsciiUtils.unaccent(toReplace));

        String remove = "\u00CE"; //$NON-NLS-1$
        String replacement = "EACSPHTLDEQDBS_"; //$NON-NLS-1$

        System.out.println("'" + remove + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("'" + AsciiUtils.replaceCharacters(remove, remove, replacement) + "'"); //$NON-NLS-1$ //$NON-NLS-2$

        try {
            System.out.println(new String(toReplace.getBytes(), "UTF-8")); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
