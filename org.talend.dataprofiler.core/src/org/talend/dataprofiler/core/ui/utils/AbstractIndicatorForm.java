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
package org.talend.dataprofiler.core.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.ui.internal.views.ReusableHelpPart;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.help.HelpPlugin;

/**
 * DOC zqin class global comment. Detailled comment
 */
public abstract class AbstractIndicatorForm extends AbstractForm {

    private static List<AbstractIndicatorParameter> parameters = new ArrayList<AbstractIndicatorParameter>();

    // ----message define----

    public static final String MSG_EMPTY = UIMessages.MSG_EMPTY_FIELD;

    public static final String MSG_ONLY_CHAR = UIMessages.MSG_ONLY_CHAR;

    public static final String MSG_ONLY_NUMBER = UIMessages.MSG_ONLY_NUMBER;

    public static final String MSG_ONLY_REAL_NUMBER = UIMessages.MSG_ONLY_REAL_NUMBER;

    public static final String MSG_ONLY_DATE = UIMessages.MSG_ONLY_DATE;

    public static final String MSG_OK = UIMessages.MSG_VALID_FIELD;

    /**
     * DOC zqin AbstractIndicatorForm constructor comment.
     * 
     * @param parent
     * @param style
     */
    public AbstractIndicatorForm(Composite parent, int style, AbstractIndicatorParameter parameter) {
        super(parent, style);
        parameters.add(parameter);
    }

    public static AbstractIndicatorParameter[] getParameters() {

        return parameters.toArray(new AbstractIndicatorParameter[parameters.size()]);
    }

    public static boolean isParametersEmpty() {
        return parameters.isEmpty();
    }

    public static void emptyParameterList() {

        parameters.clear();
    }

    public abstract FormEnum getFormEnum();

    public String getFormName() {
        return getFormEnum().getFormName();
    }

    public void showHelp() {
        String href = getFormEnum().getHelpHref();
        if (href != null && href.endsWith(".html")) {
            Point point = getShell().getDisplay().getCursorLocation();
            IContext context = HelpSystem.getContext(HelpPlugin.PATTERN_CONTEXT_HELP_ID);
            IHelpResource[] relatedTopics = context.getRelatedTopics();
            IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
            helpSystem.displayContext(context, point.x + 15, point.y);
            ReusableHelpPart lastActiveInstance = ReusableHelpPart.getLastActiveInstance();
            if (lastActiveInstance != null) {
                lastActiveInstance.showURL(href);
            }
        }
    }

}
