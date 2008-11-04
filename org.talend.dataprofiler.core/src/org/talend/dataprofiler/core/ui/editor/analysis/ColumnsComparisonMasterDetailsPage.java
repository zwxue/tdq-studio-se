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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.ui.dialog.ColumnsSelectionDialog;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;

/**
 * This page show the comparisons information of column set.
 */
public class ColumnsComparisonMasterDetailsPage extends AbstractAnalysisMetadataPage {

    private static Logger log = Logger.getLogger(ColumnsComparisonMasterDetailsPage.class);

    // private ColumnAnalysisHandler analysisHandler;

    private RowMatchingIndicator rowMatchingIndicatorA;

    private RowMatchingIndicator rowMatchingIndicatorB;

    private RowCountIndicator rowCountIndicator;

    private ScrolledForm form;

    private List<Column> columnListA;

    private List<Column> columnListB;

    public ColumnsComparisonMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    /**
     * DOC rli ColumnsComparisonAnalysisResultPage class global comment. Detailled comment
     */
    // class ColumnComparisonAnalysisHandler extends ColumnAnalysisHandler {
    //
    // public boolean addIndicator(TdColumn column, Indicator... indicators) {
    // if (!analysis.getContext().getAnalysedElements().contains(column)) {
    // analysis.getContext().getAnalysedElements().add(column);
    // }
    //
    // for (Indicator indicator : indicators) {
    // // store first level of indicators in result.
    // analysis.getResults().getIndicators().add(indicator);
    // // initializeIndicator(indicator, column);
    // }
    // DataManager connection = analysis.getContext().getConnection();
    // if (connection == null) {
    // // try to get one
    // log.error("Connection has not been set in analysis Context");
    // connection = DataProviderHelper.getTdDataProvider(column);
    // analysis.getContext().setConnection(connection);
    // }
    // return true;
    // }
    //
    // }
    public void initialize(FormEditor editor) {
        super.initialize(editor);
        Analysis analysis = (Analysis) this.currentModelElement;

        EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
        columnListA = new ArrayList<Column>();
        columnListB = new ArrayList<Column>();
        if (analysedElements.size() == 0) {
            ColumnsetFactory factory = ColumnsetFactory.eINSTANCE;
            IndicatorsFactory indicator = IndicatorsFactory.eINSTANCE;
            rowCountIndicator = indicator.createRowCountIndicator();
            rowMatchingIndicatorA = factory.createRowMatchingIndicator();

            rowMatchingIndicatorB = factory.createRowMatchingIndicator();
            Indicator[] currentIndicators = new Indicator[] { rowCountIndicator, rowMatchingIndicatorA, rowMatchingIndicatorB };
            setDefaultIndDef(currentIndicators);
        } else {
            EList<Indicator> indicators = analysis.getResults().getIndicators();
            rowCountIndicator = (RowCountIndicator) indicators.get(0);
            rowMatchingIndicatorA = (RowMatchingIndicator) indicators.get(1);
            columnListA.addAll(rowMatchingIndicatorA.getColumnSetA());
            rowMatchingIndicatorB = (RowMatchingIndicator) indicators.get(2);
            columnListB.addAll(rowMatchingIndicatorA.getColumnSetB());
            // for (Indicator indicator : indicators) {
            // IndicatorsSwitch<RowCountIndicator> rowCountIndSwitch = new IndicatorsSwitch<RowCountIndicator>() {
            //
            // public RowCountIndicator caseRowCountIndicator(RowCountIndicator object) {
            // return object;
            // }
            // };
            // ColumnsetSwitch<RowMatchingIndicator> rowMatchingIndSwitch = new ColumnsetSwitch<RowMatchingIndicator>()
            // {
            //
            // public RowMatchingIndicator caseRowMatchingIndicator(RowMatchingIndicator object) {
            // return object;
            // }
            // };
            //
            // // if(){
            // //
            // // }
            // }
        }

        // EList<ModelElement> analyzedColumns = analysisHandler.getAnalyzedColumns();
        // analysisHandler = new ColumnComparisonAnalysisHandler();
        // analysisHandler.setAnalysis();
    }

