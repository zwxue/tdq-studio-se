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
package org.talend.dq.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dataquality.reports.TdReport;
import org.talend.dataquality.rules.DQRule;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * This factory for getting the <code>ModelElement</code> from <code>IFile</code>.
 */
public final class ModelElementFileFactory {

    private ModelElementFileFactory() {

    }

    // private static Logger log = Logger.getLogger(ModelElementFileFactory.class);

    /**
     * DOC bZhou Comment method "getModelElement".
     * 
     * @param file
     * @return
     */
    public static ModelElement getModelElement(IFile file) {
        ModelElement modelElement = null;
        String fileExtension = file.getFileExtension();
        if (FactoriesUtil.isAnalysisFile(fileExtension)) {
            modelElement = AnaResourceFileHelper.getInstance().findAnalysis(file);
        } else if (FactoriesUtil.isReportFile(fileExtension)) {
            modelElement = RepResourceFileHelper.getInstance().findReport(file);
        } else if (FactoriesUtil.isDQRuleFile(fileExtension)) {
            modelElement = DQRuleResourceFileHelper.getInstance().findWhereRule(file);
        } else if (FactoriesUtil.isPatternFile(fileExtension)) {
            modelElement = PatternResourceFileHelper.getInstance().findPattern(file);
        } else if (FactoriesUtil.isUDIFile(fileExtension)) {
            modelElement = IndicatorResourceFileHelper.getInstance().findIndDefinition(file);
        } else if (FactoriesUtil.isItemFile(fileExtension)) {
            IPath filePath = file.getFullPath().removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
            if (!file.getParent().getFile(filePath.removeFirstSegments(filePath.segmentCount() - 1)).getLocation().toFile()
                    .exists()) {
                return modelElement;
            } else {
                Property itemProperty = PropertyHelper.getProperty(ResourceManager.getRoot().getFile(filePath));
                modelElement = PropertyHelper.retrieveElement(itemProperty.getItem());
            }
        }

        return modelElement;
    }

    /**
     * DOC zshen Comment method "getModelElement".
     * 
     * @param file
     * @return
     */
    public static ModelElement getModelElement(IRepositoryViewObject repositoryObject) {
        ModelElement modelElement = null;
        Item theItem = repositoryObject.getProperty().getItem();
        if (theItem instanceof ConnectionItem) {
            modelElement = ((ConnectionItem) theItem).getConnection();
        }
        // String fileExtension = file.getFileExtension();
        // if (FactoriesUtil.isAnalysisFile(fileExtension)) {
        // modelElement = AnaResourceFileHelper.getInstance().findAnalysis(file);
        // } else if (FactoriesUtil.isReportFile(fileExtension)) {
        // modelElement = RepResourceFileHelper.getInstance().findReport(file);
        // } else if (FactoriesUtil.isDQRuleFile(fileExtension)) {
        // modelElement = DQRuleResourceFileHelper.getInstance().findWhereRule(file);
        // } else if (FactoriesUtil.isPatternFile(fileExtension)) {
        // modelElement = PatternResourceFileHelper.getInstance().findPattern(file);
        // } else if (FactoriesUtil.isUDIFile(fileExtension)) {
        // modelElement = IndicatorResourceFileHelper.getInstance().findIndDefinition(file);
        // }

        return modelElement;
    }

    /**
     * DOC bZhou Comment method "getResourceFileMap".
     * 
     * @param file
     * @return
     */
    public static ResourceFileMap getResourceFileMap(IFile file) {
        ResourceFileMap modelElement = null;

        String fileExtension = file.getFileExtension();
        if (FactoriesUtil.isAnalysisFile(fileExtension)) {
            modelElement = AnaResourceFileHelper.getInstance();
        } else if (FactoriesUtil.isReportFile(fileExtension)) {
            modelElement = RepResourceFileHelper.getInstance();
        } else if (FactoriesUtil.isDQRuleFile(fileExtension)) {
            modelElement = DQRuleResourceFileHelper.getInstance();
        } else if (FactoriesUtil.isPatternFile(fileExtension)) {
            modelElement = PatternResourceFileHelper.getInstance();
        } else if (FactoriesUtil.isUDIFile(fileExtension)) {
            modelElement = IndicatorResourceFileHelper.getInstance();
        }
        return modelElement;
    }

    /**
     * DOC bZhou Comment method "getResourceFileMap".
     * 
     * @param element
     * @return
     */
    public static ResourceFileMap getResourceFileMap(ModelElement element) {
        ResourceFileMap resourceMap = null;
        if (element instanceof Analysis) {
            resourceMap = AnaResourceFileHelper.getInstance();
        } else if (element instanceof Report) {
            resourceMap = RepResourceFileHelper.getInstance();
        } else if (element instanceof DQRule) {
            resourceMap = DQRuleResourceFileHelper.getInstance();
        } else if (element instanceof Pattern) {
            resourceMap = PatternResourceFileHelper.getInstance();
        } else if (element instanceof UserDefIndicator) {
            resourceMap = IndicatorResourceFileHelper.getInstance();
        }
        return resourceMap;
    }

    /**
     * DOC bZhou Comment method "getModelElements".
     * 
     * @param files
     * @return
     */
    public static ModelElement[] getModelElements(IFile[] files) {
        List<ModelElement> modelElements = new ArrayList<ModelElement>();
        ModelElement element;
        for (IFile file : files) {
            element = getModelElement(file);
            if (element != null) {
                modelElements.add(element);
            }
        }
        return modelElements.toArray(new ModelElement[modelElements.size()]);
    }

    /**
     * DOC bZhou Comment method "getALLElements".
     * 
     * @param withSystem
     * @return
     */
    public static ModelElement[] getALLElements(boolean withSystem) {
        Collection<Analysis> allAnalysis = AnaResourceFileHelper.getInstance().getAllAnalysis();

        Collection<WhereRule> allDQRules = DQRuleResourceFileHelper.getInstance().getAllDQRules();

        Collection<Pattern> allPatternes = PatternResourceFileHelper.getInstance().getAllPatternes();

        Collection<Connection> allDataProviders = ProxyRepositoryViewObject.getAllMetadataConnections();

        Collection<TdReport> allReports = RepResourceFileHelper.getInstance().getAllReports();

        Collection<IndicatorDefinition> allUDIs = IndicatorResourceFileHelper.getInstance().getAllUDIs();

        List<ModelElement> allElement = new ArrayList<ModelElement>();
        allElement.addAll(allAnalysis);
        allElement.addAll(allDQRules);
        allElement.addAll(allPatternes);
        allElement.addAll(allDataProviders);
        allElement.addAll(allReports);
        allElement.addAll(allUDIs);

        return allElement.toArray(new ModelElement[allElement.size()]);
    }
}
