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
package org.talend.dataprofiler.core.ui.editor.preview.model;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.talend.dataprofiler.core.ui.editor.preview.CompositeIndicator;
import org.talend.dataquality.helpers.IndicatorHelper;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartTableFactory {

    public static void createTable(Composite parent, ChartWithData inputObject) {
        TableViewer tbViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);

        Table table = tbViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (inputObject.getChartNamedType().equals(CompositeIndicator.SIMPLE_STATISTICS)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Label");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("Count");
            column2.setWidth(150);
            TableColumn column3 = new TableColumn(table, SWT.NONE);
            column3.setText("%");
            column3.setWidth(150);

            tbViewer.setLabelProvider(new SimpleLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.TEXT_STATISTICS)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Value");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("Count");
            column2.setWidth(300);

            tbViewer.setLabelProvider(new SimpleLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.FREQUENCE_STATISTICS)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Value");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("Count");
            column2.setWidth(150);
            TableColumn column3 = new TableColumn(table, SWT.NONE);
            column3.setText("%");
            column3.setWidth(150);

            tbViewer.setLabelProvider(new FrequencyLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.SUMMARY_STATISTICS)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Label");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("Value");
            column2.setWidth(300);

            tbViewer.setLabelProvider(new SummaryLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.PATTERN_MATCHING)
                || inputObject.getChartNamedType().equals(CompositeIndicator.SQL_PATTERN_MATCHING)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Label");
            column1.setWidth(200);
            TableColumn column2 = new TableColumn(table, SWT.NONE);
            column2.setText("%Match");
            column2.setWidth(75);
            TableColumn column3 = new TableColumn(table, SWT.NONE);
            column3.setText("%No Match");
            column3.setWidth(75);
            TableColumn column4 = new TableColumn(table, SWT.NONE);
            column4.setText("#Match");
            column4.setWidth(75);
            TableColumn column5 = new TableColumn(table, SWT.NONE);
            column5.setText("#No Match");
            column5.setWidth(75);

            tbViewer.setLabelProvider(new PatternLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else if (inputObject.getChartNamedType().equals(CompositeIndicator.MODE_INDICATOR)) {
            TableColumn column1 = new TableColumn(table, SWT.NONE);
            column1.setText("Mode");
            column1.setAlignment(SWT.CENTER);
            column1.setWidth(500);

            tbViewer.setLabelProvider(new ModeLabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        } else {

            tbViewer.setLabelProvider(new LabelProvider());
            tbViewer.setContentProvider(new CommonContenteProvider());
        }

        tbViewer.setInput(inputObject);

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class BaseChartTableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

        public Image getColumnImage(Object element, int columnIndex) {

            return null;
        }

        public String getColumnText(Object element, int columnIndex) {

            return "";
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

            ChartDataEntity entity = null;
            if (element instanceof PatternChartDataEntity) {
                entity = (PatternChartDataEntity) element;
            } else {
                entity = (ChartDataEntity) element;
            }
            String[] indicatorThreshold = IndicatorHelper.getIndicatorThreshold(entity.getIndicator());
            if (indicatorThreshold != null) {

                switch (columnIndex) {
                case 1:
                    String min = indicatorThreshold[0];
                    String max = indicatorThreshold[1];
                    String currentValue = getColumnText(entity, columnIndex);
                    if (min != null && max != null && currentValue != null) {
                        if (Double.valueOf(max) < Double.valueOf(currentValue)
                                || Double.valueOf(currentValue) < Double.valueOf(min)) {
                            return Display.getDefault().getSystemColor(SWT.COLOR_RED);
                        }
                        break;
                    }
                default:
                }
            }

            return null;
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class SimpleLabelProvider extends BaseChartTableLabelProvider {

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
                return "";
            }
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class FrequencyLabelProvider extends BaseChartTableLabelProvider {

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
                return "";
            }
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class SummaryLabelProvider extends BaseChartTableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            switch (columnIndex) {
            case 0:
                return entity.getLabel();
            case 1:
                return entity.getValue();

            default:
                return "";
            }
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class PatternLabelProvider extends BaseChartTableLabelProvider {

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
                return "";
            }
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class ModeLabelProvider extends BaseChartTableLabelProvider {

        @Override
        public String getColumnText(Object element, int columnIndex) {
            ChartDataEntity entity = (ChartDataEntity) element;

            return entity.getValue();
        }

    }

    /**
     * DOC zqin ChartTableFactory class global comment. Detailled comment
     */
    static class CommonContenteProvider implements IStructuredContentProvider {

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
