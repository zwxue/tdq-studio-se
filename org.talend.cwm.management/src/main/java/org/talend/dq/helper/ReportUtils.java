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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;

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

    private ReportUtils() {
    }

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
                    IFile iFile = getLinkFile(file.getName());
                    iFile.createLink(file.getAbsoluteFile().toURI(), IResource.REPLACE, null);
                    repFileList.add(iFile);
                }
            }

            saveReportListFile(reportListFile, repListSave);

            folder.refreshLocal(IResource.DEPTH_INFINITE, null);
        }
        return repFileList.toArray(new IResource[repFileList.size()]);
    }

    /**
     * get the link file which link to the report generate doc file.
     * 
     * @param fileName report generate doc file name
     * @return
     */
    public static IFile getLinkFile(String fileName) {
        return ResourceManager.getRootProject().getFile("." + fileName); //$NON-NLS-1$
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
        File file = new File(ReportHelper.getOutputFolderNameDefault((IFolder) reportFile.getParent(), simpleName)
                + File.separator + REPORT_LIST);
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
     * remove the extension of full name to get the simple name of a report file.
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
     * get the report doc folder name from the report's preperty.
     * 
     * @param repProp
     * @return
     */
    public static String getSimpleName(Property repProp) {
        return repProp.getLabel() + "_" + repProp.getVersion(); //$NON-NLS-1$ 
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
        if (simpleName == null) {
            return;
        }
        IFolder reportFileFolder = ((IFolder) reportFile.getParent()).getFolder(PluginConstant.DOT_STRING + simpleName);
        if (reportFileFolder != null && reportFileFolder.exists()) {

            File repListFile = new File(ReportHelper.getOutputFolderNameDefault((IFolder) reportFile.getParent(), simpleName)
                    + File.separator + REPORT_LIST);//$NON-NLS-1$
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
        // for this map:key is main report name without vesion;value is a List about sub reports.
        if (mainSubRepMap == null || mainSubRepMap.isEmpty()) {
            mainSubRepMap = new HashMap<String, List<String>>();
            String s01ColumnSub = "s01_column_subreport";
            String b01ColumnSetBasicSubreport1 = "b01_column_set_basic_subreport1";
            String s02MatchSubSummary = "s02_match_sub_summary";
            String s03SubOverview = "s03_sub_overview";

            List<String> cloumnSubLs = new ArrayList<String>();
            cloumnSubLs.add(s01ColumnSub);
            mainSubRepMap.put("b01_column_basic", cloumnSubLs);
            mainSubRepMap.put("b02_column_evolution", cloumnSubLs);

            List<String> columnSetSubLs = new ArrayList<String>();
            columnSetSubLs.add(b01ColumnSetBasicSubreport1);
            mainSubRepMap.put("b01_column_set_basic", columnSetSubLs);
            mainSubRepMap.put("b02_column_set_evolution", columnSetSubLs);
            mainSubRepMap.put("b03_column_basic_dq_rules", columnSetSubLs);
            mainSubRepMap.put("b04_column_dq_rules_evolution", columnSetSubLs);
            mainSubRepMap.put("functionalDependencyBasic", columnSetSubLs);
            mainSubRepMap.put("functionalDependencyEvolution", columnSetSubLs);

            List<String> overviewSubLs = new ArrayList<String>();
            overviewSubLs.add(s03SubOverview);
            mainSubRepMap.put("b03_overview_basic", overviewSubLs);
            mainSubRepMap.put("b04_overview_evolution", overviewSubLs);

            List<String> matchSubLs = new ArrayList<String>();
            matchSubLs.add(s02MatchSubSummary);
            mainSubRepMap.put("b05_match_basic", matchSubLs);
            mainSubRepMap.put("b06_match_evolution", matchSubLs);
        }
        return mainSubRepMap;
    }

    /**
     * get the report generate doc folder.
     * 
     * @param reportFile report IFile
     * @return
     */
    public static IFolder getOutputFolder(IFile reportFile) {
        IFolder reportContainer = (IFolder) reportFile.getParent();
        String fileName = reportFile.getName();
        String simpleName = getSimpleName(fileName);
        if (simpleName == null) {
            log.error("The current report file name: " + reportFile.getFullPath() + " is a illegal name."); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
        return reportContainer.getFolder(PluginConstant.DOT_STRING + simpleName);
    }

    /**
     * get the report generate doc folder.
     * 
     * @param repNode the report repository node
     * @return
     */
    public static IFolder getOutputFolder(ReportRepNode repNode) {
        return getOutputFolder(RepositoryNodeHelper.getIFile(repNode));
    }

    /**
     * if the report's name changed, need to update the report folder name also.
     * 
     * @param oldFolderName
     * @param repItem
     */
    public static void checkAndUpdateRepFolderName4Rename(String oldFolderName, TDQReportItem repItem) {
        // new report folder name
        String newFolderName = ReportUtils.getSimpleName(repItem.getProperty());
        // if the report's name changed, update the report folder name also
        if (!oldFolderName.equals(newFolderName)) {
            IContainer repItemParent = PropertyHelper.getItemFile(repItem.getProperty()).getParent();
            File oldFolder = WorkspaceUtils.ifolderToFile(repItemParent.getFolder(Path
                    .fromPortableString(PluginConstant.DOT_STRING + oldFolderName)));
            File newFolder = WorkspaceUtils.ifolderToFile(repItemParent.getFolder(Path
                    .fromPortableString(PluginConstant.DOT_STRING + newFolderName)));
            try {
                // rename the folder
                oldFolder.renameTo(newFolder);
                // replace the path in the .report.list
                File file = new File(ReportHelper.getOutputFolderNameDefault((IFolder) repItemParent, newFolderName)
                        + File.separator + REPORT_LIST);
                if (file.exists() && file.isFile()) {
                    FilesUtils.replaceInFile(oldFolder.toString(), file.toString(), newFolder.toString());
                }
                // refresh the container
                repItemParent.refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (Exception e) {
                log.warn(e, e);
            }
        }
    }

    /**
     * get the link files which link to the Report Generated Doc File.
     * 
     * @param file Report file
     * @return
     */
    public static List<IFile> getRepDocLinkFiles(IFile file) {
        List<IFile> linkFiles = new ArrayList<IFile>();
        try {
            IResource[] reportListFiles = ReportUtils.getReportListFiles(file);
            for (IResource res : reportListFiles) {
                IFile linkFile = ResourceManager.getRoot().getFile(res.getFullPath());
                if (linkFile.exists() && linkFile.isLinked()) {
                    linkFiles.add(linkFile);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return linkFiles;
    }

    /**
     * get the link files which link to the Report Generated Doc File.
     * 
     * @param findAllRecycleBinNodes RepositoryNode list(can be any type)
     * @return
     */
    public static List<IFile> getRepGenDocLinkFiles(List<IRepositoryNode> findAllRecycleBinNodes) {
        List<IFile> linkFiles = new ArrayList<IFile>();
        for (IRepositoryNode iNode : findAllRecycleBinNodes) {
            IRepositoryViewObject object = iNode.getObject();
            if (object != null) {
                Property property = object.getProperty();
                if (property != null) {
                    Item item = property.getItem();
                    if (item != null && item instanceof TDQReportItem) {
                        linkFiles.addAll(ReportUtils.getRepDocLinkFiles(RepositoryNodeHelper.getIFile(iNode)));
                    }
                }
            }
        }
        return linkFiles;
    }

    /**
     * remove the link files which link to the Report Generated Doc File.
     * 
     * @param repDocLinkFiles
     */
    public static void removeRepDocLinkFiles(List<IFile> repDocLinkFiles) {
        try {
            for (IFile file : repDocLinkFiles) {
                if (file.isLinked()) {
                    file.delete(Boolean.TRUE, new NullProgressMonitor());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * delete the related output folder of report.
     * 
     * @param reportFile
     */
    public static ReturnCode deleteRepOutputFolder(IFile reportFile) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);
        IFolder currentRportFolder = ReportUtils.getOutputFolder(reportFile);
        if (currentRportFolder != null && currentRportFolder.exists()) {
            try {
                currentRportFolder.delete(true, new NullProgressMonitor());
            } catch (CoreException e) {
                log.error(e, e);
                rc.setOk(Boolean.FALSE);
                rc.setMessage(e.getMessage());
            }
        } else {
            rc.setOk(Boolean.FALSE);
        }
        return rc;
    }

    /**
     * get the IFile of the Report.
     * 
     * @param findAllRecycleBinNodes
     * @return
     */
    public static List<IFile> getReportFiles(List<IRepositoryNode> findAllRecycleBinNodes) {
        List<IFile> reportFiles = new ArrayList<IFile>();
        for (IRepositoryNode iNode : findAllRecycleBinNodes) {
            IRepositoryViewObject object = iNode.getObject();
            if (object != null) {
                Property property = object.getProperty();
                if (property != null) {
                    Item item = property.getItem();
                    if (item != null && item instanceof TDQReportItem) {
                        reportFiles.add(RepositoryNodeHelper.getIFile(iNode));
                    }
                }
            }
        }
        return reportFiles;
    }

    /**
     * delete the related output folder of reports.
     * 
     * @param repFiles
     */
    public static void deleteRepOutputFolders(List<IFile> repFiles) {
        for (IFile repFile : repFiles) {
            deleteRepOutputFolder(repFile);
        }
    }

    /**
     * update the file .report.list to deal with the report generated doc folder's movement.
     * 
     * @param outputFolder source folder, the original report generated doc folder
     * @param targetFolder target folder, the new folder which source folder moved into
     */
    public static void updateReportListFile(IFolder outputFolder, IFolder targetFolder) {
        try {
            File oldFolder = WorkspaceUtils.ifolderToFile(outputFolder);
            File newFolder = WorkspaceUtils.ifolderToFile(targetFolder.getFolder(outputFolder.getName()));
            File file = new File(newFolder.getAbsolutePath() + File.separator + ReportUtils.REPORT_LIST);
            if (file.exists() && file.isFile()) {
                String str1 = oldFolder.toString();
                String str2 = newFolder.toString();
                str1 = StringUtils.replace(str1, "\\", "\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
                str2 = StringUtils.replace(str2, "\\", "\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
                FilesUtils.replaceInFile(str1, file.toString(), str2);
            }
        } catch (Exception e) {
            log.warn(e, e);
        }
    }

    /**
     * DOC xqliu Comment method "copyAndUpdateRepGenDocFileInfo".
     * 
     * @param newFolder
     * @param tempFolder
     * @param subFolderName
     */
    public static void copyAndUpdateRepGenDocFileInfo(IFolder newFolder, File tempFolder, String subFolderName) {
        File srcFolder = new File(tempFolder.getAbsolutePath() + File.separator + subFolderName);
        File tarFolder = WorkspaceUtils.ifolderToFile(newFolder);
        // move folder
        moveHiddenFolders(srcFolder, tarFolder);
        // update info
        updateReportListFileInfo(srcFolder, tarFolder, tempFolder.getName());
        // delete temp folder
        FilesUtils.deleteFile(tempFolder, Boolean.TRUE);
    }

    /**
     * DOC xqliu Comment method "updateReportListFileInfo".
     * 
     * @param srcFolder
     * @param tarFolder
     */
    public static void updateReportListFileInfo(File srcFolder, File tarFolder, String tempFolderName) {
        if (srcFolder == null || tarFolder == null) {
            return;
        }
        File[] listFiles = tarFolder.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                if (file.getName().startsWith(".")) { //$NON-NLS-1$
                    IFolder outputFolder = WorkspaceUtils.fileToIFolder(new File(getOriginalOutoutFolderPath(srcFolder, file,
                            tempFolderName)));
                    IFolder tFolder = WorkspaceUtils.fileToIFolder(file);
                    IFolder targetFolder = tFolder == null ? null : (IFolder) tFolder.getParent();
                    if (outputFolder != null && targetFolder != null) {
                        ReportUtils.updateReportListFile(outputFolder, targetFolder);
                    }
                } else {
                    updateReportListFileInfo(getSourceFolder(file, srcFolder, tarFolder), file, tempFolderName);
                }
            }
        }
    }

    /**
     * DOC xqliu Comment method "getOriginalOutoutFolderPath".
     * 
     * @param srcFolder
     * @param file
     * @param tempFolderName
     * @return
     */
    public static String getOriginalOutoutFolderPath(File srcFolder, File file, String tempFolderName) {
        String path = srcFolder.getAbsolutePath() + File.separator + file.getName();
        int indexOf = path.indexOf(tempFolderName);
        if (indexOf > 0) {
            path = path.substring(0, indexOf) + path.substring(indexOf + tempFolderName.length() + 1, path.length());
        }
        return path;
    }

    /**
     * DOC xqliu Comment method "getSourceFolder".
     * 
     * @param file
     * @param srcFolder
     * @param tarFolder
     * @return
     */
    public static File getSourceFolder(File file, File srcFolder, File tarFolder) {
        File tarFile = null;
        String absolutePath = file.getAbsolutePath();
        String absolutePath2 = tarFolder.getAbsolutePath();
        if (absolutePath.startsWith(absolutePath2)) {
            String absolutePath3 = srcFolder.getAbsolutePath();
            tarFile = new File(absolutePath3 + absolutePath.subSequence(absolutePath2.length(), absolutePath.length()));
        }
        return tarFile;
    }

    /**
     * DOC xqliu Comment method "moveHiddenFolders".
     * 
     * @param srcFolder
     * @param tarFolder
     */
    public static void moveHiddenFolders(File srcFolder, File tarFolder) {
        if (srcFolder == null || tarFolder == null) {
            return;
        }
        File[] listFiles = srcFolder.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                if (file.getName().startsWith(".")) { //$NON-NLS-1$
                    FilesUtils.copyDirectory(file, tarFolder);
                } else {
                    moveHiddenFolders(file, getTargetFile(file, srcFolder, tarFolder));
                }
            }
        }
    }

    /**
     * DOC xqliu Comment method "getTargetFile".
     * 
     * @param file
     * @param srcFolder
     * @param tarFolder
     * @return
     */
    public static File getTargetFile(File file, File srcFolder, File tarFolder) {
        File tarFile = null;
        String absolutePath = file.getAbsolutePath();
        String absolutePath2 = srcFolder.getAbsolutePath();
        if (absolutePath.startsWith(absolutePath2)) {
            String absolutePath3 = tarFolder.getAbsolutePath();
            tarFile = new File(absolutePath3 + absolutePath.subSequence(absolutePath2.length(), absolutePath.length()));
        }
        return tarFile;
    }
}
