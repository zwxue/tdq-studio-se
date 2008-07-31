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
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.Wizard;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataprofiler.core.helper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataprofiler.core.ui.editor.pattern.PatternMasterDetailsPage;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class ImportPatternsWizard extends Wizard {

    private IFolder folder;

    private ImportPatternsWizardPage page;

    /**
     * DOC qzhang CreateSqlFileWizard constructor comment.
     * 
     * @param folder
     * @param type
     */
    public ImportPatternsWizard(IFolder folder) {
        this.folder = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        page = new ImportPatternsWizardPage();
        addPage(page);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        Set<String> names = getAllPatternNames();
        String xlsFile = page.getXLSFile();
        boolean skip = page.getSkip();
        boolean rename = page.getRename();
        try {
            File file = new File(xlsFile);
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("UTF-8");
            Workbook rwb = Workbook.getWorkbook(file, settings);
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
                        Pattern pattern = PatternResourceFileHelper.getInstance().createPattern(contents, row[6].getContents(),
                                row[2].getContents(), row[1].getContents(), DevelopmentStatus.DRAFT.getLiteral());
                        addComponents(pattern, row[3].getContents(), row[4].getContents(), row[5].getContents());
                        EMFUtil util = EMFSharedResources.getSharedEmfUtil();
                        String fname = DqRepositoryViewService.createFilename(contents,
                                NewSourcePatternActionProvider.EXTENSION_PATTERN);
                        IFile pfile = folder.getFile(fname);
                        util.addPoolToResourceSet(pfile.getFullPath().toString(), pattern);
                        util.saveLastResource();
                        names.add(contents);
                    }
                }
            }
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * DOC qzhang Comment method "addComponents".
     * 
     * @param pattern
     * @param reg
     * @param mysql
     * @param string
     */
    private void addComponents(Pattern pattern, String reg, String mysql, String oracle) {
        RegularExpression regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
        Expression expression = CoreFactory.eINSTANCE.createExpression();
        expression.setBody(reg);
        expression.setLanguage(PatternMasterDetailsPage.ALL_DATABASE_TYPE);
        regularExpr.setExpression(expression);
        regularExpr.setExpressionType(ExpressionType.REGEXP.getName());
        pattern.getComponents().add(regularExpr);

        regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
        expression = CoreFactory.eINSTANCE.createExpression();
        expression.setBody(mysql);
        expression.setLanguage("Mysql");
        regularExpr.setExpression(expression);
        regularExpr.setExpressionType(ExpressionType.REGEXP.getName());
        pattern.getComponents().add(regularExpr);

        regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
        expression = CoreFactory.eINSTANCE.createExpression();
        expression.setBody(oracle);
        expression.setLanguage("Oracle");
        regularExpr.setExpression(expression);
        regularExpr.setExpressionType(ExpressionType.REGEXP.getName());
        pattern.getComponents().add(regularExpr);

    }

    /**
     * DOC qzhang Comment method "getAllPatternNames".
     * 
     * @return
     * @throws CoreException
     */
    private Set<String> getAllPatternNames() {
        Set<String> list = new HashSet<String>();
        try {
            for (IResource resource : folder.members()) {
                if (resource instanceof IFile) {
                    Pattern fr = PatternResourceFileHelper.getInstance().findPattern((IFile) resource);
                    if (fr != null) {
                        list.add(fr.getName());
                    }
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return list;
    }
}
