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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.talend.dataquality.helpers.ReportHelper;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class ReportUtils {
    
    private ReportUtils() {
    }

    public static final String REPORT_LIST = ".report.list";

    public static final char CURRENT_SEPARATOR = '\t';

    public static final boolean USE_TEXT_QUAL = true;

    public static final char TEXT_QUAL = '"';

    public static final int ESCAPE_MODE_BACKSLASH = CsvReader.ESCAPE_MODE_BACKSLASH;

    /**
     * get report files list and rebuild the .report.list file.
     * 
     * @param reportFile
     * @return
     * @throws Exception
     */
    public static IResource[] getReportListFiles(IFile reportFile) throws Exception {
        File reportListFile = getReportListFile(reportFile);
        if (reportListFile == null || !reportListFile.exists()) {
            return null;
        }

        IFolder folder = ReportHelper.getOutputFolder(reportFile);

        List<IResource> repFileList = new ArrayList<IResource>();

        if (folder != null) {
            List<ReportListParameters> repList = getReportListParameters(reportListFile);
            List<ReportListParameters> repListSave = new ArrayList<ReportListParameters>();

            for (ReportListParameters rep : repList) {
                File file = new File(rep.path);
                if (file.exists() && !file.getName().equals(REPORT_LIST)) {
                    repListSave.add(rep);
                    IFile iFile = folder.getFile(file.getName());
                    iFile.createLink(file.getAbsoluteFile().toURI(), IResource.REPLACE, null);
                    repFileList.add(iFile);
                }
            }

            saveReportListFile(reportListFile, repListSave);
        }
        return repFileList.toArray(new IResource[repFileList.size()]);
    }

    /**
     * DOC xqliu Comment method "recordReportFiles".
     * 
     * @param reportFile
     * @param fileName
     * @param filePath
     * @param createTime
     * @return
     * @throws Exception
     */
    public static boolean recordReportFiles(IFile reportFile, String fileName, String filePath, long createTime) throws Exception {
        File reportListFile = getReportListFile(reportFile);
        if (reportListFile == null || !reportListFile.exists()) {
            return false;
        }
        // get report list from file
        List<ReportListParameters> repList = getReportListParameters(reportListFile);
        // add new report file record
        repList.add(buildRepListParams(fileName, filePath, String.valueOf(createTime)));
        // save report list
        saveReportListFile(reportListFile, repList);

        return true;
    }

    /**
     * DOC xqliu Comment method "saveReportListFile".
     * 
     * @param reportListFile
     * @param repList
     * @throws IOException
     */
    private static void saveReportListFile(File reportListFile, List<ReportListParameters> repList) throws IOException {
        CsvWriter out = new CsvWriter(new FileOutputStream(reportListFile), CURRENT_SEPARATOR, Charset.defaultCharset());
        out.setEscapeMode(ESCAPE_MODE_BACKSLASH);
        out.setTextQualifier(TEXT_QUAL);
        out.setForceQualifier(USE_TEXT_QUAL);

        ReportListEnum[] values = ReportListEnum.values();
        String[] temp = new String[values.length];

        for (int i = 0; i < repList.size() + 1; i++) {
            if (i == 0) {
                temp[0] = ReportListEnum.Name.getLiteral();
                temp[1] = ReportListEnum.Path.getLiteral();
                temp[2] = ReportListEnum.CreateTime.getLiteral();
            } else {
                temp[0] = repList.get(i - 1).name;
                temp[1] = repList.get(i - 1).path;
                temp[2] = repList.get(i - 1).createTime;
            }

            out.writeRecord(temp);
        }

        out.flush();
        out.close();
    }

    /**
     * DOC xqliu Comment method "getReportListParameters".
     * 
     * @param reportListFile
     * @return
     * @throws IOException
     */
    private static List<ReportListParameters> getReportListParameters(File reportListFile) throws IOException {
        List<ReportListParameters> repList = new ArrayList<ReportListParameters>();

        if (reportListFile != null && reportListFile.exists()) {
            CsvReader reader = new CsvReader(new FileReader(reportListFile), CURRENT_SEPARATOR);
            reader.setEscapeMode(ESCAPE_MODE_BACKSLASH);
            reader.setTextQualifier(TEXT_QUAL);
            reader.setUseTextQualifier(USE_TEXT_QUAL);
            reader.readHeaders();
            while (reader.readRecord()) {
                repList.add(buildRepListParams(reader.get(ReportListEnum.Name.getLiteral()), reader.get(ReportListEnum.Path
                        .getLiteral()), reader.get(ReportListEnum.CreateTime.getLiteral())));
            }
            reader.close();
        }
        return repList;
    }

    /**
     * DOC xqliu Comment method "getReportListFile".
     * 
     * @param reportFile
     * @return
     * @throws IOException
     */
    public static File getReportListFile(IFile reportFile) throws IOException {
        String reportFileName = reportFile.getName();
        int indexOf = reportFileName.indexOf(".");
        String simpleName = "";
        if (indexOf != -1) {
            simpleName = reportFileName.substring(0, indexOf);
        } else {
            return null;
        }
        File file = new File(ReportHelper.getOutputFolderNameDefault((IFolder) reportFile.getParent(), simpleName) + "/"
                + REPORT_LIST);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                if (!parentFile.exists()) {
                    if (parentFile.mkdirs()) {
                        file.createNewFile();
                    }
                } else {
                    file.createNewFile();
                }
            }
        }
        return file;
    }

    /**
     * DOC xqliu Comment method "buildRepListParams".
     * 
     * @param name
     * @param path
     * @param createTime
     * @return
     */
    private static ReportListParameters buildRepListParams(String name, String path, String createTime) {
        ReportListParameters repListParameters = new ReportUtils().new ReportListParameters();
        repListParameters.name = name;
        repListParameters.path = path;
        repListParameters.createTime = createTime;
        return repListParameters;
    }

    /**
     * DOC xqliu Comment method "initRepListFile".
     * 
     * @param reportFile
     * @throws Exception
     * @throws
     */
    public static void initRepListFile(IFile reportFile) throws Exception {
        String reportFileName = reportFile.getName();
        int indexOf = reportFileName.indexOf(".");
        String simpleName = "";
        if (indexOf != -1) {
            simpleName = reportFileName.substring(0, indexOf);
        } else {
            return;
        }
        IFolder reportFileFolder = ((IFolder) reportFile.getParent()).getFolder("." + simpleName);
        if (reportFileFolder != null && reportFileFolder.exists()) {

            File repListFile = new File(ReportHelper.getOutputFolderNameDefault((IFolder) reportFile.getParent(), simpleName)
                    + "/" + REPORT_LIST);
            List<ReportListParameters> repList = new ArrayList<ReportListParameters>();

            IResource[] members = reportFileFolder.members();

            for (IResource res : members) {
                if (res.getType() == IResource.FILE) {
                    IFile repFile = (IFile) res;
                    repList.add(buildRepListParams(repFile.getName(), repFile.getRawLocation().toOSString(), String
                            .valueOf(repFile.getModificationStamp())));
                }
            }

            saveReportListFile(repListFile, repList);
        }
    }
    /**
     * DOC xqliu ReportUtils class global comment. Detailled comment
     */
    private class ReportListParameters {

        String name;

        String path;

        String createTime;

        public ReportListParameters() {
            name = ""; //$NON-NLS-1$
            path = ""; //$NON-NLS-1$
            createTime = ""; //$NON-NLS-1$
        }
    }
}
