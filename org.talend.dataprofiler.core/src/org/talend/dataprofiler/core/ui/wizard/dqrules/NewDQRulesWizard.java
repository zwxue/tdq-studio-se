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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.dqrules.DQRuleEditor;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.analysis.parameters.DQRulesParameter;
import org.talend.dq.dbms.GenericSQLHandler;
import org.talend.dq.dqrule.DqRuleBuilder;
import org.talend.dq.dqrule.DqRuleWriter;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class NewDQRulesWizard extends AbstractWizard {

    private NewDQRulesWizardPage1 mPage;

    private NewDQRulesWizardPage2 mPage2;

    private DQRulesParameter parameter;

    private Expression expression;

    private static final String EXPRESSION_BODY = "SELECT COUNT(*) FROM " + GenericSQLHandler.TABLE_NAME + " "
            + GenericSQLHandler.JOIN_CLAUSE + " " + GenericSQLHandler.WHERE_CLAUSE;

    private static final String EXPRESSION_LANG = ExecutionLanguage.SQL.getLiteral();

    // default value of Criticality Level
    private static final int CRITICALITY_LEVEL_DEFAULT = 1;

    public NewDQRulesWizard(DQRulesParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void addPages() {
        String s = DefaultMessagesImpl.getString("NewDQRulesWizard.dqRule"); //$NON-NLS-1$

        mPage = new NewDQRulesWizardPage1();
        mPage.setTitle(s + DefaultMessagesImpl.getString("NewDQRulesWizard.createPage1_2")); //$NON-NLS-1$
        mPage.setDescription(DefaultMessagesImpl.getString("NewDQRulesWizard.defineProp")); //$NON-NLS-1$
        mPage.setPageComplete(false);

        mPage2 = new NewDQRulesWizardPage2();
        mPage2.setTitle(s + DefaultMessagesImpl.getString("NewDQRulesWizard.createPage2_2")); //$NON-NLS-1$
        mPage2.setDescription(DefaultMessagesImpl.getString("NewDQRulesWizard.defineWhere")); //$NON-NLS-1$

        addPage(mPage);
        addPage(mPage2);
    }

    public TypedReturnCode<IFile> createAndSaveCWMFile(ModelElement cwmElement) {
        WhereRule whereRule = (WhereRule) cwmElement;

        TaggedValueHelper.setValidStatus(true, whereRule);
        whereRule.setWhereExpression(parameter.getWhereClause());
        whereRule.setCriticalityLevel(CRITICALITY_LEVEL_DEFAULT);
        whereRule.getSqlGenericExpression().add(getExpression());

        IFolder folder = parameter.getFolderProvider().getFolderResource();
        return DqRuleWriter.getInstance().createDqRuleFile(whereRule, folder);
    }

    public ModelElement initCWMResourceBuilder() {
        DqRuleBuilder ruleBuilder = new DqRuleBuilder();

        boolean ruleInitialized = ruleBuilder.initializeDqRuleBuilder(parameter.getName());
        if (ruleInitialized) {
            return ruleBuilder.getWhereRule();
        }
        return null;
    }

    public Expression getExpression() {
        if (expression == null) {
            expression = BooleanExpressionHelper.createExpression(EXPRESSION_LANG, EXPRESSION_BODY);
        }
        return expression;
    }

    @Override
    protected String getEditorName() {
        return DQRuleEditor.class.getName();
    }

    @Override
    protected DQRulesParameter getParameter() {
        return parameter;
    }

    @Override
    protected ResourceFileMap getResourceFileMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canFinish() {
        if (mPage2 != null) {
            if (getParameter().getWhereClause() != null) {
                if (getParameter().getWhereClause() != null && !"".equals(getParameter().getWhereClause())) {
                    return mPage2.isPageComplete();
                }
            }
        }
        return false;
    }
}
