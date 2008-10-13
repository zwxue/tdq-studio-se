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
package org.talend.dataprofiler.core.pattern;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;

import com.csvreader.CsvReader;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ImportFactory {

    private static final char CURRENT_SEPARATOR = '\t';

    static void importToStucture(File importFile, IFolder selectionFolder, boolean skip, boolean rename) {

        Set<String> names = PatternUtilities.getAllPatternNames(selectionFolder);

        String fileExtName = getFileExtName(importFile);

        if ("csv".equalsIgnoreCase(fileExtName)) {

            try {

                CsvReader reader = new CsvReader(new FileReader(importFile), CURRENT_SEPARATOR);
                reader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
                reader.setTextQualifier('"');
                reader.setUseTextQualifier(true);

                reader.readHeaders();

                while (reader.readRecord()) {
                    String name = reader.get(PatternToExcelEnum.Label.getLiteral());

                    if (names.contains(name)) {
                        if (skip) {
                            continue;
                        }
                        if (rename) {
                            name = name + "(" + new Date() + ")";
                        }
                    }

                    PatternParameters patternParameters = new ImportFactory().new PatternParameters();
                    patternParameters.name = name;
                    patternParameters.auther = reader.get(PatternToExcelEnum.Author.getLiteral());
                    patternParameters.description = reader.get(PatternToExcelEnum.Description.getLiteral());
                    patternParameters.purpose = reader.get(PatternToExcelEnum.Purpose.getLiteral());
                    patternParameters.relativePath = reader.get(PatternToExcelEnum.RelativePath.getLiteral());

                    for (PatternLanguageType type : PatternLanguageType.VALUES) {
                        String cellStr = reader.get(type.getExcelEnum().getLiteral());
                        if (cellStr != null && !cellStr.equals("")) {
                            patternParameters.regex.put(type.getLiteral(), cellStr);
                        }
                    }

                    createAndStorePattern(patternParameters, selectionFolder);

                    names.add(name);
                }

                reader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if ("xls".equalsIgnoreCase(fileExtName)) {
            try {
                WorkbookSettings settings = new WorkbookSettings();
                settings.setEncoding("UTF-8");
                Workbook rwb = Workbook.getWorkbook(importFile, settings);
                Sheet[] sheets = rwb.getSheets();
                for (Sheet sheet : sheets) {
                    int rows = sheet.getRows();
                    for (int i = 1; i < rows; i++) {
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

                            for (PatternLanguageType type : PatternLanguageType.VALUES) {
                                String cellStr = sheet.getCell(type.getExcelEnum().getLiteral()).getContents();
                                if (cellStr != null && !cellStr.equals("")) {
                                    patternParameters.regex.put(type.getLiteral(), cellStr);
                                }
                            }

                            createAndStorePattern(patternParameters, selectionFolder);

                            names.add(contents);
                        }
                    }
                }

                rwb.close();
            } catch (BiffException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createAndStorePattern(PatternParameters parameters, IFolder selectionFolder) {

        Pattern pattern = PatternResourceFileHelper.getInstance().createPattern(parameters.name, parameters.auther,
                parameters.description, parameters.purpose, parameters.status);

        for (String key : parameters.regex.keySet()) {
            RegularExpression regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
            Expression expression = CoreFactory.eINSTANCE.createExpression();
            expression.setBody(parameters.regex.get(key));
            expression.setLanguage(key);
            regularExpr.setExpression(expression);
            regularExpr.setExpressionType(ExpressionType.REGEXP.getName());
            pattern.getComponents().add(regularExpr);
        }

        boolean validStatus = PatternUtilities.isPatternValid(pattern);
        TaggedValueHelper.setValidStatus(validStatus, pattern);

        String fname = DqRepositoryViewService.createFilename(parameters.name, NewSourcePatternActionProvider.EXTENSION_PATTERN);

        try {

            String[] folderNames = parameters.relativePath.split("/");

            for (String folderName : folderNames) {
                IFolder folder = selectionFolder.getFolder(folderName);
                if (!folder.exists()) {
                    folder.create(false, true, null);
                }

                folder.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY, DQStructureManager.PATTERNS_FOLDER_PROPERTY);
                selectionFolder = folder;
            }
        } catch (CoreException e) {
            e.printStackTrace();
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
            return "";
        }

        return name.substring(index + 1);
    }

    private static Set<String> list = new HashSet<String>();

    /**
     * DOC zqin ImportFactory class global comment. Detailled comment
     */
    private class PatternParameters {

        public String name;

        public String auther;

        public String description;

        public String purpose;

        public String status;

        public String relativePath;

        public Map<String, String> regex;

        public PatternParameters() {

            name = "";
            auther = "";
            description = "";
            purpose = "";
            status = DevelopmentStatus.DRAFT.getLiteral();
            relativePath = "";
            regex = new HashMap<String, String>();
        }
    }
}
