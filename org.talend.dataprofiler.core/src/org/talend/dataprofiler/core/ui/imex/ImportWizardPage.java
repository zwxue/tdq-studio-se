// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.imex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.imex.model.EImexType;
import org.talend.dataprofiler.core.ui.imex.model.IImexWriter;
import org.talend.dataprofiler.core.ui.imex.model.ImportWriterFactory;
import org.talend.dataprofiler.core.ui.imex.model.ItemRecord;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportWizardPage extends WizardPage {

    private CheckboxTreeViewer repositoryTree;

    private TableViewer errorsList;

    private Button dirBTN, archBTN;

    private Button browseDirBTN, browseArchBTN;

    private Button overwriteBTN;

    private Text dirTxt, archTxt;

    private IImexWriter writer;

    private String basePath;

    private List<String> errors = new ArrayList<String>();

    private static final String[] FILE_EXPORT_MASK = { "*.zip;*.tar;*.tar.gz", "*.*" }; //$NON-NLS-1$//$NON-NLS-2$

    public ImportWizardPage() {
        super(Messages.getString("ImportWizardPage.2")); //$NON-NLS-1$
        setMessage(Messages.getString("ImportWizardPage.3")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());
        top.setLayoutData(new GridData(GridData.FILL_BOTH));

        createSelectComposite(top);

        createRepositoryTree(top);

        createErrorsList(top);

        createOptionComposite(top);

        initControlState();

        addListeners();

        setControl(top);

    }

    /**
     * DOC bZhou Comment method "addListeners".
     */
    private void addListeners() {

        SelectionListener modeSwitchListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setDirState(isDirState());
                setArchState(!isDirState());
            }
        };

        dirBTN.addSelectionListener(modeSwitchListener);

        archBTN.addSelectionListener(modeSwitchListener);

        ModifyListener populateListener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (isDirState()) {
                    basePath = dirTxt.getText();
                } else {
                    basePath = archTxt.getText();
                }

                textModified(basePath);
            }
        };

        dirTxt.addModifyListener(populateListener);

        archTxt.addModifyListener(populateListener);

        browseDirBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String result = openDirectoryDialog();
                if (result != null) {
                    dirTxt.setText(result);
                }
            }

            private String openDirectoryDialog() {
                DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell());
                if (dirTxt.getText() != null) {
                    dialog.setFilterPath(dirTxt.getText());
                }

                return dialog.open();
            }
        });

        browseArchBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String result = openFileDialog();
                if (result != null) {
                    archTxt.setText(result);
                }
            }

            private String openFileDialog() {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell());
                dialog.setFilterExtensions(FILE_EXPORT_MASK);
                if (archTxt.getText() != null) {
                    dialog.setFileName(archTxt.getText());
                }

                return dialog.open();
            }
        });

        overwriteBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                textModified(basePath);
            }
        });

        repositoryTree.getTree().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                TreeItem item = (TreeItem) e.item;
                ItemRecord record = (ItemRecord) item.getData();

                if (record.getFile().isFile()) {
                    for (File file : record.getDependencyMap().keySet()) {
                        ItemRecord findRecord = ItemRecord.findRecord(file);
                        if (findRecord != null) {
                            repositoryTree.setChecked(findRecord, item.getChecked());
                        }
                    }
                }

                repositoryTree.refresh();

                checkforErrors();
            }
        });
    }

    /**
     * DOC bZhou Comment method "populateElement".
     */
    private void populateElement() {
        ItemRecord[] invalidRecords = writer.populate(getElements(), !overwriteBTN.getSelection());
        updateErrorList(invalidRecords);

        if (invalidRecords.length > 0) {
            removeInvalidRecords(invalidRecords);
        }
    }

    /**
     * DOC bZhou Comment method "removeInvalidRecords".
     * 
     * @param invalidRecords
     */
    public void removeInvalidRecords(ItemRecord[] invalidRecords) {
        if (invalidRecords != null) {
            for (ItemRecord record : invalidRecords) {
                File file = record.getFile();

                repositoryTree.setChecked(file, false);
            }

            repositoryTree.refresh();
        }
    }

    /**
     * DOC sgandon Comment method "dirTextModified".
     */
    protected void textModified(String pathStr) {
        IPath path = new Path(pathStr);

        if (path.toFile().exists()) {
            ItemRecord input = writer.computeInput(path);

            repositoryTree.setInput(input);

            repositoryTree.expandAll();
            TreeItem[] topItems = repositoryTree.getTree().getItems();
            for (TreeItem treeItem : topItems) {
                repositoryTree.setSubtreeChecked(treeItem.getData(), true);
            }
            repositoryTree.refresh();

        } else {
            repositoryTree.setInput(null);
        }

        populateElement();

        checkforErrors();
    }

    /**
     * update the page state that is the finish button enable state according to the error message being present or not.
     */
    private void updatePageStatus() {
        boolean valid = getErrorMessage() == null;
        setPageComplete(valid);
    }

    /**
     * check that directory exist and issue an error message if not. <br>
     * check that the folder is a data quality repository or issue an error.<br>
     * check that anything is check in the tree or issue an error.<br>
     */
    private void checkforErrors() {
        List<String> dErrors = new ArrayList<String>();

        if (repositoryTree.getTree().getItems().length == 0) {
            dErrors.add(Messages.getString("ImportWizardPage.0")); //$NON-NLS-1$
        }

        if (repositoryTree.getCheckedElements().length == 0) {
            dErrors.add(Messages.getString("ImportWizardPage.1")); //$NON-NLS-1$
        }

        dErrors.addAll(writer.check());

        ItemRecord[] elements = getElements();
        for (ItemRecord record : elements) {
            for (File depFile : record.getDependencyMap().keySet()) {
                ModelElement element = record.getDependencyMap().get(depFile);

                ItemRecord findRecord = ItemRecord.findRecord(depFile);
                if (findRecord == null || !repositoryTree.getChecked(findRecord)) {
                    dErrors.add("\"" + record.getElement().getName() + "\" miss dependency :" + element.getName());
                }
            }
        }

        if (!dErrors.isEmpty()) {
            setErrorMessage(dErrors.get(0));
        } else {
            setErrorMessage(null);
        }

        updatePageStatus();
    }

    /**
     * DOC bZhou Comment method "initControlState".
     */
    private void initControlState() {
        setArchState(false);
        setPageComplete(false);

        this.writer = ImportWriterFactory.create(EImexType.FILE);
    }

    /**
     * DOC bZhou Comment method "createRepositoryTree".
     * 
     * @param top
     */
    private void createRepositoryTree(Composite top) {
        repositoryTree = new ContainerCheckedTreeViewer(top);
        repositoryTree.setContentProvider(new FileTreeContentProvider());
        repositoryTree.setLabelProvider(new FileTreeLabelProvider());
        repositoryTree.setInput(""); //$NON-NLS-1$

        GridDataFactory.fillDefaults().grab(true, true).applyTo(repositoryTree.getTree());
    }

    /**
     * DOC bZhou Comment method "createErrorsList".
     * 
     * @param top
     */
    private void createErrorsList(Composite top) {

        Group errorGroup = new Group(top, SWT.NONE);
        errorGroup.setLayout(new GridLayout());
        errorGroup.setText(Messages.getString("ImportWizardPage.5")); //$NON-NLS-1$

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.heightHint = 100;
        errorGroup.setLayoutData(gridData);

        errorsList = new TableViewer(errorGroup, SWT.BORDER);
        errorsList.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        errorsList.setContentProvider(new IStructuredContentProvider() {

            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            public Object[] getElements(Object inputElement) {
                return errors.toArray();
            }
        });

        errorsList.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                return element.toString();
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
             */
            @Override
            public Image getImage(Object element) {
                return ImageLib.getImage(ImageLib.ICON_ERROR_INFO);
            }
        });

        errorsList.setInput(this);
        errorsList.setSorter(new ViewerSorter());

    }

    /**
     * DOC bZhou Comment method "createOptionComposite".
     * 
     * @param top
     */
    private void createOptionComposite(Composite top) {
        overwriteBTN = new Button(top, SWT.CHECK);
        overwriteBTN.setText(Messages.getString("ImportWizardPage.4")); //$NON-NLS-1$
    }

    /**
     * DOC bZhou Comment method "setDirState".
     * 
     * @param state
     */
    protected void setDirState(boolean state) {
        dirTxt.setEnabled(state);
        browseDirBTN.setEnabled(state);

        if (state) {
            writer = ImportWriterFactory.create(EImexType.FILE);
        }
    }

    /**
     * DOC bZhou Comment method "isDirState".
     * 
     * @return
     */
    public boolean isDirState() {
        return dirBTN.getSelection();
    }

    /**
     * DOC bZhou Comment method "setArchState".
     * 
     * @param state
     */
    protected void setArchState(boolean state) {
        archTxt.setEnabled(state);
        browseArchBTN.setEnabled(state);

        if (state) {
            writer = ImportWriterFactory.create(EImexType.ZIP_FILE);
        }
    }

    /**
     * DOC bZhou Comment method "isArchState".
     * 
     * @return
     */
    public boolean isArchState() {
        return archBTN.getSelection();
    }

    /**
     * DOC bZhou Comment method "createSelectComposite".
     * 
     * @param top
     */
    private void createSelectComposite(Composite top) {
        Composite selectComp = new Composite(top, SWT.NONE);
        selectComp.setLayout(new GridLayout(3, false));
        selectComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        dirBTN = new Button(selectComp, SWT.RADIO);
        dirBTN.setText(Messages.getString("ImportWizardPage.7")); //$NON-NLS-1$
        setButtonLayoutData(dirBTN);

        dirTxt = new Text(selectComp, SWT.BORDER);
        dirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        browseDirBTN = new Button(selectComp, SWT.PUSH);
        browseDirBTN.setText(Messages.getString("ImportWizardPage.8")); //$NON-NLS-1$
        setButtonLayoutData(browseDirBTN);

        archBTN = new Button(selectComp, SWT.RADIO);
        archBTN.setText(Messages.getString("ImportWizardPage.9")); //$NON-NLS-1$
        setButtonLayoutData(archBTN);

        archTxt = new Text(selectComp, SWT.BORDER);
        archTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        browseArchBTN = new Button(selectComp, SWT.PUSH);
        browseArchBTN.setText(Messages.getString("ImportWizardPage.10")); //$NON-NLS-1$
        setButtonLayoutData(browseArchBTN);

    }

    /**
     * DOC bZhou Comment method "getElements".
     * 
     * @return
     */
    public ItemRecord[] getElements() {
        List<ItemRecord> itemRecords = new ArrayList<ItemRecord>();

        Object[] checkedElements = repositoryTree.getCheckedElements();
        for (Object obj : checkedElements) {
            if (obj instanceof ItemRecord) {
                ItemRecord record = (ItemRecord) obj;
                if (record.getFile().isFile()) {
                    itemRecords.add(record);
                }
            }
        }
        return itemRecords.toArray(new ItemRecord[itemRecords.size()]);
    }

    /**
     * Getter for writer.
     * 
     * @return the writer
     */
    public IImexWriter getWriter() {
        return this.writer;
    }

    /**
     * DOC bZhou Comment method "updateErrorList".
     * 
     * @param records
     */
    public void updateErrorList(ItemRecord[] records) {
        errors.clear();

        for (ItemRecord record : records) {
            errors.addAll(record.getErrors());
        }

        errorsList.setInput(errors);
        errorsList.refresh();

    }
}
