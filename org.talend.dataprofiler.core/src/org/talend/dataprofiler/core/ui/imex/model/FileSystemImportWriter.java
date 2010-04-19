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
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FileSystemImportWriter implements IImexWriter {

    private static Logger log = Logger.getLogger(FileSystemImportWriter.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#populate(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], boolean)
     */
    public ItemRecord[] populate(ItemRecord[] elements, boolean checkExisted) {
        List<ItemRecord> inValidRecords = new ArrayList<ItemRecord>();

        if (elements instanceof ItemRecord[]) {
            ItemRecord[] recoreds = (ItemRecord[]) elements;

            for (ItemRecord record : recoreds) {

                checkDependency(record);

                if (checkExisted) {
                    checkExisted(record);
                }

                if (!record.isValid()) {
                    inValidRecords.add(record);
                }
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
        IPath itemPath = PropertyHelper.getElementPath(record.getProperty());
        IFile itemFile = ResourcesPlugin.getWorkspace().getRoot().getFile(itemPath);
        ModelElement element = record.getElement();
        if (element != null && itemFile.exists()) {
            record.addError("\"" + element.getName() + "\" is existed in workspace : " + itemFile.getFullPath().toString());
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
     * , java.lang.String)
     */
    public void write(ItemRecord record, String destination) throws IOException, CoreException {
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

            String oldProjectLabel = record.getProjectName();
            String curProjectLabel = ResourceManager.getRootProjectName();
            if (!StringUtils.equals(oldProjectLabel, curProjectLabel)) {
                String content = FileUtils.readFileToString(desFile);
                content = StringUtils.replace(content, "/" + oldProjectLabel + "/", "/" + curProjectLabel + "/");
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
                CorePlugin.getDefault().refreshWorkSpace();
                CorePlugin.getDefault().refreshDQView();
            }
        });
    }
}
