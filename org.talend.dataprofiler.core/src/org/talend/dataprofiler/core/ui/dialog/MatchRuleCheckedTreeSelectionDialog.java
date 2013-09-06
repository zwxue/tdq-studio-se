// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class MatchRuleCheckedTreeSelectionDialog extends IndicatorCheckedTreeSelectionDialog {

    private Button overwriteBTN;

    private boolean isOverwrite = false;

    public static final String T_SWOOSH_ALGORITHM = "T-Swoosh algorithm"; //$NON-NLS-1$


    /**
     * DOC yyin DQRuleCheckedTreeSelectionDialog constructor comment.
     * 
     * @param parent
     * @param labelProvider
     * @param contentProvider
     */
    public MatchRuleCheckedTreeSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);

        init();
        addFilter();
        addValidator();
    }

    /**
     * DOC yyin Comment method "addValidator".
     */
    private void addValidator() {
        setValidator(new ISelectionStatusValidator() {

            public IStatus validate(Object[] selection) {
                IStatus status = Status.OK_STATUS;
                if (selection != null && selection.length > 2) {
                    status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
                            DefaultMessagesImpl.getString("MatchRuleCheckedTreeSelectionDialog.validate")); //$NON-NLS-1$
                }
                // when the selected rule has no match & block keys, not validate(has block,no match, can validate )
                for(Object selectObject: selection){
                    if(selectObject instanceof IFile){
                        IFile file = (IFile) selectObject;
                        if (FactoriesUtil.DQRULE.equals(file.getFileExtension())) {
                             MatchRuleDefinition matchRule = DQRuleResourceFileHelper.getInstance().findMatchRule(file);
                             if((matchRule.getBlockKeys()==null || matchRule.getBlockKeys().size()<1) &&(matchRule.getMatchRules()==null || matchRule.getMatchRules().size()<1)){
                                status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
                                        DefaultMessagesImpl.getString("MatchRuleCheckedTreeSelectionDialog.emptyRule")); //$NON-NLS-1$
                             }
                             
                             // when the imported rule's algorithm is "T_Swoosh", warning
                            if (T_SWOOSH_ALGORITHM.equals(matchRule.getRecordLinkageAlgorithm())) {
                                status = new Status(IStatus.OK, CorePlugin.PLUGIN_ID,
                                        DefaultMessagesImpl.getString("MatchRuleCheckedTreeSelectionDialog.tswoosh")); //$NON-NLS-1$
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
        setInput(ResourceManager.getRulesFolder());
        setContainerMode(true);
        setTitle(DefaultMessagesImpl.getString("DQRuleCheckedTreeSelectionDialog.title")); //$NON-NLS-1$
        setMessage(DefaultMessagesImpl.getString("DQRuleCheckedTreeSelectionDialog.rule")); //$NON-NLS-1$
        setSize(80, 30);
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
                    return ResourceService.isSubFolder(ResourceManager.getRulesMatcherFolder(), folder);// getRulesMatcherFolder,getRulesFolder
                }
                return false;
            }
        });
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        overwriteBTN = new Button(composite, SWT.CHECK);
        overwriteBTN.setText(DefaultMessagesImpl.getString("DQRuleCheckedTreeSelectionDialog.isOverwrite"));
        overwriteBTN.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                isOverwrite = overwriteBTN.getSelection();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // no need to implement
            }

        });
        return composite;

    }

    public boolean isOverwrite() {
        return isOverwrite;
    }

}
