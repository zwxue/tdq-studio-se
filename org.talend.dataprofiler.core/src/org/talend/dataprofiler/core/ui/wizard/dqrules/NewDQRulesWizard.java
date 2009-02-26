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
package org.talend.dataprofiler.core.ui.wizard.dqrules;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.rules.RulesFactory;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class NewDQRulesWizard extends AbstractWizard {

    private static Logger log = Logger.getLogger(NewDQRulesWizard.class);

    private NewDQRulesWizardPage1 mPage;

    private NewDQRulesWizardPage2 mPage2;

    @Override
    public boolean canFinish() {
        if (mPage2 != null) {
            if (mPage2.getWhereText() != null) {
                if (mPage2.getWhereText().getText() != null && !"".equals(mPage2.getWhereText().getText())) {
                    return mPage2.isPageComplete();
                }
            }
        }
        return false;
    }

    private ConnectionParameter parameter;

    private IPath location;

    private Expression expression;

    private final static String EXPRESSION_BODY = "SELECT COUNT(*) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>";

    private final static String EXPRESSION_LANG = ExecutionLanguage.SQL.getLiteral();

    // default value of Criticality Level
    private static final int CRITICALITY_LEVEL_DEFAULT = 1;

    public NewDQRulesWizard(ConnectionParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void addPages() {
        String s = DefaultMessagesImpl.getString("NewDQRulesWizard.dqRule"); //$NON-NLS-1$

        mPage = new NewDQRulesWizardPage1();
        mPage.setTitle(DefaultMessagesImpl.getString("NewDQRulesWizard.createPage1_2", s)); //$NON-NLS-1$
        mPage.setDescription(DefaultMessagesImpl.getString("NewDQRulesWizard.defineProp")); //$NON-NLS-1$
        mPage.setPageComplete(false);

        mPage2 = new NewDQRulesWizardPage2();
        mPage2.setTitle(DefaultMessagesImpl.getString("NewDQRulesWizard.createPage2_2", s)); //$NON-NLS-1$
        mPage2.setDescription(DefaultMessagesImpl.getString("NewDQRulesWizard.defineWhere")); //$NON-NLS-1$

        addPage(mPage);
        addPage(mPage2);
    }

    @Override
    protected ConnectionParameter getConnectionParameter() {
        return this.parameter;
    }

    @Override
    public boolean performFinish() {

        WhereRule whereRule = RulesFactory.eINSTANCE.createWhereRule();
        String name = parameter.getName();
        whereRule.setName(name);
        String whereClause = mPage2.getWhereText().getText();
        whereRule.setWhereExpression(whereClause);
        whereRule.setCriticalityLevel(CRITICALITY_LEVEL_DEFAULT);
        whereRule.getSqlGenericExpression().add(getExpression());

        TaggedValueHelper.setAuthor(whereRule, parameter.getAuthor());
        TaggedValueHelper.setDescription(parameter.getDescription(), whereRule);
        TaggedValueHelper.setPurpose(parameter.getPurpose(), whereRule);
        TaggedValueHelper.setDevStatus(whereRule, DevelopmentStatus.get(parameter.getStatus()));
        TaggedValueHelper.setValidStatus(true, whereRule);

        String fname = DqRepositoryViewService.createFilename(name, FactoriesUtil.DQRULE);
        IFolder folderResource = parameter.getFolderProvider().getFolderResource();
        IFile file = folderResource.getFile(fname);
        location = file.getFullPath();
        if (file.exists()) {
            log.error(DefaultMessagesImpl.getString("CreatePatternWizard.cannotSavePattern", name, file.getFullPath()));
            return false;
        }

        EMFSharedResources.getInstance().addEObjectToResourceSet(file.getFullPath().toString(), whereRule);
        EMFSharedResources.getInstance().saveLastResource();
        return true;
    }

    public Expression getExpression() {
        if (expression == null) {
            expression = BooleanExpressionHelper.createExpression(EXPRESSION_LANG, EXPRESSION_BODY);
        }
        return expression;
    }

    /**
     * Getter for location.
     * 
     * @return the location
     */
    public IPath getLocation() {
        return this.location;
    }
}
