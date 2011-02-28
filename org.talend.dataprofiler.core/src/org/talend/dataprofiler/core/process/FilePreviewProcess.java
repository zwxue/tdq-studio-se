// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2011 Talend – www.talend.com
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
package org.talend.dataprofiler.core.process;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.talend.core.utils.CsvArray;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.repository.preview.IPreview;
import org.talend.repository.preview.IProcessDescription;

/**
 * DOC bZhou class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class FilePreviewProcess implements IPreview {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.preview.IPreview#preview(org.talend.repository.preview.IProcessDescription,
     * java.lang.String)
     */
    public CsvArray preview(IProcessDescription description, String type) throws CoreException {
        return preview(description, type, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.preview.IPreview#preview(org.talend.repository.preview.IProcessDescription,
     * java.lang.String, boolean)
     */
    public CsvArray preview(IProcessDescription description, String type, boolean errorOutputAsException) throws CoreException {
        CsvArray csvArray = new CsvArray();

        try {

            if (description.getLoopLimit() == null) {
                description.setLoopLimit(-1);
            }

            final String filePath = TalendQuoteUtils.removeQuotes(description.getFilepath());
            final String fileEncode = TalendQuoteUtils.removeQuotes(description.getEncoding());
            String fileSeparator = TalendQuoteUtils.removeQuotes(description.getFieldSeparator());
            String rowSeparator = TalendQuoteUtils.removeQuotes(description.getRowSeparator());

            if (fileSeparator.contains("t")) {
                fileSeparator = String.valueOf('\t');
            }

            FileInputDelimited inputDelimited = new FileInputDelimited(filePath, fileEncode, fileSeparator, rowSeparator,
                    description.getRemoveEmptyRowsToSkip(), description.getHeaderRow(), description.getFooterRow(),
                    description.getLimitRows(), description.getLoopLimit(), description.isSplitRecord());

            while (inputDelimited.nextRecord()) {
                String[] csvStrArray = new String[inputDelimited.getColumnsCountOfCurrentRow()];
                for (int i = 0; i < inputDelimited.getColumnsCountOfCurrentRow(); i++) {
                    csvStrArray[i] = inputDelimited.get(i);
                }
                csvArray.add(csvStrArray);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvArray;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.preview.IPreview#stopLoading()
     */
    public void stopLoading() {
        // TODO Auto-generated method stub

    }

}
