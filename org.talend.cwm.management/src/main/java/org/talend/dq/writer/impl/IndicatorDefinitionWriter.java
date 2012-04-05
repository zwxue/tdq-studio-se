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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.AbstractResourceChangesService;
import org.talend.core.repository.utils.TDQServiceRegister;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dq.helper.PropertyHelper;
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
            Set<Resource> needSaves = new HashSet<Resource>();
            for (EObject object : find.keySet()) {
                Resource re = object.eResource();
                if (re == null) {
                    continue;
                }
                needSaves.add(re);
            }

            ProxyRepositoryFactory.getInstance().save(indicatorItem);

            AbstractResourceChangesService resChangeService = TDQServiceRegister.getInstance().getResourceChangeService(
                    AbstractResourceChangesService.class);
            if (resChangeService != null) {
                for (Resource toSave : needSaves) {
                    EcoreUtil.resolveAll(toSave);
                    resChangeService.saveResourceByEMFShared(toSave);
                }
            }

            updateDependencies(indiDefinition);
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

    protected void updateDependencies(ModelElement element) {
        // update client dependency
        // if IndicatorDefinition have client depencency, add codes here
        IndicatorDefinition definition = (IndicatorDefinition) element;
        Property property = PropertyHelper.getProperty(definition);
        List<IRepositoryViewObject> listIndicatorDependency = DependenciesHandler
                .getIndicatorDependency(new RepositoryViewObject(property));
        for (IRepositoryViewObject viewObject : listIndicatorDependency) {
            Item item = viewObject.getProperty().getItem();
            if (item instanceof TDQAnalysisItem) {
                try {
                    ProxyRepositoryFactory.getInstance().save(item);
                } catch (PersistenceException e) {
                    log.error(e, e);
                }
            }
        }
    }
}
