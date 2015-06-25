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
 * created by jgonzalez on 22 juin 2015. If the input is a 15-digit number (ie, a correct french ssn), the function will
 * replace everything but the last five digits by the parameter, if the input is an american ssn (ie, 9 digit long) then
 * the function will mask everything but the last four characters.
 *
 */
public abstract class ReplaceSsn<T2> extends Function<T2> {

    protected String param = "8"; //$NON-NLS-1$

    protected String param2 = "X"; //$NON-NLS-1$

    protected void init() {
        if (integerParam >= 0 && integerParam <= 9) {
            param = integerParam.toString();
        }
        if (parameters[0].matches("[0-9]|[a-zA-Z]| ")) { //$NON-NLS-1$
            param2 = parameters[0];
        }
    }

    @Override
    public abstract T2 generateMaskedRow(T2 t);
}
