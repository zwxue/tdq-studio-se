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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.ui.internal.views.HelpTray;
import org.eclipse.help.ui.internal.views.ReusableHelpPart;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQPreferenceManager;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.userdefine.UDIndicatorDefinition;
import org.talend.dq.dbms.GenericSQLHandler;
import org.talend.dq.helper.UDIHelper;

/**
 * DOC yyi 2009-09-11 Feature:9030
 */
public class ExpressionEditDialog extends TrayDialog {

    private static final String RESET = DefaultMessagesImpl.getString("ExpressionEditDialog.reset"); //$NON-NLS-1$

    private static final String RESET_HINT = DefaultMessagesImpl.getString("ExpressionEditDialog.resetHintMessage"); //$NON-NLS-1$

    private static final String TEMPLATES_TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.columnTitle"); //$NON-NLS-1$

    private static final String MATCH_EXPRESSION_TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.matchExpression"); //$NON-NLS-1$

    private static final String ORDER_BY_TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.orderBy"); //$NON-NLS-1$

    private static final String GROUP_BY_TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.groupBy"); //$NON-NLS-1$

    private static final String SECOND_COLUMN_TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.secondColumn"); //$NON-NLS-1$

    private static final String FIRST_COLUMN_TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.firstColumn"); //$NON-NLS-1$

    private static final String COLUMN_EXPRESSION_TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.columnExpression"); //$NON-NLS-1$

    private static final String WHERE_EXPRESSION_TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.whereExpression"); //$NON-NLS-1$

    private static final String EXPREEION = DefaultMessagesImpl.getString("ExpressionEditDialog.groupTitle"); //$NON-NLS-1$

    private static final String TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.title"); //$NON-NLS-1$

    private static final String tab0_name = DefaultMessagesImpl.getString("ExpressionEditDialog.tab0"); //$NON-NLS-1$

    private static final String tab1_name = DefaultMessagesImpl.getString("ExpressionEditDialog.tab1"); //$NON-NLS-1$

    private static final String tab2_name = DefaultMessagesImpl.getString("ExpressionEditDialog.tab2"); //$NON-NLS-1$

    private static final String tab3_name = DefaultMessagesImpl.getString("ExpressionEditDialog.tab3"); //$NON-NLS-1$

    private static final String tab4_name = DefaultMessagesImpl.getString("ExpressionEditDialog.tab4"); //$NON-NLS-1$

    private static final String tab5_name = DefaultMessagesImpl.getString("ExpressionEditDialog.tab5"); //$NON-NLS-1$

    private static final String tab6_name = DefaultMessagesImpl.getString("ExpressionEditDialog.tab6"); //$NON-NLS-1$

    private String[] templates = new String[] { GenericSQLHandler.TABLE_NAME, GenericSQLHandler.COLUMN_NAMES,
            GenericSQLHandler.WHERE_CLAUSE, GenericSQLHandler.AND_WHERE_CLAUSE, GenericSQLHandler.GROUP_BY_ALIAS,
            GenericSQLHandler.UDI_INDICATOR_VALUE };

    private String fullSqlContent;

    private Text editTextNotUDI;

    private Composite tab0Composite;

    private Composite tab1Composite;

    private Composite tab2Composite;

    private Composite tab3Composite;

    private Composite tab4Composite;

    private Composite tab5Composite;

    private Composite tab6Composite;

    // for TAB0:
    private Text tab0_count_where_var;

    private Text tab0_realvalue_column_var;

    private Text tab0_realvalue_where_var;

    private Text tab0_fre_first_var;

    private Text tab0_fre_second_var;

    private Text tab0_fre_where_var;

    private Text tab0_fre_groupby_var;

    private Text tab0_fre_orderby_var;

    private Text tab0_match_match_var;

    private Text tab0_match_where_var;

    private Button resetButton_tab1;

    private Button resetButton_tab2_viewRows;

    private Button resetButton_tab2_match;

    private Button resetButton_tab3_match;

    private Button resetButton_tab4_match;

    private Button resetButton_tab5_match;

    // for TAB1 all types :
    private Text tab1_fullSql;

    // for TAB2 all except match :
    private Text tab2_viewRows;

    // for TAB2 match only :
    private Text tab2_match_viewvalidRows;

    // for TAB3 match only :
    private Text tab3_match_viewInvalidRows;

    // for TAB4 match only :
    private Text tab4_match_viewValidValues;

    // for TAB5 match only :
    private Text tab5_match_viewInvalidValues;

    private IndicatorDefinition definition;

    private TdExpression tdExpression;

    private HashMap<String, String> variableMap;

    private IndicatorCategory category;

    private String language;

    private String version;

    private int selectTabNumber = 0;

    private TdExpression tempViewRowsExp;

    private TdExpression tempViewValidRowsExp;

    private TdExpression tempViewInvalidRowsExp;

    private TdExpression tempViewValidValuesExp;

    private TdExpression tempViewInvalidValuesExp;

    // when tab0 text modified, set true, use autoGenSql to fill other tabs(from tab1 to tab6),
    // when the other tabs text modified, set false
    private boolean useAutoGenSqlFlag = false;

    // when tab1 or tab2 text is inputted by user, can not use the AutoGenSql to overwriten
    private boolean canBeOverwriten_tab1 = true;

    private boolean canBeOverwriten_tab2_viewRows = true;

    private boolean canBeOverwriten_tab2_match = true;

    private boolean canBeOverwriten_tab3_match = true;

    private boolean canBeOverwriten_tab4_match = true;

    private boolean canBeOverwriten_tab5_match = true;

