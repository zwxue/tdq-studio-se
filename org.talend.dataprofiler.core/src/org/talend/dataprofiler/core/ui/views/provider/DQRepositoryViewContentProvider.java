// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.FolderNodeHelper;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.helper.RepResourceFileHelper;
import org.talend.dataprofiler.core.model.nodes.foldernode.IFolderNode;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;

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
            if (file.getName().endsWith(PluginConstant.REP_SUFFIX)) {
                TdReport findReport = RepResourceFileHelper.getInstance().findReport(file);
                Object[] array = ReportHelper.getAnalyses(findReport).toArray();
                return sort(array, ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
            }
            parentElement = PrvResourceFileHelper.getInstance().getFileResource(file);
        } else if (parentElement instanceof IFolderNode) {
            IFolderNode folerNode = (IFolderNode) parentElement;
            if (!(folerNode.isLoaded())) {
                folerNode.loadChildren();
            }
            return sort(folerNode.getChildren(), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
        } else if (SwitchHelpers.CATALOG_SWITCH.doSwitch((EObject) parentElement) != null) {
            if (CatalogHelper.getSchemas(SwitchHelpers.CATALOG_SWITCH.doSwitch((EObject) parentElement)).size() > 0) {
                return sort(super.getChildren(parentElement), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
            } else {
                return FolderNodeHelper.getFolderNode((EObject) parentElement);
            }

        } else {
            return FolderNodeHelper.getFolderNode((EObject) parentElement);
        }
        return sort(super.getChildren(parentElement), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
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
        return !(element instanceof TdColumn);
    }

    /**
     * Sort the parameter objects, and return the sorted array.
     * 
     * @param objects
     * @param comparatorId the comparator id has been defined in the {@link ComparatorsFactory};
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Object[] sort(Object[] objects, int comparatorId) {
        if (objects == null || objects.length <= 1) {
            return objects;
        }
        Arrays.sort(objects, ComparatorsFactory.buildComparator(comparatorId));
        return objects;
    }

}
