// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.grid.layer.event.ColumnHeaderSelectionEvent;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.db.connection.DatabaseSQLExecutor;
import org.talend.cwm.db.connection.DelimitedFileSQLExecutor;
import org.talend.cwm.db.connection.ISQLExecutor;
import org.talend.cwm.db.connection.MDMSQLExecutor;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.MetadataAndColumnSelectionDialog;
import org.talend.dataprofiler.core.ui.events.EventEnum;
import org.talend.dataprofiler.core.ui.events.EventManager;
import org.talend.dataprofiler.core.ui.events.EventReceiver;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.wizard.analysis.connection.ConnectionWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.record.linkage.ui.composite.table.DataSampleTable;
import org.talend.dataquality.record.linkage.ui.section.BlockingKeySection;
import org.talend.dataquality.record.linkage.ui.section.MatchingKeySection;
import org.talend.dq.analysis.MatchAnalysisHandler;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * Detail Page of the match analysis
 */
public class MatchMasterDetailsPage extends AbstractAnalysisMetadataPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(MatchMasterDetailsPage.class);

    private EventReceiver afterCreateConnectionReceiver = null;

    private MatchAnalysisHandler analysisHandler;

    public MatchAnalysisHandler getAnalysisHandler() {
        return analysisHandler;
    }

    private Section dataSampleSection = null;

    private DataSampleTable sampleTable = null;

    private BlockingKeySection blockingKeySection = null;

    private MatchingKeySection matchingKeySection = null;

    private Button selectBlockKeyBtn = null;

    private Button selectMatchKeyBtn = null;

    private boolean isBlockingKeyButtonPushed = Boolean.FALSE;

    private boolean isMatchingKeyButtonPushed = Boolean.FALSE;

    private SashForm sForm;

    private ModelElement[] selectedColumns;

    private RepositoryNode[] selectedNodes;

    private boolean isMdm = false;

    private boolean isDelimitedFile = false;

    private Composite dataSampleparentComposite;

    /**
     * MatchMasterDetailsPage constructor.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public MatchMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        currentEditor = (AnalysisEditor) editor;
    }

    @Override
    public void initialize(FormEditor editor) {
        super.initialize(editor);
        computeIndicators();
    }

    public void computeIndicators() {
        analysisHandler = new MatchAnalysisHandler();
        analysisHandler.setAnalysis((Analysis) this.currentModelElement);

        EList<ModelElement> analyzedColumns = analysisHandler.getAnalyzedColumns();
        selectedColumns = new ModelElement[analyzedColumns.size()];
        int i = 0;
        for (ModelElement element : analyzedColumns) {
            selectedColumns[i++] = element;
        }
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        this.form = managedForm.getForm();
        Composite body = form.getBody();

        body.setLayout(new GridLayout());
        sForm = new SashForm(body, SWT.NULL);
        sForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        topComp = toolkit.createComposite(sForm);
        topComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        topComp.setLayout(new GridLayout());
        metadataSection = creatMetadataSection(form, topComp);
        form.setText(DefaultMessagesImpl.getString("MatchMasterDetailsPage.tableAna")); //$NON-NLS-1$
        metadataSection.setText(DefaultMessagesImpl.getString("TableMasterDetailsPage.analysisMeta")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("TableMasterDetailsPage.setPropOfAnalysis")); //$NON-NLS-1$

        createDataSampleSection(form, topComp);

        createBlockingKeySection(form, topComp);

        createMatchingKeySection(form, topComp);
    }

    /**
     * create Matching Key Section.
     * 
     * @param form
     * @param topComp
     */
    private void createMatchingKeySection(final ScrolledForm form, Composite topComp) {
        matchingKeySection.createContent();
        registerSection(matchingKeySection.getSection());
    }

    /**
     * create Blocking Key Section.
     * 
     * @param form
     * @param topComp
     */
    private void createBlockingKeySection(final ScrolledForm form, Composite topComp) {
        blockingKeySection.createContent();
        registerSection(blockingKeySection.getSection());
    }

    private void createDataSampleSection(ScrolledForm form, Composite topComp) {
        dataSampleSection = createSection(form, topComp, DefaultMessagesImpl.getString("MatchMasterDetailsPage.DataSample"));//$NON-NLS-1$
        dataSampleparentComposite = toolkit.createComposite(dataSampleSection);
        GridLayout dataSampleLayout = new GridLayout(1, Boolean.TRUE);
        dataSampleparentComposite.setLayout(dataSampleLayout);

        dataSampleSection.setClient(dataSampleparentComposite);
        // Button composite
        createButtonComposite(dataSampleparentComposite);

        blockingKeySection = new BlockingKeySection(form, topComp, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED,
                toolkit, analysis);
        matchingKeySection = new MatchingKeySection(form, topComp, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED,
                toolkit, analysis);

        // create the data table
        createDataTableComposite(dataSampleparentComposite);
    }

    /**
     * create DataTable Composite.
     * 
     * @param dataparent
     */
    private Composite dataTableComp;

    private void createDataTableComposite(Composite dataparent) {
        dataTableComp = toolkit.createComposite(dataparent);
        GridLayout dataTableLayout = new GridLayout(1, Boolean.TRUE);
        dataTableComp.setLayout(dataTableLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = 250;
        dataTableComp.setLayoutData(gridData);
        sampleTable = new DataSampleTable(this.isMdm, this.isDelimitedFile);

        if (selectedColumns != null && selectedColumns.length > 0) {
            RepositoryNode node = RepositoryNodeHelper.recursiveFind(selectedColumns[0]);
            isMdm = RepNodeUtils.isMDM(node);
            isDelimitedFile = RepNodeUtils.isDelimitedFile(node);

            createNatTable(new ArrayList<Object[]>());
        }

    }

    private void createButtonComposite(Composite dataparent) {
        Composite buttonComposite = toolkit.createComposite(dataparent);
        GridLayout buttonCompositeLayout = new GridLayout(2, Boolean.TRUE);
        buttonComposite.setLayout(buttonCompositeLayout);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(buttonComposite);
        // Data selection button composite
        createDataSelectionButtonComp(buttonComposite);

        // Key selection button composite
        createKeySelectionButtonComp(buttonComposite);
    }

    /**
     * create Key Selection Buttons.
     */
    private void createKeySelectionButtonComp(Composite parent) {
        Composite keySelectionComp = toolkit.createComposite(parent);
        GridLayout keySelectionCompLayout = new GridLayout(2, Boolean.TRUE);
        keySelectionComp.setLayout(keySelectionCompLayout);

        selectBlockKeyBtn = toolkit.createButton(keySelectionComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.SelectBlockingKeyButton"), SWT.BORDER | SWT.TOGGLE);//$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(selectBlockKeyBtn);
        selectMatchKeyBtn = toolkit.createButton(keySelectionComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.SelectMatchingKeyButton"), SWT.BORDER | SWT.TOGGLE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(selectMatchKeyBtn);

        addListenerForSelectKeyButton();
    }

    private void addListenerForSelectKeyButton() {
        selectBlockKeyBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
            }

            public void mouseDown(MouseEvent e) {
                // every time click the button, change its status
                selectMatchKeyBtn.setEnabled(isBlockingKeyButtonPushed);
                isBlockingKeyButtonPushed = !isBlockingKeyButtonPushed;

                // get the current block keys, to set the correct colors on table column
                if (isBlockingKeyButtonPushed) {
                    changeColumnColorByCurrentKeys(blockingKeySection.getSelectedColumnAsBlockKeys(), false);
                } else {
                    // when switch out of the select block key mode, should change all columns color to original black.
                    setAllColumnColorToBlack();
                }
            }

            public void mouseUp(MouseEvent e) {
            }

        });

        selectMatchKeyBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
            }

            public void mouseDown(MouseEvent e) {
                // every time click the button, change its status
                selectBlockKeyBtn.setEnabled(isMatchingKeyButtonPushed);
                isMatchingKeyButtonPushed = !isMatchingKeyButtonPushed;

                // get the current block keys, to set the correct colors on table column
                if (isMatchingKeyButtonPushed) {
                    changeColumnColorByCurrentKeys(matchingKeySection.getCurrentMatchKeyColumn(), true);
                } else {
                    // when switch out of the select match key mode, should change all columns color to original black.
                    setAllColumnColorToBlack();
                }
            }

            public void mouseUp(MouseEvent e) {
            }

        });
    }

    /**
     * change Column Color By Current selected Keys.
     * 
     * @param currentMatchKeyColumn
     */
    protected void changeColumnColorByCurrentKeys(List<String> currentKeyColumn, boolean isMatchKey) {
        if (currentKeyColumn == null || currentKeyColumn.size() < 1) {
            // set all columns' color to black
            setAllColumnColorToBlack();
            return;
        }
        // set all key's column into red/green columns
        // set all not selected columns into black color
        for (ModelElement column : selectedColumns) {
            if (currentKeyColumn.contains(column.getName())) {
                sampleTable.changeColumnHeaderLabelColor(column.getName(), isMatchKey ? GUIHelper.COLOR_RED
                        : GUIHelper.COLOR_GREEN);
            } else {
                sampleTable.changeColumnHeaderLabelColor(column.getName(), GUIHelper.COLOR_BLACK);
            }
        }
        sampleTable.refresh();
    }

    private void setAllColumnColorToBlack() {
        for (ModelElement column : selectedColumns) {
            sampleTable.changeColumnHeaderLabelColor(column.getName(), GUIHelper.COLOR_BLACK);
        }
        sampleTable.refresh();
    }

    private void createDataSelectionButtonComp(Composite parent) {
        Composite dataSelectionComp = toolkit.createComposite(parent);
        GridLayout dataSelectionCompLayout = new GridLayout(3, Boolean.TRUE);
        dataSelectionComp.setLayout(dataSelectionCompLayout);

        Button createConnectionBtn = toolkit.createButton(dataSelectionComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.CreateConnectionButton"), SWT.BORDER);//$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(createConnectionBtn);
        Button selectDataBtn = toolkit.createButton(dataSelectionComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.SelectDataButton"), SWT.BORDER);//$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(selectDataBtn);
        Button refreshDataBtn = toolkit.createButton(dataSelectionComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.RefreshDataButton"), SWT.BORDER);//$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(refreshDataBtn);

        createConnectionBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
            }

            public void mouseDown(MouseEvent e) {
                ConnectionWizard connectionWizard = new ConnectionWizard(PlatformUI.getWorkbench(), dataSampleparentComposite);
                connectionWizard.setForcePreviousAndNextButtons(true);
                WizardDialog dialog = new WizardDialog(null, connectionWizard);
                dialog.setPageSize(500, 200);
                dialog.open();
            }

            public void mouseUp(MouseEvent e) {
            }

        });
        selectDataBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
            }

            public void mouseDown(MouseEvent e) {
                openColumnsSelectionDialog(analysisHandler.getAnalysis().getContext().getConnection());
            }

            public void mouseUp(MouseEvent e) {
            }
        });

        refreshDataBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
            }

            public void mouseDown(MouseEvent e) {
                refreshDataFromConnection();
            }

            public void mouseUp(MouseEvent e) {
            }
        });
        registerCreateConnectionEvent(dataSampleparentComposite);
    }

    /**
     * connect to db/file/mdm connection to fetch the newest data, and refresh the table to display.
     */
    protected void refreshDataFromConnection() {
        // execute the query to fetch the data,
        List<Object[]> listOfData = fetchDataForTable();

        matchingKeySection.setDataInput(listOfData);
        blockingKeySection.setDataInput(listOfData);

        refreshTable(listOfData);

        // after refresh the table, need to check if it is in select key mode, then need also to set the column color
        if (isBlockingKeyButtonPushed) {
            changeColumnColorByCurrentKeys(blockingKeySection.getSelectedColumnAsBlockKeys(), false);
        } else if (isMatchingKeyButtonPushed) {
            changeColumnColorByCurrentKeys(matchingKeySection.getCurrentMatchKeyColumn(), true);
        }

    }

    private void registerCreateConnectionEvent(Composite dataSampleComposite) {
        // register: refresh the result page after running it from menu
        afterCreateConnectionReceiver = new EventReceiver() {

            @Override
            public boolean handle(Object data) {
                selectedNodes = null;
                selectedColumns = null;
                openColumnsSelectionDialog((DataManager) data);
                setActivePage();
                return true;
            }
        };
        EventManager.getInstance().register(dataSampleComposite, EventEnum.DQ_MATCH_ANALYSIS_AFTER_CREATE_CONNECTION,
                afterCreateConnectionReceiver);

    }

    /**
     * make the current page as the active one
     */
    protected void setActivePage() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().setActivePage((IWorkbenchPage) this);
    }

    /**
     * open the column selection dialog.
     */
    public void openColumnsSelectionDialog(DataManager dataManager) {
        RepositoryNode connNode = RepositoryNodeHelper.recursiveFindDatabaseConnection((DatabaseConnection) dataManager);
        List<IRepositoryNode> reposViewObjList = findAllSelectedRepositoryNode();

        MetadataAndColumnSelectionDialog dialog = new MetadataAndColumnSelectionDialog(null,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections"), reposViewObjList); //$NON-NLS-1$
        if (dialog.open() == Window.OK) {
            Object[] selectedResult = dialog.getResult();
            setSelectedNodes(selectedResult);
        }
    }

    private List<IRepositoryNode> findAllSelectedRepositoryNode() {
        List<IRepositoryNode> reposViewObjList = new ArrayList<IRepositoryNode>();
        if (selectedNodes != null) {
            for (RepositoryNode node : selectedNodes) {
                reposViewObjList.add(node);
            }
        } else if (selectedColumns != null) {// find the related nodes of the selected columns, when the first opened
                                             // analysis has noe selected nodes
            for (ModelElement selectedColumn : this.selectedColumns) {
                RepositoryNode node = RepositoryNodeHelper.recursiveFind(selectedColumn);
                reposViewObjList.add(node);
            }
        }
        return reposViewObjList;
    }

    /**
     * need to be called after the user selects some columns need to fetch the data and refresh the table
     * 
     * @param nodes
     */
    public void setSelectedNodes(Object[] nodes) {
        selectedColumns = translateSelectedNodeIntoModelElement(nodes);
        this.analysisHandler.setColumnsToAnalyze(Arrays.asList(selectedColumns));

        refreshDataFromConnection();

        this.setDirty(Boolean.TRUE);
    }

    /**
     * Refresh the table with new data
     * 
     * @param listOfData
     */
    private void refreshTable(List<Object[]> listOfData) {
        // dispose the data table composite
        disposeDataTable();
        // create the data table composite
        createNatTable(listOfData);

        dataTableComp.getParent().layout();
        dataTableComp.layout();
    }

    // no need to fetch the data after select data, only do fetch when "refresh" or run analysis
    private void createNatTable(List<Object[]> listOfData) {
        setAllColumnsToKeySections();

        Control natTable = sampleTable.createTable(dataTableComp, selectedColumns, listOfData);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

        addCustomSelectionBehaviour((NatTable) natTable);
    }

    /**
     * The key sections need to know all columns as: column_index, column_name
     */
    private void setAllColumnsToKeySections() {
        // only when open the analysis and match key is not empty
        if (selectedColumns == null || selectedColumns.length < 1) {
            return;
        }

        Map<String, String> columnMap = new HashMap<String, String>();
        int index = 0;
        for (ModelElement column : selectedColumns) {
            columnMap.put(column.getName(), String.valueOf(index++));
        }
        matchingKeySection.setColumnNameInput(columnMap);
        blockingKeySection.setColumnNameInput(columnMap);
    }

    private void addCustomSelectionBehaviour(NatTable nattable) {
        nattable.addLayerListener(new ILayerListener() {

            public void handleLayerEvent(ILayerEvent event) {
                if ((event instanceof ColumnHeaderSelectionEvent)) {
                    ColumnHeaderSelectionEvent columnEvent = (ColumnHeaderSelectionEvent) event;
                    Collection<Range> ranges = columnEvent.getColumnPositionRanges();
                    if (ranges.size() > 0) {
                        Range range = ranges.iterator().next();
                        handleColumnSelectionChange(range.start);
                    }
                }
            }
        });
    }

    /**
     * when the user select one column, check: if the column is not selected before, add it(with color changed) else if
     * the column already be selected before, remove it(with color changed) Need to check: canSelectBlockingKey/
     * canSelectMatchingKey firstly,
     * 
     * @param rowPosition
     * @param columnPosition
     * @param columnName
     */
    private void handleColumnSelectionChange(int index) {
        String columnName = sampleTable.getUserColumnNameByPosition(index);
        if (columnName == null) {
            // means that the user selected column is the additional ones,no need to process it
            return;
        }
        if (!isBlockingKeyButtonPushed && !isMatchingKeyButtonPushed) {
            return;
        } else if (isBlockingKeyButtonPushed) {
            handleBlockKeySelection(columnName);
        } else if (isMatchingKeyButtonPushed) {
            handleMatchKeySelection(columnName);
        }
        this.setDirty(Boolean.TRUE);
    }

    /**
     * handle the add/delete column for the Match Key Selection.
     * 
     * @param columnName
     */
    private void handleMatchKeySelection(String columnName) {
        try {
            Boolean isAdded = matchingKeySection.isKeyDefinitionAdded(columnName);
            if (isAdded) {
                matchingKeySection.removeMatchKeyFromCurrentMatchRule(columnName);
                sampleTable.changeColumnHeaderLabelColor(columnName, GUIHelper.COLOR_BLACK);
            } else {
                matchingKeySection.createMatchKeyFromCurrentMatchRule(columnName);
                sampleTable.changeColumnHeaderLabelColor(columnName, GUIHelper.COLOR_RED);
            }
        } catch (Exception e) {
            // TODO yyin popup to notify user that at least one match rule tab is needed.
        }
    }

    /**
     * handle the add/delete column for the BlockKey Selection.
     * 
     * @param columnName
     */
    private void handleBlockKeySelection(String columnName) {
        // check if the column is added or not:
        Boolean isAdded = Boolean.FALSE;
        try {
            isAdded = this.blockingKeySection.isKeyDefinitionAdded(columnName);
        } catch (Exception e) {
            // Normally it should have no exception.
            log.error(e.getMessage());
        }

        if (isAdded) {
            blockingKeySection.removeBlockingKey(columnName);
            sampleTable.changeColumnHeaderLabelColor(columnName, GUIHelper.COLOR_BLACK);
        } else {
            blockingKeySection.createBlockingKey(columnName);
            sampleTable.changeColumnHeaderLabelColor(columnName, GUIHelper.COLOR_GREEN);
        }
    }

    /**
     * fetch the data according to the connection type(db,file,mdm)
     * 
     * @return
     */
    private List<Object[]> fetchDataForTable() {
        ISQLExecutor sqlExecutor = null;
        if (this.isMdm) {
            sqlExecutor = new MDMSQLExecutor();
        } else if (this.isDelimitedFile) {
            sqlExecutor = new DelimitedFileSQLExecutor();
        } else {// is database
            sqlExecutor = new DatabaseSQLExecutor();
        }
        try {
            return sqlExecutor.executeQuery(this.analysisHandler.getAnalysis());
        } catch (SQLException e) {
            log.error(e, e);
            return null;
        }
    }

    private void disposeDataTable() {
        if (dataTableComp != null && !dataTableComp.isDisposed()) {
            for (Control control : dataTableComp.getChildren()) {
                control.dispose();
            }
        }
    }

    private ModelElement[] translateSelectedNodeIntoModelElement(Object[] objs) {
        List<IRepositoryNode> reposList = RepNodeUtils.translateSelectedToStandardReposityoryNode(objs);
        selectedNodes = reposList.toArray(new RepositoryNode[reposList.size()]);
        // change the connection in analysis according to the user's selection
        changeConnectionOfAnalysisByNewSelectedNode(selectedNodes[0]);

        return translateNodeIntoModelElement(objs, reposList);
    }

    private ModelElement[] translateNodeIntoModelElement(Object[] objs, List<IRepositoryNode> reposList) {
        if (reposList.size() == 0) {
            return new ModelElement[0];
        }
        if (objs != null && objs.length != 0) {
            isMdm = RepNodeUtils.isMDM(objs[0]);
            isDelimitedFile = RepNodeUtils.isDelimitedFile(objs[0]);
            if (!(reposList.get(0) instanceof DBColumnRepNode || isMdm || isDelimitedFile)) {
                return null;
            }
        }
        List<ModelElement> modelElementList = new ArrayList<ModelElement>();
        for (IRepositoryNode repObj : reposList) {
            if (isMdm) {
                modelElementList.add(((MetadataXmlElementTypeRepositoryObject) repObj.getObject()).getModelElement());
            } else {// delimited file or database
                modelElementList.add(((MetadataColumnRepositoryObject) repObj.getObject()).getTdColumn());
            }
        }
        return modelElementList.toArray(new ModelElement[modelElementList.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#canRun()
     */
    @Override
    protected ReturnCode canRun() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#refresh()
     */
    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#saveAnalysis()
     */
    @Override
    protected void saveAnalysis() throws DataprofilerCoreException {
        this.updateAnalysisClientDependency();

        if (selectedColumns != null && selectedColumns.length != 0) {

            // add the user selected columns into the analysis
            for (ModelElement selectedColumn : selectedColumns) {
                analysisHandler.addColumnToAnalyze(selectedColumn);
            }

        }
        ReturnCode saved = new ReturnCode(false);
        IEditorInput editorInput = this.getEditorInput();

        if (editorInput instanceof AnalysisItemEditorInput) {

            AnalysisItemEditorInput analysisInput = (AnalysisItemEditorInput) editorInput;

            TDQAnalysisItem tdqAnalysisItem = analysisInput.getTDQAnalysisItem();

            tdqAnalysisItem.getProperty().setDisplayName(analysisHandler.getName());
            tdqAnalysisItem.getProperty().setLabel(WorkspaceUtils.normalize(analysisHandler.getName()));
            this.nameText.setText(analysisHandler.getName());

            saved = ElementWriterFactory.getInstance().createAnalysisWrite().save(tdqAnalysisItem, true);
        }
        logSaved(saved);
    }

    /**
     * change the connection of the analysis after the user change the selected columns
     * 
     * @param modelElement
     * @return
     */
    private void changeConnectionOfAnalysisByNewSelectedNode(RepositoryNode node) {
        Property property = node.getObject().getProperty();
        if (property != null && property.getItem() instanceof ConnectionItem) {
            Connection connection = ((ConnectionItem) property.getItem()).getConnection();
            analysisHandler.getAnalysis().getContext().setConnection(connection);
        }
    }

    @Override
    public void dispose() {
        // unregister the event after create the connection
        EventManager.getInstance().unRegister(this.dataSampleparentComposite,
                EventEnum.DQ_MATCH_ANALYSIS_AFTER_CREATE_CONNECTION, afterCreateConnectionReceiver);

    }
}
