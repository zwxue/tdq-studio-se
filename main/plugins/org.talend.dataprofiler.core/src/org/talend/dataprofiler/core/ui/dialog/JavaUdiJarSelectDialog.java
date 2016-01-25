// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;

/**
 * @author zshen
 * 
 */
public class JavaUdiJarSelectDialog extends SelectionStatusDialog {

    private CheckboxTreeViewer fViewer;

    private ILabelProvider fLabelProvider;

    private ITreeContentProvider fContentProvider;

    private ISelectionStatusValidator fValidator = null;

    private ViewerComparator fComparator;

    private String fEmptyListMessage = WorkbenchMessages.CheckedTreeSelectionDialog_nothing_available;

    private IStatus fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, 0, "", null); //$NON-NLS-1$

    private List fFilters;

    private Object fInput;

    private boolean fIsEmpty;

    private int fWidth = 60;

    private int fHeight = 18;

    private boolean fContainerMode;

    private Object[] fExpandedElements;

    private List<Object> elements;

    // ADD msjian 2011-7-14 22092 feature: Java UDI: not convinient to delete udi jar files
    boolean isSelectTab;

    private int tableFolderHeight = 25;

    private CheckboxTreeViewer fManageViewer;

    // ADD end

    // ADD msjian 2011-8-9 TDQ-3199 fixed: Make it convenient to delete the jar which is used already.
    private Text jarPathText;

    private Map<Object, Boolean> selectedJars;

    private IndicatorDefinition definition;

    // ADD end

    /**
     * Constructs an instance of <code>ElementTreeSelectionDialog</code>.
     * 
     * @param parent The shell to parent from.
     * @param labelProvider the label provider to render the entries
     * @param contentProvider the content provider to evaluate the tree structure
     */
    public JavaUdiJarSelectDialog(IndicatorDefinition definition, Shell parent, ILabelProvider labelProvider,
            ITreeContentProvider contentProvider) {
        super(parent);
        fLabelProvider = labelProvider;
        fContentProvider = contentProvider;
        setResult(new ArrayList(0));
        setStatusLineAboveButtons(true);
        fContainerMode = false;
        fExpandedElements = null;
        // ADD msjian 2011-8-9 TDQ-3199 fixed: Make it convenient to delete the jar which is used already.
        selectedJars = new HashMap<Object, Boolean>();
        this.definition = definition;
    }

    /**
     * If set, the checked /gray state of containers (inner nodes) is derived from the checked state of its leaf nodes.
     * 
     * @param containerMode The containerMode to set
     */
    public void setContainerMode(boolean containerMode) {
        fContainerMode = containerMode;
    }

    /**
     * Sets the initial selection. Convenience method.
     * 
     * @param selection the initial selection.
     */
    public void setInitialSelection(Object selection) {
        setInitialSelections(new Object[] { selection });
    }

    /**
     * Sets the message to be displayed if the list is empty.
     * 
     * @param message the message to be displayed.
     */
    public void setEmptyListMessage(String message) {
        fEmptyListMessage = message;
    }

    /**
     * Sets the sorter used by the tree viewer.
     * 
     * @param sorter
     * @deprecated since 3.3, use {@link CheckedTreeSelectionDialog#setComparator(ViewerComparator)} instead
     */
    @Deprecated
    public void setSorter(ViewerSorter sorter) {
        fComparator = sorter;
    }

    /**
     * Sets the comparator used by the tree viewer.
     * 
     * @param comparator
     * @since 3.3
     */
    public void setComparator(ViewerComparator comparator) {
        fComparator = comparator;
    }

    /**
     * Adds a filter to the tree viewer.
     * 
     * @param filter a filter.
     */
    public void addFilter(ViewerFilter filter) {
        if (fFilters == null) {
            fFilters = new ArrayList(4);
        }
        fFilters.add(filter);
    }

    /**
     * Sets an optional validator to check if the selection is valid. The validator is invoked whenever the selection
     * changes.
     * 
     * @param validator the validator to validate the selection.
     */
    public void setValidator(ISelectionStatusValidator validator) {
        fValidator = validator;
    }

    /**
     * Sets the tree input.
     * 
     * @param input the tree input.
     */
    public void setInput(Object input) {
        fInput = input;
    }

    /**
     * Expands elements in the tree.
     * 
     * @param elements The elements that will be expanded.
     */
    public void setExpandedElements(Object[] elements) {
        fExpandedElements = elements;
    }

    /**
     * Sets the size of the tree in unit of characters.
     * 
     * @param width the width of the tree.
     * @param height the height of the tree.
     */
    public void setSize(int width, int height) {
        fWidth = width;
        fHeight = height;
    }

    /**
     * Validate the receiver and update the status with the result.
     * 
     */
    protected void updateOKStatus() {
        if (!fIsEmpty) {
            if (fValidator != null) {
                fCurrStatus = fValidator.validate(fViewer.getCheckedElements());
                updateStatus(fCurrStatus);
            } else if (!fCurrStatus.isOK()) {
                fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", //$NON-NLS-1$
                        null);
            }
        } else {
            fCurrStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, fEmptyListMessage, null);
        }
        updateStatus(fCurrStatus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#open()
     */
    @Override
    public int open() {
        fIsEmpty = evaluateIfTreeEmpty(fInput);
        super.open();
        return getReturnCode();
    }

    private void accessSuperCreate() {
        super.create();
    }

    /**
     * Handles cancel button pressed event.
     */
    @Override
    protected void cancelPressed() {
        setResult(null);
        super.cancelPressed();
    }

    /*
     * @see SelectionStatusDialog#computeResult()
     */
    @Override
    protected void computeResult() {
        setResult(Arrays.asList(fViewer.getCheckedElements()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        BusyIndicator.showWhile(null, new Runnable() {

            public void run() {
                accessSuperCreate();
                fViewer.setCheckedElements(getInitialElementSelections().toArray());
                // ADD msjian 2011-8-9 TDQ-3199 fixed: Make it convenient to delete the jar which is used already.
                for (Object obj : getInitialElementSelections().toArray()) {
                    selectedJars.put(obj, true);
                }
                // ADD end
                if (fExpandedElements != null) {
                    fViewer.setExpandedElements(fExpandedElements);
                }
                updateOKStatus();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        // MOD msjian 2011-7-14 22092 feature: Java UDI: not convinient to delete udi jar files
        final TabFolder tabFolder = new TabFolder(composite, SWT.FILL);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertWidthInCharsToPixels(fWidth);
        data.heightHint = convertHeightInCharsToPixels(tableFolderHeight);
        tabFolder.setLayoutData(data);

        TabItem manageTabItem = new TabItem(tabFolder, SWT.FILL);
        manageTabItem.setText(DefaultMessagesImpl.getString("JavaUdiJarSelectDialog.ManageLib")); //$NON-NLS-1$
        final Composite manageComposite = new Com(tabFolder, SWT.FILL, false);
        manageComposite.setLayout(new GridLayout());
        manageTabItem.setControl(manageComposite);

        TabItem selectTabItem = new TabItem(tabFolder, SWT.FILL);
        selectTabItem.setText(DefaultMessagesImpl.getString("JavaUdiJarSelectDialog.SelectLib")); //$NON-NLS-1$
        final Composite selectComposite = new Com(tabFolder, SWT.FILL, true);
        selectComposite.setLayout(new GridLayout());
        selectTabItem.setControl(selectComposite);

        tabFolder.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                if (DefaultMessagesImpl.getString("JavaUdiJarSelectDialog.SelectLib").equals(
                        tabFolder.getSelection()[0].toString())) {
                    isSelectTab = true;
                } else {
                    isSelectTab = false;
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        tabFolder.setSelection(selectTabItem);
        isSelectTab = true;

        return composite;
    }

    /**
     * the main Composite.
     */
    public class Com extends Composite {

        /**
         * Create the composite.
         * 
         * @param parent
         * @param style
         * @param isSelect (if true, createTreeViewer, else createManageTreeViewer)
         */
        public Com(Composite parent, int style, boolean isSelect) {
            super(parent, style);

            // Label messageLabel = createMessageArea(this);
            CheckboxTreeViewer treefViewer = null;
            if (isSelect) {
                treefViewer = createTreeViewer(this);
            } else {
                treefViewer = createManageTreeViewer(this);
            }

            Control buttonComposite = createButtons(this, isSelect);
            GridData data = new GridData(GridData.FILL_BOTH);
            data.widthHint = convertWidthInCharsToPixels(fWidth);
            data.heightHint = convertHeightInCharsToPixels(fHeight);
            Tree treeWidget = treefViewer.getTree();
            treeWidget.setLayoutData(data);
            treeWidget.setFont(this.getFont());
            // if (fIsEmpty) {
            // messageLabel.setEnabled(false);
            // }
        }

        @Override
        protected void checkSubclass() {
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.SelectionDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // TODO Auto-generated method stub
        // super.createButtonsForButtonBar(parent);
    }

    /**
     * Creates the tree viewer.
     * 
     * @param parent the parent composite
     * @return the tree viewer
     */
    protected CheckboxTreeViewer createTreeViewer(Composite parent) {
        if (fContainerMode) {
            fViewer = new ContainerCheckedTreeViewer(parent, SWT.BORDER);
        } else {
            fViewer = new CheckboxTreeViewer(parent, SWT.BORDER);
        }
        fViewer.setContentProvider(fContentProvider);
        fViewer.setLabelProvider(fLabelProvider);
        fViewer.addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                // ADD msjian 2011-8-9 TDQ-3199 fixed: Make it convenient to delete the jar which is used already.
                handleChecked();
                // ADD end
                updateOKStatus();
            }
        });
        fViewer.setComparator(fComparator);
        if (fFilters != null) {
            for (int i = 0; i != fFilters.size(); i++) {
                fViewer.addFilter((ViewerFilter) fFilters.get(i));
            }
        }
        fViewer.setInput(fInput);
        return fViewer;
    }

    // ADD msjian 2011-8-9 TDQ-3199 fixed: Make it convenient to delete the jar which is used already.
    /**
     * DOC msjian Comment method "handleChecked".
     */
    public void handleChecked() {
        computeResult();
        selectedJars.clear();
        for (Object obj : getResult()) {
            selectedJars.put(obj, true);
        }
    }

    /**
     * DOC msjian Comment method "createManageTreeViewer".
     * 
     * @param parent
     * @return
     */
    protected CheckboxTreeViewer createManageTreeViewer(Composite parent) {
        if (fContainerMode) {
            fManageViewer = new ContainerCheckedTreeViewer(parent, SWT.BORDER);
        } else {
            fManageViewer = new CheckboxTreeViewer(parent, SWT.BORDER);
        }
        fManageViewer.setContentProvider(fContentProvider);
        fManageViewer.setLabelProvider(fLabelProvider);
        fManageViewer.addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                updateOKStatus();
            }
        });
        fManageViewer.setComparator(fComparator);
        if (fFilters != null) {
            for (int i = 0; i != fFilters.size(); i++) {
                fManageViewer.addFilter((ViewerFilter) fFilters.get(i));
            }
        }
        fManageViewer.setInput(fInput);
        return fManageViewer;
    }

    /**
     * Returns the tree viewer.
     * 
     * @return the tree viewer
     */
    protected CheckboxTreeViewer getTreeViewer() {
        // MOD msjian 2011-7-14 22092 feature: Java UDI: not convinient to delete udi jar files
        return isSelectTab ? fViewer : fManageViewer;
    }

    /**
     * Adds the selection and deselection buttons to the dialog.
     * 
     * @param composite the parent composite
     * @return Composite the composite the buttons were created in.
     */
    protected Composite createButtons(final Composite composite, final boolean isSelect) {
        Composite buttonComposite = new Composite(composite, SWT.RIGHT);
        GridLayout layout = new GridLayout();
        layout.numColumns = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        buttonComposite.setLayout(layout);
        buttonComposite.setFont(composite.getFont());
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        buttonComposite.setLayoutData(data);
        // MOD msjian 2011-7-14 22092 feature: Java UDI: not convinient to delete udi jar files
        Button selectButton = createButton(buttonComposite, IDialogConstants.SELECT_ALL_ID,
                WorkbenchMessages.CheckedTreeSelectionDialog_select_all, false);
        SelectionListener listener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] viewerElements = fContentProvider.getElements(fInput);
                if (fContainerMode) {
                    if (isSelect) {
                        fViewer.setCheckedElements(viewerElements);
                        // ADD msjian 2011-8-9 TDQ-3199 fixed: Make it convenient to delete the jar which is used
                        // already.
                        handleChecked();
                        // ADD end
                    } else {
                        fManageViewer.setCheckedElements(viewerElements);
                    }
                } else {
                    for (Object viewerElement : viewerElements) {
                        if (isSelect) {
                            fViewer.setSubtreeChecked(viewerElement, true);
                        } else {
                            fManageViewer.setSubtreeChecked(viewerElement, true);
                        }
                    }
                }
                updateOKStatus();
            }
        };
        selectButton.addSelectionListener(listener);

        Button deselectButton = createButton(buttonComposite, IDialogConstants.DESELECT_ALL_ID,
                WorkbenchMessages.CheckedTreeSelectionDialog_deselect_all, false);
        listener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isSelect) {
                    fViewer.setCheckedElements(new Object[0]);
                    // ADD msjian 2011-8-9 TDQ-3199 fixed: Make it convenient to delete the jar which is used
                    // already.
                    handleChecked();
                    // ADD end
                } else {
                    fManageViewer.setCheckedElements(new Object[0]);
                }
                updateOKStatus();
            }
        };
        deselectButton.addSelectionListener(listener);

        if (!isSelect) {
            final Composite dialogComposite = composite;

            Button addButton = createButton(buttonComposite, 22,
                    DefaultMessagesImpl.getString("JavaUdiJarSelectDialog.add"), false); //$NON-NLS-1$
            SelectionListener listenerAdd = new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    FileDialog dialog = new FileDialog(dialogComposite.getShell(), SWT.NONE | SWT.MULTI);
                    dialog.setFilterExtensions(new String[] { "*.jar" }); //$NON-NLS-1$
                    String path = dialog.open();

                    if (path != null) {
                        String[] fileNames = dialog.getFileNames();
                        // jarPathText.setText(path);
                        // // MOD klliu 2010-05-31 13451: Class name of Java User Define Indicator must be validated
                        // validateJavaUDI(classNameText, jarPathText);
                        for (String name : fileNames) {
                            IPath filePath = new Path(path);
                            filePath = filePath.removeLastSegments(1).append(name);

                            // TDQ-7451 Replace File copy with eclipse IFile create.make svn could syn and control.
                            IFile targetFile = ResourceManager.getUDIJarFolder().getFile(filePath.lastSegment());
                            WorkspaceUtils.createIFileFromFile(filePath.toFile(), targetFile,
                                    DefaultMessagesImpl.getString("JavaUdiJarSelectDialog.addJarFile", name)); //$NON-NLS-1$
                        }
                    }
                    // MOD msjian 2011-7-14 22092 feature: Java UDI: not convinient to delete udi jar files
                    fViewer.refresh();
                    fManageViewer.refresh();
                    fViewer.setInput(ResourceManager.getUDIJarFolder());
                    fManageViewer.setInput(ResourceManager.getUDIJarFolder());

                    fIsEmpty = evaluateIfTreeEmpty(fInput);
                    // getTreeViewer().setCheckedElements(new Object[0]);
                    updateOKStatus();
                }
            };
            addButton.addSelectionListener(listenerAdd);

            Button delButton = createButton(buttonComposite, 23, DefaultMessagesImpl.getString("JavaUdiJarSelectDialog.delete"),
                    false);
            SelectionListener listenerDel = new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    for (Object delFile : fManageViewer.getCheckedElements()) {
                        // Object delFile = manageSelectList.get(i);
                        if (delFile instanceof File) {
                            // MOD msjian 2011-8-9 TDQ-3199 fixed: Make it convenient to delete the jar which is used
                            // already.
                            ReturnCode rc = UDIUtils.checkUDIDependency(definition, (File) delFile);
                            if (selectedJars.containsKey(delFile)) {
                                rc.setOk(false);
                                rc.setMessage(DefaultMessagesImpl.getString(
                                        "JavaUdiJarSelectDialog.fileHasBeenSelected", ((File) delFile).getName()));//$NON-NLS-1$ 
                            }
                            // MOD end
                            if (rc.isOk()) {
                                boolean delete = ((File) delFile).delete();
                                if (!delete) {
                                    MessageDialog.openWarning(getParentShell(),
                                            DefaultMessagesImpl.getString("JavaUdiJarSelectDialog.delete"), //$NON-NLS-1$
                                            DefaultMessagesImpl.getString("JavaUdiJarSelectDialog.deleteFail")); //$NON-NLS-1$
                                }
                            } else {
                                MessageUI.openWarning(rc.getMessage());
                            }
                        }
                        CorePlugin.getDefault().refreshWorkSpace();
                    }
                    // MOD msjian 2011-7-14 22092 feature: Java UDI: not convinient to delete udi jar files
                    fViewer.refresh();
                    fManageViewer.refresh();
                    fIsEmpty = evaluateIfTreeEmpty(fInput);
                    updateOKStatus();
                }

            };
            delButton.addSelectionListener(listenerDel);
        } else {
            // ADD msjian 2011-11-17 TDQ-3556 : add ok/cancel button to the selecter window
            Button okButton = createButton(buttonComposite, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
            SelectionListener listenerOK = new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    String path = "";//$NON-NLS-1$
                    for (Object obj : getResult()) {
                        if (obj instanceof File) {
                            IFile file = ResourceManager.getRoot().getFile(
                                    new org.eclipse.core.runtime.Path(((File) obj).getPath()));
                            if (!"".equalsIgnoreCase(path)) {//$NON-NLS-1$
                                path += "||";//$NON-NLS-1$
                            }
                            path += file.getName();
                        }
                    }
                    jarPathText.setText(path);
                }
            };
            okButton.addSelectionListener(listenerOK);

            Button cancelButton = createButton(buttonComposite, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
            SelectionListener listenerCancel = new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    cancelPressed();
                }
            };
            cancelButton.addSelectionListener(listenerCancel);
            // TDQ-3556 ~
        }

        return buttonComposite;
    }

    private boolean evaluateIfTreeEmpty(Object input) {
        Object[] elements = fContentProvider.getElements(input);
        if (elements.length > 0) {
            if (fFilters != null) {
                for (int i = 0; i < fFilters.size(); i++) {
                    ViewerFilter curr = (ViewerFilter) fFilters.get(i);
                    elements = curr.filter(fViewer, input, elements);
                }
            }
        }
        return elements.length == 0;
    }

    public void setCheckedElements(String[] selectElements) {
        elements = new ArrayList<Object>();
        for (Object element : fContentProvider.getElements(this.fInput)) {
            if (element instanceof File) {
                for (String sel : selectElements) {
                    if (sel.equals(((File) element).getName())) {
                        elements.add(element);
                        break;
                    }
                }
            }
        }
        this.setInitialElementSelections(elements);
        CheckboxTreeViewer treeViewer = this.getTreeViewer();
        if (treeViewer != null) {
            treeViewer.setCheckedElements(elements.toArray());
        }
    }

    /**
     * DOC msjian Comment method "setControl".
     * 
     * @param jarPathText
     */
    public void setControl(Text jarPathText) {
        this.jarPathText = jarPathText;
    }
}
