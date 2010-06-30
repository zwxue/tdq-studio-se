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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.ModelElementIdentifier;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.AElementPersistance;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class DuplicateCWMResourceAction extends Action {

    private List<ModelElement> elementList = new ArrayList<ModelElement>();

    private DuplicateCWMResourceAction() {
        super(DefaultMessagesImpl.getString("DuplicateCWMResourceAction.Duplicate")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EDIT_COPY));
    }

    /**
     * DOC bZhou DuplicateCWMResourceAction constructor comment.
     * 
     * @param files
     */
    public DuplicateCWMResourceAction(IFile[] files) {
        this();

        for (IFile file : files) {
            elementList.add(ModelElementFileFactory.getModelElement(file));
        }
    }

    /**
     * DOC bZhou DuplicateCWMResourceAction constructor comment.
     * 
     * @param elements
     */
    public DuplicateCWMResourceAction(ModelElement[] elements) {
        this();

        for (ModelElement element : elements) {
            elementList.add(element);
        }
    }

    @Override
    public void run() {
        for (ModelElement oldObject : elementList) {

            if (oldObject != null) {
                ModelElement newObject = (ModelElement) EMFSharedResources.getInstance().copyEObject(oldObject);

                IFolder folder = extractFolder(oldObject);

                if (folder != null) {
                    newObject = update(oldObject, newObject);

                    AElementPersistance elementWriter = ElementWriterFactory.getInstance().create(oldObject);

                    if (elementWriter != null) {
                        TypedReturnCode<IFile> trc = elementWriter.create(newObject, folder);
                        selectAndReveal(trc.getObject());
                    }
                }
            }
        }
        CorePlugin.getDefault().refreshDQView();
    }

    /**
     * DOC bZhou Comment method "extractFolder".
     * 
     * @param oldObject
     * @return
     */
    private IFolder extractFolder(ModelElement oldObject) {
        Resource resource = oldObject.eResource();
        if (resource != null) {
            IPath path = new Path(resource.getURI().toPlatformString(false));
            IFile oldFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            IFolder parent = (IFolder) oldFile.getParent();

            // this is for duplicated system indicator.
            if (ModelElementIdentifier.isID(oldObject) && !ModelElementIdentifier.isDQRule(oldObject)) {
                parent = ResourceManager.getUDIFolder();
            }

            return parent;
        }

        return null;
    }

    /**
     * DOC yyi 2009-09-07, Feature:8882.
     * 
     * Selects and reveals the newly added resource in all parts of the active workbench window's active page.
     * 
     * @param resource
     */
    private void selectAndReveal(IResource resource) {
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = workbenchWindow.getActivePage();
        IWorkbenchPart activePart = page.getActivePart();
        if (activePart instanceof ISetSelectionTarget) {
            ISelection selection = new StructuredSelection(resource);
            ((ISetSelectionTarget) activePart).selectReveal(selection);
        }
    }

    /**
     * DOC bZhou Comment method "update".
     * 
     * @param oldObject
     * @param newObject
     * @return
     */
    private ModelElement update(ModelElement oldObject, ModelElement newObject) {
        newObject.setName("copy of " + newObject.getName()); //$NON-NLS-1$

        String author = ReponsitoryContextBridge.getAuthor();
        if (!StringUtils.isEmpty(author)) {
            MetadataHelper.setAuthor(newObject, author);
        }

        // MOD 2009-01-06 mzhao copy analysis reference.
        if (ModelElementIdentifier.isReport(newObject)) {
            List<Analysis> anaLs = ReportHelper.getAnalyses((TdReport) oldObject);
            for (Analysis analysis : anaLs) {
                DependenciesHandler.getInstance().setDependencyOn((TdReport) newObject, analysis);
                ((TdReport) newObject).addAnalysis(analysis);
            }
        }

        // MOD 2009-11-13 yyi 9349: Duplicate analysis don't work right
        if (ModelElementIdentifier.isAnalysis(newObject)) {
            AnalysisHelper.getDataFilter((Analysis) newObject).clear();
            AnalysisHelper.setStringDataFilter((Analysis) newObject, AnalysisHelper.getStringDataFilter((Analysis) oldObject));
        }

        // MOD 2010-6-20 integrated duplicated action.
        if (ModelElementIdentifier.isID(newObject)) {
            IndicatorDefinition oldID = (IndicatorDefinition) oldObject;
            IndicatorDefinition newID = (IndicatorDefinition) newObject;
            TaggedValueHelper.setValidStatus(true, newID);
            newID.setLabel("");

            Collection<IndicatorCategory> userDefinedIndicatorCategoryList = DefinitionHandler.getInstance()
                    .getUserDefinedIndicatorCategoryList();
            IndicatorCategory category = UDIHelper.getUDICategory(oldID);
            if (userDefinedIndicatorCategoryList.contains(category)) {
                UDIHelper.setUDICategory(newID, category);
            } else {
                UDIHelper.setUDICategory(newID, DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory());
            }
        }

        return newObject;
    }

}
