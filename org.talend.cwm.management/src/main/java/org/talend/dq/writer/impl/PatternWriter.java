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
package org.talend.dq.writer.impl;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.writer.AElementPersistance;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class PatternWriter extends AElementPersistance {

    static Logger log = Logger.getLogger(PatternWriter.class);

    /**
     * DOC bZhou PatternWriter constructor comment.
     */
    PatternWriter() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {


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

    public void addResourceContent(Resource resource, Pattern element) {
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
        return FactoriesUtil.PATTERN;
    }

    public ReturnCode save(Item item) {
        ReturnCode rc = new ReturnCode();
        try {
            TDQPatternItem patternItem = (TDQPatternItem) item;
            if (patternItem != null && patternItem.eIsProxy()) {
                patternItem = (TDQPatternItem) EObjectHelper.resolveObject(patternItem);
                patternItem.getProperty().setLabel(patternItem.getPattern().getName());
            }
            Pattern pattern = patternItem.getPattern();
            addDependencies(pattern);
            addResourceContent(pattern.eResource(), pattern);
            patternItem.setPattern(pattern);
            ProxyRepositoryFactory.getInstance().save(patternItem);
        } catch (PersistenceException e) {
            log.error(e, e);
            rc.setOk(Boolean.FALSE);
            rc.setMessage(e.getMessage());
        }
        return rc;
    }

    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save(Boolean.TRUE);

    }

}
