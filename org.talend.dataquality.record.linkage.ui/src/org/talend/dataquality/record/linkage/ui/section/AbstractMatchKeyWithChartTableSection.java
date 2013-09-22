// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.section;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.columnset.BlockKeyIndicator;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;
import org.talend.dataquality.record.linkage.ui.action.ExecuteMatchRuleHandler;
import org.talend.dataquality.record.linkage.ui.composite.chart.MatchRuleDataChart;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchRule;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * created by zshen on Aug 26, 2013 Detailled comment
 * 
 */
abstract public class AbstractMatchKeyWithChartTableSection extends AbstractMatchAnaysisTableSection {

    protected MatchRuleDataChart matchRuleChartComp = null;

    private Logger log = Logger.getLogger(AbstractMatchKeyWithChartTableSection.class);

    /**
     * DOC zshen AbstractMatchKeyWithChartTableSection constructor comment.
     * 
     * @param form
     * @param parent
     * @param style
     * @param toolkit
     * @param analysis
     */
    public AbstractMatchKeyWithChartTableSection(ScrolledForm form, Composite parent, int style, FormToolkit toolkit,
            Analysis analysis) {
        super(form, parent, style, toolkit, analysis);
    }

    /**
     * DOC zshen Comment method "createHideGroupComposite".
     * 
     * @param chartComposite
     */
    protected void createHideGroupComposite(Composite chartComposite) {
        GridData gridData;
        Composite toolComp = toolkit.createComposite(chartComposite, SWT.NONE);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        toolComp.setLayoutData(gridData);

        GridLayout gridLayout = new GridLayout(3, false);
        toolComp.setLayout(gridLayout);

        Label lessText = new Label(toolComp, SWT.NONE);
        lessText.setText(DefaultMessagesImpl.getString("AbstractMatchKeyWithChartTableSection.hide_groups")); //$NON-NLS-1$

        // change spin to combo
        final Spinner lessSpin = new Spinner(toolComp, SWT.BORDER);
        lessSpin.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                int times = Integer.parseInt(lessSpin.getText());
                if (matchRuleChartComp != null) {
                    matchRuleChartComp.setTimes(times);
                    listeners.firePropertyChange(MatchAnalysisConstant.NEED_REFRESH_DATA, true, false);
                    RecordMatchingIndicator recordMatchingIndicator = computeMatchResult();
                    if (recordMatchingIndicator != null) {
                        matchRuleChartComp.refresh(recordMatchingIndicator.getGroupSize2groupFrequency());
                    } else {
                        log.error("Can not get rusult data after AbstractMatchKeyWithChartTableSection.computeMatchResult()"); //$NON-NLS-1$
                    }
                    matchRows.clear();
                }

            }
        });

        Label lessText2 = new Label(toolComp, SWT.NONE);
        lessText2.setText(DefaultMessagesImpl.getString("AbstractMatchKeyWithChartTableSection.items")); //$NON-NLS-1$
    }

    protected RecordMatchingIndicator computeMatchResult() {
        final Object[] IndicatorList = MatchRuleAnlaysisUtils.getNeedIndicatorFromAna(analysis);
        if (IndicatorList.length != 2) {
            return null;
        }
        final RecordMatchingIndicator recordMatchingIndicator = EcoreUtil.copy((RecordMatchingIndicator) IndicatorList[0]);
        BlockKeyIndicator blockKeyIndicator = EcoreUtil.copy((BlockKeyIndicator) IndicatorList[1]);
        if (!hasMatchKey()) {
            MessageDialogWithToggle
                    .openError(
                            null,
                            DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), DefaultMessagesImpl.getString("MatchMasterDetailsPage.NoMatchKey")); //$NON-NLS-1$//$NON-NLS-2$
            return recordMatchingIndicator;
        }
        TypedReturnCode<MatchGroupResultConsumer> execute = ExecuteMatchRuleHandler.execute(analysis, columnMap,
                recordMatchingIndicator, matchRows, blockKeyIndicator);
        if (!execute.isOk()) {
            Shell shell = null;
            if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
                shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            }
            MessageDialogWithToggle
                    .openError(
                            shell,
                            DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), DefaultMessagesImpl.getString("RunAnalysisAction.failRunAnalysis", analysis.getName(), execute.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$
        }else{
            MatchRuleAnlaysisUtils.refreshDataTable(analysis, execute.getObject().getFullMatchResult());
        }
        return recordMatchingIndicator;

    }

    private boolean hasMatchKey() {
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        List<MatchRule> matchRules = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules();
        if (matchRules == null || matchRules.size() == 0) {
            return Boolean.FALSE;
        }
        for (MatchRule matchRule : matchRules) {
            if (matchRule.getMatchKeys().size() > 0) {
                return true;
            }
        }
        return false;
    }

}
