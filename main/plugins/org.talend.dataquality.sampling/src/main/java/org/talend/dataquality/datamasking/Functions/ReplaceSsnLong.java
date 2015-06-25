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
public class ReplaceSsnLong extends ReplaceSsn<Long> {

    @Override
    public Long generateMaskedRow(Long l) {
        if (l == null && keepNull) {
            return null;
        } else {
            if (l != null) {
                super.init();
                int digits_to_keep = 0;
                String str = l.toString();
                if (str.length() == 9) {
                    digits_to_keep = 4;
                } else if (str.length() == 15) {
                    digits_to_keep = 5;
                }
                String res_ssn = str.substring(0, str.length() - digits_to_keep).replaceAll("[0-9]", param); //$NON-NLS-1$ 
                res_ssn = res_ssn + str.substring(str.length() - digits_to_keep, str.length());
                return Long.parseLong(res_ssn);
            } else {
                return 0L;
            }
        }
    }
}
