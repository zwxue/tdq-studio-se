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
package org.talend.dataprofiler.core.ui.imex.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.migration.helper.IndicatorDefinitionFileHelper;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dataprofiler.migration.IMigrationTask;
import org.talend.dataprofiler.migration.IWorkspaceMigrationTask.MigrationTaskType;
import org.talend.dataprofiler.migration.manager.MigrationTaskManager;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.RepositoryWorkUnit;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.utils.ProductVersion;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FileSystemImportWriter implements IImportWriter {

    private static Logger log = Logger.getLogger(FileSystemImportWriter.class);

    private static final String VERSION_FILE_NAME = ".version.txt";//$NON-NLS-1$ 

    private static final String DEFINITION_FILE_NAME = DefinitionHandler.FILENAME;

    private List<IMigrationTask> commTasks = new ArrayList<IMigrationTask>();

    private File tempFolder;

    private File versionFile;

    private File definitionFile;

    private IPath basePath;

    private String projectName;

    /*
     * check the dependency and conflict; when the record is a indicator(system or user): if overwrite should not add
     * error in record(only check conflict, but not check dependency)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#populate(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], boolean)
     */
    public ItemRecord[] populate(ItemRecord[] elements, boolean checkExisted) {
        List<ItemRecord> inValidRecords = new ArrayList<ItemRecord>();

        for (ItemRecord record : elements) {

            record.getErrors().clear();

            // modify: if it is a indicator and used in analysis, do not add errors
            checkConflict(record, isIndicator(record.getElement()) || isPattern(record.getElement()));

            // Added 20120809 yyin TDQ-4189, when it is indicator, can be overwrite
            if (!checkExisted && (isIndicator(record.getElement()) || isPattern(record.getElement()))) {
                continue;
            }// ~

            checkDependency(record);

            if (checkExisted && record.getConflictObject() != null) {
                record.addError(DefaultMessagesImpl.getString("FileSystemImproWriter.hasConflictObject", record.getName()));//$NON-NLS-1$ 
            }

            if (!record.isValid()) {
                inValidRecords.add(record);
            }
        }

        return inValidRecords.toArray(new ItemRecord[inValidRecords.size()]);
    }

    /**
     * judge if the record is a indicator or not.
     * 
     * @param element
     * @return
     */
    private boolean isIndicator(ModelElement element) {
        return element instanceof IndicatorDefinition && !(element instanceof DQRule);
    }

    /**
     * judge if the record is a indicator or not.
     * 
     * @param element
     * @return
     */
    private boolean isPattern(ModelElement element) {
        return element instanceof Pattern;
    }

    /**
     * MOdified 20120810 yyin TDQ-4189 when the record is a system indicator and be used by some analysis, only remember
     * the conflict object to merge, but do not add any errors which will cause it can't be imported
     * 
     * @param record
     * @param checkExisted
     */
    private void checkConflict(ItemRecord record, boolean isIndicator) {
        Property property = record.getProperty();
        if (property != null) {
            try {

                ERepositoryObjectType itemType = ERepositoryObjectType.getItemType(property.getItem());
                List<IRepositoryViewObject> allObjects = ProxyRepositoryFactory.getInstance().getAll(itemType, true);
                for (IRepositoryViewObject object : allObjects) {
                    if (isConflict(property, object.getProperty())) {
                        if (!isIndicator) {
                            List<IRepositoryViewObject> supplierDependency = DependenciesHandler.getInstance()
                                    .getSupplierDependency(object);
                            for (IRepositoryViewObject supplierViewObject : supplierDependency) {
                                record.addError(DefaultMessagesImpl
                                        .getString(
                                                "FileSystemImproWriter.DependencyWarning", new Object[] { record.getName(), supplierViewObject.getProperty().getLabel(), object.getLabel() }));//$NON-NLS-1$
                            }
                        }
                        // If set this parameter will delete the object when finished the wizard.
                        record.setConflictObject(object);
                        return;
                    }
                }
                record.setConflictObject(null);
            } catch (Exception e) {
                record.addError("\"" + record.getName() + "\" check item conflict failed!");//$NON-NLS-1$ //$NON-NLS-2$ 
            }
        }
    }

    private boolean isConflict(Property p1, Property p2) {
        if (p1.getLabel().equals(p2.getLabel())) {
            return true;
        } else if (p1.getId().equals(p2.getId())) {
            return true;
        }
        return false;
    }

    /**
     * DOC bZhou Comment method "checkDependency".
     * 
     * @param record
     */
    private void checkDependency(ItemRecord record) {
        for (ModelElement melement : record.getDependencyMap().values()) {
            if (melement != null && melement.eIsProxy()) {
                InternalEObject inObject = (InternalEObject) melement;
                record.addError("\"" + record.getName() + "\" missing dependented file : " + inObject.eProxyURI().toFileString());//$NON-NLS-1$ //$NON-NLS-2$ 
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#mapping(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * )
     */
    public Map<IPath, IPath> mapping(ItemRecord record) {

        Map<IPath, IPath> toImportMap = new HashMap<IPath, IPath>();

        if (record.isValid()) {
            Property property = record.getProperty();
            // MOD by zshen for bug 18724 2011.02.23
            IPath itemPath = null;
            IPath itemDesPath = null;
            if (property != null) {
                itemPath = PropertyHelper.getItemPath(property);
            } else {
                String currentProjectName = ResourceManager.getRootProjectName();

                IPath fullPath = record.getFullPath();

                int constantIndex = EResourceConstant.getTopConstantIndexFromPath(fullPath);

                itemPath = new Path(currentProjectName).append(fullPath.removeFirstSegments(constantIndex));
            }
            itemDesPath = ResourcesPlugin.getWorkspace().getRoot().getFile(itemPath).getLocation();
            IPath propDesPath = itemDesPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

            toImportMap.put(record.getFilePath(), itemDesPath);
            if (property == null) {
                return toImportMap;
            }
            toImportMap.put(record.getPropertyPath(), propDesPath);

            EResourceConstant typedConstant = EResourceConstant.getTypedConstant(property.getItem());
            if (typedConstant != null && typedConstant == EResourceConstant.MDM_CONNECTIONS) {
                ConnectionItem item = (ConnectionItem) property.getItem();
                Connection connection = item.getConnection();
                List<TdXmlSchema> tdXmlDocumentList = ConnectionHelper.getTdXmlDocument(connection);
                for (TdXmlSchema schema : tdXmlDocumentList) {
                    IPath srcPath = getXsdFolderPath(record, schema);
                    if (!srcPath.toFile().exists()) {
                        log.error("The file : " + srcPath.toFile() + " can't be found.This will make MDMConnection useless ");//$NON-NLS-1$ //$NON-NLS-2$ 
                        break;
                    }
                    IPath desPath = ResourceManager.getMDMConnectionFolder().getLocation()
                            .append(new Path(schema.getXsdFilePath()));
                    toImportMap.put(srcPath, desPath);
                }
            }
        }

        return toImportMap;
    }

    private IPath getXsdFolderPath(ItemRecord record, TdXmlSchema schema) {
        IPath mdmPath = record.getFilePath().removeLastSegments(1);
        IPath srcPath = mdmPath.append(schema.getXsdFilePath());
        while (!srcPath.toFile().exists()) {
            if (mdmPath.isRoot()) {
                break;
            }
            mdmPath = mdmPath.removeLastSegments(1);
            srcPath = mdmPath.append(schema.getXsdFilePath());
        }
        return srcPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#write(org.eclipse.core.runtime.IPath,
     * org.eclipse.core.runtime.IPath)
     */
    public void write(IPath resPath, IPath desPath) throws IOException, CoreException {
        File resFile = resPath.toFile();
        File desFile = desPath.toFile();

        boolean isCovered = desFile.exists();
        if (isCovered) {
            log.warn(desFile.getAbsoluteFile() + " is overwritten!");//$NON-NLS-1$ 
        }

        FileUtils.copyFile(resFile, desFile);

        update(desFile, isCovered);

    }

    /**
     * DOC bZhou Comment method "update".
     * 
     * @param desFile
     * @param isCovered
     * 
     * @throws IOException
     * @throws CoreException
     * 
     * @throws Exception
     */
    private void update(File desFile, boolean isCovered) throws IOException, CoreException {

        String curProjectLabel = ResourceManager.getRootProjectName();

        if (desFile.exists()) {

            IFile desIFile = ResourceService.file2IFile(desFile);

            String fileExt = desIFile.getFileExtension();
            if (FactoriesUtil.isEmfFile(fileExt)) {

                if (!StringUtils.equals(projectName, curProjectLabel)) {
                    String content = FileUtils.readFileToString(desFile, "utf-8");//$NON-NLS-1$
                    content = StringUtils.replace(content, "/" + projectName + "/", "/" + curProjectLabel + "/");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
                    FileUtils.writeStringToFile(desFile, content, "utf-8");//$NON-NLS-1$
                }

                if (isCovered) {
                    URI uri = URI.createPlatformResourceURI(desIFile.getFullPath().toString(), false);
                    EMFSharedResources.getInstance().reloadResource(uri);
                }
            }

            if (fileExt.equals(FactoriesUtil.PROPERTIES_EXTENSION)) {
                Property property = PropertyHelper.getProperty(desIFile);

                if (property != null) {
                    User user = ReponsitoryContextBridge.getUser();
                    if (user != null && property.getAuthor() == null) {
                        property.setAuthor(user);
                        EMFSharedResources.getInstance().saveResource(property.eResource());
                    }

                    if (log.isDebugEnabled()) {
                        log.debug("property file for " + desIFile + " = " + property.getLabel());//$NON-NLS-1$ //$NON-NLS-2$
                    }
                } else {
                    log.error("Loading property error: " + desIFile.getFullPath().toString());//$NON-NLS-1$
                }
            }
        }

    }

    /*
     * After check the conflicts of the imported object, calling this method (from ImportWizard) replace the conflicts
     * object in the records if the record is valid; then call the finish to do migrate. OR: merge the conflict system
     * indicators if valid.(overwrite)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#write(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], org.eclipse.core.runtime.IProgressMonitor)
     */
    public void write(ItemRecord[] records, IProgressMonitor monitor) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        final ItemRecord[] fRecords = records;
        final IProgressMonitor fMonitor = monitor;

        RepositoryWorkUnit<Object> workUnit = new RepositoryWorkUnit<Object>("Import TDQ Element") {//$NON-NLS-1$

            @Override
            protected void run() {
                try {

                    for (ItemRecord record : fRecords) {

                        if (fMonitor.isCanceled()) {
                            break;
                        }

                        Map<IPath, IPath> toImportMap = mapping(record);

                        fMonitor.subTask("Importing " + record.getName());//$NON-NLS-1$

                        if (record.isValid()) {
                            log.info("Importing " + record.getFile().getAbsolutePath());//$NON-NLS-1$

                            // Delete the conflict node before import.
                            IRepositoryViewObject object = record.getConflictObject();
                            boolean isDelete = true;
                            if (object != null) {
                                // added 20120808 yyin TDQ-4189
                                // when record is valid&conflict, means it need to be merged with the current one if it
                                // is a system indicator, (using its UUid to find this SI not label)
                                if (isIndicator(record.getElement())) {
                                    IndicatorDefinition siDef = ((TDQIndicatorDefinitionItem) object.getProperty().getItem())
                                            .getIndicatorDefinition();

                                    mergeSystemIndicator(record, siDef);
                                    isDelete = false;
                                } else if (isPattern(record.getElement())) {
                                    mergePattern(record, (TDQPatternItem) object.getProperty().getItem());
                                    isDelete = false;
                                } else {// ~
                                    ProxyRepositoryFactory.getInstance().deleteObjectPhysical(object);
                                }
                            }

                            if (isDelete) {
                                for (IPath resPath : toImportMap.keySet()) {
                                    IPath desPath = toImportMap.get(resPath);
                                    ResourceSet resourceSet = ProxyRepositoryFactory.getInstance()
                                            .getRepositoryFactoryFromProvider().getResourceManager().resourceSet;
                                    synchronized (resourceSet) {
                                        write(resPath, desPath);
                                    }
                                }
                            }

                        } else {
                            for (String error : record.getErrors()) {
                                log.error(error);
                            }
                        }

                        fMonitor.worked(1);
                    }

                    finish(fRecords, fMonitor);

                } catch (Exception e) {
                    log.error(e, e);
                }
            }

        };

        workUnit.setAvoidUnloadResources(Boolean.TRUE);
        ProxyRepositoryFactory.getInstance().executeRepositoryWorkUnit(workUnit);
    }

    /**
     * Added: (20120808 yyin, TDQ-4189) The system indicators are not read-only because the user may want to write his
     * own SQL template. so this task deals with the modified SI from imported one, and merge them with the current
     * studio. 1)only when the user select the"Overwrite existing items" on the import wizard(and the modifydate is
     * newer than the current studio's SI), the conflict modification in imported SI will overwrite the ones in current
     * studio, otherwise, the SI in current studio will keep. 2)If a language does not exist in the system indicator but
     * exists in the user modified indicator, then we add it 3)if a language exists in the system indicator but has been
     * removed in the user modified indicator, then we keep the system indicator definition
     * 
     * @param record imported modified system indicator
     * @param siDef the system indicator in the current studio
     */
    protected void mergeSystemIndicator(ItemRecord record, IndicatorDefinition siDef) {
        // only when the Si is modified, do the save
        boolean isModified = false;
        // get expression list from record
        EList<TdExpression> importedExs = ((IndicatorDefinition) record.getElement()).getSqlGenericExpression();
        // for each expression:
        for (TdExpression importedEx : importedExs) {
            // if the modify date ==null, means it is not modified, do nothing
            if (importedEx.getModificationDate() == null) {
                continue;
            }

            // find the related template in system indicator(with same language)
            TdExpression systemExpression = null;
            for (TdExpression ex : siDef.getSqlGenericExpression()) {
                if (ex.getLanguage().equals(importedEx.getLanguage())) {
                    systemExpression = ex;
                    break;
                }
            }

            // if new, add to SI
            if (systemExpression == null) {
                IndicatorDefinitionFileHelper.addSqlExpression(siDef, importedEx.getLanguage(), importedEx.getBody(),
                        importedEx.getModificationDate());
                isModified = true;
            } else {// if the expression are different: compare the modify date, make the SI keep the new one
                if (replaceExpression(systemExpression, importedEx)) {
                    IndicatorDefinitionFileHelper.removeSqlExpression(siDef, importedEx.getLanguage());
                    IndicatorDefinitionFileHelper.addSqlExpression(siDef, importedEx.getLanguage(), importedEx.getBody(),
                            importedEx.getModificationDate());
                    isModified = true;
                }
            }
        }
        // replace the name (using the imported name incase of modify the name), and save the SI
        // siDef.setName(record.getElement().getName());
        if (isModified) {
            IndicatorDefinitionFileHelper.save(siDef);
        }
    }

    /**
     * when imported pattern is from lower version, even if it is modified, the "modify date" is still null, so, even if
     * the modify date is null ,still do the comparation.
     * 
     * @param record
     * @param patternItem
     */
    protected void mergePattern(ItemRecord record, TDQPatternItem patternItem) {
        Pattern pattern = patternItem.getPattern();
        // only when the Si is modified, do the save
        boolean isModified = false;
        // get expression list from record
        EList<PatternComponent> importComponents = ((Pattern) record.getElement()).getComponents();
        // for each expression:
        for (PatternComponent component : importComponents) {
            // if the modify date ==null, maybe it is from lower version, still do the compare
            TdExpression importEx = ((RegularExpression) component).getExpression();
            PatternComponent replaced = null;
            for (PatternComponent pComp : pattern.getComponents()) {
                TdExpression pex = ((RegularExpression) pComp).getExpression();
                if (importEx.getLanguage().equals(pex.getLanguage())) {
                    replaced = pComp;
                    break;
                }
            }

            // if new, add to SI
            if (replaced == null) {
                pattern.getComponents().add(createPatternComponent(component));
                isModified = true;
            } else {// if the expression are different: compare the modify date, make the SI keep the new one
                if (replaceExpression(((RegularExpression) replaced).getExpression(), importEx)) {
                    pattern.getComponents().remove(replaced);
                    pattern.getComponents().add(createPatternComponent(component));
                    isModified = true;
                }
            }
        }
        // replace the name (using the imported name incase of modify the name), and save the SI
        // siDef.setName(record.getElement().getName());
        if (isModified) {
            ElementWriterFactory.getInstance().createPatternWriter().save(patternItem, true);
        }
    }

    private PatternComponent createPatternComponent(PatternComponent oldComponent) {
        RegularExpression newComponent = PatternFactory.eINSTANCE.createRegularExpression();
        newComponent.setExpression(((RegularExpression) oldComponent).getExpression());
        newComponent.setExpressionType(((RegularExpression) oldComponent).getExpressionType());

        return newComponent;
    }

    private boolean replaceExpression(TdExpression currentEx, TdExpression importedEx) {
        // when both modify date=null, compare the body
        if (importedEx.getModificationDate() == null) {
            if (currentEx.getModificationDate() == null) {
                if (!importedEx.getBody().equalsIgnoreCase(currentEx.getBody())) {
                    return true;
                }
            } else {
                // when current workspace has modified, the imported one not, keep the workspace
                return false;
            }
        } else {// import has modify date
            if (currentEx.getModificationDate() == null) {
                return true;
            } else {
                if (importedEx.getModificationDate().compareToIgnoreCase(currentEx.getModificationDate()) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * when clicking the finish button on the import wizard, execute this method.
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImportWriter#finish(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], org.eclipse.core.runtime.IProgressMonitor)
     */
    public void finish(ItemRecord[] records, IProgressMonitor monitor) throws IOException, CoreException {
        ItemRecord.clear();

        handleDefinitionFile();

        doMigration(monitor);

        deleteTempProjectFolder();

    }

    private void handleDefinitionFile() throws IOException {
        IFile defFile = ResourceManager.getLibrariesFolder().getFile(DEFINITION_FILE_NAME);

        if (definitionFile != null && definitionFile.exists()) {
            File defintionFile = defFile.getLocation().toFile();
            FilesUtils.copyFile(definitionFile, defintionFile);

            URI uri = URI.createPlatformResourceURI(defFile.getFullPath().toString(), false);
            EMFSharedResources.getInstance().unloadResource(uri.toString());
        }
    }

    private void doMigration(IProgressMonitor monitor) {
        ResourceService.refreshStructure();

        if (!commTasks.isEmpty() && monitor != null) {
            MigrationTaskManager.doMigrationTask(commTasks, monitor);
        }
    }

    private void deleteTempProjectFolder() throws IOException {
        if (tempFolder != null && tempFolder.exists()) {
            if (log.isDebugEnabled()) {
                log.debug("Deleting temporary workspace..." + tempFolder.getAbsolutePath());//$NON-NLS-1$
            }
            FileUtils.deleteDirectory(tempFolder);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#migration(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void migration(IProgressMonitor monitor) {

        List<IMigrationTask> modelTasks = new ArrayList<IMigrationTask>();

        if (versionFile != null && versionFile.exists()) {
            ProductVersion version = WorkspaceVersionHelper.getVesion(new Path(versionFile.getAbsolutePath()));
            MigrationTaskManager manager = new MigrationTaskManager(version, MigrationTaskType.FILE);
            List<IMigrationTask> taskList = manager.getValidTasks();

            if (!taskList.isEmpty()) {

                for (IMigrationTask task : taskList) {
                    if (task.isModelTask()) {
                        ((AbstractWorksapceUpdateTask) task).setWorkspacePath(basePath);
                        modelTasks.add(task);
                    } else {
                        commTasks.add(task);
                    }
                }
            }

        }

        if (!modelTasks.isEmpty()) {
            MigrationTaskManager.doMigrationTask(modelTasks, monitor);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#computeInput(org.eclipse.core.runtime.IPath)
     */
    public ItemRecord computeInput(IPath path) {

        if (path != null) {
            versionFile = path.append(EResourceConstant.LIBRARIES.getPath()).append(VERSION_FILE_NAME).toFile();
            definitionFile = path.append(EResourceConstant.LIBRARIES.getPath()).append(DEFINITION_FILE_NAME).toFile();

            if (!versionFile.exists()) {
                return null;
            }

            tempFolder = backUPWorksapce(path);
            if (tempFolder == null) {
                // the error is logged in #checkTempPath()
                return null;
            }

            IPath tempBasePath = new Path(tempFolder.getAbsolutePath());
            IPath projPath = tempBasePath.append(FileConstants.LOCAL_PROJECT_FILENAME);
            setBasePath(tempBasePath);
            if (projPath.toFile().exists()) {
                Object projOBJ = EObjectHelper.retrieveEObject(projPath, PropertiesPackage.eINSTANCE.getProject());
                if (projOBJ != null) {
                    Project project = (Project) projOBJ;
                    projectName = project.getTechnicalLabel();
                }
            } else {
                projectName = ReponsitoryContextBridge.getProjectName();
            }
            return new ItemRecord(tempFolder);
        }
        return null;
    }

    protected File backUPWorksapce(IPath workspacePath) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Back-up workspace...." + workspacePath.toOSString());//$NON-NLS-1$
            }

            File temporaryFolder = ResourceManager.getRootFolderLocation().append("tempFolder" + EcoreUtil.generateUUID())//$NON-NLS-1$
                    .toFile();
            if (!temporaryFolder.exists()) {
                temporaryFolder.mkdir();
            }

            FileUtils.copyDirectory(workspacePath.toFile(), temporaryFolder);
            return temporaryFolder;
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#setBasePath(org.eclipse.core.runtime.IPath)
     */
    public void setBasePath(IPath path) {
        this.basePath = path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#getBasePath()
     */
    public IPath getBasePath() {
        return this.basePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#check()
     */
    public List<String> check() {
        List<String> errors = new ArrayList<String>();
        if (!checkBasePath()) {
            errors.add("The root directory is invalid!");//$NON-NLS-1$
        } else if (!checkVersion()) {
            errors.add("Can't verify the imported version!");//$NON-NLS-1$
        } else if (!checkProject()) {
            errors.add("Invalid Project! Can't load the project setting.");//$NON-NLS-1$
        } else if (!checkTempPath()) {
            errors.add("Cannot create temporary folder.");//$NON-NLS-1$
        }

        return errors;
    }

    /**
     * DOC bZhou Comment method "checkProject".
     * 
     * @return
     */
    private boolean checkProject() {
        return projectName != null;
    }

    /**
     * DOC bZhou Comment method "checkVersion".
     * 
     * @return
     */
    private boolean checkVersion() {
        return versionFile.exists();
    }

    /**
     * DOC bZhou Comment method "checkBasePath".
     * 
     * @return
     */
    private boolean checkBasePath() {
        return basePath != null && basePath.toFile().exists()
                && basePath.append(EResourceConstant.LIBRARIES.getPath()).toFile().exists();
    }

    private boolean checkTempPath() {
        return tempFolder != null && tempFolder.exists();
    }

}
