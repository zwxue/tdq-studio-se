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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.Wizard;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.EMFUtil;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
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
public class CreatePatternWizard extends Wizard {

    private CreatePatternWizardPage1 mPage;

    private IFolder folder;

    private CreatePatternWizardPage2 mPage2;

    /**
     * DOC qzhang CreateSqlFileWizard constructor comment.
     * 
     * @param folder
     */
    public CreatePatternWizard(IFolder folder) {
        this.folder = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        mPage = new CreatePatternWizardPage1();
        AbstractWizardPage.setConnectionParams(new ConnectionParameter());
        mPage.setTitle("New Pattern Creation Page1/2");
        mPage.setDescription("Define the properties");
        mPage.setPageComplete(false);

        mPage2 = new CreatePatternWizardPage2();
        AbstractWizardPage.setConnectionParams(new ConnectionParameter());
        mPage2.setTitle("New Pattern Creation Page2/2");
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
        String name = AbstractWizardPage.getConnectionParams().getName();
        pattern.setName(name);
        RegularExpression regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
        Expression expression = CoreFactory.eINSTANCE.createExpression();
        String expr = mPage2.getNameText().getText();
        expression.setBody(expr);
        expression.setLanguage("SQL");
        regularExpr.setExpression(expression);
        pattern.getComponents().add(regularExpr);
        EMFUtil util = EMFSharedResources.getSharedEmfUtil();
        IPath location = folder.getLocation();
        location = location.append(name);
        location = location.addFileExtension(NewSourcePatternActionProvider.EXTENSION_PATTERN);
        util.addPoolToResourceSet(new File(location.toPortableString()), pattern);
        util.save();
        return true;
    }

}
