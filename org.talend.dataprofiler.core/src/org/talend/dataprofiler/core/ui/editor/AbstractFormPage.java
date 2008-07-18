// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractFormPage extends FormPage {

    protected boolean isDirty = false;

    protected FormToolkit toolkit;

    public AbstractFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        this.toolkit = this.getEditor().getToolkit();
    }

    /**
     * @param form
     * @param toolkit
     * @param anasisDataComp
     * @param title
     * @param expanded
     * @param discription
     * @return
     */
    protected Section createSection(final ScrolledForm form, Composite parent, String title, boolean expanded, String description) {
        final int style = (description == null) ? (Section.TWISTIE | Section.TITLE_BAR)
                : (Section.DESCRIPTION | Section.TWISTIE | Section.TITLE_BAR);
        Section section = toolkit.createSection(parent, style);

        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

        section.addExpansionListener(new ExpansionAdapter() {

            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
            }

        });

        section.setText(title);
        section.setDescription(description);
        section.setExpanded(expanded);
        return section;
    }

    public abstract void setDirty(boolean isDirty);

    @Override
    public boolean isDirty() {
        return super.isDirty() || isDirty;
    }
}
