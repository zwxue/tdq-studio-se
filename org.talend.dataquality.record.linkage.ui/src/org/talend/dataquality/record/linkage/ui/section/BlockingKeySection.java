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
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.dataquality.record.linkage.ui.action.ExecuteGenerateBlockingAction;
import org.talend.dataquality.record.linkage.ui.composite.BlockingKeyTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.chart.BlockingKeyDataChart;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.rules.BlockKeyDefinition;

/**
 * created by zshen on Aug 6, 2013
 * Detailled comment
 *
 */
public class BlockingKeySection extends AbstractMatchTableSection {

    private final String SECTION_NAME = "Blocking Key"; //$NON-NLS-1$

    private BlockingKeyDataChart blockingKeyDataChart = null;

    private BlockingKeyTableComposite blockingKeyComposite = null;
    /**
     * DOC zshen BlockingKeySection constructor comment.
     *
     * @param parent
     * @param style
     * @param toolkit
     */
    public BlockingKeySection(final ScrolledForm form, Composite parent, int style, FormToolkit toolkit) {
        super(form, parent, style, toolkit);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchTableSection#getSectionName()
     */
    @Override
    protected String getSectionName() {
        return SECTION_NAME;
    }

    @Override
    protected void createSubContext(Composite sectionClient) {

        Composite ruleComp = toolkit.createComposite(sectionClient, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        ruleComp.setLayoutData(data);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        ruleComp.setLayout(gridLayout);
        blockingKeyComposite = new BlockingKeyTableComposite(ruleComp, SWT.NO_FOCUS);
        blockingKeyComposite.setLayout(gridLayout);
        blockingKeyComposite.setLayoutData(data);
        createRefrshButton(ruleComp);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.section.AbstractMatchTableSection#createSubChart(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    protected void createSubChart(Composite sectionClient) {
        Composite blockComp = toolkit.createComposite(sectionClient, SWT.NONE);
        GridLayout tableLayout = new GridLayout(1, Boolean.TRUE);
        blockComp.setLayout(tableLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        blockComp.setLayoutData(gridData);
        ExecuteGenerateBlockingAction executeGenerateBlockingAction = computeRusult();
        blockingKeyDataChart = new BlockingKeyDataChart(blockComp, executeGenerateBlockingAction.getResultDatas());

    }



    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchTableSection#RefreshChart()
     */
    @Override
    public void RefreshChart() {
        ExecuteGenerateBlockingAction executeGenerateBlockingAction = computeRusult();
        blockingKeyDataChart.refresh(executeGenerateBlockingAction.getResultDatas());
    }

    /**
     * DOC zshen Comment method "computeRusult".
     * @return
     */
    protected ExecuteGenerateBlockingAction computeRusult() {
        List<Map<String, String>> blockingKeyData = MatchRuleAnlaysisUtils.blockingKeyDataConvert(
blockingKeyComposite
                .getInputElement());
        ExecuteGenerateBlockingAction executeGenerateBlockingAction = new ExecuteGenerateBlockingAction(blockingKeyData,
                columnMap);
        if (hasBlockingKey()) {
            executeGenerateBlockingAction.setInputData(tableData);
            executeGenerateBlockingAction.run();
        }
        return executeGenerateBlockingAction;
    }

    /**
     * DOC zshen Comment method "hasBlockingKey".
     *
     * @return
     */
    private boolean hasBlockingKey() {
        List<BlockKeyDefinition> inputElement = blockingKeyComposite.getInputElement();
        if (inputElement.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean addElement(String columnName) {
        return blockingKeyComposite.addElement(columnName);
    }

    public void removeElement(String columnName) {
        blockingKeyComposite.removeElement(columnName);
    }



    /**
     * DOC yyin Comment method "hasBlockKey".
     *
     * @return
     */
    private boolean hasBlockKey() {
        if (this.blockingKeyComposite.getInputElement().size() <= 0) {
            return false;
        }
        return true;
    }

}
