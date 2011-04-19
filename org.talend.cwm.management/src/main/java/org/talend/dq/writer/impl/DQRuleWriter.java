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

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.writer.AElementPersistance;
import org.talend.repository.model.RepositoryNode;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class DQRuleWriter extends AElementPersistance {

    static Logger log = Logger.getLogger(DQRuleWriter.class);

    /**
     * DOC bZhou DQRuleWriter constructor comment.
     */
    DQRuleWriter() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addResourceContent(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addResourceContent(ModelElement element) {
        // TODO Auto-generated method stub

    }

    public void addResourceContent(Resource resource, DQRule element) {
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
        return FactoriesUtil.DQRULE;
    }

    public ReturnCode save(Item item) {
        ReturnCode rc = new ReturnCode();
        try {
            TDQBusinessRuleItem ruleItem = (TDQBusinessRuleItem) item;
            DQRule rule = ruleItem.getDqrule();
            addDependencies(rule);
            ProxyRepositoryFactory.getInstance().save(ruleItem);
            updateDependencies(rule);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#updateDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void updateDependencies(ModelElement element) {
        DQRule rule = (DQRule) element;
        // update supplier dependency
        EList<Dependency> supplierDependency = rule.getSupplierDependency();
        try {
            for (Dependency sDependency : supplierDependency) {
                EList<ModelElement> client = sDependency.getClient();
                for (ModelElement me : client) {
                    if (me instanceof Analysis) {
                        Analysis analysis = (Analysis) me;
                        TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(
                                analysis, rule);
                        if (dependencyReturn.isOk()) {
                            RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(analysis);
                            if (repositoryNode != null) {
                                TDQAnalysisItem analysisItem = (TDQAnalysisItem) repositoryNode.getObject().getProperty()
                                        .getItem();
                                analysisItem.setAnalysis(analysis);
                            }
                            ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
                                    .saveResource(analysis.eResource());
                        }
                    }
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        // update client dependency
        // if DQRule have client depencency, add codes here
    }

}
