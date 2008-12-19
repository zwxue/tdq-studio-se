// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40 +0000 (ven., 29 sept. 2006) nrousseau $
 * 
 */
public class FilesUtils {

    public static void copyFolder(File source, File target, boolean emptyTargetBeforeCopy, final FileFilter sourceFolderFilter,
            final FileFilter sourceFileFilter, boolean copyFolder, IProgressMonitor... monitorWrap) throws IOException {
        IProgressMonitor monitor = null;
        if (monitorWrap != null && monitorWrap.length == 1) {
            monitor = monitorWrap[0];
        }

        if (!target.exists()) {
            target.mkdirs();
        }

        if (emptyTargetBeforeCopy) {
            emptyFolder(target);
        }

        FileFilter folderFilter = new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.isDirectory() && (sourceFolderFilter == null || sourceFolderFilter.accept(pathname));
            }

        };
        FileFilter fileFilter = new FileFilter() {

            public boolean accept(File pathname) {
                return !pathname.isDirectory() && (sourceFileFilter == null || sourceFileFilter.accept(pathname));
            }

        };

        for (File current : source.listFiles(folderFilter)) {
            if (monitor != null && monitor.isCanceled()) {
                throw new OperationCanceledException("Operation is canceled during copying folders or files.");
            }
            if (copyFolder) {
                File newFolder = new File(target, current.getName());
                newFolder.mkdir();
                copyFolder(current, newFolder, emptyTargetBeforeCopy, sourceFolderFilter, sourceFileFilter, copyFolder);
            } else {
                copyFolder(current, target, emptyTargetBeforeCopy, sourceFolderFilter, sourceFileFilter, copyFolder);
            }
        }

        for (File current : source.listFiles(fileFilter)) {
            if (monitor != null && monitor.isCanceled()) {
                throw new OperationCanceledException("");
            }
            File out = new File(target, current.getName());
            copyFile(current, out);
        }
    }

    private static void emptyFolder(File toEmpty) {
        final File[] listFiles = toEmpty.listFiles(getExcludeSystemFilesFilter());
        for (File current : listFiles) {
            if (current.isDirectory()) {
                emptyFolder(current);
            }
            current.delete();
        }
    }

    public static void copyFile(File source, File target) throws IOException {
        // Need to recopy the file in one of these cases:
        // 1. target doesn't exists (never copied)
        // 2. if the target exists, compare their sizes, once defferent, for the copy.
        // 2. target exists but source has been modified recently(not used right now)

        if (!target.exists() || source.length() != target.length()) {
            copyFile(new FileInputStream(source), target);
        }
    }

    public static void removeFile(File target) {
        if (target.exists() && target.isFile()) {
            target.delete();
        }
    }

    public static void copyFile(InputStream source, File target) throws IOException {
        FileOutputStream fos = null;
        try {
            if (!target.getParentFile().exists()) {
                target.getParentFile().mkdirs();
            }

            fos = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = source.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } finally {
            try {
                source.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }

    }

    public static void replaceInFile(String regex, String fileName, String replacement) throws IOException {
        InputStream in = new FileInputStream(fileName);
        StringBuffer buffer = new StringBuffer();
        try {
            InputStreamReader inR = new InputStreamReader(in);
            BufferedReader buf = new BufferedReader(inR);
            String line;
            while ((line = buf.readLine()) != null) {
                buffer.append(line.replaceAll(regex, replacement)).append("\n");
            }
        } finally {
            in.close();
        }

        OutputStream os = new FileOutputStream(fileName);
        os.write(buffer.toString().getBytes());
        os.close();
    }

    public static List<URL> getFilesFromFolder(Bundle bundle, String path, String extension) {
        // List<URL> toReturn = new ArrayList<URL>();
        //
        // Enumeration entryPaths = bundle.getEntryPaths(path);
        // for (Enumeration enumer = entryPaths; enumer.hasMoreElements();) {
        // String fileName = (String) enumer.nextElement();
        // if (fileName.endsWith(extension)) {
        // URL url = bundle.getEntry(fileName);
        // try {
        // toReturn.add(FileLocator.toFileURL(url));
        // } catch (IOException e) {
        // CommonExceptionHandler.process(e);
        // }
        // }
        // }
        // return toReturn;

        return getFilesFromFolder(bundle, path, extension, true, false);
    }

    public static List<URL> getFilesFromFolder(Bundle bundle, String path, String extension, boolean absoluteURL, boolean nested) {
        List<URL> toReturn = new ArrayList<URL>();

        Enumeration entryPaths = bundle.getEntryPaths(path);
        if (entryPaths == null) {
            return toReturn;
        }
        for (Enumeration enumer = entryPaths; enumer.hasMoreElements();) {
            String fileName = (String) enumer.nextElement();
            if (fileName.endsWith(extension)) {
                URL url = bundle.getEntry(fileName);
                if (absoluteURL) {
                    try {
                        toReturn.add(FileLocator.toFileURL(url));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    toReturn.add(url);
                }
            } else {
                if (nested) {
                    List<URL> subResult = getFilesFromFolder(bundle, fileName, extension, absoluteURL, nested);
                    toReturn.addAll(subResult);
                }
            }
        }
        return toReturn;
    }

    public static FileFilter getExcludeSystemFilesFilter() {
        FileFilter filter = new FileFilter() {

            public boolean accept(File pathname) {
                return !pathname.toString().endsWith(".svn") && !pathname.toString().endsWith(".dummy");
            }

        };
        return filter;
    }

    public static FileFilter getAcceptJARFilesFilter() {
        FileFilter filter = new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.toString().toLowerCase().endsWith(".jar") || pathname.toString().toLowerCase().endsWith(".zip");//$NON-NLS-1$
            }

        };
        return filter;
    }

    public static FileFilter getAcceptModuleFilesFilter() {
        FileFilter filter = new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.toString().toLowerCase().endsWith(".jar") || pathname.toString().toLowerCase().endsWith(".zip") || pathname.toString().toLowerCase().endsWith(".properties");//$NON-NLS-1$
            }

        };
        return filter;
    }

    public static String[] getAcceptJARFilesSuffix() {
        return new String[] { "*.jar;*.properties;*.zip;*.dll;*.so" };//$NON-NLS-1$
    }

    public static FileFilter getAcceptPMFilesFilter() {
        FileFilter filter = new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.toString().endsWith(".pm");
            }

        };
        return filter;
    }

    /**
     * Load in a list all lines of the given file.
     * 
     * @throws IOException
     */
    public static List<String> getContentLines(String filePath) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        List<String> lines = new ArrayList<String>();
        try {
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            in.close();
        }
        return lines;
    }

    /**
     * .
     * 
     * @param path
     * @param pathIsFilePath if true the given path has a filename at last segment so this segment is not processed
     * @throws IOException
     */
    public static void createFoldersIfNotExists(String path, boolean pathIsFilePath) throws IOException {
        Path completePath = new Path(path);
        IPath pathFolder = null;
        if (pathIsFilePath) {
            pathFolder = completePath.removeLastSegments(1);
        } else {
            pathFolder = completePath;
        }

        File folder = new File(pathFolder.toOSString());
        if (!folder.exists()) {

            int size = pathFolder.segmentCount();
            for (int i = 0; i < size; i++) {
                folder = new File(pathFolder.uptoSegment(i + 1).toOSString());
                if (!folder.exists()) {
                    folder.mkdir();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            // createFoldersIfNotExists("c:\\test\\test1/test2", false);
            // createFoldersIfNotExists("c:\\test10\\test11/test20/test.pl", true);
            unzip("d:/tFileOutputPDF.zip", "d:/temp");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * DOC amaumont Comment method "removeDirectory".
     * 
     * @param b
     */
    public static boolean removeFolder(String pathFolder, boolean recursiveRemove) {
        File folder = new File(pathFolder);
        if (folder.isDirectory()) {
            return removeFolder(folder, recursiveRemove);
        }
        return false;
    }

    /**
     * DOC amaumont Comment method "removeFolder".
     * 
     * @param current
     * @param removeRecursivly
     */
    public static boolean removeFolder(File folder, boolean removeRecursivly) {
        if (removeRecursivly) {
            for (File current : folder.listFiles()) {
                if (current.isDirectory()) {
                    removeFolder(current, true);
                } else {
                    current.delete();
                }
            }
        }
        return folder.delete();
    }

    public static void removeEmptyFolder(File folder) {
        if (!folder.isDirectory()) {
            return;
        }
        File[] children = folder.listFiles();
        if (children == null) {
            folder.delete();
        } else {
            for (File current : children) {
                removeEmptyFolder(current);
            }
        }
        children = folder.listFiles();
        if (children == null || children.length == 0) {
            folder.delete();
        }
    }

    public static String extractPathFolderFromFilePath(String filePath) {
        Path completePath = new Path(filePath);
        return completePath.removeLastSegments(1).toOSString();
    }

    /**
     * Unzip the component file to the user folder.
     * 
     * @param zipFile The component zip file
     * @param targetFolder The user folder
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void unzip(String zipFile, String targetFolder) throws Exception {
        Exception exception = null;
        ZipFile zip = new ZipFile(zipFile);
        byte[] buf = new byte[8192];

        try {
            Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zip.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement();

                File file = new File(targetFolder, entry.getName());

                if (entry.isDirectory()) {
                    if (!file.exists()) {
                        file.mkdir();
                    }
                } else {

                    InputStream zin = zip.getInputStream(entry);
                    OutputStream fout = new FileOutputStream(file);
                    // check if parent folder exists
                    File dir = file.getParentFile();
                    if (dir.isDirectory() && !dir.exists()) {
                        dir.mkdirs();
                    }

                    try {
                        while (true) {
                            int bytesRead = zin.read(buf);
                            if (bytesRead == -1) { // end of file
                                break;
                            }
                            fout.write(buf, 0, bytesRead);

                        }
                        fout.flush();
                    } catch (Exception e) {
                        exception = e;
                        // stop looping
                        return;
                    } finally {
                        zin.close();
                        fout.close();
                    }
                }
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            zip.close();

            if (exception != null) {
                // notify caller with exception
                throw exception;
            }
        }
    }

}
