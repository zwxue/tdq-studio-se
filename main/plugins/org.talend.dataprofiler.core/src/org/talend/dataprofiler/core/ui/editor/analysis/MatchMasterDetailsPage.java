// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.db.connection.DatabaseSQLExecutor;
import org.talend.cwm.db.connection.DelimitedFileSQLExecutor;
import org.talend.cwm.db.connection.ISQLExecutor;
import org.talend.cwm.db.connection.MDMSQLExecutor;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.MetadataAndColumnSelectionDialog;
import org.talend.dataprofiler.core.ui.events.EventEnum;
import org.talend.dataprofiler.core.ui.events.EventManager;
import org.talend.dataprofiler.core.ui.events.EventReceiver;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.analysis.connection.ConnectionWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.record.linkage.ui.composite.table.DataSampleTable;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.record.linkage.ui.section.BlockingKeySection;
import org.talend.dataquality.record.linkage.ui.section.MatchParameterSection;
import org.talend.dataquality.record.linkage.ui.section.MatchingKeySection;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dq.analysis.MatchAnalysisHandler;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.AnalysisRepNode;
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

    private EventReceiver refreshTableDataReceiver = null;

    private MatchAnalysisHandler analysisHandler;

    public MatchAnalysisHandler getAnalysisHandler() {
        return analysisHandler;
    }

    private Section dataSampleSection = null;

    private DataSampleTable sampleTable = null;

    private BlockingKeySection blockingKeySection = null;

    private MatchingKeySection matchingKeySection;

    private Button selectBlockKeyBtn = null;

    private Button selectMatchKeyBtn = null;

    private boolean isBlockingKeyButtonPushed = Boolean.FALSE;

    private boolean isMatchingKeyButtonPushed = Boolean.FALSE;

    private SashForm sForm;

    private IRepositoryNode[] selectedNodes;

    private boolean isMdm = false;

    private boolean isDelimitedFile = false;

    private Composite dataSampleparentComposite;

    private Composite dataTableComp;

    private Text rowLoadedText = null;

    private Label analyzeDataLabel;

    private String analyzeDataDefaultInfo;

    // Added yyin TDQ-8430 20140218
    private TDQAnalysisItem analysisItem;

    private EventReceiver refreshDataProiverLabel = null;

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
        analyzeDataDefaultInfo = DefaultMessagesImpl.getString("MatchMasterDetailsPage.DataDefultInfor"); //$NON-NLS-1$
    }

    @Override
    public void initialize(FormEditor editor) {
        super.initialize(editor);
        computeIndicators();
    }

    public void computeIndicators() {
        analysisHandler = new MatchAnalysisHandler();
        analysisHandler.setAnalysis((Analysis) this.currentModelElement);

    }

    // Added yyin TDQ-8430 20140218: because when change connection needs the item to save, so only move the refactor of
    // this method from trunk, (override the parent's method by the method on trunk parent's same method)
    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput instanceof AnalysisItemEditorInput) {
            AnalysisItemEditorInput fileEditorInput = (AnalysisItemEditorInput) editorInput;
            analysisItem = fileEditorInput.getTDQAnalysisItem();
        } else if (editorInput instanceof FileEditorInput) {
            FileEditorInput input = (FileEditorInput) editorInput;
            Property property = PropertyHelper.getProperty(input.getFile());
            analysisItem = (TDQAnalysisItem) property.getItem();
        }
        analysis = analysisItem.getAnalysis();
        currentModelElement = analysis;
        // initAnalysisRepNode
        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(analysis);
        if (recursiveFind != null && recursiveFind instanceof AnalysisRepNode) {
            this.analysisRepNode = (AnalysisRepNode) recursiveFind;
        }
        return analysis;
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

        createDataSection();

        createBlockingKeySection();

        createMatchingKeySection();

        createMatchParameterSection();

        // TDQ-7781: we must do this, this will recompute the layout and scroll bars
        form.reflow(true);
    }

    /**
     * DOC zshen Comment method "createMatchParameterSection".
     */
    private void createMatchParameterSection() {
        MatchParameterSection matchParameterSection = new MatchParameterSection(form, topComp, Section.TWISTIE
                | Section.TITLE_BAR | Section.EXPANDED, toolkit, analysis);
        matchParameterSection.addPropertyChangeListener(this);
        matchParameterSection.createParameterCom();
        registerSection(matchParameterSection.getSection());
    }

    /**
     * create Matching Key Section.
     * 
     */
    private void createMatchingKeySection() {
        matchingKeySection.createContent();
        registerSection(matchingKeySection.getSection());
    }

    /**
     * create Blocking Key Section.
     * 
     */
    private void createBlockingKeySection() {
        blockingKeySection.createContent();
        registerSection(blockingKeySection.getSection());
    }

    /**
     * create Data Section.
     */
    private void createDataSection() {
        dataSampleSection = createSection(form, topComp, DefaultMessagesImpl.getString("MatchMasterDetailsPage.DataSample"));//$NON-NLS-1$
        dataSampleparentComposite = toolkit.createComposite(dataSampleSection);
        GridLayout dataSampleLayout = new GridLayout(1, Boolean.TRUE);
        dataSampleparentComposite.setLayout(dataSampleLayout);

        dataSampleSection.setClient(dataSampleparentComposite);

        // create analyze data title composite
        createAnaDataLabelComposite(dataSampleparentComposite);

        // create Button composite
        createButtonComposite(dataSampleparentComposite);

        blockingKeySection = new BlockingKeySection(form, topComp, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED,
                toolkit, analysis);
        blockingKeySection.addPropertyChangeListener(this);

        matchingKeySection = new MatchingKeySection(form, topComp, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED,
                toolkit, analysis);
        matchingKeySection.addPropertyChangeListener(this);

        // create the data table
        createDataTableComposite(dataSampleparentComposite);

    }

    /**
     * create DataTable Composite.
     * 
     * @param dataparent
     */
    private void createDataTableComposite(Composite dataparent) {
        dataTableComp = toolkit.createComposite(dataparent);
        GridLayout dataTableLayout = new GridLayout(1, Boolean.TRUE);
        dataTableComp.setLayout(dataTableLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = 250;
        dataTableComp.setLayoutData(gridData);
        sampleTable = new DataSampleTable();
        // use handler to save selected columns
        ModelElement[] selectedColumns = this.analysisHandler.getSelectedColumns();

        if (selectedColumns != null && selectedColumns.length > 0) {
            // use ModelElement instead of node to get the data source type directly.
            if (selectedColumns[0] instanceof TdXmlElementType) {
                isMdm = true;
            } else if (selectedColumns[0] instanceof MetadataColumn && !(selectedColumns[0] instanceof TdColumn)) {
                isDelimitedFile = true;
            }

            createNatTable(new ArrayList<Object[]>());
        }
        sampleTable.addPropertyChangeListener(this);
    }

    /**
     * create Analysis Data Label Composite.
     * 
     * @param dataparent
     */
    private void createAnaDataLabelComposite(Composite dataparent) {
        Composite titleComposite = toolkit.createComposite(dataparent);
        GridLayout layout = new GridLayout(2, Boolean.TRUE);
        titleComposite.setLayout(layout);
        analyzeDataLabel = new Label(titleComposite, SWT.NONE);
        // register: refresh the dataprovider combobox when the name of the data provider is changed.
        refreshDataProiverLabel = new EventReceiver() {

            @Override
            public boolean handle(Object data) {
                RepositoryNode fColumnNode = analysisHandler.getAnalyzedColumns().size() > 0 ? RepositoryNodeHelper
                        .recursiveFind(analysisHandler.getAnalyzedColumns().get(0)) : null;
                if (fColumnNode != null) {
                    updateAnalyzeDataLabel(fColumnNode);
                }

                return true;
            }
        };
        EventManager.getInstance().register(getAnalysis(), EventEnum.DQ_MATCH_ANALYSIS_REFRESH_DATAPROVIDER_LABEL,
                refreshDataProiverLabel);
        if (analysisHandler.getAnalyzedColumns().size() > 0) {
            RepositoryNode node = RepositoryNodeHelper.recursiveFind(analysisHandler.getAnalyzedColumns().get(0));
            updateAnalyzeDataLabel(node);
        } else {
            analyzeDataLabel.setText(analyzeDataDefaultInfo);
        }

        // ADD msjian TDQ-8040 2013-11-8: Add a button "show in repository view"
        ImageHyperlink showInDQViewLink = toolkit.createImageHyperlink(titleComposite, SWT.NONE);
        showInDQViewLink.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.showDQElement")); //$NON-NLS-1$
        showInDQViewLink.setImage(ImageLib.getImage(ImageLib.APPLICATION_HOME));
        showInDQViewLink.addHyperlinkListener(new HyperlinkAdapter() {

            @Override
            public void linkActivated(HyperlinkEvent e) {
                List<IRepositoryNode> analysedElements = findAllSelectedRepositoryNode();
                if (analysedElements != null && analysedElements.size() > 0) {
                    DQRespositoryView dqview = CorePlugin.getDefault().findAndOpenRepositoryView();
                    // if DqRepository view is not openning we will not do anything
                    if (dqview == null) {
                        return;
                    }
                    try {
                        ModelElement column = RepositoryNodeHelper.getModelElementFromRepositoryNode(analysedElements.get(0));
                        ModelElement container = (ModelElement) (column.eContainer());
                        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(container);
                        if (recursiveFind == null) {
                            recursiveFind = RepositoryNodeHelper.createRepositoryNode(container);
                        }
                        dqview.showSelectedElements(recursiveFind);
                    } catch (Exception ex) {
                        log.error(ex, ex);
                    }
                }

            }
        });
        // TDQ-8040~
    }

    /**
     * create Button Composite.
     * 
     * @param dataparent
     */
    private void createButtonComposite(Composite dataparent) {
        Composite buttonComposite = toolkit.createComposite(dataparent);
        GridLayout buttonCompositeLayout = new GridLayout(3, Boolean.FALSE);
        buttonComposite.setLayout(buttonCompositeLayout);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(buttonComposite);

        // Data selection button composite
        createDataSelectionButtonComp(buttonComposite);

        // Data refresh, and row control composite
        createDataQueryButtonComp(buttonComposite);

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
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.SelectBlockingKeyButton"), SWT.TOGGLE);//$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(selectBlockKeyBtn);

        selectMatchKeyBtn = toolkit.createButton(keySelectionComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.SelectMatchingKeyButton"), SWT.TOGGLE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(selectMatchKeyBtn);

        addListenerForSelectKeyButton();
    }

    private void addListenerForSelectKeyButton() {
        selectBlockKeyBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
                // no need to implement
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
                // no need to implement
            }
        });

        selectMatchKeyBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
                // no need to implement
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
                // no need to implement
            }
        });
    }

    /**
     * Added TDQ-7954: After the "Create New connection" and "Run" action, the buttons should be reset.
     */
    private void resetSelectKeyButton() {
        // Because they are TOGGLE button, so, when re-initial them, should make them as not pushed.
        selectMatchKeyBtn.setSelection(Boolean.FALSE);
        selectBlockKeyBtn.setSelection(Boolean.FALSE);
        // ~
        selectBlockKeyBtn.setEnabled(Boolean.TRUE);
        selectMatchKeyBtn.setEnabled(Boolean.TRUE);
        isBlockingKeyButtonPushed = Boolean.FALSE;
        isMatchingKeyButtonPushed = Boolean.FALSE;
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
        for (ModelElement column : analysisHandler.getSelectedColumns()) {
            if (currentKeyColumn.contains(column.getName())) {
                sampleTable.changeColumnHeaderLabelColor(column.getName(), isMatchKey ? DataSampleTable.COLOR_RED
                        : DataSampleTable.COLOR_GREEN, isMatchKey ? DataSampleTable.MATCH_EKY : DataSampleTable.BLOCK_EKY);
            } else {
                sampleTable.changeColumnHeaderLabelColor(column.getName(), DataSampleTable.COLOR_BLACK,
                        isMatchKey ? DataSampleTable.MATCH_EKY : DataSampleTable.BLOCK_EKY);
            }
        }
        sampleTable.refresh();
    }

    private void setAllColumnColorToBlack() {
        for (ModelElement column : analysisHandler.getSelectedColumns()) {
            sampleTable.changeColumnHeaderLabelColor(column.getName(), DataSampleTable.COLOR_BLACK, PluginConstant.EMPTY_STRING);
        }
        sampleTable.refresh();
    }

    private void createDataSelectionButtonComp(Composite parent) {
        Composite dataSelectionComp = toolkit.createComposite(parent);
        GridLayout dataSelectionCompLayout = new GridLayout(2, Boolean.TRUE);
        dataSelectionComp.setLayout(dataSelectionCompLayout);

        Button createConnectionBtn = toolkit.createButton(dataSelectionComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.CreateConnectionButton"), SWT.NONE);//$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(createConnectionBtn);
        Button selectDataBtn = toolkit.createButton(dataSelectionComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.SelectDataButton"), SWT.NONE);//$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(selectDataBtn);

        createConnectionBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
                // no need to implement
            }

            public void mouseDown(MouseEvent e) {
                ConnectionWizard connectionWizard = new ConnectionWizard(PlatformUI.getWorkbench(), dataSampleparentComposite);
                connectionWizard.setForcePreviousAndNextButtons(true);
                WizardDialog dialog = new WizardDialog(null, connectionWizard);
                dialog.setPageSize(500, 200);
                dialog.open();
            }

            public void mouseUp(MouseEvent e) {
                // no need to implement
            }
        });
        selectDataBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
                // no need to implement
            }

            public void mouseDown(MouseEvent e) {
                // no need to give the current connection, the called method will find the current selected nodes auto
                openColumnsSelectionDialog(null);
            }

            public void mouseUp(MouseEvent e) {
                // no need to implement
            }
        });
        registerEvents(dataSampleparentComposite);
    }

    /**
     * create "Refresh Button", and the row control input.
     * 
     * @param buttonComposite
     */
    private void createDataQueryButtonComp(Composite parent) {
        Composite dataQueryComp = toolkit.createComposite(parent);
        GridLayout dataQueryCompLayout = new GridLayout(3, Boolean.FALSE);
        dataQueryComp.setLayout(dataQueryCompLayout);

        Button refreshDataBtn = toolkit.createButton(dataQueryComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.RefreshDataButton"), SWT.NONE);//$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(refreshDataBtn);

        refreshDataBtn.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
                // no need to implement
            }

            public void mouseDown(MouseEvent e) {
                if (isValidateRowCount()) {
                    refreshDataFromConnection(true);
                } else {
                    MessageDialog.openWarning(null, DefaultMessagesImpl.getString("MatchMasterDetailsPage.NotValidate"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("MatchMasterDetailsPage.LoadedRowCountError")); //$NON-NLS-1$
                }
            }

            public void mouseUp(MouseEvent e) {
                // no need to implement
            }
        });

        // create the input to control how many rows will be loaded.
        Label rowLoadedLabel = toolkit.createLabel(dataQueryComp,
                DefaultMessagesImpl.getString("MatchMasterDetailsPage.ControlRowsLabel"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(rowLoadedLabel);
        rowLoadedText = toolkit.createText(dataQueryComp, null, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(rowLoadedText);
        // fix the width of the text field
        GridData textData = new GridData();
        textData.widthHint = 100;
        rowLoadedText.setLayoutData(textData);
        rowLoadedText.setText(analysisHandler.getDefaultLoadedRowCount());
        rowLoadedText.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent e) {
                setDirty(true);
            }
        });
    }

    /**
     * check if the row loaded value is valid or not
     * 
     * @return
     */
    private boolean isValidateRowCount() {
        String text = rowLoadedText.getText();
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        try {
            int parseInt = Integer.parseInt(text);
            if (parseInt < 1) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * connect to db/file/mdm connection to fetch the newest data, and refresh the table to display.
     */
    protected void refreshDataFromConnection() {
        refreshDataFromConnection(false);
    }

    /**
     * connect to db/file/mdm connection to fetch the newest data, and refresh the table to display.
     */
    protected void refreshDataFromConnection(boolean refreshDataSample) {
        // execute the query to fetch the data,
        List<Object[]> listOfData = fetchDataForTable();

        blockingKeySection.setDataInput(listOfData);
        matchingKeySection.setDataInput(listOfData);

        if (refreshDataSample) {
            refreshTable(listOfData);
        }

        // after refresh the table, need to check if it is in select key mode, then need also to set the column color
        if (isBlockingKeyButtonPushed) {
            changeColumnColorByCurrentKeys(blockingKeySection.getSelectedColumnAsBlockKeys(), false);
        } else if (isMatchingKeyButtonPushed) {
            changeColumnColorByCurrentKeys(matchingKeySection.getCurrentMatchKeyColumn(), true);
        }

    }

    private void registerEvents(Composite dataSampleComposite) {
        // register: refresh the result page after running it from menu
        afterCreateConnectionReceiver = new EventReceiver() {

            @Override
            public boolean handle(Object data) {
                // check if the connection is unavailable, give a warning dialog to user without opening the columns
                // select dialog
                DataManager dataManager = (DataManager) data;
                if (ConnectionUtils.checkConnection(dataManager)) {
                    // need to give the new connection to the dialog to show only this new one in the dialog.
                    openColumnsSelectionDialog(dataManager);

                    // reset the select key buttons status
                    resetSelectKeyButton();
                }

                return true;
            }
        };
        EventManager.getInstance().register(dataSampleComposite, EventEnum.DQ_MATCH_ANALYSIS_AFTER_CREATE_CONNECTION,
                afterCreateConnectionReceiver);

        // register: refresh the data sample table to display the running result(with GID, SCORE, ...)
        refreshTableDataReceiver = new EventReceiver() {

            @Override
            public boolean handle(final Object data) {
                Display.getDefault().asyncExec(new Runnable() {

                    @SuppressWarnings("unchecked")
                    public void run() {
                        refreshTable((List<Object[]>) data);
                    }

                });
                return true;
            }
        };
        EventManager.getInstance().register(analysisHandler.getAnalysis(), EventEnum.DQ_MATCH_ANALYSIS_REFRESH_WITH_RESULT,
                refreshTableDataReceiver);
    }

    /**
     * open the column selection dialog.
     */
    public void openColumnsSelectionDialog(DataManager dataManager) {
        MetadataAndColumnSelectionDialog dialog = null;
        List<IRepositoryNode> oldSelectedColumns = findAllSelectedRepositoryNode();
        if (oldSelectedColumns == null || oldSelectedColumns.size() == 0) {
            clearAllKeys();
        }

        if (dataManager != null) {
            // only when "new connection" will give the new connection to this method
            dialog = new MetadataAndColumnSelectionDialog(
                    Display.getCurrent().getActiveShell(),
                    DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections"), dataManager, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections")); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            dialog = new MetadataAndColumnSelectionDialog(
                    Display.getCurrent().getActiveShell(),
                    DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections"), oldSelectedColumns, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (dialog.open() == Window.OK) {
            Object[] selectedResult = dialog.getResult();
            List<IRepositoryNode> reposList = RepNodeUtils.translateSelectedToStandardReposityoryNode(selectedResult);

            // selectedNodes = reposList.toArray(new RepositoryNode[reposList.size()]);
            if (selectedChanged(oldSelectedColumns, reposList)) {
                this.setDirty(true);
                // update all related keys in block and match section
                updateAllKeys(oldSelectedColumns);
                // update the analyzed data label with checked elements name.
                if (reposList != null && reposList.size() > 0) {
                    // String selectedElementNames = RepositoryNodeHelper.getAnalyzeDataNames(reposList.get(0));
                    updateAnalyzeDataLabel(reposList.get(0));
                } else {
                    analyzeDataLabel.setText(analyzeDataDefaultInfo);
                }
                refreshColumnAndData();
                // TDQ-8289 clear blocking key chart and match key chart when dataset is changed.
                matchingKeySection.clearChart();
                blockingKeySection.clearChart();
            }
        }
    }

    private void clearAllKeys() {
        selectedNodes = null;

        // clear all keys if the old selection is null
        analysisHandler.clearAllKeys();
        matchingKeySection.resolveAnalysis();
        blockingKeySection.resolveAnalysis();
        this.matchingKeySection.redrawnSubTableContent();
        this.blockingKeySection.redrawnSubTableContent();
    }

    /**
     * if some columns are deleted : remove the blocking/match key which used this column ; if some column still there :
     * update the index info in their keys; if some new columns added : do nothing
     * 
     * @param oldSelectedColumns
     */
    private void updateAllKeys(List<IRepositoryNode> oldSelectedColumns) {
        for (IRepositoryNode oldSelectNode : oldSelectedColumns) {
            int newPosition = positionInNewSelectColumns(oldSelectNode);
            if (newPosition > -1) {// update the position of the column
                this.matchingKeySection.updateColumnPosition(oldSelectNode.getLabel(), newPosition);
                this.blockingKeySection.updateColumnPosition(oldSelectNode.getLabel(), newPosition);
            } else { // delete all keys which used this column
                matchingKeySection.removeKeyFromAllTab(oldSelectNode.getLabel());
                blockingKeySection.removeBlockingKey(oldSelectNode.getLabel());
            }

        }

        // add new columns
        for (IRepositoryNode selectedOne : this.selectedNodes) {
            if (!oldSelectedColumns.contains(selectedOne)) {
                // the old doesnot contain the current, it need to be added to the columnMap
                int positionInNewSelectColumns = positionInNewSelectColumns(selectedOne);
                matchingKeySection.addColumn(selectedOne.getLabel(), positionInNewSelectColumns);
                blockingKeySection.addColumn(selectedOne.getLabel(), positionInNewSelectColumns);
            }
        }

        this.matchingKeySection.redrawnSubTableContent();
        this.blockingKeySection.redrawnSubTableContent();
    }

    /**
     * loop the new selected columns to check if the old one still Contained: find the new position.
     * 
     * @param oldSelectNode
     * @return
     */
    private int positionInNewSelectColumns(IRepositoryNode oldSelectNode) {
        int position = 0;
        for (IRepositoryNode newColumn : selectedNodes) {
            if (oldSelectNode.getLabel().equals(newColumn.getLabel())) {
                return position;
            }
            position++;
        }
        return -1;
    }

    /**
     * compare two array of objects, if them are same, return false, if any difference, return true. check for the
     * column name of data table: when user select a column named "GID", "GRP_SIZE", "BLOCK_KEY", if has, remove them
     * and give the user a warning
     * 
     * @param oldSelectedNodes : original selected columns
     * @param selectedResult : new selected columns
     * @return
     */
    private boolean selectedChanged(List<IRepositoryNode> oldSelectedNodes, List<IRepositoryNode> selectedResult) {
        boolean isChanged = false;

        List<IRepositoryNode> notRemovedNode = new ArrayList<IRepositoryNode>();
        for (IRepositoryNode newSelectNode : selectedResult) {
            if (!MatchRuleAnlaysisUtils.isEqualsToAdditionalColumn(newSelectNode.getLabel())) {
                notRemovedNode.add(newSelectNode);
            }
            if (!oldSelectedNodes.contains(newSelectNode)) {
                isChanged = true;
            }
        }
        if (!isChanged) {// if not changed, check if the size of two list equals, if not equals, means that the user
                         // remove some columns
            isChanged = (oldSelectedNodes.size() != selectedResult.size());
        }
        // if the selected columns have some same name with Additional ones.
        if (selectedResult.size() != notRemovedNode.size()) {
            selectedNodes = notRemovedNode.toArray(new RepositoryNode[notRemovedNode.size()]);
            // when the selected columns changed, need to popup the warning to tell the user
            MessageDialog.openWarning(null, DefaultMessagesImpl.getString("MatchMasterDetailsPage.warning"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("MatchMasterDetailsPage.RemoveSomeColumns")); //$NON-NLS-1$
        } else {
            // set the current selected nodes with new selected ones.
            selectedNodes = selectedResult.toArray(new RepositoryNode[selectedResult.size()]);
        }
        return isChanged;
    }

    private List<IRepositoryNode> findAllSelectedRepositoryNode() {
        List<IRepositoryNode> reposViewObjList = new ArrayList<IRepositoryNode>();
        // TDQ-9354 get selected columns from Anlaysis not form "selectedNodes".avoid to get an old instance when rename
        // the related connection then click "select data".
        if (analysisHandler.getSelectedColumns() != null) {
            // analysis has noe selected nodes
            for (ModelElement selectedColumn : analysisHandler.getSelectedColumns()) {
                RepositoryNode node = RepositoryNodeHelper.recursiveFind(selectedColumn);
                reposViewObjList.add(node);
            }
        }
        return reposViewObjList;
    }

    /**
     * need to be called after the user selects some columns need to fetch the data and refresh the table
     * 
     * @param repositoryNodes
     * 
     * @param nodes
     */
    public void setSelectedNodes(IRepositoryNode[] repositoryNodes) {
        this.selectedNodes = repositoryNodes;
        refreshColumnAndData();
    }

    private void refreshColumnAndData() {
        ModelElement[] modelElements = translateSelectedNodeIntoModelElement();
        this.analysisHandler.setSelectedColumns(modelElements);

        // MOD TDQ-8483 update analysed elements after selection of data.
        EList<ModelElement> analyzedElements = this.analysisHandler.getAnalysis().getContext().getAnalysedElements();
        analyzedElements.clear();
        analyzedElements.addAll(Arrays.asList(modelElements));
        updateAllColumnsToKeySection();
        refreshDataFromConnection(true);

        this.setDirty(Boolean.TRUE);
    }

    /**
     * Refresh the table with new data
     * 
     * @param listOfData
     */
    public void refreshTable(List<Object[]> listOfData) {
        // if come from the run, the color should back to black
        if (listOfData == null) {
            this.setAllColumnColorToBlack();
            return;
        }
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

        ScrolledComposite panel = new ScrolledComposite(dataTableComp, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(panel);
        panel.setLayout(new GridLayout(1, Boolean.FALSE));
        panel.setExpandHorizontal(true);
        panel.setExpandVertical(true);

        GridData layoutDataFillBoth = new GridData(GridData.FILL_BOTH);
        Composite subPanel = new Composite(panel, SWT.NONE);
        subPanel.setLayoutData(layoutDataFillBoth);
        subPanel.setLayout(new GridLayout(1, true));

        DataSampleTable.TControl tControl = sampleTable.createTable(subPanel, analysisHandler.getSelectedColumns(), listOfData);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tControl.getControl());

        // when refresh the data, the dataSampleSection's width is not 0
        if (dataSampleSection.getBounds().width > 0) {
            GridData gridData = new GridData(GridData.FILL_VERTICAL);
            // get the min value between the NatTable's width and dataSampleSection's width
            // if the NatTable's width larger than dataSampleSection's width, should minus 40 to let the vertical scroll
            // bar show
            int width = Math.min(tControl.getWidth(), dataSampleSection.getBounds().width - 40);
            // the width must langer than 0
            width = width > 0 ? width : dataSampleSection.getBounds().width - 40;
            gridData.widthHint = width;
            panel.setLayoutData(gridData);
        } else { // when open the editor, the dataSampleSection's width is 0, just set the layout fill both.
            panel.setLayoutData(layoutDataFillBoth);
        }

        panel.setContent(subPanel);
    }

    /**
     * this method will change the value of column list and adapt when section is emprty case. other case should use
     * updateAllKeys method
     */
    public void updateAllColumnsToKeySection() {
        setAllColumnsToKeySections();
        this.matchingKeySection.redrawnSubTableContent();
        this.blockingKeySection.redrawnSubTableContent();
    }

    /**
     * The key sections need to know all columns as: column_index, column_name
     */
    private void setAllColumnsToKeySections() {
        // only when open the analysis and match key is not empty
        if (analysisHandler.getSelectedColumns() == null || analysisHandler.getSelectedColumns().length < 1) {
            return;
        }

        Map<String, String> columnMap = new HashMap<String, String>();
        int index = 0;
        for (ModelElement column : analysisHandler.getSelectedColumns()) {
            columnMap.put(column.getName(), String.valueOf(index++));
        }
        matchingKeySection.setColumnNameInput(columnMap);
        blockingKeySection.setColumnNameInput(columnMap);
    }

    /**
     * when the user select one column, check: if the column is not selected before, add it(with color changed) else if
     * the column already be selected before, remove it(with color changed) Need to check: canSelectBlockingKey/
     * canSelectMatchingKey firstly,
     * 
     * Added: sort by the column. when selecting keys, the sort will not work; when sort, the select keys not work
     * 
     * @param rowPosition
     * @param columnPosition
     * @param columnName
     */
    private void handleColumnSelectionChange() {
        String columnName = sampleTable.getCurrentSelectedColumn();
        if (!isBlockingKeyButtonPushed && !isMatchingKeyButtonPushed) {
            // sort by column
            sampleTable.sortByColumn(Arrays.asList(analysisHandler.getSelectedColumns()));
            return;
        }
        sampleTable.resetSortSelection();
        if (columnName == null) {
            // means that the user selected column is the additional ones,no need to process it
            return;
        }
        if (isBlockingKeyButtonPushed) {
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
                sampleTable.changeColumnHeaderLabelColor(columnName, DataSampleTable.COLOR_BLACK, DataSampleTable.MATCH_EKY);
            } else {
                matchingKeySection.createMatchKeyFromCurrentMatchRule(columnName);
                sampleTable.changeColumnHeaderLabelColor(columnName, DataSampleTable.COLOR_RED, DataSampleTable.MATCH_EKY);
            }
        } catch (Exception e) {
            // popup to notify user that at least one match rule tab is needed.
            MessageDialog.openWarning(null, DefaultMessagesImpl.getString("MatchMasterDetailsPage.warning"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("MatchMasterDetailsPage.NoMatchRuleTabError")); //$NON-NLS-1$

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
            sampleTable.changeColumnHeaderLabelColor(columnName, DataSampleTable.COLOR_BLACK, DataSampleTable.BLOCK_EKY);
        } else {
            blockingKeySection.createBlockingKey(columnName);
            sampleTable.changeColumnHeaderLabelColor(columnName, DataSampleTable.COLOR_GREEN, DataSampleTable.BLOCK_EKY);
        }
    }

    /**
     * fetch the data according to the connection type(db,file,mdm)
     * 
     * @return
     */
    private List<Object[]> fetchDataForTable() {
        // TDQ-8267 when the file is changed, and the columns cleared, the analysis is unloaded
        if (analysisHandler.getAnalysis().eIsProxy()) {
            analysisHandler.setAnalysis((Analysis) EObjectHelper.resolveObject(analysisHandler.getAnalysis()));
            if (analysisHandler.getAnalysis().getContext().getConnection() == null) {
                // when all columns cleared, keys also need to be cleared.
                this.clearAllKeys();
            }
        }
        if (this.analysisHandler.getSelectedColumns() == null || analysisHandler.getSelectedColumns().length == 0) {
            return new ArrayList<Object[]>();
        }
        ISQLExecutor sqlExecutor = null;
        if (this.isMdm) {
            sqlExecutor = new MDMSQLExecutor();
        } else if (this.isDelimitedFile) {
            sqlExecutor = new DelimitedFileSQLExecutor();
        } else {// is database
            sqlExecutor = new DatabaseSQLExecutor();
        }
        try {
            // set limit
            sqlExecutor.setLimit(Integer.valueOf(rowLoadedText.getText()));
            return sqlExecutor.executeQuery(this.analysisHandler.getConnection(),
                    Arrays.asList(analysisHandler.getSelectedColumns()));
        } catch (SQLException e) {
            log.error(e, e);
            return new ArrayList<Object[]>();
        }
    }

    private void disposeDataTable() {
        if (dataTableComp != null && !dataTableComp.isDisposed()) {
            for (Control control : dataTableComp.getChildren()) {
                control.dispose();
            }
        }
    }

    private ModelElement[] translateSelectedNodeIntoModelElement() {
        // change the connection in analysis according to the user's selection
        if (selectedNodes != null && selectedNodes.length > 0) {
            changeConnectionOfAnalysisByNewSelectedNode(selectedNodes[0]);
        } else {
            changeConnectionOfAnalysisByNewSelectedNode(null);
        }

        return translateNodeIntoModelElement();
    }

    private ModelElement[] translateNodeIntoModelElement() {
        if (this.selectedNodes.length == 0) {
            return new ModelElement[0];
        }
        if (selectedNodes != null && selectedNodes.length != 0) {
            isMdm = RepNodeUtils.isMDM(selectedNodes[0]);
            isDelimitedFile = RepNodeUtils.isDelimitedFile(selectedNodes[0]);
            if (!(selectedNodes[0] instanceof DBColumnRepNode || isMdm || isDelimitedFile)) {
                selectedNodes = null;
                return null;
            }
        }
        List<ModelElement> modelElementList = new ArrayList<ModelElement>();
        for (IRepositoryNode repObj : selectedNodes) {
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
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            setDirty(Boolean.TRUE);
        } else
        // when the user switch the matchrule tab, receive the event, here should change the table's column color
        // according to current tab
        if (MatchAnalysisConstant.MATCH_RULE_TAB_SWITCH.equals(evt.getPropertyName())) {
            // find the current rule tab, and change the color of the table column
            if (isMatchingKeyButtonPushed) {
                changeColumnColorByCurrentKeys(matchingKeySection.getCurrentMatchKeyColumn(), true);
            } else if (this.isBlockingKeyButtonPushed) {
                changeColumnColorByCurrentKeys(blockingKeySection.getSelectedColumnAsBlockKeys(), false);
            }
        } else if (MatchAnalysisConstant.NEED_REFRESH_DATA.equals(evt.getPropertyName())) {
            refreshDataFromConnection();
        } else if (MatchAnalysisConstant.DATA_SAMPLE_TABLE_COLUMN_SELECTION.equals(evt.getPropertyName())) {
            handleColumnSelectionChange();
        } else if (MatchAnalysisConstant.NEED_REFRESH_DATA_SAMPLE_TABLE.equals(evt.getPropertyName())) {
            String minGrpSizeText = evt.getNewValue().toString();
            sampleTable.setMinGroupSize(Integer.valueOf(minGrpSizeText));
            matchingKeySection.refreshChart();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#canRun()
     */
    @Override
    protected ReturnCode canRun() {
        ReturnCode rc = new ReturnCode(Boolean.FALSE);

        // when the user didnot select any columns, can not run
        if (analysisHandler.getAnalyzedColumns() == null || analysisHandler.getAnalyzedColumns().size() < 1) {
            rc.setMessage(DefaultMessagesImpl.getString("MatchMasterDetailsPage.NoSelectColumn")); //$NON-NLS-1$
            return rc;
        }

        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        EList<MatchRule> matchRules = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules();
        if (matchRules.size() > 0) {
            MatchRule matchRule = matchRules.get(0);
            if (matchRule.getMatchKeys().size() > 0) {
                rc.setOk(Boolean.TRUE);
            } else {
                rc.setMessage(DefaultMessagesImpl.getString("MatchMasterDetailsPage.NoMatchKey")); //$NON-NLS-1$
            }
        } else {
            rc.setMessage(DefaultMessagesImpl.getString("MatchMasterDetailsPage.NoMatchKey")); //$NON-NLS-1$
        }

        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#canSave()
     */
    @Override
    public ReturnCode canSave() {
        if (this.isDirty) {
            ReturnCode checkResultStatus = blockingKeySection.checkResultStatus();
            if (checkResultStatus.isOk()) {
                checkResultStatus = matchingKeySection.checkResultStatus();
            }

            if (!checkResultStatus.isOk()) {
                return checkResultStatus;
            }
        }
        return super.canSave();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#refresh()
     */
    @Override
    public void refresh() {
        // no need for refresh in match analysis now.

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#saveAnalysis()
     */
    @Override
    protected void saveAnalysis() throws DataprofilerCoreException {
        if (this.isValidateRowCount()) {
            analysisHandler.changeDefaultRowLoaded(rowLoadedText.getText());
        } else {
            MessageDialog.openWarning(null, DefaultMessagesImpl.getString("MatchMasterDetailsPage.NotValidate"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("MatchMasterDetailsPage.LoadedRowCountError")); //$NON-NLS-1$
            return;
        }

        analysisHandler.saveSelectedAnalyzedElements();
        analysisHandler.updateAnaConnRelationship(analysisItem);

        ReturnCode saved = new ReturnCode(false);
        IEditorInput editorInput = this.getEditorInput();

        if (editorInput instanceof AnalysisItemEditorInput) {

            AnalysisItemEditorInput analysisInput = (AnalysisItemEditorInput) editorInput;

            TDQAnalysisItem tdqAnalysisItem = analysisInput.getTDQAnalysisItem();

            tdqAnalysisItem.getProperty().setDisplayName(analysisHandler.getName());
            tdqAnalysisItem.getProperty().setLabel(WorkspaceUtils.normalize(analysisHandler.getName()));
            this.nameText.setText(analysisHandler.getName());
            // save the default loaded row count
            // tdqAnalysisItem.getAnalysis().setParameters(analysisHandler.getParameters());

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
    private void changeConnectionOfAnalysisByNewSelectedNode(IRepositoryNode node) {
        if (node == null) {// clear the connection when the user select no columns
            analysisHandler.SetConnection(null);
        } else {
            Property property = node.getObject().getProperty();
            if (property != null && property.getItem() instanceof ConnectionItem) {
                Connection connection = ((ConnectionItem) property.getItem()).getConnection();
                analysisHandler.SetConnection(connection);
            }
        }
    }

    @Override
    public void dispose() {
        // unregister the event after create the connection
        EventManager.getInstance().unRegister(this.dataSampleparentComposite,
                EventEnum.DQ_MATCH_ANALYSIS_AFTER_CREATE_CONNECTION, afterCreateConnectionReceiver);
        EventManager.getInstance().unRegister(analysisHandler.getAnalysis(), EventEnum.DQ_MATCH_ANALYSIS_REFRESH_WITH_RESULT,
                refreshTableDataReceiver);
        EventManager.getInstance().unRegister(analysisHandler.getAnalysis(),
                EventEnum.DQ_MATCH_ANALYSIS_REFRESH_DATAPROVIDER_LABEL, refreshDataProiverLabel);

        this.getCurrentModelElement(this.getEditor()).eResource().unload();
        super.dispose();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage#fireRuningItemChanged(boolean)
     */
    @Override
    public void fireRuningItemChanged(boolean status) {
        if (status) {
            currentEditor.setActivePage(AnalysisEditor.RESULT_PAGE);
            currentEditor.getResultPage().refresh(this);
            // after running the analysis, reset the select keys button
            resetSelectKeyButton();
        }
    }

    public void importMatchRule(MatchRuleDefinition matchRule, boolean overwrite) {
        this.matchingKeySection.importMatchRule(matchRule, overwrite);
        this.blockingKeySection.importMatchRule(matchRule, overwrite);
        this.setDirty(true);
    }

    /**
     * 
     * save/update the selected elements names as TaggedValue.
     * 
     * @param iRepositoryNode
     */
    public void updateAnalyzeDataLabel(IRepositoryNode node) {
        analyzeDataLabel.setText(analyzeDataDefaultInfo + RepositoryNodeHelper.getAnalyzeDataNames(node));
    }

}
