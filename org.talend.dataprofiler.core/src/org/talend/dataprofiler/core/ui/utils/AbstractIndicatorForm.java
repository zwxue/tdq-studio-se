// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public abstract class AbstractIndicatorForm extends AbstractForm {

    private static List<AbstractIndicatorParameter> parameters = new ArrayList<AbstractIndicatorParameter>();

    // ----message define----

    public static final String MSG_EMPTY = "some fieldes are empty!";

    public static final String MSG_ONLY_CHAR = "this field just allowed to input character text!";

    public static final String MSG_ONLY_NUMBER = "this field just allowed to input numberic text!";

    public static final String MSG_ONLY_DATE = "this field just allowed to input date formate text!";

    public static final String MSG_OK = "your input is valid!";

    /**
     * DOC zqin AbstractIndicatorForm constructor comment.
     * 
     * @param parent
     * @param style
     */
    public AbstractIndicatorForm(Composite parent, int style, AbstractIndicatorParameter parameter) {
        super(parent, style);
        parameters.add(parameter);
    }

    public static AbstractIndicatorParameter[] getParameters() {

        return parameters.toArray(new AbstractIndicatorParameter[parameters.size()]);
    }

    public static boolean isParametersEmpty() {
        return parameters.isEmpty();
    }

    public static void emptyParameterList() {

        parameters.clear();
    }

    public abstract String getFormName();

}
