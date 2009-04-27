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
package org.talend.dataprofiler.core.model.nodes.foldernode;

import java.util.List;

import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dq.helper.NeedSaveDataProviderHelper;
import org.talend.dq.nodes.foldernode.AbstractDatabaseFolderNode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author rli
 * 
 */
public class ColumnFolderNode extends AbstractDatabaseFolderNode {

    /**
     * @param name
     */
    public ColumnFolderNode() {
        super(DefaultMessagesImpl.getString("ColumnFolderNode.columns")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.AbstractFolderNode#loadChildren()
     */
    @Override
    public void loadChildren() {
        // MOD xqliu 2009-04-27 bug 6507
        // get columns from either tables or views.
        ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(this.getParent());
        if (columnSet != null) {
            List<TdColumn> columnList = ColumnSetHelper.getColumns(columnSet, true);
            if (columnList.size() > 0) {
                if (columnList.size() == 1 && columnList.get(0).getName().equals(TaggedValueHelper.TABLE_VIEW_COLUMN_OVER_FLAG)) {
                    this.setChildren(null);
                    MessageUI
                            .openWarning(DefaultMessagesImpl.getString("ColumnFolderNode.warnMsg", TaggedValueHelper.COLUMN_MAX));
                } else {
                    this.setChildren(columnList.toArray());
                }
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
                TaggedValue tv = TaggedValueHelper.getTaggedValue(TaggedValueHelper.COLUMN_FILTER, columnSet.getTaggedValue());
                String columnFilter = tv == null ? null : tv.getValue();
                columnList = DqRepositoryViewService.getColumns(provider, columnSet, columnFilter, true);
                if (columnList.size() == 1 && columnList.get(0).getName().equals(TaggedValueHelper.TABLE_VIEW_COLUMN_OVER_FLAG)) {
                    // if (columnList.size() > TaggedValueHelper.COLUMN_MAX) {
                    this.setChildren(null);
                    MessageUI
                            .openWarning(DefaultMessagesImpl.getString("ColumnFolderNode.warnMsg", TaggedValueHelper.COLUMN_MAX));
                    return;
                }
            } catch (TalendException e) {
                MessageBoxExceptionHandler.process(e);
            }
            // store tables in catalog
            // MOD scorreia 2009-01-29 columns are stored in the table
            // ColumnSetHelper.addColumns(columnSet, columnList);
            this.setChildren(columnList.toArray());
            NeedSaveDataProviderHelper.register(provider.eResource().getURI().path(), provider);
        }
        super.loadChildren();
        // ~
    }

    public int getFolderNodeType() {
        return COLUMNFOLDER_NODE_TYPE;
    }

}
