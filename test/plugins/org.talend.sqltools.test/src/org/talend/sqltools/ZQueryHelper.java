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
package org.talend.sqltools;

import java.io.ByteArrayInputStream;
import java.util.Vector;

import org.apache.log4j.Logger;

import Zql.ParseException;
import Zql.ZExp;
import Zql.ZExpression;
import Zql.ZFromItem;
import Zql.ZSelectItem;
import Zql.ZqlParser;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public final class ZQueryHelper {

    protected static Logger log = Logger.getLogger(ZQueryHelper.class);

    private ZQueryHelper() {
    }

    public static Vector<ZSelectItem> createSelectVector(String... f) {
        Vector<ZSelectItem> v = new Vector<ZSelectItem>();
        if (f == null) {
            return v;
        }
        for (String string : f) {
            if (string != null) {
                v.add(new ZSelectItem(string));
            }
        }
        return v;
    }

    /**
     * DOC scorreia Comment method "createWhereVector".
     * 
     * @param f a list of where clauses
     * @return
     */
    public static Vector<ZExp> createWhereVector(String... f) {
        Vector<ZExp> v = new Vector<ZExp>();
        if (f == null) {
            return v;
        }
        for (String string : f) {
            if (string != null) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
                ZqlParser parser = new ZqlParser();
                parser.initParser(byteArrayInputStream);

                try {
                    ZExpression expr = (ZExpression) parser.readExpression();
                    v.add(expr);
                } catch (ParseException e) {
                    log.error(e, e);
                }
            }
        }
        return v;
    }

    public static Vector<ZFromItem> createFromVector(String... f) {
        Vector<ZFromItem> v = new Vector<ZFromItem>();
        if (f == null) {
            return v;
        }
        for (String string : f) {
            if (string != null) {
                v.add(new ZFromItem(string));
            }
        }
        return v;
    }
}
