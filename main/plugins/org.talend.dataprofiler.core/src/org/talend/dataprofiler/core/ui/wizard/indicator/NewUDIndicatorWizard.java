// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.indicator;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.Item;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.PatternLanguageType;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorDefinitionItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditor;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.parameters.UDIndicatorParameter;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.indicators.UDIndicatorBuilder;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.dates.DateUtils;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class NewUDIndicatorWizard extends AbstractWizard {

    static Logger log = Logger.getLogger(NewUDIndicatorWizard.class);

    private TdExpression expression;

    private NewUDIndicatorWizardPage1 mPage1;

    private NewUDIndicatorWizardPage2 mPage2;

    private UDIndicatorParameter parameter;

    public NewUDIndicatorWizard(UDIndicatorParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void addPages() {
        String s = DefaultMessagesImpl.getString("NewUDIndicatorWizard.udi"); //$NON-NLS-1$

        mPage1 = new NewUDIndicatorWizardPage1();
        mPage1.setTitle(s + DefaultMessagesImpl.getString("NewUDIndicatorWizard.createPage1_2")); //$NON-NLS-1$
        mPage1.setDescription(DefaultMessagesImpl.getString("NewUDIndicatorWizard.defineProp")); //$NON-NLS-1$
        mPage1.setPageComplete(false);

        mPage2 = new NewUDIndicatorWizardPage2();
        mPage2.setTitle(s + DefaultMessagesImpl.getString("NewUDIndicatorWizard.createPage2_2")); //$NON-NLS-1$
        mPage2.setDescription(""); //$NON-NLS-1$

        addPage(mPage1);
        addPage(mPage2);
    }

    public TypedReturnCode<Object> createAndSaveCWMFile(ModelElement cwmElement) {
        IndicatorDefinition indicatorDefinition = (IndicatorDefinition) cwmElement;
        // MOD mzhao feature 11128.
        if (!getParameter().getLanguage().equals(PatternLanguageType.JAVA.getName())) {
            indicatorDefinition.getSqlGenericExpression().add(getExpression());
        } else {
            TaggedValue classNameTV = TaggedValueHelper.createTaggedValue(TaggedValueHelper.CLASS_NAME_TEXT, ""); //$NON-NLS-1$
            TaggedValue jarPathTV = TaggedValueHelper.createTaggedValue(TaggedValueHelper.JAR_FILE_PATH, ""); //$NON-NLS-1$
            indicatorDefinition.getTaggedValue().add(classNameTV);
            indicatorDefinition.getTaggedValue().add(jarPathTV);
        }
        UDIHelper.setUDICategory(indicatorDefinition, DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory());
        IFolder folder = parameter.getFolderProvider().getFolderResource();
        // ADD xqliu 2010-06-04 feature 13454
        recordModificationDate(indicatorDefinition);
        // ~ 13454
        TypedReturnCode<Object> returnCode = ElementWriterFactory.getInstance().createIndicatorDefinitionWriter()
                .create(indicatorDefinition, folder);
        // MOD qiongli 2011-2-18,bug 19014.After creating UDI,reaload all indicators
        DefinitionHandler.getInstance().reloadIndicatorsDefinitions();
        return returnCode;
    }

    /**
     * DOC xqliu Comment method "recordModificationDate".
     * 
     * @param indicatorDefinition
     */
    private void recordModificationDate(IndicatorDefinition indicatorDefinition) {
        EList<TdExpression> sqlGenericExpression = indicatorDefinition.getSqlGenericExpression();
        if (sqlGenericExpression != null) {
            String dateValue = DateUtils.getCurrentDate(DateUtils.PATTERN_5);
            for (TdExpression exp : sqlGenericExpression) {
                exp.setModificationDate(dateValue);
            }
        }
    }

    public ModelElement initCWMResourceBuilder() {
        UDIndicatorBuilder udiBuilder = new UDIndicatorBuilder();
        boolean udiInitialized = udiBuilder.initializeUDIndicatorBuilder(parameter.getName());
        if (udiInitialized) {
            return udiBuilder.getUDIndicator();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataprofiler.core.ui.wizard.AbstractWizard#fillMetadataToCWMResource(orgomg.cwm.objectmodel.core.
     * ModelElement)
     */
    @Override
    public void fillMetadataToCWMResource(ModelElement cwmElement) {
        super.fillMetadataToCWMResource(cwmElement);
        TaggedValueHelper.setValidStatus(true, cwmElement);
    }

    public TdExpression getExpression() {
        if (expression == null) {
            expression = RelationalFactory.eINSTANCE.createTdExpression();
        }
        if (expression != null) {
            expression.setBody(this.getParameter().getExpression());
            expression.setLanguage(this.getParameter().getLanguage());
        }
        return expression;
    }

    @Override
    protected String getEditorName() {
        return IndicatorEditor.class.getName();
    }

    @Override
    protected UDIndicatorParameter getParameter() {
        return parameter;
    }

    @Override
    protected ResourceFileMap getResourceFileMap() {
        return null;
    }

    @Override
    public boolean canFinish() {
        if (mPage1 != null && mPage2 != null) {
            // MOD mzhao feature 11128, In case of Java UDI,the page can finish.
            if (PatternLanguageType.JAVA.getName().equals(getParameter().getLanguage())) {
                return true;
            }
            if (getParameter().getExpression() != null && !"".equals(getParameter().getExpression().trim())) { //$NON-NLS-1$
                return mPage1.isPageComplete() && mPage2.isPageComplete();
            }
        }
        return false;
    }

    @Override
    public void openEditor(Item item) {
        IndicatorDefinitionItemEditorInput udiEditorInput = new IndicatorDefinitionItemEditorInput(item);
        CorePlugin.getDefault().openEditor(udiEditorInput, IndicatorEditor.class.getName());
    }
}
