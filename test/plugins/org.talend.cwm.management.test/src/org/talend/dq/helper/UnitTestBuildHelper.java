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
package org.talend.dq.helper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.ConnectionPackage;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.InformationLevel;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.string.StringUtilities;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.record.RecordFactory;
import orgomg.cwm.resource.record.RecordFile;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UnitTestBuildHelper {

    private static Logger log = Logger.getLogger(UnitTestBuildHelper.class);

    private static UnitTestBuildHelper INSTANCE;

    private boolean init = false;

    private IProject iProject;

    private Project tProject;

    private File projectFile;

    private RepositoryNode dataProfilingNode;

    private RepositoryNode librariesNode;

    private RepositoryNode metadataNode;

    public File getProjectFile() {
        return this.projectFile;
    }

    public void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
    }

    public boolean isInit() {
        return this.init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public IProject getiProject() {
        return this.iProject;
    }

    public void setiProject(IProject iProject) {
        this.iProject = iProject;
    }

    public Project gettProject() {
        return this.tProject;
    }

    public void settProject(Project tProject) {
        this.tProject = tProject;
    }

    public RepositoryNode getDataProfilingNode() {
        return this.dataProfilingNode;
    }

    public void setDataProfilingNode(RepositoryNode dataProfilingNode) {
        this.dataProfilingNode = dataProfilingNode;
    }

    public RepositoryNode getLibrariesNode() {
        return this.librariesNode;
    }

    public void setLibrariesNode(RepositoryNode librariesNode) {
        this.librariesNode = librariesNode;
    }

    public RepositoryNode getMetadataNode() {
        return this.metadataNode;
    }

    public void setMetadataNode(RepositoryNode metadataNode) {
        this.metadataNode = metadataNode;
    }

    private UnitTestBuildHelper() {
    }

    public static UnitTestBuildHelper getDefault() {
        if (INSTANCE == null) {
            INSTANCE = new UnitTestBuildHelper();
        }
        return INSTANCE;
    }

    /**
     * create project with a specified name.
     * 
     * @param projectName specified project name
     * @return
     */
    public static IProject createRealProject(String projectName) {
        // IProject rootProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        // if (!rootProject.exists()) {
        // initProxyRepository(rootProject);
        // }
        //
        // return rootProject;
        return null;
    }

    /**
     * create the subfolder under the project and named for folderName.
     * 
     * @param project
     * @param folderName
     * @return
     */
    public static IFolder createRealFolder(IProject project, String folderName) {
        IFolder iFolder = project.getFolder(folderName);
        if (!iFolder.exists()) {
            try {
                iFolder.create(true, true, null);
                File file = WorkspaceUtils.ifolderToFile(iFolder);
                if (!file.exists()) {
                    file.mkdirs();
                }
            } catch (CoreException e) {
                ExceptionHandler.process(e);
                log.error(e, e);
            }
        }
        return iFolder;
    }

    /**
     * create the subfolder under the parent folder and named for folderName.
     * 
     * @param parentFolder
     * @param folderName
     * @return
     */
    public static IFolder createRealFolder(IFolder parentFolder, String folderName) {
        IFolder iFolder = parentFolder.getFolder(folderName);
        if (!iFolder.exists()) {
            try {
                iFolder.create(true, true, null);
                File file = WorkspaceUtils.ifolderToFile(iFolder);
                if (!file.exists()) {
                    file.mkdirs();
                }
            } catch (CoreException e) {
                ExceptionHandler.process(e);
                log.error(e, e);
            }
        }
        return iFolder;
    }

    /**
     * create the file under the parentFolder and named for fileName.
     * 
     * @param parentFolder
     * @param fileName
     * @return
     */
    public static IFile createRealFile(IFolder parentFolder, String fileName) {
        IFile iFile = parentFolder.getFile(fileName);
        if (!iFile.exists()) {
            try {
                iFile.create(null, true, null);
                File file = WorkspaceUtils.ifileToFile(iFile);
                if (!file.exists()) {
                    File parentFile = file.getParentFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    file.createNewFile();
                }
            } catch (CoreException e) {
                ExceptionHandler.process(e);
                log.error(e, e);
            } catch (IOException e) {
                ExceptionHandler.process(e);
                log.error(e, e);
            }
        }
        return iFile;
    }

    /**
     * copy the method from ProxyRepositoryFactory to avoid tos migeration.
     * 
     * @param fileName
     * @param pattern
     */
    private static void checkFileName(String fileName, String pattern) {
        if (!java.util.regex.Pattern.matches(pattern, fileName)) {
            throw new IllegalArgumentException("the file name [" + fileName + "] is invalid of the pattern [" + pattern + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * create the real RepositoryNode for DataProfiling.
     * 
     * @param iProject
     * @return
     */
    public static RepositoryNode createRealDataProfilingNode(IProject iProject) {
        RepositoryNode node = null;

        if (iProject != null) {
            UnitTestBuildHelper.createRealFolder(iProject, ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder());

            Project tProject = ProjectManager.getInstance().getCurrentProject();
            if (tProject != null && tProject.getEmfProject() != null && tProject.getAuthor() != null) {
                IRepositoryViewObject viewObject = buildRepositoryViewObjectSystemFolder(tProject.getEmfProject(),
                        tProject.getAuthor(), ERepositoryObjectType.TDQ_DATA_PROFILING);

                node = new RepositoryNode(viewObject, null, ENodeType.SYSTEM_FOLDER);
                viewObject.setRepositoryNode(node);
            }
        }

        return node;
    }

    /**
     * create the real RepositoryNode for Libraries.
     * 
     * @param iProject
     * @return
     */
    public static RepositoryNode createRealLibrariesNode(IProject iProject) {
        RepositoryNode node = null;

        if (iProject != null) {
            UnitTestBuildHelper.createRealFolder(iProject, ERepositoryObjectType.TDQ_LIBRARIES.getFolder());

            Project tProject = ProjectManager.getInstance().getCurrentProject();
            if (tProject != null && tProject.getEmfProject() != null) {
                IRepositoryViewObject viewObject = buildRepositoryViewObjectSystemFolder(tProject.getEmfProject(),
                        tProject.getAuthor(), ERepositoryObjectType.TDQ_LIBRARIES);

                node = new RepositoryNode(viewObject, null, ENodeType.SYSTEM_FOLDER);
                viewObject.setRepositoryNode(node);
            }
        }

        return node;
    }

    /**
     * create the real RepositoryNode for Metadata.
     * 
     * @param iProject
     * @return
     */
    public static RepositoryNode createRealMetadataNode(IProject iProject) {
        RepositoryNode node = null;

        if (iProject != null) {
            UnitTestBuildHelper.createRealFolder(iProject, ERepositoryObjectType.METADATA.getFolder());

            Project tProject = ProjectManager.getInstance().getCurrentProject();
            if (tProject != null && tProject.getEmfProject() != null && tProject.getAuthor() != null) {
                IRepositoryViewObject viewObject = buildRepositoryViewObjectSystemFolder(tProject.getEmfProject(),
                        tProject.getAuthor(), ERepositoryObjectType.METADATA);

                node = new RepositoryNode(viewObject, null, ENodeType.SYSTEM_FOLDER);
                viewObject.setRepositoryNode(node);
            }
        }

        return node;
    }

    /**
     * create a RepositoryViewObject which is a system folder.
     * 
     * @param tProject
     * @param author
     * @param type
     * @return
     */
    private static IRepositoryViewObject buildRepositoryViewObjectSystemFolder(
            org.talend.core.model.properties.Project emfProject, User author, ERepositoryObjectType type) {
        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        FolderItem folderItem = PropertiesFactory.eINSTANCE.createFolderItem();
        Property property = PropertiesFactory.eINSTANCE.createProperty();

        itemState.setItemRelated(folderItem);

        folderItem.setParent(emfProject);
        folderItem.setType(FolderType.STABLE_SYSTEM_FOLDER_LITERAL);
        folderItem.setProperty(property);

        property.setAuthor(author);
        property.setCreationDate(new Date());
        property.setId(StringUtilities.getRandomString(26));
        property.setItem(folderItem);
        property.setLabel(type.getFolder());
        property.setMaxInformationLevel(InformationLevel.DEBUG_LITERAL);
        property.setOldStatusCode(""); //$NON-NLS-1$
        property.setVersion("0.1"); //$NON-NLS-1$

        return new Folder(property, type);
    }

    /**
     * delete the project which has been login else will effect the result of junit.
     */
    public static void deleteCurrentProject() {
        // IProject rootProject = ReponsitoryContextBridge.getRootProject();
        // if (rootProject.exists()) {
        // try {
        // rootProject.delete(true, true, null);
        // } catch (CoreException e) {
        // log.error(e, e);
        // Assert.fail(e.getMessage());
        // }
        // }
    }

    /**
     * create a real file connection witl file url
     * 
     * @param fileUrl
     * @param delimitedFileconnection
     * @return
     */
    public MetadataTable initFileConnection(URL fileUrl, DelimitedFileConnection delimitedFileconnection) {
        try {
            delimitedFileconnection.setFilePath(FileLocator.toFileURL(fileUrl).toURI().getPath().toString());
            delimitedFileconnection.setRowSeparatorValue("\n"); //$NON-NLS-1$
            delimitedFileconnection.setEncoding("UTF-8"); //$NON-NLS-1$
            delimitedFileconnection.setFieldSeparatorValue(","); //$NON-NLS-1$
            delimitedFileconnection.setName(ERepositoryObjectType.METADATA_FILE_DELIMITED.getKey());

            MetadataTable metadataTable = ConnectionFactory.eINSTANCE.createMetadataTable();
            IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
            metadataTable.setId(factory.getNextId());
            RecordFile record = (RecordFile) ConnectionHelper.getPackage(delimitedFileconnection.getName(),
                    delimitedFileconnection, RecordFile.class);
            if (record != null) { // hywang
                PackageHelper.addMetadataTable(metadataTable, record);
            } else {
                RecordFile newrecord = RecordFactory.eINSTANCE.createRecordFile();
                newrecord.setName(delimitedFileconnection.getName());
                ConnectionHelper.addPackage(newrecord, delimitedFileconnection);
                PackageHelper.addMetadataTable(metadataTable, newrecord);
            }
            return metadataTable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * init the file's related metadata table with columns: name, company, city,country, comment. and add all columns as
     * analyzed elements.
     * 
     * @param context
     */
    public MetadataColumn initColumns(AnalysisContext context, MetadataTable metadataTable) {
        List<ModelElement> anaElements = context.getAnalysedElements();

        // // Name
        MetadataColumn name = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        name.setName("name"); //$NON-NLS-1$
        name.setLabel("name"); //$NON-NLS-1$
        anaElements.add(name);
        metadataTable.getColumns().add(name);

        // Company
        MetadataColumn company = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        company.setName("company"); //$NON-NLS-1$
        company.setLabel("company"); //$NON-NLS-1$
        anaElements.add(company);
        metadataTable.getColumns().add(company);
        // City
        MetadataColumn city = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        city.setName("city"); //$NON-NLS-1$
        city.setLabel("city"); //$NON-NLS-1$
        anaElements.add(city);
        metadataTable.getColumns().add(city);
        // Country
        MetadataColumn country = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        country.setName("country"); //$NON-NLS-1$
        country.setLabel("country"); //$NON-NLS-1$
        anaElements.add(country);
        metadataTable.getColumns().add(country);

        // comment
        MetadataColumn comment = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        comment.setName("comment"); //$NON-NLS-1$
        comment.setLabel("comment"); //$NON-NLS-1$
        anaElements.add(comment);
        metadataTable.getColumns().add(comment);

        return name;
    }
}
