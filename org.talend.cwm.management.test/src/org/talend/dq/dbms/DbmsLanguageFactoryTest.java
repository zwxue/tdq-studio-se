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
}
