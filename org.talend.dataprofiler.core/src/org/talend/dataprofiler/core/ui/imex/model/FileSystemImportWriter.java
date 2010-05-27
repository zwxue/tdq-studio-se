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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.migration.IMigrationTask;
import org.talend.dataprofiler.core.migration.MigrationTaskManager;
import org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask.MigrationTaskType;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.resource.ResourceManager;
import org.talend.utils.ProductVersion;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FileSystemImportWriter implements IImexWriter {

    private static Logger log = Logger.getLogger(FileSystemImportWriter.class);

    private File versionFile;

    private String basePath;

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

            checkDependency(record);

            if (checkExisted) {
                checkExisted(record);
            }

            if (!record.isValid()) {
                inValidRecords.add(record);
            }
        }

        if (elements.length != 0) {
            ItemRecord anyRecord = elements[0];

            retrieveProjectName(anyRecord);
        }

        versionFile = new Path(basePath).append("/TDQ_Libraries/.version.txt").toFile();

        return inValidRecords.toArray(new ItemRecord[inValidRecords.size()]);
    }

    private void retrieveProjectName(ItemRecord anyRecord) {
        Property property = anyRecord.getProperty();
        if (projectName == null && property != null) {

            InternalEObject author = (InternalEObject) property.getAuthor();
            if (author != null && !author.eIsProxy()) {
                Resource projResource = author.eResource();
                if (projResource != null) {
                    IPath projectPath = new Path(projResource.getURI().toFileString());
                    Object projOBJ = EObjectHelper.retrieveEObject(projectPath, PropertiesPackage.eINSTANCE.getProject());
                    if (projOBJ != null) {
                        Project project = (Project) projOBJ;
                        projectName = project.getTechnicalLabel();
                    }
                }
            } else {
                projectName = ReponsitoryContextBridge.PROJECT_DEFAULT_NAME;
            }
        }
    }

    /**
     * DOC bZhou Comment method "checkExisted".
     * 
     * @param record
     */
    private void checkExisted(ItemRecord record) {
        Property property = record.getProperty();
        if (property != null) {
            IPath itemPath = PropertyHelper.getElementPath(property);
            IFile itemFile = ResourcesPlugin.getWorkspace().getRoot().getFile(itemPath);
            ModelElement element = record.getElement();
            if (element != null && itemFile.exists()) {
                record.addError("\"" + element.getName() + "\" is existed in workspace : " + itemFile.getFullPath().toString());
            }
        }
    }

    /**
     * DOC bZhou Comment method "checkDependency".
     * 
     * @param record
     */
    private void checkDependency(ItemRecord record) {
        List<ModelElement> dependencyElements = new ArrayList<ModelElement>();

        ModelElement element = record.getElement();

        if (element != null) {
            ModelElementHelper.iterateClientDependencies(element, dependencyElements);
            for (ModelElement melement : dependencyElements) {
                if (melement.eIsProxy()) {
                    InternalEObject inObject = (InternalEObject) melement;
                    record.addError("\"" + element.getName() + "\" missing dependented file : "
                            + inObject.eProxyURI().toFileString());
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#write(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * )
     */
    public void write(ItemRecord record) throws IOException, CoreException {
        IPath itemPath = PropertyHelper.getElementPath(record.getProperty());

        IPath itemDesPath = ResourcesPlugin.getWorkspace().getRoot().getFile(itemPath).getLocation();
        IPath propDesPath = itemDesPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        Map<IPath, IPath> resMap = new HashMap<IPath, IPath>();
        resMap.put(record.getFilePath(), itemDesPath);
        resMap.put(record.getPropertyPath(), propDesPath);

        for (IPath resPath : resMap.keySet()) {
            File resFile = resPath.toFile();
            File desFile = resMap.get(resPath).toFile();

            if (desFile.exists()) {
                log.warn(desFile.getAbsoluteFile() + " is overwrittern!");
            }

            FilesUtils.copyFile(resFile, desFile);

            String curProjectLabel = ResourceManager.getRootProjectName();
            if (!StringUtils.equals(projectName, curProjectLabel)) {
                String content = FileUtils.readFileToString(desFile, "utf-8");
                content = StringUtils.replace(content, "/" + projectName + "/", "/" + curProjectLabel + "/");
                FileUtils.writeStringToFile(desFile, content, "utf-8");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#finish(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [])
     */
    public void finish(ItemRecord[] records) throws IOException {
        for (ItemRecord record : records) {
            record.clear();
        }

        IFile defintionFile = ResourceManager.getLibrariesFolder().getFile(".Talend.definition");
        if (!defintionFile.exists()) {
            DefinitionHandler.getInstance();
        }

        Display.getDefault().asyncExec(new Runnable() {

            public void run() {

                if (versionFile != null) {
                    ProductVersion version = WorkspaceVersionHelper.getVesion(versionFile);
                    List<IMigrationTask> migrationTasks = MigrationTaskManager.findWorkspaceTaskByType(MigrationTaskType.FILE,
                            version);
                    MigrationTaskManager.doMigrationTask(migrationTasks);
                }

                CorePlugin.getDefault().refreshWorkSpace();
                CorePlugin.getDefault().refreshDQView();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#setBasePath(java.lang.String)
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#getBasePath()
     */
    public String getBasePath() {
        return this.basePath;
    }
}
