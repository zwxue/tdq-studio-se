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
package org.talend.dataprofiler.core.recycle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.StringUtils;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;

/**
 * @author qiongli handle logical delete and restore
 */
public class LogicalDeleteFileHandle {

    private static String filePath = ResourceManager.getLibrariesFolder().getLocation().toOSString() + "/.logicalDelete.txt";

    public static String fileType = "File:";

    public static String folderType = "Folder:";

    private static Logger log = Logger.getLogger(LogicalDeleteFileHandle.class);

    private static List<String[]> delLs = null;

    private static String slashStr = "\\";

    /**
     * Replace a path String to "" in logicalDelete.txt.
     * 
     * @param regex
     * @param fileName
     * @param replacement
     * @throws IOExceptio
     */
    public static void replaceInFile(String oldString, String newString) {

        InputStream in = null;
        InputStreamReader inRead = null;
        try {
            in = new FileInputStream(filePath);
            StringBuffer buffer = new StringBuffer();
            inRead = new InputStreamReader(in);
            BufferedReader buf = new BufferedReader(inRead);
            String line;
            String newLine;
            while ((line = buf.readLine()) != null) {
                if (!line.equals(oldString)) {
                    newLine = line;
                } else {
                    newLine = line.replace(oldString, newString);
                    for (int i = 0; i < delLs.size(); i++) {
                        String[] es = (String[]) delLs.get(i);
                        if ((es[0] + ":" + es[1]).equals(oldString)) {
                            delLs.remove(es);
                            break;
                        }
                    }
                }
                if (!newLine.equals(PluginConstant.EMPTY_STRING)) {
                    buffer.append(newLine).append("\r\n"); //$NON-NLS-1$		
                }
            }
            OutputStream os = new FileOutputStream(filePath);
            os.write(buffer.toString().getBytes());
            os.close();
        } catch (Exception exc) {
            log.error(exc, exc);
        } finally {
            try {
                in.close();
                inRead.close();
            } catch (IOException e) {
                log.error(e, e);
            }
        }
    }

    /**
     * DOC bZhou Comment method "deleteElement".
     * 
     * @param resource
     */
    public static void deleteElement(IResource resource) {

        String prefix;

        if (resource.getType() == IResource.FOLDER) {
            prefix = folderType;
        } else {
            prefix = fileType;
        }

        replaceInFile(prefix + resource.getFullPath().toOSString(), "");
    }

    /**
     * 
     * Save logical delete path to TXT file.
     * 
     * @param type
     * @param path
     */
    public static void saveElement(String type, String path) {
        try {
            File f = new Path(filePath).toFile();
            if (!f.exists()) {
                f = new File(filePath);
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(f.getPath(), true);
            PrintWriter out = new PrintWriter(fw);
            out.println(type + path);
            fw.close();
            out.close();
            String[] es = { type.replaceAll(":", PluginConstant.EMPTY_STRING), path };
            if (delLs == null) {
                delLs = getDelLs();
            }
            delLs.add(es);
        } catch (Exception exc) {
            log.error(exc, exc);
        }
    }

    /**
     * 
     * Read all logical delete elements from the TXT file.
     * 
     * @return
     */
    public static List<String[]> readFileByLine() {
        List<String[]> list = new ArrayList<String[]>();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            createTxtFile();
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line = null;
            String[] es = null;
            while ((line = br.readLine()) != null) {
                es = StringUtils.split(line, ':');
                if (es.length < 2) {
                    continue;
                }
                list.add(es);
            }
        } catch (Exception exc) {
            log.error(exc, exc);
        } finally {
            try {
                fr.close();
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.error(e, e);
            }
        }
        return list;
    }

    /**
     * 
     * Remove the path of file's parents(until to the top) in the TXT file.
     * 
     * @param ifile
     * @throws IOException
     */

    public static void removeAllParent(IFile ifile) throws IOException {
        IFolder parent = (IFolder) ifile.getParent();
        while (!ResourceService.isReadOnlyFolder(parent)) {
            replaceInFile(folderType + parent.getFullPath().toOSString(), PluginConstant.EMPTY_STRING);
            parent = (IFolder) parent.getParent();
        }
    }

