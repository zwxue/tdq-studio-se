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
package org.talend.dataquality.record.linkage.ui.section.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataquality.record.linkage.ui.composite.DefaultSurvivorshipTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.sorter.KeyDefinitionTableViewerSorter;
import org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.DefaultSurvivorshipDefinition;
import org.talend.dataquality.rules.MatchRuleDefinition;

/**
 * created by HHB on 2013-8-23 Detailled comment
 *
 */
public class DefaultSurvivorshipDefinitionSection extends AbstractMatchAnaysisTableSection {

    private DefaultSurvivorshipTableComposite tableComposite;

    private MatchRuleDefinition matchRuleDef;


    /**
     * DOC HHB SurvivorshipDefinitionTableSection constructor comment.
     *
     * @param form
     * @param parent
     * @param style
     * @param toolkit
     * @param analysis
     */
    public DefaultSurvivorshipDefinitionSection(ScrolledForm form, Composite parent, FormToolkit toolkit) {
        super(form, parent, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED, toolkit, null);
        super.setIsNeedSubChart(false);
    }

    @Override
    protected void createSubChart(Composite sectionClient) {
        // do nothing
    }

    @Override
    protected Composite createSubContent(Composite sectionClient) {
        Composite ruleComp = toolkit.createComposite(sectionClient, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        ruleComp.setLayoutData(data);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        ruleComp.setLayout(gridLayout);
        tableComposite = new DefaultSurvivorshipTableComposite(ruleComp, SWT.NO_FOCUS);
        tableComposite.addPropertyChangeListener(this);
        tableComposite.setLayout(gridLayout);
        tableComposite.setLayoutData(data);
        tableComposite.createContent();
        section.setExpanded(true);
        tableComposite.serViewerSorter(new KeyDefinitionTableViewerSorter<DefaultSurvivorshipDefinition>(this.matchRuleDef
                .getDefaultSurvivorshipDefinitions()));
        initTableInput();

        return ruleComp;
    }


    private void initTableInput() {

        tableComposite.setInput(matchRuleDef.getDefaultSurvivorshipDefinitions());
    }

    @Override
    protected String getSectionName() {
        return MatchAnalysisConstant.SURVIVIORSHIP_DEFAULT_DEFINITION_SECTION_NAME;
    }

    @Override
    public void refreshChart() {
        // do nothing
    }

    @Override
    public Boolean isKeyDefinitionAdded(String columnName) throws Exception {
        return false;
    }

    public void setMatchRuleDef(MatchRuleDefinition matchRuleDef) {
        this.matchRuleDef = matchRuleDef;
    }

    @Override
    public void addTableItem() {
        tableComposite.addKeyDefinition(StringUtils.EMPTY, matchRuleDef.getDefaultSurvivorshipDefinitions());
        listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, true, false);
    }

    @Override
    public void removeTableItem() {
        boolean success = false;
        ISelection selectItems = tableComposite.getSelectItems();
        if (selectItems instanceof StructuredSelection) {
            Iterator<DefaultSurvivorshipDefinition> iterator = ((StructuredSelection) selectItems).iterator();
            while (iterator.hasNext()) {
                DefaultSurvivorshipDefinition next = iterator.next();
                tableComposite.removeKeyDefinition(next, matchRuleDef);
                success = true;
            }
            if (success) {
                listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, true, false);
            }
        }
    }

    /**
     * Getter for defaultSurvivorshipKeys.
     *
     * @return the defaultSurvivorshipKeys
     */
    public List<DefaultSurvivorshipDefinition> getDefaultSurvivorshipKeys() {
        List<DefaultSurvivorshipDefinition> defaultSurvivorshipDefKeys = new ArrayList<DefaultSurvivorshipDefinition>();
        defaultSurvivorshipDefKeys.addAll(EcoreUtil.copyAll(this.matchRuleDef.getDefaultSurvivorshipDefinitions()));
        return defaultSurvivorshipDefKeys;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#moveDownTableItem()
     */
    @Override
    public void moveDownTableItem() {
        ISelection selectItems = tableComposite.getSelectItems();
        if (selectItems instanceof StructuredSelection) {
            if (selectItems.isEmpty()) {
                return;
            }
            List<DefaultSurvivorshipDefinition> currentElements = this.matchRuleDef.getDefaultSurvivorshipDefinitions();
            List<DefaultSurvivorshipDefinition> defaultSurvivorshipKeyDefinitionlist = ((StructuredSelection) selectItems)
                    .toList();
            for (int index = defaultSurvivorshipKeyDefinitionlist.size() - 1; 0 <= index; index--) {
                if (!isSameWithCurrentModel(
                        currentElements.get(currentElements.size() - defaultSurvivorshipKeyDefinitionlist.size() + index),
                        defaultSurvivorshipKeyDefinitionlist.get(index))) {
                    continue;
                }
                DefaultSurvivorshipDefinition next = defaultSurvivorshipKeyDefinitionlist.get(index);
                tableComposite.moveDownKeyDefinition(next, currentElements);
            }
            tableComposite.selectAllItem(((StructuredSelection) selectItems).toList());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#moveUpTableItem()
     */
    @Override
    public void moveUpTableItem() {
        ISelection selectItems = tableComposite.getSelectItems();
        if (selectItems instanceof StructuredSelection) {
            if (selectItems.isEmpty()) {
                return;
            }
            List<DefaultSurvivorshipDefinition> currentElements = this.matchRuleDef.getDefaultSurvivorshipDefinitions();
            List<DefaultSurvivorshipDefinition> defaultSurvivorshipKeyDefinitionlist = ((StructuredSelection) selectItems)
                    .toList();
            for (int index = 0; index < defaultSurvivorshipKeyDefinitionlist.size(); index++) {
                if (!isSameWithCurrentModel(currentElements.get(index), defaultSurvivorshipKeyDefinitionlist.get(index))) {
                    continue;
                }
                DefaultSurvivorshipDefinition next = defaultSurvivorshipKeyDefinitionlist.get(index);
                tableComposite.moveUpKeyDefinition(next, currentElements);
            }
            tableComposite.selectAllItem(((StructuredSelection) selectItems).toList());
        }
    }

}
