// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils.pagination;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;

/**
 * 
 * DOC mzhao UIPagination class global comment. Detailled comment
 */
public abstract class PaginationInfo implements IPagination {

    private static Logger log = Logger.getLogger(PaginationInfo.class);

    protected ScrolledForm form;

    protected List<? extends Object> objectList;

    protected List<Widget> needDispostWidgets = new ArrayList<Widget>();

    protected UIPagination uiPagination;

    public PaginationInfo(ScrolledForm form, List<? extends Object> objectList, UIPagination uiPagination) {
        this.form = form;
        this.objectList = objectList;
        this.uiPagination = uiPagination;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.pagination.IPagination#renderContents()
     */
    public void renderContents() {
        IRunnableWithProgress rwp = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Loading page...", objectList.size()); //$NON-NLS-1$
                uiPagination.notifyPageNavigator();
                render();
                uiPagination.updatePageInfoLabel();
                monitor.done();
                uiPagination.pack();
                form.reflow(true);
            }
        };
        try {
            ProgressUI.popProgressDialog(rwp, false, true);
        } catch (Exception ex) {
            log.error(ex, ex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.pagination.IPagination#dispose()
     */
    public void dispose() {
        for (Widget widget : needDispostWidgets) {
            widget.dispose();
        }
        needDispostWidgets.clear();
    }

    /**
     * DOC bZhou Comment method "render".
     */
    protected abstract void render();

}
