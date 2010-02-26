// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import orgomg.cwm.objectmodel.core.CorePackage;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class ResoureceChangedListener extends WorkbenchContentProvider {

    private static Logger log = Logger.getLogger(ResoureceChangedListener.class);

    protected EMFSharedResources util = EMFSharedResources.getInstance();

    // private IWorkspace wsp = ResourceManager.getRootProject().getWorkspace();

    @Override
    public void processDelta(final IResourceDelta rootDelta) {
        final List<IFile> added = new ArrayList<IFile>();
        final List<IFile> removed = new ArrayList<IFile>();
        // wsp.removeResourceChangeListener(this);
        IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {

            public boolean visit(IResourceDelta delta) {
                if (delta.getKind() == IResourceDelta.ADDED) {
                    IResource resource = delta.getResource();
                    if (resource.getType() == IResource.FILE && FactoriesUtil.isEmfFile(resource.getFileExtension())) {
                        added.add((IFile) resource);
                    }
                }

                if (delta.getKind() == IResourceDelta.REMOVED) {
                    IResource resource = delta.getResource();
                    if (resource.getType() == IResource.FILE && FactoriesUtil.isEmfFile(resource.getFileExtension())) {
                        removed.add((IFile) resource);
                    }
                }
                return true;
            }

        };

        try {
            rootDelta.accept(visitor);
        } catch (CoreException e1) {
            log.error(e1);
        }

        List<Runnable> refreshedRannables = new ArrayList<Runnable>();

        for (IFile file : removed) {
            try {
                refreshedRannables.add(getDeleteRunnable(file));
            } catch (Exception e) {
                log.error(e);
            }
        }
        for (IFile file : added) {
            Resource resource = null;
            try {
                // URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
                // if (isExist(uri)) {
                resource = RepResourceFileHelper.getInstance().getFileResource(file);
                // resource = EMFSharedResources.getInstance().getResource(uri, true);
                refreshedRannables.add(getRefreshRunnable(resource));
                // }
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
            }
        }
        if (refreshedRannables != null && refreshedRannables.size() > 0) {
            postEventExecute(refreshedRannables);
        }
        // reRegisterListner();
    }

    // private void reRegisterListner() {
    // WorkspaceJob cleanJob = new WorkspaceJob("Register resource listener") {
    //
    // public boolean belongsTo(Object family) {
    // return ResourcesPlugin.FAMILY_MANUAL_BUILD.equals(family);
    // }
    //
    // public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
    // wsp.addResourceChangeListener(ResoureceChangedListener.this);
    // return Status.OK_STATUS;
    // }
    // };
    // cleanJob.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
    // cleanJob.setUser(true);
    // cleanJob.schedule();
    //
    // }

    private void postEventExecute(final List<Runnable> refreshedRunnables) {
        WorkspaceJob cleanJob = new WorkspaceJob("Workspace refresh") {

            public boolean belongsTo(Object family) {
                return ResourcesPlugin.FAMILY_MANUAL_BUILD.equals(family);
            }

            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

                runUpdates(refreshedRunnables);

                return Status.OK_STATUS;
            }
        };
        cleanJob.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
        cleanJob.setUser(true);
        cleanJob.schedule();

    }

    private void runUpdates(List<Runnable> runnables) {
        Iterator<Runnable> runnableIterator = runnables.iterator();
        while (runnableIterator.hasNext()) {
            ((Runnable) runnableIterator.next()).run();
        }

    }

    private Runnable getDeleteRunnable(final IFile file) {
        return new Runnable() {

            public void run() {
                IFile propFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
                        file.getFullPath().removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION));
                if (propFile != null) {
                    try {
                        propFile.delete(true, new NullProgressMonitor());
                    } catch (CoreException e) {
                        log.error(e);
                    }
                }
            }
        };
    }

    private Runnable getRefreshRunnable(final Resource resource) {
        return new Runnable() {

            public void run() {

                IFile propertyFile = getPropertyFile(resource);

                if (propertyFile != null) {

                    Resource propertyResource = RepResourceFileHelper.getInstance().getFileResource(propertyFile);

                    updateProperty(resource, propertyResource);

                    updateElement(resource, propertyResource);

                    clear(propertyFile);
                }
            }

        };
    }

    /**
     * DOC bZhou Comment method "clear".
     * 
     * @param file
     */
    private void clear(IFile file) {
        try {
            // Delete previous property file.
            file.delete(true, new NullProgressMonitor());
        } catch (CoreException e) {
            log.error(e);
        }
    }

    /**
     * DOC bZhou Comment method "updateElement".
     * 
     * @param resource
     * @param propertyResource
     */
    private void updateElement(final Resource resource, Resource propertyResource) {
        EList<EObject> modelElements = resource.getContents();

        ModelElement modelElement = (ModelElement) EcoreUtil.getObjectByType(modelElements, CorePackage.eINSTANCE
                .getModelElement());
        if (modelElement != null) {
            MetadataHelper.setPropertyPath(propertyResource.getURI().toPlatformString(true), modelElement);
            util.saveResource(resource);
        }
    }

    /**
     * DOC bZhou Comment method "updateProperty".
     * 
     * @param elementResource
     * @param propertyResource
     */
    private void updateProperty(final Resource elementResource, Resource propertyResource) {
        EList<EObject> contents = propertyResource.getContents();

        Property property = (Property) EcoreUtil.getObjectByType(contents, PropertiesPackage.eINSTANCE.getProperty());

        if (property != null) {
            URI elementURI = elementResource.getURI();
            String fileExtension = elementURI.fileExtension();

            IPath path = new Path(elementURI.toPlatformString(true));
            path = path.makeRelativeTo(PropertyHelper.getItemWorkspaceBasePath(fileExtension));
            path = path.removeLastSegments(1);
            property.getItem().getState().setPath(path.toString());
        }

        URI desUri = elementResource.getURI().trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
        EMFSharedResources.getInstance().saveToUri(propertyResource, desUri.trimSegments(1));
    }

    /**
     * DOC bZhou Comment method "getPropertyFile".
     * 
     * @param resource
     * @return
     */
    private IFile getPropertyFile(Resource resource) {

        if (resource != null) {

            EList<EObject> modelElements = resource.getContents();

            ModelElement modelElement = (ModelElement) EcoreUtil.getObjectByType(modelElements, CorePackage.eINSTANCE
                    .getModelElement());
            if (modelElement != null) {
                String propertyPath = MetadataHelper.getPropertyPath(modelElement);
                return (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(propertyPath);
            }

        }
        return null;

    }
}