    // the sql generated from template
    private String autoGenSql = PluginConstant.EMPTY_STRING;

    public ExpressionEditDialog(Shell parentShell, String patternText, IndicatorDefinition definition, TdExpression tdExpression) {
        super(parentShell);
        this.definition = definition;
        this.category = IndicatorCategoryHelper.getCategory(definition);
        this.fullSqlContent = patternText;

        this.tdExpression = tdExpression;
        language = tdExpression.getLanguage().trim();
        if (definition instanceof UDIndicatorDefinition) {
            version = tdExpression.getVersion();
            UDIndicatorDefinition definitionTemp = (UDIndicatorDefinition) this.definition;
            if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                tempViewValidRowsExp = getCurrentLanguageExp(definitionTemp.getViewValidRowsExpression());
                tempViewInvalidRowsExp = getCurrentLanguageExp(definitionTemp.getViewInvalidRowsExpression());
                tempViewValidValuesExp = getCurrentLanguageExp(definitionTemp.getViewValidValuesExpression());
                tempViewInvalidValuesExp = getCurrentLanguageExp(definitionTemp.getViewInvalidValuesExpression());
            } else {
                tempViewRowsExp = getCurrentLanguageExp(definitionTemp.getViewRowsExpression());
            }

            this.variableMap = tdExpression.getExpressionVariableMap();
            if (variableMap == null) {
                variableMap = new HashMap<String, String>();
            }
        }

