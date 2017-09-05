// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.sqltools;

import java.util.Vector;

import Zql.ZFromItem;
import Zql.ZQuery;
import Zql.ZSelectItem;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class MyZQueryTest {

    /**
     * DOC scorreia Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {
        String db = "weka"; //$NON-NLS-1$
        String table = db + ".arff"; //$NON-NLS-1$
        String[] columns = new String[] { table + ".outlook", table + ".temperature" }; //$NON-NLS-1$ //$NON-NLS-2$

        ZQuery query = new ZQuery();

        Vector<ZFromItem> f = new Vector<ZFromItem>();
        f.add(new ZFromItem(table));
        query.addFrom(f);

        Vector<ZSelectItem> s = new Vector<ZSelectItem>();
        for (String col : columns) {
            s.add(new ZSelectItem(col));
        }
        query.addSelect(s);
        // simple select
        System.out.println(query);

        System.out.println("## From part"); //$NON-NLS-1$
        Vector<ZFromItem> froms = query.getFrom();
        for (ZFromItem from : froms) {
            System.out.println("\talias " + from.getAlias()); //$NON-NLS-1$
            System.out.println("\tSchema " + from.getSchema()); //$NON-NLS-1$
            System.out.println("\ttable " + from.getTable()); //$NON-NLS-1$
            System.out.println("\tcolumn " + from.getColumn()); //$NON-NLS-1$
        }

        System.out.println("## Select part"); //$NON-NLS-1$
        Vector<ZSelectItem> sel = query.getSelect();
        for (ZSelectItem from : sel) {
            System.out.println("\talias " + from.getAggregate()); //$NON-NLS-1$
            System.out.println("\talias " + from.getExpression()); //$NON-NLS-1$
            System.out.println("\talias " + from.getAlias()); //$NON-NLS-1$
            System.out.println("\tSchema " + from.getSchema()); //$NON-NLS-1$
            System.out.println("\ttable " + from.getTable()); //$NON-NLS-1$
            System.out.println("\tcolumn " + from.getColumn()); //$NON-NLS-1$
            System.out.println();
        }
    }

}
