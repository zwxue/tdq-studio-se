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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Expression;

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

    public static List<String> importToStucture(File importFile, IFolder selectionFolder, ExpressionType type, boolean skip,
            boolean rename) {

        List<String> importInformation = new ArrayList<String>();

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
                            importInformation.add("Pattern \"" + name + "\" has already imported");
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

                    String relativePath = "Patterns/" + createAndStorePattern(patternParameters, selectionFolder, type);
                    names.add(name);

                    importInformation.add("Pattern \"" + name + "\" imported in the \"" + relativePath + "\" folder");
                }

                reader.close();

            } catch (Exception e) {
                log.error(e, e);
                importInformation.add("Import failed");
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
                                    importInformation.add("Pattern \"" + contents + "\" has already imported");
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

                            String relativePath = "Patterns/" + createAndStorePattern(patternParameters, selectionFolder, type);

                            names.add(contents);

                            importInformation.add("Pattern \"" + contents + "\" imported in the \"" + relativePath + "\" folder");
                        }
                    }
                }

                rwb.close();
            } catch (BiffException e) {
                log.error(e, e);
                importInformation.add("Import failed");
            } catch (IOException e) {
                log.error(e, e);
                importInformation.add("Import failed");
            }
        }

        return importInformation;
    }

    private static String createAndStorePattern(PatternParameters parameters, IFolder selectionFolder, ExpressionType type) {

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

        return ResourceManager.getLibrariesFolder().getFolder(DQStructureManager.PATTERNS).getLocationURI().relativize(
                selectionFolder.getLocationURI()).toString();
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
     * DOC xqliu ImportFactory class global comment. Detailled comment
     */
    private class UDIParameters extends ImportFactory.PatternParameters {

        String category;

        public UDIParameters() {
            super();
            category = "";
        }

    }

    /**
     * DOC xqliu Comment method "importIndicatorToStucture".
     * 
     * @param importFile
     * @param selectionFolder
     * @param skip
     * @param rename
     * @return
     */
    public static List<String> importIndicatorToStucture(File importFile, IFolder selectionFolder, boolean skip, boolean rename) {

        List<String> information = new ArrayList<String>();

        Set<String> names = UDIHelper.getAllIndicatorNames(selectionFolder);

        String fileExtName = getFileExtName(importFile);

        if ("csv".equalsIgnoreCase(fileExtName)) { //$NON-NLS-1$

            String name = "";
            try {

                CsvReader reader = new CsvReader(new FileReader(importFile), CURRENT_SEPARATOR);
                reader.setEscapeMode(ESCAPE_MODE_BACKSLASH);
                reader.setTextQualifier(TEXT_QUAL);
                reader.setUseTextQualifier(USE_TEXT_QUAL);

                reader.readHeaders();

                java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");

                while (reader.readRecord()) {
                    name = reader.get(PatternToExcelEnum.Label.getLiteral());

                    if (names.contains(name)) {
                        if (skip) {
                            information.add("User Defined Indicator \"" + name + "\" has already imported");
                            continue;
                        }
                        if (rename) {
                            name = name + "(" + simpleDateFormat.format(new Date()) + Math.random() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }

                    UDIParameters udiParameters = new ImportFactory().new UDIParameters();
                    udiParameters.name = name;
                    udiParameters.auther = reader.get(PatternToExcelEnum.Author.getLiteral());
                    udiParameters.description = reader.get(PatternToExcelEnum.Description.getLiteral());
                    udiParameters.purpose = reader.get(PatternToExcelEnum.Purpose.getLiteral());
                    udiParameters.relativePath = reader.get(PatternToExcelEnum.RelativePath.getLiteral());
                    udiParameters.category = reader.get(PatternToExcelEnum.Category.getLiteral());

                    for (PatternLanguageType languagetype : PatternLanguageType.values()) {
                        String cellStr = reader.get(languagetype.getExcelEnum().getLiteral());
                        if (cellStr != null && !cellStr.equals("")) { //$NON-NLS-1$
                            udiParameters.regex.put(languagetype.getLiteral(), cellStr);
                        }
                    }

                    createAndStoreUDI(udiParameters, selectionFolder);

                    names.add(name);

                    information.add("User Defined Indicator \"" + name
                            + "\" imported in the \"Indicators/User Defined Indicators\" folder");
                }

                reader.close();
            } catch (Exception e) {
                log.error(e, e);
                information.add("User Defined Indicator \"" + name + "\" import failed");
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

                            UDIParameters udiParameters = new ImportFactory().new UDIParameters();

                            udiParameters.name = contents;
                            udiParameters.auther = row[6].getContents();
                            udiParameters.description = row[2].getContents();
                            udiParameters.purpose = row[1].getContents();
                            udiParameters.status = DevelopmentStatus.DRAFT.getLiteral();
                            udiParameters.category = row[16].getContents();

                            for (int columnIndex : expressionMap.keySet()) {
                                String rowContent = row[columnIndex].getContents();
                                if (!rowContent.equals("")) {
                                    udiParameters.regex.put(expressionMap.get(columnIndex).getLiteral(), rowContent);
                                }
                            }

                            createAndStoreUDI(udiParameters, selectionFolder);

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

        return information;
    }

    /**
     * DOC xqliu Comment method "createAndStoreUDI".
     * 
     * @param parameters
     * @param selectionFolder
     */
    private static void createAndStoreUDI(UDIParameters parameters, IFolder selectionFolder) {

        IndicatorDefinition id = UDIHelper.createUDI(parameters.name, parameters.auther, parameters.description,
                parameters.purpose, parameters.status, parameters.category);

        for (String key : parameters.regex.keySet()) {
            Expression expression = BooleanExpressionHelper.createExpression(key, parameters.regex.get(key));
            id.getSqlGenericExpression().add(expression);
        }

        String fname = DqRepositoryViewService.createFilename(parameters.name, FactoriesUtil.UDI);

        try {

            String[] folderNames = parameters.relativePath.split("/"); //$NON-NLS-1$

            for (String folderName : folderNames) {
                IFolder folder = selectionFolder.getFolder(folderName);
                if (!folder.exists()) {
                    folder.create(false, true, null);
                }

                folder.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY, DQStructureManager.UDI_FOLDER_PROPERTY);
                selectionFolder = folder;
            }
        } catch (CoreException e) {
            log.error(e, e);
        }

        IFile pfile = selectionFolder.getFile(fname);

        EMFSharedResources.getInstance().addEObjectToResourceSet(pfile.getFullPath().toString(), id);
        EMFSharedResources.getInstance().saveLastResource();
    }

    /**
     * DOC xqliu Comment method "importFile".
     * 
     * @param importFile
     * @param targetFile
     * @throws IOException
     */
    public static void importFile(File importFile, IFile targetFile) throws IOException {
        if (importFile != null && targetFile != null) {
            File file = new File(targetFile.getRawLocation().toOSString());
            if (file.exists()) {
                java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");
                File bakFile = new File(file.getAbsolutePath() + "." + simpleDateFormat.format(new Date()) + ".bak");
                FilesUtils.copyFile(file, bakFile);
            }
            FilesUtils.copyFile(importFile, file);
        }
    }
}
