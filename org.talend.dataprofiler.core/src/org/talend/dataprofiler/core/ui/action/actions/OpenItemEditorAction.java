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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.dqrules.BusinessRuleItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.dqrules.DQRuleEditor;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorDefinitionItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditor;
import org.talend.dataprofiler.core.ui.editor.parserrules.ParserRuleEditor;
import org.talend.dataprofiler.core.ui.editor.parserrules.ParserRuleItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.pattern.PatternEditor;
import org.talend.dataprofiler.core.ui.editor.pattern.PatternItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.report.ReportItemEditorInput;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.ReportFileRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC mzhao Open TDQ items editor action.
 */
public class OpenItemEditorAction extends Action implements IIntroAction {

    protected static Logger log = Logger.getLogger(OpenItemEditorAction.class);

    private IRepositoryViewObject repViewObj = null;

    protected String editorID = null;

    private AbstractItemEditorInput itemEditorInput = null;

    private IFile file = null;

    private Connection connection = null;

    private IRepositoryNode repNode = null;

    public OpenItemEditorAction() {
        super(DefaultMessagesImpl.getString("OpenIndicatorDefinitionAction.Open")); //$NON-NLS-1$
    }

    public OpenItemEditorAction(IRepositoryViewObject reposViewObj) {
        super(DefaultMessagesImpl.getString("OpenIndicatorDefinitionAction.Open")); //$NON-NLS-1$
        this.repViewObj = reposViewObj;
    }

    public OpenItemEditorAction(IRepositoryNode repNode) {
        super(DefaultMessagesImpl.getString("OpenIndicatorDefinitionAction.Open")); //$NON-NLS-1$
        this.repNode = repNode;
        this.repViewObj = repNode.getObject();

    }

