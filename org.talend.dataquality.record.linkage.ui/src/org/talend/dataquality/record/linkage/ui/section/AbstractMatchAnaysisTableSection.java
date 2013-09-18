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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.ui.action.RefreshChartAction;
import org.talend.dataquality.record.linkage.ui.composite.utils.ImageLib;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.rules.KeyDefinition;

/**
 * created by zshen on Jul 31, 2013 Detailled comment
 * 
 */
public abstract class AbstractMatchAnaysisTableSection extends AbstractSectionComposite implements ITableEditOperation {

    protected List<Object[]> matchRows = new ArrayList<Object[]>();

    // <column name, column index>
    protected Map<String, String> columnMap = null;

    protected Analysis analysis = null;

    protected Boolean isNeedSubChart = true;

    private Composite sectionClient = null;

    private boolean isAddColumn = false;

    /**
     * @param parent
     * @param style
     */
    public AbstractMatchAnaysisTableSection(final ScrolledForm form, Composite parent, int style, FormToolkit toolkit,
            Analysis analysis) {
        super(form, parent, style, toolkit);
        this.analysis = analysis;
    }

    /**
     * DOC zshen Comment method "createContext".
     */
    public Composite createContent() {
        section.setText(getSectionName());
        sectionClient = toolkit.createComposite(section, SWT.NONE);
        sectionClient.setLayout(new GridLayout(getGridColumnNum(), true));
        sectionClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite createSubContent = createSubContent(sectionClient);
        createButtons(createSubContent);
        if (isNeedSubChart()) {
            createSubChart(sectionClient);
        }
        section.setClient(sectionClient);
        return sectionClient;
    }

    public void redrawnContent() {
        if (sectionClient != null && !sectionClient.isDisposed()) {
            sectionClient.dispose();
        }
        createContent();
        section.layout();

    }

    /**
     * Getter for isNeedSubChart.
     * 
     * @return the isNeedSubChart
     */
    public Boolean isNeedSubChart() {
        return this.isNeedSubChart;
    }

    /**
     * Sets the isNeedSubChart.
     * 
     * @param isNeedSubChart the isNeedSubChart to set
     */
    public void setIsNeedSubChart(Boolean isNeedSubChart) {
        this.isNeedSubChart = isNeedSubChart;
    }

    /**
     * DOC zshen Comment method "getColumnNum".
     * 
     * @return
     */
    private int getGridColumnNum() {
        if (isNeedSubChart()) {
            return 2;
        } else {
            return 1;
        }
    }

    protected void createButtons(Composite currentSectionClient) {
        if (isNeedSubChart) {
            createRefreshButton(currentSectionClient);
        } else {
            createEditOperationButtons(currentSectionClient);
        }
    }

    protected void createMoveButton(Composite buttonsComposite, GridData labelGd) {

        final Button upButton = new Button(buttonsComposite, SWT.NONE);
        upButton.setToolTipText(DefaultMessagesImpl.getString("AbstractMatchAnaysisTableSection.MoveUp_button_tooltip")); //$NON-NLS-1$
        upButton.setImage(ImageLib.getImage(ImageLib.UP_ACTION));
        upButton.setLayoutData(labelGd);
        upButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                moveUpTableItem();
            }
        });

        final Button downButton = new Button(buttonsComposite, SWT.NONE);
        downButton.setToolTipText(DefaultMessagesImpl.getString("AbstractMatchAnaysisTableSection.MoveDown_button_tooltip")); //$NON-NLS-1$
        downButton.setImage(ImageLib.getImage(ImageLib.DOWN_ACTION));
        downButton.setLayoutData(labelGd);
        downButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                moveDownTableItem();
            }
        });

    }

    /**
     * DOC zshen Comment method "createEditOperationButtons".
     * 
     * @param sectionClient
     */
    private void createEditOperationButtons(Composite parent) {
        Composite buttonsComposite = new Composite(parent, SWT.NONE);
        buttonsComposite.setLayout(new GridLayout(7, true));

        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.LEFT;
        labelGd.widthHint = 30;

        final Button addButton = new Button(buttonsComposite, SWT.NONE);
        addButton.setToolTipText(DefaultMessagesImpl.getString("AbstractMatchAnaysisTableSection.add_button_tooltip")); //$NON-NLS-1$
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                addTableItem();
            }
        });

        final Button delButton = new Button(buttonsComposite, SWT.NONE);
        delButton.setToolTipText(DefaultMessagesImpl.getString("AbstractMatchAnaysisTableSection.delete_button_tooltip")); //$NON-NLS-1$
        delButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delButton.setLayoutData(labelGd);
        delButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                removeTableItem();
            }
        });
        createMoveButton(buttonsComposite, labelGd);
        // TODO will be implemented later (copy and paste)

    }

    /**
     * DOC zshen Comment method "createButtons".
     */
    private void createRefreshButton(Composite currentSectionClient) {

        Composite buttonsComposite = new Composite(currentSectionClient, SWT.NONE);
        buttonsComposite.setLayout(new GridLayout(7, true));

        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.LEFT;
        labelGd.widthHint = 30;
        createMoveButton(buttonsComposite, labelGd);
        labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.CENTER;
        labelGd.horizontalSpan = 5;
        String buttonText = DefaultMessagesImpl.getString("AbstractMatchAnaysisTableSection.visulize_button_text"); //$NON-NLS-1$
        labelGd.widthHint = buttonText.length() * 8;
        final Button refresh = new Button(buttonsComposite, SWT.NONE);
        refresh.setToolTipText(DefaultMessagesImpl.getString("AbstractMatchAnaysisTableSection.visulize_button_tooltip")); //$NON-NLS-1$
        refresh.setText(buttonText);
        refresh.setLayoutData(labelGd);
        refresh.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                new RefreshChartAction(AbstractMatchAnaysisTableSection.this).run();
            }
        });

    }
