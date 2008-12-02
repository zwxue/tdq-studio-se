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

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.HideSeriesPanel;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dq.analysis.AnalysisHandler;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC xzhao class global comment. Detailled comment
 */
public class ColumnCorrelationNominalIntervalResultPage extends AbstractAnalysisResultPage implements PropertyChangeListener {

    private Composite resultComp;

    private Composite graphicsAndTableComp;

    private ColumnCorrelationNominalAndIntervalMasterPage masterPage;

    private ColumnSetMultiValueIndicator columnSetMultiIndicator;

    private Composite chartComposite;

    private Composite[] previewChartCompsites;

    /**
     * DOC zqin ColumnAnalysisResultPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ColumnCorrelationNominalIntervalResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        AnalysisEditor analysisEditor = (AnalysisEditor) editor;
        this.masterPage = (ColumnCorrelationNominalAndIntervalMasterPage) analysisEditor.getMasterPage();
        columnSetMultiIndicator = masterPage.getColumnSetMultiValueIndicator();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);

        graphicsAndTableComp = toolkit.createComposite(topComposite);
        graphicsAndTableComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        graphicsAndTableComp.setLayout(new GridLayout());
        createResultSection(graphicsAndTableComp);
        form.reflow(true);
    }

    @Override
    protected AnalysisHandler getColumnAnalysisHandler() {
        return this.masterPage.getColumnCorrelationAnalysisHandler();
    }

    protected void createResultSection(Composite parent) {
        Section graphicsAndTableSection = this.createSection(form, parent, "Analysis Result", false, null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(graphicsAndTableSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));
        // sectionClient.setLayout(new GridLayout(2, true));
        // this.createSectionPart(form, sectionClient, "left Columns");
        // this.createSectionPart(form, sectionClient, "Right Columns");

        // GridData layoutData = new GridData(GridData.FILL_BOTH);
        // layoutData.horizontalAlignment = SWT.CENTER;

        // SashForm sashForm = new SashForm(sectionClient, SWT.NULL);
        // sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        // if (countAvgNullIndicator == null) {
        // ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
        // countAvgNullIndicator = columnsetFactory.createCountAvgNullIndicator();
        // }
        Composite graphicsComp = toolkit.createComposite(sectionClient);
        GridData graphicsGridData = new GridData(GridData.FILL_BOTH);
        graphicsGridData.heightHint = 1000;
        graphicsGridData.widthHint = 1000;
        graphicsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        graphicsComp.setLayout(new GridLayout());
        if (columnSetMultiIndicator == null) {
            return;
        } else {
            this.createGraphicsSectionPart(sectionClient, columnSetMultiIndicator); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Composite tableSectionClient = toolkit.createComposite(graphicsAndTableSection);
        // tableSectionClient.setLayout(new GridLayout());
        // tableSectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite tableComp = toolkit.createComposite(sectionClient);
        tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        tableComp.setLayout(new GridLayout());
        if (columnSetMultiIndicator == null) {
            return;
        } else {
            this.createTableSectionPart(sectionClient, "Table Section", columnSetMultiIndicator); //$NON-NLS-1$ //$NON-NLS-2$
        }
        graphicsAndTableSection.setClient(sectionClient);
    }

    private Section createGraphicsSectionPart(Composite parentComp, ColumnSetMultiValueIndicator columnSetMultiValueIndicator) {
        Section section = createSection(form, parentComp, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.graphics"),
                true, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$ //$NON-NLS-2$
        section.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        chartComposite = toolkit.createComposite(sectionClient);
        chartComposite.setLayout(new GridLayout());
        chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        createBubbleOrGanttChart(form, chartComposite, columnSetMultiValueIndicator);
        section.setClient(sectionClient);
        return section;
    }

    private void createBubbleOrGanttChart(final ScrolledForm form, final Composite composite,
            final ColumnSetMultiValueIndicator columnSetMultiValueIndicator) {
        List<Composite> previewChartList = new ArrayList<Composite>();
        List<Column> bubOrGanttColumnList = new ArrayList<Column>();
        if (columnSetMultiValueIndicator instanceof CountAvgNullIndicator) {
            bubOrGanttColumnList = columnSetMultiValueIndicator.getNumericColumns();
        } else {
            bubOrGanttColumnList = columnSetMultiValueIndicator.getDateColumns();
        }
        for (Column column : bubOrGanttColumnList) {
            final TdColumn tdColumn = (TdColumn) column;

            final ExpandableComposite exComp = toolkit.createExpandableComposite(composite, ExpandableComposite.TREE_NODE
                    | ExpandableComposite.CLIENT_INDENT);
            exComp.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.column") + tdColumn.getName()); //$NON-NLS-1$
            exComp.setLayout(new GridLayout());
            exComp.setData(columnSetMultiValueIndicator);
            previewChartList.add(exComp);

            final Composite comp = toolkit.createComposite(exComp);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            if (tdColumn != null) {
                IRunnableWithProgress rwp = new IRunnableWithProgress() {

                    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        monitor.beginTask(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.createPreview")
                                + tdColumn.getName(), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                        Display.getDefault().asyncExec(new Runnable() {

                            public void run() {

                                // carete chart
                                HideSeriesPanel hideSeriesPanel = new HideSeriesPanel(columnSetMultiValueIndicator, tdColumn);
                                if (hideSeriesPanel != null) {
                                    Composite frameComp = toolkit.createComposite(comp, SWT.EMBEDDED);
                                    frameComp.setLayout(new GridLayout());
                                    GridData gd = new GridData();
                                    gd.heightHint = 500;
                                    gd.widthHint = 1000;
                                    frameComp.setLayoutData(gd);

                                    Frame frame = SWT_AWT.new_Frame(frameComp);
                                    frame.setLayout(new java.awt.BorderLayout());

                                    frame.add(hideSeriesPanel);
                                    frame.validate();
                                }
                            }

                        });

                        monitor.done();
                    }

                };

                try {
                    new ProgressMonitorDialog(getSite().getShell()).run(true, false, rwp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            exComp.addExpansionListener(new ExpansionAdapter() {

                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    getChartComposite().layout();
                    form.reflow(true);
                }

            });

            exComp.setExpanded(true);
            exComp.setClient(comp);
        }
        if (!previewChartList.isEmpty()) {
            this.previewChartCompsites = previewChartList.toArray(new Composite[previewChartList.size()]);
        }
    }

    private Section createTableSectionPart(Composite parentComp, String title,
            ColumnSetMultiValueIndicator columnSetMultiIndicator) {
        Section columnSetElementSection = this.createSection(form, parentComp, title, true, null);
        Composite sectionTableComp = toolkit.createComposite(columnSetElementSection);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).grab(true, true).applyTo(sectionTableComp);
        sectionTableComp.setLayout(new GridLayout());

        Composite columsComp = toolkit.createComposite(sectionTableComp, SWT.NULL);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).grab(true, true).applyTo(columsComp);
        columsComp.setLayout(new GridLayout());

        final TableViewer columnsElementViewer = new TableViewer(columsComp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Table table = columnsElementViewer.getTable();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
        ((GridData) table.getLayoutData()).heightHint = 280;
        List<String> tableColumnNames = columnSetMultiIndicator.getColumnHeaders();
        for (String tableColumnName : tableColumnNames) {
            // System.out.println(tableColumnName);
            final TableColumn columnHeader = new TableColumn(table, SWT.NONE);
            columnHeader.setText(tableColumnName);
        }
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableSectionViewerProvider provider = new TableSectionViewerProvider();
        List<Object[]> tableRows = columnSetMultiIndicator.getListRows();
        columnsElementViewer.setContentProvider(provider);
        columnsElementViewer.setLabelProvider(provider);
        columnsElementViewer.setInput(tableRows);
        for (int i = 0; i < tableColumnNames.size(); i++) {
            table.getColumn(i).pack();
        }
        columnSetElementSection.setClient(sectionTableComp);
        return columnSetElementSection;

    }

    /**
     * 
     * DOC zhaoxinyi ColumnCorrelationNominalIntervalResultPage class global comment. Detailled comment
     */
    class TableSectionViewerProvider implements IStructuredContentProvider, ITableLabelProvider {

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            List<Object> columnDataSet = (List<Object>) inputElement;
            return columnDataSet.toArray();
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public Image getImage(Object element) {
            if (element instanceof TdColumn) {
                return ImageLib.getImage(ImageLib.TD_COLUMN);
            }
            return null;
        }

        public void dispose() {
            // TODO Auto-generated method stub

        }

        public Image getColumnImage(Object element, int columnIndex) {
            // TODO Auto-generated method stub
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            for (int i = 0; i < ((Object[]) element).length; i++) {
                if (columnIndex == i) {
                    return String.valueOf(((Object[]) element)[i]);
                }
            }
            return null;
        }

        public void addListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }

        public boolean isLabelProperty(Object element, String property) {
            // TODO Auto-generated method stub
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    public void refresh(ColumnCorrelationNominalAndIntervalMasterPage masterPage) {
        this.masterPage = masterPage;
        this.summaryComp.dispose();
        this.graphicsAndTableComp.dispose();
        createFormContent(getManagedForm());
    }

    public Composite getChartComposite() {
        return chartComposite;
    }

    public Composite[] getPreviewChartCompsites() {
        return previewChartCompsites;
    }
}
