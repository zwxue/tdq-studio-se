// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.parserrule;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.core.model.properties.Item;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.dqrules.DQRuleEditor;
import org.talend.dataprofiler.core.ui.editor.parserrules.ParserRuleItemEditorInput;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.rules.ParserRule;
import org.talend.dq.analysis.parameters.DQParserRulesParameter;
import org.talend.dq.dqrule.DqRuleBuilder;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class NewParserRulesWizard extends AbstractWizard {

    static Logger log = Logger.getLogger(NewParserRulesWizard.class);

    private NewParserRulesWizardPage1 mPage;

    private NewParserRulesWizardPage2 mPage2;

    private DQParserRulesParameter parameter;

    private TdExpression expression;

    public NewParserRulesWizard(DQParserRulesParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void addPages() {
        String s = DefaultMessagesImpl.getString("NewParserRulesWizard.parserRule"); //$NON-NLS-1$

        mPage = new NewParserRulesWizardPage1();
        mPage.setTitle(s + DefaultMessagesImpl.getString("NewParserRulesWizard.createPage1_2")); //$NON-NLS-1$
        mPage.setDescription(DefaultMessagesImpl.getString("NewParserRulesWizard.defineProp")); //$NON-NLS-1$
        mPage.setPageComplete(false);

        mPage2 = new NewParserRulesWizardPage2();
        mPage2.setTitle(s + DefaultMessagesImpl.getString("NewParserRulesWizard.createPage2_2")); //$NON-NLS-1$
        mPage2.setDescription(DefaultMessagesImpl.getString("NewParserRulesWizard.defineRules")); //$NON-NLS-1$

        addPage(mPage);
        if (!isComeFromTestEditor()) {
            addPage(mPage2);
        }
    }

    /**
     * DOC zshen Comment method "isComeFromTestEditor".
     * 
     * @return
     */
    private boolean isComeFromTestEditor() {
        return getParameter().getParserRule() != null;
    }

    public TypedReturnCode<Object> createAndSaveCWMFile(ModelElement cwmElement) {
        ParserRule parserRule = (ParserRule) cwmElement;
        TaggedValueHelper.setValidStatus(true, parserRule);
        if (isComeFromTestEditor()) {
            // MOD klliu bug TDQ-4772 2012-03-20
            // copy the informations of old Expressions for the new parser rule,and it's used when an new rule is
            // created in testing rule editor.
            List<TdExpression> ruleExpressions = parameter.getParserRule().getExpression();
            for (TdExpression expression : ruleExpressions) {
                parserRule.addExpression(expression.getName(), expression.getLanguage(), expression.getBody());
            }
            // ~
        } else {
            parserRule
                    .addExpression(parameter.getParserRuleName(), parameter.getParserRuleType(), parameter.getParserRuleValue());
            IndicatorCategory ruleIndicatorCategory = DefinitionHandler.getInstance().getDQRuleIndicatorCategory();
            if (ruleIndicatorCategory != null && !parserRule.getCategories().contains(ruleIndicatorCategory)) {
                parserRule.getCategories().add(ruleIndicatorCategory);
            }
        }
        IFolder folder = parameter.getFolderProvider().getFolderResource();
        return ElementWriterFactory.getInstance().createdRuleWriter().create(parserRule, folder);
    }

    public ModelElement initCWMResourceBuilder() {
        DqRuleBuilder ruleBuilder = new DqRuleBuilder();
        boolean ruleInitialized = ruleBuilder.initializeParserRuleBuilder(parameter.getName());
        if (ruleInitialized) {
            return ruleBuilder.getParserRule();
        }
        return null;
    }

    public TdExpression getExpression() {
        if (expression == null) {
            expression = (TdExpression) EcoreUtil.copy(DefinitionHandler.getInstance().getDQRuleDefaultIndicatorDefinition()
                    .getSqlGenericExpression().get(0));
        }
        return expression;
    }

    @Override
    protected String getEditorName() {
        return DQRuleEditor.class.getName();
    }

    @Override
    protected DQParserRulesParameter getParameter() {
        return parameter;
    }

    @Override
    protected ResourceFileMap getResourceFileMap() {
        return DQRuleResourceFileHelper.getInstance();
    }

    @Override
    public boolean canFinish() {
        if (isComeFromTestEditor()) {
            return true;
        }
        if (mPage2 != null) {
            if (getParameter().getParserRuleName() != null && !"".equals(getParameter().getParserRuleName())) { //$NON-NLS-1$
                return mPage2.isPageComplete();
            }
        }
        return false;
    }

    @Override
    public void openEditor(Item item) {
        ParserRuleItemEditorInput parserRuleEditorInput = new ParserRuleItemEditorInput(item);
        CorePlugin.getDefault().openEditor(parserRuleEditorInput, DQRuleEditor.class.getName());
    }

}
