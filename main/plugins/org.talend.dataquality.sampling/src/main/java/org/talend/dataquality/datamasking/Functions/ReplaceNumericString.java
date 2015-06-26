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

/**
 * created by jgonzalez on 22 juin 2015. See ReplaceNumeric.
 *
 */
public class ReplaceNumericString extends ReplaceNumeric<String> {

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            super.init();
            if (str != null && !EMPTY_STRING.equals(str)) {
                if ((" ").equals(param2)) { //$NON-NLS-1$
                    return str.replaceAll("\\d", param2).replace(" ", EMPTY_STRING); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    return str.replaceAll("\\d", param2); //$NON-NLS-1$
                }
            } else {
                return EMPTY_STRING;
            }
        }
    }
}
