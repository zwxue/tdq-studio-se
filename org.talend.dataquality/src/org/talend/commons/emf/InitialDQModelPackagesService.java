// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.commons.emf;

import org.talend.core.repository.utils.AbstractDQModelService;


/**
 * DOC klliu  class global comment. Detailled comment
 */
public class InitialDQModelPackagesService extends AbstractDQModelService {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.utils.AbstractDQModelService#initTDQEMFResource()
     */
    @Override
    public void initTDQEMFResource() {
        EMFUtil.initialize();
    }
}
