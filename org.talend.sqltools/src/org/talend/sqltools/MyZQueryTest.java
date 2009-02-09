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
        String db = "weka";
        String table = db + ".arff";
        String[] columns = new String[] { table + ".outlook", table + ".temperature" };

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

        System.out.println("## From part");
        Vector<ZFromItem> froms = query.getFrom();
        for (ZFromItem from : froms) {
            System.out.println("\talias " + from.getAlias());
            System.out.println("\tSchema " + from.getSchema());
            System.out.println("\ttable " + from.getTable());
            System.out.println("\tcolumn " + from.getColumn());
        }

        System.out.println("## Select part");
        Vector<ZSelectItem> sel = query.getSelect();
        for (ZSelectItem from : sel) {
            System.out.println("\talias " + from.getAggregate());
            System.out.println("\talias " + from.getExpression());
            System.out.println("\talias " + from.getAlias());
            System.out.println("\tSchema " + from.getSchema());
            System.out.println("\ttable " + from.getTable());
            System.out.println("\tcolumn " + from.getColumn());
            System.out.println();
        }
    }

}
