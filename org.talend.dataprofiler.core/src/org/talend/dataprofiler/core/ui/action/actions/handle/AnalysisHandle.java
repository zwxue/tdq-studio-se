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
        newObject = super.update(oldObject, newObject);

        // Added yyin 2012-10-10 TDQ-6236, create a new domain instead of using the domain in the original object
        EList<Domain> dataFilters = ((Analysis) newObject).getParameters().getDataFilter();
        if (!dataFilters.isEmpty()) {// if the old already be copied into the duplicated one, replace it with a new one
            Domain domain = DomainHelper.createDomain(((Analysis) newObject).getName());
            dataFilters.remove(0);
            dataFilters.add(domain);
        }
        // ~6236

        AnalysisHelper.setStringDataFilter((Analysis) newObject, AnalysisHelper.getStringDataFilter((Analysis) oldObject));

        return newObject;
    }
}
