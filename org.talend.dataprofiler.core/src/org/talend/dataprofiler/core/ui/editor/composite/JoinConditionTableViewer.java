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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.dqrules.DQRuleMasterDetailsPage;
import org.talend.dataprofiler.core.ui.views.ColumnViewerDND;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.rules.JoinElement;
import org.talend.dataquality.rules.RulesFactory;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC xqliu class global comment. bug 8791 2009-08-31.
 */
public class JoinConditionTableViewer extends AbstractColumnDropTree {
    
    protected static Logger log = Logger.getLogger(JoinConditionTableViewer.class);

    private static final String COLUMN_A = "A";

    private static final String COLUMN_B = "B";
    
    private DQRuleMasterDetailsPage masterPage;
    
    private Table myTable;
    
    private TableViewer myTableViewer;

    private List<JoinElement> myJoinElement;
    
    private Composite parentComposite;

    private static final String DEFAULT_OPERATOR = "=";

    private static final String[] OPERATORS = { "=", ">", "<", ">=", "<=" };

    private String[] headers = { "TableA", "TableAliasA", "ColumnA", "ColumnAliasA", "Operator", "TableB", "TableAliasB",
            "ColumnB", "ColumnAliasB" };

    private int[] widths = { 100, 100, 100, 100, 70, 100, 100, 100, 100 };
    
    public JoinConditionTableViewer(Composite parent, DQRuleMasterDetailsPage masterPage) {
        this.parentComposite = parent;
        this.masterPage = masterPage;
        this.myJoinElement = masterPage.getTempJoinElements();
        this.myTable = createTable(parent);
    }

    /**
     * DOC xqliu Comment method "createTable".
     * 
     * @param parent
     */
    private Table createTable(Composite parent) {
        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
        
        final Table table = new Table(parentComposite, style);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        for (int i = 0; i < headers.length; ++i) {
            TableColumn tableColumn = new TableColumn(table, SWT.LEFT, i);
            tableColumn.setText(headers[i]);
            tableColumn.setWidth(widths[i]);
        }
        
        myTableViewer = new TableViewer(table);

        myTableViewer.setUseHashlookup(true);
        myTableViewer.setColumnProperties(headers);

        CellEditor[] editors = new CellEditor[headers.length];
        for (int i = 0; i < editors.length; ++i) {
            switch (i) {
            case 1:
            case 3:
            case 6:
            case 8:
                editors[i] = new TextCellEditor(table);
                break;
            case 4:
                editors[i] = new ComboBoxCellEditor(table, OPERATORS, SWT.READ_ONLY);
                break;
            default:
                 editors[i] = null;
             }
        }
        myTableViewer.setCellEditors(editors);

        myTableViewer.setCellModifier(new JoinElementCellModifier(headers, myTableViewer));
        myTableViewer.setContentProvider(new JoinElementContentProvider());
        myTableViewer.setLabelProvider(new JoinElementLabelProvider());
        myTableViewer.setInput(this.myJoinElement);
        
        // ADD xqliu 2009-09-01 bug 8790
        table.setMenu(createMenus(table)); 
        // ~
        
        ColumnViewerDND.installDND(table);
        table.setData(this);
        GridData tableGD = new GridData(GridData.FILL_BOTH);
        tableGD.heightHint = 130;
        table.setLayoutData(tableGD);
        
        return table;
    }

