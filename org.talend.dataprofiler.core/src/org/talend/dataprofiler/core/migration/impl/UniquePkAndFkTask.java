// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.PrimaryKey;

/**
 * 
 * DOC zshen class global comment. one table only have one primaryKey element and one foreignKey element,so filter the
 * *.prv file others to one.
 */
public class UniquePkAndFkTask extends AWorkspaceTask {

    public UniquePkAndFkTask() {

    }

    @Override
    protected boolean doExecute() throws Exception {
        boolean returnFlag = false;
        IFolder folder = ResourceManager.getConnectionFolder();
        IResource[] resource = null;

        try {
            if (folder != null && folder.exists())
                resource = folder.members();
        } catch (CoreException e1) {
            e1.printStackTrace();
        }
        if (resource != null && resource.length > 0) {
            for (IResource re : resource) {
                if (re instanceof IFile && FactoriesUtil.isProvFile(re.getFileExtension())) {
                    IFile file = (IFile) re;
                    boolean saveFalg = false;
                    TdDataProvider tdDataProvider = PrvResourceFileHelper.getInstance().findProvider(file).getObject();
                    List<orgomg.cwm.objectmodel.core.Package> dataManagerList = tdDataProvider.getDataPackage();
                    for (orgomg.cwm.objectmodel.core.Package firstLevelElement : dataManagerList) {
                        saveFalg = getTables(firstLevelElement).isOk().booleanValue();
                    }
                    if (saveFalg) {
                        PrvResourceFileHelper.getInstance().save(tdDataProvider);
                    }

                }

            }
            returnFlag = true;
        }
        return returnFlag;
    }

    private Boolean checkPkAndFk(TdTable element) {
        boolean needToSave = false;
        // case pk
        if (TableHelper.getPrimaryKeys(element).size() > 1) {
            List<PrimaryKey> primaryKeyList = TableHelper.getPrimaryKeys(element);
            for (int i = 1; i < primaryKeyList.size(); i++) {
                PrimaryKey pk = primaryKeyList.get(i);
                Column column = (Column) pk.getFeature().get(pk.getFeature().size() - 1);
                ColumnHelper.removePrimaryKey(column);// remove old pk from column
                column.getUniqueKey().add(primaryKeyList.get(0));// add new pk to column
                primaryKeyList.get(0).getFeature().add(column);// add column to new pk
                TableHelper.removePrimaryKey(element, pk);// remove old pk from table
            }
            needToSave = true;
        }
        // case fk
        if (TableHelper.getForeignKeys(element).size() > 1) {
            List<ForeignKey> foreignKeyList = TableHelper.getForeignKeys(element);
            for (int i = 1; i < foreignKeyList.size(); i++) {
                ForeignKey fk = foreignKeyList.get(i);
                Column column = (Column) fk.getFeature().get(fk.getFeature().size() - 1);
                ColumnHelper.removeForeignKey(column);// remove old fk from column
                column.getKeyRelationship().add(foreignKeyList.get(0));// add new fk to column
                foreignKeyList.get(0).getFeature().add(column);// add column to new pk
                TableHelper.removeForeignKey(element, fk);// remove old fk from table
            }
            needToSave = true;
        }
        return Boolean.valueOf(needToSave);
    }

    private TypedReturnCode<List<TdTable>> getTables(ModelElement element) {
        TypedReturnCode<List<TdTable>> returnCode = new TypedReturnCode<List<TdTable>>(false);
        List<TdTable> tableList = new ArrayList<TdTable>();
        returnCode.setObject(tableList);
        if (element == null)
            return returnCode;
        if (element instanceof TdCatalog) {
            for (ModelElement subElement : ((TdCatalog) element).getOwnedElement()) {
                tableList.addAll(getTables(subElement).getObject());
            }
        }
        if (element instanceof TdSchema) {
            for (ModelElement subElement : ((TdSchema) element).getOwnedElement()) {
                tableList.addAll(getTables(subElement).getObject());
            }
        }
        if (element instanceof TdTable) {
            tableList.add((TdTable) element);
            returnCode.setOk(checkPkAndFk((TdTable) element));
        }

        return returnCode;

    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {

        return createDate(2010, 6, 9);
    }

}
