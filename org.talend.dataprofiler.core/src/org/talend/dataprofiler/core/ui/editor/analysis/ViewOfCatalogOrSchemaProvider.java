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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataquality.indicators.schema.ViewIndicator;

/**
 * The provider for table viewer display.
 */
public class ViewOfCatalogOrSchemaProvider extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider {

    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {

        if (inputElement == null) {
            return new Object[0];
        }
        Object[] objs = ((List) inputElement).toArray();
        return objs;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    public String getColumnText(Object element, int columnIndex) {
        String text = PluginConstant.EMPTY_STRING;
        if (element instanceof ViewIndicator) {
            ViewIndicator indicator = (ViewIndicator) element;
            switch (columnIndex) {
            case 0:
                text = indicator.getTableName();
                return text;
            case 1:
                text = PluginConstant.EMPTY_STRING + indicator.getRowCount();
                return text;
            default:
                break;
            }
        }

        return text;
    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

}
