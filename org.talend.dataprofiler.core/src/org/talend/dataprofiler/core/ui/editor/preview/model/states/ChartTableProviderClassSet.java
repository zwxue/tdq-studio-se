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
package org.talend.dataprofiler.core.ui.editor.preview.model.states;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartWithData;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.indicators.preview.table.PatternChartDataEntity;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartTableProviderClassSet {

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    public static class BaseChartTableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            String currentText = getColumnText(element, columnIndex);
            boolean isCurrentCol = currentText.equals(entity.getValue()) || currentText.equals(entity.getPersent());
            if (isCurrentCol && entity.isOutOfRange(currentText)) {
                return ImageLib.getImage(ImageLib.LEVEL_WARNING);
            }

            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;
            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getValue();
            case 2:
                return entity.getPersent();
            default:
                return ""; //$NON-NLS-1$
            }
        }

        public Color getBackground(Object element, int columnIndex) {

            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
         */
        public Color getForeground(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            String currentText = getColumnText(element, columnIndex);
            boolean isCurrentCol = currentText.equals(entity.getValue()) || currentText.equals(entity.getPersent());
            if (isCurrentCol && entity.isOutOfRange(currentText)) {
                return Display.getDefault().getSystemColor(SWT.COLOR_RED);
            }

            return null;
        }
    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    public static class SummaryLabelProvider extends BaseChartTableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getValue();

            default:
                return ""; //$NON-NLS-1$
            }
        }
    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    public static class FrequencyLabelProvider extends BaseChartTableLabelProvider {

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.
         * BaseChartTableLabelProvider#getColumnText(java.lang.Object, int)
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;
            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getValue();
            case 2:
                return entity.getPersent();
            default:
                return ""; //$NON-NLS-1$
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.
         * BaseChartTableLabelProvider#getForeground(java.lang.Object, int)
         */
        @Override
        public Color getForeground(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;
            String label = entity.getLabel();
            switch (columnIndex) {
            case 0:
                if (PluginConstant.NULL_FIELD.equals(label) || PluginConstant.EMPTY_FIELD.equals(label)) {
                    return Display.getDefault().getSystemColor(SWT.COLOR_RED);
                }
            default:
                return null;
            }
        }
    }

    /**
     * DOC mzhao FrequencyTypeStates, soundex frequency label provider.Feature: 6307.
     */
    public static class SoundexBaseChartTableLabelProvider extends BaseChartTableLabelProvider {

        @Override
        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getValue();
            case 2:
                return String.valueOf(((FrequencyIndicator) entity.getIndicator()).getCount(entity.getKey()));
            case 3:
                return entity.getPersent();
            default:
                return ""; //$NON-NLS-1$
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.
         * BaseChartTableLabelProvider#getForeground(java.lang.Object, int)
         */
        @Override
        public Color getForeground(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;
            String label = entity.getLabel();
            switch (columnIndex) {
            case 0:
                if (PluginConstant.NULL_FIELD.equals(label) || PluginConstant.EMPTY_FIELD.equals(label)) {
                    return Display.getDefault().getSystemColor(SWT.COLOR_RED);
                }
            default:
                return null;
            }
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    public static class PatternLabelProvider extends BaseChartTableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            PatternChartDataEntity entity = (PatternChartDataEntity) element;

            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getPerMatch();
            case 2:
                return entity.getPerNoMatch();
            case 3:
                return entity.getNumMatch();
            case 4:
                return entity.getNumNoMatch();

            default:
                return ""; //$NON-NLS-1$
            }
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            PatternChartDataEntity entity = (PatternChartDataEntity) element;

            String currentText = getColumnText(element, columnIndex);
            // MOD mzhao bug 8838 2009-09-08
            boolean isCurrentCol = currentText.equals(entity.getNumMatch()) || currentText.equals(entity.getPerMatch());
            if (isCurrentCol && entity.isOutOfRange(currentText)) {
                return ImageLib.getImage(ImageLib.LEVEL_WARNING);
            }

            return null;
        }

        @Override
        public Color getForeground(Object element, int columnIndex) {
            PatternChartDataEntity entity = (PatternChartDataEntity) element;

            String currentText = getColumnText(element, columnIndex);
         // MOD mzhao bug 8838 2009-09-08
            boolean isCurrentCol = currentText.equals(entity.getNumMatch()) || currentText.equals(entity.getPerMatch());
            if (isCurrentCol && entity.isOutOfRange(currentText)) {
                return Display.getDefault().getSystemColor(SWT.COLOR_RED);
            }

            return null;
        }
    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    public static class ModeLabelProvider extends BaseChartTableLabelProvider {

        @Override
        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            return entity.getValue();
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    public static class CommonContenteProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof ChartWithData) {
                return ((ChartWithData) inputElement).getEnity();
            } else {
                return new Object[0];
            }
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }
}
