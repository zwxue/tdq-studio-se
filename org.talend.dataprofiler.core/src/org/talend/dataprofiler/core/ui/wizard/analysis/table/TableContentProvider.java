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
package org.talend.dataprofiler.core.ui.wizard.analysis.table;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.talend.cwm.relational.TdTable;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.core.ui.views.provider.MNComposedAdapterFactory;
import org.talend.dq.PluginConstant;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableContentProvider extends AdapterFactoryContentProvider {

    private static Logger log = Logger.getLogger(TableContentProvider.class);

    public TableContentProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IContainer) {
            try {
                return ((IContainer) parentElement).members();
            } catch (CoreException e) {
                log
                        .error(DefaultMessagesImpl.getString("TableContentProvider.cannotGetChildren") + ((IContainer) parentElement).getLocation()); //$NON-NLS-1$
            }
        } else if (parentElement instanceof IFile) {
            IFile prvFile = (IFile) parentElement;
            if (prvFile.getName().endsWith(PluginConstant.PRV_SUFFIX)) {
                parentElement = PrvResourceFileHelper.getInstance().getFileResource((IFile) parentElement);
                return ComparatorsFactory.sort(super.getChildren(parentElement), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
            }
        }
        return super.getChildren(parentElement);
    }

    @Override
    public Object[] getElements(Object object) {
        return this.getChildren(object);
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof IContainer) {
            return ((IContainer) element).getParent();
        }
        return super.getParent(element);
    }

    @Override
    public boolean hasChildren(Object element) {
        return !(element instanceof TdTable);
    }
}