/**
 * need clear the old data every time 
 * 
 */
    public void setDataInput(List<Object[]> allData) {
        matchRows.clear();
        matchRows.addAll(allData);
    }

    public void setColumnNameInput(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }

    public void updateColumnPosition(String column, int newPosition) {
        columnMap.remove(column);
        columnMap.put(column, String.valueOf(newPosition));
    }

    /**
     * Getter for isAddColumn.
     * 
     * @return the isAddColumn
     */
    public boolean isAddColumn() {
        return this.isAddColumn;
    }

    /**
     * Sets the isAddColumn.
     * 
     * @param isAddColumn the isAddColumn to set
     */
    public void setAddColumn(boolean isAddColumn) {
        this.isAddColumn = isAddColumn;
    }

    /**
     * DOC zshen Comment method "shouldMoveUp".
     * 
     * @param blockKeyDefinition
     * @param blockKeyDefinition2
     * @return
     */
    protected boolean isSameWithCurrentModel(Object blockKeyDefinition, Object blockKeyDefinition2) {
        if (blockKeyDefinition.equals(blockKeyDefinition2)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#pasteTableItem()
     */
    @Override
    public void pasteTableItem() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#copyTableItem()
     */
    @Override
    public void copyTableItem() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#moveDownTableItem()
     */
    @Override
    public void moveDownTableItem() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#moveUpTableItem()
     */
    @Override
    public void moveUpTableItem() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#removeTableItem()
     */
    @Override
    public void removeTableItem() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#addTableItem()
     */
    @Override
    public void addTableItem() {

    }

    /**
     * DOC zshen Comment method "createSubChart". TODO re-order the protected and abstract key word. let "protected"
     * first.
     * 
     * @param sectionClient
     */
    abstract protected void createSubChart(Composite sectionClient);

    /**
     * DOC zshen Comment method "createSubContent".
     * 
     * @param sectionClient
     */
    abstract protected Composite createSubContent(Composite sectionClient);

    /**
     * DOC zshen Comment method "getSectionName".
     * 
     * @return
     */
    abstract protected String getSectionName();

    public abstract void refreshChart();

    public abstract Boolean isKeyDefinitionAdded(String columnName) throws Exception;

    /**
     * check if the key's name is same with some columns name
     * 
     * @param keyName
     * @return
     */
    public boolean hasSameColumnWithKeyName(String keyName) {
        if (this.columnMap == null) {
            return false;
        }
        return columnMap.containsKey(keyName);
    }

    public void addColumn(String column, int index) {
        if (this.columnMap == null) {
            columnMap = new HashMap<String, String>();
        }
        columnMap.put(column, String.valueOf(index));
    }

    /**
     * When import:1) use key's name & key's column to compare, 2) set it empty when no match
     * 
     * @param keyDefinition
     */
    protected void setColumnValueIfMatch(KeyDefinition keyDefinition) {
        // if the key name= some column name, set the column to this key
        if (hasSameColumnWithKeyName(keyDefinition.getName())) {
            keyDefinition.setColumn(keyDefinition.getName());
        } else if (!hasSameColumnWithKeyName(keyDefinition.getColumn())) {
            // if the key's column = some column name, keep it
            // when key's name and key's column both no match, set it empty
            keyDefinition.setColumn(StringUtils.EMPTY);
        }
    }

}
