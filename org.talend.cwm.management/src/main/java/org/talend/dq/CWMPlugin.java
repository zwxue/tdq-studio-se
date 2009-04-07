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
package org.talend.dq;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.osgi.framework.BundleContext;

public class CWMPlugin extends Plugin {

    private static CWMPlugin self;

    public CWMPlugin() {
        super();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        self = this;
        initPreferences(self);
    }

    /**
     * DOC xqliu Comment method "initPreferences".
     * 
     * @param cwm
     */
    private void initPreferences(CWMPlugin cwm) {
        Preferences prefs = cwm.getPluginPreferences();
        prefs.setDefault(PluginConstant.CONNECTION_TIMEOUT, false);
    }

    public static CWMPlugin getDefault() {
        return self;
    }
}
