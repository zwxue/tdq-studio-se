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
package org.talend.cwm.compare.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.ImageLib;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ReloadDatabaseAction extends Action {

    private static Logger log = Logger.getLogger(ReloadDatabaseAction.class);

    private static final String ANALYSIS_EDITOR_ID = "org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor"; //$NON-NLS-1$

    private Object selectedObject;

    public ReloadDatabaseAction(Object selectedNode, String menuText) {
        super(menuText);
        this.selectedObject = selectedNode;
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.UPDATE_IMAGE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                final IComparisonLevel creatComparisonLevel = ComparisonLevelFactory.creatComparisonLevel(selectedObject);
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        try {
                            DataProvider oldDataProvider = creatComparisonLevel.reloadCurrentLevelElement();

                            // MOD mzhao 2009-07-13 bug 7454 Impact existing
                            // analysis.
                            impactExistingAnalyses(oldDataProvider);
                        } catch (ReloadCompareException e) {
                            // TODO Auto-generated catch block
                            log.error(e, e);
                        } catch (PartInitException e) {
                            log.error(e, e);
                        }

                    }
                });
            }
        };
        try {
            ProgressUI.popProgressDialog(op);
            CorePlugin.getDefault().refreshDQView();
        } catch (InvocationTargetException e) {
            MessageUI.openError(Messages.getString("ReloadDatabaseAction.checkConnectionFailured", e.getCause().getMessage())); //$NON-NLS-1$
            log.error(e, e);
        } catch (InterruptedException e) {
            log.error(e, e);
        }

    }

    private void impactExistingAnalyses(DataProvider oldDataProvider) throws PartInitException {
        EList<Dependency> clientDependencies = oldDataProvider.getSupplierDependency();
        List<Analysis> unsynedAnalyses = new ArrayList<Analysis>();
        for (Dependency dep : clientDependencies) {
            StringBuffer impactedAnaStr = new StringBuffer();
            for (ModelElement mod : dep.getClient()) {
                // MOD mzhao 2009-08-24 The dependencies include "Property" and "Analysis"
                if (!(mod instanceof Analysis)) {
                    continue;
                }
                Analysis ana = (Analysis) mod;
                unsynedAnalyses.add(ana);
                impactedAnaStr.append(ana.getName());
            }

            for (Analysis analysis : unsynedAnalyses) {
                // Reload.
                Resource eResource = analysis.eResource();
                if (eResource == null) {
                    continue;
                }

                EMFSharedResources.getInstance().unloadResource(eResource.getURI().toString());

                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                Path path = new Path(analysis.getFileName());
                IFile file = root.getFile(path);
                analysis = AnaResourceFileHelper.getInstance().readFromFile(file);
                Map<EObject, Collection<Setting>> referenceMaps = EcoreUtil.UnresolvedProxyCrossReferencer.find(eResource);
                Iterator<EObject> it = referenceMaps.keySet().iterator();
                ModelElement eobj = null;
                while (it.hasNext()) {
                    eobj = (ModelElement) it.next();
                    Collection<Setting> settings = referenceMaps.get(eobj);
                    for (Setting setting : settings) {
                        if (setting.getEObject() instanceof AnalysisContext) {
                            analysis.getContext().getAnalysedElements().remove(eobj);
                        } else if (setting.getEObject() instanceof Indicator) {
                            analysis.getResults().getIndicators().remove(setting.getEObject());
                        }
                    }

                }
                AnaResourceFileHelper.getInstance().save(analysis);
            }
        }

        // Refresh current opened editors.
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editors = activePage.getEditorReferences();
        if (editors != null) {
            for (IEditorReference editorRef : editors) {
                if (editorRef.getId().equals(ANALYSIS_EDITOR_ID)) {
                    boolean isConfirm = MessageDialog.openConfirm(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages
                                    .getString("ReloadDatabaseAction.ElementChange"), //$NON-NLS-1$
                            Messages.getString("ReloadDatabaseAction.RefreshCurrentEditor")); //$NON-NLS-1$
                    if (!isConfirm) {
                        return;
                    }
                }
            }

            for (IEditorReference editorRef : editors) {
                IEditorInput editorInput = editorRef.getEditorInput();
                if (editorRef.getId().equals(ANALYSIS_EDITOR_ID)) {
                    activePage.closeEditor(editorRef.getEditor(false), false);
                    activePage.openEditor(editorInput, ANALYSIS_EDITOR_ID);
                }
            }
        }

    }
}