    /**
     * DOC xqliu Comment method "createMenus".
     * @param table
     * @return
     */
    private Menu createMenus(final Table table) {
        Menu menu = new Menu(table);

        MenuItem menuItemA = new MenuItem(menu, SWT.CASCADE);
        menuItemA.setText(DefaultMessagesImpl.getString("JoinConditionTableViewer.showDQElementA")); //$NON-NLS-1$
        menuItemA.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
        menuItemA.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                showSelectedElements(table, COLUMN_A);
            }

        });
        
        MenuItem menuItemB = new MenuItem(menu, SWT.CASCADE);
        menuItemB.setText(DefaultMessagesImpl.getString("JoinConditionTableViewer.showDQElementB")); //$NON-NLS-1$
        menuItemB.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
        menuItemB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                showSelectedElements(table, COLUMN_B);
            }

        });
        
        return menu;
    }

    /**
     * DOC xqliu Comment method "showSelectedElements".
     * 
     * @param table
     * @param ab
     */
    protected void showSelectedElements(Table table, String ab) {
        TableItem[] selection = table.getSelection();
        DQRespositoryView dqview = (DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID);
        if (selection.length == 1) {
            try {
                JoinElement join = (JoinElement) selection[0].getData();
                ModelElement column = join.getColA();
                if (COLUMN_B.equals(ab)) {
                    column = join.getColB();
                }
                dqview.showSelectedElements(column);
            } catch (Exception e) {
                log.error(e, e);
            }
        }
    }

    /**
     * DOC xqliu Comment method "addJoinElement".
     * 
     * @return
     */
    public JoinElement addJoinElement() {
        JoinElement newJoinElement = RulesFactory.eINSTANCE.createJoinElement();
        newJoinElement.setOperator(DEFAULT_OPERATOR);
        this.myTableViewer.add(newJoinElement);
        this.myJoinElement.add(newJoinElement);
        JoinConditionTableViewer.this.masterPage.setDirty(true);
        return newJoinElement;
    }

    /**
     * DOC xqliu Comment method "getSelection".
     * 
     * @return
     */
    public ISelection getSelection() {
        return this.myTableViewer.getSelection();
    }

    /**
     * DOC xqliu Comment method "removeJoinElement".
     * 
     * @param join
     */
    public void removeJoinElement(JoinElement join) {
        this.myTableViewer.remove(join);
        this.myJoinElement.remove(join);
        JoinConditionTableViewer.this.masterPage.setDirty(true);
    }

    @Override
    public boolean canDrop(Column column) {
        return true;
    }

    @Override
    public void dropColumns(List<Column> columns, int index) {
        JoinElementColumnDialog joinElementColumnDialog = new JoinElementColumnDialog(null);
        if (joinElementColumnDialog.open() == Window.OK) {
            JoinElement join = (JoinElement) this.myTableViewer.getElementAt(index);
            if (join == null) {
                join = this.addJoinElement();
            }
            if (join != null) {
                for (Column column : columns) {
                    if (column != null) {
                        if (COLUMN_A.equals(joinElementColumnDialog.getAb())) {
                            join.setColA(column);
                            join.setColumnAliasA(column.getName());
                            join.setTableAliasA(ColumnHelper.getColumnSetFullName(column));
                        } else {
                            join.setColB(column);
                            join.setColumnAliasB(column.getName());
                            join.setTableAliasB(ColumnHelper.getColumnSetFullName(column));
                        }
                    }
                }
                this.setDirty(true);
                this.myTableViewer.update(join, null);
            }
        }
    }

    @Override
    public void updateModelViewer() {

    }
    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private final class JoinElementCellModifier implements ICellModifier {

        private List<String> columeNames;

        private TableViewer tableViewer;

        public JoinElementCellModifier(String[] columeNames, TableViewer tableViewer) {
            super();
            this.columeNames = new ArrayList<String>();
            for (String columnName : columeNames) {
                this.columeNames.add(columnName);
            }
            this.tableViewer = tableViewer;
        }

        public boolean canModify(Object element, String property) {
            return true;
        }

        public Object getValue(Object element, String property) {
            int columnIndex = columeNames.indexOf(property);

            Object result = null;
            JoinElement join = (JoinElement) element;

            TdColumn colA = (TdColumn) join.getColA();
            TdColumn colB = (TdColumn) join.getColB();

            ColumnSet tabA = colA == null ? null : ColumnHelper.getColumnSetOwner(colA);
            ColumnSet tabB = colB == null ? null : ColumnHelper.getColumnSetOwner(colB);

            switch (columnIndex) {
            case 0:
                result = tabA == null ? "" : tabA.getName();
                break;
            case 1:
                result = join.getTableAliasA() == null ? "" : join.getTableAliasA();
                break;
            case 2:
                result = colA == null ? "" : colA.getName();
                break;
            case 3:
                result = join.getColumnAliasA() == null ? "" : join.getColumnAliasA();
                break;
            case 4:
                String stringValue = join.getOperator();
                int i = OPERATORS.length - 1;
                while (!stringValue.equals(OPERATORS[i]) && i > 0) {
                    --i;
                }
                result = new Integer(i);
                break;
            case 5:
                result = tabB == null ? "" : tabB.getName();
                break;
            case 6:
                result = join.getTableAliasB() == null ? "" : join.getTableAliasB();
                break;
            case 7:
                result = colB == null ? "" : colB.getName();
                break;
            case 8:
                result = join.getColumnAliasB() == null ? "" : join.getColumnAliasB();
                break;
            default:
                result = "";
            }
            return result;
        }

        public void modify(Object element, String property, Object value) {
            int columnIndex = this.columeNames.indexOf(property);

            TableItem tableItem = (TableItem) element;
            JoinElement join = (JoinElement) tableItem.getData();
            String valueString = String.valueOf(value).trim();

            switch (columnIndex) {
            case 1:
                join.setTableAliasA(valueString);
                break;
            case 3:
                join.setColumnAliasA(valueString);
                break;
            case 4:
                valueString = OPERATORS[((Integer) value).intValue()].trim();
                if (!join.getOperator().equals(valueString)) {
                    join.setOperator(valueString);
                }
                join.setOperator(valueString);
                break;
            case 6:
                join.setTableAliasB(valueString);
                break;
            case 8:
                join.setColumnAliasB(valueString);
                break;
            default:
            }
            tableViewer.update(join, null);
            JoinConditionTableViewer.this.masterPage.setDirty(true);
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private final class JoinElementContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            if (inputElement != null) {
                if (inputElement instanceof List) {
                    return ((List) inputElement).toArray();
                }
            }
            return null;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * DOC xqliu IndicatorDefinitionMaterPage class global comment. Detailled comment
     */
    private class JoinElementLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            Image result = null;
            switch (columnIndex) {
            case 0:
                result = ImageLib.getImage(ImageLib.TABLE);
                break;
            case 2:
                result = ImageLib.getImage(ImageLib.TD_COLUMN);
                break;
            case 5:
                result = ImageLib.getImage(ImageLib.TABLE);
                break;
            case 7:
                result = ImageLib.getImage(ImageLib.TD_COLUMN);
                break;
            default:
            }
            return result;
        }

        public String getColumnText(Object element, int columnIndex) {
            String result = "";
            
            JoinElement join = (JoinElement) element;

            TdColumn colA = (TdColumn) join.getColA();
            TdColumn colB = (TdColumn) join.getColB();

            ColumnSet tabA = colA == null ? null : ColumnHelper.getColumnSetOwner(colA);
            ColumnSet tabB = colB == null ? null : ColumnHelper.getColumnSetOwner(colB);

            switch (columnIndex) {
            case 0:
                result = tabA == null ? "" : tabA.getName();
                break;
            case 1:
                result = join.getTableAliasA() == null ? "" : join.getTableAliasA();
                break;
            case 2:
                result = colA == null ? "" : colA.getName();
                break;
            case 3:
                result = join.getColumnAliasA() == null ? "" : join.getColumnAliasA();
                break;
            case 4:
                result = join.getOperator();
                break;
            case 5:
                result = tabB == null ? "" : tabB.getName();
                break;
            case 6:
                result = join.getTableAliasB() == null ? "" : join.getTableAliasB();
                break;
            case 7:
                result = colB == null ? "" : colB.getName();
                break;
            case 8:
                result = join.getColumnAliasB() == null ? "" : join.getColumnAliasB();
                break;
            default:
                result = "";
            }
            return result;
        }

    }
    
    /**
     * DOC xqliu JoinConditionTableViewer class global comment. Detailled comment
     */
    private class JoinElementColumnDialog extends Dialog {
        
        private String ab;

        public String getAb() {
            return ab;
        }

        
        public void setAb(String ab) {
            this.ab = ab;
        }

        /**
         * DOC xqliu JoinElementColumnDialog constructor comment.
         * 
         * @param parentShell
         */
        protected JoinElementColumnDialog(Shell parentShell) {
            super(parentShell);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite comp = new Composite(parent, SWT.NONE);
            comp.setLayout(new GridLayout(2, true));
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));
            
            Button[] radios = new Button[2];

            radios[0] = new Button(comp, SWT.RADIO);
            radios[0].setSelection(true);
            setAb(COLUMN_A);
            radios[0].setText("ColumnA");
            radios[0].addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent arg0) {
                    widgetSelected(arg0);
                }

                public void widgetSelected(SelectionEvent arg0) {
                    setAb(COLUMN_A);
                }

            });

            radios[1] = new Button(comp, SWT.RADIO);
            radios[1].setText("ColumnB");
            radios[1].addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent arg0) {
                    widgetSelected(arg0);
                }

                public void widgetSelected(SelectionEvent arg0) {
                    setAb(COLUMN_B);
                }

            });
            
            GridData radioGD = new GridData(GridData.FILL_BOTH);
            radios[0].setLayoutData(radioGD);
            radios[1].setLayoutData(radioGD);

            return comp;
        }

    }

}
