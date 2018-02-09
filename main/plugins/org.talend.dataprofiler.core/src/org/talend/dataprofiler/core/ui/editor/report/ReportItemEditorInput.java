// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.report;

import org.talend.core.model.properties.Item;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataquality.properties.TDQReportItem;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class ReportItemEditorInput extends AbstractItemEditorInput {

    private TDQReportItem reportItem;
    /**
     * DOC klliu ReportItemEditorInput constructor comment.
     * 
     * @param reposViewObj
     */
    public ReportItemEditorInput(Item item) {
        super(item);
        reportItem = (TDQReportItem) item;
    }
    @Override
    public String getName() {
        return getPath() + reportItem.getReport().getName();
    }

    @Override
    public String getToolTipText() {
        return getPath() + reportItem.getReport().getName();
    }
    public TDQReportItem getTDQReportItem() {
        return reportItem;
    }

    public String getModelElementUuid() {
        if (this.item != null) {
            return ResourceHelper.getUUID(this.reportItem.getReport());
        }
        return super.getModelElementUuid();
    }
}
