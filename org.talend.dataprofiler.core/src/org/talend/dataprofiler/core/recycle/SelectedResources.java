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
package org.talend.dataprofiler.core.recycle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.resource.ResourceManager;

/**
 * @author qiongli Get all selected elements which used to delete forever
 */
public class SelectedResources {

    public SelectedResources() {
    }

    /**
     * Return an array of the currently selected resources. MOD qiongli 2010-10-9,bug 15674
     * 
     * @return the selected resources
     */
    @SuppressWarnings("unchecked")
    public Property[] getSelectedArrayForDelForever() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();

        Set<Property> propList = new HashSet<Property>();
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof DQRecycleBinNode) {
                DQRecycleBinNode rbn = (DQRecycleBinNode) obj;
                Object o = rbn.getObject();

                if (o instanceof Property) {
                    Property property = (Property) o;
                    if (property.getItem().getState().isDeleted())
                        propList.add(property);
                } else if (o instanceof IFolder) {
                    getAllSubFilesByRecycleBinNode((IFolder) o, propList);
                }
            } else {
                return new Property[0];
            }
        }
        return propList.toArray(new Property[propList.size()]);
    }

    /**
     * DOC qiongli Comment method "getAllSubFilesByRecycleBinNode".MOD qiongli 2010-10-9,bug 15674.
     * 
     * @param folder
     * @param fileList
     */
    private void getAllSubFilesByRecycleBinNode(IFolder folder, Set<Property> fileList) {
        HashSet<Property> allDelLs = LogicalDeleteFileHandle.getDelPropertyLs();
        IFile itemFile = null;
        for (Property prop : allDelLs) {
            itemFile = PropertyHelper.getItemFile(prop);
            if (itemFile.exists() && itemFile.getFullPath().toOSString().startsWith(folder.getFullPath().toOSString())) {
                fileList.add(prop);
            }
        }
    }

    /**
     * DOC bZhou Comment method "iteratedProperties".Move the method from DeleteObjectsAction.java to this
     * class(-qiongli).
     * 
     * @param obj
     * @param propList
     * @throws Exception
     */
    public void getPropertiesByObject(Object obj, Set<Property> propList) throws Exception {
        if (obj instanceof IFile) {
            IFile file = (IFile) obj;
            Property property = PropertyHelper.getProperty(file);
            if (property == null) {
                property = PropertyHelper.createTDQItemProperty();
                property.setLabel(file.getName());
                ((TDQItem) property.getItem()).setFilename(file.getFullPath().toString());
            }

            if (!property.getItem().getState().isDeleted()) {
                propList.add(property);
            }
        } else if (obj instanceof IFolder) {
            IFolder folder = (IFolder) obj;
            // MOD qiongli bug 15587:if it is a folder under metadata,get the property by IRepositoryViewObject
            IPath path = folder.getFullPath();
            List<IRepositoryViewObject> conList = null;
            boolean hasChild = true;
            if (folder.members().length == 0) {
                hasChild = false;
            }
            if (hasChild && ResourceManager.getConnectionFolder().getFullPath().isPrefixOf(path)) {
                path = path.makeRelativeTo(ResourceManager.getConnectionFolder().getFullPath());
                conList = ProxyRepositoryViewObject.fetchRepositoryViewObjectsByFolder(true,
                        ERepositoryObjectType.METADATA_CONNECTIONS, path, true, null);
            } else if (hasChild && ResourceManager.getMDMConnectionFolder().getFullPath().isPrefixOf(path)) {
                path = path.makeRelativeTo(ResourceManager.getMDMConnectionFolder().getFullPath());
                conList = ProxyRepositoryViewObject.fetchRepositoryViewObjectsByFolder(true,
                        ERepositoryObjectType.METADATA_MDMCONNECTION, path, true, null);
            }
            if (conList != null) {
                for (IRepositoryViewObject repViewObj : conList) {
                    Property prop = repViewObj.getProperty();
                    if (!prop.getItem().getState().isDeleted())
                        propList.add(prop);
                }
            } else {
                if (!existChildFolder(propList, folder)) {
                    Property property = PropertyHelper.createFolderItemProperty();
                    property.getItem().getState().setPath(folder.getFullPath().toOSString());
                    propList.add(property);
                }
                for (IResource rersource : folder.members()) {
                    getPropertiesByObject(rersource, propList);
                }
            }
        } else if (obj instanceof IRepositoryViewObject) {
            propList.add(((IRepositoryViewObject) obj).getProperty());
        } else if (obj instanceof DQRecycleBinNode) {
            // MOD qiongli 2010-10-9,bug 15674
            Object o = ((DQRecycleBinNode) obj).getObject();
            if (o instanceof Property) {
                propList.add((Property) o);
            } else {
                String pathStr = o.toString();
                IPath path = new Path(pathStr).removeFirstSegments(1);
                Property property = PropertyHelper.createFolderItemProperty();
                property.getItem().getState().setPath(path.toOSString());
                propList.add(property);
                // if (o instanceof IFolder)
                // getAllSubFilesByRecycleBinNode((IFolder) o, propList);
            }
        }
    }

    /**
     * DOC bZhou Comment method "existChildFolder".
     * 
     * @param propList
     * @param folder
     * @return
     */
    private boolean existChildFolder(Set<Property> propList, IFolder folder) {
        for (Property property : propList) {
            // bug 14697 avoid NPE
            if (property.getItem().getState().getPath() == null)
                return false;
            IPath path = new Path(property.getItem().getState().getPath());
            if (folder.getFullPath().isPrefixOf(path)) {
                return true;
            }
        }
        return false;
    }

}
