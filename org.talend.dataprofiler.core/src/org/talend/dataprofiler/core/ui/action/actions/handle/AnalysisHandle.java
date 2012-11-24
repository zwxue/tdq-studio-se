// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.Property;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.DomainHelper;
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
        ModelElement tempObject = newObject;
        tempObject = super.update(oldObject, tempObject);

        // Added yyin 2012-10-10 TDQ-6236, create a new domain instead of using the domain in the original object
        EList<Domain> dataFilters = ((Analysis) tempObject).getParameters().getDataFilter();
        if (!dataFilters.isEmpty()) {// if the old already be copied into the duplicated one, replace it with a new one
            Domain domain = DomainHelper.createDomain(((Analysis) tempObject).getName());
            dataFilters.remove(0);
            dataFilters.add(domain);
        }
        // ~6236

        // MOD yyi 2012-05-07 TDQ-5270 duplicate an overview analysis with table/view filters.
        AnalysisHelper.setStringDataFilter((Analysis) tempObject, AnalysisHelper.getStringDataFilter((Analysis) oldObject));

        return tempObject;
    }
}
