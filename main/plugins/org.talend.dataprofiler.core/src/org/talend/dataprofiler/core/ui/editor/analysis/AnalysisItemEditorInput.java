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
package org.talend.dataprofiler.core.ui.editor.analysis;

import org.talend.core.model.properties.Item;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * DOC klliu Analysis editor input.
 */
public class AnalysisItemEditorInput extends AbstractItemEditorInput {

    private TDQAnalysisItem item = null;

    private IRepositoryNode connectionNode;

    public IRepositoryNode getConnectionNode() {
        // MOD mzhao bug 19244, when connection node wasn't set before, find it from analysis.
        if (connectionNode == null) {
            DataManager connection = item.getAnalysis().getContext().getConnection();
            if (connection != null) {
                connectionNode = RepositoryNodeHelper.recursiveFind(connection);
            }
        }
        return this.connectionNode;
    }

    public void setConnectionNode(IRepositoryNode connectionNode) {
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

    public String getModelElementUuid() {
        if (this.item != null) {
            return ResourceHelper.getUUID(this.item.getAnalysis());
        }
        return super.getModelElementUuid();
    }
}
