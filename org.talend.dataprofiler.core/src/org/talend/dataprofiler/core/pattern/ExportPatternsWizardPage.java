// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.pattern;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewLabelProvider;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ExportPatternsWizardPage extends WizardPage {

    private IFolder folder;

    private Text fileText;

    private CheckboxTreeViewer selectedPatternsTree;

    public CheckboxTreeViewer getSelectedPatternsTree() {
        return selectedPatternsTree;
    }

    public String getXLSFile() {
        return fileText.getText();
    }

    /**
     * DOC zqin ExportPatternsWizardPage constructor comment.
     */
    public ExportPatternsWizardPage(IFolder folder) {
        super("ExportPatternsWizardPage");

        setTitle("Export Patterns to File");
        setDescription("Choose a file to export patterns.");

        this.folder = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        GridData gridData = new GridData(GridData.FILL_BOTH);
        container.setLayout(layout);
        container.setLayoutData(gridData);

        Composite fileComp = new Composite(container, SWT.NONE);
        layout = new GridLayout(3, false);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        fileComp.setLayout(layout);
        fileComp.setLayoutData(gridData);
        Label label = new Label(fileComp, SWT.NONE);
        label.setText("Select File (csv):");
        fileText = new Text(fileComp, SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gridData);
        fileText.addModifyListener(new ModifyListener() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            public void modifyText(ModifyEvent e) {
                File file = new File(fileText.getText());
                if (file.exists()) {
                    setPageComplete(true);
                } else {
                    setPageComplete(false);
                }
            }
        });
        Button button = new Button(fileComp, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell());
                dialog.setFilterExtensions(new String[] { "*.csv" });
                if (fileText.getText() != null) {
                    dialog.setFileName(fileText.getText());
                }
                String path = dialog.open();
                if (path != null) {
                    fileText.setText(path);
                }
            }
        });

        Group group = new Group(container, SWT.NONE);
        group.setText("Selected patterns:");
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        selectedPatternsTree = new ContainerCheckedTreeViewer(group);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(selectedPatternsTree.getTree());

        selectedPatternsTree.setLabelProvider(new ResourceViewLabelProvider());
        selectedPatternsTree.setContentProvider(new ResourceViewContentProvider() {

            @Override
            public boolean hasChildren(Object element) {
                if (element instanceof IFile) {
                    return false;
                }
                return super.hasChildren(element);
            }

        });

        selectedPatternsTree.setInput(this.folder);

        try {
            selectedPatternsTree.setCheckedElements(folder.members());
        } catch (CoreException e1) {
            e1.printStackTrace();
        }

        setControl(container);
    }

}
