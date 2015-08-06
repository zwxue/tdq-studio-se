// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
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

    public ReturnCode save(Item item, boolean careDependency) {
        TDQIndicatorDefinitionItem indicatorItem = (TDQIndicatorDefinitionItem) item;
        IndicatorDefinition indiDefinition = indicatorItem.getIndicatorDefinition();
        // MOD yyi 2012-02-07 TDQ-4621:Update dependencies when careDependency is true.
        ReturnCode returnCode = careDependency ? saveWithDependencies(indicatorItem, indiDefinition) : saveWithoutDependencies(
                indicatorItem,
                indiDefinition);
        if (returnCode.isOk()) {
            this.updateDependencies(indiDefinition);
        }
        return returnCode;
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
