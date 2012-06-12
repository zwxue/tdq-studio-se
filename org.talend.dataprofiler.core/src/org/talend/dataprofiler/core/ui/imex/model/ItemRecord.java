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
package org.talend.dataprofiler.core.ui.imex.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.emf.FactoriesUtil.EElementEName;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.helpers.ReportHelper.ReportType;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.resource.EResourceConstant;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ItemRecord {

    private static Logger log = Logger.getLogger(ItemRecord.class);

    private File file;

    private ModelElement element;

    private Property property;

    private IRepositoryViewObject conflictObject;

    private Map<File, ModelElement> dependencyMap = new HashMap<File, ModelElement>();

    private List<String> errors = new ArrayList<String>();

    private ItemRecord parent;

    private ItemRecord[] childern;

    private EElementEName elementEName;

    private static ResourceSet resourceSet;

    private static List<ItemRecord> allItemRecords;

    public ItemRecord(File file) {
        this.file = file;

        if (resourceSet == null) {
            resourceSet = new ResourceSetImpl();
        }

        if (allItemRecords == null) {
            allItemRecords = new ArrayList<ItemRecord>();
        }

        try {
            initialize();
        } catch (Exception e) {
            String errorMessage = "Can't initialize element [" + getName() + "] : " + e.getMessage(); //$NON-NLS-1$  //$NON-NLS-2$
            addError(errorMessage);
            log.error(errorMessage);
        }

    }

    /**
     * DOC bZhou Comment method "initialize".
     */
    private void initialize() {
        if (file != null && file.isFile()) {
            if (!isJarFile()) {
                URI itemURI = URI.createFileURI(file.getAbsolutePath());
                URI propURI = itemURI.trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

                elementEName = EElementEName.findENameByExt(itemURI.fileExtension());

                if (property == null && !file.getName().endsWith(PluginConstant.JASPER_STRING)) {
                    Resource resource = resourceSet.getResource(propURI, true);
                    property = (Property) EcoreUtil.getObjectByType(resource.getContents(),
                            PropertiesPackage.eINSTANCE.getProperty());

                    if (property != null) {
                        element = PropertyHelper.getModelElement(property);
                    }
                }

                computeDependencies();
            }

            allItemRecords.add(this);
        }
    }

    /**
     * DOC bZhou Comment method "getElement".
     * 
     * @return
     */
    public ModelElement getElement() {
        return element;
    }

    /**
     * DOC bZhou Comment method "getFilePath".
     * 
     * @return
     */
    public IPath getFilePath() {
        return new Path(file.getAbsolutePath());
    }

    /**
     * DOC bZhou Comment method "getPropertyPath".
     * 
     * @return
     */
    public IPath getPropertyPath() {
        if (file != null) {
            IPath itemResPath = new Path(file.getAbsolutePath());
            return itemResPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
        }
        return null;
    }

    /**
     * DOC bZhou Comment method "getFullPath".
     * 
     * @return
     */
    public IPath getFullPath() {
        IPath path = new Path(file.getAbsolutePath());
        path = path.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation());
        return path;
    }

    /**
     * Getter for dependencyMap.
     * 
     * @return the dependencyMap
     */
    public Map<File, ModelElement> getDependencyMap() {
        return this.dependencyMap;
    }

    /**
     * DOC bZhou Comment method "computeDependencies".
     */
    private void computeDependencies() {

        if (isJRXml()) {
            Collection<TdReport> allReports = (Collection<TdReport>) RepResourceFileHelper.getInstance().getAllElement();
            for (TdReport report : allReports) {
                // MOD yyi 2012-02-20 TDQ-4545 TDQ-4701: Change to relative path comparing.
                IPath pathRepFile = RepResourceFileHelper.findCorrespondingFile(report).getLocation();
                IPath pathJrxmlFile = new Path(file.getPath());
                String path = pathJrxmlFile.makeRelativeTo(pathRepFile).toString();

                for (AnalysisMap anaMap : report.getAnalysisMap()) {
                    if (StringUtils.equals(path, anaMap.getJrxmlSource())) {
                        dependencyMap.put(file, report);
                    }
                }
            }
        } else if (element != null) {

            // MOD by zshen for bug 18724 2011.02.23
            TaggedValue tv = TaggedValueHelper.getTaggedValue(TaggedValueHelper.JAR_FILE_PATH, element.getTaggedValue());
            if (tv != null) {
                IPath libFolderPath = getFilePath().removeLastSegments(1).append("lib");
                File libFolder = libFolderPath.toFile();
                if (libFolder.exists()) {
                    for (File udiJarFile : UDIUtils.getLibJarFileList(libFolder)) {
                        for (String str : tv.getValue().split("\\|\\|")) {//$NON-NLS-1$
                            if (udiJarFile.getName().equals(str)) {
                                dependencyMap.put(udiJarFile, null);
                            }
                        }
                    }
                }
            }

            List<Property> dependencyProperty = DependenciesHandler.getInstance().getClintDependency(element);
            for (Property depElement : dependencyProperty) {
                ModelElement modelElement = PropertyHelper.getModelElement(depElement);
                URI uri = EObjectHelper.getURI(modelElement);

                if (uri != null) {
                    String uriString = WorkspaceUtils.toFile(uri);
                    File depFile = new File(uriString);
                    dependencyMap.put(depFile, modelElement);
                }
            }
            // MOD yyi 2012-02-20 TDQ-4545 TDQ-4701: Map user define jrxm templates with report.
            if (element instanceof TdReport) {
                TdReport rep = (TdReport) element;
                for (AnalysisMap anaMap : rep.getAnalysisMap()) {
                    ReportType reportType = ReportHelper.ReportType.getReportType(anaMap.getAnalysis(), anaMap.getReportType());
                    boolean isUseMode = ReportHelper.ReportType.USER_MADE.equals(reportType);
                    if (isUseMode) {
                        URI uri = rep.eResource().getURI();
                        IPath path = new Path(uri.toFileString());
                        // Append the relative path of jrxmltemplate to file.
                        dependencyMap.put(path.append(anaMap.getJrxmlSource()).toFile(), element);
                    }
                }
            }
        }
    }

    /**
     * clear the resource set.
     */
    public static void clear() {

        if (resourceSet != null) {
            for (Resource resource : resourceSet.getResources()) {
                resource.unload();
            }
            resourceSet.getResources().clear();
            resourceSet = null;
        }

        if (allItemRecords != null) {
            allItemRecords.clear();
            allItemRecords = null;
        }
    }

    /**
     * DOC bZhou Comment method "addError".
     * 
     * @param error
     */
    public void addError(String error) {
        if (elementEName != null) {
            error = "[" + elementEName.name() + "]" + error;//$NON-NLS-1$ //$NON-NLS-2$
        }
        this.errors.add(error);
    }

    /**
     * Getter for file.
     * 
     * @return the file
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Getter for errors.
     * 
     * @return the errors
     */
    public List<String> getErrors() {
        return this.errors;
    }

    /**
     * DOC bZhou Comment method "isValid".
     * 
     * @return
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Getter for property.
     * 
     * @return the property
     */
    public Property getProperty() {
        return this.property;
    }

    /**
     * Getter for resourceSet.
     * 
     * @return the resourceSet
     */
    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    /**
     * Getter for parent.
     * 
     * @return the parent
     */
    public ItemRecord getParent() {
        if (parent == null && file != null) {
            parent = new ItemRecord(file.getParentFile());
        }

        return this.parent;
    }

    /**
     * Getter for childern.
     * 
     * @return the childern
     */
    public ItemRecord[] getChildern() {
        if (childern == null) {
            List<ItemRecord> recordList = new ArrayList<ItemRecord>();

            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File aFile : listFiles) {
                    if (isValid(aFile)) {
                        ItemRecord itemRecord = new ItemRecord(aFile);
                        if (itemRecord.isValid()) {
                            recordList.add(itemRecord);
                        }
                    }
                }
            }

            childern = recordList.toArray(new ItemRecord[recordList.size()]);
        }

        return this.childern;
    }

    /**
     * DOC bZhou Comment method "getName".
     * 
     * @return
     */
    public String getName() {
        if (property != null) {
            return property.getLabel();
        } else if (element != null) {
            return element.getName();
        } else {
            return file.getName();
        }
    }

    /**
     * DOC bZhou Comment method "isValid".
     * 
     * @param file
     * @return
     */
    private boolean isValid(File file) {
        if (file.isDirectory()) {
            return isValidDirectory(file);
        }

        return isValidFile(file);
    }

    /**
     * DOC bZhou Comment method "isValidFile".
     * 
     * @param file
     * @return
     */
    private boolean isValidFile(File file) {
        IPath path = new Path(file.getAbsolutePath());
        IPath propPath = path.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        // MOD qiongli 2012-5-14 TDQ-5259.".Talend.properties" exists on 401,need to filter it and ".Talend.definition".
        String fileName = file.getName();
        if ("jasper".equals(path.getFileExtension()) //$NON-NLS-1$
                || (fileName != null && (fileName.equals(".Talend.definition") || fileName.equals(".Talend.properties")))) { //$NON-NLS-1$//$NON-NLS-2$
            return false;
        }

        return FactoriesUtil.JAR.equals(path.getFileExtension()) || propPath.toFile().exists() && !propPath.equals(path);
    }

    /**
     * DOC bZhou Comment method "isTOPFile".
     * 
     * @param file
     * @return
     */
    private boolean isValidDirectory(File file) {
        // filter the bin folder
        if (!file.getName().startsWith(".") && !file.getName().equals("bin")) {
            IPath filePath = new Path(file.getAbsolutePath());
            String pathStr = filePath.toPortableString();

            for (EResourceConstant constant : EResourceConstant.getTopConstants()) {
                if (filePath.toString().indexOf(constant.getPath()) > 0) {
                    String lastSeg = filePath.lastSegment();
                    if (constant == EResourceConstant.METADATA) {
                        return lastSeg.equals(constant.getName()) || pathStr.contains(EResourceConstant.DB_CONNECTIONS.getPath())
                                || pathStr.contains(EResourceConstant.MDM_CONNECTIONS.getPath())
                                || pathStr.contains(EResourceConstant.FILEDELIMITED.getPath());
                    }

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * DOC bZhou Comment method "isJRXml".
     * 
     * @return
     */
    private boolean isJRXml() {
        return file.getName().endsWith(FactoriesUtil.JRXML);
    }

    /**
     * DOC zshen Comment method "isSQL".
     * 
     * @return
     */
    private boolean isSQL() {
        return file.getName().endsWith(FactoriesUtil.SQL);
    }

    private boolean isJarFile() {
        return file.getName().endsWith(FactoriesUtil.JAR);
    }

    /**
     * DOC bZhou Comment method "findRecord".
     * 
     * @param file
     * @return
     */
    public static ItemRecord findRecord(File file) {
        for (ItemRecord record : allItemRecords) {
            if (file.getAbsolutePath().equals(record.getFile().getAbsolutePath())) {
                return record;
            }
        }

        return null;
    }

    /**
     * Getter for allItemRecords.
     * 
     * @return the allItemRecords
     */
    public static List<ItemRecord> getAllItemRecords() {
        return allItemRecords;
    }

    /**
     * Getter for conflictObject.
     * 
     * @return the conflictObject
     */
    public IRepositoryViewObject getConflictObject() {
        return this.conflictObject;
    }

    /**
     * Sets the conflictObject.
     * 
     * @param conflictObject the conflictObject to set
     */
    public void setConflictObject(IRepositoryViewObject conflictObject) {
        this.conflictObject = conflictObject;
    }

}
