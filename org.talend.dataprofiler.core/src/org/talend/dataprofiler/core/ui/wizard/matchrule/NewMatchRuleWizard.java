// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.matchrule;

import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.core.model.properties.Item;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.dqrules.DQRuleEditor;
import org.talend.dataprofiler.core.ui.editor.matchrule.MatchRuleItemEditorInput;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.analysis.parameters.DQMatchRuleParameter;
import org.talend.dq.dqrule.MatchRuleBuilder;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.MatchRuleDefinitionWriter;
import org.talend.resource.EResourceConstant;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;


/**
 * created by zshen on Aug 19, 2013
 * Detailled comment
 *
 */
public class NewMatchRuleWizard extends AbstractWizard {

    private NewMatchRuleMetadataPage newMatchRuleMetadataPage = null;


    private DQMatchRuleParameter parameter;

    private MatchRuleDefinition matchRule = null;

    private boolean isExport = false;

    public NewMatchRuleWizard(DQMatchRuleParameter parameter) {
        this.parameter = parameter;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        newMatchRuleMetadataPage = new NewMatchRuleMetadataPage();
        this.addPage(newMatchRuleMetadataPage);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.ui.wizard.ICWMResouceAdapter#initCWMResourceBuilder()
     */
    public ModelElement initCWMResourceBuilder() {
        if (matchRule != null) {
            MatchRuleDefinition copiedMatchRuleDefinition = EcoreUtil.copy(matchRule);
            copiedMatchRuleDefinition.setName(parameter.getName());
            return copiedMatchRuleDefinition;
        }
        MatchRuleBuilder ruleBuilder = new MatchRuleBuilder();
        boolean ruleInitialized = ruleBuilder.initializeDqRuleBuilder(parameter.getName());
        if (ruleInitialized) {
            return ruleBuilder.getMatchRule();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.ui.wizard.ICWMResouceAdapter#createAndSaveCWMFile(orgomg.cwm.objectmodel.core.
     * ModelElement)
     */
    public TypedReturnCode<Object> createAndSaveCWMFile(ModelElement repositoryObject) {
        MatchRuleDefinition newMatchRule = (MatchRuleDefinition) repositoryObject;

        TaggedValueHelper.setValidStatus(true, newMatchRule);
        
        // Added for export an match rule created by the match analysis, which need to replace the rules in createdItem,
        MatchRuleDefinitionWriter matchRuleWriter = ElementWriterFactory.getInstance().createdMatchRuleWriter();
        if(isExport){
            matchRuleWriter.copy(newMatchRule, this.matchRule);
        }
        IFolder folder = parameter.getFolderProvider().getFolderResource();
        TypedReturnCode<Object> createdItem = matchRuleWriter.create(newMatchRule, folder);

        return createdItem;

    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.ui.wizard.AbstractWizard#openEditor(org.talend.core.model.properties.Item)
     */
    @Override
    public void openEditor(Item item) {
        MatchRuleItemEditorInput matchRuleEditorInput = new MatchRuleItemEditorInput(item);
        CorePlugin.getDefault().openEditor(matchRuleEditorInput, DQRuleEditor.class.getName());
        // refresh the view
        CorePlugin.getDefault().refreshDQView(RepositoryNodeHelper.getLibrariesFolderNode(EResourceConstant.RULES_MATCHER));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.ui.wizard.AbstractWizard#getResourceFileMap()
     */
    @Override
    protected ResourceFileMap getResourceFileMap() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.ui.wizard.AbstractWizard#getParameter()
     */
    @Override
    protected ConnectionParameter getParameter() {
        return parameter;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.ui.wizard.AbstractWizard#getEditorName()
     */
    @Override
    protected String getEditorName() {
        return DQRuleEditor.class.getName();
    }

    public void setMatchRule(MatchRuleDefinition matchRule) {
        this.matchRule = matchRule;
        isExport = true;
    }

}
