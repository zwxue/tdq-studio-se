// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;

import com.talend.csv.CSVReader;
import com.talend.csv.CSVWriter;

/**
 * DOC bZhou class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z bzhou $
 * 
 */
public final class FileUtils {

    private static final char TEXT_QUAL = '\"';

    private static final char ESCAPE_CHAR = '\\';

    private static final char CURRENT_SEPARATOR = '\t';

    private static final char SEPARATOR = ',';

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

    /**
     * DOC bZhou Comment method "getFilesByExtension".
     * 
     * @param parentFolder
     * @param extensions
     * @return
     */
    public static List<File> getFilesByExtension(File parentFolder, final String... extensions) {
        ArrayList<File> fileList = new ArrayList<File>();
        getAllFilesFromFolder(parentFolder, fileList, new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (extensions != null) {
                    for (String ext : extensions) {
                        if (name.endsWith(ext)) {
                            return true;
                        }
                    }
                    return false;
                }

                return true;
            }
        });

        return fileList;
    }

    /**
     * DOC sgandon Comment method "getAllFilesFromFolder".
     * 
     * @param sampleFolder
     * @param arrayList
     * @param filenameFilter
     */
    public static void getAllFilesFromFolder(File sampleFolder, ArrayList<File> fileList, FilenameFilter filenameFilter) {
        File[] folderFiles = sampleFolder.listFiles(filenameFilter);
        Collections.addAll(fileList, folderFiles);
        File[] allFolders = sampleFolder.listFiles(new FileFilter() {

            public boolean accept(File arg0) {
                return arg0.isDirectory();
            }
        });
        for (File folder : allFolders) {
            getAllFilesFromFolder(folder, fileList, filenameFilter);
        }
    }

    /**
     * create a CSVWriter with necessory setting.
     * 
     * @param reportListFile
     * @return
     * @throws FileNotFoundException
     */
    public static CSVWriter createCSVWriter(final File reportListFile) throws FileNotFoundException {
        CSVWriter out = new CSVWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportListFile),
                Charset.defaultCharset())));
        out.setSeparator(CURRENT_SEPARATOR);
        out.setEscapeChar(ESCAPE_CHAR);
        out.setQuoteChar(TEXT_QUAL);
        // out.setForceQualifier(USE_TEXT_QUAL);
        return out;
    }

    public static CSVReader createCsvReader(File file, DelimitedFileConnection delimitedFileconnection)
            throws UnsupportedEncodingException, FileNotFoundException {
        String separator = JavaSqlFactory.getFieldSeparatorValue(delimitedFileconnection);
        String encoding = JavaSqlFactory.getEncoding(delimitedFileconnection);
        return new CSVReader(new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file),
                encoding == null ? "UTF-8" : encoding)), ParameterUtil //$NON-NLS-1$
                .trimParameter(separator).charAt(0));
    }

    public static CSVReader createCSVReader(File file) throws UnsupportedEncodingException, FileNotFoundException {
        CSVReader csvReader = new CSVReader(new FileReader(file), CURRENT_SEPARATOR);
        csvReader.setQuoteChar(TEXT_QUAL);
        csvReader.setEscapeChar(ESCAPE_CHAR);
        return csvReader;
    }

    public static boolean isCSV(String fileExtName) {
        return "csv".equalsIgnoreCase(fileExtName); //$NON-NLS-1$
    }

    /**
     * 
     * DOC qiongli Comment method "initializeCsvReader".
     * 
     * @param csvReader
     * @param connection
     */
    public static void initializeCsvReader(DelimitedFileConnection delimitedFileconnection, CSVReader csvReader) {
        String rowSep = JavaSqlFactory.getRowSeparatorValue(delimitedFileconnection);
        if (rowSep != null && !rowSep.equals("\"\\n\"") && !rowSep.equals("\"\\r\"")) { //$NON-NLS-1$ //$NON-NLS-2$
            csvReader.setSeparator(ParameterUtil.trimParameter(rowSep).charAt(0));
        }

        csvReader.setSkipEmptyRecords(true);
        String textEnclosure = delimitedFileconnection.getTextEnclosure();
        if (textEnclosure != null && textEnclosure.length() > 0) {
            csvReader.setQuoteChar(ParameterUtil.trimParameter(textEnclosure).charAt(0));
        } else {
            csvReader.setQuoteChar('z');
        }
        String escapeChar = delimitedFileconnection.getEscapeChar();
        if (escapeChar == null || escapeChar.equals("\"\\\\\"") || escapeChar.equals("\"\"")) { //$NON-NLS-1$ //$NON-NLS-2$
            csvReader.setEscapeChar(ESCAPE_CHAR);
        }

    }
}
