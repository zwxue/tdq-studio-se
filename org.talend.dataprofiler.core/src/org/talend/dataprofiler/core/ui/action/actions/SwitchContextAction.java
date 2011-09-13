// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.talend.core.model.context.ContextUtils;
import org.talend.core.model.properties.ContextItem;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.SwitchContextDialog;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.ReturnCode;
/**
 * DOC msjian class global comment. Detailled comment
 */
public class SwitchContextAction extends Action {

    private static Logger log = Logger.getLogger(SwitchContextAction.class);

    private Object selectedObject;

    public SwitchContextAction(Object selectedNode, String menuText) {
        super(menuText);
        this.selectedObject = selectedNode;
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.SWITCH_IMAGE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        SwitchContextDialog switchContextDialog = new SwitchContextDialog(Display.getDefault().getActiveShell(), selectedObject);
        if (switchContextDialog.open() == Window.OK) {
            String contextName = switchContextDialog.getContextName();
            ContextItem contextItem = ContextUtils.getContextItemByName(contextName);
            String id = contextItem.getProperty().getId();
            if (selectedObject instanceof DBConnectionRepNode) {
                DBConnectionRepNode dbConnectionRepNode = (DBConnectionRepNode) selectedObject;
                dbConnectionRepNode.getDatabaseConnection().setContextId(id);

                ReturnCode returnCode = ElementWriterFactory.getInstance().createDataProviderWriter()
                        .save(dbConnectionRepNode.getDatabaseConnection());
                String fileStr = dbConnectionRepNode.getDatabaseConnection().eResource().getURI().toFileString();
                if (returnCode.isOk()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Saved in  " + fileStr + " successful"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } else {
                    try {
                        throw new DataprofilerCoreException(DefaultMessagesImpl.getString("ConnectionInfoPage.ProblemSavingFile",
                                fileStr, returnCode.getMessage()));
                    } catch (DataprofilerCoreException e) {
                        e.printStackTrace();
                    } //$NON-NLS-1$
                }
            }
        }
    }
}
