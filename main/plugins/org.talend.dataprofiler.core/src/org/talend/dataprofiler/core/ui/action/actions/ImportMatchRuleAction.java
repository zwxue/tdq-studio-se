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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.dialog.MatchRuleElementTreeSelectionDialog;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.editor.analysis.MatchMasterDetailsPage;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class ImportMatchRuleAction extends Action {

    protected static Logger log = Logger.getLogger(ImportMatchRuleAction.class);

    private AbstractAnalysisMetadataPage masterPage;

    public ImportMatchRuleAction(AbstractAnalysisMetadataPage masterPage) {
        this.masterPage = masterPage;
        ImageDescriptor imageDescriptor = ImageLib.getImageDescriptor(ImageLib.IMPORT_MATCH_RULE_ICON);

        this.setImageDescriptor(imageDescriptor);
    }

    @Override
    public void run() {
        MatchRuleElementTreeSelectionDialog dialog = new MatchRuleElementTreeSelectionDialog(null,
                new ImportMatchRuleLabelProvider(), new WorkbenchContentProvider(),
                MatchRuleElementTreeSelectionDialog.MATCH_ANALYSIS_TYPE);

        List<String> inputColumnNames = new ArrayList<String>();
        Analysis analysis = masterPage.getAnalysis();
        EList<ModelElement> elements = analysis.getContext().getAnalysedElements();
        for (ModelElement me : elements) {
            inputColumnNames.add(me.getName());
        }
        dialog.setInputColumnNames(inputColumnNames);
        dialog.create();

        // dialog.setExpandedElements(getAllMatchRules());

        if (dialog.open() == Window.OK) {
            Object[] results = dialog.getResult();
            for (Object obj : results) {
                if (obj instanceof IFile) {
                    IFile file = (IFile) obj;
                    MatchRuleDefinition matchRule = DQRuleResourceFileHelper.getInstance().findMatchRule(file);
                    if (matchRule != null) {
                        updateMatchRule(matchRule, dialog.isOverwrite());
                    }
                }
            }

        }
    }

    /**
     * if is overwrite: clear all block/match keys. add keys from matchRule to the master page
     * 
     * @param matchRule
     * @param overwrite
     */
    private void updateMatchRule(MatchRuleDefinition matchRule, boolean overwrite) {
        ((MatchMasterDetailsPage) masterPage).importMatchRule(matchRule, overwrite);
    }

}
