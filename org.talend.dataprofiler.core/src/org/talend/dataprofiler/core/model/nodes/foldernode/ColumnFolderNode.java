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
package org.talend.dataprofiler.core.model.nodes.foldernode;

import java.util.List;

import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.helper.NeedSaveDataProviderHelper;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author rli
 * 
 */
public class ColumnFolderNode extends AbstractFolderNode {

    /**
     * @param name
     */
    public ColumnFolderNode() {
        super("Columns");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.AbstractFolderNode#loadChildren()
     */
    @Override
    public void loadChildren() {
        // get columns from either tables or views.
        ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(this.getParent());
        if (columnSet != null) {
            List<TdColumn> columnList = ColumnSetHelper.getColumns(columnSet);
            if (columnList.size() > 0) {
                this.setLoaded(true);
                this.setChildren(columnList.toArray());
                return;
            }
            Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
            if (parentCatalogOrSchema == null) {
                return;
            }
            TdDataProvider provider = DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);
            if (provider == null) {
                return;
            }
            try {
                columnList = DqRepositoryViewService.getColumns(provider, columnSet, null, true);
            } catch (TalendException e) {
                MessageBoxExceptionHandler.process(e);
            }
            // store tables in catalog
            ColumnSetHelper.addColumns(columnSet, columnList);
            this.setChildren(columnList.toArray());
            NeedSaveDataProviderHelper.register(provider.getName(), provider);
            this.setLoaded(true);
        }
    }

}
