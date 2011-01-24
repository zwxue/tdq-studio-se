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
package org.talend.dataprofiler.core.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC bZhou  class global comment. Detailled comment
 */
public abstract class AbstractMetadataCreationAction extends Action implements ICheatSheetAction {

    private static final int WIDTH = 550;

    private static final int HEIGHT = 550;

    protected RepositoryNode node;

    private AbstractMetadataCreationAction() {
        setText(getActionLabel());
        setImageDescriptor(getActionImage());
    }

    public AbstractMetadataCreationAction(RepositoryNode node) {
        this();
        this.node = node;
    }

    @Override
    public void run() {
        IWizard wizard = createWizard();
        if (wizard != null) {
            WizardDialog dialog = new WizardDialog(null, wizard);

            initDialogBeforeOpen(dialog);

            dialog.open();

            CorePlugin.getDefault().refreshDQView();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[], org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        initCheateSheetRun(params, manager);

        run();
    }

    /**
     * DOC bZhou Comment method "initCheateSheetRun".
     * 
     * It's a hook to initialized the cheat sheet run action.
     * 
     * @param manager
     * @param params
     * 
     */
    protected void initCheateSheetRun(String[] params, ICheatSheetManager manager) {
        // do nothing
    }

    /**
     * DOC bZhou Comment method "initDialogBeforeOpen".
     * 
     * It's a hook to initialized the dialog before open it.
     * 
     * @param dialog
     */
    protected void initDialogBeforeOpen(WizardDialog dialog) {
        dialog.setPageSize(WIDTH, HEIGHT);
    }

    /**
     * DOC bZhou Comment method "getExistingNames".
     * 
     * This method is to return the existing names in current repository.
     * 
     * @return
     */
    protected String[] getExistingNames() {
        switch (node.getType()) {
        case SIMPLE_FOLDER:
        case SYSTEM_FOLDER:
            return collectChildNames(node);
        case REPOSITORY_ELEMENT:
            return collectSiblingNames(node);
        default:
            return null;
        }
    }

    protected String[] collectChildNames(final RepositoryNode node) {
        List<String> names = doCollectChildNames(node);
        return names.toArray(new String[names.size()]);
    }

    protected String[] collectSiblingNames(final RepositoryNode node) {
        List<String> names = doCollectChildNames(node.getParent());
        names.remove(node.getObject().getProperty().getLabel());
        return names.toArray(new String[names.size()]);
    }

    private List<String> doCollectChildNames(final RepositoryNode node) {
        List<String> names = new ArrayList<String>();
        for (IRepositoryNode sibling : node.getChildren()) {
            names.add(sibling.getObject().getProperty().getLabel());
        }
        return names;
    }

    /**
     * DOC bZhou Comment method "createWizard".
     * 
     * Create the wizard for each element.
     * 
     * @return
     */
    protected abstract IWizard createWizard();

    /**
     * DOC bZhou Comment method "getActionLabel".
     * 
     * Get the label displayed on the menu.
     * 
     * @return
     */
    protected abstract String getActionLabel();

    /**
     * DOC bZhou Comment method "getActionImage".
     * 
     * Get the image displayed on the menu.
     * 
     * @return
     */
    protected abstract ImageDescriptor getActionImage();

}
