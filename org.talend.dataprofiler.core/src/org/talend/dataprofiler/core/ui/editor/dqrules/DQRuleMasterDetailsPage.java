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
package org.talend.dataprofiler.core.ui.editor.dqrules;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.views.WhereClauseDND;
import org.talend.dataquality.rules.JoinElement;
import org.talend.dataquality.rules.RulesFactory;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class DQRuleMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private static final String WHERE = "WHERE";

    private static final int CRITICALITY_LEVEL_MIN = 1;

    private static final int CRITICALITY_LEVEL_MAX = 10;

    private static final String DEFAULT_OPERATOR = "=";

    private static final String[] OPERATORS = { "=", ">", "<", ">=", "<=" };

    final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

    private WhereRule whereRule;

    private ScrolledForm form;

    private Section dqRuleDefinitionSection;

    private Section joinConditionSection;

    private Text whereText;

    private Text criticalityLevelText;

    private List<JoinElement> tempJoinElements;

    private Composite joinElementComp;

    public Text getCriticalityLevelText() {
        return criticalityLevelText;
    }

    public Text getWhereText() {
        return whereText;
    }

    /**
     * DOC xqliu DQRuleMasterDetailsPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public DQRuleMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        this.whereRule = DQRuleResourceFileHelper.getInstance().findWhereRule(input.getFile());
        return whereRule;
    }

    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((DQRuleEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((DQRuleEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        form = managedForm.getForm();
        form.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.dqRuleSettings")); //$NON-NLS-1$

        metadataSection.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.DQRuleMetadata")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.setProperties")); //$NON-NLS-1$

        resetJoinElements();

        createDQRuleDefinitionSection(topComp);
        createJoinConditionSection(topComp);

        currentEditor.registerSections(new Section[] { dqRuleDefinitionSection, joinConditionSection });

    }

    /**
     * DOC xqliu Comment method "creatDQRuleDefinitionSection".
     * 
     * @param comp
     */
    private void createDQRuleDefinitionSection(Composite comp) {
        dqRuleDefinitionSection = createSection(form, comp, DefaultMessagesImpl
                .getString("DQRuleMasterDetailsPage.dqRuleDefinition"), false, null); //$NON-NLS-1$

        Label label = new Label(dqRuleDefinitionSection, SWT.WRAP);
        label.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.text")); //$NON-NLS-1$
        dqRuleDefinitionSection.setDescriptionControl(label);

        createDQRuleDefinitionComp();
    }

    /**
     * DOC xqliu Comment method "createDQRuleDefinitionComp".
     * 
     * @return
     */
    private Composite createDQRuleDefinitionComp() {

        Composite newComp = toolkit.createComposite(dqRuleDefinitionSection);
        newComp.setLayout(new GridLayout());

        Composite container = new Composite(newComp, SWT.NONE);
        GridLayout gdLayout = new GridLayout(2, false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(container);
        container.setLayout(gdLayout);

        Label label1 = new Label(container, SWT.NONE);
        label1.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.criticalityLevel"));

        GridData data;
        criticalityLevelText = new Text(container, SWT.BORDER);
        criticalityLevelText.setText("" + whereRule.getCriticalityLevel());
        data = new GridData(GridData.FILL_HORIZONTAL);
        criticalityLevelText.setLayoutData(data);

        Label label2 = new Label(container, SWT.NONE);
        label2.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.whereClause"));
        label2.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        whereText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        WhereClauseDND.installDND(whereText);
        whereText.setText(whereRule.getWhereExpression());
        data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 180;
        whereText.setLayoutData(data);

        addModifyListeners();

        dqRuleDefinitionSection.setClient(newComp);
        return newComp;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        if (saveDQRule()) {
            this.isDirty = false;
        }
    }

    /**
     * DOC xqliu Comment method "saveDQRule".
     * 
     * @return
     */
    private boolean saveDQRule() {
        boolean ret = false;
        if (checkValus()) {
            TaggedValueHelper.setValidStatus(true, whereRule);
            whereRule.setCriticalityLevel(Integer.valueOf(getCriticalityLevelText().getText()));
            whereRule.setWhereExpression(whereText.getText());
            whereRule.getJoins().clear();
            whereRule.getJoins().addAll(cleanJoins(tempJoinElements));
            EMFUtil.saveSingleResource(whereRule.eResource());
            ret = true;
        }
        return ret;

    }

    /**
     * DOC xqliu Comment method "cleanJoins".
     * 
     * @param joinElements
     * @return
     */
    private List<JoinElement> cleanJoins(List<JoinElement> joinElements) {
        List<JoinElement> retJoinElements = new ArrayList<JoinElement>();
        for (JoinElement je : joinElements) {
            if (je.getColA() != null && je.getColB() != null) {
                retJoinElements.add(je);
            }
        }
        return retJoinElements;
    }

    /**
     * DOC xqliu Comment method "checkValus".
     * 
     * @return
     */
    private boolean checkValus() {
        boolean ret = true;
        String msg = "";
        String cl = getCriticalityLevelText().getText();
        if (cl == null || "".equals(cl)) {
            ret = false;
            msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.criticalityLevelIsEmpty") + "\n";
        } else {
            try {
                int i = Integer.valueOf(cl).intValue();
                if (!(i >= CRITICALITY_LEVEL_MIN && i <= CRITICALITY_LEVEL_MAX)) {
                    ret = false;
                    msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.criticalityLevelIsInvalid") + "\n";
                }
            } catch (NumberFormatException e) {
                ret = false;
                msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.criticalityLevelIsInvalid") + "\n";
            }
        }
        String wh = getWhereText().getText();
        if (wh == null || "".equals(wh)) {
            ret = false;
            msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.whereClauseIsEmpty") + "\n";
        } else {
            if (wh.startsWith(WHERE)) {
                ret = false;
                msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.whereClauseIsInvalid") + "\n";
            }
        }
        if (!ret) {
            MessageDialog.openWarning(null, DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.warning"), msg); //$NON-NLS-1$
        }
        return ret;
    }

    /**
     * DOC xqliu Comment method "addModifyListeners".
     */
    protected void addModifyListeners() {
        whereText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });
        criticalityLevelText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });
    }

    /**
     * DOC xqliu Comment method "createJoinConditionSection".
     * 
     * @param comp
     */
    private void createJoinConditionSection(Composite comp) {
        joinConditionSection = createSection(form, comp, DefaultMessagesImpl
                .getString("DQRuleMasterDetailsPage.joinCondition"), false, null); //$NON-NLS-1$

        Label label = new Label(joinConditionSection, SWT.WRAP);
        label.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.textJoinCondition")); //$NON-NLS-1$
        joinConditionSection.setDescriptionControl(label);

        createJoinConditionComp();
    }

    /**
     * DOC xqliu Comment method "createJoinConditionComp".
     * 
     * @return
     */
    private Composite createJoinConditionComp() {

        Composite newComp = toolkit.createComposite(joinConditionSection);
        newComp.setLayout(new GridLayout());

        joinElementComp = new Composite(newComp, SWT.NONE);
        joinElementComp.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(joinElementComp);
        for (int i = 0; i < tempJoinElements.size(); i++) {
            creatNewJoinElementLine(tempJoinElements.get(i));
        }
        createAddButton(newComp);

        joinConditionSection.setClient(newComp);
        return newComp;
    }

    /**
     * DOC xqliu Comment method "createAddButton".
     * 
     * @param parent top composite
     */
    private void createAddButton(Composite parent) {
        final Button addButton = new Button(parent, SWT.NONE);
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setToolTipText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.add")); //$NON-NLS-1$
        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.CENTER;
        labelGd.widthHint = 65;
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                JoinElement newJoinElement = RulesFactory.eINSTANCE.createJoinElement();
                newJoinElement.setOperator(DEFAULT_OPERATOR);
                creatNewJoinElementLine(newJoinElement);
                tempJoinElements.add(newJoinElement);
                form.reflow(true);
                setDirty(true);
            }
        });
    }

    /**
     * DOC xqliu Comment method "creatNewJoinElementLine".
     * 
     * @param joinElement
     */
    private void creatNewJoinElementLine(JoinElement joinElement) {
        final Composite expressComp = new Composite(joinElementComp, SWT.NONE);
        expressComp.setLayout(new GridLayout(6, false));
        final JoinElement fje = joinElement;
        boolean flag = false;
        if (fje.getColA() != null && fje.getColB() != null && fje.getOperator() != null) {
            flag = true;
        }

        final Label labelL = new Label(expressComp, SWT.NONE);
        labelL.setImage(ImageLib.getImage(ImageLib.TD_COLUMN));
        GridDataFactory.fillDefaults().span(1, 1).grab(false, false).applyTo(labelL);

        final Text textL = new Text(expressComp, SWT.BORDER);
        textL.setEditable(false);
        textL.setText(flag ? fje.getColA().getName() : PluginConstant.EMPTY_STRING);
        GridDataFactory.fillDefaults().span(1, 1).grab(true, false).applyTo(textL);
        ((GridData) textL.getLayoutData()).widthHint = 100;

        DropTarget targetL = new DropTarget(textL, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
        targetL.setTransfer(new Transfer[] { transfer });
        targetL.addDropListener(new ColumnDropTargetListener(fje, ColumnDropTargetListener.LEFT));

        final CCombo combo = new CCombo(expressComp, SWT.BORDER);
        combo.setEditable(false);
        combo.setItems(OPERATORS);
        combo.setText(flag ? fje.getOperator() : DEFAULT_OPERATOR);
        GridDataFactory.fillDefaults().span(1, 1).grab(false, false).applyTo(combo);
        combo.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                fje.setOperator(combo.getText());
                setDirty(true);
            }
        });

        final Label labelR = new Label(expressComp, SWT.NONE);
        labelR.setImage(ImageLib.getImage(ImageLib.TD_COLUMN));
        GridDataFactory.fillDefaults().span(1, 1).grab(false, false).applyTo(labelR);

        final Text textR = new Text(expressComp, SWT.BORDER);
        textR.setEditable(false);
        textR.setText(flag ? fje.getColB().getName() : PluginConstant.EMPTY_STRING);
        GridDataFactory.fillDefaults().span(1, 1).grab(true, false).applyTo(textR);
        ((GridData) textR.getLayoutData()).widthHint = 100;

        DropTarget targetR = new DropTarget(textR, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
        targetR.setTransfer(new Transfer[] { transfer });
        targetR.addDropListener(new ColumnDropTargetListener(fje, ColumnDropTargetListener.RIGHT));

        Button delButton = new Button(expressComp, SWT.NONE);
        delButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delButton.setToolTipText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.delete")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().span(1, 1).grab(false, false).applyTo(delButton);
        delButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                tempJoinElements.remove(fje);
                expressComp.dispose();
                joinElementComp.layout();
                form.reflow(true);
                setDirty(true);
            }
        });

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(expressComp);
    }

    private void resetJoinElements() {
        if (tempJoinElements == null) {
            tempJoinElements = new ArrayList<JoinElement>();
        } else {
            tempJoinElements.clear();
        }
        tempJoinElements.addAll(whereRule.getJoins());
    }

    /**
     * DOC xqliu DQRuleMasterDetailsPage class global comment. Detailled comment
     */
    private class ColumnDropTargetListener implements DropTargetListener {

        private JoinElement joinElement;

        private int index;

        public static final int LEFT = 1;

        public static final int RIGHT = 2;

        public JoinElement getJoinElement() {
            if (joinElement == null) {
                joinElement = RulesFactory.eINSTANCE.createJoinElement();
                joinElement.setOperator(DEFAULT_OPERATOR);
            }
            return joinElement;
        }

        public int getIndex() {
            if (index != LEFT && index != RIGHT) {
                index = LEFT;
            }
            return index;
        }

        public ColumnDropTargetListener(JoinElement je, int index) {
            super();
            this.joinElement = je;
            this.index = index;
        }

        public void dragEnter(DropTargetEvent event) {
            if (event.detail == DND.DROP_DEFAULT)
                event.detail = DND.DROP_COPY;
        }

        public void dragOver(DropTargetEvent event) {
            event.feedback = DND.FEEDBACK_NONE;
        }

        public void dragOperationChanged(DropTargetEvent event) {
            if (event.detail == DND.DROP_DEFAULT)
                event.detail = DND.DROP_COPY;
        }

        public void dragLeave(DropTargetEvent event) {
        }

        public void dropAccept(DropTargetEvent event) {
        }

        public void drop(DropTargetEvent event) {
            if (transfer.isSupportedType(event.currentDataType)) {
                if (event.data instanceof TreeSelection) {
                    TreeSelection ts = (TreeSelection) event.data;
                    if (ts.getFirstElement() instanceof Column) {
                        Column column = (Column) ts.getFirstElement();
                        setColumn(column);

                        DropTarget target = (DropTarget) event.widget;
                        Text text = (Text) target.getControl();
                        text.setText(column.getName());

                        setDirty(true);
                    }
                }
            }
        }

        public void setColumn(Column column) {
            switch (getIndex()) {
            case LEFT:
                getJoinElement().setColA(column);
                // MOD scorreia 2009-04-08 removed alias setting
                // getJoinElement().setColumnAliasA(column.getName());
                // getJoinElement().setTableAliasA(ColumnHelper.getColumnSetOwner(column).getName());
                break;
            case RIGHT:
                getJoinElement().setColB(column);
                // MOD scorreia 2009-04-08 removed alias setting
                // getJoinElement().setColumnAliasB(column.getName());
                // getJoinElement().setTableAliasB(ColumnHelper.getColumnSetOwner(column).getName());
                break;
            default:
            }
        }
    }
}
