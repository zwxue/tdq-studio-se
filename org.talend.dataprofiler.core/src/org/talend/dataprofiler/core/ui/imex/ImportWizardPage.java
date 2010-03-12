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
package org.talend.dataprofiler.core.ui.imex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportWizardPage extends WizardPage {

    private CheckboxTreeViewer repositoryTree;

    private Button dirBTN, archBTN;

    private Button browseDirBTN, browseArchBTN;

    private Text dirTxt, archTxt;

    private static final String[] FILE_EXPORT_MASK = { "*.zip;*.tar;*.tar.gz", "*.*" };

    public ImportWizardPage() {
        super("Import Item");
        setMessage("Import item to current projecdt.");
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

        initControlState();

        addListeners();

        setControl(top);

    }

    /**
     * DOC bZhou Comment method "addListeners".
     */
    private void addListeners() {
        dirBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setDirState(dirBTN.getSelection());
            }
        });

        archBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setArchState(archBTN.getSelection());
            }
        });

        browseDirBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String result = openDirectoryDialog();
                if (result != null) {
                    dirTxt.setText(result);
                    File file = new File(result);
                    repositoryTree.setInput(file);
                    repositoryTree.setAllChecked(true);
                    repositoryTree.expandAll();
                    repositoryTree.refresh();
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
    }

    /**
     * DOC bZhou Comment method "initControlState".
     */
    private void initControlState() {
        setArchState(false);
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
        repositoryTree.setInput("");

        GridDataFactory.fillDefaults().grab(true, true).applyTo(repositoryTree.getTree());
    }

    /**
     * DOC bZhou Comment method "setDirState".
     * 
     * @param state
     */
    protected void setDirState(boolean state) {
        dirTxt.setEnabled(state);
        browseDirBTN.setEnabled(state);
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
        dirBTN.setText("Select root directory:");
        setButtonLayoutData(dirBTN);

        dirTxt = new Text(selectComp, SWT.BORDER);
        dirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        browseDirBTN = new Button(selectComp, SWT.PUSH);
        browseDirBTN.setText("Browse");
        setButtonLayoutData(browseDirBTN);

        archBTN = new Button(selectComp, SWT.RADIO);
        archBTN.setText("Select archive file:");
        archBTN.setEnabled(false); // TODO make it enable after implemence.
        setButtonLayoutData(archBTN);

        archTxt = new Text(selectComp, SWT.BORDER);
        archTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        browseArchBTN = new Button(selectComp, SWT.PUSH);
        browseArchBTN.setText("Browse");
        setButtonLayoutData(browseArchBTN);

    }

    /**
     * DOC bZhou Comment method "getElements".
     * 
     * @return
     */
    public File[] getElements() {
        Object[] checkedElements = repositoryTree.getCheckedElements();

        List<File> files = new ArrayList<File>();
        if (checkedElements != null) {
            for (Object obj : checkedElements) {
                if (obj instanceof File) {
                    File file = (File) obj;
                    if (file.isFile()) {
                        files.add(file);
                    }
                }
            }
        }
        return files.toArray(new File[files.size()]);
    }
}
