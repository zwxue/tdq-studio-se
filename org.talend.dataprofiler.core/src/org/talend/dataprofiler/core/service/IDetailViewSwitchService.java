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
package org.talend.dataprofiler.core.service;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public interface IDetailViewSwitchService extends IService {

    /**
     * DOC Zqin Comment method "isShow".
     * 
     * this method is to judge whether the special detail group show or not.
     * 
     * @return retrun true, if show the special detail group, false will not.
     */
    public boolean isShow();
}
