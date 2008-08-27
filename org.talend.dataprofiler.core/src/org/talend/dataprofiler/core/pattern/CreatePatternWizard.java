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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class CreatePatternWizard extends AbstractWizard {

    private static Logger log = Logger.getLogger(CreatePatternWizard.class);

    private CreatePatternWizardPage1 mPage;

    private CreatePatternWizardPage2 mPage2;

    private IPath location;

    private ExpressionType type;

    private ConnectionParameter parameter;

    /**
     * DOC qzhang CreateSqlFileWizard constructor comment.
     * 
     * @param folder
     * @param type
     */
    public CreatePatternWizard(ConnectionParameter parameter, ExpressionType type) {
        this.type = type;
        this.parameter = parameter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        String s = "Regular expression";
        switch (type) {
        case SQL_LIKE:
            s = "SQL expression";
            break;
        default:
            break;
        }
        mPage = new CreatePatternWizardPage1();
        mPage.setTitle(s + " Creation Page1/2");
        mPage.setDescription("Define the properties");
        mPage.setPageComplete(false);

        mPage2 = new CreatePatternWizardPage2();
        mPage2.setTitle(s + " Creation Page2/2");
        mPage2.setDescription("Define the properties");
        mPage2.setPageComplete(false);
        addPage(mPage);
        addPage(mPage2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        Pattern pattern = PatternFactory.eINSTANCE.createPattern();
        String name = parameter.getName();
        pattern.setName(name);
        TaggedValueHelper.setAuthor(pattern, parameter.getAuthor());
        TaggedValueHelper.setDescription(parameter.getDescription(), pattern);
        TaggedValueHelper.setPurpose(parameter.getPurpose(), pattern);
        TaggedValueHelper.setDevStatus(pattern, DevelopmentStatus.get(parameter.getStatus()));

        // PTODO qzhang fixed bug 4296: set the Pattern is valid
        TaggedValueHelper.setValidStatus(true, pattern);

        RegularExpression regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
        Expression expression = CoreFactory.eINSTANCE.createExpression();
        String expr = mPage2.getNameText().getText();
        expression.setBody(expr);
        String cl = mPage2.getComboLang();
        expression.setLanguage(cl); // PTODO qzhang fixed bug 4259.save language from selected db type
        regularExpr.setExpression(expression);
        regularExpr.setExpressionType(type.getName());

        pattern.getComponents().add(regularExpr);
        EMFUtil util = EMFSharedResources.getSharedEmfUtil();
        String fname = DqRepositoryViewService.createFilename(name, NewSourcePatternActionProvider.EXTENSION_PATTERN);

        IFolder folderResource = parameter.getFolderProvider().getFolderResource();
        IFile file = folderResource.getFile(fname);
        location = file.getFullPath();
        if (file.exists()) {
            log.error("Cannot save pattern " + name + ", file " + file.getFullPath() + " already exists!");
            return false;
        }

        util.addPoolToResourceSet(file.getFullPath().toString(), pattern);
        util.saveLastResource();
        return true;
    }

    /**
     * Getter for location.
     * 
     * @return the location
     */
    public IPath getLocation() {
        return this.location;
    }

    @Override
    protected ConnectionParameter getConnectionParameter() {

        return this.parameter;
    }

}
