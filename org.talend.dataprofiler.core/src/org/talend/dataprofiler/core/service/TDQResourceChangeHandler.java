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
package org.talend.dataprofiler.core.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.AbstractResourceChangesService;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.properties.TDQFileItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.DQRuleWriter;
import org.talend.dq.writer.impl.IndicatorDefinitionWriter;
import org.talend.dq.writer.impl.PatternWriter;
import org.talend.dq.writer.impl.ReportWriter;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * 
 * DOC mzhao Handle resource unload events from TOS.
 */
public class TDQResourceChangeHandler extends AbstractResourceChangesService {

    private static Logger log = Logger.getLogger(TDQResourceChangeHandler.class);

    private XmiResourceManager xmiResourceManager = ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider()
            .getResourceManager();

    public TDQResourceChangeHandler() {
    }

    @Override
    public void handleUnload(Resource toBeUnloadedResource) {
        for (EObject eObject : toBeUnloadedResource.getContents()) {
            // try {
            if (eObject instanceof DatabaseConnection) {
                // ProxyRepositoryViewObject.registerURI((DatabaseConnection) eObject, toBeUnloadedResource.getURI());
                // if (xmiResourceManager != null) {
                // try {
                // xmiResourceManager.saveResource(toBeUnloadedResource);
                // } catch (PersistenceException e) {
                // log.error(e, e);
                // }
                //
                // }
            } else if (eObject instanceof MDMConnection) {

                // ProxyRepositoryViewObject.registerURI((MDMConnection) eObject, toBeUnloadedResource.getURI());
                // if (xmiResourceManager != null) {
                // try {
                // xmiResourceManager.saveResource(toBeUnloadedResource);
                // } catch (PersistenceException e) {
                // log.error(e, e);
                // }
                //
                // }
            }

            // } catch (PersistenceException e) {
            // log.error(e, e);
            // }
            // else anaysis,report etc.
        }
        super.handleUnload(toBeUnloadedResource);
    }

    @Override
    public boolean handleResourceChange(ModelElement modelElement) {
        List<ModelElement> clientDependencys = EObjectHelper.getDependencyClients(modelElement);
        if (clientDependencys.size() > 0) {
            ModelElement[] dependencyElements = clientDependencys.toArray(new ModelElement[clientDependencys.size()]);

            DeleteModelElementConfirmDialog.showDialog(null,
                    PropertyHelper.getItemFile(PropertyHelper.getProperty(modelElement)), dependencyElements,
                    DefaultMessagesImpl.getString("TDQResourceChangeHandler.ConnectionNotBeSave"));
            return false;
        }

        // TODO Handle element deletion from resource, resource delete.
        return super.handleResourceChange(modelElement);

    }

    /**
     * add a prop instance to the static var.
     */
    public void handleLogicalDelete(Property prop) {
        // MOD qiongli 2010-12-6.bug 16843.can not be restored for svn project when it is prox property.
        if (prop == null)
            return;
        if (prop.eIsProxy()) {
            prop = (Property) EObjectHelper.resolveObject(prop);
        }
        // MOD qiongli 2010-10-22,bug 16610
        // ProxyRepositoryViewObject.fetchAllRepositoryViewObjects(true, true);
        if (prop != null) {
            LogicalDeleteFileHandle.refreshDelPropertys(1, prop);
        }
    }

    /**
     * remove prop instance in the static var.
     */
    public void handlePhysicalDelete(Property prop) {
        LogicalDeleteFileHandle.refreshDelPropertys(0, prop);
    }

    /**
     * remove prop instance in the static var.
     */
    public void handleRestore(Property prop) {
        LogicalDeleteFileHandle.refreshDelPropertys(0, prop);
    }

