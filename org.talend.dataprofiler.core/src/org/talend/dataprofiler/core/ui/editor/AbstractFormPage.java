// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataprofiler.core.ui.pref.EditorPreferencePage;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractFormPage extends FormPage {

    protected boolean isDirty = false;

    protected FormToolkit toolkit;

    protected CommonFormEditor currentEditor;

    protected Boolean foldingState;

    private static int sectionCount = 0;

    public AbstractFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        this.toolkit = this.getEditor().getToolkit();
        this.currentEditor = (CommonFormEditor) editor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
     */
    @Override
    public void initialize(FormEditor editor) {
        super.initialize(editor);

        initFoldingState();

        sectionCount = 0;
    }

    /**
     * DOC bZhou Comment method "createSection".
     * 
     * @param form
     * @param parent
     * @param title
     * @return
     */
    public Section createSection(final ScrolledForm form, Composite parent, String title) {
        return createSection(form, parent, title, null);
    }

    /**
     * DOC bZhou Comment method "createSection".
     * 
     * @param form
     * @param parent
     * @param title
     * @param description
     * @return
     */
    public Section createSection(final ScrolledForm form, Composite parent, String title, String description) {
        int style = Section.DESCRIPTION | Section.TWISTIE | Section.TITLE_BAR;

        if (description == null) {
            style = Section.TWISTIE | Section.TITLE_BAR;
        }

        Section section = toolkit.createSection(parent, style);

        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

        section.addExpansionListener(new ExpansionAdapter() {

            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
            }

        });

        section.setText(title);
        section.setDescription(description);

        if (foldingState == null) {
            section.setExpanded(sectionCount == 0);
        } else {
            section.setExpanded(foldingState);
        }

        currentEditor.registerSection(section);

        sectionCount++;

        return section;
    }

    /**
     * DOC bZhou Comment method "initFoldingState".
     */
    private void initFoldingState() {
        int foldType = EditorPreferencePage.getCurrentFolding();

        switch (foldType) {
        case EditorPreferencePage.FOLDING_1:
            foldingState = true;
            break;
        case EditorPreferencePage.FOLDING_2:
            foldingState = false;
            break;
        default:
        }
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || isDirty;
    }

    /**
     * DOC bZhou Comment method "setDirty".
     * 
     * @param isDirty
     */
    public abstract void setDirty(boolean isDirty);

}