        int shellStyle = getShellStyle();
        setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);
    }

    /**
     * get Current Language Expression.
     * 
     * @param tempViewValidRowsExpList
     */
    public TdExpression getCurrentLanguageExp(List<TdExpression> tempViewValidRowsExpList) {
        if (tempViewValidRowsExpList != null) {
            for (TdExpression tdExp : tempViewValidRowsExpList) {
                if (isCurrentLanguageAndVersion(tdExp)) {
                    return tdExp;
                }
            }
        }
        TdExpression createTdExpression = BooleanExpressionHelper.createTdExpression(language, PluginConstant.EMPTY_STRING,
                version);
        createTdExpression.setModificationDate(UDIUtils.getCurrentDateTime());
        return createTdExpression;
    }

    /**
     * DOC msjian Comment method "isCurrentLanguageAndVersion".
     * 
     * @param tdExp
     * @return
     */
    public boolean isCurrentLanguageAndVersion(TdExpression tdExp) {
        return tdExp.getLanguage().equals(language)
                && ((tdExp.getVersion() == null && version == null) || (tdExp.getVersion() != null && tdExp.getVersion().equals(
                        version)));
    }

    /**
     * create Dialog Area For Not UDI.
     * 
     * @param parent
     * @return
     */
    protected Control createDialogAreaForNotUDI(Composite parent) {
        Composite comp = (Composite) super.createDialogArea(parent);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 750;
        data.heightHint = 650;
        comp.setLayoutData(data);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(comp, HelpPlugin.getDefault().getExpressionEditContextID());

        SashForm sform = new SashForm(comp, SWT.VERTICAL | SWT.SMOOTH | SWT.FILL);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        sform.setLayoutData(data);

        editTextNotUDI = createTextPart(sform, EXPREEION, 20, fullSqlContent);

        createTemplatesTablePart(sform);

        sform.setWeights(new int[] { 2, 1 });
        comp.layout();
        return comp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Control comp;
        // if the category is user define, then create tabs, else as before
        if (definition instanceof UDIndicatorDefinition && IndicatorCategoryHelper.isUserDefCategory(category)) {
            comp = createDialogAreaForUDI(parent);
        } else {
            comp = createDialogAreaForNotUDI(parent);
        }
        return comp;

    }

    /**
     * create Dialog Area For UDI.
     * 
     * @param parent
     * @return
     */
    public Control createDialogAreaForUDI(Composite parent) {
        final Composite comp = (Composite) super.createDialogArea(parent);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 750;
        data.heightHint = 650;
        comp.setLayoutData(data);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(comp, HelpPlugin.getDefault().getExpressionEditContextID());

        final TabFolder tabFolder = new TabFolder(comp, SWT.FILL);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = convertWidthInCharsToPixels(200);
        gridData.heightHint = convertHeightInCharsToPixels(300);
        tabFolder.setLayoutData(gridData);

        TabItem tab0 = new TabItem(tabFolder, SWT.NULL);
        tab0.setText(tab0_name);
        tab0Composite = new Composite(tabFolder, SWT.None);
        tab0Composite.setLayout(new GridLayout());
        tab0.setControl(tab0Composite);
        selectTabNumber = 0;
        createTabPart(tab0Composite);

        TabItem tab1 = new TabItem(tabFolder, SWT.NULL);
        tab1.setText(tab1_name);
        tab1Composite = new Composite(tabFolder, SWT.None);
        tab1Composite.setLayout(new GridLayout());
        tab1.setControl(tab1Composite);
        selectTabNumber = 1;
        createTabPart(tab1Composite);

        // for use define match, have more tabs belong to itself
        if (IndicatorCategoryHelper.isUserDefMatching(category)) {
            TabItem tab3 = new TabItem(tabFolder, SWT.NULL);
            tab3.setText(tab3_name);
            tab3Composite = new Composite(tabFolder, SWT.None);
            tab3Composite.setLayout(new GridLayout());
            tab3.setControl(tab3Composite);
            selectTabNumber = 2;
            createTabPart(tab3Composite);

            TabItem tab4 = new TabItem(tabFolder, SWT.NULL);
            tab4.setText(tab4_name);
            tab4Composite = new Composite(tabFolder, SWT.None);
            tab4Composite.setLayout(new GridLayout());
            tab4.setControl(tab4Composite);
            selectTabNumber = 3;
            createTabPart(tab4Composite);

            TabItem tab5 = new TabItem(tabFolder, SWT.NULL);
            tab5.setText(tab5_name);
            tab5Composite = new Composite(tabFolder, SWT.None);
            tab5Composite.setLayout(new GridLayout());
            tab5.setControl(tab5Composite);
            selectTabNumber = 4;
            createTabPart(tab5Composite);

            TabItem tab6 = new TabItem(tabFolder, SWT.NULL);
            tab6.setText(tab6_name);
            tab6Composite = new Composite(tabFolder, SWT.None);
            tab6Composite.setLayout(new GridLayout());
            tab6.setControl(tab6Composite);
            selectTabNumber = 5;
            createTabPart(tab6Composite);

        } else {
            TabItem tab2 = new TabItem(tabFolder, SWT.NULL);
            tab2.setText(tab2_name);
            tab2Composite = new Composite(tabFolder, SWT.None);
            tab2Composite.setLayout(new GridLayout());
            tab2.setControl(tab2Composite);
            selectTabNumber = 2;
            createTabPart(tab2Composite);
        }

        tabFolder.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                String text = ((TabItem) e.item).getText();
                selectTabNumber = tabFolder.getSelectionIndex();

                if (tab0_name.equals(text)) {
                    // Indicator Definition
                    tab0Composite.layout();
                } else {
                    if (tab1_name.equals(text)) {
                        // Full SQL Template
                        if (useAutoGenSqlFlag && canBeOverwriten_tab1) {
                            tab1_fullSql.setText(getAutoGeneratedSql());
                            useAutoGenSqlFlag = true;
                            canBeOverwriten_tab1 = true;
                        }
                        tab1Composite.layout();
                    } else if (tab2_name.equals(text)) {
                        // View Rows
                        if (useAutoGenSqlFlag && canBeOverwriten_tab2_viewRows) {
                            tab2_viewRows.setText(getAutoGeneratedSql());
                            useAutoGenSqlFlag = true;
                            canBeOverwriten_tab2_viewRows = true;
                        }
                        tab2Composite.layout();
                    } else if (tab3_name.equals(text)) {
                        // View Valid Rows Template
                        if (useAutoGenSqlFlag && canBeOverwriten_tab2_match) {
                            tab2_match_viewvalidRows.setText(getAutoGeneratedSql());
                            useAutoGenSqlFlag = true;
                            canBeOverwriten_tab2_match = true;
                        }
                        tab3Composite.layout();
                    } else if (tab4_name.equals(text)) {
                        // View Invalid Rows Template
                        if (useAutoGenSqlFlag && canBeOverwriten_tab3_match) {
                            tab3_match_viewInvalidRows.setText(getAutoGeneratedSql());
                            useAutoGenSqlFlag = true;
                            canBeOverwriten_tab3_match = true;
                        }
                        tab4Composite.layout();
                    } else if (tab5_name.equals(text)) {
                        // View Valid Values Template
                        if (useAutoGenSqlFlag && canBeOverwriten_tab4_match) {
                            tab4_match_viewValidValues.setText(getAutoGeneratedSql());
                            useAutoGenSqlFlag = true;
                            canBeOverwriten_tab4_match = true;
                        }
                        tab5Composite.layout();
                    } else if (tab6_name.equals(text)) {
                        // View Invalid Values Template
                        if (useAutoGenSqlFlag && canBeOverwriten_tab5_match) {
                            tab5_match_viewInvalidValues.setText(getAutoGeneratedSql());
                            useAutoGenSqlFlag = true;
                            canBeOverwriten_tab5_match = true;
                        }
                        tab6Composite.layout();
                    }

                    if (selectTabNumber == 1) {
                        resetButton_tab1.setEnabled(!getAutoGeneratedSql().equals(tab1_fullSql.getText()));
                    } else if (selectTabNumber == 2) {
                        if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                            // for match dis View Valid Rows template
                            resetButton_tab2_match.setEnabled(!getAutoGeneratedSql().equals(tab2_match_viewvalidRows.getText()));
                        } else {
                            // for others is view rows template
                            resetButton_tab2_viewRows.setEnabled(!getAutoGeneratedSql().equals(tab2_viewRows.getText()));
                        }
                    } else if (selectTabNumber == 3) {
                        // for match is View Invalid Rows Template
                        resetButton_tab3_match.setEnabled(!getAutoGeneratedSql().equals(tab3_match_viewInvalidRows.getText()));
                    } else if (selectTabNumber == 4) {
                        // for match is View Valid Values Template
                        resetButton_tab4_match.setEnabled(!getAutoGeneratedSql().equals(tab4_match_viewValidValues.getText()));
                    } else if (selectTabNumber == 5) {
                        // for match is View Invalid Values Template
                        resetButton_tab5_match.setEnabled(!getAutoGeneratedSql().equals(tab5_match_viewInvalidValues.getText()));
                    }

                }

            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        // set the default selected tab
        selectTabNumber = 0;
        tabFolder.setSelection(selectTabNumber);
        tab0Composite.layout();

        comp.layout();
        return comp;
    }

    /**
     * create Reset Button.
     * 
     * @param sform
     */
    public Button createResetButton(Composite sform) {
        Button resetButton = new Button(sform, SWT.PUSH);
        resetButton.setBounds(10, 10, 60, 30);
        resetButton.setText(RESET);
        resetButton.setToolTipText(RESET_HINT);
        // // set default is unenable
        // resetButton.setEnabled(false);

        resetButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                useAutoGenSqlFlag = true;
                if (selectTabNumber == 1) {
                    tab1_fullSql.setText(getAutoGeneratedSql());
                } else if (selectTabNumber == 2) {
                    if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                        // for match is View Valid Rows template
                        tab2_match_viewvalidRows.setText(getAutoGeneratedSql());
                    } else {
                        // for others is view rows template
                        tab2_viewRows.setText(getAutoGeneratedSql());
                    }
                } else if (selectTabNumber == 3) {
                    tab3_match_viewInvalidRows.setText(getAutoGeneratedSql());
                } else if (selectTabNumber == 4) {
                    tab4_match_viewValidValues.setText(getAutoGeneratedSql());
                } else if (selectTabNumber == 5) {
                    tab5_match_viewInvalidValues.setText(getAutoGeneratedSql());
                }

                setResetButtonStatus(false);
                setTextOverWriteStatus(true);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return resetButton;
    }

    /**
     * create tab1,tab2(for match is tab3456) part
     * 
     * @param comp
     */
    public void createTabPart(Composite comp) {
        // create reset button, and set its status.
        // if the text different from the template, then set the reset button default status is enable, else set
        // unenable
        if (selectTabNumber == 1) {
            resetButton_tab1 = createResetButton(comp);
            canBeOverwriten_tab1 = getAutoGeneratedSql().equals(fullSqlContent);
            resetButton_tab1.setEnabled(!canBeOverwriten_tab1);
        } else if (selectTabNumber == 2) {
            if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                // for match is View Valid Rows template
                resetButton_tab2_match = createResetButton(comp);
                canBeOverwriten_tab2_match = getAutoGeneratedSql().equals(getExpressValue(tempViewValidRowsExp));
                resetButton_tab2_match.setEnabled(!canBeOverwriten_tab2_match);
            } else {
                // for others is view rows template
                resetButton_tab2_viewRows = createResetButton(comp);
                canBeOverwriten_tab2_viewRows = getAutoGeneratedSql().equals(getExpressValue(tempViewRowsExp));
                resetButton_tab2_viewRows.setEnabled(!canBeOverwriten_tab2_viewRows);
            }
        } else if (selectTabNumber == 3) {
            // for match is View Invalid Rows Template
            resetButton_tab3_match = createResetButton(comp);
            canBeOverwriten_tab3_match = getAutoGeneratedSql().equals(getExpressValue(tempViewInvalidRowsExp));
            resetButton_tab3_match.setEnabled(!canBeOverwriten_tab3_match);
        } else if (selectTabNumber == 4) {
            // for match is View Valid Values Template
            resetButton_tab4_match = createResetButton(comp);
            canBeOverwriten_tab4_match = getAutoGeneratedSql().equals(getExpressValue(tempViewValidValuesExp));
            resetButton_tab4_match.setEnabled(!canBeOverwriten_tab4_match);
        } else if (selectTabNumber == 5) {
            // for match is View Invalid Values Template
            resetButton_tab5_match = createResetButton(comp);
            canBeOverwriten_tab5_match = getAutoGeneratedSql().equals(getExpressValue(tempViewInvalidValuesExp));
            resetButton_tab5_match.setEnabled(!canBeOverwriten_tab5_match);
        }

        GridData data;
        SashForm sform = new SashForm(comp, SWT.VERTICAL | SWT.SMOOTH | SWT.FILL);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        sform.setLayoutData(data);

        // create content for different tabs in sfrom
        createTabPage(sform);

        // create templates table part
        createTemplatesTablePart(sform);

        // set weights for sform
        if (selectTabNumber == 0) {
            if (IndicatorCategoryHelper.isUserDefCount(category)) {
                sform.setWeights(new int[] { 2, 1 });
            } else if (IndicatorCategoryHelper.isUserDefFrequency(category)) {
                sform.setWeights(new int[] { 1, 1, 1, 1, 1, 2 });
            } else if (IndicatorCategoryHelper.isUserDefRealValue(category)) {
                sform.setWeights(new int[] { 1, 1, 1 });
            } else if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                sform.setWeights(new int[] { 1, 1, 1 });
            }
        } else {
            sform.setWeights(new int[] { 10, 5 });
        }
    }

    /**
     * create a Text with the given title, height, textContent.
     * 
     * @param sform
     * @param title
     * @param height
     * @param textContent
     * @return
     */
    private Text createTextPart(SashForm sform, String title, int height, String textContent) {
        Group group = new Group(sform, SWT.NONE);
        group.setText(title);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = height;
        group.setLayout(gridLayout);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);

        Text text = new Text(group, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
        text.setText(textContent);
        text.setLayoutData(data);
        // addDropListener
        addDropVariableListenerToText(text);
        return text;
    }

    /**
     * when Text Is Empty, return true.
     * 
     * @return
     */
    public boolean whenTextIsEmpty(Text text) {
        return text.getText().trim().isEmpty();
    }

    /**
     * get the auto generated sql.
     */
    public String getAutoGeneratedSql() {
        Properties properties = UDIHelper.getUDITemplatesProperties();
        // get sql template depend on the language and selectTabNumber

        String key = PluginConstant.EMPTY_STRING;
        autoGenSql = PluginConstant.EMPTY_STRING;
        // when from tab0 change to tab1, get full sql
        if (selectTabNumber == 1) {
            if (IndicatorCategoryHelper.isUserDefCount(category)) {
                key = "SqlTemplate." + language + ".UDI.Count"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (IndicatorCategoryHelper.isUserDefRealValue(category)) {
                key = "SqlTemplate." + language + ".UDI.RealValue"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (IndicatorCategoryHelper.isUserDefFrequency(category)) {
                key = "SqlTemplate." + language + ".UDI.Frequency"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                key = "SqlTemplate." + language + ".UDI.Match"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else if (selectTabNumber == 2) {
            // for match is View Valid Rows template
            // for others is view rows template
            if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                key = "SqlTemplate." + language + ".UDI.Match.ViewValidRows"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (IndicatorCategoryHelper.isUserDefRealValue(category)) {
                key = "SqlTemplate." + language + ".UDI.RealValue.ViewRows"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (IndicatorCategoryHelper.isUserDefFrequency(category)) {
                key = "SqlTemplate." + language + ".UDI.Frequency.ViewRows"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (IndicatorCategoryHelper.isUserDefCount(category)) {
                key = "SqlTemplate." + language + ".UDI.Count.ViewRows"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else if (selectTabNumber == 3) {
            // for match is View Invalid Rows Template
            key = "SqlTemplate." + language + ".UDI.Match.ViewInvalidRows"; //$NON-NLS-1$ //$NON-NLS-2$
        } else if (selectTabNumber == 4) {
            // for match is View Valid Values Template
            key = "SqlTemplate." + language + ".UDI.Match.ViewValidValues"; //$NON-NLS-1$ //$NON-NLS-2$
        } else if (selectTabNumber == 5) {
            // for match is View Invalid Values Template
            key = "SqlTemplate." + language + ".UDI.Match.ViewInvalidValues"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        autoGenSql = properties.getProperty(key);

        // replace some variables in the auto generate sql
        if (definition instanceof UDIndicatorDefinition) {
            if (IndicatorCategoryHelper.isUserDefCount(category)) {
                // replace <WHERE_TEXT_FIELD>
                if (whenTextIsEmpty(tab0_count_where_var)) {
                    autoGenSql = StringUtils.replace(autoGenSql,
                            "WHERE " + GenericSQLHandler.UDI_WHERE + PluginConstant.SPACE_STRING //$NON-NLS-1$ 
                                    + GenericSQLHandler.AND_WHERE_CLAUSE, GenericSQLHandler.WHERE_CLAUSE);
                } else {
                    autoGenSql = StringUtils.replace(autoGenSql, GenericSQLHandler.UDI_WHERE, tab0_count_where_var.getText());
                }

            } else if (IndicatorCategoryHelper.isUserDefRealValue(category)) {
                // replace <COLUMN_EXPRESSION_TEXT_FIELD>
                autoGenSql = StringUtils.replace(autoGenSql, GenericSQLHandler.UDI_COLUMN, tab0_realvalue_column_var.getText());

                // replace <WHERE_TEXT_FIELD>
                if (whenTextIsEmpty(tab0_realvalue_where_var)) {
                    autoGenSql = StringUtils.replace(autoGenSql,
                            "WHERE " + GenericSQLHandler.UDI_WHERE + PluginConstant.SPACE_STRING //$NON-NLS-1$ 
                                    + GenericSQLHandler.AND_WHERE_CLAUSE, GenericSQLHandler.WHERE_CLAUSE);
                    autoGenSql = StringUtils.replace(autoGenSql,
                            "AND " + GenericSQLHandler.UDI_WHERE + PluginConstant.SPACE_STRING //$NON-NLS-1$ 
                                    + GenericSQLHandler.AND_WHERE_CLAUSE, GenericSQLHandler.AND_WHERE_CLAUSE);
                } else {
                    autoGenSql = StringUtils.replace(autoGenSql, GenericSQLHandler.UDI_WHERE, tab0_realvalue_where_var.getText());
                }

            } else if (IndicatorCategoryHelper.isUserDefFrequency(category)) {
                // replace <FIRST_COLUMN_EXPRESSION_TEXT_FIELD>
                // replace <SECOND_COLUMN_EXPRESSION_TEXT_FIELD>
                autoGenSql = new GenericSQLHandler(autoGenSql).replaceUDIFirstColumn(tab0_fre_first_var.getText())
                        .replaceUDISecondColumn(tab0_fre_second_var.getText()).getSqlString();

                // replace <WHERE_TEXT_FIELD>
                if (whenTextIsEmpty(tab0_fre_where_var)) {
                    autoGenSql = StringUtils.replace(autoGenSql,
                            "WHERE " + GenericSQLHandler.UDI_WHERE + PluginConstant.SPACE_STRING //$NON-NLS-1$ 
                                    + GenericSQLHandler.AND_WHERE_CLAUSE, GenericSQLHandler.WHERE_CLAUSE);
                    autoGenSql = StringUtils.replace(autoGenSql,
                            "AND " + GenericSQLHandler.UDI_WHERE + PluginConstant.SPACE_STRING //$NON-NLS-1$ 
                                    + GenericSQLHandler.AND_WHERE_CLAUSE, GenericSQLHandler.AND_WHERE_CLAUSE);
                } else {
                    autoGenSql = StringUtils.replace(autoGenSql, GenericSQLHandler.UDI_WHERE, tab0_fre_where_var.getText());
                }

                // replace <GROUP_BY_TEXT_FIELD>
                if (whenTextIsEmpty(tab0_fre_groupby_var)) {
                    autoGenSql = StringUtils.replace(autoGenSql, "GROUP BY " + GenericSQLHandler.UDI_GROUP_BY, ""); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    autoGenSql = StringUtils.replace(autoGenSql, GenericSQLHandler.UDI_GROUP_BY, tab0_fre_groupby_var.getText());
                }

                // replace <ORDER_BY_TEXT_FIELD>
                if (whenTextIsEmpty(tab0_fre_orderby_var)) {
                    autoGenSql = StringUtils.replace(autoGenSql, "ORDER BY " + GenericSQLHandler.UDI_ORDER_BY, ""); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    autoGenSql = StringUtils.replace(autoGenSql, GenericSQLHandler.UDI_ORDER_BY, tab0_fre_orderby_var.getText());
                }

            } else if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                // replace <MATCHING_EXPRESSION_TEXT_FIELD>
                autoGenSql = StringUtils.replace(autoGenSql, GenericSQLHandler.UDI_MATCHING, tab0_match_match_var.getText());

                // replace <WHERE_TEXT_FIELD>
                if (whenTextIsEmpty(tab0_match_where_var)) {
                    autoGenSql = StringUtils.replace(autoGenSql,
                            "WHERE " + GenericSQLHandler.UDI_WHERE + PluginConstant.SPACE_STRING //$NON-NLS-1$ 
                                    + GenericSQLHandler.AND_WHERE_CLAUSE, GenericSQLHandler.WHERE_CLAUSE);
                } else {
                    autoGenSql = StringUtils.replace(autoGenSql, GenericSQLHandler.UDI_WHERE, tab0_match_where_var.getText());
                }

            }
        }
        return autoGenSql;
    }

    /**
     * get the text content.
     * 
     * @param old content
     * @return
     */
    public String checkUseAutoGenSql(String oldContent) {
        // check whether use the auto generated sql
        if (useAutoGenSqlFlag && autoGenSql != null && !autoGenSql.equals(PluginConstant.EMPTY_STRING)) {
            return autoGenSql;
        }
        return oldContent;
    }

    /**
     * get tdExpression body Value, when it is null, return blank.
     * 
     * @return
     */
    public String getExpressValue(TdExpression tdExp) {
        return tdExp != null ? tdExp.getBody() : PluginConstant.EMPTY_STRING;
    }

    /**
     * create Tab Page content.
     * 
     * @param sform
     */
    public void createTabPage(SashForm sform) {
        ModifyListener listener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                useAutoGenSqlFlag = false;
                setResetButtonStatus(true);
                setTextOverWriteStatus(false);
            }

        };

        if (selectTabNumber == 0) {
            createTab0Page(sform);

        } else if (selectTabNumber == 1) {
            tab1_fullSql = createTextPart(sform, EXPREEION, 20, checkUseAutoGenSql(fullSqlContent));
            tab1_fullSql.addModifyListener(listener);

        } else if (selectTabNumber == 2) {
            if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                // for match dis View Valid Rows template
                tab2_match_viewvalidRows = createTextPart(sform, EXPREEION, 20,
                        checkUseAutoGenSql(getExpressValue(tempViewValidRowsExp)));
                tab2_match_viewvalidRows.addModifyListener(listener);
            } else {
                // for others is view rows template
                tab2_viewRows = createTextPart(sform, EXPREEION, 20, checkUseAutoGenSql(getExpressValue(tempViewRowsExp)));
                tab2_viewRows.addModifyListener(listener);
            }

        } else if (selectTabNumber == 3) {
            // for match is View Invalid Rows Template
            tab3_match_viewInvalidRows = createTextPart(sform, EXPREEION, 20,
                    checkUseAutoGenSql(getExpressValue(tempViewInvalidRowsExp)));
            tab3_match_viewInvalidRows.addModifyListener(listener);

        } else if (selectTabNumber == 4) {
            // for match is View Valid Values Template
            tab4_match_viewValidValues = createTextPart(sform, EXPREEION, 20,
                    checkUseAutoGenSql(getExpressValue(tempViewValidValuesExp)));
            tab4_match_viewValidValues.addModifyListener(listener);

        } else if (selectTabNumber == 5) {
            // for match is View Invalid Values Template
            tab5_match_viewInvalidValues = createTextPart(sform, EXPREEION, 20,
                    checkUseAutoGenSql(getExpressValue(tempViewInvalidValuesExp)));
            tab5_match_viewInvalidValues.addModifyListener(listener);
        }

    }

    /**
     * set Reset Button Status and the overwrite flag value. when reset button is true, the overwrite flag is false
     * 
     * @param isEnable
     */
    private void setResetButtonStatus(boolean isEnable) {
        if (selectTabNumber == 1) {
            resetButton_tab1.setEnabled(isEnable);
        } else if (selectTabNumber == 2) {
            if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                // for match is View Valid Rows template
                resetButton_tab2_match.setEnabled(isEnable);
            } else {
                // for others is view rows template
                resetButton_tab2_viewRows.setEnabled(isEnable);
            }
        } else if (selectTabNumber == 3) {
            resetButton_tab3_match.setEnabled(isEnable);
        } else if (selectTabNumber == 4) {
            resetButton_tab4_match.setEnabled(isEnable);
        } else if (selectTabNumber == 5) {
            resetButton_tab5_match.setEnabled(isEnable);
        }
    }

    /**
     * set Reset Button Status and the overwrite flag value. when reset button is true, the overwrite flag is false
     * 
     * @param isEnable
     */
    private void setTextOverWriteStatus(boolean canBeOverwriten) {
        if (selectTabNumber == 1) {
            canBeOverwriten_tab1 = canBeOverwriten;
        } else if (selectTabNumber == 2) {
            if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                // for match is View Valid Rows template
                canBeOverwriten_tab2_match = canBeOverwriten;
            } else {
                // for others is view rows template
                canBeOverwriten_tab2_viewRows = canBeOverwriten;
            }
        } else if (selectTabNumber == 3) {
            canBeOverwriten_tab3_match = canBeOverwriten;
        } else if (selectTabNumber == 4) {
            canBeOverwriten_tab4_match = canBeOverwriten;
        } else if (selectTabNumber == 5) {
            canBeOverwriten_tab5_match = canBeOverwriten;
        }
    }

    /**
     * create Tab0 Page content.
     * 
     * @param sform
     */
    public void createTab0Page(SashForm sform) {
        ModifyListener listener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                useAutoGenSqlFlag = true;
            }
        };

        if (IndicatorCategoryHelper.isUserDefCount(category)) {
            tab0_count_where_var = createTextPart(sform, WHERE_EXPRESSION_TITLE, 20,
                    getVaribleFromMap(GenericSQLHandler.UDI_WHERE));
            tab0_count_where_var.addModifyListener(listener);

        } else if (IndicatorCategoryHelper.isUserDefRealValue(category)) {
            tab0_realvalue_column_var = createTextPart(sform, COLUMN_EXPRESSION_TITLE, 10,
                    getVaribleFromMap(GenericSQLHandler.UDI_COLUMN));
            tab0_realvalue_column_var.addModifyListener(listener);

            tab0_realvalue_where_var = createTextPart(sform, WHERE_EXPRESSION_TITLE, 10,
                    getVaribleFromMap(GenericSQLHandler.UDI_WHERE));
            tab0_realvalue_where_var.addModifyListener(listener);

        } else if (IndicatorCategoryHelper.isUserDefFrequency(category)) {
            tab0_fre_first_var = createTextPart(sform, FIRST_COLUMN_TITLE, 5,
                    getVaribleFromMap(GenericSQLHandler.UDI_FIRST_COLUMN));
            tab0_fre_first_var.addModifyListener(listener);

            tab0_fre_second_var = createTextPart(sform, SECOND_COLUMN_TITLE, 5,
                    getVaribleFromMap(GenericSQLHandler.UDI_SECOND_COLUMN));
            tab0_fre_second_var.addModifyListener(listener);

            tab0_fre_where_var = createTextPart(sform, WHERE_EXPRESSION_TITLE, 5, getVaribleFromMap(GenericSQLHandler.UDI_WHERE));
            tab0_fre_where_var.addModifyListener(listener);

            tab0_fre_groupby_var = createTextPart(sform, GROUP_BY_TITLE, 5, getVaribleFromMap(GenericSQLHandler.UDI_GROUP_BY));
            tab0_fre_groupby_var.addModifyListener(listener);

            tab0_fre_orderby_var = createTextPart(sform, ORDER_BY_TITLE, 5, getVaribleFromMap(GenericSQLHandler.UDI_ORDER_BY));
            tab0_fre_orderby_var.addModifyListener(listener);

        } else if (IndicatorCategoryHelper.isUserDefMatching(category)) {
            tab0_match_match_var = createTextPart(sform, MATCH_EXPRESSION_TITLE, 10,
                    getVaribleFromMap(GenericSQLHandler.UDI_MATCHING));
            tab0_match_match_var.addModifyListener(listener);

            tab0_match_where_var = createTextPart(sform, WHERE_EXPRESSION_TITLE, 10,
                    getVaribleFromMap(GenericSQLHandler.UDI_WHERE));
            tab0_match_where_var.addModifyListener(listener);
        }

    }

    /**
     * get Varible value From variableMap.
     * 
     * @param key
     * @return
     */
    public String getVaribleFromMap(String key) {
        return variableMap == null ? PluginConstant.EMPTY_STRING : StringUtils.trimToEmpty(variableMap.get(key));
    }

    /**
     * create templates Table Part(only TAB0 use old paramaters, and others use new paramaters)
     * 
     * @param sform
     */
    public void createTemplatesTablePart(SashForm sform) {
        GridData data;
        final Table templatesTable = new Table(sform, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        templatesTable.setLinesVisible(false);
        templatesTable.setHeaderVisible(true);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 200;
        templatesTable.setLayoutData(data);

        DragSource ds = new DragSource(templatesTable, DND.DROP_MOVE);
        ds.setTransfer(new Transfer[] { TextTransfer.getInstance() });
        ds.addDragListener(new DragSourceAdapter() {

            @Override
            public void dragSetData(DragSourceEvent event) {
                event.data = templatesTable.getSelection()[0].getText();
            }
        });

        TableColumn column = new TableColumn(templatesTable, SWT.NONE);
        column.setText(TEMPLATES_TITLE);

        for (String template : templates) {
            TableItem item = new TableItem(templatesTable, SWT.NONE);
            item.setText(0, template);

        }
        templatesTable.getColumn(0).pack();
    }

    /**
     * add Drop Variable Listener To Text.
     * 
     * @param text
     */
    public void addDropVariableListenerToText(final Text text) {
        DropTarget dt = new DropTarget(text, DND.DROP_MOVE);

        dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
        dt.addDropListener(new DropTargetAdapter() {

            @Override
            public void drop(DropTargetEvent event) {
                String head, end;
                Text editText = text;
                head = editText.getText().substring(0, editText.getSelection().x);
                end = editText.getText().substring(editText.getSelection().x + editText.getSelectionCount());

                editText.setText(head + (String) event.data + end);
                editText.setFocus();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().setText(TITLE);

        if (!DQPreferenceManager.isBlockWeb()) {
            showHelp();
        }
    }

    /**
     * DOC yyi Comment method "showHelp".
     */
    protected void showHelp() {

        IContext context = HelpSystem.getContext(HelpPlugin.getDefault().getExpressionEditContextID());
        if (null != context && 0 < context.getRelatedTopics().length) {
            openTray(new HelpTray());
            ReusableHelpPart helpPart = ((HelpTray) getTray()).getHelpPart();
            helpPart.showURL(context.getRelatedTopics()[0].getHref());
        }
    }

    /**
     * get the full sql tempaltes text content.
     * 
     * @return
     */
    public String getResult() {
        return fullSqlContent;
    }

    /**
     * get this TdExpression.
     * 
     * @return
     */
    public TdExpression getTdExpression() {
        return tdExpression;
    }

    /**
     * get this IndicatorDefinition.
     * 
     * @return
     */
    public IndicatorDefinition getIndicatorDefinition() {
        return definition;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        // when the category is not user define indicator
        if (!IndicatorCategoryHelper.isUserDefCategory(category)) {
            fullSqlContent = editTextNotUDI.getText();
        } else {
            fullSqlContent = tab1_fullSql.getText();

            variableMap.clear();
            if (IndicatorCategoryHelper.isUserDefMatching(category)) {
                variableMap.put(GenericSQLHandler.UDI_MATCHING, tab0_match_match_var.getText());
                variableMap.put(GenericSQLHandler.UDI_WHERE, tab0_match_where_var.getText());
                storeExpForMatch();
            } else {
                if (IndicatorCategoryHelper.isUserDefCount(category)) {
                    variableMap.put(GenericSQLHandler.UDI_WHERE, tab0_count_where_var.getText());
                } else if (IndicatorCategoryHelper.isUserDefRealValue(category)) {
                    variableMap.put(GenericSQLHandler.UDI_COLUMN, tab0_realvalue_column_var.getText());
                    variableMap.put(GenericSQLHandler.UDI_WHERE, tab0_realvalue_where_var.getText());
                } else if (IndicatorCategoryHelper.isUserDefFrequency(category)) {
                    variableMap.put(GenericSQLHandler.UDI_FIRST_COLUMN, tab0_fre_first_var.getText());
                    variableMap.put(GenericSQLHandler.UDI_SECOND_COLUMN, tab0_fre_second_var.getText());
                    variableMap.put(GenericSQLHandler.UDI_WHERE, tab0_fre_where_var.getText());
                    variableMap.put(GenericSQLHandler.UDI_GROUP_BY, tab0_fre_groupby_var.getText());
                    variableMap.put(GenericSQLHandler.UDI_ORDER_BY, tab0_fre_orderby_var.getText());
                }
                storeViewRowsExp();
            }

            tdExpression.setExpressionVariableMap(variableMap);
        }
        super.okPressed();
    }

    /**
     * store Expression values when indicator is user define match.
     */
    public void storeExpForMatch() {
        storeTdExpValuesFromText(tempViewValidRowsExp, tab2_match_viewvalidRows);
        EList<TdExpression> viewValidRowsExpression = ((UDIndicatorDefinition) this.definition).getViewValidRowsExpression();
        removeThenAddToList(viewValidRowsExpression, tempViewValidRowsExp);

        storeTdExpValuesFromText(tempViewInvalidRowsExp, tab3_match_viewInvalidRows);
        EList<TdExpression> viewInvalidRowsExpression = ((UDIndicatorDefinition) this.definition).getViewInvalidRowsExpression();
        removeThenAddToList(viewInvalidRowsExpression, tempViewInvalidRowsExp);

        storeTdExpValuesFromText(tempViewValidValuesExp, tab4_match_viewValidValues);
        EList<TdExpression> viewValidValuesExpression = ((UDIndicatorDefinition) this.definition).getViewValidValuesExpression();
        removeThenAddToList(viewValidValuesExpression, tempViewValidValuesExp);

        storeTdExpValuesFromText(tempViewInvalidValuesExp, tab5_match_viewInvalidValues);
        EList<TdExpression> viewInvalidValuesExpression = ((UDIndicatorDefinition) this.definition)
                .getViewInvalidValuesExpression();
        removeThenAddToList(viewInvalidValuesExpression, tempViewInvalidValuesExp);
    }

    /**
     * store ViewRows Expression value.
     */
    public void storeViewRowsExp() {
        storeTdExpValuesFromText(tempViewRowsExp, tab2_viewRows);
        EList<TdExpression> viewRowsExpression = ((UDIndicatorDefinition) this.definition).getViewRowsExpression();
        removeThenAddToList(viewRowsExpression, tempViewRowsExp);
    }

    /**
     * set Text value as body of TdExpression.
     * 
     * @param tdExp
     * @param text
     */
    public void storeTdExpValuesFromText(TdExpression tdExp, Text text) {
        tdExp.setBody(text.getText());
        tdExp.setVersion(version);
        tdExp.setLanguage(language);
        tdExp.setModificationDate(UDIUtils.getCurrentDateTime());
    }

    /**
     * remove current language expression, and then add the new one to list.
     * 
     * @param list
     * @param newTdExp
     */
    public void removeThenAddToList(EList<TdExpression> list, TdExpression newTdExp) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (isCurrentLanguageAndVersion(list.get(i))) {
                    list.remove(i);
                }
            }
        }
        list.add(newTdExp);
    }
}
