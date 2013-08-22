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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.record.linkage.ui.section.definition.BlockingKeyDefinitionSection;

/**
 * created by zshen on Aug 20, 2013
 * Detailled comment
 *
 */
public class SelectAlgorithmSection extends AbstractSectionComposite {

    private boolean isVSRMode = true;

    BlockingKeyDefinitionSection bk = null;


    public SelectAlgorithmSection(ScrolledForm form, Composite parent, FormToolkit toolkit) {
        this(form, parent, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED, toolkit);
    }
    /**
     * DOC zshen SelectAlgorithmSection constructor comment.
     *
     * @param parent
     * @param style
     * @param toolkit
     */
    public SelectAlgorithmSection(ScrolledForm form, Composite parent, int style, FormToolkit toolkit) {
        super(form, parent, style, toolkit);
        section.setText(DefaultMessagesImpl.getString("SelectAlgorithmSection.record.linkage.algorithm")); //$NON-NLS-1$
        section.setDescription(DefaultMessagesImpl.getString("SelectAlgorithmSection.choose.algorithm")); //$NON-NLS-1$
        createChooseAlgorithmCom(section);
    }

    /**
     * DOC zshen Comment method "createChooseAlgorithmCom".
     *
     * @param dqRuleDefinitionSection
     */
    private void createChooseAlgorithmCom(Section dqRuleDefinitionSection) {
        Composite mainComp = toolkit.createComposite(dqRuleDefinitionSection);
        mainComp.setLayout(new GridLayout());

        Composite container = toolkit.createComposite(mainComp, SWT.NONE);
        GridLayout gdLayout = new GridLayout(1, false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(container);
        container.setLayout(gdLayout);

        Button tSwooshButton = toolkit.createButton(container,
                DefaultMessagesImpl.getString("SelectAlgorithmSection.t_swoosh.algorithm"), SWT.RADIO); //$NON-NLS-1$
        tSwooshButton.setSelection(!isVSRMode());
        tSwooshButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isVSRMode = false;
                bk.setIsAddColumn(true);
                bk.getSection().redraw();
                bk.getSection().update();
                // bk.getSection().setVisible(false);
                bk.getSection().layout();
                bk.getSection().dispose();
                getForm().layout();
            }

        });
        Button vsrButton = toolkit.createButton(container,
                DefaultMessagesImpl.getString("SelectAlgorithmSection.vsr_algorithm"), SWT.RADIO); //$NON-NLS-1$
        vsrButton.setSelection(isVSRMode());
        vsrButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                isVSRMode = true;
                bk.setIsAddColumn(false);
                bk.getSection().redraw();
                bk.getSection().update();
                bk.getSection().layout();
                // getForm().redraw();
            }

        });
        dqRuleDefinitionSection.setClient(mainComp);

    }

    public boolean isVSRMode() {
        return isVSRMode;
    }

    /**
     * Sets the bk.
     *
     * @param bk the bk to set
     */
    public void setBk(BlockingKeyDefinitionSection bk) {
        this.bk = bk;
    }


}
