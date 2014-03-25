// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.talend.dataquality.record.linkage.ui.composite.utils.ImageLib;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.rules.MatchRule;

/**
 * this class is to create a dialog to Edit and Sort Match Rule Name.
 * 
 */
public class EditSortMatchRuleNamesDialog extends Dialog {

    private TableViewer tableViewer;

    // the beginning and final data
    private List<MatchRule> matchRuleList;

    // a temp hashtable used to store data. and at last read data from it to set to matchRuleList
    private Hashtable<Integer, MatchRule> hashtable = new Hashtable<Integer, MatchRule>();

    // a tempList used to store data.
    List<MatchRule> tempList = new ArrayList<MatchRule>();

    private Button upButton;

    private Button downButton;

    // will be set to true, only the names or the order is changed.
    private boolean isDirty = false;

    /**
     * Sets the isDirty.
     * 
     * @param isDirty the isDirty to set
     */
    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    /**
     * Getter for isDirty.
     * 
     * @return the isDirty
     */
    public boolean isDirty() {
        return isDirty;
    }

    /**
     * EditSortMatchRuleNamesDialog constructor.
     * 
     * @param parentShell
     * @param matchRuleList
     */
    public EditSortMatchRuleNamesDialog(Shell parentShell, List<MatchRule> matchRuleList) {
        super(parentShell);
        setShellStyle(SWT.CLOSE | SWT.RESIZE);
        this.matchRuleList = matchRuleList;

        if (matchRuleList != null) {
            int i = 0;
            for (MatchRule matchRule : matchRuleList) {
                hashtable.put(i++, matchRule);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        GridLayout layout = new GridLayout(1, false);

        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        createContentComposite(container);

        return container;
    }

    /**
     * create Content Composite.
     * 
     * @param parent
     */
    private void createContentComposite(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_composite.heightHint = 285;
        composite.setLayoutData(gd_composite);
        composite.setLayout(new GridLayout(1, true));

        createTableViewer(composite);

        createMoveButtons(composite);

        composite.pack();
        parent.pack();
    }

    /**
     * create up/down Buttons.
     * 
     * @param parent
     */
    public void createMoveButtons(Composite parent) {
        Composite buttonsComposite = new Composite(parent, SWT.RIGHT);
        buttonsComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        buttonsComposite.setLayout(new GridLayout(2, true));

        GridData labelGd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        labelGd.horizontalAlignment = SWT.LEFT;
        labelGd.widthHint = 30;

        upButton = new Button(buttonsComposite, SWT.NONE);
        upButton.setToolTipText(DefaultMessagesImpl.getString("AbstractMatchAnaysisTableSection.MoveUp_button_tooltip")); //$NON-NLS-1$
        upButton.setImage(ImageLib.getImage(ImageLib.UP_ACTION));
        upButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                moveItemUpOrDown(true);
            }
        });

        downButton = new Button(buttonsComposite, SWT.NONE);
        downButton.setToolTipText(DefaultMessagesImpl.getString("AbstractMatchAnaysisTableSection.MoveDown_button_tooltip")); //$NON-NLS-1$
        downButton.setImage(ImageLib.getImage(ImageLib.DOWN_ACTION));
        downButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                moveItemUpOrDown(false);
            }
        });

    }

    /**
     * create Table Viewer.
     * 
     * @param parent
     * @return
     */
    private void createTableViewer(Composite parent) {
        tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        Table table_1 = tableViewer.getTable();
        GridData gd_table_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_table_1.heightHint = 217;
        table_1.setLayoutData(gd_table_1);

        Table table = (Table) tableViewer.getControl();

        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnPixelData(350));
        table.setLayout(tableLayout);

        tableViewer.setContentProvider(new IStructuredContentProvider() {

            @Override
            public Object[] getElements(Object inputElement) {
                return (MatchRule[]) inputElement;
            }

            @Override
            public void dispose() {
                // do nothing here, until now
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                // do nothing here, until now
            }
        });

        createMatchRuleNameColumn();

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        tableViewer.setInput(matchRuleList.toArray(new MatchRule[matchRuleList.size()]));
    }

    /**
     * create Match Rule Name Column.
     */
    private void createMatchRuleNameColumn() {
        TableViewerColumn vNameColumn = new TableViewerColumn(tableViewer, SWT.CENTER);
        TableColumn nameColumn = vNameColumn.getColumn();
        nameColumn.setWidth(300);
        nameColumn.setText(DefaultMessagesImpl.getString("EditSortMatchRuleNamesDialog.columnName")); //$NON-NLS-1$

        vNameColumn.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((MatchRule) element).getName();
            }
        });

        vNameColumn.setEditingSupport(new EditingSupport(tableViewer) {

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor((Table) tableViewer.getControl());
            }

            @Override
            protected Object getValue(Object element) {
                return ((MatchRule) element).getName();
            }

            @Override
            protected void setValue(Object element, Object value) {
                // the modified value cant not be empty
                if (value != null && !StringUtils.isBlank((String) value)) {
                    if (!((MatchRule) element).getName().equals(value)) {
                        setDirty(true);
                    }
                    ((MatchRule) element).setName((String) value);
                } else {
                    ((MatchRule) element).setName(((MatchRule) element).getName());
                }
                tableViewer.refresh();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(DefaultMessagesImpl.getString("EditSortMatchRuleNamesDialog.Title")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * update the up/down Buttons whether is enable.
     */
    protected void updateButtonsStatus() {
        int index = tableViewer.getTable().getSelectionIndex();
        int size = tableViewer.getTable().getItemCount();
        upButton.setEnabled(size > 1 && index > 0);
        downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
    }

    /**
     * Move the selected item up or down.
     * 
     * @param isMoveUp <code>true</code> if move up, and <code>false</code> if move down
     */
    private void moveItemUpOrDown(boolean isMoveUp) {
        int selectIndex = tableViewer.getTable().getSelectionIndex();
        int targetIndex = isMoveUp ? selectIndex - 1 : selectIndex + 1;
        boolean isValidatedIndex = isMoveUp ? targetIndex >= 0 : targetIndex < tableViewer.getTable().getItemCount();

        // when the first one item is selected, can not move up. also when the last item is selected, can not move down.
        if (selectIndex >= 0 && isValidatedIndex) {
            // set the order to the hashtable
            if (hashtable != null && hashtable.size() > 0) {
                MatchRule oldTarget = hashtable.get(targetIndex);
                MatchRule oldSelect = hashtable.get(selectIndex);
                hashtable.put(selectIndex, oldTarget);
                hashtable.put(targetIndex, oldSelect);
            }

            tempList.clear();
            for (int i = 0; i < hashtable.size(); i++) {
                tempList.add(hashtable.get(i));
            }
            tableViewer.setInput(tempList.toArray(new MatchRule[tempList.size()]));
            tableViewer.refresh();

            updateButtonsStatus();

            setDirty(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        matchRuleList.clear();
        for (int i = 0; i < hashtable.size(); i++) {
            matchRuleList.add(hashtable.get(i));
        }
        super.okPressed();
    }

}
