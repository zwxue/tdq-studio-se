// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.db.connection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.Escape;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.helper.AnalysisExecutorHelper;
import org.talend.fileprocess.FileInputDelimited;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.csvreader.CsvReader;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class DelimitedFileSQLExecutor implements ISQLExecutor {

    /* (non-Javadoc)
     * @see org.talend.cwm.db.connection.ISQLExecutor#executeQuery(org.talend.dataquality.analysis.Analysis)
     */
    public List<Object[]> executeQuery(Analysis analysis) {
        List<Object[]> dataFromTable = new ArrayList<Object[]>();
        DelimitedFileConnection delimitedFileconnection = (DelimitedFileConnection) analysis.getContext().getConnection();
        String path = AnalysisExecutorHelper.getFilePath(delimitedFileconnection);
        IPath iPath = new Path(path);

        try {
            File file = iPath.toFile();
            if (!file.exists()) {
                return null;
            }

            List<ModelElement> analysisElementList = analysis.getContext().getAnalysedElements();
            // use CsvReader to parse.
            if (Escape.CSV.equals(delimitedFileconnection.getEscapeType())) {
                useCsvReader(file, delimitedFileconnection, analysisElementList,dataFromTable);
            } else {
                // use TOSDelimitedReader in FileInputDelimited to parse.
                FileInputDelimited fileInputDelimited = AnalysisExecutorHelper.createFileInputDelimited(delimitedFileconnection);
                long currentRow = AnalysisExecutorHelper.getHeadValue(delimitedFileconnection);

                while (fileInputDelimited.nextRecord()) {
                    currentRow++;
                    int columsCount = fileInputDelimited.getColumnsCountOfCurrentRow();
                    String[] rowValues = new String[columsCount];
                    for (int i = 0; i < columsCount; i++) {
                        rowValues[i] = fileInputDelimited.get(i);
                    }
                    dataFromTable.add(rowValues);
                }
                fileInputDelimited.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataFromTable;
    }

    private void useCsvReader(File file, DelimitedFileConnection delimitedFileconnection, List<ModelElement> analysisElementList,
            List<Object[]> dataFromTable) {
        int limitValue = AnalysisExecutorHelper.getLimitValue(delimitedFileconnection);
        int headValue = AnalysisExecutorHelper.getHeadValue(delimitedFileconnection);
        CsvReader csvReader = null;
        try {
            csvReader = AnalysisExecutorHelper.createCsvReader(file, delimitedFileconnection);
            AnalysisExecutorHelper.initializeCsvReader(delimitedFileconnection, csvReader);

            for (int i = 0; i < headValue && csvReader.readRecord(); i++) {
                // do nothing, just ignore the header part
            }
            long currentRecord = 0;
            while (csvReader.readRecord()) {
                currentRecord = csvReader.getCurrentRecord();
                if (limitValue != -1 && currentRecord > limitValue - 1) {
                    break;
                }

                if (delimitedFileconnection.isFirstLineCaption() && currentRecord == 0) {
                    continue;
                }
                dataFromTable.add(csvReader.getValues());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (csvReader != null) {
                csvReader.close();
            }
        }
    }

}
