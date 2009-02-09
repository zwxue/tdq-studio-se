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
package org.talend.cwm.compare.ui.actions;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.compare.ui.editor.ModelCompareEditorLauncher;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.IUIHandler;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import orgomg.cwm.objectmodel.core.ModelElement;


/**
 * DOC scorreia  class global comment. Detailled comment
 */
public class UiHandler implements IUIHandler {
 

    
    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.compare.factory.IUIHandler#popComparisonUI(org.eclipse.core.runtime.IPath)
     */
    public void popComparisonUI(final IPath diffResourcePath) throws ReloadCompareException {
        new ModelCompareEditorLauncher().open(diffResourcePath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.compare.factory.IUIHandler#popRemoveElement(org.talend.cwm.softwaredeployment.TdDataProvider)
     */
    public void popRemoveElement(final TdDataProvider provider) {
        // TODO Auto-generated method stub
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            public void run() {

                DeleteModelElementConfirmDialog.showElementImpactDialog(new Shell(display), new ModelElement[] { provider },
                        Messages.getString("UiHandler.followingAnalysisBeImpact")); //$NON-NLS-1$
            }
        });
    }

}
