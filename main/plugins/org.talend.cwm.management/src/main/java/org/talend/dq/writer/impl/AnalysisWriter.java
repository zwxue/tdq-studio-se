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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.AElementPersistance;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author scorreia
 * 
 * This class saves the analysis.
 */
public class AnalysisWriter extends AElementPersistance {

    static Logger log = Logger.getLogger(AnalysisWriter.class);

    /**
     * DOC bZhou AnalysisWriter constructor comment.
     */
    AnalysisWriter() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    public void addDependencies(ModelElement element) {
        Analysis analysis = (Analysis) element;

        List<IndicatorDefinition> udis = AnalysisHelper.getUserDefinedIndicators(analysis);
        try {
            for (IndicatorDefinition udi : udis) {
                if (udi == null) {
                    continue;
                }
                InternalEObject iudi = (InternalEObject) udi;
                if (!iudi.eIsProxy()) {
                    TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(analysis,
                            udi);
                    if (dependencyReturn.isOk()) {
                        Property property = PropertyHelper.getProperty(udi);
                        if (property != null) {
                            TDQItem udiItem = (TDQItem) property.getItem();
                            if (udiItem instanceof TDQIndicatorDefinitionItem) {
                                ((TDQIndicatorDefinitionItem) udiItem).setIndicatorDefinition(udi);
                            } else if (udiItem instanceof TDQBusinessRuleItem) {
                                ((TDQBusinessRuleItem) udiItem).setDqrule((DQRule) udi);
                            }
                        }
                        ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
                                .saveResource(udi.eResource());
                    }
                }
            }

            List<Pattern> patterns = AnalysisHelper.getPatterns(analysis);
            for (Pattern pattern : patterns) {
                InternalEObject iptn = (InternalEObject) pattern;
                if (!iptn.eIsProxy()) {
                    TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(analysis,
                            pattern);
                    if (dependencyReturn.isOk()) {
                        Property property = PropertyHelper.getProperty(pattern);
                        if (property != null && property.getItem() instanceof TDQPatternItem) {
                            TDQPatternItem patternItem = (TDQPatternItem) property.getItem();
                            patternItem.setPattern(pattern);
                        }
                        ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
                                .saveResource(pattern.eResource());

                        // EMFUtil.saveSingleResource(pattern.eResource());
                    }
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addResourceContent(orgomg.cwm.objectmodel.core.ModelElement)
     * 
     * @deprecated
     */
    @Override
    protected void addResourceContent(ModelElement element) {
        Resource resource = element.eResource();
        if (resource != null) {
            EList<EObject> resourceContents = resource.getContents();
            EList<Domain> dataFilter = AnalysisHelper.getDataFilter((Analysis) element);
            List<Domain> domains = DomainHelper.getDomains(resourceContents);
            resourceContents.removeAll(domains);

            if (dataFilter != null) {
                // scorreia save them in their own file? -> no, it's ok to save them
                // in the analysis file.
                for (Domain domain : dataFilter) {
                    if (!resourceContents.contains(domain)) {
                        resourceContents.add(domain);
                    }
                }
            }
        }
    }

    @Override
    public void addResourceContent(Resource resource, ModelElement element) {
        super.addResourceContent(resource, element);

        if (resource != null) {
            EList<EObject> resourceContents = resource.getContents();

            EList<Domain> dataFilter = AnalysisHelper.getDataFilter((Analysis) element);
            if (dataFilter != null) {
                // scorreia save them in their own file? -> no, it's ok to save them
                // in the analysis file.
                for (Domain domain : dataFilter) {
                    if (!resourceContents.contains(domain)) {
                        resourceContents.add(domain);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.ANA;
    }

    /**
     * Save analysis item and update the dependencies(optional). <B>To update dependencies of the analysis the
     * #careDependency must be set as TRUE.</B>
     * 
     * @see org.talend.dq.writer.AElementPersistance#save(org.talend.core.model.properties.Item, boolean[])
     */
    public ReturnCode save(Item item, boolean careDependency) {
        // MOD yyi 2012-02-07 TDQ-4621:Update dependencies(connection) when careDependency is true.
        TDQAnalysisItem anaItem = (TDQAnalysisItem) item;
        Analysis analysis = anaItem.getAnalysis();
        return careDependency ? saveWithDependencies(anaItem, analysis) : saveWithoutDependencies(anaItem, analysis);
    }

    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save();

    }

}