    public Resource create(IProject project, Item item, int classID, IPath path) {
        String fileExtension = FileConstants.ITEM_EXTENSION;
        Resource itemResource = null;
        try {
            switch (classID) {
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_ANALYSIS_ITEM:
                fileExtension = FileConstants.ANA_EXTENSION;
                itemResource = ProxyRepositoryFactory
                        .getInstance()
                        .getRepositoryFactoryFromProvider()
                        .getResourceManager()
                        .createItemResourceWithExtension(project, item, path, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT, false,
                                fileExtension);

                AnalysisWriter createAnalysisWrite = org.talend.dq.writer.impl.ElementWriterFactory.getInstance()
                        .createAnalysisWrite();
                Analysis analysis = ((TDQAnalysisItem) item).getAnalysis();
                createAnalysisWrite.addResourceContent(itemResource, analysis);
                createAnalysisWrite.addDependencies(analysis);
                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_REPORT_ITEM:
                fileExtension = FileConstants.REP_EXTENSION;
                itemResource = ProxyRepositoryFactory
                        .getInstance()
                        .getRepositoryFactoryFromProvider()
                        .getResourceManager()
                        .createItemResourceWithExtension(project, item, path, ERepositoryObjectType.TDQ_REPORT_ELEMENT, false,
                                fileExtension);
                ReportWriter createReportWriter = org.talend.dq.writer.impl.ElementWriterFactory.getInstance()
                        .createReportWriter();
                Report report = ((TDQReportItem) item).getReport();
                createReportWriter.addResourceContent(itemResource, report);
                createReportWriter.addDependencies(report);
                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_INDICATOR_DEFINITION_ITEM:
                fileExtension = FileConstants.DEF_EXTENSION;
                itemResource = ProxyRepositoryFactory
                        .getInstance()
                        .getRepositoryFactoryFromProvider()
                        .getResourceManager()
                        .createItemResourceWithExtension(project, item, path, ERepositoryObjectType.TDQ_INDICATOR_ELEMENT, false,
                                fileExtension);
                IndicatorDefinition indicatorDefinition = ((TDQIndicatorDefinitionItem) item).getIndicatorDefinition();
                IndicatorDefinitionWriter createIndicatorDefinitionWriter = org.talend.dq.writer.impl.ElementWriterFactory
                        .getInstance().createIndicatorDefinitionWriter();
                createIndicatorDefinitionWriter.addResourceContent(itemResource, indicatorDefinition);
                // createIndicatorDefinitionWriter.addDependencies(indicatorDefinition);
                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_PATTERN_ITEM:
                fileExtension = FileConstants.PAT_EXTENSION;
                itemResource = ProxyRepositoryFactory
                        .getInstance()
                        .getRepositoryFactoryFromProvider()
                        .getResourceManager()
                        .createItemResourceWithExtension(project, item, path, ERepositoryObjectType.TDQ_PATTERN_ELEMENT, false,
                                fileExtension);
                Pattern pattern = ((TDQPatternItem) item).getPattern();
                PatternWriter createPatternWriter = org.talend.dq.writer.impl.ElementWriterFactory.getInstance()
                        .createPatternWriter();
                createPatternWriter.addResourceContent(itemResource, pattern);
                // createPatternWriter.addDependencies(pattern);
                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_BUSINESS_RULE_ITEM:
                fileExtension = FileConstants.RULE_EXTENSION;
                itemResource = ProxyRepositoryFactory
                        .getInstance()
                        .getRepositoryFactoryFromProvider()
                        .getResourceManager()
                        .createItemResourceWithExtension(project, item, path, ERepositoryObjectType.TDQ_RULES_SQL, false,
                                fileExtension);
                DQRule dqrule = ((TDQBusinessRuleItem) item).getDqrule();
                DQRuleWriter createdRuleWriter = org.talend.dq.writer.impl.ElementWriterFactory.getInstance().createdRuleWriter();
                createdRuleWriter.addResourceContent(itemResource, dqrule);

                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_JRXML_ITEM:
                fileExtension = FileConstants.JRXML_EXTENSION;
                itemResource = ProxyRepositoryFactory
                        .getInstance()
                        .getRepositoryFactoryFromProvider()
                        .getResourceManager()
                        .createItemResourceWithExtension(project, item, path, ERepositoryObjectType.TDQ_JRXMLTEMPLATE, true,
                                fileExtension);
                itemResource.getContents().add(((TDQFileItem) item).getContent());
                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_SOURCE_FILE_ITEM:
                fileExtension = FileConstants.SQL_EXTENSION;
                itemResource = ProxyRepositoryFactory
                        .getInstance()
                        .getRepositoryFactoryFromProvider()
                        .getResourceManager()
                        .createItemResourceWithExtension(project, item, path, ERepositoryObjectType.TDQ_SOURCE_FILES, true,
                                fileExtension);
                itemResource.getContents().add(((TDQFileItem) item).getContent());
                break;
            default:
            }

        } catch (PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return itemResource;
    }
}
