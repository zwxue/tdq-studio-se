// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.Functions;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 19 juin 2015. This function will look for a ’@’ and replace all characters before by ’X’ and
 * leave the rest unchanged. If there is no ’@’ in the input, the generated data will be a serie of ’X’.
 *
 */
public class MaskEmail extends Function<String> {

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null) || EMPTY_STRING.equals(str) && keepNull) {
            return str;
        } else {
            if (str != null && !EMPTY_STRING.equals(str)) {
                StringBuilder sb = new StringBuilder(EMPTY_STRING);
                int count = str.lastIndexOf('@');
                if (count == -1) {
                    count = str.length();
                }
                for (int i = 0; i < count; ++i) {
                    sb.append("X"); //$NON-NLS-1$
                }
                return sb.append(str.substring(count, str.length())).toString();
            } else {
                return EMPTY_STRING;
            }
        }
    }
}
