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
package org.talend.cwm.management.api;

import org.junit.Test;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class DbmsLanguageTest {

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#DbmsLanguage()}.
     */
    @Test
    public void testDbmsLanguage() {
        // The constructor DbmsLanguage() is not visible
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#DbmsLanguage(java.lang.String)}.
     */
    @Test
    public void testDbmsLanguageString() {
        // The constructor DbmsLanguage(String) is not visible
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#DbmsLanguage(java.lang.String, int, int)}.
     */
    @Test
    public void testDbmsLanguageStringIntInt() {
        // The constructor DbmsLanguage(String, ProductVersion) is not visible
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#and()}.
     */
    @Test
    public void testAnd() {
        // get a default DbmsLanguage
        DataManager dm = null;
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
        // test the medhod
        dbmsLanguage.and();
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#or()}.
     */
    @Test
    public void testOr() {
        // get a default DbmsLanguage
        DataManager dm = null;
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
        // test the medhod
        dbmsLanguage.or();
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#between()}.
     */
    @Test
    public void testBetween() {
        // get a default DbmsLanguage
        DataManager dm = null;
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
        // test the medhod
        dbmsLanguage.between();
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#notEqual()}.
     */
    @Test
    public void testNotEqual() {
        // get a default DbmsLanguage
        DataManager dm = null;
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
        // test the medhod
        dbmsLanguage.notEqual();
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#eos()}.
     */
    @Test
    public void testEos() {
        // get a default DbmsLanguage
        DataManager dm = null;
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
        // test the medhod
        dbmsLanguage.eos();
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#getDbmsName()}.
     */
    @Test
    public void testGetDbmsName() {
        // get a default DbmsLanguage
        DataManager dm = null;
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
        // test the medhod
        dbmsLanguage.getDbmsName();
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#getDefaultLanguage()}.
     */
    @Test
    public void testGetDefaultLanguage() {
        // get a default DbmsLanguage
        DataManager dm = null;
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
        // test the medhod
        dbmsLanguage.getDefaultLanguage();
    }

    /**
     * Test method for
     * {@link org.talend.dq.dbms.DbmsLanguage#toQualifiedName(java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testToQualifiedName() {
        // get a default DbmsLanguage
        DataManager dm = null;
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
        // test the medhod
        String catalog = "catalog"; //$NON-NLS-1$
        String schema = "schema"; //$NON-NLS-1$
        String table = "table"; //$NON-NLS-1$
        dbmsLanguage.toQualifiedName(catalog, schema, table);
    }

    static final String LIMIT = "LIMIT";

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#parseQuery(java.lang.String)}.
     */
    @Test
    public void testParseQuery() {
        // don't find this method in DbmsLanguage
    }

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguage#getZqlParser()}.
     */
    @Test
    public void testGetZqlParser() {
        // don't find this method in DbmsLanguage
    }

}
