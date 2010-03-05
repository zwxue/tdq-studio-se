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
package org.talend.dq.writer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public interface IElementPersistence {

    /**
     * DOC bZhou Comment method "create".
     * 
     * @param element
     * @param folder
     * @return
     */
    public TypedReturnCode<IFile> create(ModelElement element, IFolder folder);

    /**
     * DOC bZhou Comment method "save".
     * 
     * @param element
     * @param file
     * @return
     */
    public ReturnCode save(ModelElement element, IFile file);

    /**
     * DOC bZhou Comment method "save".
     * 
     * @param element
     * @return
     */
    public ReturnCode save(ModelElement element);

    /**
     * DOC bZhou Comment method "check".
     * 
     * @param file
     * @return
     */
    public boolean check(IFile file);
}
