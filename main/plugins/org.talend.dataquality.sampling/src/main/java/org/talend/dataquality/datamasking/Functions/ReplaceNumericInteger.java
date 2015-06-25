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
public class ReplaceNumericInteger extends ReplaceNumeric<Integer> {

    @Override
    public Integer generateMaskedRow(Integer i) {
        if (i == null && keepNull) {
            return null;
        } else {
            super.init();
            if (i != null) {
                String str = i.toString();
                String res = str.replaceAll("\\d", param); //$NON-NLS-1$
                return Integer.valueOf(res);
            } else {
                return 0;
            }
        }
    }

}
