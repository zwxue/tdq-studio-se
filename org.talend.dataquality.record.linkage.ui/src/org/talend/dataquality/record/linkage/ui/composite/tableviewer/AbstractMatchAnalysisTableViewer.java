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
package org.talend.dataquality.record.linkage.ui.composite.tableviewer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.ui.action.RemoveMatchKeyDefinitionAction;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.KeyDefinition;
import org.talend.dataquality.rules.MatchRuleDefinition;

/**
 * created by zshen on Aug 6, 2013 Detailled comment
 *
 */
public abstract class AbstractMatchAnalysisTableViewer extends TableViewer {

    private static final int DEFAULT_TABLE_HEIGHT_HIME = 130;

    protected Table innerTable = null;

    boolean isAddColumn = true;

    protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    /**
     * DOC zshen MatchAnalysisTabveViewer constructor comment.
     *
     * @param parent
     * @param style
     */
    public AbstractMatchAnalysisTableViewer(Composite parent, int style, boolean isAddColumn) {
        super(parent, style);
        innerTable = this.getTable();
        this.isAddColumn = isAddColumn;
        initListener();
    }

    /**
     * DOC zshen Comment method "initListener".
     */
    private void initListener() {
        innerTable.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                char character = e.character;
                if (SWT.DEL == character) {
                    new RemoveMatchKeyDefinitionAction(AbstractMatchAnalysisTableViewer.this).run();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub

            }

        });
    }

    /**
     *
     * DOC zshen Comment method "initTable".
     *
     * @param headers the name of column
     * @param pixelDataOfHeaders the width of the column
     */
    public void initTable(List<String> headers) {
        TableLayout tLayout = new TableLayout();
        innerTable.setLayout(tLayout);
        innerTable.setHeaderVisible(true);
        innerTable.setLinesVisible(true);
        GridData gd = new GridData(GridData.FILL_BOTH);
        innerTable.setLayoutData(gd);

        for (int index = 0; index < headers.size(); index++) {
            tLayout.addColumnData(new ColumnPixelData(headers.get(index).length() * getHeaderDisplayWeight()));

            new TableColumn(innerTable, SWT.LEFT).setText(headers.get(index));
        }

        CellEditor[] editors = getCellEditor(headers);
        // add menu
        addContextMenu();
        ICellModifier cellModifier = getTableCellModifier();
        if (cellModifier != null) {
            this.setCellModifier(cellModifier);
        }
        this.setCellEditors(editors);
        this.setColumnProperties(headers.toArray(new String[headers.size()]));
        this.setContentProvider(getTableContentProvider());
        this.setLabelProvider(getTableLabelProvider());
        GridData tableGD = new GridData(GridData.FILL_BOTH);
        tableGD.heightHint = getTableHeightHint();
        innerTable.setLayoutData(tableGD);

    }

    /**
     *
     * @return
     */
    protected int getTableHeightHint() {
        return DEFAULT_TABLE_HEIGHT_HIME; // 130 by defaut.
    }

    /**
     * DOC zshen Comment method "selectAllItem".
     *
     * @param bkdList
     */
    public void selectAllItem(List<KeyDefinition> bkdList) {
        this.setSelectionToWidget(bkdList, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ColumnViewer#update(java.lang.Object, java.lang.String[])
     */
    @Override
    public void update(Object element, String[] properties) {
        super.update(element, properties);
        listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, true, false);
    }

    /**
     * DOC zhao Comment method "addContextMenu".
     */
    abstract public void addContextMenu();

    /**
     * Getter for isAddColumn.
     *
     * @return the isAddColumn
     */
    public boolean isAddColumn() {
        return this.isAddColumn;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * DOC zshen Comment method "getDisplayWeight".
     *
     * @return
     */
    abstract protected int getHeaderDisplayWeight();

    /**
     * DOC zshen Comment method "getTableLabelProvider".
     *
     * @return
     */
    abstract protected IBaseLabelProvider getTableLabelProvider();

    /**
     * DOC zshen Comment method "getTableContentProvider".
     *
     * @return
     */
    abstract protected IContentProvider getTableContentProvider();

    /**
     * DOC zshen Comment method "getTableCellModifier".
     *
     * @return
     */
    abstract protected ICellModifier getTableCellModifier();

    /**
     * DOC zshen Comment method "getCellEditor".
     *
     * @param headers
     * @return
     */
    abstract protected CellEditor[] getCellEditor(List<String> headers);

    /**
     *
     * add new Element
     *
     * @param columnName the name of column
     * @param analysis the context of this add operation perform on.
     */
    abstract public boolean addElement(String columnName, Analysis analysis);

    /**
     *
     * add new Element
     *
     * @param columnName the name of column
     * @param analysis the context of this add operation perform on.
     */
    abstract public boolean addElement(String columnName, MatchRuleDefinition matchRuleDef);

    /**
     * remove Element
     *
     * @param columnName the name of column
     */
    abstract public void removeElement(String columnName, Analysis analysis);

    /**
     * remove Element
     *
     * @param columnName the element of column
     */
    abstract public void removeElement(KeyDefinition keyDef, Analysis analysis);

    /**
     * remove Element
     *
     * @param columnName the element of column
     */
    abstract public void removeElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef);

    /**
     *
     * move up element
     *
     * @param keyDef
     * @param matchRuleDef
     */
    abstract public void moveUpElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef);

    /**
     *
     * move down element
     *
     * @param keyDef
     * @param matchRuleDef
     */
    abstract public void moveDownElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef);

}