    /**
     * DOC rli Comment method "setDefaultIndDef".
     * 
     * @param indicator
     */
    private void setDefaultIndDef(Indicator[] indicators) {
        for (int i = 0; i < indicators.length; i++) {
            if (!DefinitionHandler.getInstance().setDefaultIndicatorDefinition(indicators[i])) {
                log.error("Could not set the definition of the given indicator : " + indicators[i].getName());
            }
        }
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        form = managedForm.getForm();
        form.setText("ColumnSet Comparision Analysis");
        this.metadataSection.setText("Analysis Metadata");
        this.metadataSection.setDescription("Set the properties of analysis.");
        createAnalyzedColumnSetsSection(topComp);
    }

    private void createAnalyzedColumnSetsSection(Composite parentComp) {
        Section columnsComparisonSection = this.createSection(form, parentComp, "Analyzed Column Sets", false, null);
        Composite sectionClient = toolkit.createComposite(columnsComparisonSection);
        sectionClient.setLayout(new GridLayout());
        // sectionClient.setLayout(new GridLayout(2, true));
        // this.createSectionPart(form, sectionClient, "left Columns");
        // this.createSectionPart(form, sectionClient, "Right Columns");

        Button checkComputeButton = new Button(sectionClient, SWT.CHECK);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.horizontalAlignment = SWT.CENTER;
        checkComputeButton.setLayoutData(layoutData);
        checkComputeButton.setText("Compute only number of A rows not in B");
        checkComputeButton.setToolTipText("When unchecked, will compute also number of B rows not in A ");

        SashForm sashForm = new SashForm(sectionClient, SWT.NULL);
        sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite leftComp = toolkit.createComposite(sashForm);
        leftComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        leftComp.setLayout(new GridLayout());
        this.createSectionPart(leftComp, columnListA, "Left Columns", "Select columns for A Set");

        Composite rightComp = toolkit.createComposite(sashForm);
        rightComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        rightComp.setLayout(new GridLayout());
        this.createSectionPart(rightComp, columnListB, "Right Columns", "Select columns for B Set");

        columnsComparisonSection.setClient(sectionClient);
    }

