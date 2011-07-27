// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.parserrules;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.rules.ParserRule;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.nodes.RuleRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class ParserRuleMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

    private ParserRule parserRule;

    protected RuleRepNode ruleRepNode;

    protected TDQBusinessRuleItem parserRuleItem;

    private ScrolledForm form;

    private Section parserRuleDefinitionSection;

    private ParserRuleTableViewer parserRuleTableViewer;

    public TDQBusinessRuleItem getParserRuleItem() {
        if (this.parserRuleItem == null) {
            if (this.parserRule != null) {
                initRuleRepNode(this.parserRule);
            }
        }
        return this.parserRuleItem;

    }

    public void setParserRuleItem(TDQBusinessRuleItem parserRuleItem) {
        this.parserRuleItem = parserRuleItem;
    }

    public RuleRepNode getRuleRepNode() {
        return this.ruleRepNode;
    }

    private void initRuleRepNode(ParserRule parserRule) {
        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(parserRule);
        if (recursiveFind != null && recursiveFind instanceof RuleRepNode) {
            this.ruleRepNode = (RuleRepNode) recursiveFind;
            this.parserRuleItem = (TDQBusinessRuleItem) this.ruleRepNode.getObject().getProperty().getItem();
        } else {
            Property property = PropertyHelper.getProperty(parserRule);
            this.parserRuleItem = (TDQBusinessRuleItem) property.getItem();
        }
    }

    public ParserRuleMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput instanceof FileEditorInput) {
            FileEditorInput input = (FileEditorInput) editor.getEditorInput();
            this.currentModelElement = DQRuleResourceFileHelper.getInstance().findDQRule(input.getFile());
        } else if (editorInput instanceof ParserRuleItemEditorInput) {
            ParserRuleItemEditorInput input = (ParserRuleItemEditorInput) editor.getEditorInput();
            this.currentModelElement = input.getTDQBusinessRuleItem().getDqrule();
        }
        parserRule = (ParserRule) this.currentModelElement;
        initRuleRepNode(parserRule);
        return parserRule;
    }

    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((ParserRuleEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
            // MOD klliu 2009-06-25 bug 7687
            this.firePropertyChange(IEditorPart.PROP_DIRTY);
            // ~
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((ParserRuleEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        form = managedForm.getForm();
        form.setText("Parser Rule Settings"); //$NON-NLS-1$
        metadataSection.setText("Parser Rule Metadata"); //$NON-NLS-1$
        metadataSection.setDescription("Set Properties of Parser Rule."); //$NON-NLS-1$
        createParserRuleDefinitionSection(topComp);
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        if (!checkWhithspace()) {
            MessageUI.openError(DefaultMessagesImpl.getString("AbstractMetadataFormPage.whitespace")); //$NON-NLS-1$
            return;
        }

        super.doSave(monitor);
        if (saveParserRule()) {
            this.isDirty = false;
        }
    }

    private boolean saveParserRule() {
        TaggedValueHelper.setValidStatus(true, parserRule);
        // parserRule.getExpression().clear();
        parserRule.getExpression().addAll(parserRuleTableViewer.getParserRuleTdExpressions());
        TDQBusinessRuleItem parserRuleItem = this.getParserRuleItem();
        parserRuleItem.setDqrule(parserRule);
        ReturnCode rc = ElementWriterFactory.getInstance().createdRuleWriter().save(parserRuleItem);

        return rc.isOk();

    }

    /**
     * DOC klliu Comment method "createJoinConditionSection".
     * 
     * @param comp
     */
    private void createParserRuleDefinitionSection(Composite comp) {
        parserRuleDefinitionSection = createSection(form, comp, "Parser Rule", null); //$NON-NLS-1$

        Label label = new Label(parserRuleDefinitionSection, SWT.WRAP);
        label.setText("Type in the definition of your Parser Rules."); //$NON-NLS-1$
        parserRuleDefinitionSection.setDescriptionControl(label);
        createParserRuleDefinitionComp();
    }

    /**
     * DOC klliu Comment method "createJoinConditionComp".
     * 
     * @return
     */
    private Composite createParserRuleDefinitionComp() {
        Composite newComp = toolkit.createComposite(parserRuleDefinitionSection);
        newComp.setLayout(new GridLayout());
        Composite container = new Composite(newComp, SWT.NONE);
        GridLayout gdLayout = new GridLayout(2, false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(container);
        container.setLayout(gdLayout);
        Label label1 = new Label(container, SWT.NONE);
        label1.setText("Parser Rules"); //$NON-NLS-1$
        Composite ruleViewerComp = new Composite(container, SWT.None);
        GridData parData = new GridData(GridData.FILL_BOTH);
        ruleViewerComp.setLayoutData(parData);
        GridLayout layout = new GridLayout(1, false);
        ruleViewerComp.setLayout(layout);

        createParserRuleViewColumns(ruleViewerComp);
        parserRuleDefinitionSection.setClient(newComp);
        return newComp;
    }

    /**
     * DOC klliu Comment method "createParserRuleViewColumns".
     * 
     * @param parView
     */
    private void createParserRuleViewColumns(Composite ruleViewerComp) {
        parserRuleTableViewer = new ParserRuleTableViewer(ruleViewerComp, this);
        parserRuleTableViewer.setDirty(false);
        createButtons(ruleViewerComp, parserRuleTableViewer);
    }

    /**
     * DOC klliu Comment method "createButtons".
     * 
     * @param parent
     */
    private void createButtons(Composite parent, final ParserRuleTableViewer parserRuleTableViewer) {
        Composite buttonsComposite = new Composite(parent, SWT.NONE);
        buttonsComposite.setLayout(new GridLayout(6, true));

        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.LEFT;
        labelGd.widthHint = 30;

        final Button addButton = new Button(buttonsComposite, SWT.NONE);
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                parserRuleTableViewer.addTdExpression();
            }
        });

        final Button delButton = new Button(buttonsComposite, SWT.NONE);
        delButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delButton.setLayoutData(labelGd);
        delButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                TdExpression tdExpression = (TdExpression) ((IStructuredSelection) parserRuleTableViewer.getSelection())
                        .getFirstElement();
                if (tdExpression != null) {
                    parserRuleTableViewer.removeTdExpression(tdExpression);
                }
            }
        });
        final Button upButton = new Button(buttonsComposite, SWT.NONE);
        upButton.setImage(ImageLib.getImage(ImageLib.UP_ACTION));
        upButton.setLayoutData(labelGd);
        upButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                TdExpression tdExpression = (TdExpression) ((IStructuredSelection) parserRuleTableViewer.getSelection())
                        .getFirstElement();
                if (tdExpression != null) {
                    parserRuleTableViewer.moveTdExpression(tdExpression, 1);
                }
            }
        });

        final Button downButton = new Button(buttonsComposite, SWT.NONE);
        downButton.setImage(ImageLib.getImage(ImageLib.DOWN_ACTION));
        downButton.setLayoutData(labelGd);
        downButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                TdExpression tdExpression = (TdExpression) ((IStructuredSelection) parserRuleTableViewer.getSelection())
                        .getFirstElement();
                if (tdExpression != null) {
                    parserRuleTableViewer.moveTdExpression(tdExpression, -1);
                }
            }
        });
        final Button copyButton = new Button(buttonsComposite, SWT.NONE);
        copyButton.setImage(ImageLib.getImage(ImageLib.COPY_ACTION));
        copyButton.setLayoutData(labelGd);
        copyButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                List<TdExpression> listTdExpression = ((IStructuredSelection) parserRuleTableViewer.getSelection()).toList();
                parserRuleTableViewer.copyTdExpression(listTdExpression);

            }
        });

        final Button pasteButton = new Button(buttonsComposite, SWT.NONE);
        pasteButton.setImage(ImageLib.getImage(ImageLib.PASTE_ACTION));
        pasteButton.setLayoutData(labelGd);
        pasteButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                parserRuleTableViewer.pasteTdExpression();

            }
        });
    }

}
