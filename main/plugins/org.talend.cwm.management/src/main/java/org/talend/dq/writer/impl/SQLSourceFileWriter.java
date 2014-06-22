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
package org.talend.dq.writer.impl;

import org.talend.core.model.properties.Item;
import org.talend.core.repository.constants.FileConstants;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.AElementPersistance;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * For save the xx.sql file, because it is not an EMF model DOC yyin added 20120614 TDQ-5468
 */
public class SQLSourceFileWriter extends AElementPersistance {

    SQLSourceFileWriter() {
        super();
    }

    /* (non-Javadoc)
     * @see org.talend.dq.writer.AElementPersistance#notifyResourceChanges()
     */
    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save();
    }

    /* (non-Javadoc)
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        // TODO Auto-generated method stub
        return FileConstants.SQL_EXTENSION;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#save(org.talend.core.model.properties.Item, boolean)
     */
    @Override
    public ReturnCode save(Item item, boolean careDependency) {
        // TODO Auto-generated method stub
        return null;
    }

}
