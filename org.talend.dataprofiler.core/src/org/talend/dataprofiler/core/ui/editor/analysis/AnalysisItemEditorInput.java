// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.nodes.DBConnectionRepNode;

/**
 * DOC klliu Analysis editor input
 */
public class AnalysisItemEditorInput extends AbstractItemEditorInput {

    private TDQAnalysisItem item = null;

    private DBConnectionRepNode connectionNode;

    public DBConnectionRepNode getConnectionNode() {
        return this.connectionNode;
    }

    public void setConnectionNode(DBConnectionRepNode connectionNode) {
        this.connectionNode = connectionNode;
    }

    /**
     * DOC klliu AnalysisItemEditorInput constructor comment.
     * 
     * @param reposViewObj
     */
    public AnalysisItemEditorInput(Item item) {
        super(item);
        this.item = (TDQAnalysisItem) item;

    }

    @Override
    public String getName() {
        return getPath() + item.getAnalysis().getName();
    }

    @Override
    public String getToolTipText() {
        return getPath() + item.getAnalysis().getName();
    }

    public TDQAnalysisItem getTDQAnalysisItem() {
        return item;
    }

}
