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
package org.talend.dataprofiler.core.ui.views.provider;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataprofiler.core.helper.FolderNodeHelper;
import org.talend.dataprofiler.core.model.nodes.foldernode.AnaElementFolderNode;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.category.CategoryHandler;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class DQRepositoryViewContentProvider extends AdapterFactoryContentProvider {

    /**
     * @param adapterFactory
     */
    public DQRepositoryViewContentProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IFile) {
            IFile file = (IFile) parentElement;
            if (FactoriesUtil.isAnalysisFile(file)) {
                Analysis analysis = (Analysis) AnaResourceFileHelper.getInstance().findAnalysis(file);
                EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
                AnaElementFolderNode folderNode = new AnaElementFolderNode(analysedElements);
                return new Object[] { folderNode };
            }
            parentElement = PrvResourceFileHelper.getInstance().getFileResource(file);
        } else if (parentElement instanceof IFolderNode) {
            IFolderNode folerNode = (IFolderNode) parentElement;
            folerNode.loadChildren();
            if (folerNode.getChildren() == null) {
                return new Object[0];
            }
            if (folerNode.getChildrenType() == IFolderNode.MODELELEMENT_TYPE) {
                return ComparatorsFactory.sort(folerNode.getChildren(), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
            } else {
                return ComparatorsFactory.sort(folerNode.getChildren(), ComparatorsFactory.FILE_RESOURCE_COMPARATOR_ID);
            }
        } else if (SwitchHelpers.CATALOG_SWITCH.doSwitch((EObject) parentElement) != null) {
            if (CatalogHelper.getSchemas(SwitchHelpers.CATALOG_SWITCH.doSwitch((EObject) parentElement)).size() > 0) {
                return ComparatorsFactory.sort(super.getChildren(parentElement), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
            } else {
                return FolderNodeHelper.getFolderNodes((EObject) parentElement);
            }

        } else if (SwitchHelpers.XMLDOCUMENT_SWITCH.doSwitch((EObject) parentElement) != null) {
            // MOD mzhao feature 10238 xml documents.
            return DqRepositoryViewService.getXMLElements((TdXMLDocument) parentElement).toArray();
        } else if (SwitchHelpers.XMLELEMENT_SWITCH.doSwitch((EObject) parentElement) != null) {
            // MOD mzhao xml elements
            return DqRepositoryViewService.getXMLElements((TdXMLElement) parentElement).toArray();

        } else if (parentElement instanceof IndicatorCategory) {
            IndicatorCategory category = (IndicatorCategory) parentElement;
            Map<IndicatorCategory, List<IndicatorDefinition>> categoriesIDMaps = CategoryHandler.getCategoriesIDMaps();
            List<IndicatorDefinition> list = categoriesIDMaps.get(category);
            if (list != null) {
                return list.toArray();
            }

        } else {
            return FolderNodeHelper.getFolderNodes((EObject) parentElement);
        }
        return ComparatorsFactory.sort(super.getChildren(parentElement), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
    }

    @Override
    public Object[] getElements(Object object) {
        return this.getChildren(object);
    }

    public Object getParent(Object element) {
        if (element instanceof IFile) {
            return ((IResource) element).getParent();
        }
        return super.getParent(element);
    }

    public boolean hasChildren(Object element) {
        if (element instanceof EObject) {
            EObject eobject = (EObject) element;
            if (SwitchHelpers.XMLELEMENT_SWITCH.doSwitch(eobject) != null) {
                return DqRepositoryViewService.hasChildren((TdXMLElement) element);
            }

            if (SwitchHelpers.COLUMN_SWITCH.doSwitch(eobject) != null) {
                return false;
            }

            if (eobject instanceof IndicatorDefinition) {
                return false;
            }
        }

        return true;
    }
}