    /**
     * 
     * Get all Children from TXT by 'folderPath',contain file or subFoldern.
     * 
     * @param folderPath
     * @return
     */
    public static List<Object> getChildFromTXT(String folderPath) {
        List<Object> ls = new ArrayList<Object>();
        try {
            IPath iPath = null;
            IFile file = null;
            IFolder folder = null;
            HashSet<String> set = new HashSet<String>();
            DQRecycleBinNode rbn = null;

            for (String[] es : delLs) {
                if (es.length < 2 || folderType.equals(es[0] + ":") && folderPath.equals(es[1]))
                    continue;
                if (es[1].startsWith(folderPath)) {
                    iPath = new Path(es[1]);
                    if (es[0].equals("File")) {
                        file = ResourcesPlugin.getWorkspace().getRoot().getFile(iPath);
                        if (file.getParent().getFullPath().toOSString().equals(folderPath)) {
                            rbn = new DQRecycleBinNode();
                            rbn.setObject(file);
                            ls.add(rbn);
                            IFolder rootFolder = (IFolder) file.getParent();
                            // if its brother is empty folder,add its brother
                            for (IResource res : rootFolder.members()) {
                                if (res.getType() == IResource.FOLDER && ((IFolder) res).members().length == 0) {
                                    set.add(folderPath + slashStr + ((IFolder) res).getName());
                                }
                            }
                        } else {
                            // add the subFoler
                            addToSet(es[1], folderPath, set);
                        }
                    }
                }

            }
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                rbn = new DQRecycleBinNode();
                folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(iterator.next()));
                rbn.setObject(folder);
                ls.add(rbn);
            }
        } catch (Exception exc) {
            log.error(exc, exc);
        }
        return ls;
    }

    /**
     * Judge whether the TXT file exit.if not,create it.
     * 
     * @throws IOException
     */
    private static void createTxtFile() throws IOException {
        File f = new Path(filePath).toFile();
        if (!f.exists()) {
            f = new File(filePath);
            f.createNewFile();
        }
    }

    /**
     * 
     * Make sure the same subFoleder only appear once.
     * 
     * @param fullPath
     * @param folderPath
     * @param hashSet
     */
    private static void addToSet(String fullPath, String folderPath, HashSet<String> hashSet) throws CoreException {
        String subFolderName = fullPath.replace(folderPath, PluginConstant.EMPTY_STRING);
        String[] temp = StringUtils.split(subFolderName, '\\');
        if (temp != null && temp.length > 0) {
            subFolderName = temp[0];
            hashSet.add(folderPath + slashStr + subFolderName);
            // MOD qiongli 2010-8-6 bug 14697.if its brother is empty folder,add its brother
            if (isStartWithDelFolder(folderPath)) {
                IPath iPath = new Path(folderPath);
                IFolder rootFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(iPath);
                for (IResource res : rootFolder.members()) {
                    if (res.getType() == IResource.FOLDER && ((IFolder) res).members().length == 0) {
                        hashSet.add(folderPath + slashStr + ((IFolder) res).getName());
                    }
                }
            }
        }

    }

    /**
     * 
     * Logical delete file.set the property of isDelete to 'true'.save the file fullPath to the TXT.
     * 
     * @param ifile
     * @return
     * @throws Exception
     */
    public static ReturnCode deleteLogical(IFile ifile) throws Exception {
        ReturnCode rc = new ReturnCode();

        IFile propFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
                ifile.getFullPath().removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION));
        if (propFile.exists()) {
            Property property = PropertyHelper.getProperty(propFile);
            ItemState itemState = property.getItem().getState();
            if (!itemState.isDeleted()) {
                itemState.setDeleted(true);
                Resource propertyResource = property.eResource();
                rc.setOk(EMFSharedResources.getInstance().saveResource(propertyResource));
            }
            saveElement(LogicalDeleteFileHandle.fileType, ifile.getFullPath().toOSString());
        }

        // svn commit
        ProxyRepositoryManager.getInstance().save();
        // finish
        ifile.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
        rc.setMessage(DefaultMessagesImpl.getString("LogicalDeleteFileHandle.logicalDelSuccess"));

        return rc;
    }

    /**
     * 
     * Get the Logical delete elements.
     * 
     * @return
     */
    public static List<String[]> getDelLs() {
        if (delLs == null)
            delLs = readFileByLine();
        return delLs;
    }

    /**
     * 
     * judge current folder path whethere start with deleted folder path which in delLs.
     * 
     * @param path
     * @return
     */
    public static boolean isStartWithDelFolder(String path) {
        for (String[] es : delLs) {
            if (es[0].equals("Folder") && path.startsWith(es[1])) {
                return true;
            }
        }
        return false;
    }

}
