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
package org.talend.dataprofiler.core.ui.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.resource.ResourceManager;
import org.talend.utils.io.FilesUtils;
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

    /**
     * Constructs an instance of <code>ElementTreeSelectionDialog</code>.
     * 
     * @param parent The shell to parent from.
     * @param labelProvider the label provider to render the entries
     * @param contentProvider the content provider to evaluate the tree structure
     */
    public JavaUdiJarSelectDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        super(parent);
        fLabelProvider = labelProvider;
        fContentProvider = contentProvider;
        setResult(new ArrayList(0));
        setStatusLineAboveButtons(true);
        fContainerMode = false;
        fExpandedElements = null;
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
    public int open() {
        fIsEmpty = evaluateIfTreeEmpty(fInput);
        super.open();
        // FIXME treeViewer is never used.
        CheckboxTreeViewer treeViewer = this.getTreeViewer();
        return getReturnCode();
    }

    private void accessSuperCreate() {
        super.create();
    }

    /**
     * Handles cancel button pressed event.
     */
    protected void cancelPressed() {
        setResult(null);
        super.cancelPressed();
    }

    /*
     * @see SelectionStatusDialog#computeResult()
     */
    protected void computeResult() {
        setResult(Arrays.asList(fViewer.getCheckedElements()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#create()
     */
    public void create() {
        BusyIndicator.showWhile(null, new Runnable() {

            public void run() {
                accessSuperCreate();
                fViewer.setCheckedElements(getInitialElementSelections().toArray());
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
                        tabFolder.getSelection()[0].toString())) { //$NON-NLS-1$
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

            Label messageLabel = createMessageArea(this);
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
            if (fIsEmpty) {
                messageLabel.setEnabled(false);
            }
        }

        @Override
        protected void checkSubclass() {
        }
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
    protected Composite createButtons(Composite composite, final boolean isSelect) {
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
            public void widgetSelected(SelectionEvent e) {
                Object[] viewerElements = fContentProvider.getElements(fInput);
                if (fContainerMode) {
                    if (isSelect) {
                        fViewer.setCheckedElements(viewerElements);
                    } else {
                        fManageViewer.setCheckedElements(viewerElements);
                    }
                } else {
                    for (int i = 0; i < viewerElements.length; i++) {
                        if (isSelect) {
                            fViewer.setSubtreeChecked(viewerElements[i], true);
                        } else {
                            fManageViewer.setSubtreeChecked(viewerElements[i], true);
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
            public void widgetSelected(SelectionEvent e) {
                if (isSelect) {
                    fViewer.setCheckedElements(new Object[0]);
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

                public void widgetSelected(SelectionEvent e) {
                    FileDialog dialog = new FileDialog(dialogComposite.getShell(), SWT.NONE | SWT.MULTI);
                    dialog.setFilterExtensions(new String[] { "*.jar" }); //$NON-NLS-1$
                    String path = dialog.open();

                    if (path != null) {
                        String[] fileNames = dialog.getFileNames();
                        // jarPathText.setText(path);
                        // // MOD klliu 2010-05-31 13451: Class name of Java User Define Indicator must be validated
                        // validateJavaUDI(classNameText, jarPathText);
                        try {
                            for (String name : fileNames) {
                                IPath filePath = new Path(path);
                                filePath = filePath.removeLastSegments(1).append(name);

                                // File jarFile =
                                // ResourceManager.getUDIJarFolder().getFullPath().append(filePath.lastSegment()).toFile();
                                // boolean createNewFile = false;
                                // if (!jarFile.getAbsoluteFile().isAbsolute()) {
                                // createNewFile = jarFile.createNewFile();
                                // }
                                // if (createNewFile) {
                                FilesUtils.copyFile(filePath.toFile(),
                                        ResourceManager.getUDIJarFolder().getLocation().append(filePath.lastSegment()).toFile());
                                // ProxyRepositoryManager.getInstance().save(Boolean.TRUE);
                                // }
                            }
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        CorePlugin.getDefault().refreshWorkSpace();
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

            Button delButton = createButton(buttonComposite, 23, DefaultMessagesImpl.getString("JavaUdiJarSelectDialog.delete"),//$NON-NLS-3$
                    false);
            SelectionListener listenerDel = new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    for (Object delFile : fManageViewer.getCheckedElements()) {
                        // Object delFile = manageSelectList.get(i);
                        if (delFile instanceof File) {
                            ReturnCode rc = UDIUtils.checkUDIDependency((File) delFile);
                            if (elements.contains(delFile)) {
                                rc.setOk(false);
                                rc.setMessage("the File " + ((File) delFile).getName() + " has been select by the UDI");//$NON-NLS-1$ //$NON-NLS-3$
                            }
                            if (rc.isOk()) {
                                ((File) delFile).delete();
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
}
