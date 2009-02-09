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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import orgomg.cwm.objectmodel.core.Expression;

import com.csvreader.CsvWriter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ExportFactory {

    private static final char CURRENT_SEPARATOR = '\t';

    private static ExpressionType type;

    static void exportToFile(Pattern[] patterns, File exportFile, ExpressionType type) {

        String fileExtName = getFileExtName(exportFile);
        ExportFactory.type = type;

        if ("csv".equalsIgnoreCase(fileExtName)) { //$NON-NLS-1$

            try {

                CsvWriter out = new CsvWriter(new FileOutputStream(exportFile), CURRENT_SEPARATOR, Charset.defaultCharset());
                out.setEscapeMode(CsvWriter.ESCAPE_MODE_BACKSLASH);
                out.setTextQualifier('"');
                out.setForceQualifier(true);

                PatternToExcelEnum[] values = PatternToExcelEnum.values();
                String[] temp = new String[values.length];

                for (int i = 0; i < patterns.length + 1; i++) {

                    for (int j = 0; j < values.length; j++) {
                        if (i == 0) {
                            temp[j] = values[j].getLiteral();
                        } else {
                            temp[j] = getRelatedValueFromPattern(patterns[i - 1]).get(values[j]);
                        }
                    }

                    out.writeRecord(temp);
                }

                out.flush();
                out.close();

            } catch (FileNotFoundException fe) {
                MessageDialogWithToggle
                        .openError(
                                null,
                                DefaultMessagesImpl.getString("ExportFactory.errorOne"), DefaultMessagesImpl.getString("ExportFactory.notFoundFile")); //$NON-NLS-1$ //$NON-NLS-2$
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private static Map<PatternToExcelEnum, String> getRelatedValueFromPattern(Pattern pattern) {

        Map<PatternToExcelEnum, String> patternMap = new HashMap<PatternToExcelEnum, String>();

        IFolder curFolder = null;

        switch (type) {
        case REGEXP:
            curFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES).getFolder(
                    DQStructureManager.PATTERNS);
            break;
        case SQL_LIKE:
            curFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES).getFolder(
                    DQStructureManager.SQL_PATTERNS);
            break;
        default:
        }

        if (curFolder != null) {
            IFile file = PatternResourceFileHelper.getInstance().getPatternFile(pattern, new IFolder[] { curFolder });
            URI relativeURI = curFolder.getLocationURI().relativize(file.getParent().getLocationURI());

            // get the basic information
            patternMap.put(PatternToExcelEnum.Label, pattern.getName());
            patternMap.put(PatternToExcelEnum.Purpose, TaggedValueHelper.getPurpose(pattern));
            patternMap.put(PatternToExcelEnum.Description, TaggedValueHelper.getDescription(pattern));
            patternMap.put(PatternToExcelEnum.Author, TaggedValueHelper.getAuthor(pattern));
            patternMap.put(PatternToExcelEnum.RelativePath, relativeURI.toString());

            for (PatternLanguageType languagetype : PatternLanguageType.values()) {
                for (PatternComponent component : pattern.getComponents()) {
                    Expression expression = ((RegularExpression) component).getExpression();
                    if (expression != null && expression.getLanguage().equalsIgnoreCase(languagetype.getLiteral())) {
                        patternMap.put(languagetype.getExcelEnum(), expression.getBody());
                    }
                }

                if (!patternMap.containsKey(languagetype.getExcelEnum())) {
                    patternMap.put(languagetype.getExcelEnum(), ""); //$NON-NLS-1$
                }
            }
        }

        return patternMap;
    }
}
