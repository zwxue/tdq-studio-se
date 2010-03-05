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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.utils.sugars.TypedReturnCode;
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
        if (FactoriesUtil.isProvFile(fileExtension)) {
            TypedReturnCode<TdDataProvider> returnValue = PrvResourceFileHelper.getInstance().findProvider(file);
            modelElement = returnValue.getObject();
        } else if (FactoriesUtil.isAnalysisFile(fileExtension)) {
            modelElement = AnaResourceFileHelper.getInstance().findAnalysis(file);
        } else if (FactoriesUtil.isReportFile(fileExtension)) {
            modelElement = RepResourceFileHelper.getInstance().findReport(file);
        } else if (FactoriesUtil.isDQRuleFile(fileExtension)) {
            modelElement = DQRuleResourceFileHelper.getInstance().findWhereRule(file);
        } else if (FactoriesUtil.isPatternFile(fileExtension)) {
            modelElement = PatternResourceFileHelper.getInstance().findPattern(file);
        } else if (FactoriesUtil.isUDIFile(fileExtension)) {
            modelElement = UDIResourceFileHelper.getInstance().findUDI(file);
        }

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
        if (FactoriesUtil.isProvFile(fileExtension)) {
            modelElement = PrvResourceFileHelper.getInstance();
        } else if (FactoriesUtil.isAnalysisFile(fileExtension)) {
            modelElement = AnaResourceFileHelper.getInstance();
        } else if (FactoriesUtil.isReportFile(fileExtension)) {
            modelElement = RepResourceFileHelper.getInstance();
        } else if (FactoriesUtil.isDQRuleFile(fileExtension)) {
            modelElement = DQRuleResourceFileHelper.getInstance();
        } else if (FactoriesUtil.isPatternFile(fileExtension)) {
            modelElement = PatternResourceFileHelper.getInstance();
        } else if (FactoriesUtil.isUDIFile(fileExtension)) {
            modelElement = UDIResourceFileHelper.getInstance();
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
        if (element instanceof TdDataProvider) {
            resourceMap = PrvResourceFileHelper.getInstance();
        } else if (element instanceof Analysis) {
            resourceMap = AnaResourceFileHelper.getInstance();
        } else if (element instanceof Report) {
            resourceMap = RepResourceFileHelper.getInstance();
        } else if (element instanceof DQRule) {
            resourceMap = DQRuleResourceFileHelper.getInstance();
        } else if (element instanceof Pattern) {
            resourceMap = PatternResourceFileHelper.getInstance();
        } else if (element instanceof UserDefIndicator) {
            resourceMap = UDIResourceFileHelper.getInstance();
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
     * DOC bZhou Comment method "getProperty".
     * 
     * @param file
     * @return null if there is no property reference to this file.
     */
    public static Property getProperty(IFile file) {

        ModelElement modelElement = getModelElement(file);
        if (modelElement != null) {
            String propertyPath = MetadataHelper.getPropertyPath(modelElement);
            if (propertyPath != null) {
                IFile propertyFile = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(propertyPath);
                if (propertyFile != null) {
                    return loadProperty(propertyFile);
                }
            }
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "loadProperty".
     * 
     * @param file
     * @return null if property is not existed.
     */
    static Property loadProperty(IFile file) {
        URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
        Resource resource = EMFSharedResources.getInstance().getResource(uri, true);

        if (resource != null) {
            Property property = (Property) EcoreUtil.getObjectByType(resource.getContents(), PropertiesPackage.eINSTANCE
                    .getProperty());
            return property;
        }

        return null;
    }
}
