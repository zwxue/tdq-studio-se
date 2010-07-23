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
package org.talend.cwm.compare.factory.update;

import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import orgomg.cwm.foundation.keysindexes.KeyRelationship;
import orgomg.cwm.foundation.keysindexes.UniqueKey;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.PrimaryKey;
import orgomg.cwm.resource.relational.Table;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class AddTdRelationalSwitch extends UpdateRelationalSwitch {

    private EObject leftElement = null;

    public void setLeftElement(EObject leftElement) {
        this.leftElement = leftElement;
    }

    @Override
    public Boolean caseForeignKey(ForeignKey object) {
        ColumnSet columnSet = null;
        TdColumn tdColumn = null;
        if (leftElement instanceof Column) {
            tdColumn = (TdColumn) leftElement;
            columnSet = ColumnHelper.getColumnSetOwner(tdColumn);
        }
        if (columnSet == null) {
            return Boolean.FALSE;
        }
        String fkName = object.getName();
        ForeignKey foreignKey = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createForeignKey();
        foreignKey.setName(fkName);
        if (columnSet instanceof Table) {
            TableHelper.addForeignKey((TdTable) columnSet, foreignKey);
            tdColumn.getKeyRelationship().add(foreignKey);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean caseKeyRelationship(KeyRelationship object) {
        // TODO Auto-generated method stub
        return super.caseKeyRelationship(object);
    }

    @Override
    public Boolean casePrimaryKey(PrimaryKey object) {
        ColumnSet columnSet = null;
        TdColumn tdColumn = null;
        if (leftElement instanceof Column) {
            tdColumn = (TdColumn) leftElement;
            columnSet = ColumnHelper.getColumnSetOwner(tdColumn);
        }
        if (columnSet == null) {
            return Boolean.FALSE;
        }
        String pkName = object.getName();
        PrimaryKey primaryKey = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createPrimaryKey();
        primaryKey.setName(pkName);
        if (columnSet instanceof Table) {
            TableHelper.addPrimaryKey((TdTable) columnSet, primaryKey);
            tdColumn.getUniqueKey().add(primaryKey);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean caseUniqueKey(UniqueKey object) {
        // TODO Auto-generated method stub
        return super.caseUniqueKey(object);
    }

}
