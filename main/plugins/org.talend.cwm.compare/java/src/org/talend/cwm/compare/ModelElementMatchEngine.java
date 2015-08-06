// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.compare;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.compare.FactoryException;
import org.eclipse.emf.compare.match.engine.GenericMatchEngine;
import org.eclipse.emf.ecore.EObject;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * yyi 2011-03-30 19137:reload database list on editing connection url.
 */
public class ModelElementMatchEngine extends GenericMatchEngine {

    /*
     * ADD yyi 2011-03-30 19137:reload database list on editing connection url
     * 
     * @see org.eclipse.emf.compare.match.engine.GenericMatchEngine#isSimilar(org.eclipse.emf.ecore.EObject,
     * org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean isSimilar(EObject obj1, EObject obj2) throws FactoryException {

        // MOD xqliu 2011-06-03 bug:16538
        // return changeUrl ? false : super.isSimilar(obj1, obj2);

        boolean result = true;
        if (obj1 instanceof Catalog && obj2 instanceof Catalog) {
            Catalog catalog1 = (Catalog) obj1;
            Catalog catalog2 = (Catalog) obj2;
            result = StringUtils.equalsIgnoreCase(catalog1.getName(), catalog2.getName());
        } else if (obj1 instanceof Schema && obj2 instanceof Schema) {
            Schema schema1 = (Schema) obj1;
            Schema schema2 = (Schema) obj2;
            result = StringUtils.equalsIgnoreCase(schema1.getName(), schema2.getName());
        } else if (obj1 instanceof TdTable && obj2 instanceof TdTable) {
            TdTable tdTable1 = (TdTable) obj1;
            TdTable tdTable2 = (TdTable) obj2;
            result = StringUtils.equalsIgnoreCase(tdTable1.getName(), tdTable2.getName());
        } else if (obj1 instanceof TdView && obj2 instanceof TdView) {
            TdView tdView1 = (TdView) obj1;
            TdView tdView2 = (TdView) obj2;
            result = StringUtils.equalsIgnoreCase(tdView1.getName(), tdView2.getName());
        } else if (obj1 instanceof TdColumn && obj2 instanceof TdColumn) {
            TdColumn tdColumn1 = (TdColumn) obj1;
            TdColumn tdColumn2 = (TdColumn) obj2;
            result = StringUtils.equalsIgnoreCase(tdColumn1.getName(), tdColumn2.getName())
                    && isSimilar(tdColumn1.getSqlDataType(), tdColumn2.getSqlDataType());
        } else if (obj1 instanceof TaggedValue && obj2 instanceof TaggedValue) {
            TaggedValue taggedValue1 = (TaggedValue) obj1;
            TaggedValue taggedValue2 = (TaggedValue) obj2;
            result = StringUtils.equalsIgnoreCase(taggedValue1.getTag(), taggedValue2.getTag())
                    && StringUtils.equalsIgnoreCase(taggedValue1.getValue(), taggedValue2.getValue());
        } else if (obj1 instanceof TdExpression && obj2 instanceof TdExpression) {
            TdExpression tdExpression1 = (TdExpression) obj1;
            TdExpression tdExpression2 = (TdExpression) obj2;
            result = StringUtils.equalsIgnoreCase(tdExpression1.getLanguage(), tdExpression2.getLanguage())
                    && StringUtils.equalsIgnoreCase(tdExpression1.getBody(), tdExpression2.getBody());
        } else if (obj1 instanceof TdSqlDataType && obj2 instanceof TdSqlDataType) {
            TdSqlDataType tdExpression1 = (TdSqlDataType) obj1;
            TdSqlDataType tdExpression2 = (TdSqlDataType) obj2;
            result = StringUtils.equalsIgnoreCase(tdExpression1.getName(), tdExpression2.getName());
        }

        if (obj1 instanceof DatabaseConnection) {
            return true;
        }
        // MOD yyin 20130201 TDQ-6780, do not use "isURlChanged" any more.
        return result;
        // ~ 16538
    }

    /*
     * ADD yyi 2011-03-30 19137:reload database list on editing connection url
     * 
     * @see org.eclipse.emf.compare.match.engine.GenericMatchEngine#reset()
     */
    @Override
    public void reset() {
        super.reset();
    }
}
