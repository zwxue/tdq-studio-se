// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.pattern;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.dq.indicators.UDIndicatorWriter;

import com.csvreader.CsvReader;

/**
 * DOC zqin class global comment. Detailled comment
 */
public final class ImportFactory {

    protected static Logger log = Logger.getLogger(ImportFactory.class);

    public static final boolean USE_TEXT_QUAL = true;

    public static final char TEXT_QUAL = '"';

    public static final int ESCAPE_MODE_BACKSLASH = CsvReader.ESCAPE_MODE_BACKSLASH;

    public static final char CURRENT_SEPARATOR = '\t';

    private ImportFactory() {

    }

    public static void importToStucture(File importFile, IFolder selectionFolder, ExpressionType type, boolean skip,
            boolean rename) {

        Set<String> names = PatternUtilities.getAllPatternNames(selectionFolder);

        String fileExtName = getFileExtName(importFile);

        if ("csv".equalsIgnoreCase(fileExtName)) { //$NON-NLS-1$

            try {

                CsvReader reader = new CsvReader(new FileReader(importFile), CURRENT_SEPARATOR);
                reader.setEscapeMode(ESCAPE_MODE_BACKSLASH);
                reader.setTextQualifier(TEXT_QUAL);
                reader.setUseTextQualifier(USE_TEXT_QUAL);

                reader.readHeaders();

                while (reader.readRecord()) {
                    String name = reader.get(PatternToExcelEnum.Label.getLiteral());

                    if (names.contains(name)) {
                        if (skip) {
                            continue;
                        }
                        if (rename) {
                            name = name + "(" + new Date() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }

                    PatternParameters patternParameters = new ImportFactory().new PatternParameters();
                    patternParameters.name = name;
                    patternParameters.auther = reader.get(PatternToExcelEnum.Author.getLiteral());
                    patternParameters.description = reader.get(PatternToExcelEnum.Description.getLiteral());
                    patternParameters.purpose = reader.get(PatternToExcelEnum.Purpose.getLiteral());
                    patternParameters.relativePath = reader.get(PatternToExcelEnum.RelativePath.getLiteral());

                    for (PatternLanguageType languagetype : PatternLanguageType.values()) {
                        String cellStr = reader.get(languagetype.getExcelEnum().getLiteral());
                        if (cellStr != null && !cellStr.equals("")) { //$NON-NLS-1$
                            patternParameters.regex.put(languagetype.getLiteral(), cellStr);
                        }
                    }

                    createAndStorePattern(patternParameters, selectionFolder, type);

                    names.add(name);
                }

                reader.close();

                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        MessageDialog.openInformation(null, "Information", "Patterns imported in the \"Patterns\" folder");
                    }
                });
            } catch (Exception e) {
                log.error(e, e);
            }
        }

        if ("xls".equalsIgnoreCase(fileExtName)) { //$NON-NLS-1$
            Map<Integer, PatternLanguageType> expressionMap = new HashMap<Integer, PatternLanguageType>();
            try {
                WorkbookSettings settings = new WorkbookSettings();
                settings.setEncoding("UTF-8"); //$NON-NLS-1$
                Workbook rwb = Workbook.getWorkbook(importFile, settings);
                Sheet[] sheets = rwb.getSheets();
                for (Sheet sheet : sheets) {
                    Cell[] headerRow = sheet.getRow(0);

                    for (Cell cell : headerRow) {
                        for (PatternLanguageType languageType : PatternLanguageType.values()) {
                            if (cell.getContents().equals(languageType.getExcelEnum().getLiteral())) {
                                expressionMap.put(cell.getColumn(), languageType);
                            }
                        }
                    }

                    for (int i = 1; i < sheet.getRows(); i++) {
                        Cell[] row = sheet.getRow(i);
                        Cell cell = row[0];
                        if (CellType.LABEL.equals(cell.getType())) {
                            String contents = cell.getContents();
                            if (names.contains(contents)) {
                                if (skip) {
                                    continue;
                                }
                                if (rename) {
                                    contents = contents + "(" + new Date() + ")";
                                }
                            }

                            PatternParameters patternParameters = new ImportFactory().new PatternParameters();

                            patternParameters.name = contents;
                            patternParameters.auther = row[6].getContents();
                            patternParameters.description = row[2].getContents();
                            patternParameters.purpose = row[1].getContents();
                            patternParameters.status = DevelopmentStatus.DRAFT.getLiteral();

                            for (int columnIndex : expressionMap.keySet()) {
                                String rowContent = row[columnIndex].getContents();
                                if (!rowContent.equals("")) {
                                    patternParameters.regex.put(expressionMap.get(columnIndex).getLiteral(), rowContent);
                                }
                            }

                            createAndStorePattern(patternParameters, selectionFolder, type);

                            names.add(contents);
                        }
                    }
                }

                rwb.close();
            } catch (BiffException e) {
                log.error(e, e);
            } catch (IOException e) {
                log.error(e, e);
            }
        }
    }

    private static void createAndStorePattern(PatternParameters parameters, IFolder selectionFolder, ExpressionType type) {

        Pattern pattern = PatternResourceFileHelper.getInstance().createPattern(parameters.name, parameters.auther,
                parameters.description, parameters.purpose, parameters.status);

        for (String key : parameters.regex.keySet()) {
            RegularExpression regularExpr = BooleanExpressionHelper.createRegularExpression(key, parameters.regex.get(key), type);
            pattern.getComponents().add(regularExpr);
        }

        boolean validStatus = PatternUtilities.isPatternValid(pattern);
        TaggedValueHelper.setValidStatus(validStatus, pattern);

        String fname = DqRepositoryViewService.createFilename(parameters.name, NewSourcePatternActionProvider.EXTENSION_PATTERN);

        try {

            String[] folderNames = parameters.relativePath.split("/"); //$NON-NLS-1$

            for (String folderName : folderNames) {
                IFolder folder = selectionFolder.getFolder(folderName);
                if (!folder.exists()) {
                    folder.create(false, true, null);
                }

                folder.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY, DQStructureManager.PATTERNS_FOLDER_PROPERTY);
                selectionFolder = folder;
            }
        } catch (CoreException e) {
            log.error(e, e);
        }

        IFile pfile = selectionFolder.getFile(fname);

        EMFSharedResources.getInstance().addEObjectToResourceSet(pfile.getFullPath().toString(), pattern);
        EMFSharedResources.getInstance().saveLastResource();
    }

    private static String getFileExtName(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        if (index == -1) {
            return null;
        }

        if (index == (name.length() - 1)) {
            return ""; //$NON-NLS-1$
        }

        return name.substring(index + 1);
    }

    /**
     * DOC zqin ImportFactory class global comment. Detailled comment
     */
    private class PatternParameters {

        String name;

        String auther;

        String description;

        String purpose;

        String status;

        String relativePath;

        Map<String, String> regex;

        public PatternParameters() {

            name = ""; //$NON-NLS-1$
            auther = ""; //$NON-NLS-1$
            description = ""; //$NON-NLS-1$
            purpose = ""; //$NON-NLS-1$
            status = DevelopmentStatus.DRAFT.getLiteral();
            relativePath = ""; //$NON-NLS-1$
            regex = new HashMap<String, String>();
        }
    }

    /**
     * DOC xqliu Comment method "importIndicatorToStucture".
     * 
     * @param importFile
     * @param selectionFolder
     * @param skip
     * @param rename
     */
    public static void importIndicatorToStucture(File importFile, IFolder selectionFolder, boolean skip, boolean rename) {
        String fileExtName = getFileExtName(importFile);
        if ("zip".equalsIgnoreCase(fileExtName)) { //$NON-NLS-1$
            Set<String> names = UDIUtils.getAllIndicatorNames(selectionFolder);
            File tempFile = null, copyFile = null;
            try {
                tempFile = new File(importFile.getAbsolutePath() + "." + System.currentTimeMillis());
                int tempCount = 0;
                while (tempFile.exists() || tempCount > 100) {
                    tempFile = new File(importFile.getAbsolutePath() + "." + System.currentTimeMillis());
                    tempCount++;
                }
                if (tempFile.mkdirs()) {
                    FilesUtils.unzip(importFile.getAbsolutePath(), tempFile.getAbsolutePath());
                    File[] listFiles = tempFile.listFiles();
                    for (int i = 0; i < listFiles.length; ++i) {
                        File file = listFiles[i];
                        String fileExt = getFileExtName(file);
                        if (FactoriesUtil.UDI.equalsIgnoreCase(fileExt)) {
                            String fname = DqRepositoryViewService.createFilename(file.getName(), FactoriesUtil.UDI);
                            copyFile = new File(selectionFolder.getLocation().toOSString() + "/" + fname);
                            FilesUtils.copyFile(file, copyFile);
                            if (copyFile.exists()) {
                                IndicatorDefinition id = UDIResourceFileHelper.getInstance().findUDI(
                                        selectionFolder.getFile(fname));
                                String name = id.getName();
                                if (names.contains(name)) {
                                    if (skip) {
                                        deleteFiles(copyFile);
                                        continue;
                                    }
                                    if (rename) {
                                        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat(
                                                "yyyyMMddHHmmssSSS");
                                        name += "(" + simpleDateFormat.format(new Date()) + Math.random() + ")";
                                        id.setName(name);
                                        names.add(name);
                                    }
                                }
                                UDIndicatorWriter.getInstance().createUDIndicatorFile(id, selectionFolder);
                                deleteFiles(copyFile);
                            }
                        }
                    }
                    deleteFiles(tempFile);
                }
            } catch (Exception e) {
                deleteFiles(copyFile, tempFile);
                log.error(e, e);
            }
        }
    }

    private static void deleteFiles(File... files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                if (file.isDirectory()) {
                    for (File tempFile : file.listFiles()) {
                        deleteFiles(tempFile);
                    }
                }
                file.delete();
            }
        }
    }
}
