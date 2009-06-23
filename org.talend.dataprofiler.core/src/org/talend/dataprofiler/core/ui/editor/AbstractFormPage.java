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

	public AbstractFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		this.toolkit = this.getEditor().getToolkit();
	}

	/**
	 * MOD mzhao 2009-06-17 feature 5887
	 * 
	 * @param form
	 * @param toolkit
	 * @param anasisDataComp
	 * @param title
	 * @param expanded
	 * @param discription
	 * @return
	 */
	public Section createSection(final ScrolledForm form, Composite parent,
			String title, boolean expanded, String description) {
		final int style = (description == null) ? (Section.TWISTIE | Section.TITLE_BAR)
				: (Section.DESCRIPTION | Section.TWISTIE | Section.TITLE_BAR);
		Section section = toolkit.createSection(parent, style);

		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING));

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
	
	protected int foldingSections(Section[] sections) {
        int foldType = EditorPreferencePage.getCurrentFolding();
        if (sections != null && sections.length > 0) {
            for (int i = 0; i < sections.length; ++i) {
                switch (foldType) {
                case EditorPreferencePage.FOLDING_1:
                    sections[i].setExpanded(true);
                    break;
                case EditorPreferencePage.FOLDING_2:
                    sections[i].setExpanded(false);
                    break;
                default: // EditorPreferencePage.FOLDING_3
                    if (i == 0) {
                        sections[i].setExpanded(true);
                    } else {
                        sections[i].setExpanded(false);
                    }
                }
            }
            return foldType;
        }
        return 0;
    }
	
}
