// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.commons.emf;

import org.eclipse.emf.ecore.EObject;
import org.talend.core.AbstractDQModelService;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQJrxmlItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.properties.TDQSourceFileItem;


/**
 * DOC klliu  class global comment. Detailled comment
 */
public class InitialDQModelPackagesService extends AbstractDQModelService {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.repository.utils.AbstractDQModelService#initTDQEMFResource()
     */
    @Override
    public void initTDQEMFResource() {
        EMFUtil.initialize();
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ITDQItemService#getTDQRepObjType(org.talend.core.model.properties.Item)
     */
    public ERepositoryObjectType getTDQRepObjType(Item item) {
        return (ERepositoryObjectType) new org.talend.dataquality.properties.util.PropertiesSwitch() {

            // MOD mzhao feature 13114, 2010-05-19
            @Override
            public Object caseTDQAnalysisItem(TDQAnalysisItem object) {
                return ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT;
            }

            @Override
            public Object caseTDQBusinessRuleItem(TDQBusinessRuleItem object) {
                return ERepositoryObjectType.TDQ_BUSINESSRULE_ELEMENT;
            }

            @Override
            public Object caseTDQIndicatorDefinitionItem(TDQIndicatorDefinitionItem object) {
                return ERepositoryObjectType.TDQ_INDICATOR_ELEMENT;
            }

            @Override
            public Object caseTDQPatternItem(TDQPatternItem object) {
                return ERepositoryObjectType.TDQ_PATTERN_ELEMENT;
            }

            @Override
            public Object caseTDQReportItem(TDQReportItem object) {
                return ERepositoryObjectType.TDQ_REPORT_ELEMENT;
            }

            @Override
            public Object caseTDQJrxmlItem(TDQJrxmlItem object) {
                return ERepositoryObjectType.TDQ_JRAXML_ELEMENT;
            }

            @Override
            public Object caseTDQSourceFileItem(TDQSourceFileItem object) {
                return ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT;
            }

            public Object defaultCase(EObject object) {
                return null;
            }
        }.doSwitch(item);
    }

}
