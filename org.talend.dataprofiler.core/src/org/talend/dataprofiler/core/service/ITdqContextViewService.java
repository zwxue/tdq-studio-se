// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import org.eclipse.ui.IWorkbenchPart;

/**
 * created by xqliu on Jul 29, 2013 Detailled comment
 * 
 */
public interface ITdqContextViewService extends IService {

    public void updateContextView(IWorkbenchPart part);

    public void hideContextView(IWorkbenchPart part);

    public void resetContextView();
}
