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
public class ReplaceNumericLong extends ReplaceNumeric<Long> {

    @Override
    public Long generateMaskedRow(Long l) {
        if (l == null && keepNull) {
            return null;
        } else {
            if (l != null) {
                if (l > 0) {
                    int len = (int) Math.log10(l) + 1;
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
                    return Long.valueOf(res.toString());
                } else {
                    if (integerParam >= 0 && integerParam <= 9) {
                        return Long.parseLong(integerParam.toString());
                    } else {
                        return (long) rnd.nextInt(9);
                    }
                }
            } else {
                return 0L;
            }
        }
    }

}
