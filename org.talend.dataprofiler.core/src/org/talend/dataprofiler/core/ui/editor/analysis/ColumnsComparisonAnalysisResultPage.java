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

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dq.analysis.AnalysisHandler;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ColumnsComparisonAnalysisResultPage extends AbstractAnalysisResultPage {

    private ColumnsComparisonMasterDetailsPage masterPage;

    /**
     * DOC rli ColumnsComparisonAnalysisResultPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ColumnsComparisonAnalysisResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        AnalysisEditor analysisEditor = (AnalysisEditor) editor;
        this.masterPage = (ColumnsComparisonMasterDetailsPage) analysisEditor.getMasterPage();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        Composite analyzedColumnSetsComp = toolkit.createComposite(topComposite);
        analyzedColumnSetsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        analyzedColumnSetsComp.setLayout(new GridLayout());
        createAnalyzedColumnSetsSection(analyzedColumnSetsComp);
        Composite analysisResultsComp = toolkit.createComposite(topComposite);
        analysisResultsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        analysisResultsComp.setLayout(new GridLayout());
        createResultSection(analysisResultsComp);

        // resultComp = toolkit.createComposite(topComposite);
        // resultComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        // resultComp.setLayout(new GridLayout());
        // createResultSection(resultComp);

        // form.reflow(true);
    }

    private void createAnalyzedColumnSetsSection(Composite parent) {
        Section section = createSection(form, parent, "Analyzed Column Sets", true, null);
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        TableViewer elementsTableViewer = new TableViewer(sectionClient, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Table table = elementsTableViewer.getTable();
        GridDataFactory.fillDefaults().applyTo(table);
        ((GridData) table.getLayoutData()).heightHint = 280;
        ((GridData) table.getLayoutData()).widthHint = 510;
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setDragDetect(true);
        table.setToolTipText("You can reorder elements by drag&drop");
        final TableColumn columnHeader1 = new TableColumn(table, SWT.CENTER);
        columnHeader1.setWidth(260);
        columnHeader1.setAlignment(SWT.CENTER);
        final TableColumn columnHeader2 = new TableColumn(table, SWT.CENTER);
        columnHeader2.setWidth(260);
        columnHeader2.setAlignment(SWT.CENTER);
        Analysis analysis = this.masterPage.getAnalysisHandler().getAnalysis();
        EList<Indicator> indicators = analysis.getResults().getIndicators();

        RowMatchingIndicator rowMatchingIndicatorA = null;
        if (indicators.size() != 0) {
            rowMatchingIndicatorA = (RowMatchingIndicator) indicators.get(1);
            rowMatchingIndicatorA.getColumnSetA();
            rowMatchingIndicatorA.getColumnSetB();
            String columnName = rowMatchingIndicatorA.getColumnSetA().size() > 0 ? ColumnHelper.getColumnSetOwner(
                    rowMatchingIndicatorA.getColumnSetA().get(0)).getName() : PluginConstant.EMPTY_STRING;
            columnHeader1.setText(columnName.equals(PluginConstant.EMPTY_STRING) ? columnName : "Element(s) from " + columnName);
            columnName = rowMatchingIndicatorA.getColumnSetA().size() > 0 ? ColumnHelper.getColumnSetOwner(
                    rowMatchingIndicatorA.getColumnSetB().get(0)).getName() : PluginConstant.EMPTY_STRING;
            columnHeader2.setText(columnName.equals(PluginConstant.EMPTY_STRING) ? columnName : "Element(s) from " + columnName);
        }
        ColumnPairsViewerProvider provider = new ColumnPairsViewerProvider();
        elementsTableViewer.setContentProvider(provider);
        elementsTableViewer.setLabelProvider(provider);
        elementsTableViewer.setInput(rowMatchingIndicatorA);
        section.setClient(sectionClient);
    }

    @Override
    protected void createResultSection(Composite parent) {
        Section section = createSection(form, parent, "Analysis Results", true, null);
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout(2, false));
        sectionClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        section.setClient(sectionClient);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
        // TODO Auto-generated method stub

    }

    @Override
    protected AnalysisHandler getColumnAnalysisHandler() {
        return this.masterPage.getAnalysisHandler();
    }

    /**
     * The provider for displaying the pair of <Code>Column</Code>.
     */
    class ColumnPairsViewerProvider extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            if (element instanceof Column) {
                return ImageLib.getImage(ImageLib.TD_COLUMN);
            }
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String text = PluginConstant.EMPTY_STRING;
            if (element instanceof ColumnPair) {
                ColumnPair columnPair = (ColumnPair) element;
                switch (columnIndex) {
                case 0:
                    text = columnPair.getAOfPair().getName();
                    return text;
                case 1:
                    text = columnPair.getBOfPair().getName();
                    return text;
                default:
                    break;
                }
                return text;
            }

            return text;
        }

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof RowMatchingIndicator) {
                RowMatchingIndicator rowMatchingIndicator = (RowMatchingIndicator) inputElement;
                return ColumnPair.createColumnPairs(rowMatchingIndicator.getColumnSetA(), rowMatchingIndicator.getColumnSetB());
            }
            return null;
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }

    }

    /**
     * The pair of columns.
     */
    static class ColumnPair {

        private Column columnA;

        private Column columnB;

        public ColumnPair(Column columnA, Column columnB) {
            this.columnA = columnA;
            this.columnB = columnB;

        }

        public Column getAOfPair() {
            return columnA;
        }

        public Column getBOfPair() {
            return columnB;
        }

        public static ColumnPair[] createColumnPairs(List<Column> columnListA, List<Column> columnListB) {
            if (columnListA.size() != columnListB.size()) {
                return null;
            }
            ColumnPair[] columnPairs = new ColumnPair[columnListA.size()];
            for (int i = 0; i < columnListA.size(); i++) {
                columnPairs[i] = new ColumnPair(columnListA.get(i), columnListB.get(i));
            }
            return columnPairs;
        }
    }
}
