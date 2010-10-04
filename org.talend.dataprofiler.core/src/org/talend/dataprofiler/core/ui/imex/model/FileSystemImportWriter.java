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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.management.api.SoftwareSystemManager;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.migration.IMigrationTask;
import org.talend.dataprofiler.core.migration.MigrationTaskManager;
import org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask.MigrationTaskType;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.repository.model.ProxyRepositoryFactory;
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

    private static final String VERSION_FILE_NAME = ".version.txt";

    private static final String DEFINITION_FILE_NAME = DefinitionHandler.FILENAME;

    private List<IMigrationTask> commTasks = new ArrayList<IMigrationTask>();

    private File tempFolder;

    private File versionFile;

    private File definitionFile;

    private IPath basePath;

    private String projectName;
    


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#populate(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], boolean)
     */
    public ItemRecord[] populate(ItemRecord[] elements, boolean checkExisted) {
        List<ItemRecord> inValidRecords = new ArrayList<ItemRecord>();

        for (ItemRecord record : elements) {

            record.getErrors().clear();

            checkDependency(record);

            if (checkExisted) {
                checkExisted(record);
            }

            if (!record.isValid()) {
                inValidRecords.add(record);
            }
        }

        return inValidRecords.toArray(new ItemRecord[inValidRecords.size()]);
    }

    /**
     * DOC bZhou Comment method "checkExisted".
     * 
     * @param record
     */
    private void checkExisted(ItemRecord record) {
        Property property = record.getProperty();
        if (property != null) {
            IPath itemPath = PropertyHelper.getItemPath(property);
            IFile itemFile = ResourcesPlugin.getWorkspace().getRoot().getFile(itemPath);

            try {
                itemFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
                //
            }

            String aString = record.getName();

            if (itemFile.exists()) {
                ModelElementFileFactory.getResourceFileMap(itemFile).clear();
                URI itemURI = URI.createPlatformResourceURI(itemFile.getFullPath().toString(), false);
                EMFSharedResources.getInstance().unloadResource(itemURI.toString());
                URI propURI = itemURI.trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
                EMFSharedResources.getInstance().unloadResource(propURI.toString());

                record.addError("\"" + aString + "\" is existed in workspace : " + itemFile.getFullPath().toString());
            }
        }
    }

    /**
     * DOC bZhou Comment method "checkDependency".
     * 
     * @param record
     */
    private void checkDependency(ItemRecord record) {
        for (ModelElement melement : record.getDependencyMap().values()) {
            if (melement.eIsProxy()) {
                InternalEObject inObject = (InternalEObject) melement;
                record.addError("\"" + record.getName() + "\" missing dependented file : " + inObject.eProxyURI().toFileString());
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
            IPath itemPath = PropertyHelper.getItemPath(record.getProperty());

            IPath itemDesPath = ResourcesPlugin.getWorkspace().getRoot().getFile(itemPath).getLocation();
            IPath propDesPath = itemDesPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

            toImportMap.put(record.getFilePath(), itemDesPath);
            toImportMap.put(record.getPropertyPath(), propDesPath);
        }

        return toImportMap;
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

        if (desFile.exists()) {
            log.warn(desFile.getAbsoluteFile() + " is overwritten!");
        }

        FilesUtils.copyFile(resFile, desFile);

        try {
            update(desFile);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
    }

    /**
     * DOC bZhou Comment method "update".
     * 
     * @param desFile
     * @throws IOException
     * @throws PersistenceException
     */
    private void update(File desFile) throws IOException, PersistenceException {

        String curProjectLabel = ResourceManager.getRootProjectName();
        if (!StringUtils.equals(projectName, curProjectLabel)) {
            String content = FileUtils.readFileToString(desFile, "utf-8");
            content = StringUtils.replace(content, "/" + projectName + "/", "/" + curProjectLabel + "/");
            FileUtils.writeStringToFile(desFile, content, "utf-8");
        }

        if (desFile.getName().endsWith(FactoriesUtil.PROPERTIES_EXTENSION)) {
            Property property = PropertyHelper.getProperty(desFile);

            User user = ReponsitoryContextBridge.getUser();
            if (user != null) {
                property.setAuthor(user);
            }

            if (log.isDebugEnabled()) {
                log.debug("property file for " + desFile + " = " + property.getLabel());
            }
            EMFSharedResources.getInstance().saveResource(property.eResource());
        }

        if (desFile.getName().endsWith(FactoriesUtil.ITEM_EXTENSION)) {
            File propFile = new Path(desFile.getAbsolutePath()).removeFileExtension().addFileExtension(
                    FactoriesUtil.PROPERTIES_EXTENSION).toFile();
            Property property = PropertyHelper.getProperty(propFile);

            if (property != null && property.getItem() instanceof ConnectionItem) {
                Connection connection = ((ConnectionItem) property.getItem()).getConnection();

                // update software system
                TdSoftwareSystem softwareSystem = SoftwareSystemManager.getInstance().getSoftwareSystem(connection);
                if (softwareSystem != null) {
                    ConnectionHelper.setSoftwareSystem(connection, softwareSystem);
                }
                // save
                ProxyRepositoryFactory.getInstance().save(property.getItem(), true);

                IRepositoryViewObject object = new RepositoryViewObject(property);
                ProxyRepositoryViewObject.registerReposViewObj(connection, object);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#write(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], org.eclipse.core.runtime.IProgressMonitor)
     */
    public void write(ItemRecord[] records, IProgressMonitor monitor) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        try {

            for (ItemRecord record : records) {

                if (monitor.isCanceled()) {
                    break;
                }

                Map<IPath, IPath> toImportMap = mapping(record);

                monitor.subTask("Importing " + record.getName());

                if (record.isValid()) {
                    log.info("Importing " + record.getFile().getAbsolutePath());

                    for (IPath resPath : toImportMap.keySet()) {
                        IPath desPath = toImportMap.get(resPath);
                        write(resPath, desPath);
                    }

                } else {
                    for (String error : record.getErrors()) {
                        log.error(error);
                    }
                }

                monitor.worked(1);
            }

            finish(records, monitor);

        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImportWriter#finish(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], org.eclipse.core.runtime.IProgressMonitor)
     */
    public void finish(ItemRecord[] records, IProgressMonitor monitor) throws IOException, CoreException {
        ItemRecord.clear();

        IFile defFile = ResourceManager.getLibrariesFolder().getFile(DEFINITION_FILE_NAME);

        if (definitionFile != null && definitionFile.exists()) {
            File defintionFile = defFile.getLocation().toFile();
            FilesUtils.copyFile(definitionFile, defintionFile);
        }

        ResourceService.refreshStructure();

        if (!commTasks.isEmpty() && monitor != null) {
            MigrationTaskManager.doMigrationTask(commTasks, monitor);
        }

        if (tempFolder != null && tempFolder.exists()) {
            if (log.isDebugEnabled()) {
                log.debug("Deleting temporary workspace..." + tempFolder.getAbsolutePath());
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
            List<IMigrationTask> taskList = MigrationTaskManager.findWorkspaceTaskByType(MigrationTaskType.FILE, version);

            if (!taskList.isEmpty()) {

                for (IMigrationTask task : taskList) {
                    if (isModelTask(task)) {
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
            IPath projPath = tempBasePath.append("talend.project");
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
                log.debug("Back-up workspace...." + workspacePath.toOSString());
            }
                File temporaryFolder = workspacePath.removeLastSegments(1).append("tempFolder" + EcoreUtil.generateUUID()).toFile();
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

    private boolean isModelTask(IMigrationTask task) {
        return "org.talend.dataprofiler.core.migration.impl.MergeMetadataTask".equals(task.getId())
                || "org.talend.dataprofiler.core.migration.impl.ExchangeFileNameToReferenceTask".equals(task.getId())
                || "org.talend.dataprofiler.core.migration.impl.UpdatePropertiesFileTask".equals(task.getId())
                || "org.talend.dataprofiler.core.migration.impl.UpdateAnalysisWithMinLengthIndicator".equals(task.getId())
                || "org.talend.dataprofiler.core.migration.impl.UpdateFileAfterMergeConnectionTask".equals(task.getId());
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
        List<String> errors = new ArrayList<String>(); // TODO externalize these strings!!
        if (!checkBasePath()) {
            errors.add("The root directory is invalid!");
        } else if (!checkVersion()) {
            errors.add("Can't verify the imported version!");
        } else if (!checkProject()) {
            errors.add("Invalid Project! Can't load the project setting.");
        } else if (!checkTempPath()) {
            errors.add("Cannot create temporary folder.");
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
