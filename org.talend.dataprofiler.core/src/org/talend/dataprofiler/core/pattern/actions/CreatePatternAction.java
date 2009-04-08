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
package org.talend.dataprofiler.core.pattern.actions;

import org.eclipse.core.resources.IFolder;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.CreatePatternWizard;
import org.talend.dataprofiler.core.ui.utils.OpeningHelpWizardDialog;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dq.analysis.parameters.PatternParameter;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class CreatePatternAction extends Action {

    private IFolder folder;

    private ExpressionType type;

    private String expression;

    private String lanuage;

    /**
     * DOC qzhang AddSqlFileAction constructor comment.
     * 
     * @param folder
     * @param type
     */
    public CreatePatternAction(IFolder folder, ExpressionType type) {
        switch (type) {
        case SQL_LIKE:
            setText(DefaultMessagesImpl.getString("CreatePatternAction.newSQLPattern")); //$NON-NLS-1$
            break;
        default:
            setText(DefaultMessagesImpl.getString("CreatePatternAction.newRegularPattern")); //$NON-NLS-1$
            break;
        }
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ADD_PATTERN));
        this.folder = folder;
        this.type = type;
    }

    public CreatePatternAction(IFolder folder, ExpressionType type, String expression, String lanuage) {
        this(folder, type);
        this.expression = expression;
        this.lanuage = lanuage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (folder.exists()) {
            PatternParameter parameter = new PatternParameter();
            FolderProvider folderProvider = new FolderProvider();
            folderProvider.setFolderResource(folder);
            parameter.setFolderProvider(folderProvider);

            CreatePatternWizard fileWizard;
            if (this.expression != null && this.lanuage != null) {
                fileWizard = (CreatePatternWizard) WizardFactory.createPatternWizard(type, parameter, expression, lanuage);
            } else {
                fileWizard = (CreatePatternWizard) WizardFactory.createPatternWizard(type, parameter);
            }
            IContext context = HelpSystem.getContext(HelpPlugin.getDefault().getPatternHelpContextID());
            IHelpResource[] relatedTopics = context.getRelatedTopics();
            String href = relatedTopics[0].getHref();
            switch (type) {
            case SQL_LIKE:
                href = relatedTopics[1].getHref();
                break;
            default:
                break;
            }
            WizardDialog dialog = new OpeningHelpWizardDialog(Display.getDefault().getActiveShell(), fileWizard, href);
            fileWizard.setWindowTitle(getText());
            dialog.open();
        }
    }
}
