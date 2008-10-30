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

/**
 * This page show the comparisons information of column set.
 */
public class ColumnsComparisonMasterDetailsPage extends AbstractAnalysisMetadataPage {

    public ColumnsComparisonMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();
        form.setText("ColumnSet Comparision Analysis");
        this.metadataSection.setText("Analysis Metadata");
        this.metadataSection.setDescription("Set the properties of analysis.");
        createAnalyzedColumnSetsSection(form, topComp);
    }

    private void createAnalyzedColumnSetsSection(ScrolledForm form, Composite parentComp) {
        Section columnsComparisonSection = this.createSection(form, parentComp, "Analyzed Column Sets", false, null);
        Composite sectionClient = toolkit.createComposite(columnsComparisonSection);
        sectionClient.setLayout(new GridLayout());
        // sectionClient.setLayout(new GridLayout(2, true));
        // this.createSectionPart(form, sectionClient, "left Columns");
        // this.createSectionPart(form, sectionClient, "Right Columns");

        Button checkButton = new Button(sectionClient, SWT.CHECK);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.horizontalAlignment = SWT.CENTER;
        checkButton.setLayoutData(layoutData);
        checkButton.setText("Compute only number of A rows not in B");
        checkButton.setToolTipText("When unchecked, will compute also number of B rows not in A ");

        SashForm sForm = new SashForm(sectionClient, SWT.NULL);
        sForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite leftComp = toolkit.createComposite(sForm);
        leftComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        leftComp.setLayout(new GridLayout());
        this.createSectionPart(form, leftComp, "Left Columns", "Select columns for A Set");

        Composite rightComp = toolkit.createComposite(sForm);
        rightComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        rightComp.setLayout(new GridLayout());
        this.createSectionPart(form, rightComp, "Right Columns", "Select columns for B Set");

        columnsComparisonSection.setClient(sectionClient);
    }

    @SuppressWarnings("unchecked")
    private Section createSectionPart(ScrolledForm form, Composite parentComp, String title, String hyperlinkText) {
        Section columnSetElementSection = this.createSection(form, parentComp, title, true, null);
        // columnSetElementSection.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite sectionComp = toolkit.createComposite(columnSetElementSection);
        sectionComp.setLayout(new GridLayout());

        Hyperlink selectColumnBtn = toolkit.createHyperlink(sectionComp, hyperlinkText, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(selectColumnBtn);

        Composite columsComp = toolkit.createComposite(sectionComp, SWT.NULL);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, true).applyTo(columsComp);
        columsComp.setLayout(new GridLayout());

        final TableViewer columnsElementViewer = new TableViewer(columsComp);
        Table table = columnsElementViewer.getTable();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
        ((GridData) table.getLayoutData()).heightHint = 280;
        table.setHeaderVisible(true);
        table.setDragDetect(true);
        table.setToolTipText("you can reorder elements by drag&drop");
        final TableColumn columnHeader = new TableColumn(table, SWT.CENTER);
        columnHeader.setWidth(260);
        columnHeader.setAlignment(SWT.CENTER);
        // table.s
        ColumnsElementViewerProvider provider = new ColumnsElementViewerProvider();
        columnsElementViewer.setContentProvider(provider);
        columnsElementViewer.setLabelProvider(provider);
        columnsElementViewer.setInput(new ArrayList<TdColumn>());

        // tableViewer.setContentProvider(provider)

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
        selectColumnBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                openColumnsSelectionDialog(columnsElementViewer);
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
    public void openColumnsSelectionDialog(TableViewer columnsElementViewer) {
        // ColumnIndicator[] columnIndicator = treeViewer.getColumnIndicator();
        List<TdColumn> columnList = new ArrayList<TdColumn>();
        ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(null, "Column Selection", columnList, "Column Selection");
        if (dialog.open() == Window.OK) {
            Object[] columns = dialog.getResult();
            List<Object> columnSet = new ArrayList<Object>();
            for (Object obj : columns) {
                columnSet.add(obj);
            }
            columnsElementViewer.setInput(columnSet);
            if (columnSet.size() != 0) {
                String tableName = ColumnHelper.getColumnSetOwner((TdColumn) columnSet.get(0)).getName();

                columnsElementViewer.getTable().getColumn(0).setText("Element(s) from " + tableName);
            }
        }
    }

    @Override
    protected void saveAnalysis() throws DataprofilerCoreException {
        // TODO Auto-generated method stub

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
            if (element instanceof TdColumn) {
                return ((TdColumn) element).getName();
            }
            return PluginConstant.EMPTY_STRING;
        }

    }

}
