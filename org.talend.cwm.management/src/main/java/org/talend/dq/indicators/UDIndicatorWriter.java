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
package org.talend.dq.indicators;

import org.apache.log4j.Logger;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dq.writer.AElementPersistance;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UDIndicatorWriter extends AElementPersistance {

    static Logger log = Logger.getLogger(UDIndicatorWriter.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addResourceContent(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addResourceContent(ModelElement element) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.UDI;
    }
}
