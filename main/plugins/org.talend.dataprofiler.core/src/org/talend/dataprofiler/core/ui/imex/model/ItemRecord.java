// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import org.eclipse.core.resources.IFolder;
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
import org.talend.cwm.management.i18n.InternationalizationUtil;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.helpers.ReportHelper.ReportType;
import org.talend.dataquality.indicators.definition.DefinitionPackage;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.userdefine.UDIndicatorDefinition;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
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
            String errorMessage = DefaultMessagesImpl.getString("ItemRecord.cantInitializeElement", getName(), e.getMessage()); //$NON-NLS-1$
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

            List<Property> dependencyProperty = DependenciesHandler.getInstance().getClintDependency(element);
            for (Property depElement : dependencyProperty) {
                ModelElement modelElement = PropertyHelper.getModelElement(depElement);
                URI uri = EObjectHelper.getURI(modelElement);

                if (uri != null) {
                    String uriString = WorkspaceUtils.toFile(uri);
                    File depFile = new File(uriString);
                    dependencyMap.put(depFile, modelElement);
                }

                // MOD sizhaoliu 2013-04-13 TDQ-7082
                if (modelElement instanceof IndicatorDefinition) {
                    if (modelElement instanceof UDIndicatorDefinition) {
                        includeJUDIDependencies((IndicatorDefinition) modelElement);
                    } else {
                        for (IndicatorDefinition definition : ((IndicatorDefinition) modelElement).getAggregatedDefinitions()) {
                            includeAggregatedDependencies(definition);
                        }
                    }
                }
            }
            // MOD yyi 2012-02-20 TDQ-4545 TDQ-4701: Map user define jrxm templates with report.
            if (element instanceof TdReport) {
                TdReport rep = (TdReport) element;
                for (AnalysisMap anaMap : rep.getAnalysisMap()) {
                    ReportType reportType = ReportHelper.ReportType.getReportType(anaMap.getAnalysis(), anaMap.getReportType());
                    boolean isUserMade = ReportHelper.ReportType.USER_MADE.equals(reportType);
                    if (isUserMade) {
                        traverseFolderAndAddJrxmlDependencies(getJrxmlFolderFromReport(rep, ResourceManager.getJRXMLFolder()));
                    }
                }
            } else if (element instanceof IndicatorDefinition) { // MOD sizhaoliu 2013-04-13 TDQ-7082
                IndicatorDefinition definition = (IndicatorDefinition) element;
                if (definition instanceof UDIndicatorDefinition) {
                    includeJUDIDependencies(definition);
                } else {
                    for (IndicatorDefinition defInd : definition.getAggregatedDefinitions()) {
                        includeAggregatedDependencies(defInd);
                    }
                }
            }
        }
    }

    /**
     * get the jrxml folder according to the Report file(if the Report file is out of current workspace, the Jrxml
     * Folder should also out of it).
     * 
     * @param rep the Report file
     * @param folder the Jrxml Folder in the current project
     * @return
     */
    private File getJrxmlFolderFromReport(TdReport rep, IFolder folder) {
        File jrxmlFolderFile = null;
        String repFileString = new File(rep.eResource().getURI().toFileString()).getAbsolutePath();
        String projectString = folder.getProject().getLocation().toFile().getAbsolutePath();
        if (repFileString.startsWith(projectString)) {
            jrxmlFolderFile = folder.getLocation().toFile();
        } else {
            String jrxmlFolderString = folder.getLocation().toFile().getAbsolutePath();
            jrxmlFolderFile = new File(StringUtils.replace(jrxmlFolderString, projectString,
                    repFileString.substring(0, repFileString.indexOf(EResourceConstant.DATA_PROFILING.getPath()) - 1), 1));
        }
        return jrxmlFolderFile;
    }

    private void includeJUDIDependencies(IndicatorDefinition definition) {
        TaggedValue tv = TaggedValueHelper.getTaggedValue(TaggedValueHelper.JAR_FILE_PATH, definition.getTaggedValue());
        if (tv != null) {
            IPath definitionPath = Path.fromOSString(definition.eResource().getURI().toFileString());
            IPath libFolderPath = definitionPath.removeLastSegments(1).append("lib"); //$NON-NLS-1$
            File libFolder = libFolderPath.toFile();
            if (libFolder.exists()) {
                List<File> libJarFileList = UDIUtils.getLibJarFileList(libFolder);
                String[] splitTagValues = tv.getValue().split("\\|\\|");//$NON-NLS-1$
                for (File udiJarFile : libJarFileList) {
                    for (String str : splitTagValues) {
                        if (udiJarFile.getName().equals(str)) {
                            dependencyMap.put(udiJarFile, null);
                        }
                    }
                }
            }
        }
    }

    private void traverseFolderAndAddJrxmlDependencies(File folderFile) {
        for (File subFile : folderFile.listFiles()) {
            if (subFile.isDirectory()) {
                traverseFolderAndAddJrxmlDependencies(subFile);
            } else if (subFile.isFile()) {
                String name = subFile.getName();
                int dotIndex = name.lastIndexOf("."); //$NON-NLS-1$
                if (dotIndex > 0) {
                    String ext = name.substring(dotIndex + 1);
                    if (FactoriesUtil.JRXML.equals(ext)) {
                        dependencyMap.put(subFile, null);
                    }
                }
            }
        }
    }

    private void includeAggregatedDependencies(IndicatorDefinition definition) {
        URI uri = EObjectHelper.getURI(definition);
        if (uri != null) {
            String uriString = WorkspaceUtils.toFile(uri);
            File depFile = new File(uriString);
            dependencyMap.put(depFile, definition);
        }

        for (IndicatorDefinition aggregatedDefinition : definition.getAggregatedDefinitions()) {
            includeAggregatedDependencies(aggregatedDefinition);
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
            return getDisplayName();
        } else if (element != null) {
            return element.getName();
        } else {
            return file.getName();
        }
    }

    /**
     * only internationalization name of SystemIndicator
     * 
     * @return
     */
    private String getDisplayName() {
        // only internationalization SystemIndicator
        if (element != null && DefinitionPackage.eINSTANCE.getIndicatorDefinition().equals(element.eClass())) {
            return InternationalizationUtil.getDefinitionInternationalizationLabel(property.getLabel());
        }
        return property.getDisplayName();
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

        String fileName = file.getName();
        // MOD qiongli 2012-5-14 TDQ-5259.".Talend.properties" exists on 401,need to filter it and ".Talend.definition".
        if ("jasper".equals(path.getFileExtension()) //$NON-NLS-1$
                || (fileName != null && (fileName.equals(".Talend.definition") || fileName.equals(".Talend.properties")))) {//$NON-NLS-1$ //$NON-NLS-2$
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
        if (!file.getName().startsWith(".") && !file.getName().equals("bin")) { //$NON-NLS-1$ //$NON-NLS-2$
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
