// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.dbms;

import org.eclipse.core.runtime.Assert;
import org.junit.Test;

/**
 * Test class for DbmsLanguageFactory.
 */
public class DbmsLanguageFactoryTest {

    /**
     * Test method for {@link org.talend.dq.dbms.DbmsLanguageFactory#isHive(java.lang.String)}.
     */
    @Test
    public void testIsHive() {
        String hive = "Hive"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.isHive(hive));
        hive = "Apache Hive"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.isHive(hive));
        hive = "Hadoop Hive"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.isHive(hive));
        hive = "Hadoop"; //$NON-NLS-1$
        Assert.isTrue(!DbmsLanguageFactory.isHive(hive));
        hive = "Hadive"; //$NON-NLS-1$
        Assert.isTrue(!DbmsLanguageFactory.isHive(hive));
    }

    /**
     * Test method for
     * {@link org.talend.dq.dbms.DbmsLanguageFactory#compareDbmsLanguage(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testCompareDbmsLanguage() {
        // test the common database language, eg: mysql, mssql, oracle...
        String dbType = "Mysql"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.MYSQL));
        dbType = "MySQL"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.MYSQL));
        dbType = "MySQL123"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.MYSQL));
        dbType = "My12sql3"; //$NON-NLS-1$
        Assert.isTrue(!DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.MYSQL));

        dbType = "Microsoft SQL Server"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.MSSQL));
        dbType = "Microsoft sql server"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.MSSQL));
        dbType = "Microsoft SQL Server Database"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.MSSQL));
        dbType = "ms sql server"; //$NON-NLS-1$
        Assert.isTrue(!DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.MSSQL));

        dbType = "Oracle"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.ORACLE));
        dbType = "oracle"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.ORACLE));
        dbType = "Oracle 9i"; //$NON-NLS-1$
        Assert.isTrue(DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.ORACLE));
        dbType = "ora_cle 10g"; //$NON-NLS-1$
        Assert.isTrue(!DbmsLanguageFactory.compareDbmsLanguage(dbType, DbmsLanguage.ORACLE));
    }
}
