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
package org.talend.dataprofiler.core.ui.wizard.indicator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditor;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.parameters.UDIndicatorParameter;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.indicators.UDIndicatorBuilder;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class NewUDIndicatorWizard extends AbstractWizard {

    private Expression expression;

    private NewUDIndicatorWizardPage1 mPage1;

    private NewUDIndicatorWizardPage2 mPage2;

    private UDIndicatorParameter parameter;

    public NewUDIndicatorWizard(UDIndicatorParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void addPages() {
        String s = DefaultMessagesImpl.getString("NewUDIndicatorWizard.userDefinedIndicator"); //$NON-NLS-1$

        mPage1 = new NewUDIndicatorWizardPage1();
        mPage1.setTitle(s + DefaultMessagesImpl.getString("NewUDIndicatorWizard.createPage1_2")); //$NON-NLS-1$
        mPage1.setDescription(DefaultMessagesImpl.getString("NewUDIndicatorWizard.defineProp")); //$NON-NLS-1$
        mPage1.setPageComplete(false);

        mPage2 = new NewUDIndicatorWizardPage2();
        mPage2.setTitle(s + DefaultMessagesImpl.getString("NewUDIndicatorWizard.createPage2_2")); //$NON-NLS-1$
        mPage2.setDescription(DefaultMessagesImpl.getString("NewUDIndicatorWizard.defineUDIndicator")); //$NON-NLS-1$

        addPage(mPage1);
        addPage(mPage2);
    }

    public TypedReturnCode<IFile> createAndSaveCWMFile(ModelElement cwmElement) {
        IndicatorDefinition indicatorDefinition = (IndicatorDefinition) cwmElement;
        indicatorDefinition.getSqlGenericExpression().add(getExpression());
        UDIHelper.setUDICategory(indicatorDefinition, DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory());
        IFolder folder = parameter.getFolderProvider().getFolderResource();
        return ElementWriterFactory.getInstance().createUDIndicatorWriter().create(indicatorDefinition, folder);
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

    public Expression getExpression() {
        if (expression == null) {
            expression = CoreFactory.eINSTANCE.createExpression();
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
            if (getParameter().getExpression() != null && !"".equals(getParameter().getExpression().trim())) {
                return mPage1.isPageComplete() && mPage2.isPageComplete();
            }
        }
        return false;
    }
}
