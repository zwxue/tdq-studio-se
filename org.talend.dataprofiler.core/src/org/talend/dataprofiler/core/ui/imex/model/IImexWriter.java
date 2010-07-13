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
package org.talend.dataprofiler.core.ui.imex.model;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public interface IImexWriter {

    /**
     * DOC bZhou Comment method "computeInput".
     * 
     * @param path
     * @return
     */
    public ItemRecord computeInput(IPath path);

    /**
     * DOC bZhou Comment method "setBasePath".
     * 
     * @param basePath
     */
    public void setBasePath(IPath path);

    /**
     * DOC bZhou Comment method "getBasePath".
     * 
     * @return
     */
    public IPath getBasePath();

    /**
     * DOC bZhou Comment method "checkBasePath".
     * 
     * Check the base path is valid or not.
     * 
     * @return
     */
    public ReturnCode checkBasePath();

    /**
     * DOC bZhou Comment method "populate".
     * 
     * @param elements
     * @param checkExisted
     * @return
     */
    public ItemRecord[] populate(ItemRecord[] elements, boolean checkExisted);

    /**
     * DOC bZhou Comment method "write".
     * 
     * @param recored
     * @throws IOException
     * @throws CoreException
     */
    public void write(ItemRecord recored) throws IOException, CoreException;

    /**
     * DOC bZhou Comment method "finish".
     * 
     * @throws IOException
     */
    public void finish(ItemRecord[] records) throws IOException;
}
