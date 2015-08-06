// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.rcp.intro;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;

/**
 * DOC rli class global comment. Detailled comment <br/>
 * 
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1024, 768));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(false);
        String buildId = VersionUtils.getVersion();
        IBrandingService brandingService = GlobalServiceRegister.getDefault().getBrandingService(IBrandingService.class);
        configurer.setTitle(brandingService.getFullProductName() + " (" + buildId + ")"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowOpen()
     */
    @Override
    public void postWindowOpen() {

        this.getWindowConfigurer().getWindow().getShell().setMaximized(true);
        super.postWindowOpen();
        ChangePerspectiveAction.getAction().switchTitle();
        ITDQRepositoryService tdqRepositoryService = (ITDQRepositoryService) org.talend.core.GlobalServiceRegister.getDefault()
                .getService(ITDQRepositoryService.class);
        if (tdqRepositoryService != null) {
            tdqRepositoryService.addPartListener();
        }
    }

}
