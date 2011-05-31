// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.core.model.properties.Property;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.IRuningStatusListener;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.composite.AbstractColumnDropTree;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DFConnectionRepNode;
import org.talend.dq.nodes.MDMConnectionRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractAnalysisMetadataPage extends AbstractMetadataFormPage implements IRuningStatusListener {

    private static Logger log = Logger.getLogger(AbstractAnalysisMetadataPage.class);

    protected Analysis analysis;

    protected AnalysisRepNode analysisRepNode;

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

    protected CCombo connCombo;

    protected Text textConnVersion;

    protected Label labelConnDeleted;

    public CCombo getConnCombo() {
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
            MessageDialogWithToggle.openError(null,
                    DefaultMessagesImpl.getString("AbstractAnalysisMetadataPage.SaveAnalysis"), rc.getMessage()); //$NON-NLS-1$
        } else if(!checkWhithspace()){
            MessageDialogWithToggle.openError(null,DefaultMessagesImpl.getString("AbstractAnalysisMetadataPage.SaveAnalysis"), DefaultMessagesImpl.getString("AbstractMetadataFormPage.whitespace")); //$NON-NLS-1$
        }else {
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
                ExceptionHandler.process(e, Level.ERROR);
                log.error(e, e);
            }
        }
    }

    public ScrolledForm getScrolledForm() {
        return null;
    }

    public abstract ReturnCode canSave();

    /**
     * Analysis of whether the name check can be modified
     * 
     * DOC gdbu Comment method "canModifyAnalysisName".
     * 
     * @return ReturnCodec
     */
    protected ReturnCode canModifyAnalysisName() {
        // MOD by gdbu 2011-3-21 bug 19179
        this.nameText.setText(this.nameText.getText().replace(" ", ""));//$NON-NLS-1$ //$NON-NLS-2$
        if (this.nameText.getText().length() == 0) {
            // analysis can not without a name
            this.nameText.setText(this.analysis.getName());
            return new ReturnCode(DefaultMessagesImpl.getString("AbstractFilterMetadataPage.MSG_ANALYSIS_NONE_NAME"), false);//$NON-NLS-1$
        }
        String elementName = this.nameText.getText();
        List<IRepositoryNode> childrensname = this.analysisRepNode.getParent().getChildren();
        for (IRepositoryNode children : childrensname) {
            if (elementName.equals(this.analysis.getName())) {
                // if new name equals itself's old name ,return true
                break;
            } else if (elementName.equals((children.getLabel() + "").replace(" ", ""))) {//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-2$
                // if new name equals one of tree-list's name,return false
                this.nameText.setText(this.analysis.getName());
                return new ReturnCode(DefaultMessagesImpl.getString("AbstractFilterMetadataPage.MSG_ANALYSIS_SAME_NAME"), false);//$NON-NLS-1$
            }
        }
        // MOD klliu bug 19995 question2
        // After checking nametext contain empty string,must setUp FormPage's dirty is false in saving
        // process
        setDirty(false);
        // ~
        return new ReturnCode(true);
        // ~19179
    }

    protected abstract ReturnCode canRun();

    public abstract void refresh();

    protected abstract void saveAnalysis() throws DataprofilerCoreException;

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
     * 
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
        connCombo = new CCombo(labelButtonClient, SWT.BORDER);
        connCombo.setEditable(false);
        connCombo.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                updateAnalysisConnectionVersionInfo();
            }

            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
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
            String strConnVersion = DefaultMessagesImpl.getString("AbstractMetadataFormPage.connVersion")
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
                // shouldn't use RepositoryNodeHelper.recursiveFind() to find the RepositoryNode, because this method
                // will return the last version of the connection, not the correct version of connection which analysis
                // depend on
                //
                // RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(connection);
                // if (recursiveFind != null) {
                // if (recursiveFind.getObject() != null && recursiveFind.getObject().getProperty() != null) {
                // version = recursiveFind.getObject().getProperty().getVersion();
                // }
                // }
            }
        }
        return version == null ? getConnectionVersionDefault() : version; //$NON-NLS-1$
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
    protected void reloadDataproviderAndFillConnCombo() {
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
        connCombo.removeAll();
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
            connCombo.add(property.getLabel(), index);
            // String prvFileName = PrvResourceFileHelper.getInstance().findCorrespondingFile(prov).getName();
            connCombo.setData(property.getLabel(), index);
            connCombo.setData(index + "", repNode); //$NON-NLS-1$
            index++;
        }
        if (index > 0) {
            connCombo.select(0);
        }
    }

    /**
     * DOC Update the client dependency of the analysis. bug 14014
     */
    public void updateAnalysisClientDependency() {
        DependenciesHandler.getInstance().updateAnalysisClientDependencyConnection(analysis);
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
     * 
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

}
