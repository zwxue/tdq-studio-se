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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.jfree.util.Log;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.dq.helper.EObjectHelper;
import org.talend.resource.EResourceConstant;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ItemRecord {

    private File file;

    private ModelElement element;

    private Property property;

    private Map<File, ModelElement> dependencyMap;

    private List<String> errors = new ArrayList<String>();

    private ItemRecord parent;

    private ItemRecord[] childern;

    private static ResourceSet resourceSet;

    private static List<ItemRecord> allItemRecords;

    public ItemRecord(File file) {
        this.file = file;

        try {
            if (file.isFile()) {
                init();
            }
        } catch (Exception e) {
            Log.error(e, e);
        }
    }

    /**
     * DOC bZhou Comment method "init".
     */
    private void init() {
        if (resourceSet == null) {
            resourceSet = new ResourceSetImpl();
        }

        if (allItemRecords == null) {
            allItemRecords = new ArrayList<ItemRecord>();
        }

        allItemRecords.add(this);

        if (element == null && file != null) {
            URI fileURI = URI.createFileURI(file.getAbsolutePath());
            Resource resource = resourceSet.getResource(fileURI, true);
            EList<EObject> contents = resource.getContents();
            if (contents != null && !contents.isEmpty()) {
                EObject object = contents.get(0);
                if (object instanceof ModelElement) {
                    element = (ModelElement) object;
                }
            }
        }

        if (property == null && file != null) {
            property = (Property) EObjectHelper.retrieveEObject(getPropertyPath(), PropertiesPackage.eINSTANCE.getProperty());
        }

        computeDependencies();
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
        if (file.isFile()) {
            IPath path = new Path(file.getAbsolutePath());
            path = path.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation());
            return path;
        }
        return null;
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
        if (dependencyMap == null) {
            dependencyMap = new HashMap<File, ModelElement>();

            List<ModelElement> dependencyElements = new ArrayList<ModelElement>();

            ModelElementHelper.iterateClientDependencies(element, dependencyElements);

            for (ModelElement dElement : dependencyElements) {

                URI dURI = dElement.eResource().getURI();
                Resource dResource = resourceSet.getResource(dURI, false);

                if (dResource != null) {
                    File depFile = new File(dResource.getURI().toFileString());
                    dependencyMap.put(depFile, dElement);
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
     * Getter for element.
     * 
     * @return the element
     */
    public ModelElement getElement() {
        return this.element;
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
                        recordList.add(new ItemRecord(aFile));
                    }
                }
            }

            childern = recordList.toArray(new ItemRecord[recordList.size()]);
        }

        return this.childern;
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

        File propFile = propPath.toFile();

        return propFile.exists() && !StringUtils.equals(path.getFileExtension(), FactoriesUtil.PROPERTIES_EXTENSION);
    }

    /**
     * DOC bZhou Comment method "isTOPFile".
     * 
     * @param file
     * @return
     */
    private boolean isValidDirectory(File file) {
        String absolutePath = file.getAbsolutePath();

        boolean tdqProject = false;

        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File afile : listFiles) {
                if (StringUtils.equals(afile.getName(), "talend.project")) {
                    tdqProject = true;
                    break;
                }
            }
        }

        return (absolutePath.indexOf(EResourceConstant.DATA_PROFILING.getName()) > 0
                || absolutePath.indexOf(EResourceConstant.LIBRARIES.getName()) > 0
                || absolutePath.indexOf(EResourceConstant.METADATA.getName()) > 0
                || StringUtils.equals(file.getName(), ReponsitoryContextBridge.PROJECT_DEFAULT_NAME) || tdqProject)
                && !file.getName().startsWith(".");
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
}
