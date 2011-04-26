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
package org.talend.dataprofiler.core.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.AbstractResourceChangesService;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
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
import org.talend.resource.ResourceManager;
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

        // MOD qiongli 2011-1-28 bug 16776,add asyncExec(new Runnable).
        final ModelElement modelElementFinal = modelElement;
        List<ModelElement> clientDependencys = EObjectHelper.getDependencyClients(modelElement);
        if (clientDependencys.size() > 0) {
            final ModelElement[] dependencyElements = clientDependencys.toArray(new ModelElement[clientDependencys.size()]);

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    DeleteModelElementConfirmDialog.showDialog(null,
                            PropertyHelper.getItemFile(PropertyHelper.getProperty(modelElementFinal)), dependencyElements,
                            DefaultMessagesImpl.getString("TDQResourceChangeHandler.ConnectionNotBeSave"),false); //$NON-NLS-1$
                }
            });

            return false;
        }

        return super.handleResourceChange(modelElement);
    }

    public Resource create(IProject project, Item item, int classID, IPath path) {
        String fileExtension = FileConstants.ITEM_EXTENSION;
        Resource itemResource = null;
        try {
            switch (classID) {
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_ANALYSIS_ITEM:
                fileExtension = FileConstants.ANA_EXTENSION;
                itemResource = xmiResourceManager.createItemResourceWithExtension(project, item, path,
                        ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT, false, fileExtension);

                AnalysisWriter createAnalysisWrite = org.talend.dq.writer.impl.ElementWriterFactory.getInstance()
                        .createAnalysisWrite();
                Analysis analysis = ((TDQAnalysisItem) item).getAnalysis();
                createAnalysisWrite.addResourceContent(itemResource, analysis);
                createAnalysisWrite.addDependencies(analysis);

                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_REPORT_ITEM:
                fileExtension = FileConstants.REP_EXTENSION;
                itemResource = xmiResourceManager.createItemResourceWithExtension(project, item, path,
                        ERepositoryObjectType.TDQ_REPORT_ELEMENT, false, fileExtension);

                ReportWriter createReportWriter = org.talend.dq.writer.impl.ElementWriterFactory.getInstance()
                        .createReportWriter();
                Report report = ((TDQReportItem) item).getReport();
                createReportWriter.addResourceContent(itemResource, report);
                createReportWriter.addDependencies(report);

                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_INDICATOR_DEFINITION_ITEM:
                fileExtension = FileConstants.DEF_EXTENSION;
                itemResource = xmiResourceManager.createItemResourceWithExtension(project, item, path,
                        ERepositoryObjectType.TDQ_INDICATOR_ELEMENT, false, fileExtension);

                IndicatorDefinition indicatorDefinition = ((TDQIndicatorDefinitionItem) item).getIndicatorDefinition();
                IndicatorDefinitionWriter createIndicatorDefinitionWriter = org.talend.dq.writer.impl.ElementWriterFactory
                        .getInstance().createIndicatorDefinitionWriter();
                createIndicatorDefinitionWriter.addResourceContent(itemResource, indicatorDefinition);
                // createIndicatorDefinitionWriter.addDependencies(indicatorDefinition);

                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_PATTERN_ITEM:
                fileExtension = FileConstants.PAT_EXTENSION;
                itemResource = xmiResourceManager.createItemResourceWithExtension(project, item, path,
                        ERepositoryObjectType.TDQ_PATTERN_ELEMENT, false, fileExtension);

                Pattern pattern = ((TDQPatternItem) item).getPattern();
                PatternWriter createPatternWriter = org.talend.dq.writer.impl.ElementWriterFactory.getInstance()
                        .createPatternWriter();
                createPatternWriter.addResourceContent(itemResource, pattern);
                // createPatternWriter.addDependencies(pattern);

                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_BUSINESS_RULE_ITEM:
                fileExtension = FileConstants.RULE_EXTENSION;
                itemResource = xmiResourceManager.createItemResourceWithExtension(project, item, path,
                        ERepositoryObjectType.TDQ_RULES_SQL, false, fileExtension);

                DQRule dqrule = ((TDQBusinessRuleItem) item).getDqrule();
                DQRuleWriter createdRuleWriter = org.talend.dq.writer.impl.ElementWriterFactory.getInstance().createdRuleWriter();
                createdRuleWriter.addResourceContent(itemResource, dqrule);

                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_JRXML_ITEM:
                fileExtension = FileConstants.JRXML_EXTENSION;
                itemResource = xmiResourceManager.createItemResourceWithExtension(project, item, path,
                        ERepositoryObjectType.TDQ_JRAXML_ELEMENT, true, fileExtension);
                itemResource.getContents().add(((TDQFileItem) item).getContent());

                break;
            case org.talend.dataquality.properties.PropertiesPackage.TDQ_SOURCE_FILE_ITEM:
                fileExtension = FileConstants.SQL_EXTENSION;
                itemResource = xmiResourceManager.createItemResourceWithExtension(project, item, path,
                        ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT, true, fileExtension);
                itemResource.getContents().add(((TDQFileItem) item).getContent());

                break;
            default:
            }

        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return itemResource;
    }

    @Override
    public boolean isAnalysisOrReportItem(Item item) {
        String path = item.getState().getPath();
        if (path != null) {
            if (item instanceof TDQAnalysisItem) {
                return path.equals(ResourceManager.getAnalysisFolder().getFullPath().toString());
            } else if (item instanceof TDQReportItem) {
                return path.equals(ResourceManager.getReportsFolder().getFullPath().toString());
            }
        }
        return super.isAnalysisOrReportItem(item);
    }
}
