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

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
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
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.reports.TdReport;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.writer.AElementPersistance;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.RepositoryNode;
import org.talend.top.repository.ProxyRepositoryManager;
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
                        if (property != null) {
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

    public void addResourceContent(Resource resource, Analysis element) {
        super.addResourceContent(resource, element);

        if (resource != null) {
            EList<EObject> resourceContents = resource.getContents();

            EList<Domain> dataFilter = AnalysisHelper.getDataFilter(element);
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

    public ReturnCode save(Item item) {
        ReturnCode rc = new ReturnCode();
        try {
            TDQAnalysisItem anaItem = (TDQAnalysisItem) item;
            Analysis analysis = anaItem.getAnalysis();
            addDependencies(analysis);
            addResourceContent(analysis.eResource(), analysis);
            anaItem.setAnalysis(analysis);
            // MOD klliu 2011-02-15
            Project currentProject = ProjectManager.getInstance().getCurrentProject();
            ProxyRepositoryFactory.getInstance().save(currentProject, anaItem);
            // super.save(anaItem);
            updateDependencies(analysis);
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
        Analysis analysis = (Analysis) element;
        // update supplier dependency
        EList<Dependency> supplierDependency = analysis.getSupplierDependency();
        try {
            for (Dependency sDependency : supplierDependency) {
                EList<ModelElement> client = sDependency.getClient();
                for (ModelElement me : client) {
                    if (me instanceof TdReport) {
                        TdReport report = (TdReport) me;
                        TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(report,
                                analysis);
                        if (dependencyReturn.isOk()) {
                            RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(report);
                            if (repositoryNode != null) {
                                TDQReportItem reportItem = (TDQReportItem) repositoryNode.getObject().getProperty().getItem();
                                reportItem.setReport(report);
                            }
                            ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
                                    .saveResource(report.eResource());
                        }
                    }
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        // update client dependency
        EList<Dependency> clientDependency = analysis.getClientDependency();
        try {
            for (Dependency cDependency : clientDependency) {
                EList<ModelElement> supplier = cDependency.getSupplier();
                for (ModelElement me : supplier) {
                    if (me instanceof Connection) {
                        Connection connection = (Connection) me;
                        TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(
                                analysis, connection);
                        if (dependencyReturn.isOk()) {
                            RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(connection);
                            if (repositoryNode != null) {
                                ConnectionItem connectionItem = (ConnectionItem) repositoryNode.getObject().getProperty()
                                        .getItem();
                                connectionItem.setConnection(connection);
                            }
                            ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
                                    .saveResource(connection.eResource());
                        }
                    } else if (me instanceof IndicatorDefinition) {
                        IndicatorDefinition udi = (IndicatorDefinition) me;
                        TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(
                                analysis, udi);
                        if (dependencyReturn.isOk()) {
                            RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(udi);
                            if (repositoryNode != null) {
                                TDQItem udiItem = (TDQItem) repositoryNode.getObject().getProperty().getItem();
                                if (udiItem instanceof TDQIndicatorDefinitionItem) {
                                    ((TDQIndicatorDefinitionItem) udiItem).setIndicatorDefinition(udi);
                                } else if (udiItem instanceof TDQBusinessRuleItem) {
                                    ((TDQBusinessRuleItem) udiItem).setDqrule((DQRule) udi);
                                }
                            }
                            ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
                                    .saveResource(udi.eResource());
                        }
                    } else if (me instanceof Pattern) {
                        Pattern pattern = (Pattern) me;
                        TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(
                                analysis, pattern);
                        if (dependencyReturn.isOk()) {
                            RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(pattern);
                            if (repositoryNode != null) {
                                TDQPatternItem patternItem = (TDQPatternItem) repositoryNode.getObject().getProperty().getItem();
                                patternItem.setPattern(pattern);
                            }
                            ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
                                    .saveResource(pattern.eResource());
                        }
                    }
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }

    }
}
