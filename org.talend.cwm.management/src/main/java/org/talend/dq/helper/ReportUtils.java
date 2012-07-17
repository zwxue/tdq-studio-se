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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.helpers.ReportHelper;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class ReportUtils {

    private static Logger log = Logger.getLogger(ReportUtils.class);

    public static final String REPORT_LIST = ".report.list";//$NON-NLS-1$

    public static final char CURRENT_SEPARATOR = '\t';//$NON-NLS-1$

    public static final boolean USE_TEXT_QUAL = true;

    public static final char TEXT_QUAL = '"';//$NON-NLS-1$

    public static final int ESCAPE_MODE_BACKSLASH = CsvReader.ESCAPE_MODE_BACKSLASH;

    private static Map<String, List<String>> mainSubRepMap;

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

        IFolder folder = getOutputFolder(reportFile);

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
                repList.add(buildRepListParams(reader.get(ReportListEnum.Name.getLiteral()),
                        reader.get(ReportListEnum.Path.getLiteral()), reader.get(ReportListEnum.CreateTime.getLiteral())));
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
        String simpleName = getSimpleName(reportFileName);
        File file = new File(ReportHelper.getOutputFolderNameDefault((IFolder) reportFile.getParent(), simpleName) + "/"//$NON-NLS-1$
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
	 * remove the extension of full name to get the simple name of a report file
	 * 
	 * @param reportFileName
	 * @return
	 */
	public static String getSimpleName(String reportFileName) {
		int indexOf = reportFileName.lastIndexOf(PluginConstant.DOT_STRING);
        String simpleName = PluginConstant.EMPTY_STRING;
        if (indexOf != -1) {
            simpleName = reportFileName.substring(0, indexOf);
        } else {
            return null;
        }
		return simpleName;
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
        String simpleName = getSimpleName(reportFileName);
        if(simpleName == null){
            return;
        }
        IFolder reportFileFolder = ((IFolder) reportFile.getParent()).getFolder(PluginConstant.DOT_STRING + simpleName);
        if (reportFileFolder != null && reportFileFolder.exists()) {

            File repListFile = new File(ReportHelper.getOutputFolderNameDefault((IFolder) reportFile.getParent(), simpleName)
                    + "/" + REPORT_LIST);//$NON-NLS-1$
            List<ReportListParameters> repList = new ArrayList<ReportListParameters>();

            IResource[] members = reportFileFolder.members();

            for (IResource res : members) {
                if (res.getType() == IResource.FILE) {
                    IFile repFile = (IFile) res;
                    repList.add(buildRepListParams(repFile.getName(), repFile.getRawLocation().toOSString(),
                            String.valueOf(repFile.getModificationStamp())));
                }
            }

            saveReportListFile(repListFile, repList);
        }
    }

    /**
     * DOC xqliu ReportUtils class global comment. Detailled comment
     * 
     * // FIXME use a static inner class instead
     */
    private class ReportListParameters {

        String name;

        String path;

        String createTime;

        public ReportListParameters() {
            name = PluginConstant.EMPTY_STRING; //$NON-NLS-1$
            path = PluginConstant.EMPTY_STRING; //$NON-NLS-1$
            createTime = PluginConstant.EMPTY_STRING; //$NON-NLS-1$
        }
    }

    /**
     * 
     * set the relationship of main-sub report.
     * 
     * @return
     */
    public static Map<String, List<String>> getMainSubRepMap() {
        // for this map:key is main report name without version;value is a List about sub reports.
        if (mainSubRepMap == null || mainSubRepMap.isEmpty()) {
            mainSubRepMap = new HashMap<String, List<String>>();
            String b01ColumnSetBasicSubreport1 = "b01_column_set_basic_subreport1";
            List<String> columnSetSubLs = new ArrayList<String>();
            columnSetSubLs.add(b01ColumnSetBasicSubreport1);
            mainSubRepMap.put("b01_column_set_basic", columnSetSubLs);
            mainSubRepMap.put("b02_column_set_evolution", columnSetSubLs);
        }
        return mainSubRepMap;
    }

    /**
     * DOC xqliu Comment method "getOutputFolder".
     * 
     * @param reportFile
     * @return
     */
    public static IFolder getOutputFolder(IFile reportFile) {
        IFolder reportContainer = (IFolder) reportFile.getParent();
        String fileName = reportFile.getName();
        String simpleName = getSimpleName(fileName);
        if(simpleName == null){
            log.error("The current report file name: " + reportFile.getFullPath() + " is a illegal name."); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
        return reportContainer.getFolder(PluginConstant.DOT_STRING + simpleName);
    }
}
