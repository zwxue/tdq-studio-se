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
            if (i != null) {
                if (i > 0) {
                    int len = (int) Math.log10(i) + 1;
                    StringBuilder res = new StringBuilder(EMPTY_STRING);
                    if (integerParam >= 0 && integerParam <= 9) {
                        for (int j = 0; j < len; j++) {
                            res.append(integerParam);
                        }
                    } else {
                        for (int j = 0; j < len; j++) {
                            res.append(rnd.nextInt(9));
                        }
                    }
                    return Integer.valueOf(res.toString());
                } else {
                    if (integerParam >= 0 && integerParam <= 9) {
                        return integerParam;
                    } else {
                        return rnd.nextInt(9);
                    }
                }
            } else {
                return 0;
            }
        }
    }
}