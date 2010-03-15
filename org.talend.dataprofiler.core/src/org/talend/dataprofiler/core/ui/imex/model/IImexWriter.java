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

/**
 * DOC bZhou class global comment. Detailled comment
 */
public interface IImexWriter {

    /**
     * DOC bZhou Comment method "init".
     * 
     * @param resource
     * @param destination
     */
    public void initPath(ItemRecord resource, String destination);

    /**
     * DOC bZhou Comment method "write".
     * 
     * @throws IOException
     * @throws CoreException
     */
    public void write() throws IOException, CoreException;

    /**
     * DOC bZhou Comment method "finish".
     * 
     * @throws IOException
     */
    public void finish() throws IOException;
}
