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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.model.dburl.SupportDBUrlStore;
import org.talend.dataprofiler.core.ui.editor.AbstractFormPage;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.impl.RegularExpressionImpl;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * DOC rli class global comment. Detailled comment
 */
public class PatternMasterDetailsPage extends AbstractFormPage implements PropertyChangeListener {

    private static final String SQL = "SQL";

    private Pattern pattern;

    private String[] allDBTypes;

    public static final String ALL_DATABASE_TYPE = "ALL_DATABASE_TYPE";

    private Composite sectionComp;

    private Composite componentsComp;

    private List<PatternComponent> tempPatternComponents;

    public PatternMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        this.pattern = PatternResourceFileHelper.getInstance().findPattern(input.getFile());

        String[] supportTypes = SupportDBUrlStore.getInstance().getDBTypes();
        allDBTypes = new String[supportTypes.length + 1];
        System.arraycopy(supportTypes, 0, allDBTypes, 0, supportTypes.length);
        allDBTypes[supportTypes.length] = ALL_DATABASE_TYPE;
        if (tempPatternComponents == null) {
            tempPatternComponents = new ArrayList<PatternComponent>();
        } else {
            tempPatternComponents.clear();
        }
        tempPatternComponents.addAll(pattern.getComponents());
    }

    protected void createFormContent(IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        Composite body = form.getBody();
        form.setText("Pattern Settings");

        // TableWrapLayout layout = new TableWrapLayout();
        body.setLayout(new GridLayout());

        topComp = toolkit.createComposite(body);
        GridData anasisData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);

        topComp.setLayoutData(anasisData);
        topComp.setLayout(new GridLayout(1, false));
        metadataSection = creatMetadataSection(form, topComp);
        metadataSection.setText("Pattern Metadata");
        metadataSection.setDescription("Set the properties of pattern.");
        creatPatternDefinitionSection(form, topComp);
    }

    private void creatPatternDefinitionSection(ScrolledForm form, Composite topComp) {
        Section section = createSection(form, topComp, "Pattern Definition", false, null);
        sectionComp = toolkit.createComposite(section);
        sectionComp.setLayout(new GridLayout());
        componentsComp = new Composite(sectionComp, SWT.NONE);
        componentsComp.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(componentsComp);
        EList<PatternComponent> components = this.pattern.getComponents();
        for (int i = 0; i < components.size(); i++) {
            RegularExpressionImpl regularExpress = (RegularExpressionImpl) components.get(i);
            creatNewExpressLine(regularExpress);
        }
        createAddButton(form);

        section.setClient(sectionComp);

    }

    private void createAddButton(final ScrolledForm form) {
        final Button addButton = new Button(sectionComp, SWT.NONE);
        addButton.setText("ADD");
        addButton.setToolTipText("Add");
        GridData gdButton = new GridData();
        gdButton.horizontalAlignment = SWT.CENTER;
        gdButton.widthHint = 65;
        addButton.setLayoutData(gdButton);
        addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                Expression expression = CoreFactory.eINSTANCE.createExpression();
                expression.setLanguage(SQL);
                RegularExpressionImpl newRegularExpress = (RegularExpressionImpl) PatternFactory.eINSTANCE
                        .createRegularExpression();
                newRegularExpress.setExpression(expression);
                creatNewExpressLine(newRegularExpress);
                tempPatternComponents.add(newRegularExpress);
                form.reflow(true);
                setDirty(true);
            }
        });
    }

    private void creatNewExpressLine(RegularExpressionImpl regularExpress) {
        final Composite expressComp = new Composite(componentsComp, SWT.NONE);
        expressComp.setLayout(new GridLayout(10, true));
        final CCombo combo = new CCombo(expressComp, SWT.BORDER);
        combo.setEditable(false);
        combo.setItems(allDBTypes);
        final RegularExpressionImpl finalRegExpress = regularExpress;
        String language = regularExpress.getExpression().getLanguage();
        if (language == null) {
            combo.setText(ALL_DATABASE_TYPE);
        } else {
            combo.setText(language.equalsIgnoreCase(SQL) ? ALL_DATABASE_TYPE : language);
        }
        // GridData gd = new GridData();
        // gd.widthHint = 120;
        // combo.setLayoutData(gd);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(combo);
        combo.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                String language = combo.getText().equalsIgnoreCase(ALL_DATABASE_TYPE) ? SQL : combo.getText();
                finalRegExpress.getExpression().setLanguage(language);
                setDirty(true);
            }
        });
        final Text patternText = new Text(expressComp, SWT.BORDER);
        patternText.setText(regularExpress.getExpression().getBody() == null ? PluginConstant.EMPTY_STRING : regularExpress
                .getExpression().getBody());
        // gd = new GridData();
        // gd.widthHint = 350;
        // patternText.setLayoutData(gd);
        GridDataFactory.fillDefaults().span(7, 1).grab(true, false).applyTo(patternText);
        patternText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                finalRegExpress.getExpression().setBody(patternText.getText());
                setDirty(true);
            }

        });
        Button delButton = new Button(expressComp, SWT.NONE);
        delButton.setText("DEL");
        delButton.setToolTipText("Delete");
        // gd = new GridData();
        // gd.widthHint = 40;
        // delButton.setLayoutData(gd);
        GridDataFactory.fillDefaults().span(1, 1).grab(true, false).applyTo(delButton);
        delButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                tempPatternComponents.remove(finalRegExpress);
                expressComp.dispose();
                sectionComp.layout();
                setDirty(true);
            }
        });
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(expressComp);
    }

    @Override
    protected void fireTextChange() {
        pattern.setName(nameText.getText());
        TaggedValueHelper.setPurpose(purposeText.getText(), pattern);
        TaggedValueHelper.setDescription(descriptionText.getText(), pattern);
        TaggedValueHelper.setAuthor(pattern, authorText.getText());
        TaggedValueHelper.setDevStatus(pattern, DevelopmentStatus.get(statusCombo.getText()));
    }

    @Override
    protected void initMetaTextFied() {
        nameText.setText(pattern.getName() == null ? PluginConstant.EMPTY_STRING : pattern.getName());
        purposeText.setText(TaggedValueHelper.getPurpose(pattern) == null ? PluginConstant.EMPTY_STRING : TaggedValueHelper
                .getPurpose(pattern));
        descriptionText.setText(TaggedValueHelper.getDescription(pattern) == null ? PluginConstant.EMPTY_STRING
                : TaggedValueHelper.getDescription(pattern));
        authorText.setText(TaggedValueHelper.getAuthor(pattern) == null ? PluginConstant.EMPTY_STRING : TaggedValueHelper
                .getAuthor(pattern));
        statusCombo.setText(TaggedValueHelper.getDevStatus(pattern) == null ? PluginConstant.EMPTY_STRING : TaggedValueHelper
                .getDevStatus(pattern).getLiteral());
    }

    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((PatternEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((PatternEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        savePattern();
        this.isDirty = false;
    }

    private void savePattern() {
        this.pattern.getComponents().clear();
        this.pattern.getComponents().addAll(tempPatternComponents);
        EMFUtil.saveSingleResource(pattern.eResource());

    }
}
