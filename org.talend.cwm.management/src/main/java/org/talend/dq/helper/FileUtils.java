// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.io.File;

import org.eclipse.core.runtime.Path;

/**
 * DOC bZhou class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z bzhou $
 * 
 */
public final class FileUtils {

    /**
     * DOC bZhou Comment method "getName".
     * 
     * This method get the name of a file, without the file extension.
     * 
     * @param file
     * @return
     */
    public static String getName(File file) {
        Path fileNamePath = new Path(file.getName());
        return fileNamePath.removeFileExtension().toString();
    }

    /**
     * DOC bZhou Comment method "getExtension".
     * 
     * This method get the extension of a file.
     * 
     * @param file
     * @return
     */
    public static String getExtension(File file) {
        Path fileNamePath = new Path(file.getName());
        return fileNamePath.getFileExtension();
    }
}
