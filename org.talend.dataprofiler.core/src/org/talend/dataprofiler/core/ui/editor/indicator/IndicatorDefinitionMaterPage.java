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
package org.talend.dataprofiler.core.ui.editor.indicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.PatternLanguageType;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorDefinitionMaterPage extends AbstractMetadataFormPage {

    private Composite definitionComp;

    private List<String> allDBTypeList;

    private List<String> remainDBTypeList;

    private List<Expression> tempExpression;

    private Composite expressionComp;

    /**
     * DOC bZhou IndicatorDefinitionMaterPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public IndicatorDefinitionMaterPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor
     * )
     */
    @Override
    public void initialize(FormEditor editor) {
        super.initialize(editor);
        String[] supportTypes = PatternLanguageType.getAllLanguageTypes();
        allDBTypeList = new ArrayList<String>();
        allDBTypeList.addAll(Arrays.asList(supportTypes));

        remainDBTypeList = new ArrayList<String>();
        remainDBTypeList.addAll(allDBTypeList);

        if (tempExpression == null) {
            tempExpression = new ArrayList<Expression>();
        } else {
            tempExpression.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#createFormContent(org.eclipse.ui.forms.IManagedForm
     * )
     */
    @Override
    protected void createFormContent(IManagedForm managedForm) {
        setFormTitle("Indicator Definition");
        setMetadataTitle("Indicator Metadata");
        super.createFormContent(managedForm);

        creatDefinitionSection(topComp);
    }

    private void creatDefinitionSection(Composite topCmp) {
        Section definitionSection = createSection(form, topCmp, "Indicator Definition", false, null);

        Label label = new Label(definitionSection, SWT.WRAP);
        label.setText("This section is for indicator definition.");
        definitionSection.setDescriptionControl(label);

        definitionComp = createDefinitionComp(definitionSection);

        definitionSection.setClient(definitionComp);
    }

    /**
     * DOC bZhou Comment method "createPatternDefinitionComp".
     * 
     * @param definitionSection
     * 
     * @return
     */
    private Composite createDefinitionComp(Composite definitionSection) {
        Composite composite = toolkit.createComposite(definitionSection);
        composite.setLayout(new GridLayout());

        expressionComp = new Composite(composite, SWT.NONE);
        expressionComp.setLayout(new GridLayout());
        expressionComp.setLayoutData(new GridData(GridData.FILL_BOTH));

        IndicatorDefinition definition = (IndicatorDefinition) getCurrentModelElement(getEditor());
        if (definition != null) {
            EList<Expression> expressions = definition.getSqlGenericExpression();
            for (Expression expression : expressions) {
                tempExpression.add(expression);
                creatNewExpressLine(expression);
            }
        }

        createAddButton(composite);

        return composite;
    }

    /**
     * DOC bZhou Comment method "creatNewExpressLine".
     * 
     * @param expression
     */
    private void creatNewExpressLine(final Expression expression) {
        final Composite expressComp = new Composite(expressionComp, SWT.NONE);
        expressComp.setLayout(new GridLayout(2, false));
        final CCombo combo = new CCombo(expressComp, SWT.BORDER);
        combo.setLayoutData(new GridData());
        ((GridData) combo.getLayoutData()).widthHint = 150;

        combo.setEditable(false);
        combo.setItems(remainDBTypeList.toArray(new String[remainDBTypeList.size()]));

        String language = expression.getLanguage();
        String body = expression.getBody();

        if (language == null) {
            combo.setText(remainDBTypeList.get(0));
        } else {
            combo.setText(PatternLanguageType.findNameByLanguage(language));
        }

        combo.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                String lang = combo.getText();
                expression.setLanguage(PatternLanguageType.findLanguageByName(lang));
                setDirty(true);
            }
        });
        final Text patternText = new Text(expressComp, SWT.BORDER);
        patternText.setText(body == null ? PluginConstant.EMPTY_STRING : body);
        patternText.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridData) patternText.getLayoutData()).widthHint = 600;
        patternText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                expression.setBody(patternText.getText());
                setDirty(true);
            }

        });

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(expressComp);
    }

    private void createAddButton(final Composite parent) {
        final Button addButton = new Button(parent, SWT.NONE);
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setToolTipText(DefaultMessagesImpl.getString("PatternMasterDetailsPage.add")); //$NON-NLS-1$
        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.CENTER;
        labelGd.widthHint = 65;
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                remainDBTypeList.clear();
                remainDBTypeList.addAll(allDBTypeList);
                for (Expression expression : tempExpression) {
                    String language = expression.getLanguage();
                    String languageName = PatternLanguageType.findNameByLanguage(language);
                    remainDBTypeList.remove(languageName);
                }
                if (remainDBTypeList.size() == 0) {
                    MessageDialog
                            .openWarning(
                                    null,
                                    DefaultMessagesImpl.getString("PatternMasterDetailsPage.warning"), DefaultMessagesImpl.getString("PatternMasterDetailsPage.patternExpression")); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                String language = PatternLanguageType.findLanguageByName(remainDBTypeList.get(0));
                Expression expression = BooleanExpressionHelper.createExpression(language, null);
                creatNewExpressLine(expression);
                tempExpression.add(expression);
                form.reflow(true);
                setDirty(true);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#getCurrentModelElement(org.eclipse.ui.forms.editor
     * .FormEditor)
     */
    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        if (editor.getEditorInput() instanceof IndicatorEditorInput) {
            IndicatorEditorInput editorInput = (IndicatorEditorInput) editor.getEditorInput();
            return editorInput.getIndicatorDefinition();
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((IndicatorEditor) getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
            firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        // IndicatorDefinition definition = (IndicatorDefinition) getCurrentModelElement(getEditor());
        // if (definition != null) {
        // EMFUtil.saveSingleResource(definition.eResource());
        // }

        IndicatorDefinition definition = (IndicatorDefinition) getCurrentModelElement(getEditor());

        EList<Expression> expressiones = definition.getSqlGenericExpression();

        expressiones.clear();

        for (Expression expression : tempExpression) {
            if (expression.getBody() != null && !"".equals(expression.getBody())) {
                expressiones.add(expression);
            }
        }

        DefinitionHandler.getInstance().saveResource();

        super.doSave(monitor);

        this.isDirty = false;
    }

}
