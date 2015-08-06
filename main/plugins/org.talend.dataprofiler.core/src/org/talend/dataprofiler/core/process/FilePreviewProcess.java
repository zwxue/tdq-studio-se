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
package org.talend.dataprofiler.core.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.core.utils.CsvArray;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dq.helper.ParameterUtil;
import org.talend.repository.preview.IPreview;
import org.talend.repository.preview.IProcessDescription;

import com.csvreader.CsvReader;

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
        CsvReader csvReader = null;
        try {

            if (description.getLoopLimit() == null) {
                description.setLoopLimit(-1);
            }

            // MOD qiongli 2011-6-20 bug 21850,use CsvReader to parser
            final String pathStr = TalendQuoteUtils.removeQuotes(description.getFilepath());
            String fileSeparator = TalendQuoteUtils.removeQuotes(description.getFieldSeparator());
            String encoding = TalendQuoteUtils.removeQuotes(description.getEncoding());
            int headValue = description.getHeaderRow();

            if (fileSeparator.contains("t")) { //$NON-NLS-1$
                fileSeparator = String.valueOf('\t');
            }

            IPath filePath = new Path(pathStr);
            File file = filePath.toFile();

            if (!file.exists()) {
                MessageUI.openWarning(Messages.getString("System can not find the file specified"));
                return csvArray;
            }
            csvReader = new CsvReader(new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file),
                    encoding == null ? "UTF-8" : encoding)), ParameterUtil //$NON-NLS-1$
                    .trimParameter(fileSeparator).charAt(0));

            initCsvReader(csvReader, description);
            for (int i = 0; i < headValue && csvReader.readRecord(); i++) {
                // do nothing, just ignore the header part
            }
            String[] rowValues = null;
            long currentRecord = csvReader.getCurrentRecord();
            // just preview the top of 50
            while (csvReader.readRecord() && currentRecord < 50) {
                currentRecord = csvReader.getCurrentRecord();
                rowValues = csvReader.getValues();
                csvArray.add(rowValues);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csvReader != null) {
                csvReader.close();
            }
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

    public boolean isTopPreview() {
        return true;
    }

    /**
     * 
     * DOC qiongli Comment method "initCsvReader".
     * 
     * @param csvReader
     * @param description
     */
    private void initCsvReader(CsvReader csvReader, IProcessDescription description) {
        String rowSep = description.getRowSeparator();
        if (!rowSep.equals("\"\\n\"") && !rowSep.equals("\"\\r\"")) { //$NON-NLS-1$ //$NON-NLS-2$
            csvReader.setRecordDelimiter(ParameterUtil.trimParameter(rowSep).charAt(0));
        }

        csvReader.setSkipEmptyRecords(true);
        String textEnclosure = description.getTextEnclosure();
        if (!textEnclosure.equals("\"\"") && textEnclosure.length() > 0) {
            csvReader.setTextQualifier(ParameterUtil.trimParameter(textEnclosure).charAt(0));
        } else {
            csvReader.setUseTextQualifier(false);
        }
        String escapeChar = description.getEscapeCharacter();
        if (escapeChar == null || escapeChar.equals("\"\\\\\"") || escapeChar.equals("\"\"")) { //$NON-NLS-1$ //$NON-NLS-2$
            csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
        } else {
            csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
        }
    }

}
