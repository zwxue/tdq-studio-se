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
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.writer.AElementPersistance;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.utils.files.FileUtils;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * update TDQ_Data Profiling and TDQ_Libraries folders after use same api with TOS(metadata folder has been updated
 * before).
 */
public class UpdateAfterMergeTosApiTask extends AbstractWorksapceUpdateTask {

    private static final String FILE_NAME_FLAG = "_0.1."; //$NON-NLS-1$

    private static Logger log = Logger.getLogger(UpdateAfterMergeTosApiTask.class);

    private List<File> newFileList;

    private Map<String, String> replaceMap;

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2011, 1, 20);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {

        newFileList = new ArrayList<File>();

        replaceMap = new HashMap<String, String>();

        ArrayList<File> fileList = getNeedUpdateFiles();

        for (File file : fileList) {

            createNewItemFile(file);

            deleteOldItemFile(file);
        }

        updateConnectionFile();

        updateFile();

        return true;
    }

    /**
     * DOC bZhou Comment method "addConnectionFileToUpdate".
     */
    private void updateConnectionFile() {
        File rawDir = getWorkspacePath().append(EResourceConstant.METADATA.getPath()).toFile();
        ArrayList<File> fileList = new ArrayList<File>();

        if (rawDir.exists()) {
            getAllFilesFromFolder(rawDir, fileList, new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(FactoriesUtil.ITEM_EXTENSION);
                }
            });
        }

        for (File file : fileList) {
            IFile iFile = WorkspaceUtils.fileToIFile(file);

            Property property = PropertyHelper.getProperty(iFile);
            if (property != null) {
                Item item = property.getItem();
                EResourceConstant type = EResourceConstant.getTypedConstant(item);
                if (type == EResourceConstant.DB_CONNECTIONS) {
                    DatabaseConnectionItem dbItem = (DatabaseConnectionItem) item;
                    DatabaseConnection connection = (DatabaseConnection) dbItem.getConnection();

                    String otherParameter = ConnectionHelper.getOtherParameter(connection);
                    if (!StringUtils.isBlank(otherParameter)) {
                        connection.setUiSchema(otherParameter);
                        EMFSharedResources.getInstance().saveResource(connection.eResource());
                    }
                }
            }
        }

        newFileList.addAll(fileList);
    }

    /**
     * DOC bZhou Comment method "updateFile".
     */
    private void updateFile() throws Exception {
        for (File file : newFileList) {
            updateFileByNewFileName(file);

            updateIndicatorItem(file);

            try {
                reloadFile(file);
            } catch (Exception e) {
                log.warn(e, e);
                // continue if any exception.
            }
        }

        ResourceService.refreshStructure();
    }

    /**
     * DOC bZhou Comment method "reloadFile".
     * 
     * @param file
     */
    private void reloadFile(File file) {
        IFile ifile = WorkspaceUtils.fileToIFile(file);
        URI uri = URI.createPlatformResourceURI(ifile.getFullPath().toString(), false);
        EMFSharedResources.getInstance().reloadResource(uri);
    }

    /**
     * DOC bZhou Comment method "updateFileByNewFileName".
     * 
     * @param file
     * @throws IOException
     * @throws URISyntaxException
     */
    private void updateFileByNewFileName(File file) throws IOException, URISyntaxException {
        for (String fragment : replaceMap.keySet()) {
            FileUtils.replaceInFile(file.getAbsolutePath(), fragment, replaceMap.get(fragment));
        }
    }

    /**
     * DOC bZhou Comment method "getNeedUpdateFiles".
     * 
     * @return
     */
    private ArrayList<File> getNeedUpdateFiles() {
        File dapRawFile = getWorkspacePath().append(EResourceConstant.DATA_PROFILING.getPath()).toFile();
        File libRawFile = getWorkspacePath().append(EResourceConstant.LIBRARIES.getPath()).toFile();

        ArrayList<File> fileList = findRawFiles(dapRawFile);
        fileList.addAll(findRawFiles(libRawFile));
        return fileList;
    }

    /**
     * DOC bZhou Comment method "updateIndicatorItem".
     * 
     * @param file
     * @throws URISyntaxException
     * @throws IOException
     */
    private void updateIndicatorItem(File file) throws IOException, URISyntaxException {
        if (file.getName().endsWith(FactoriesUtil.ANA)) {
            Analysis analysis = (Analysis) getModelElement(file);
            Collection<Indicator> indicators = IndicatorHelper.getIndicators(analysis.getResults());
            for (Indicator indicator : indicators) {
                String fragment = null;
                IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
                if (indicatorDefinition != null) {
                    if (indicatorDefinition.eIsProxy()) {
                        URI uri = ((InternalEObject) indicatorDefinition).eProxyURI();
                        fragment = uri.lastSegment();
                    } else {
                        fragment = indicatorDefinition.eResource().getURI().lastSegment();
                    }

                    if (fragment != null && !fragment.contains(FILE_NAME_FLAG)) {
                        String replace = fragment.replace(" ", "_").replace(".", FILE_NAME_FLAG); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        FileUtils.replaceInFile(file.getAbsolutePath(), fragment, replace);
                    }
                }
            }
        }
    }

    /**
     * DOC bZhou Comment method "createNewItemFile".
     * 
     * @param file
     */
    private void createNewItemFile(File file) {
        ModelElement modelElement = getModelElement(file);

        if (modelElement != null) {
            boolean needUpdateFlag = !modelElement.eIsProxy();

            if (needUpdateFlag) {
                IFile iFile = WorkspaceUtils.fileToIFile(file);
                IFolder parentFolder = (IFolder) iFile.getParent();

                AElementPersistance writer = ElementWriterFactory.getInstance().create(modelElement);
                writer.create(modelElement, parentFolder, true);
            }

            URI uri = EObjectHelper.getURI(modelElement);
            if (uri != null) {
                replaceMap.put(file.getName(), uri.lastSegment());

                if (needUpdateFlag && uri.isPlatform()) {
                    IPath filePath = new Path(uri.toPlatformString(false));
                    IFile needUpdateFile = ResourceManager.getRoot().getFile(filePath);
                    newFileList.add(needUpdateFile.getLocation().toFile());
                }
            }

        }

    }

    /**
     * DOC bZhou Comment method "deleteOldFile".
     * 
     * @param file
     * @throws CoreException
     */
    private void deleteOldItemFile(File file) throws CoreException {
        IFile itemFile = WorkspaceUtils.fileToIFile(file);
        IFile propFile = PropertyHelper.getPropertyFile(itemFile);

        Property property = PropertyHelper.getProperty(propFile);

        Item currentItem = property.getItem();
        if (currentItem.getParent() instanceof FolderItem) {
            ((FolderItem) currentItem.getParent()).getChildren().remove(currentItem);
        }

        itemFile.delete(true, null);
        propFile.delete(true, null);
    }

    /**
     * get the ModelElement from the file.
     * 
     * @param parentFile
     * @return
     */
    private ModelElement getModelElement(File parentFile) {
        ModelElement result = null;
        try {
            IFile iFile = WorkspaceUtils.fileToIFile(parentFile);
            result = ModelElementFileFactory.getModelElement(iFile);
        } catch (Exception e) {
            log.error(e, e);
        }
        return result;
    }

    private ArrayList<File> findRawFiles(File rawDir) {
        ArrayList<File> fileList = new ArrayList<File>();

        if (rawDir.exists()) {
            getAllFilesFromFolder(rawDir, fileList, new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    if (isNotSVNFolder(dir)) {
                        File file = new Path(dir.getAbsolutePath()).append(name).toFile();
                        if (file.isFile()) {
                            ModelElement element = getModelElement(file);
                            if (element != null) {
                                String newName = WorkspaceUtils.normalize(element.getName());
                                return !name.contains(newName) || !name.contains("0.1"); //$NON-NLS-1$
                            }
                        }
                    }
                    return false;
                }
            });
        }

        return fileList;
    }

    /**
     * DOC bZhou Comment method "isNotSVNFolder".
     * 
     * @param file
     * @return
     */
    private boolean isNotSVNFolder(File file) {
        return !file.getAbsolutePath().contains("svn"); //$NON-NLS-1$
    }
}
