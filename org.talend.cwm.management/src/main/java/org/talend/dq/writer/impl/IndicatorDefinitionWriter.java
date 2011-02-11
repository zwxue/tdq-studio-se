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
package org.talend.dq.writer.impl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.Item;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.writer.AElementPersistance;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorDefinitionWriter extends AElementPersistance {

    /**
     * DOC bZhou SYSIndicatorWriter constructor comment.
     */
    IndicatorDefinitionWriter() {
    }

    @Override
    public void addDependencies(ModelElement element) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addResourceContent(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    public void addResourceContent(ModelElement element) {
        EList<EObject> resourceContents = element.eResource().getContents();
        resourceContents.addAll(element.getDescription());
    }

    public void addResourceContent(Resource resource, IndicatorDefinition element) {
        if (resource != null) {
            EList<EObject> resourceContents = resource.getContents();
            resourceContents.addAll(element.getDescription());
            resource.getContents().add(element);
        }
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.DEFINITION;
    }

    public ReturnCode save(Item item) {
        return null;
    }

    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save(Boolean.TRUE);

    }
}
