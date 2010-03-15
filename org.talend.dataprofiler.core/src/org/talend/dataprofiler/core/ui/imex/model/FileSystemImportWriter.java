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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dq.helper.PropertyHelper;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FileSystemImportWriter implements IImexWriter {

    private static Logger log = Logger.getLogger(FileSystemImportWriter.class);

    private ItemRecord resource;

    protected Map<IPath, IPath> resMap = new HashMap<IPath, IPath>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#initPath(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * , java.lang.String)
     */
    public void initPath(ItemRecord resource, String destination) {
        this.resource = resource;

        if (resource != null) {
            IPath itemResPath = new Path(resource.getFile().getAbsolutePath());
            IPath propResPath = new Path(resource.getPropertyFilePath());

            Object propOBJ = retrieveEObject(propResPath, PropertiesPackage.eINSTANCE.getProperty());
            if (propOBJ != null) {
                Property property = (Property) propOBJ;
                InternalEObject author = (InternalEObject) property.getAuthor();
                if (author != null) {
                    Resource projResource = author.eResource();
                    if (projResource != null) {
                        URI projectUri = projResource.getURI();
                        IPath projectPath = new Path(projectUri.toFileString());
                        if (projectPath.toFile().exists()) {
                            Object projOBJ = retrieveEObject(projectPath, PropertiesPackage.eINSTANCE.getProject());
                            if (projOBJ != null) {
                                Project project = (Project) projOBJ;
                                resource.setProjectName(project.getLabel());
                            }
                        }
                    }
                }

                IPath itemDesPath = PropertyHelper.getElementPath(property);

                itemDesPath = ResourcesPlugin.getWorkspace().getRoot().getFile(itemDesPath).getLocation();

                IPath propDesPath = itemDesPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

                resMap.put(itemResPath, itemDesPath);
                resMap.put(propResPath, propDesPath);
            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#write()
     */
    public void write() throws IOException, CoreException {
        for (IPath resPath : resMap.keySet()) {
            File resFile = resPath.toFile();
            File desFile = resMap.get(resPath).toFile();

            if (!desFile.exists()) {
                FilesUtils.copyFile(resFile, desFile);

                String oldProjectLabel = resource.getProjectName() == null ? "TOP_DEFAULT_PRJ" : resource.getProjectName();
                String curProjectLabel = ResourceManager.getRootProjectName();
                if (!StringUtils.equals(oldProjectLabel, curProjectLabel)) {
                    String content = FileUtils.readFileToString(desFile);
                    content = StringUtils.replace(content, "/" + oldProjectLabel + "/", "/" + curProjectLabel + "/");
                    FileUtils.writeStringToFile(desFile, content, "utf-8");
                }
            } else {
                log.warn(desFile.getAbsoluteFile() + " is already existed !");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#finish()
     */
    public void finish() throws IOException {
        CorePlugin.getDefault().refreshWorkSpace();
    }

    /**
     * DOC bZhou Comment method "retrieveEObject".
     * 
     * @param filePath
     * @param classfier
     * @return
     */
    private Object retrieveEObject(IPath filePath, EClass classfier) {
        URI uri = URI.createFileURI(filePath.toOSString());
        Resource res = new ResourceSetImpl().getResource(uri, true);
        return EcoreUtil.getObjectByType(res.getContents(), classfier);
    }
}
