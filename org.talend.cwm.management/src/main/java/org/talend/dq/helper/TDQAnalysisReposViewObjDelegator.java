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
package org.talend.dq.helper;

import java.util.List;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.analysis.Analysis;
import org.talend.utils.sugars.ReturnCode;

/**
 * 
 * DOC mzhao Help to store analysis repository view object.
 */
public class TDQAnalysisReposViewObjDelegator extends AbstractDQRepositoryViewObjectDelegator<Analysis> {

    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ReturnCode save(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

}
