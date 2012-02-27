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
package org.talend.dataquality.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewServiceTest;
import org.talend.cwm.db.connection.ConnectionUtilsTest;
import org.talend.cwm.management.i18n.MessagesTest;
import org.talend.dq.dbms.DbmsLanguageTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ConnectionUtilsTest.class, DbmsLanguageTest.class, DqRepositoryViewServiceTest.class,
 MessagesTest.class })
/**
 * DOC yyi 2010-05-27 for 12747: Refactor Unit Test for automatic testing
 */
public class AllTestsForJUnit4Suite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.talend.test.alltestsfortqd.junit4.suite");
        // $JUnit-BEGIN$

        // $JUnit-END$
        return suite;
    }

}
