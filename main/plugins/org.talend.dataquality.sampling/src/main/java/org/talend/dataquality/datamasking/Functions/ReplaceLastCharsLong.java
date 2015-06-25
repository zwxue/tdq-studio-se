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
 * created by jgonzalez on 22 juin 2015. See ReplaceLastChars.
 *
 */
public class ReplaceLastCharsLong extends ReplaceLastChars<Long> {

    @Override
    public Long generateMaskedRow(Long l) {
        if (l == null && keepNull) {
            return null;
        } else {
            if (l != null && integerParam > 0) {
                if ((int) Math.log10(l) + 1 < integerParam) {
                    integerParam = (int) Math.log10(l) + 1;
                }
                StringBuilder sbui = new StringBuilder(l.toString());
                StringBuilder rempl = new StringBuilder(EMPTY_STRING);
                for (int i = 0; i < integerParam; ++i) {
                    rempl.append(rnd.nextInt(9));
                }
                sbui.replace(sbui.length() - integerParam, sbui.length(), rempl.toString());
                return Long.parseLong(sbui.toString());
            } else {
                return 0L;
            }
        }
    }
}