    @SuppressWarnings("unchecked")
    private Section createSectionPart(Composite parentComp, List<Column> columnList, String title, String hyperlinkText) {
        Section columnSetElementSection = this.createSection(form, parentComp, title, true, null);
        Composite sectionComp = toolkit.createComposite(columnSetElementSection);
        sectionComp.setLayout(new GridLayout());

        Hyperlink selectColumnBtn = toolkit.createHyperlink(sectionComp, hyperlinkText, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(selectColumnBtn);

        Composite columsComp = toolkit.createComposite(sectionComp, SWT.NULL);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, true).applyTo(columsComp);
        columsComp.setLayout(new GridLayout());

        final TableViewer columnsElementViewer = new TableViewer(columsComp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Table table = columnsElementViewer.getTable();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
        ((GridData) table.getLayoutData()).heightHint = 280;
        table.setHeaderVisible(true);
        table.setDragDetect(true);
        table.setToolTipText("You can reorder elements by drag&drop");
        final TableColumn columnHeader = new TableColumn(table, SWT.CENTER);
        columnHeader.setWidth(260);
        columnHeader.setAlignment(SWT.CENTER);
        if (columnList.size() > 0) {
            String tableName = ColumnHelper.getColumnSetOwner((TdColumn) columnList.get(0)).getName();
            columnHeader.setText("Element(s) from " + tableName);
        }
        ColumnsElementViewerProvider provider = new ColumnsElementViewerProvider();
        columnsElementViewer.setContentProvider(provider);
        columnsElementViewer.setLabelProvider(provider);
        columnsElementViewer.setInput(columnList);

        Composite buttonsComp = toolkit.createComposite(columsComp, SWT.NULL);
        buttonsComp.setLayout(new GridLayout(4, true));
        buttonsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Button delButton = new Button(buttonsComp, SWT.NULL);
        delButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        GridData buttonGridData = new GridData(GridData.FILL_BOTH);
        delButton.setLayoutData(buttonGridData);
        Button moveUpButton = new Button(buttonsComp, SWT.NULL);
        moveUpButton.setText("Move Up");
        moveUpButton.setLayoutData(buttonGridData);
        Button moveDownButton = new Button(buttonsComp, SWT.NULL);
        moveDownButton.setText("Move Down");
        moveDownButton.setLayoutData(buttonGridData);
        Button sortButton = new Button(buttonsComp, SWT.NULL);
        sortButton.setText("Sort");
        sortButton.setLayoutData(buttonGridData);
        // treeViewer.setDirty(false);
        final Button[] buttons = new Button[] { delButton, moveUpButton, moveDownButton, sortButton };
        Object input = columnsElementViewer.getInput();
        List<Object> columnSet = (List<Object>) input;
        this.enabledButtons(buttons, columnSet.size() != 0);
        final List<Column> columnsOfSectionPart = columnList;
        selectColumnBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                openColumnsSelectionDialog(columnsElementViewer, columnsOfSectionPart);
                Object input = columnsElementViewer.getInput();
                List<Object> columnSet = (List<Object>) input;
                enabledButtons(buttons, columnSet.size() != 0);
            }

        });

        columnSetElementSection.setClient(sectionComp);
        return columnSetElementSection;

    }

    private void enabledButtons(Button[] buttons, boolean enabled) {
        for (Button button : buttons) {
            button.setEnabled(enabled);
        }
    }

    /**
     * 
     */
    public void openColumnsSelectionDialog(TableViewer columnsElementViewer, List<Column> columnsOfSectionPart) {
        ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(null, "Column Selection", columnsOfSectionPart,
                "Column Selection");
        if (dialog.open() == Window.OK) {
            Object[] columns = dialog.getResult();
            List<Column> columnSet = new ArrayList<Column>();
            for (Object obj : columns) {
                columnSet.add((Column) obj);
            }
            columnsElementViewer.setInput(columnSet);
            columnsOfSectionPart.clear();
            columnsOfSectionPart.addAll(columnSet);
            this.setDirty(true);
            if (columnSet.size() != 0) {
                String tableName = ColumnHelper.getColumnSetOwner((TdColumn) columnSet.get(0)).getName();
                columnsElementViewer.getTable().getColumn(0).setText("Element(s) from " + tableName);
            }
        }
    }

    @Override
    protected void saveAnalysis() throws DataprofilerCoreException {
        List<ModelElement> analysedElements = new ArrayList<ModelElement>();
        setColumnAB(rowMatchingIndicatorA, columnListA, columnListB);
        setColumnAB(rowMatchingIndicatorB, columnListB, columnListA);
        for (int i = 0; i < columnListA.size(); i++) {
            analysedElements.add(columnListA.get(i));
        }
        for (int i = 0; i < columnListB.size(); i++) {
            analysedElements.add(columnListB.get(i));
        }
        AnalysisBuilder anaBuilder = new AnalysisBuilder();
        anaBuilder.setAnalysis(this.analysis);
        anaBuilder.addElementsToAnalyze(analysedElements, new Indicator[] { rowCountIndicator, rowMatchingIndicatorA,
                rowMatchingIndicatorB });
        ReturnCode save = AnaResourceFileHelper.getInstance().save(analysis);
        if (save.isOk()) {
            log.info("Success to save connection analysis:" + analysis.getFileName());
        }

    }

    /**
     * DOC rli Comment method "setColumnAB".
     */
    private void setColumnAB(RowMatchingIndicator rowMatchingIndicator, List<Column> columnsA, List<Column> columnsB) {
        rowMatchingIndicator.getColumnSetA().clear();
        rowMatchingIndicator.getColumnSetA().addAll(columnsA);
        rowMatchingIndicator.getColumnSetB().clear();
        rowMatchingIndicator.getColumnSetB().addAll(columnsB);
    }

    /**
     * The provider for ColumnsElementViewer.
     */
    class ColumnsElementViewerProvider extends LabelProvider implements IStructuredContentProvider {

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            List<Object> columnSet = (List<Object>) inputElement;
            return columnSet.toArray();
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public Image getImage(Object element) {
            if (element instanceof TdColumn) {
                return ImageLib.getImage(ImageLib.TD_COLUMN);
            }
            return null;
        }

        public String getText(Object element) {
            if (element instanceof Column) {
                return ((Column) element).getName();
            }
            return PluginConstant.EMPTY_STRING;
        }

    }

}
