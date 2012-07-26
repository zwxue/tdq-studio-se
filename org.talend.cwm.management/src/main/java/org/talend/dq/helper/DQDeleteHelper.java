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
package org.talend.dq.helper;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public final class DQDeleteHelper {

    private static Logger log = Logger.getLogger(DQDeleteHelper.class);

    private static DQDeleteHelper instance;

    private DQDeleteHelper() {

    }

    /**
     * 
     * physical delete related.
     * 
     * @param item
     */
    public static ReturnCode deleteRelations(Item item) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);
        if (item == null || item.getProperty() == null || item instanceof FolderItem) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }

        IFile itemFile = PropertyHelper.getItemFile(item.getProperty());
        // if file is null or this file is not physical deleted,do nothing.
        if (itemFile == null || itemFile.exists()) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        if (item instanceof TDQReportItem) {
            return deleteRepOutputFolder(itemFile);
        }
        return rc;
    }

    /**
     * 
     * delete the related output folder of report.
     * 
     * @param reportFile
     */
    private static ReturnCode deleteRepOutputFolder(IFile reportFile) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);
        IFolder currentRportFolder = ReportUtils.getOutputFolder(reportFile);
        if (currentRportFolder != null && currentRportFolder.exists()) {
            try {
                currentRportFolder.delete(true, new NullProgressMonitor());
            } catch (CoreException e) {
                log.error(e, e);
                rc.setOk(Boolean.FALSE);
                rc.setMessage(e.getMessage());
            }
        } else {
            rc.setOk(Boolean.FALSE);
        }
        return rc;
    }

}
