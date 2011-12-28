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
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.AElementPersistance;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorDefinitionWriter extends AElementPersistance {

    private Logger log = Logger.getLogger(IndicatorDefinitionWriter.class);

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
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.DEFINITION;
    }

    public ReturnCode save(Item item, boolean... careDependency) {
        ReturnCode rc = new ReturnCode();
        try {
            TDQIndicatorDefinitionItem indicatorItem = (TDQIndicatorDefinitionItem) item;
            IndicatorDefinition indiDefinition = indicatorItem.getIndicatorDefinition();
            addDependencies(indiDefinition);
            addResourceContent(indiDefinition.eResource(), indiDefinition);
            indicatorItem.setIndicatorDefinition(indiDefinition);

            Map<EObject, Collection<Setting>> find = EcoreUtil.ExternalCrossReferencer.find(indiDefinition.eResource());
            List<Resource> needSaves = new ArrayList<Resource>();
            for (EObject object : find.keySet()) {
                Resource re = object.eResource();
                if (re == null) {
                    continue;
                }
                EcoreUtil.resolveAll(re);
                needSaves.add(re);
            }

            ProxyRepositoryFactory.getInstance().save(indicatorItem);
            
            AbstractResourceChangesService resChangeService = TDQServiceRegister.getInstance().getResourceChangeService(
                    AbstractResourceChangesService.class);
            if (resChangeService != null) {
                for (Resource toSave : needSaves) {
                    resChangeService.saveResourceByEMFShared(toSave);
                }
            }

            // updateDependencies(indiDefinition);
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
    // IndicatorDefinition definition = (IndicatorDefinition) element;
    // // update supplier dependency
    // EList<Dependency> supplierDependency = definition.getSupplierDependency();
    // try {
    // for (Dependency sDependency : supplierDependency) {
    // EList<ModelElement> client = sDependency.getClient();
    // for (ModelElement me : client) {
    // if (me instanceof Analysis) {
    // Analysis analysis = (Analysis) me;
    // TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(
    // analysis, definition);
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
    // // if IndicatorDefinition have client depencency, add codes here
    // }
}
