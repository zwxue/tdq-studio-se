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
 * created by jgonzalez on 22 juin 2015. See ReplcaSsn.
 *
 */
public class ReplaceSsnString extends ReplaceSsn<String> {

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            if (str != null && !EMPTY_STRING.equals(str)) {
                super.init();
                int digits_to_keep = 0;
                String str_nospaces = str.replaceAll("\\s+", EMPTY_STRING); //$NON-NLS-1$
                if (str_nospaces.replaceAll("\\D", EMPTY_STRING).length() == 9) {//$NON-NLS-1$
                    digits_to_keep = 4;
                } else if (str_nospaces.replaceAll("\\D", EMPTY_STRING).length() == 15) { //$NON-NLS-1$
                    digits_to_keep = 5;
                }
                String res = str_nospaces.substring(0, str_nospaces.length() - digits_to_keep).replaceAll("[0-9]", param2); //$NON-NLS-1$ 
                res = res + str_nospaces.substring(str_nospaces.length() - digits_to_keep, str_nospaces.length());
                return res;
            } else {
                return EMPTY_STRING;
            }
        }
    }
}
