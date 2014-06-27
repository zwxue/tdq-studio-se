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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.platform.PluginChecker;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.provider.BlockingKeysTableLabelProvider;
import org.talend.dataprofiler.core.ui.dialog.provider.MatchRulesTableLabelProvider;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
import org.talend.dataquality.rules.AlgorithmDefinition;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dataquality.rules.SurvivorshipKeyDefinition;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class MatchRuleElementTreeSelectionDialog extends ElementTreeSelectionDialog {

    private TableViewer blockingKeysTable;

    private TableViewer matchingRulesTable;

    private List<String> inputColumnNames;

    private List<String> survivorshipKeys;

    private List<String> lookupColumnNames;

    private Button overwriteBTN;

    private boolean isOverwrite = false;

    private int dialogType;

    private MatchRuleDefinition matchRuleDefinitionInput;

    public static final String T_SWOOSH_ALGORITHM = "T_SwooshAlgorithm"; //$NON-NLS-1$

    public static final int GENKEY_TYPE = 0;

    public static final int MATCHGROUP_TYPE = 1;

    public static final int MATCH_ANALYSIS_TYPE = 2;

    public static final int RECORD_MATCHING_TYPE = 3;

    public static final int SUGGEST_TYPE = 4;

    private Composite matchRulesTableComposite = null;

    /**
     * DOC yyin DQRuleCheckedTreeSelectionDialog constructor comment.
     * 
     * @param parent
     * @param labelProvider
     * @param contentProvider
     */
    public MatchRuleElementTreeSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider,
            int componentType) {
        super(parent, labelProvider, contentProvider);
        this.dialogType = componentType;
        init();
        addFilter();
        addValidator();
        setHelpAvailable(Boolean.FALSE);
    }

    // override this method for add the talend help. TDQ-8236
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        if (PluginChecker.isTDQLoaded()) {
            Button help = createButton(parent, IDialogConstants.HELP_ID, IDialogConstants.HELP_LABEL, false);
            help.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    PlatformUI.getWorkbench().getHelpSystem().displayHelp("org.talend.help.match_rule_selector");//$NON-NLS-1$
                }
            });
        }
    }

    /**
     * DOC yyin Comment method "addValidator".
     */
    private void addValidator() {
        setValidator(new ISelectionStatusValidator() {

            public IStatus validate(Object[] selection) {
                IStatus status = new Status(IStatus.OK, CorePlugin.PLUGIN_ID, StringUtils.EMPTY);
                if (selection == null || (selection != null && selection.length > 1)) {
                    status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
                            DefaultMessagesImpl.getString("MatchRuleCheckedTreeSelectionDialog.validate")); //$NON-NLS-1$
                    return status;
                } else {
                    // when the selected rule has no match & block keys, not validate(has block,no match, can validate )
                    for (Object selectObject : selection) {
                        if (selectObject instanceof IFile) {
                            IFile file = (IFile) selectObject;
                            if (FactoriesUtil.DQRULE.equals(file.getFileExtension())) {
                                MatchRuleDefinition matchRuleDef = DQRuleResourceFileHelper.getInstance().findMatchRule(file);
                                if ((matchRuleDef.getBlockKeys() == null || matchRuleDef.getBlockKeys().size() < 1)
                                        && (matchRuleDef.getMatchRules() == null || matchRuleDef.getMatchRules().size() < 1)) {
                                    status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
                                            DefaultMessagesImpl.getString("MatchRuleCheckedTreeSelectionDialog.emptyRule")); //$NON-NLS-1$
                                    return status;
                                }

                                // check if exist duplicated Match Keys
                                Set<String> duplicatedSurvivorshipKeys = new HashSet<String>();
                                for (SurvivorshipKeyDefinition skDef : matchRuleDef.getSurvivorshipKeys()) {
                                    if (!isOverwrite) {
                                        if (skDef != null && getSurvivorshipKeys().contains(skDef.getName())) {
                                            duplicatedSurvivorshipKeys.add(skDef.getName());
                                        }
                                    }
                                }
                                if (!duplicatedSurvivorshipKeys.isEmpty()) {
                                    status = new Status(
                                            IStatus.ERROR,
                                            CorePlugin.PLUGIN_ID,
                                            DefaultMessagesImpl
                                                    .getString(
                                                            "MatchRuleCheckedTreeSelectionDialog.duplicatedMatchKey", duplicatedSurvivorshipKeys.toString())); //$NON-NLS-1$
                                    return status;
                                }

                                // for component tMatchGroup and tRecordMatching when the imported rule's algorithm is
                                // "T_Swoosh", block importing, !!!!NOTE!!! these code are a temporary solution, we will
                                // support the importing of Match Rule which's algorithm is t-swoosh for component
                                // tMatchGroup and tRecordMatching later
                                if ((dialogType == MATCHGROUP_TYPE || dialogType == RECORD_MATCHING_TYPE)
                                        && T_SWOOSH_ALGORITHM.equals(matchRuleDef.getRecordLinkageAlgorithm())) {
                                    status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
                                            "The algorithm of this Match Rule is t-swoosh, can't import it now!"); //$NON-NLS-1$
                                    return status;
                                }
                                // ~~~~~~~~~~

                                String warningMsg = StringUtils.EMPTY;

                                boolean needColumnWarning = false;
                                if (dialogType != MATCHGROUP_TYPE && dialogType != RECORD_MATCHING_TYPE) {
                                    for (BlockKeyDefinition bkd : matchRuleDef.getBlockKeys()) {
                                        boolean hasColumnMatch = false;
                                        for (String col : inputColumnNames) {
                                            if (col.equalsIgnoreCase(bkd.getColumn()) || col.equalsIgnoreCase(bkd.getName())) {
                                                hasColumnMatch = true;
                                                break;
                                            }
                                        }
                                        if (!hasColumnMatch) {
                                            needColumnWarning = true;
                                            break;
                                        }
                                    }
                                }
                                if (dialogType != GENKEY_TYPE) {
                                    for (MatchRule rule : matchRuleDef.getMatchRules()) {
                                        EList<MatchKeyDefinition> matchKeys = rule.getMatchKeys();
                                        for (MatchKeyDefinition mkd : matchKeys) {
                                            boolean hasColumnMatch = false;
                                            for (String col : inputColumnNames) {
                                                if (col.equalsIgnoreCase(mkd.getColumn()) || col.equalsIgnoreCase(mkd.getName())) {
                                                    hasColumnMatch = true;
                                                    break;
                                                }
                                            }
                                            if (!hasColumnMatch) {
                                                needColumnWarning = true;
                                                break;
                                            }
                                        }
                                        if (needColumnWarning) {
                                            break;
                                        }
                                    }
                                }
                                if (needColumnWarning) {
                                    warningMsg = DefaultMessagesImpl
                                            .getString("MatchRuleCheckedTreeSelectionDialog.noColumnMatchWarning"); //$NON-NLS-1$
                                }

                                if (!StringUtils.EMPTY.equals(warningMsg)) {
                                    status = new Status(IStatus.WARNING, CorePlugin.PLUGIN_ID, warningMsg);
                                }
                            }
                        }
                    }
                }

                return status;
            }

        });
    }

    /**
     * DOC yyin Comment method "init".
     */
    private void init() {
        if (dialogType != SUGGEST_TYPE) {
            setInput(ResourceManager.getRulesMatcherFolder());
        }
        setTitle(DefaultMessagesImpl.getString("DQRuleCheckedTreeSelectionDialog.title")); //$NON-NLS-1$
        setMessage(DefaultMessagesImpl.getString("DQRuleCheckedTreeSelectionDialog.rule")); //$NON-NLS-1$
    }

    /**
     * DOC yyin Comment method "addFilter".
     */
    private void addFilter() {
        addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    if (FactoriesUtil.DQRULE.equals(file.getFileExtension())) {
                        return true;
                    }
                } else if (element instanceof IFolder) {
                    IFolder folder = (IFolder) element;
                    if (folder.getName().startsWith(".")) { //$NON-NLS-1$
                        return false;
                    }
                    return ResourceService.isSubFolder(ResourceManager.getRulesMatcherFolder(), folder);// getRulesMatcherFolder,getRulesFolder
                }
                return false;
            }
        });
    }

    @Override
    protected Control createDialogArea(final Composite parent) {

        final SashForm form = new SashForm(parent, SWT.SMOOTH | SWT.VERTICAL | SWT.FILL);
        form.setSize(Math.min(Display.getCurrent().getActiveShell().getSize().x, 800), 580);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
        form.setLayoutData(data);
        Composite composite = (Composite) super.createDialogArea(form);
        getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                if (dialogType != SUGGEST_TYPE) {
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    Object[] array = selection.toArray();
                    if (array.length == 1) {
                        if (array[0] != null && array[0] instanceof IFile) {
                            MatchRuleDefinition matchRuleDefinition = DQRuleResourceFileHelper.getInstance().findMatchRule(
                                    (IFile) array[0]);
                            if (matchRuleDefinition != null) {
                                if (blockingKeysTable != null) {
                                    blockingKeysTable.setInput(getBlockingKeysFromFiles(array, true));
                                }
                                if (matchingRulesTable != null) {
                                    matchRulesTableComposite.dispose();
                                    if (StringUtils.equals(RecordMatcherType.T_SwooshAlgorithm.name(),
                                            matchRuleDefinition.getRecordLinkageAlgorithm())) {
                                        createSelectMatchRulesTableTswoosh(form);
                                    } else {
                                        createSelectMatchRulesTableVsr(form);
                                    }
                                    matchingRulesTable.setInput(getMatchRulesFromFiles(array, true));
                                    // refresh the dialog
                                    matchRulesTableComposite.getParent().layout();
                                    matchRulesTableComposite.getParent().redraw();
                                }
                            }
                        }
                    }
                }
            }
        });
        if (dialogType == GENKEY_TYPE) {
            createSelectBlockingKeysTable(form);
            form.setWeights(new int[] { 3, 2 });
        } else if (dialogType == MATCHGROUP_TYPE || dialogType == RECORD_MATCHING_TYPE) {
            createSelectMatchRulesTableVsr(form);
            form.setWeights(new int[] { 3, 2 });
        } else if (dialogType == MATCH_ANALYSIS_TYPE) {
            createSelectBlockingKeysTable(form);
            createSelectMatchRulesTableVsr(form);
            form.setWeights(new int[] { 5, 2, 3 });
        } else if (dialogType == SUGGEST_TYPE) {
            createSelectBlockingKeysTable(form);
            createSelectMatchRulesTableVsr(form);
            form.setWeights(new int[] { 5, 2, 3 });
            if (blockingKeysTable != null) {
                blockingKeysTable.setInput(getBlockingKeysFromRules(matchRuleDefinitionInput, true));
            }
            if (matchingRulesTable != null) {
                matchingRulesTable.setInput(getMatchRulesFromRules(matchRuleDefinitionInput, true));
            }
        }
        createCheckerArea(composite);
        return composite;

    }

    private Composite createCheckerArea(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);

        GridLayout innerLayout = new GridLayout();
        innerLayout.numColumns = 1;
        composite.setLayout(innerLayout);
        composite.setFont(parent.getFont());

        overwriteBTN = new Button(composite, SWT.CHECK);
        overwriteBTN.setText(DefaultMessagesImpl.getString("DQRuleCheckedTreeSelectionDialog.isOverwrite")); //$NON-NLS-1$
        overwriteBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                isOverwrite = overwriteBTN.getSelection();
                updateOKStatus();
            }

        });

        return composite;
    }

    private void createSelectBlockingKeysTable(Composite parent) {
        Composite composite = new Composite(parent, SWT.None);
        GridLayout layout = new GridLayout();
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        composite.setLayout(layout);

        blockingKeysTable = new TableViewer(composite, SWT.BORDER);
        Table table = blockingKeysTable.getTable();
        TableColumn c1 = new TableColumn(table, SWT.NULL);
        c1.setText(DefaultMessagesImpl.getString("BlockingKeyTableComposite.BLOCKING_KEY_NAME")); //$NON-NLS-1$
        TableColumn c2 = new TableColumn(table, SWT.NULL);
        c2.setText(DefaultMessagesImpl.getString("BlockingKeyTableComposite.PRECOLUMN")); //$NON-NLS-1$
        TableColumn c3 = new TableColumn(table, SWT.NULL);
        c3.setText(DefaultMessagesImpl.getString("BlockingKeyTableComposite.PRE_ALGO")); //$NON-NLS-1$
        TableColumn c4 = new TableColumn(table, SWT.NULL);
        c4.setText(DefaultMessagesImpl.getString("BlockingKeyTableComposite.PRE_VALUE")); //$NON-NLS-1$
        TableColumn c5 = new TableColumn(table, SWT.NULL);
        c5.setText(DefaultMessagesImpl.getString("BlockingKeyTableComposite.KEY_ALGO")); //$NON-NLS-1$
        TableColumn c6 = new TableColumn(table, SWT.NULL);
        c6.setText(DefaultMessagesImpl.getString("BlockingKeyTableComposite.KEY_VALUE")); //$NON-NLS-1$
        TableColumn c7 = new TableColumn(table, SWT.NULL);
        c7.setText(DefaultMessagesImpl.getString("BlockingKeyTableComposite.POST_ALGO")); //$NON-NLS-1$
        TableColumn c8 = new TableColumn(table, SWT.NULL);
        c8.setText(DefaultMessagesImpl.getString("BlockingKeyTableComposite.POST_VALUE")); //$NON-NLS-1$
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableLayout tableLayout = new TableLayout();
        for (int i = 0; i < 8; i++) {
            tableLayout.addColumnData(new ColumnWeightData(1, 120, true));
        }
        table.setLayout(tableLayout);

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
        table.setLayoutData(data);

        blockingKeysTable.setContentProvider(new ArrayContentProvider());
        blockingKeysTable.setLabelProvider(new BlockingKeysTableLabelProvider(inputColumnNames));
    }

    private void createSelectMatchRulesTableVsr(Composite parent) {
        matchRulesTableComposite = new Composite(parent, SWT.None);
        GridLayout layout = new GridLayout();
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        matchRulesTableComposite.setLayout(layout);

        matchingRulesTable = new TableViewer(matchRulesTableComposite, SWT.BORDER);
        Table table = matchingRulesTable.getTable();
        TableColumn c1 = new TableColumn(table, SWT.NULL);
        c1.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.MATCH_KEY_NAME")); //$NON-NLS-1$
        TableColumn c2 = new TableColumn(table, SWT.NULL);
        c2.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.INPUT_COLUMN")); //$NON-NLS-1$
        TableColumn c3 = new TableColumn(table, SWT.NULL);
        c3.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.MATCHING_TYPE")); //$NON-NLS-1$
        TableColumn c4 = new TableColumn(table, SWT.NULL);
        c4.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.CUSTOM_MATCHER_CLASS")); //$NON-NLS-1$
        TableColumn c5 = new TableColumn(table, SWT.NULL);
        c5.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.CONFIDENCE_WEIGHT")); //$NON-NLS-1$
        TableColumn c6 = new TableColumn(table, SWT.NULL);
        c6.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.HANDLE_NULL")); //$NON-NLS-1$
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableLayout tableLayout = new TableLayout();
        for (int i = 0; i < 6; i++) {
            tableLayout.addColumnData(new ColumnWeightData(1, 150, true));
        }
        table.setLayout(tableLayout);

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
        table.setLayoutData(data);

        matchingRulesTable.setContentProvider(new ArrayContentProvider());
        matchingRulesTable.setLabelProvider(new MatchRulesTableLabelProvider(inputColumnNames));
    }

    private void createSelectMatchRulesTableTswoosh(Composite parent) {
        matchRulesTableComposite = new Composite(parent, SWT.None);
        GridLayout layout = new GridLayout();
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        matchRulesTableComposite.setLayout(layout);

        matchingRulesTable = new TableViewer(matchRulesTableComposite, SWT.BORDER);
        Table table = matchingRulesTable.getTable();
        TableColumn c1 = new TableColumn(table, SWT.NULL);
        c1.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.MATCH_KEY_NAME")); //$NON-NLS-1$
        TableColumn c2 = new TableColumn(table, SWT.NULL);
        c2.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.INPUT_COLUMN")); //$NON-NLS-1$
        TableColumn c3 = new TableColumn(table, SWT.NULL);
        c3.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.MATCHING_TYPE")); //$NON-NLS-1$
        TableColumn c4 = new TableColumn(table, SWT.NULL);
        c4.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.CUSTOM_MATCHER_CLASS")); //$NON-NLS-1$
        TableColumn c5 = new TableColumn(table, SWT.NULL);
        c5.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.CONFIDENCE_WEIGHT")); //$NON-NLS-1$
        TableColumn c6 = new TableColumn(table, SWT.NULL);
        c6.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.HANDLE_NULL")); //$NON-NLS-1$
        TableColumn c7 = new TableColumn(table, SWT.NULL);
        c7.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.THRESHOLD")); //$NON-NLS-1$
        TableColumn c8 = new TableColumn(table, SWT.NULL);
        c8.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.SURVIVORSHIP_FUNCTION")); //$NON-NLS-1$
        TableColumn c9 = new TableColumn(table, SWT.NULL);
        c9.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.PARAMETER")); //$NON-NLS-1$
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableLayout tableLayout = new TableLayout();
        for (int i = 0; i < 9; i++) {
            tableLayout.addColumnData(new ColumnWeightData(1, 150, true));
        }
        table.setLayout(tableLayout);

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
        table.setLayoutData(data);

        matchingRulesTable.setContentProvider(new ArrayContentProvider());
        matchingRulesTable.setLabelProvider(new MatchRulesTableLabelProvider(inputColumnNames));
    }

    public boolean isOverwrite() {
        return isOverwrite;
    }

    public List<Map<String, String>> getBlockingKeysFromFiles(Object[] files) {
        return getBlockingKeysFromFiles(files, false);
    }

    public List<Map<String, String>> getBlockingKeysFromFiles(Object[] files, boolean retrieveDisplayValue) {
        List<Map<String, String>> ruleValues = new ArrayList<Map<String, String>>();
        for (Object rule : files) {
            if (rule instanceof IFile) {
                MatchRuleDefinition matchRuleDefinition = DQRuleResourceFileHelper.getInstance().findMatchRule((IFile) rule);
                ruleValues.addAll(getBlockingKeysFromRules(matchRuleDefinition, retrieveDisplayValue));
            }
        }
        return ruleValues;
    }

    private String matchExistingColumnForBlockingKey(BlockKeyDefinition blockingKey) {
        String matchedColumnName = StringUtils.EMPTY;
        for (String inputColumnName : getInputColumnNames()) {
            if (inputColumnName.equalsIgnoreCase(blockingKey.getColumn())
                    || inputColumnName.equalsIgnoreCase(blockingKey.getName())) {
                matchedColumnName = inputColumnName;
                break;
            }
        }
        return matchedColumnName;
    }

    public List<Map<String, String>> getMatchRulesFromFiles(Object[] files) {
        return getMatchRulesFromFiles(files, false);
    }

    /**
     * DOC sizhaoliu Comment method "getMatchRulesFromFiles".
     * 
     * @param files
     * @param retrieveDisplayValue get the display value when this parameter is set to true, otherwise, get the
     * component value.
     * @return
     */
    public List<Map<String, String>> getMatchRulesFromFiles(Object[] files, boolean retrieveDisplayValue) {
        List<Map<String, String>> ruleValues = new ArrayList<Map<String, String>>();
        for (Object rule : files) {
            if (rule instanceof IFile) {
                MatchRuleDefinition matchRuleDefinition = DQRuleResourceFileHelper.getInstance().findMatchRule((IFile) rule);
                ruleValues.addAll(getMatchRulesFromRules(matchRuleDefinition, retrieveDisplayValue));
            }
        }
        return ruleValues;
    }

    private String matchExistingColumnForMatchRule(MatchKeyDefinition matchKey) {
        String matchedColumnName = StringUtils.EMPTY;
        for (String inputColumnName : getInputColumnNames()) {
            if (inputColumnName.equalsIgnoreCase(matchKey.getColumn()) || inputColumnName.equalsIgnoreCase(matchKey.getName())) {
                matchedColumnName = inputColumnName;
                break;
            }
        }
        return matchedColumnName;
    }

    private List<Map<String, String>> getBlockingKeysFromRules(MatchRuleDefinition matchRuleDefinition,
            boolean retrieveDisplayValue) {

        if (matchRuleDefinition != null) {
            List<Map<String, String>> ruleValues = new ArrayList<Map<String, String>>();
            for (BlockKeyDefinition bkDefinition : matchRuleDefinition.getBlockKeys()) {

                Map<String, String> pr = new HashMap<String, String>();
                pr.put(BlockingKeysTableLabelProvider.BLOCKING_KEY_NAME, null == bkDefinition.getName() ? StringUtils.EMPTY
                        : bkDefinition.getName());

                String matchedColumnName = matchExistingColumnForBlockingKey(bkDefinition);
                pr.put(BlockingKeysTableLabelProvider.PRECOLUMN, null == matchedColumnName ? StringUtils.EMPTY
                        : matchedColumnName);

                pr.put(BlockingKeysTableLabelProvider.PRE_ALGO, null == bkDefinition.getPreAlgorithm() ? StringUtils.EMPTY
                        : bkDefinition.getPreAlgorithm().getAlgorithmType());
                pr.put(BlockingKeysTableLabelProvider.PRE_VALUE, null == bkDefinition.getPreAlgorithm() ? StringUtils.EMPTY
                        : bkDefinition.getPreAlgorithm().getAlgorithmParameters());

                pr.put(BlockingKeysTableLabelProvider.KEY_ALGO, null == bkDefinition.getAlgorithm() ? StringUtils.EMPTY
                        : bkDefinition.getAlgorithm().getAlgorithmType());
                pr.put(BlockingKeysTableLabelProvider.KEY_VALUE, null == bkDefinition.getAlgorithm() ? StringUtils.EMPTY
                        : bkDefinition.getAlgorithm().getAlgorithmParameters());

                pr.put(BlockingKeysTableLabelProvider.POST_ALGO, null == bkDefinition.getPostAlgorithm() ? StringUtils.EMPTY
                        : bkDefinition.getPostAlgorithm().getAlgorithmType());
                pr.put(BlockingKeysTableLabelProvider.POST_VALUE, null == bkDefinition.getPostAlgorithm() ? StringUtils.EMPTY
                        : bkDefinition.getPostAlgorithm().getAlgorithmParameters());
                ruleValues.add(pr);
            }
            return ruleValues;
        }
        return null;
    }

    private List<Map<String, String>> getMatchRulesFromRules(MatchRuleDefinition matchRuleDefinition, boolean retrieveDisplayValue) {

        if (matchRuleDefinition != null) {
            List<Map<String, String>> ruleValues = new ArrayList<Map<String, String>>();
            for (MatchRule matchRule : matchRuleDefinition.getMatchRules()) {
                for (MatchKeyDefinition matchKey : matchRule.getMatchKeys()) {
                    Map<String, String> pr = new HashMap<String, String>();
                    pr.put(MatchRulesTableLabelProvider.MATCH_KEY_NAME,
                            null == matchKey.getName() ? StringUtils.EMPTY : matchKey.getName());

                    String matchedColumnName = matchExistingColumnForMatchRule(matchKey);
                    pr.put(MatchRulesTableLabelProvider.INPUT_COLUMN, null == matchedColumnName ? StringUtils.EMPTY
                            : matchedColumnName);

                    if (getLookupColumnNames().size() > 0) {
                        for (String lookupColumnName : getLookupColumnNames()) {
                            if (lookupColumnName.equalsIgnoreCase(matchKey.getColumn())
                                    || lookupColumnName.equalsIgnoreCase(matchKey.getName())) {
                                pr.put("LOOKUP_COLUMN", null == matchKey.getColumn() ? StringUtils.EMPTY : lookupColumnName); //$NON-NLS-1$
                                break;
                            }
                        }
                    }

                    String algorithmType = matchKey.getAlgorithm().getAlgorithmType();
                    if (retrieveDisplayValue) {
                        pr.put(MatchRulesTableLabelProvider.MATCHING_TYPE, null == algorithmType ? StringUtils.EMPTY
                                : AttributeMatcherType.valueOf(algorithmType).getLabel());
                    } else {
                        pr.put(MatchRulesTableLabelProvider.MATCHING_TYPE, null == algorithmType ? StringUtils.EMPTY
                                : AttributeMatcherType.valueOf(algorithmType).getComponentValue());
                    }

                    // MOD sizhaoliu TDQ-8431 split the value by "||" and take the second part as custom class value
                    String algoParams = matchKey.getAlgorithm().getAlgorithmParameters();
                    if (algoParams != null) {
                        int idxSeparator = algoParams.indexOf("||"); //$NON-NLS-1$
                        if (idxSeparator > 0 && algoParams.length() > idxSeparator + 2) {
                            algoParams = "\"" + algoParams.substring(idxSeparator + 2) + "\""; //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                    pr.put(MatchRulesTableLabelProvider.CUSTOM_MATCHER, null == algoParams ? StringUtils.EMPTY : algoParams);
                    pr.put(MatchRulesTableLabelProvider.CONFIDENCE_WEIGHT, String.valueOf(matchKey.getConfidenceWeight()));

                    if (retrieveDisplayValue) {
                        pr.put(MatchRulesTableLabelProvider.HANDLE_NULL, null == matchKey.getHandleNull() ? StringUtils.EMPTY
                                : HandleNullEnum.getTypeByValue(matchKey.getHandleNull()).getLabel());
                    } else {
                        pr.put(MatchRulesTableLabelProvider.HANDLE_NULL, null == matchKey.getHandleNull() ? StringUtils.EMPTY
                                : matchKey.getHandleNull());
                    }

                    // set threshold
                    pr.put(MatchRulesTableLabelProvider.THRESHOLD, String.valueOf(matchKey.getThreshold()));

                    // set survivorship function and parameter
                    AlgorithmDefinition algorithmDefinition = getSurvivorshipFunctionAlgorithm(matchKey, matchRuleDefinition);
                    pr.put(MatchRulesTableLabelProvider.SURVIVORSHIP_FUNCTION,
                            algorithmDefinition != null && algorithmDefinition.getAlgorithmType() != null ? algorithmDefinition
                                    .getAlgorithmType() : StringUtils.EMPTY);
                    pr.put(MatchRulesTableLabelProvider.PARAMETER,
                            algorithmDefinition != null && algorithmDefinition.getAlgorithmParameters() != null ? algorithmDefinition
                                    .getAlgorithmParameters() : StringUtils.EMPTY);
                    ruleValues.add(pr);
                }
            }
            return ruleValues;
        }
        return null;
    }

    /**
     * DOC xqliu Comment method "getSurvivorshipFunctionAlgorithm".
     * 
     * @param matchKey
     * @param matchRuleDefinition
     * @return
     */
    private AlgorithmDefinition getSurvivorshipFunctionAlgorithm(MatchKeyDefinition matchKey,
            MatchRuleDefinition matchRuleDefinition) {
        EList<SurvivorshipKeyDefinition> survivorshipKeys = matchRuleDefinition.getSurvivorshipKeys();
        for (SurvivorshipKeyDefinition survivorshipKeyDefinition : survivorshipKeys) {
            if (StringUtils.equals(matchKey.getName(), survivorshipKeyDefinition.getName())) {
                return survivorshipKeyDefinition.getFunction();
            }
        }
        return null;
    }

    public List<String> getInputColumnNames() {
        if (inputColumnNames == null) {
            inputColumnNames = new ArrayList<String>();
        }
        return inputColumnNames;
    }

    public void setInputColumnNames(List<String> inputColumnNames) {
        this.inputColumnNames = inputColumnNames;
    }

    /**
     * Getter for survivorshipKeys.
     * 
     * @return the survivorshipKeys
     */
    public List<String> getSurvivorshipKeys() {
        if (this.survivorshipKeys == null) {
            this.survivorshipKeys = new ArrayList<String>();
        }
        return this.survivorshipKeys;
    }

    /**
     * Sets the survivorshipKeys.
     * 
     * @param survivorshipKeys the survivorshipKeys to set
     */
    public void setSurvivorshipKeys(List<String> survivorshipKeys) {
        this.survivorshipKeys = survivorshipKeys;
    }

    public List<String> getLookupColumnNames() {
        if (lookupColumnNames == null) {
            lookupColumnNames = new ArrayList<String>();
        }
        return lookupColumnNames;
    }

    public void setLookupColumnNames(List<String> lookupColumnNames) {
        this.lookupColumnNames = lookupColumnNames;
    }

    /**
     * DOC sizhaoliu Comment method "setMatchRuleDefinitionInput".
     * 
     * @param mrDef
     */
    public void setMatchRuleDefinitionInput(MatchRuleDefinition matchRuleDefinitionInput) {
        this.matchRuleDefinitionInput = matchRuleDefinitionInput;
    }

}
