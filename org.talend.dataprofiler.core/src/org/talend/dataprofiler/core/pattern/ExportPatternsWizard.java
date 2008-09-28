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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.Wizard;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ExportPatternsWizard extends Wizard {

    private IFolder folder;

    private ExportPatternsWizardPage page;

    /**
     * DOC zqin ExportPatternsWizard constructor comment.
     */
    public ExportPatternsWizard(IFolder folder) {
        this.folder = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        String xlsFile = page.getXLSFile();
        Object[] elements = page.getSelectedPatternsTree().getCheckedElements();

        List<Pattern> seletedPatterns = new ArrayList<Pattern>();
        for (Object element : elements) {
            if (element instanceof Pattern) {
                seletedPatterns.add((Pattern) element);
            }
        }

        // try {
        // File file = new File(xlsFile);
        // WorkbookSettings settings = new WorkbookSettings();
        // settings.setEncoding("UTF-8");
        //
        // // create new sheet
        // WritableWorkbook wwb = Workbook.createWorkbook(file, settings);
        // WritableSheet mainSheet = wwb.createSheet("Test Sheet 1", 0);
        //
        // // create header
        // for (int i = 0; i < PatternToExcelEnum.VALUES.size(); i++) {
        // WritableFont wf = new WritableFont(WritableFont.TAHOMA, 12, WritableFont.BOLD, true);
        // WritableCellFormat wcfF = new WritableCellFormat(wf);
        // wcfF.setBackground(Colour.GRAY_25);
        //
        // String literal = PatternToExcelEnum.VALUES.get(i).getLiteral();
        // Label label = new Label(i, 0, literal, wcfF);
        // mainSheet.addCell(label);
        // }
        //
        // // create content
        // for (int i = 1; i < seletedPatterns.size() + 1; i++) {
        // Pattern pattern = seletedPatterns.get(i - 1);
        //
        // }
        //
        // wwb.write();
        //
        // wwb.close();
        //
        // return true;
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter out = null;

        File file = new File(xlsFile);
        try {

            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos);
            out = new BufferedWriter(osw);

            for (int i = 0; i < seletedPatterns.size() + 1; i++) {
                for (PatternToExcelEnum enmu : PatternToExcelEnum.VALUES) {
                    if (i == 0) {
                        out.write(enmu.getLiteral() + ",");
                    } else {
                        out.write(getRelatedValueFromPattern(seletedPatterns.get(i - 1)).get(enmu) + ",");
                    }
                }

                out.newLine();
            }

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void addPages() {
        page = new ExportPatternsWizardPage(folder);
        addPage(page);
    }

    private Map<PatternToExcelEnum, String> getRelatedValueFromPattern(Pattern pattern) {

        Map<PatternToExcelEnum, String> patternMap = new HashMap<PatternToExcelEnum, String>();

        IFile file = PatternResourceFileHelper.getInstance().getPatternFile(pattern);
        IFolder patternFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES).getFolder(
                DQStructureManager.PATTERNS);
        URI relativeURI = patternFolder.getLocationURI().relativize(file.getLocationURI());
        // URI relativeURI = file.getLocationURI().relativize(patternFolder.getLocationURI());

        // get the basic information
        patternMap.put(PatternToExcelEnum.Label, pattern.getName());
        patternMap.put(PatternToExcelEnum.Purpose, TaggedValueHelper.getPurpose(pattern));
        patternMap.put(PatternToExcelEnum.Description, TaggedValueHelper.getDescription(pattern));
        patternMap.put(PatternToExcelEnum.Author, TaggedValueHelper.getAuthor(pattern));
        patternMap.put(PatternToExcelEnum.RelativePath, relativeURI.toString());

        for (PatternLanguageType type : PatternLanguageType.VALUES) {
            for (PatternComponent component : pattern.getComponents()) {
                Expression expression = ((RegularExpression) component).getExpression();
                if (expression != null && expression.getLanguage().equals(type.getDbType().getDBKey())) {
                    patternMap.put(type.getExcelEnum(), expression.getBody());
                }
            }

            if (!patternMap.containsKey(type.getExcelEnum())) {
                patternMap.put(type.getExcelEnum(), "");
            }
        }

        return patternMap;
    }
}
