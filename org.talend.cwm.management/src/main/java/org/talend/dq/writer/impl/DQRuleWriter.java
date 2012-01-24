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
package org.talend.dq.writer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.AbstractResourceChangesService;
import org.talend.core.repository.utils.TDQServiceRegister;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.AElementPersistance;
import org.talend.utils.sugars.ReturnCode;
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
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.DQRULE;
    }

    public ReturnCode save(Item item, boolean... careDependency) {
        ReturnCode rc = new ReturnCode();
        try {
            TDQBusinessRuleItem ruleItem = (TDQBusinessRuleItem) item;
            DQRule rule = ruleItem.getDqrule();
            addDependencies(rule);

            Map<EObject, Collection<Setting>> find = EcoreUtil.ExternalCrossReferencer.find(rule.eResource());
            List<Resource> needSaves = new ArrayList<Resource>();
            for (EObject object : find.keySet()) {
                Resource re = object.eResource();
                if (re == null) {
                    continue;
                }
                EcoreUtil.resolveAll(re);
                needSaves.add(re);
            }

            ProxyRepositoryFactory.getInstance().save(ruleItem);

            AbstractResourceChangesService resChangeService = TDQServiceRegister.getInstance().getResourceChangeService(
                    AbstractResourceChangesService.class);
            if (resChangeService != null) {
                for (Resource toSave : needSaves) {
                    resChangeService.saveResourceByEMFShared(toSave);
                }
            }

            // updateDependencies(rule);
        } catch (PersistenceException e) {
            log.error(e, e);
            rc.setOk(Boolean.FALSE);
            rc.setMessage(e.getMessage());
        }
        return rc;
    }

    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#updateDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    // @Override
    // protected void updateDependencies(ModelElement element) {
    // DQRule rule = (DQRule) element;
    // // update supplier dependency
    // EList<Dependency> supplierDependency = rule.getSupplierDependency();
    // try {
    // for (Dependency sDependency : supplierDependency) {
    // EList<ModelElement> client = sDependency.getClient();
    // for (ModelElement me : client) {
    // if (me instanceof Analysis) {
    // Analysis analysis = (Analysis) me;
    // TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(
    // analysis, rule);
    // if (dependencyReturn.isOk()) {
    // RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(analysis);
    // if (repositoryNode != null) {
    // TDQAnalysisItem analysisItem = (TDQAnalysisItem) repositoryNode.getObject().getProperty()
    // .getItem();
    // analysisItem.setAnalysis(analysis);
    // }
    // ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
    // .saveResource(analysis.eResource());
    // }
    // }
    // }
    // }
    // } catch (PersistenceException e) {
    // log.error(e, e);
    // }
    // // update client dependency
    // // if DQRule have client depencency, add codes here
    // }

}
