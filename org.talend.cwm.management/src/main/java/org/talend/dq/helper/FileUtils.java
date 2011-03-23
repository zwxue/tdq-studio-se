// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2011 Talend ¨C www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
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
