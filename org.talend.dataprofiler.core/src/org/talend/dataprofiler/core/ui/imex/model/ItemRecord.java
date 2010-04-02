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
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ItemRecord {

    private File file;

    private String projectName;

    private ModelElement element;

    private List<String> errors = new ArrayList<String>();

    private static ResourceSet resourceSet;

    public ItemRecord(File file) {
        this.file = file;

        init();
    }

    /**
     * DOC bZhou Comment method "init".
     */
    private void init() {
        if (resourceSet == null) {
            resourceSet = new ResourceSetImpl();
        }

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
    }

    /**
     * DOC bZhou Comment method "computeDependencies".
     */
    public void computeDependencies() {
        List<ModelElement> dependencyElements = new ArrayList<ModelElement>();

        if (resourceSet != null) {
            ModelElementHelper.iterateClientDependencies(element, dependencyElements);
            for (ModelElement melement : dependencyElements) {
                if (melement.eIsProxy()) {
                    InternalEObject inObject = (InternalEObject) melement;
                    addError("\"" + element.getName() + "\" missing dependented file : " + inObject.eProxyURI().toString());
                }
            }
        }
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
     * Getter for fullPath.
     * 
     * @return the fullPath
     */
    public IPath getFullPath() {
        if (file.isFile()) {
            IPath path = new Path(file.getAbsolutePath());
            path = path.makeRelativeTo(ResourceManager.getRootProject().getLocation());
            return path.removeLastSegments(1);
        }
        return null;
    }

    /**
     * Getter for propertyFilePath.
     * 
     * @return the propertyFilePath
     */
    public String getPropertyFilePath() {
        if (file != null) {
            IPath itemResPath = new Path(file.getAbsolutePath());
            IPath propResPath = itemResPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
            return propResPath.toOSString();
        }
        return null;
    }

    /**
     * clear the resource set.
     */
    public void clear() {

        if (resourceSet != null) {
            for (Resource resource : resourceSet.getResources()) {
                resource.unload();
            }
            resourceSet.getResources().clear();
            resourceSet = null;
        }

        element = null;
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
     * Sets the projectName.
     * 
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Getter for projectName.
     * 
     * @return the projectName
     */
    public String getProjectName() {
        return this.projectName;
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
}
