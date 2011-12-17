// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.talend.cwm.db.connection.DBConnectTest;
import org.talend.cwm.management.api.DbmsLanguageTest;
import org.talend.cwm.management.api.DqRepositoryViewServiceTest;
import org.talend.cwm.management.connection.DatabaseContentRetrieverTest;
import org.talend.cwm.management.i18n.MessagesTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { DBConnectTest.class, DbmsLanguageTest.class, DqRepositoryViewServiceTest.class,
        DatabaseContentRetrieverTest.class, MessagesTest.class })
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
