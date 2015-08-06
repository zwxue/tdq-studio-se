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
package org.talend.dataprofiler.core.migration.impl;

import org.junit.Test;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class RefactMdmMetadataTaskTest {

    /**
     * Test method for {@link org.talend.dataprofiler.core.migration.impl.RefactMdmMetadataTask#doExecute()}.
     */
    @Test
    public void testDoExecute() {
        try {
            RefactMdmMetadataTask refactMdmMetadataTask = new RefactMdmMetadataTask();
            refactMdmMetadataTask.doExecute();
            // FIXME there is no assert here

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
