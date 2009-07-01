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
package org.talend.dataprofiler.core.ui.editor.indicator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.util.EditUIUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataquality.indicators.definition.IndicatorsDefinitions;
import orgomg.cwm.analysis.businessnomenclature.provider.BusinessnomenclatureItemProviderAdapterFactory;
import orgomg.cwm.analysis.datamining.provider.DataminingItemProviderAdapterFactory;
import orgomg.cwm.analysis.informationvisualization.provider.InformationvisualizationItemProviderAdapterFactory;
import orgomg.cwm.analysis.olap.provider.OlapItemProviderAdapterFactory;
import orgomg.cwm.analysis.transformation.provider.TransformationItemProviderAdapterFactory;
import orgomg.cwm.foundation.businessinformation.provider.BusinessinformationItemProviderAdapterFactory;
import orgomg.cwm.foundation.datatypes.provider.DatatypesItemProviderAdapterFactory;
import orgomg.cwm.foundation.expressions.provider.ExpressionsItemProviderAdapterFactory;
import orgomg.cwm.foundation.keysindexes.provider.KeysindexesItemProviderAdapterFactory;
import orgomg.cwm.foundation.softwaredeployment.provider.SoftwaredeploymentItemProviderAdapterFactory;
import orgomg.cwm.foundation.typemapping.provider.TypemappingItemProviderAdapterFactory;
import orgomg.cwm.management.warehouseoperation.provider.WarehouseoperationItemProviderAdapterFactory;
import orgomg.cwm.management.warehouseprocess.events.provider.EventsItemProviderAdapterFactory;
import orgomg.cwm.management.warehouseprocess.provider.WarehouseprocessItemProviderAdapterFactory;
import orgomg.cwm.objectmodel.behavioral.provider.BehavioralItemProviderAdapterFactory;
import orgomg.cwm.objectmodel.core.provider.CoreItemProviderAdapterFactory;
import orgomg.cwm.objectmodel.instance.provider.InstanceItemProviderAdapterFactory;
import orgomg.cwm.objectmodel.relationships.provider.RelationshipsItemProviderAdapterFactory;
import orgomg.cwm.resource.multidimensional.provider.MultidimensionalItemProviderAdapterFactory;
import orgomg.cwm.resource.record.provider.RecordItemProviderAdapterFactory;
import orgomg.cwm.resource.relational.provider.RelationalItemProviderAdapterFactory;
import orgomg.cwm.resource.xml.provider.XmlItemProviderAdapterFactory;
import orgomg.cwmmip.provider.CwmmipItemProviderAdapterFactory;
import orgomg.cwmx.analysis.informationreporting.provider.InformationreportingItemProviderAdapterFactory;
import orgomg.cwmx.analysis.informationset.provider.InformationsetItemProviderAdapterFactory;
import orgomg.cwmx.foundation.er.provider.ErItemProviderAdapterFactory;
import orgomg.cwmx.resource.coboldata.provider.CoboldataItemProviderAdapterFactory;
import orgomg.cwmx.resource.dmsii.provider.DmsiiItemProviderAdapterFactory;
import orgomg.cwmx.resource.essbase.provider.EssbaseItemProviderAdapterFactory;
import orgomg.cwmx.resource.express.provider.ExpressItemProviderAdapterFactory;
import orgomg.cwmx.resource.imsdatabase.provider.ImsdatabaseItemProviderAdapterFactory;
import orgomg.mof.model.provider.ModelItemProviderAdapterFactory;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorEditor extends CommonFormEditor implements IEditingDomainProvider {

    private static Logger log = Logger.getLogger(IndicatorEditor.class);

    protected AdapterFactoryEditingDomain editingDomain;

    protected ComposedAdapterFactory adapterFactory;

    protected Collection<Resource> removedResources = new ArrayList<Resource>();

    protected IndicatorsDefinitions indicatorDefinitions;

    /**
     * Resources that have been changed since last activation.
     */
    protected Collection<Resource> changedResources = new ArrayList<Resource>();

    /**
     * Resources that have been saved.
     */
    protected Collection<Resource> savedResources = new ArrayList<Resource>();

    protected Map<Resource, Diagnostic> resourceToDiagnosticMap = new LinkedHashMap<Resource, Diagnostic>();

    protected boolean updateProblemIndication = true;

    protected IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {

        public void resourceChanged(IResourceChangeEvent event) {
            IResourceDelta delta = event.getDelta();
            try {
                class ResourceDeltaVisitor implements IResourceDeltaVisitor {

                    protected ResourceSet resourceSet = editingDomain.getResourceSet();

                    protected Collection<Resource> changedResources = new ArrayList<Resource>();

                    protected Collection<Resource> removedResources = new ArrayList<Resource>();

                    public boolean visit(IResourceDelta delta) {
                        if (delta.getResource().getType() == IResource.FILE) {
                            if (delta.getKind() == IResourceDelta.REMOVED || delta.getKind() == IResourceDelta.CHANGED
                                    && delta.getFlags() != IResourceDelta.MARKERS) {
                                Resource resource = resourceSet.getResource(URI.createURI(delta.getFullPath().toString()), false);
                                if (resource != null) {
                                    if (delta.getKind() == IResourceDelta.REMOVED) {
                                        removedResources.add(resource);
                                    } else if (!savedResources.remove(resource)) {
                                        changedResources.add(resource);
                                    }
                                }
                            }
                        }

                        return true;
                    }

                    public Collection<Resource> getChangedResources() {
                        return changedResources;
                    }

                    public Collection<Resource> getRemovedResources() {
                        return removedResources;
                    }
                }

                ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
                delta.accept(visitor);

                if (!visitor.getRemovedResources().isEmpty()) {
                    removedResources.addAll(visitor.getRemovedResources());
                    if (!isDirty()) {
                        getSite().getShell().getDisplay().asyncExec(new Runnable() {

                            public void run() {
                                getSite().getPage().closeEditor(IndicatorEditor.this, false);
                            }
                        });
                    }
                }

                if (!visitor.getChangedResources().isEmpty()) {
                    changedResources.addAll(visitor.getChangedResources());
                    if (getSite().getPage().getActiveEditor() == IndicatorEditor.this) {
                        getSite().getShell().getDisplay().asyncExec(new Runnable() {

                            public void run() {
                                handleActivate();
                            }
                        });
                    }
                }
            } catch (CoreException exception) {
                log.error(exception, exception);
            }
        }
    };

    public IndicatorEditor() {
        super();
        initializeEditingDomain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInputWithNotify(input);
        setPartName(input.getName());
        ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
    }

    protected void handleActivate() {
        // Recompute the read only state.
        //
        if (editingDomain.getResourceToReadOnlyMap() != null) {
            editingDomain.getResourceToReadOnlyMap().clear();

            // Refresh any actions that may become enabled or disabled.
            //
            // setSelection(getSelection());
        }

        if (!removedResources.isEmpty()) {
            if (handleDirtyConflict()) {
                getSite().getPage().closeEditor(IndicatorEditor.this, false);
            } else {
                removedResources.clear();
                changedResources.clear();
                savedResources.clear();
            }
        } else if (!changedResources.isEmpty()) {
            changedResources.removeAll(savedResources);
            handleChangedResources();
            changedResources.clear();
            savedResources.clear();
        }
    }

    protected void handleChangedResources() {
        if (!changedResources.isEmpty() && (!isDirty() || handleDirtyConflict())) {
            if (isDirty()) {
                changedResources.addAll(editingDomain.getResourceSet().getResources());
            }
            editingDomain.getCommandStack().flush();

            updateProblemIndication = false;
            for (Resource resource : changedResources) {
                if (resource.isLoaded()) {
                    resource.unload();
                    try {
                        resource.load(Collections.EMPTY_MAP);
                    } catch (IOException exception) {
                        // if (!resourceToDiagnosticMap.containsKey(resource)) {
                        // resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
                        // }
                    }
                }
            }

            // if (AdapterFactoryEditingDomain.isStale(editorSelection)) {
            // setSelection(StructuredSelection.EMPTY);
            // }

            updateProblemIndication = true;
            // updateProblemIndication();
        }
    }

    protected boolean handleDirtyConflict() {
        return MessageDialog.openQuestion(getSite().getShell(), "FileConflict", "File Conflict!");
    }

    /**
     * DOC bZhou Comment method "initializeEditingDomain".
     */
    protected void initializeEditingDomain() {
        // Create an adapter factory that yields item providers.
        //
        adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

        adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new CoreItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new BehavioralItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new RelationshipsItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new InstanceItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new BusinessinformationItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new DatatypesItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new ExpressionsItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new KeysindexesItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new SoftwaredeploymentItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new TypemappingItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new RelationalItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new RecordItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new MultidimensionalItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new XmlItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new TransformationItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new OlapItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new DataminingItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new InformationvisualizationItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new BusinessnomenclatureItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new WarehouseprocessItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new EventsItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new WarehouseoperationItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new ErItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new CoboldataItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new DmsiiItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new ImsdatabaseItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new EssbaseItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new ExpressItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new InformationsetItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new InformationreportingItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new CwmmipItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new ModelItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

        // Create the command stack that will notify this editor as commands are executed.
        //
        BasicCommandStack commandStack = new BasicCommandStack();

        // Add a listener to set the most recent command's affected objects to be the selection of the viewer with
        // focus.
        //
        commandStack.addCommandStackListener(new CommandStackListener() {

            public void commandStackChanged(final EventObject event) {
                getContainer().getDisplay().asyncExec(new Runnable() {

                    public void run() {
                        firePropertyChange(IEditorPart.PROP_DIRTY);

                        // Try to select the affected objects.
                        //
                        Command mostRecentCommand = ((CommandStack) event.getSource()).getMostRecentCommand();
                        if (mostRecentCommand != null) {
                            System.out.println("most recent command");
                        }
                    }
                });
            }
        });

        // Create the editing domain with a special command stack.
        //
        editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>());
    }

    public void createModel() {
        URI resourceURI = EditUIUtil.getURI(getEditorInput());
        Exception exception = null;
        Resource resource = null;
        try {
            // Load the resource through the editing domain.
            //
            resource = editingDomain.getResourceSet().getResource(resourceURI, true);
        } catch (Exception e) {
            exception = e;
            resource = editingDomain.getResourceSet().getResource(resourceURI, false);
        }

        Diagnostic diagnostic = analyzeResourceProblems(resource, exception);
        if (diagnostic.getSeverity() != Diagnostic.OK) {
            resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.CommonFormEditor#addPages()
     */
    @Override
    protected void addPages() {

        createModel();

        super.addPages();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.CommonFormEditor#isDirty()
     */
    @Override
    public boolean isDirty() {
        return ((BasicCommandStack) editingDomain.getCommandStack()).isSaveNeeded();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.CommonFormEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        // Save only resources that have actually changed.
        //
        final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
        saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

        // Do the work within an operation because this is a long running activity that modifies the workbench.
        //
        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

            // This is the method that gets invoked when the operation runs.
            //
            @Override
            public void execute(IProgressMonitor monitor) {
                // Save the resources to the file system.
                //
                boolean first = true;
                for (Resource resource : editingDomain.getResourceSet().getResources()) {
                    if ((first || !resource.getContents().isEmpty() || isPersisted(resource))
                            && !editingDomain.isReadOnly(resource)) {
                        try {
                            long timeStamp = resource.getTimeStamp();
                            resource.save(saveOptions);
                            if (resource.getTimeStamp() != timeStamp) {
                                savedResources.add(resource);
                            }
                        } catch (Exception exception) {
                            // resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
                        }
                        first = false;
                    }
                }
            }
        };

        updateProblemIndication = false;
        try {
            // This runs the options, and shows progress.
            //
            new ProgressMonitorDialog(getSite().getShell()).run(true, false, operation);

            // Refresh the necessary state.
            //
            ((BasicCommandStack) editingDomain.getCommandStack()).saveIsDone();
            firePropertyChange(IEditorPart.PROP_DIRTY);
        } catch (Exception exception) {
            // Something went wrong that shouldn't.
            //
            log.error(exception, exception);
        }
    }

    public Diagnostic analyzeResourceProblems(Resource resource, Exception exception) {
        if (!resource.getErrors().isEmpty() || !resource.getWarnings().isEmpty()) {
            BasicDiagnostic basicDiagnostic = new BasicDiagnostic(Diagnostic.ERROR, "org.talend.dataquality.editor", 0,
                    "current model error", new Object[] { exception == null ? (Object) resource : exception });
            basicDiagnostic.merge(EcoreUtil.computeDiagnostic(resource, true));
            return basicDiagnostic;
        } else if (exception != null) {
            return new BasicDiagnostic(Diagnostic.ERROR, "org.talend.dataquality.editor", 0, "current model error",
                    new Object[] { exception });
        } else {
            return Diagnostic.OK_INSTANCE;
        }
    }

    protected void updateProblemIndication() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.edit.domain.IEditingDomainProvider#getEditingDomain()
     */
    public EditingDomain getEditingDomain() {
        return editingDomain;
    }

    protected boolean isPersisted(Resource resource) {
        boolean result = false;
        try {
            InputStream stream = editingDomain.getResourceSet().getURIConverter().createInputStream(resource.getURI());
            if (stream != null) {
                result = true;
                stream.close();
            }
        } catch (IOException e) {
            // Ignore
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.CommonFormEditor#dispose()
     */
    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
        super.dispose();
    }

    /**
     * Sets the indicatorDefinitions.
     * 
     * @param indicatorDefinitions the indicatorDefinitions to set
     */
    public void setIndicatorDefinitions(IndicatorsDefinitions indicatorDefinitions) {
        this.indicatorDefinitions = indicatorDefinitions;
    }

    /**
     * Getter for indicatorDefinitions.
     * 
     * @return the indicatorDefinitions
     */
    public IndicatorsDefinitions getIndicatorDefinitions() {
        return indicatorDefinitions;
    }
}
