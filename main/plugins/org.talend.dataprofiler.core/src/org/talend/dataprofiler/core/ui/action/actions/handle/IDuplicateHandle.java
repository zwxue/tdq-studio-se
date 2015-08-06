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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.eclipse.core.resources.IFile;
import org.talend.commons.exception.BusinessException;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public interface IDuplicateHandle extends IActionHandle {

    /**
     * DOC bZhou Comment method "isExistedLabel".
     * 
     * @param label
     * @return
     */
    public boolean isExistedLabel(String label);

    /**
     * DOC bZhou Comment method "duplicate".
     * 
     * This function is to duplicate an item in tdq reponsitory.
     * 
     * @param newLabel
     * 
     * @return
     */
    public IFile duplicate(String newLabel) throws BusinessException;

    /**
     * DOC bZhou Comment method "validDuplicated".
     * 
     * This function is to sure this handle can be execute.
     * 
     * @return
     */
    public ReturnCode validDuplicated();

}
