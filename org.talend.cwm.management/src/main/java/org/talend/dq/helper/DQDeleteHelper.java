// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dq.CWMPlugin;

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

    public static DQDeleteHelper getInstance() {
        if (instance == null) {
            instance = new DQDeleteHelper();
        }

        return instance;
    }

    /**
     * 
     * physical delete related.
     * 
     * @param item
     */
    public void deleteRelations(Item item) {
        if (item == null || item.getProperty() == null) {
            return;
        }

        IFile itemFile = PropertyHelper.getItemFile(item.getProperty());
        if (itemFile.exists()) {
            return;
        }
        if (item instanceof DatabaseConnectionItem) {
            deleteConnectionForSQL((DatabaseConnectionItem) item);
        } else if (item instanceof TDQReportItem) {
            deleteRepOutputFolder(itemFile);
        }
    }

    /**
     * 
     * remove sql explorer.
     * 
     * @param reportFile
     */
    private void deleteConnectionForSQL(DatabaseConnectionItem item) {
        DatabaseConnection databaseConnection = (DatabaseConnection) ((DatabaseConnectionItem) item).getConnection();
        CWMPlugin.getDefault().removeAliasInSQLExplorer(databaseConnection);
    }

    /**
     * 
     * delete the related output folder of report.
     * 
     * @param reportFile
     */
    private void deleteRepOutputFolder(IFile reportFile) {
        IFolder currentRportFolder = ReportHelper.getOutputFolder(reportFile);
        if (currentRportFolder.exists()) {
            try {
                currentRportFolder.delete(true, null);
            } catch (CoreException e) {
                log.error(e, e);
            }
        }
    }

}
