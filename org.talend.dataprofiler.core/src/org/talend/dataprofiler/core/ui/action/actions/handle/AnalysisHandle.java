// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.talend.core.model.properties.Property;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class AnalysisHandle extends EMFResourceHandle {

    /**
     * DOC bZhou DuplicateAnalysisHandle constructor comment.
     * 
     * @param propety
     */
    AnalysisHandle(Property propety) {
        super(propety);
    }

    AnalysisHandle(IRepositoryNode node) {
        super(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.action.actions.duplicate.DuplicateEMFResourceHandle#update(orgomg.cwm.objectmodel
     * .core.ModelElement, orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected ModelElement update(ModelElement oldObject, ModelElement newObject) {
        newObject = super.update(oldObject, newObject);

        AnalysisHelper.getDataFilter((Analysis) newObject).clear();
        AnalysisHelper.setStringDataFilter((Analysis) newObject, AnalysisHelper.getStringDataFilter((Analysis) oldObject));

        return newObject;
    }
}
