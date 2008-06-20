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
package org.talend.dataprofiler.core.ui.editor.pattern;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataprofiler.core.ui.editor.AbstractFormPage;

/**
 * DOC rli class global comment. Detailled comment
 */
public class PatternMasterDetailsPage extends AbstractFormPage implements PropertyChangeListener {

    public PatternMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();
        metadataSection.setText("Pattern Metadata");
        metadataSection.setDescription("Set the properties of pattern.");
        creatPatternDefinition(form, topComp);
    }

    private void creatPatternDefinition(ScrolledForm form, Composite topComp) {
        Section section = createSection(form, topComp, "Pattern Definition", false, null);
        Composite sectionComp = toolkit.createComposite(section);
        topComp.setLayout(new GridLayout());
        Composite expressComp = new Composite(sectionComp, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(expressComp);
        expressComp.setLayout(new GridLayout(3, false));
        Combo combo = new Combo(expressComp, SWT.NONE);
        GridData gd = new GridData();
        gd.widthHint = 20;
        combo.setLayoutData(gd);

        Text patternText = new Text(expressComp, SWT.BORDER);
        gd = new GridData();
        gd.widthHint = 40;
        patternText.setLayoutData(gd);

        Button delButton = new Button(expressComp, SWT.NONE);
        gd = new GridData();
        gd.widthHint = 20;
        delButton.setLayoutData(gd);
    }

    @Override
    protected void fireTextChange() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initMetaTextFied() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDirty(boolean isDirty) {
        // TODO Auto-generated method stub

    }

    public void propertyChange(PropertyChangeEvent arg0) {
        // TODO Auto-generated method stub

    }
}
