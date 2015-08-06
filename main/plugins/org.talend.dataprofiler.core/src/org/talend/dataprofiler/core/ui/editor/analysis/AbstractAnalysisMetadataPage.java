// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.IRuningStatusListener;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.composite.AbstractColumnDropTree;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.analysis.AnalysisHandler;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DFConnectionRepNode;
import org.talend.dq.nodes.MDMConnectionRepNode;
import org.talend.dq.writer.AElementPersistance;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractAnalysisMetadataPage extends AbstractMetadataFormPage implements IRuningStatusListener {

    private static Logger log = Logger.getLogger(AbstractAnalysisMetadataPage.class);

    protected Analysis analysis;

    protected AnalysisRepNode analysisRepNode;

    protected Section analysisParamSection;

    public AnalysisRepNode getAnalysisRepNode() {
        return this.analysisRepNode;
    }

    private void initAnalysisRepNode(Analysis analysis) {
        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(analysis);
        if (recursiveFind != null && recursiveFind instanceof AnalysisRepNode) {
            this.analysisRepNode = (AnalysisRepNode) recursiveFind;
        }
    }

    protected AnalysisEditor currentEditor = null;

    // MOD yyin 201204 TDQ-4977, change to TableCombo type to show the connection type.
    protected TableCombo connCombo;

    protected Text textConnVersion;

    protected Label labelConnDeleted;

    public TableCombo getConnCombo() {
        return connCombo;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public AbstractAnalysisMetadataPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        currentEditor = (AnalysisEditor) editor;
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        // MOD klliu 2010-12-10
        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput instanceof AnalysisItemEditorInput) {
            AnalysisItemEditorInput fileEditorInput = (AnalysisItemEditorInput) editorInput;
            TDQAnalysisItem tdqAnalysisItem = fileEditorInput.getTDQAnalysisItem();
            analysis = tdqAnalysisItem.getAnalysis();
        } else if (editorInput instanceof FileEditorInput) {
            FileEditorInput input = (FileEditorInput) editorInput;
            currentModelElement = AnaResourceFileHelper.getInstance().findAnalysis(input.getFile());
            analysis = (Analysis) currentModelElement;
        }
        initAnalysisRepNode(analysis);
        return analysis;
    }

    protected IRepositoryNode getCurrentRepNodeOnUI() {
        // MOD klliu 2010-12-10
        IRepositoryNode connectionNode = null;
        IEditorInput editorInput = getEditor().getEditorInput();
        if (editorInput instanceof AnalysisItemEditorInput) {
            AnalysisItemEditorInput fileEditorInput = (AnalysisItemEditorInput) editorInput;
            connectionNode = fileEditorInput.getConnectionNode();
        }
        return connectionNode;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        ReturnCode rc = canSave();
        if (!rc.isOk()) {
            // MOD yyi 2012-02-29 TDQ-3605 Pop an error if rc is not ok.
            MessageDialogWithToggle.openError(null,
                    DefaultMessagesImpl.getString("AbstractAnalysisMetadataPage.SaveAnalysis"), rc.getMessage()); //$NON-NLS-1$
            return;
        } else if (!checkWhithspace()) {
            MessageDialogWithToggle
                    .openError(
                            null,
                            DefaultMessagesImpl.getString("AbstractAnalysisMetadataPage.SaveAnalysis"), DefaultMessagesImpl.getString("AbstractMetadataFormPage.whitespace")); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            super.doSave(monitor);
            try {
                saveAnalysis();
                this.isDirty = false;
                // MOD qiongli bug 0012766,2010-5-31:After change to another connection
                // which has same columns with before,the editor should not
                // dirty.
                ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
                this.updateAnalysisConnectionVersionInfo();
            } catch (DataprofilerCoreException e) {
                MessageDialogWithToggle.openError(null,
                        DefaultMessagesImpl.getString("AbstractAnalysisMetadataPage.SaveAnalysis"), e.getMessage()); //$NON-NLS-1$ 
                ExceptionHandler.process(e, Level.ERROR);
            }
        }
    }

    public ScrolledForm getScrolledForm() {
        return null;
    }

    protected abstract ReturnCode canRun();

    public abstract void refresh();

    protected abstract void saveAnalysis() throws DataprofilerCoreException;

    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    public void fireRuningItemChanged(boolean status) {
        currentEditor.setRunActionButtonState(status);
        currentEditor.setRefreshResultPage(status);
        if (status) {
            refresh();
        }
    }

    /**
     * DOC bZhou Comment method "switchToResultPage".
     */
    protected void switchToResultPage() {
        IFormPage resultPage = currentEditor.findPage(AnalysisEditor.RESULT_PAGE);
        if (resultPage != null && !resultPage.isActive()) {
            IFormPage activePageInstance = currentEditor.getActivePageInstance();
            if (activePageInstance.canLeaveThePage()) {
                currentEditor.setActivePage(AnalysisEditor.RESULT_PAGE);
            }
        }
    }

    /**
     * MOD mzhao 2009-06-17 feature 5887.
     * 
     * @param parentComp
     */
    public void createConnBindWidget(Composite parentComp) {
        // ~ MOD mzhao 2009-05-05,Bug 6587.
        Composite labelButtonClient = toolkit.createComposite(parentComp);
        GridLayout labelButtonClientLayout = new GridLayout();
        labelButtonClientLayout.numColumns = 4;
        labelButtonClient.setLayout(labelButtonClientLayout);

        toolkit.createLabel(labelButtonClient, DefaultMessagesImpl.getString("AbstractMetadataFormPage.connBind")); //$NON-NLS-1$

        // MOD yyin 201204 TDQ-4977, change to TableCombo type to show the connection type.
        // create group
        // Group group = new Group(labelButtonClient, SWT.NONE);
        // group.setLayout(new GridLayout(2, false));
        // group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        // group.setText(" ");

        // create label
        // Label label = new Label(group, SWT.NONE);
        // label.setText("Metadata Name & Type:");

        // create TableCombo
        connCombo = new TableCombo(labelButtonClient, SWT.BORDER | SWT.READ_ONLY);
        connCombo.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT));

        // tell the TableCombo that I want 2 blank columns auto sized.
        connCombo.defineColumns(2);

        // set which column will be used for the selected item.
        connCombo.setDisplayColumnIndex(0);
        connCombo.setSize(SWT.DEFAULT, SWT.DEFAULT);

        // add listener
        // connCombo = new TableCombo(labelButtonClient, SWT.BORDER);
        connCombo.setEditable(false);
        connCombo.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                updateAnalysisConnectionVersionInfo();
            }

            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
        // ADD msjian TDQ-5184 2012-4-8: set the connCombo background color as system set color
        connCombo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        // TDQ-5184~
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(labelButtonClient);
        reloadDataproviderAndFillConnCombo();
        // ~
        createConnVersionText(labelButtonClient);
        createConnDeletedLabel(labelButtonClient);
    }

    /**
     * create the version Text of the connection.
     * 
     * @param parentComp
     */
    private void createConnVersionText(Composite parentComp) {
        textConnVersion = toolkit.createText(parentComp, PluginConstant.EMPTY_STRING, SWT.FLAT);
        textConnVersion.setEditable(false);
        updateAnalysisConnectionVersionInfo();
    }

    /**
     * update the version info of the analysis.
     */
    public void updateAnalysisConnectionVersionInfo() {
        if (this.textConnVersion != null) {
            String strConnVersion = DefaultMessagesImpl.getString("AbstractMetadataFormPage.connVersion") //$NON-NLS-1$
                    + getConnectionVersion();
            textConnVersion.setText(strConnVersion);
        }
    }

    /**
     * get the database's version of the Analysis.
     * 
     * @return
     */
    public String getConnectionVersion() {
        String version = null;
        if (this.analysis != null) {
            DataManager dm = this.analysis.getContext().getConnection();
            if (dm != null) {
                if (dm instanceof Connection) {
                    Connection connection = (Connection) dm;
                    version = connection.getVersion();
                    if (version == null) {
                        version = initConnectionVersion(connection);
                    }
                }
            }
        }
        return version == null ? getConnectionVersionDefault() : version;
    }

    /**
     * get the default connection's version of this analysis.
     * 
     * @returnd efault connection's version
     */
    private String getConnectionVersionDefault() {
        String version = "Unknown"; //$NON-NLS-1$
        Object data = connCombo.getData(connCombo.getSelectionIndex() + PluginConstant.EMPTY_STRING);
        if (data != null) {
            if (data instanceof DBConnectionRepNode) {
                DBConnectionRepNode dbConnRepNode = (DBConnectionRepNode) data;
                if (dbConnRepNode.getObject() != null && dbConnRepNode.getObject().getProperty() != null) {
                    version = dbConnRepNode.getObject().getProperty().getVersion();
                }
            } else if (data instanceof MDMConnectionRepNode) {
                MDMConnectionRepNode mdmConnRepNode = (MDMConnectionRepNode) data;
                if (mdmConnRepNode.getObject() != null && mdmConnRepNode.getObject().getProperty() != null) {
                    version = mdmConnRepNode.getObject().getProperty().getVersion();
                }
            } else if (data instanceof DFConnectionRepNode) {
                DFConnectionRepNode dfConnRepNode = (DFConnectionRepNode) data;
                if (dfConnRepNode.getObject() != null && dfConnRepNode.getObject().getProperty() != null) {
                    version = dfConnRepNode.getObject().getProperty().getVersion();
                }
            }
        }
        return version;
    }

    /**
     * init the version of the Connection accroding to the file name.
     * 
     * @param connection
     * @return
     */
    private String initConnectionVersion(Connection connection) {
        String version = "0.1"; //$NON-NLS-1$
        Resource eResource = connection.eResource();
        if (eResource != null) {
            URI uri = eResource.getURI();
            if (uri != null) {
                String fileName = uri.toString().toLowerCase();
                String[] splits = fileName.split("_"); //$NON-NLS-1$
                if (splits.length > 0) {
                    String str = splits[splits.length - 1];
                    int indexOf = str.indexOf("." + FactoriesUtil.ITEM_EXTENSION); //$NON-NLS-1$
                    version = str.substring(0, indexOf);
                }
            }
        }
        return version;
    }

    // MOD mzhao 2009-05-05, bug 6587.
    /**
     * 
     * This method will make connection elem become proxy, look out for use it.
     */
    public void reloadDataproviderAndFillConnCombo() {
        // MOD yyi 2010-09-27 14549: delete or hide connections when a connection is moved to the trash bin
        // MOD xqliu 2010-09-26 bug 15685
        // Collection<Connection> connections = ProxyRepositoryViewObject.getAllDatabaseConnections(true);
        // // MOD qiongli bug 14891 2010-9-20,Add MDM connections
        // Collection<Connection> mdmConne = ProxyRepositoryViewObject.getAllMDMConnections(true);
        // connections.addAll(mdmConne);
        List<IRepositoryNode> allConnectionReposNodes = RepositoryNodeHelper.getConnectionRepositoryNodes(true);
        // ~ 15685
        // ~ 14549

        int index = 0;
        if (allConnectionReposNodes.size() == 0 && !RepositoryNodeHelper.isOpenDQCommonViewer()) {
            return;
        }
        connCombo.getTable().removeAll();

        // connCombo.defineColumns(new String[] { "Id", "Name", "Metadata Type" });// , new int[] { 5, SWT.DEFAULT,
        // MOD qiongli 2011-5-16,filter the logical delete connection except the analysis dependen on.
        Property property = null;
        DataManager connection = analysis.getContext().getConnection();
        for (IRepositoryNode repNode : allConnectionReposNodes) {
            property = repNode.getObject().getProperty();
            ModelElement modelElement = RepositoryNodeHelper.getModelElementFromRepositoryNode(repNode);
            if (repNode.getObject().isDeleted()) {
                if (connection == null || modelElement != null && !connection.equals(modelElement)) {
                    continue;
                }
            }

            // MOD yyin 201204 TDQ-4977, change to TableCombo type to show the connection type.
            TableItem ti = new TableItem(connCombo.getTable(), SWT.NONE);
            ti.setText(new String[] { property.getDisplayName(), RepositoryNodeHelper.getConnectionType(repNode) });
            // connCombo.add(property.getDisplayName(), index);
            // String prvFileName = PrvResourceFileHelper.getInstance().findCorrespondingFile(prov).getName();

            // MOD sizhaoliu TDQ-6286 fix the migration problem (the table combo shows the first item in case the label
            // of imported analysis does not equal to the file name. )
            // MOD sizhaoliu TDQ-6286 revert this change to avoid the side effect for delimited file connection.
            connCombo.setData(property.getDisplayName() + RepositoryNodeHelper.getConnectionType(repNode), index);
            // connCombo.setData(modelElement.getName() + RepositoryNodeHelper.getConnectionType(repNode), index);
            connCombo.setData(index + "", repNode); //$NON-NLS-1$
            index++;
        }
        if (index > 0) {
            connCombo.select(0);
            // connCombo.setVisibleItemCount(index * 3);
        }
    }

    /**
     * DOC Update the client dependency of the analysis. bug 14014
     */
    public void updateAnalysisClientDependency() {
        DependenciesHandler.getInstance().updateAnalysisClientDependencyConnection(analysis);
    }

    /**
     * ADD gdbu 2011-6-1 bug : 19833
     * 
     * DOC gdbu Comment method "updateDQRuleDependency".
     * 
     * @param dqRules
     */
    protected void updateDQRuleDependency(List<DQRule> dqRules) {
        for (DQRule dqRule : dqRules) {
            List<Dependency> realSupplierDependency = new ArrayList<Dependency>();
            EList<Dependency> supplierDependency = dqRule.getSupplierDependency();
            for (Dependency dependency : supplierDependency) {
                EList<ModelElement> client = dependency.getClient();
                for (ModelElement modelElement : client) {
                    if (modelElement instanceof Analysis) {
                        List<DQRule> dqRules2 = getDqRules((Analysis) modelElement);
                        if (dqRules2.contains(dqRule)) {
                            realSupplierDependency.add(dependency);
                        }
                    }
                }
            }
            supplierDependency.clear();
            supplierDependency.addAll(realSupplierDependency);
        }
    }

    /**
     * ADD gdbu 2011-6-1 bug : 19833
     * 
     * DOC gdbu Comment method "getDqRules". Get all DQRule from analysis.
     * 
     * @param analysis
     * @return
     */
    public List<DQRule> getDqRules(Analysis analysis) {
        List<DQRule> result = new ArrayList<DQRule>();
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
            if (indicatorDefinition instanceof DQRule) {
                result.add((DQRule) indicatorDefinition);
            }
        }
        return result;
    }

    /**
     * DOC bZhou Comment method "getTreeViewer".
     * 
     * @return
     */
    public AbstractColumnDropTree getTreeViewer() {
        return null;
    }

    /**
     * create a label to indicate this connection is logical deleted.
     * 
     * @param parentComp
     */
    private void createConnDeletedLabel(Composite parentComp) {
        this.labelConnDeleted = toolkit.createLabel(parentComp, PluginConstant.EMPTY_STRING);
        labelConnDeleted.setVisible(false);
        labelConnDeleted.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
    }

    public Label getLabelConnDeleted() {
        return this.labelConnDeleted;
    }

    public ExecutionLanguage getUIExecuteEngin() {
        return ExecutionLanguage.SQL;
    }

    /**
     * Log on debug enable.
     * 
     * @param logger
     * @param level
     * @param message
     */
    protected void doLog(Logger logger, Level level, String message) {
        logger.log(level, message);
    }

    /**
     * log when analysis saved
     * 
     * @param saved
     * @throws DataprofilerCoreException
     */
    protected void logSaved(ReturnCode saved) throws DataprofilerCoreException {
        String urlString = analysis.eResource() != null ? (analysis.eResource().getURI().isFile() ? analysis.eResource().getURI()
                .toFileString() : analysis.eResource().getURI().toString()) : PluginConstant.EMPTY_STRING;
        if (!saved.isOk()) {
            throw new DataprofilerCoreException(DefaultMessagesImpl.getString(
                    "ColumnMasterDetailsPage.problem", analysis.getName(), urlString, saved.getMessage())); //$NON-NLS-1$

        } else if (log.isDebugEnabled()) {
            // MOD yyi 2012-02-06 TDQ-4581:avoid the instantiation of the strings to optimize the performances.
            doLog(log, Level.INFO, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.success", urlString)); //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#canSave()
     */
    @Override
    public ReturnCode canSave() {
        return canModifyName(ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
    }

    /**
     * 
     * DOC zshen Comment method "deleteConnectionDependency".
     * 
     * @param analysis
     * @return whether it has been deleted
     * 
     * delete the dependency between analysis and connection
     */
    public boolean deleteConnectionDependency(Analysis analysis) {
        Connection tdProvider = (Connection) analysis.getContext().getConnection();
        if (tdProvider != null && tdProvider.getSupplierDependency().size() > 0) {
            List<Property> clintDependency = DependenciesHandler.getInstance().getClintDependency(analysis);
            tdProvider.getSupplierDependency().get(0).getClient().remove(analysis);
            for (Property clintProperty : clintDependency) {
                Item item = clintProperty.getItem();
                AElementPersistance create = ElementWriterFactory.getInstance().create(item);
                create.save(item, false);
            }
        }
        // always clean the connection info from the analysis
        analysis.getContext().setConnection(null);
        analysis.getClientDependency().clear();
        return true;
    }

    /**
     * DOC xqliu Comment method "createAnalysisLimitSection".
     * 
     * @param sForm
     * @param pComp
     * @return
     * @deprecated use createAnalysisLimitComposite(Composite pComp) instead
     */
    @Deprecated
    protected Section createAnalysisLimitSection(final ScrolledForm sForm, Composite pComp) {
        Section section = createSection(sForm, pComp,
                DefaultMessagesImpl.getString("AbstractMetadataFormPage.AnalysisLimit"), null); //$NON-NLS-1$
        Composite parent = this.toolkit.createComposite(section);
        this.createAnalysisLimitComposite(parent);
        section.setClient(parent);
        return section;
    }

    /**
     * DOC xqliu Comment method "createAnalysisLimitComposite".
     * 
     * @param pComp
     * @return
     */
    protected Composite createAnalysisLimitComposite(Composite pComp) {
        Composite comp = pComp;
        comp.setLayout(new GridLayout(2, false));
        this.toolkit.createLabel(comp,
                DefaultMessagesImpl.getString("AnalysisTuningPreferencePage.NumberOfConnectionsPerAnalysis")); //$NON-NLS-1$

        this.numberOfConnectionsPerAnalysisText = this.toolkit.createText(comp,
                String.valueOf(AnalysisHandler.createHandler(getAnalysis()).getNumberOfConnectionsPerAnalysis()), SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, true).applyTo(this.numberOfConnectionsPerAnalysisText);
        ((GridData) this.numberOfConnectionsPerAnalysisText.getLayoutData()).widthHint = 60;

        this.numberOfConnectionsPerAnalysisText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }

        });

        this.numberOfConnectionsPerAnalysisText.addVerifyListener(new VerifyListener() {

            public void verifyText(VerifyEvent e) {
                String inputValue = e.text;
                Pattern pattern = Pattern.compile("^[0-9]"); //$NON-NLS-1$
                char[] charArray = inputValue.toCharArray();
                for (char c : charArray) {
                    if (!pattern.matcher(String.valueOf(c)).matches()) {
                        e.doit = false;
                    }
                }
            }
        });

        return comp;
    }

    /**
     * DOC xqliu Comment method "saveNumberOfConnectionsPerAnalysis".
     */
    protected void saveNumberOfConnectionsPerAnalysis() {
        AnalysisHandler.createHandler(this.getAnalysis()).setNumberOfConnectionsPerAnalysis(
                this.numberOfConnectionsPerAnalysisText.getText());
    }

    /**
     * 
     * save the removed elements(clean depency) in treeviewer.
     */
    protected void saveRemovedElements() {
        HashSet<ModelElement> removedElements = this.getTreeViewer().getRemovedElements();
        for (ModelElement mod : removedElements) {
            if (mod.eIsProxy()) {
                mod = (ModelElement) EObjectHelper.resolveObject(mod);
            }
            if (mod.eResource() == null) {
                log.error("There is something wrong when saving resource of " + mod.getName()); //$NON-NLS-1$ 
            } else {
                EMFUtil.saveSingleResource(mod.eResource());
            }
        }
        this.getTreeViewer().getRemovedElements().clear();
    }

    /**
     * set ScrolledForm.
     * 
     * @param form
     */
    public void setForm(ScrolledForm form) {
        super.form = form;
    }

    /**
     * get ScrolledForm.
     * 
     * @return
     */
    public ScrolledForm getForm() {
        return form;
    }

    /**
     * get ChartComposite.
     * 
     * @return
     */
    public Composite getChartComposite() {
        return null;
    }

    /**
     * create Analysis Param Section.
     * 
     * @param pForm
     * @param pComp
     */
    protected void createAnalysisParamSection(final ScrolledForm pForm, Composite pComp) {
        analysisParamSection = createSection(pForm, pComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.AnalysisParameter"), null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(analysisParamSection);
        createAnalysisLimitComposite(sectionClient);
        analysisParamSection.setClient(sectionClient);
    }
}
