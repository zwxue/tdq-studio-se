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
package org.talend.dataprofiler.help;

import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * DOC zqin  class global comment. Detailled comment
 * <br/>
 *
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 *
 */
public class HelpPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.talend.dataprofiler.help";
    
    public static final String INDICATOR_OPTION_HELP_ID = ".mycontexthelpid";
    
    public static final String INDICATOR_SELECTOR_HELP_ID = ".indicatorhelpcontext";
    
    private static HelpPlugin plugin;
    
    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static HelpPlugin getDefault() {
        return plugin;
    }
}
