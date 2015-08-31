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

import java.io.Serializable;

/**
 * created by jgonzalez on 22 juin 2015. See ReplaceNumeric.
 *
 */
public class ReplaceNumericDouble extends ReplaceNumeric<Double> implements Serializable {

    private static final long serialVersionUID = 2373577711682777009L;

    @Override
    public Double generateMaskedRow(Double d) {
        if (d == null && keepNull) {
            return null;
        } else {
            if (d != null) {
                String str = d.toString();
                String res = EMPTY_STRING;
                if (integerParam >= 0 && integerParam <= 9) {
                    res = str.replaceAll("\\d", String.valueOf(integerParam)); //$NON-NLS-1$
                } else {
                    res = str.replaceAll("\\d", String.valueOf(rnd.nextInt(9))); //$NON-NLS-1$
                }
                return Double.valueOf(res);
            } else {
                return 0.0;
            }
        }
    }
}