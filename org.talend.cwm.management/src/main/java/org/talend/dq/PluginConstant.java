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
package org.talend.dq;

import org.talend.commons.emf.FactoriesUtil;

/**
 * This class store all the constant of current plugin.
 * 
 */
public final class PluginConstant {

    private PluginConstant() {
    }

    public static final String EMPTY_STRING = "";

    public static final String PASSWORD_PROPERTY = "password";

    public static final String HOSTNAME_PROPERTY = "hostname";

    public static final String PORT_PROPERTY = "port";

    public static final String DBTYPE_PROPERTY = "dbtype";

    public static final String DEFAULT_PARAMETERS = "noDatetimeStringSync=true";

    public static final String PRV_SUFFIX = "." + FactoriesUtil.PROV;

    public static final String ANA_SUFFIX = "." + FactoriesUtil.ANA;

    public static final String REP_SUFFIX = "." + FactoriesUtil.REP;

    public static final String PATTERN_SUFFIX = "*.pattern";
}