    @Override
    public void run() {
        this.itemEditorInput = computeEditorInput();
        if (itemEditorInput != null) {
            // open ItemEditorInput
            CorePlugin.getDefault().openEditor(itemEditorInput, editorID);
        } else {
            // not find ItemEditorInput
            if (repViewObj == null) {
                // open Report's genetated doc file
                if (repNode != null && repNode instanceof ReportFileRepNode) {
                    ReportFileRepNode reportFileNode = (ReportFileRepNode) repNode;
                    IPath location = Path.fromOSString(reportFileNode.getResource().getRawLocation().toOSString());
                    IFile latestRepIFile = ResourceManager.getRootProject().getFile(location.lastSegment());
                    try {
                        latestRepIFile.createLink(location, IResource.REPLACE, null);
                        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        page.openEditor(new FileEditorInput(latestRepIFile), IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
                    } catch (Throwable e) {
                        log.error(e, e);
                    }
                }
            } else {
                // if there don't found the correct ItemEditorInput and it is not Report's genetated doc file, try to
                // open it as a File, this code will not be execute when method computeEditorInput() work well
                IPath append = WorkbenchUtils.getFilePath((RepositoryNode) repViewObj.getRepositoryNode());
                file = ResourceManager.getRootProject().getFile(append);
                try {
                    IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
                } catch (PartInitException e) {
                    log.error(e, e);
                }
            }
        }
    }

    /**
     * get the ItemEditorInput according to the reposViewObj, if there no ItemEditorInput return null.
     * 
     * @return
     */
    public AbstractItemEditorInput computeEditorInput() {
        AbstractItemEditorInput result = null;
        if (repViewObj != null) {
            // Connection editor
            String key = repViewObj.getRepositoryObjectType().getKey();
            Item item = repViewObj.getProperty().getItem();
            if (ERepositoryObjectType.METADATA_CONNECTIONS.getKey().equals(key)
                    || ERepositoryObjectType.METADATA_MDMCONNECTION.getKey().equals(key)) {
                result = new ConnectionItemEditorInput(item);
                editorID = ConnectionEditor.class.getName();
            } else if (ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT.getKey().equals(key)) {
                result = new AnalysisItemEditorInput(item);
                Analysis analysis = ((TDQAnalysisItem) item).getAnalysis();
                AnalysisParameters parameters = analysis.getParameters();
                AnalysisType analysisType = parameters.getAnalysisType();
                // boolean equals = analysisType.equals(AnalysisType.CONNECTION);
                // if (equals) {
                EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
                RepositoryNode connectionRepositoryNode = null;
                if (analysedElements.size() > 0) {
                    ModelElement modelElement = analysedElements.get(0);
                    if (modelElement instanceof Connection) {
                        connection = (Connection) modelElement;
                    }
                    if (modelElement instanceof Catalog) {
                        Catalog catalog = SwitchHelpers.CATALOG_SWITCH.caseCatalog((Catalog) modelElement);
                        connection = ConnectionHelper.getConnection(catalog);
                    } else if (modelElement instanceof Schema) {
                        Schema schema = SwitchHelpers.SCHEMA_SWITCH.caseSchema((Schema) modelElement);
                        if (schema != null) {
                            connection = ConnectionHelper.getConnection(schema);
                        }
                    } else if (modelElement instanceof TdTable) {
                        TdTable tdTable = SwitchHelpers.TABLE_SWITCH.caseTdTable((TdTable) modelElement);
                        connection = ConnectionHelper.getConnection(tdTable);
                    } else if (modelElement instanceof TdView) {
                        TdView tdView = SwitchHelpers.VIEW_SWITCH.caseTdView((TdView) modelElement);
                        connection = ConnectionHelper.getConnection(tdView);
                    } else if (modelElement instanceof TdColumn) {
                        TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.caseTdColumn((TdColumn) modelElement);
                        connection = ConnectionHelper.getConnection(tdColumn);
                    } else if (modelElement instanceof MetadataColumn) {
                        MetadataColumn mColumn = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(modelElement);
                        connection = ConnectionHelper.getTdDataProvider(mColumn);
                    }
                    connectionRepositoryNode = RepositoryNodeHelper.recursiveFind(connection);
                }

                ((AnalysisItemEditorInput) result).setConnectionNode(connectionRepositoryNode);

                editorID = AnalysisEditor.class.getName();
            } else if (ERepositoryObjectType.TDQ_INDICATOR_ELEMENT.getKey().equals(key)) {
                result = new IndicatorDefinitionItemEditorInput(item);
                editorID = IndicatorEditor.class.getName();
            } else if (ERepositoryObjectType.TDQ_RULES.getKey().equals(key)) {
                // MOD klliu feature 23109
                ERepositoryObjectType properties = (ERepositoryObjectType) repViewObj.getRepositoryNode().getProperties(
                        EProperties.CONTENT_TYPE);
                if (properties.equals(ERepositoryObjectType.TDQ_RULES_PARSER)) {
                    result = new ParserRuleItemEditorInput(item);
                    editorID = ParserRuleEditor.class.getName();
                } else {
                    result = new BusinessRuleItemEditorInput(item);
                    editorID = DQRuleEditor.class.getName();
                }

            } else if (ERepositoryObjectType.TDQ_PATTERN_ELEMENT.getKey().equals(key)) {
                result = new PatternItemEditorInput(item);
                editorID = PatternEditor.class.getName();
            } else if (ERepositoryObjectType.TDQ_REPORT_ELEMENT.getKey().equals(key)) {
                result = new ReportItemEditorInput(item);
                editorID = "org.talend.dataprofiler.core.tdq.ui.editor.report.ReportEditror"; //$NON-NLS-1$
            }
        }
        return result;
    }

    /**
     * DOC klliu Comment method "retiveConnections".
     * 
     * @param modelElement
     * @return
     */
    private Connection retiveConnections(ModelElement modelElement) {
        Connection conn = SwitchHelpers.CONNECTION_SWITCH.doSwitch(modelElement);
        if (conn == null) {
            Catalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(modelElement);
            Schema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(modelElement);
            if (catalog != null) {
                conn = ConnectionHelper.getConnection(catalog);
            } else if (schema != null) {
                conn = ConnectionHelper.getConnection(schema);
            }
        }
        return conn;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite, java.util.Properties)
     */
    public void run(IIntroSite site, Properties params) {
        initRepositoryViewObject(params);
        PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
        run();
    }

    /**
     * DOC xqliu Comment method "initRepositoryViewObject".
     * 
     * @param params
     */
    private void initRepositoryViewObject(Properties params) {
        RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(params.getProperty("nodeId")); //$NON-NLS-1$
        if (repositoryNode != null) {
            this.repViewObj = repositoryNode.getObject();
        }
    }

}
