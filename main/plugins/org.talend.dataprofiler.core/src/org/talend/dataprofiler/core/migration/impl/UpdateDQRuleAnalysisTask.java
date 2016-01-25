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
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.sql.IndicatorSqlFactory;
import org.talend.dataquality.indicators.sql.WhereRuleAideIndicator;
import org.talend.dataquality.indicators.sql.WhereRuleIndicator;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UpdateDQRuleAnalysisTask extends AbstractWorksapceUpdateTask {

    public Date getOrder() {
        return createDate(2012, 4, 25);
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    @Override
    protected boolean doExecute() throws Exception {
        // get all Analysis
        List<? extends ModelElement> allElement = AnaResourceFileHelper.getInstance().getAllElement();
        for (ModelElement me : allElement) {
            if (me instanceof Analysis) {
                Analysis ana = (Analysis) me;
                AnalysisType analysisType = AnalysisHelper.getAnalysisType(ana);
                // get the "BUSINESS_RULE" analysis
                if (AnalysisType.TABLE.equals(analysisType)) {
                    // get the Indicators which need to add AideIndicator
                    List<WhereRuleIndicator> needAddAideIndicators = getNeedAddAideIndicators(ana);
                    // add the AideIndicators
                    if (needAddAideIndicators.size() > 0) {
                        addAideIndicator(needAddAideIndicators, ana);
                    }
                }
            }
        }
        return true;
    }

    /**
     * DOC xqliu Comment method "addAideIndicator".
     * 
     * @param needAddAideIndicators
     * @param ana
     */
    private void addAideIndicator(List<WhereRuleIndicator> needAddAideIndicators, Analysis ana) {
        EList<Indicator> indicators = ana.getResults().getIndicators();
        for (WhereRuleIndicator wrIndicator : needAddAideIndicators) {
            // create WhereRuleAideIndicator according to WhereRuleIndicator
            WhereRuleAideIndicator wraIndicator = IndicatorSqlFactory.eINSTANCE.createWhereRuleAideIndicator();
            wraIndicator.setAnalyzedElement(wrIndicator.getAnalyzedElement());
            wraIndicator.setIndicatorDefinition(wrIndicator.getIndicatorDefinition());
            // add WhereRuleAideIndicator into the list
            indicators.add(wraIndicator);
        }

        URI uriItem = ana.eResource().getURI();
        File fileItem = null;
        if (uriItem.isPlatform()) {
            fileItem = WorkspaceUtils.ifileToFile(ModelElementHelper.getIFile(ana));
        } else {
            fileItem = new File(uriItem.toFileString());
        }

        File fileProp = WorkspaceUtils.ifileToFile(PropertyHelper.getPropertyFile(ana));
        Property property = PropertyHelper.getProperty(ana);
        Item item = property.getItem();

        // save AnalysisItem
        TDQAnalysisItem analysisItem = (TDQAnalysisItem) item;
        analysisItem.setAnalysis(ana);
        Resource itemResource = getResource(fileItem);
        EMFUtil.saveResource(itemResource);

        // save the property
        Resource propResource = getResource(fileProp);
        Property newProperty = (Property) EcoreUtil.getObjectByType(propResource.getContents(),
                PropertiesPackage.eINSTANCE.getProperty());

        newProperty.setAuthor(property.getAuthor());
        newProperty.setLabel(analysisItem.getAnalysis().getName());
        newProperty.setItem(item);

        item.setProperty(newProperty);

        propResource.getContents().clear();
        propResource.getContents().add(newProperty);
        propResource.getContents().add(item);
        propResource.getContents().add(item.getState());

        EMFUtil.saveResource(propResource);
    }

    /**
     * DOC xqliu Comment method "getNeedAddAideIndicators".
     * 
     * @param ana
     * @return
     */
    private List<WhereRuleIndicator> getNeedAddAideIndicators(Analysis ana) {
        List<WhereRuleIndicator> needAddAideIndicators = new ArrayList<WhereRuleIndicator>();

        AnalysisResult results = ana.getResults();
        if (results != null) {
            EList<Indicator> indicators = results.getIndicators();
            if (indicators != null) {
                for (Indicator ind : indicators) {
                    if (needAddAideIndicator(ind, indicators)) {
                        needAddAideIndicators.add((WhereRuleIndicator) ind);
                    }
                }
            }
        }
        return needAddAideIndicators;
    }

    /**
     * DOC xqliu Comment method "needAddAideIndicator".
     * 
     * @param ind
     * @param indicators
     * @return
     */
    private boolean needAddAideIndicator(Indicator ind, EList<Indicator> indicators) {
        boolean result = true;
        if (ind instanceof WhereRuleIndicator) {
            // MOD msjian TDQ-7186 2013-6-3: fix a NPE(get name is null)
            for (Indicator indd : indicators) {
                if (indd instanceof WhereRuleAideIndicator) {
                    if (ind.getIndicatorDefinition() == indd.getIndicatorDefinition()) {
                        result = false;
                        break;
                    }
                }
            }
            // TDQ-7186~
        } else {
            result = false;
        }
        return result;
    }
}
