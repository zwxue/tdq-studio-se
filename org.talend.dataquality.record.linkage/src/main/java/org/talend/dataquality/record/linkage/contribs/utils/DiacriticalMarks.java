/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2003-2007 Sun Microsystems, Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the terms of the Common 
 * Development and Distribution License ("CDDL")(the "License"). You 
 * may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://open-dm-mi.dev.java.net/cddl.html
 * or open-dm-mi/bootstrap/legal/license.txt. See the License for the 
 * specific language governing permissions and limitations under the  
 * License.  
 *
 * When distributing the Covered Code, include this CDDL Header Notice 
 * in each file and include the License file at
 * open-dm-mi/bootstrap/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the 
 * fields enclosed by brackets [] replaced by your own identifying 
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 */
package org.talend.dataquality.record.linkage.contribs.utils;

/**
 *
 * @author souaguenouni
 */
public class DiacriticalMarks {
    
    /**
     * Removes diacritical mark from a character
     *
     * @param ch a character
     * @return the same input character without the diacritical mark
     * if any. 
     */ 
    public static char removeDiacriticalMark(char c) {
        
        if (c < 192)
            return c;
        if (c >= 192 && c <= 197)
            return (char) 'A';
        if (c == 199)
            return (char) 'C';
        if (c >= 200 && c <= 203)
            return (char) 'E';
        if (c >= 204 && c <= 207)
            return (char) 'I';
        if (c == 208)
            return (char) 'D';
        if (c == 209)
            return (char) 'N';
        if ((c >= 210 && c <= 214) || c == 216)
            return (char) 'O';
        if (c >= 217 && c <= 220)
            return (char) 'U';
        if (c == 221)
            return (char) 'Y';
        if (c >= 224 && c <= 229)
            return (char) 'a';
        if (c == 231)
            return (char) 'c';
        if (c >= 232 && c <= 235)
            return (char) 'e';
        if (c >= 236 && c <= 239)
            return (char) 'i';
        if (c == 240)
            return (char) 'd';
        if (c == 241)
            return (char) 'n';
        if ((c >= 242 && c <= 246) || c == 248)
            return (char) 'o';
        if (c >= 249 && c <= 252)
            return (char) 'u';
        if (c == 253 || c == 255)
            return (char) 'y';
    
        return c;   
    }            
    /**
     * Removes diacritical mark from a string
     *
     * @param st a string
     * @return the same input string without the diacritical mark if any. 
     */ 
    public static String removeDiacriticalMark(String st) {
        
        int len = st.length();
        String tempS = new String(st);
        
        for (int i = 0; i < len; i++) {
             char ch = tempS.charAt(i);
             tempS = tempS.replace(ch, removeDiacriticalMark(ch));   
        }  
        return tempS;   
    }                            
}
