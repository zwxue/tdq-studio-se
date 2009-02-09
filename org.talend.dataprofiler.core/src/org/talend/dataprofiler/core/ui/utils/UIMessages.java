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
package org.talend.dataprofiler.core.ui.utils;

import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * @author scorreia
 * 
 * Contains messages often used in wizards or in other UI windows.
 */
public class UIMessages {

    public static final String MSG_EMPTY_FIELD = DefaultMessagesImpl.getString("UIMessages.empty"); //$NON-NLS-1$

    public static final String MSG_ONLY_CHAR = DefaultMessagesImpl.getString("UIMessages.characterText"); //$NON-NLS-1$

    public static final String MSG_ONLY_NUMBER = DefaultMessagesImpl.getString("UIMessages.numeric"); //$NON-NLS-1$

    public static final String MSG_ONLY_REAL_NUMBER = DefaultMessagesImpl.getString("UIMessages.realNumber"); //$NON-NLS-1$

    public static final String MSG_ONLY_DATE = DefaultMessagesImpl.getString("UIMessages.date"); //$NON-NLS-1$

    public static final String MSG_VALID_FIELD = DefaultMessagesImpl.getString("UIMessages.inputValid"); //$NON-NLS-1$

    public static final String MSG_INVALID_FIELD = DefaultMessagesImpl.getString("UIMessages.fieldInvalid"); //$NON-NLS-1$

    public static final String MSG_LOWER_LESS_HIGHER = DefaultMessagesImpl.getString("UIMessages.lowerValuelessThanHigherValue"); //$NON-NLS-1$

    public static final String MSG_INDICATOR_WIZARD = "In this wizard, parameters for the given indicator can be set";

    public static final String MSG_INDICATOR_VALUE_OUT_OF_RANGE = "These percent value must between 0-100.";

    public static final String MSG_SELECT_GENERIC_JDBC = "Generic connection is provided mainly for trying to connect to untested databases. Use it only if your database does not appear in the list of supported databases. \n"
            + "When using a custom driver, generic SQL queries will be generated to compute indicators. These queries may not work on some databases. Help us to know these issues by reporting a bug at http://talendforge.org/bugs/my_view_page.php";

}
