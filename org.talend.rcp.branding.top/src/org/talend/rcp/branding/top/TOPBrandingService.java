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
package org.talend.rcp.branding.top;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.talend.dataprofiler.core.service.IBrandingService;

/**
 * DOC rli class global comment. Detailled comment
 */
public class TOPBrandingService implements IBrandingService {

    public String getFullProductName() {
        return "Talend Open Profiler"; //$NON-NLS-1$
    }

    public URL getLicenseFile() throws IOException {
        final Bundle b = Platform.getBundle(Activator.PLUGIN_ID);
        final URL url = FileLocator.toFileURL(FileLocator.find(b, new Path("resources/license.txt"), null)); //$NON-NLS-1$
        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.service.IBrandingService#getCorporationName()
     */
    public String getCorporationName() {
        return "Talend"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.service.IBrandingService#getShortProductName()
     */
    public String getShortProductName() {
        return "T.O.P"; //$NON-NLS-1$
    }

    public String getAcronym() {
        return "top";
    }

}
