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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.utils.files.FileUtils;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * update TDQ_Data Profiling and TDQ_Libraries folders after use same api with TOS(metadata folder has been updated
 * before).
 */
public class UpdateAfterMergeTosApiTask extends AbstractWorksapceUpdateTask {

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

            updateIndicatorItem(file);

            createNewItem(file);

            deleteOldItem(file);
        }

        updateFileByNewFileName();

        reloadModelElementCache();

        return true;
    }

    /**
     * DOC bZhou Comment method "updateFileByNewFileName".
     * 
     * @throws IOException
     * @throws URISyntaxException
     */
    private void updateFileByNewFileName() throws IOException, URISyntaxException {
        for (File file : newFileList) {
            for (String fragment : replaceMap.keySet()) {
                FileUtils.replaceInFile(file.getAbsolutePath(), fragment, replaceMap.get(fragment));
            }
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
     * clear the cache of ModelElement.
     * 
     */
    private void reloadModelElementCache() {
        ResourceService.refreshStructure();

        // 1) clear and unload element
        AnaResourceFileHelper.getInstance().clear();
        RepResourceFileHelper.getInstance().clear();
        IndicatorResourceFileHelper.getInstance().clear();
        PatternResourceFileHelper.getInstance().clear();
        DQRuleResourceFileHelper.getInstance().clear();
        PrvResourceFileHelper.getInstance().clear();
        // 2) reload from file
        ResourceFileMap.getAll();
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

                    if (fragment != null) {
                        String replace = fragment.replace(" ", "_").replace(".", "_0.1.");//$NON-NLS-2$
                        FileUtils.replaceInFile(file.getAbsolutePath(), fragment, replace);
                    }
                }
            }
        }
    }

    /**
     * DOC bZhou Comment method "updateDependencyFile".
     * 
     * @param file
     * @throws IOException
     * @throws URISyntaxException
     */
    private void updateDependencyItem(File file) throws IOException, URISyntaxException {
        ModelElement modelElement = getModelElement(file);
        if (modelElement != null) {
            List<ModelElement> dependencyElements = new ArrayList<ModelElement>();

            ModelElementHelper.iterateSupplyDependencies(modelElement, dependencyElements);

            for (ModelElement depElement : dependencyElements) {
                if (!depElement.eIsProxy()) {
                    IFile depFile = ResourceManager.getRoot().getFile(
                            new Path(depElement.eResource().getURI().toPlatformString(false)));
                    File depSysFile = depFile.getLocation().toFile();

                    String oldFileName = new Path(file.getAbsolutePath()).removeFileExtension().lastSegment();
                    String newFileName = WorkspaceUtils.normalize(modelElement.getName()) + "_0.1";
                    FileUtils.replaceInFile(depSysFile.getAbsolutePath(), oldFileName, newFileName);
                }
            }
        }
    }

    /**
     * DOC bZhou Comment method "createNewItem".
     * 
     * @param file
     */
    private void createNewItem(File file) {
        ModelElement modelElement = getModelElement(file);

        if (modelElement != null && !modelElement.eIsProxy()) {
            IFile iFile = ResourceService.file2IFile(file);
            IFolder parentFolder = (IFolder) iFile.getParent();

            TypedReturnCode<Object> trc = ElementWriterFactory.getInstance().create(modelElement)
                    .create(modelElement, parentFolder, true);
            if (trc.getObject() instanceof Item) {
                Property property = ((Item) trc.getObject()).getProperty();

                IPath newFileNamePath = new Path(property.getLabel() + "_" + property.getVersion()).addFileExtension(iFile
                        .getFileExtension());
                IFile newFile = parentFolder.getFile(newFileNamePath);
                newFileList.add(newFile.getLocation().toFile());
                replaceMap.put(file.getName(), newFile.getFullPath().lastSegment());
            }
        } else if (modelElement.eIsProxy()) {
            InternalEObject eobject = (InternalEObject) modelElement;
            replaceMap.put(file.getName(), eobject.eProxyURI().lastSegment());
        }

        // add all connection files to instead all old dependency link.
        newFileList.addAll(findRawConnectionFiles());
    }

    /**
     * DOC bZhou Comment method "deleteOldFile".
     * 
     * @param file
     */
    private void deleteOldItem(File file) {
        file.delete();
        File propFile = new Path(file.getAbsolutePath()).removeFileExtension()
                .addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION).toFile();
        if (propFile.exists()) {
            propFile.delete();
        }
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
            if (parentFile.getName().endsWith(FactoriesUtil.DEFINITION)) {
                result = IndicatorResourceFileHelper.getInstance().findIndDefinition(WorkspaceUtils.fileToIFile(parentFile));
            } else if (parentFile.getName().endsWith(FactoriesUtil.PATTERN)) {
                result = PatternResourceFileHelper.getInstance().findPattern(WorkspaceUtils.fileToIFile(parentFile));
            } else if (parentFile.getName().endsWith(FactoriesUtil.DQRULE)) {
                result = DQRuleResourceFileHelper.getInstance().findWhereRule(WorkspaceUtils.fileToIFile(parentFile));
            } else if (parentFile.getName().endsWith(FactoriesUtil.ANA)) {
                result = AnaResourceFileHelper.getInstance().findAnalysis(WorkspaceUtils.fileToIFile(parentFile));
            } else if (parentFile.getName().endsWith(FactoriesUtil.REP)) {
                result = RepResourceFileHelper.getInstance().findReport(WorkspaceUtils.fileToIFile(parentFile));
            }
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
                                return !name.contains(newName) || !name.contains("0.1");
                            }
                        }
                    }
                    return false;
                }
            });
        }

        return fileList;
    }

    private ArrayList<File> findRawConnectionFiles() {
        File rawDir = getWorkspacePath().append(EResourceConstant.METADATA.getPath()).toFile();
        ArrayList<File> fileList = new ArrayList<File>();

        if (rawDir.exists()) {
            getAllFilesFromFolder(rawDir, fileList, new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(FactoriesUtil.ITEM_EXTENSION);
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
        return !file.getAbsolutePath().contains("svn");
    }
}
