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
package org.talend.dataprofiler.core.ui.editor.composite;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.pattern.PatternUtilities;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * 
 * DOC mzhao Feature 13040 . 2010-05-21
 */
public class PatternMouseAdapter extends MouseAdapter {

    private AbstractColumnDropTree columnDropTree = null;

    private AbstractAnalysisMetadataPage masterPage;

    private Analysis analysis = null;

    private ModelElementIndicator meIndicator = null;

    private TreeItem treeItem = null;

    private ViewerFilter[] filters;

    public PatternMouseAdapter(AbstractColumnDropTree columnDropTree, AbstractAnalysisMetadataPage masterPage,
            ModelElementIndicator meIndicator, TreeItem treeItem) {
        this.masterPage = masterPage;
        this.analysis = masterPage.getAnalysis();
        this.meIndicator = meIndicator;
        this.treeItem = treeItem;
        this.columnDropTree = columnDropTree;
    }

    @Override
    public void mouseDown(MouseEvent e) {
        DataManager dm = analysis.getContext().getConnection();
        if (dm == null) {
            masterPage.doSave(null);
        }

        // IFolder libProject = ResourceManager.getLibrariesFolder();
        IRepositoryNode patternFolderNode = RepositoryNodeHelper.getLibrariesFolderNode(EResourceConstant.PATTERN_REGEX);

        CheckedTreeSelectionDialog dialog = PatternUtilities.createPatternCheckedTreeSelectionDialog(patternFolderNode);

        if (null != filters) {
            for (ViewerFilter filter : filters) {
                dialog.addFilter(filter);
            }
        }
        // MOD qiongli 2011-6-16 bug 21768,pattern in columnset just support java engine.
        AnalysisType analysisType = analysis.getParameters().getAnalysisType();
        ExecutionLanguage executionLanguage = analysis.getParameters().getExecutionLanguage();
        if (AnalysisType.COLUMN_SET.equals(analysisType)) {
            if (ExecutionLanguage.SQL.equals(executionLanguage)) {
                MessageUI.openWarning(DefaultMessagesImpl.getString("PatternMouseAdapter.noSupportForSqlEngine"));
                return;
            }
        }

        // IFile[] selectedFiles =
        // PatternUtilities.getPatternFileByIndicator(clmIndicator);
        // dialog.setInitialSelections(selectedFiles);
        if (dialog.open() == Window.OK) {
            for (Object obj : dialog.getResult()) {
                if (obj instanceof PatternRepNode) {
                    PatternRepNode patternNode = (PatternRepNode) obj;
                    TypedReturnCode<IndicatorUnit> trc = PatternUtilities.createIndicatorUnit(patternNode.getPattern(),
                            meIndicator, analysis);
                    if (trc.isOk()) {
                        columnDropTree.createOneUnit(treeItem, trc.getObject());
                        columnDropTree.setDirty(true);
                    } else if (trc.getMessage() != null && !trc.getMessage().trim().equals("")) {//$NON-NLS-1$
                        // Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
                        // MessageUI.openError(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.IndicatorSelected") //$NON-NLS-1$
                        // + pattern.getName());
                        MessageUI.openError(trc.getMessage());
                    }
                }
            }
        }
    }

    public void addFilter(ViewerFilter... filters) {
        this.filters = filters;
    }
}
