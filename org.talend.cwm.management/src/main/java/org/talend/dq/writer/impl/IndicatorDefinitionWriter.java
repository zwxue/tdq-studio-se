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

import org.apache.log4j.Logger;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.Item;
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

    public ReturnCode save(Item item, boolean careDependency) {
        TDQIndicatorDefinitionItem indicatorItem = (TDQIndicatorDefinitionItem) item;
        IndicatorDefinition indiDefinition = indicatorItem.getIndicatorDefinition();
        // MOD yyi 2012-02-07 TDQ-4621:Update dependencies when careDependency is true.
        return careDependency ? saveWithDependencies(indicatorItem, indiDefinition) : saveWithoutDependencies(indicatorItem,
                indiDefinition);
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
