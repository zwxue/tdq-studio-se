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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ImageLib.CWMImageEnum;
import org.talend.dataquality.indicators.schema.TableIndicator;

/**
 * The provider for table viewer display.
 */
public class TableOfCatalogOrSchemaProvider extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider {

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
        if (element instanceof TableIndicator) {
            TableIndicator indicator = (TableIndicator) element;
            switch (columnIndex) {
            case 0:
                text = indicator.getTableName();
                return text;
            case 1:
                text = PluginConstant.EMPTY_STRING + indicator.getRowCount();
                return text;
            case 2:
                text = indicator.getKeyCount() + PluginConstant.EMPTY_STRING;
                return text;
            case 3:
                text = indicator.getIndexCount() + PluginConstant.EMPTY_STRING;
                return text;
            default:
                break;
            }
        }

        return text;
    }

    public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof TableIndicator && columnIndex == 0) {
            return CWMImageEnum.Table.getImg();
        }
        return null;
    }
}
