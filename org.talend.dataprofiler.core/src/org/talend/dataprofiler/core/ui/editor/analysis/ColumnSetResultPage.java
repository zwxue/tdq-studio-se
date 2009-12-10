// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.analysis;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Section;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class ColumnSetResultPage extends ColumnCorrelationNominalIntervalResultPage {

    public ColumnSetResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.analysis.ColumnCorrelationNominalIntervalResultPage#createGraphicsSectionPart
     * (org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Section createGraphicsSectionPart(Composite parentComp) {
        return null;
    }
}
