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
package org.talend.dataprofiler.core.ui.wizard.analysis;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ViewerDataFactory;
import org.talend.dataprofiler.core.model.nodes.analysis.AnalysisTypeNode;
import org.talend.dataprofiler.core.ui.wizard.analysis.provider.AnalysisTypeContentProvider;
import org.talend.dataprofiler.core.ui.wizard.analysis.provider.AnalysisTypeLabelProvider;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.analysis.parameters.ConnectionAnalysisParameter;

/**
 * @author zqin
 * 
 */
public class NewWizardSelectionPage extends AbstractAnalysisWizardPage {

    private TreeViewer typeTreeViewer;

    private Wizard selectedWizard;

    public NewWizardSelectionPage() {
        setTitle(DefaultMessagesImpl.getString("NewWizardSelectionPage.wizard")); //$NON-NLS-1$
        setMessage(DefaultMessagesImpl.getString("NewWizardSelectionPage.Analysis")); //$NON-NLS-1$
        setCanFinishEarly(false);
        setPageComplete(false);
        setHasPages(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gdLayout = new GridLayout(1, true);
        container.setLayout(gdLayout);

        PatternFilter filter = new PatternFilter();
        FilteredTree tree = new FilteredTree(container, SWT.BORDER, filter);

        typeTreeViewer = tree.getViewer();
        typeTreeViewer.setContentProvider(new AnalysisTypeContentProvider());
        typeTreeViewer.setLabelProvider(new AnalysisTypeLabelProvider());
        typeTreeViewer.setInput(ViewerDataFactory.createTreeData());

        addListeners();

        setControl(container);
    }

    protected void addListeners() {

        typeTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {

                AnalysisTypeNode node = (AnalysisTypeNode) ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (node == null) {
                    return;
                }
                AnalysisTypeNode parent = (AnalysisTypeNode) node.getParent();
                if (parent == null) {
                    setPageComplete(false);
                    return;
                } else {
                    String literal = parent.getLiteral();

                    AnalysisParameter parameter = null;

                    AnalysisType type = AnalysisType.get(literal);

                    FolderProvider currentFolderProvider = ((CreateNewAnalysisWizard) getWizard()).getCurrentFolderProvider();
                    switch (type) {
                    case COLUMN_CORRELATION:
                        AnalysisParameter correlationColumnParam = new AnalysisParameter();
                        correlationColumnParam.setFolderProvider(currentFolderProvider);
                        parameter = correlationColumnParam;
                        break;
                    case MULTIPLE_COLUMN:
                        AnalysisParameter correlationParam = new AnalysisParameter();
                        correlationParam.setFolderProvider(currentFolderProvider);
                        parameter = correlationParam;
                        break;
                    case COLUMNS_COMPARISON:
                        AnalysisParameter anaParam = new AnalysisParameter();
                        anaParam.setFolderProvider(currentFolderProvider);
                        parameter = anaParam;

                        break;
                    case CONNECTION:
                        ConnectionAnalysisParameter connParam = new ConnectionAnalysisParameter();
                        connParam.setFolderProvider(currentFolderProvider);
                        parameter = connParam;

                        break;
                    default:

                        parameter = new AnalysisParameter();
                    }

                    selectedWizard = WizardFactory.createAnalysisWizard(type, parameter);
                    setPageComplete(true);
                }
            }

        });

        typeTreeViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {

                AnalysisTypeNode node = (AnalysisTypeNode) ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (node.getParent() == null) {
                    typeTreeViewer.expandToLevel(node, 1);
                } else {
                    advanceToNextPageOrFinish();
                }
            }

        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
     */
    @Override
    public IWizardPage getNextPage() {

        if (selectedWizard == null) {
            return null;
        }

        if (selectedWizard.getStartingPage() == null) {
            selectedWizard.addPages();
        }

        return selectedWizard.getStartingPage();
    }

    /**
     * @return the selectedWizard
     */
    public Wizard getSelectedWizard() {
        return this.selectedWizard;
    }

}
