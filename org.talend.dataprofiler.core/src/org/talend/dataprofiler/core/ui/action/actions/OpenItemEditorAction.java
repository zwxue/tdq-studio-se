// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.dqrules.BusinessRuleItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.dqrules.DQRuleEditor;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorDefinitionItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditor;
import org.talend.dataprofiler.core.ui.editor.pattern.PatternEditor;
import org.talend.dataprofiler.core.ui.editor.pattern.PatternItemEditorInput;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC mzhao Open TDQ items editor action.
 */
public class OpenItemEditorAction extends Action {

    protected static Logger log = Logger.getLogger(OpenItemEditorAction.class);

    private IRepositoryViewObject reposViewObj = null;

    protected String editorID = null;

    private AbstractItemEditorInput editorInput = null;

    private IFile fileEditorInput = null;

    public OpenItemEditorAction(IRepositoryViewObject reposViewObj) {
        super(DefaultMessagesImpl.getString("OpenIndicatorDefinitionAction.Open")); //$NON-NLS-1$
        this.reposViewObj = reposViewObj;
    }

    @Override
    public void run() {
        // Connection editor
        String key = reposViewObj.getRepositoryObjectType().getKey();
        Item item = reposViewObj.getProperty().getItem();
        if (ERepositoryObjectType.METADATA_CONNECTIONS.getKey().equals(key)
                || ERepositoryObjectType.METADATA_MDMCONNECTION.getKey().equals(key)) {
            editorInput = new ConnectionItemEditorInput(item);
            editorID = ConnectionEditor.class.getName();
        } else if (ERepositoryObjectType.TDQ_ANALYSIS.getKey().equals(key)) {
            editorInput = new AnalysisItemEditorInput(item);
            Analysis analysis = ((TDQAnalysisItem) item).getAnalysis();
            AnalysisParameters parameters = analysis.getParameters();
            AnalysisType analysisType = parameters.getAnalysisType();
            boolean equals = analysisType.equals(AnalysisType.CONNECTION);
            if (equals) {
                EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
                Connection conn = null;
                if (analysedElements.size() > 0) {
                    conn = (Connection) analysedElements.get(0);
                }
                // FIXME User UUID to find the right conn repository node.
                String label = conn.getLabel();
                IRepositoryNode connectionRepositoryNode = DQStructureManager.getInstance().getConnectionRepositoryNode(label);
                ((AnalysisItemEditorInput) editorInput).setConnectionNode((DBConnectionRepNode) connectionRepositoryNode);
            }
            editorID = AnalysisEditor.class.getName();
        } else if (ERepositoryObjectType.TDQ_INDICATOR_ELEMENT.getKey().equals(key)) {
            editorInput = new IndicatorDefinitionItemEditorInput(item);
            editorID = IndicatorEditor.class.getName();
        } else if (ERepositoryObjectType.TDQ_RULES.getKey().equals(key)) {
            editorInput = new BusinessRuleItemEditorInput(item);
            editorID = DQRuleEditor.class.getName();
        } else if (ERepositoryObjectType.TDQ_PATTERN_ELEMENT.getKey().equals(key)) {
            editorInput = new PatternItemEditorInput(item);
            editorID = PatternEditor.class.getName();
        } else if (ERepositoryObjectType.TDQ_JRXMLTEMPLATE.getKey().equals(key)
                || ERepositoryObjectType.TDQ_SOURCE_FILES.getKey().equals(key)
                || ERepositoryObjectType.TDQ_RULES_SQL.getKey().equals(key)) {
            IPath append = WorkbenchUtils.getFilePath((RepositoryNode) reposViewObj.getRepositoryNode());
            fileEditorInput = ResourceManager.getRootProject().getFile(append);
        }
        if (editorInput != null) {
            CorePlugin.getDefault().openEditor(editorInput, editorID);
        } else {
            try {
                IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileEditorInput, true);
            } catch (PartInitException e) {
                log.error(e, e);
            }
        }

    }
}
