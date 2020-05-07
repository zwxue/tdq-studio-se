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
package org.talend.dq;

import java.util.ArrayList;
import java.util.List;

import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.context.link.ContextLinkService;
import org.talend.core.model.context.link.IItemContextLinkService;
import org.talend.core.model.properties.Item;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.reports.TdReport;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;

/**
 * created by msjian on 2020年5月6日
 * this class is used for save DQ item's context link
 *
 */
public class DQItemContextLinkService implements IItemContextLinkService {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.context.link.IItemContextLinkService#accept(org.talend.core.model.properties.Item)
     */
    @Override
    public boolean accept(Item item) {
        if (item instanceof TDQAnalysisItem || item instanceof TDQReportItem) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.core.model.context.link.IItemContextLinkService#saveItemLink(org.talend.core.model.properties.Item)
     */
    @Override
    public boolean saveItemLink(Item item) throws PersistenceException {
        if (item instanceof TDQAnalysisItem) {
            TDQAnalysisItem tdqAnalysisItem = (TDQAnalysisItem) item;
            List<ContextType> contextTypeList = new ArrayList<ContextType>();
            if (tdqAnalysisItem.getAnalysis() != null) {
                contextTypeList.addAll(tdqAnalysisItem.getAnalysis().getContextType());
            }
            return ContextLinkService.saveContextLink(contextTypeList, item.getProperty().getId());
        } else if (item instanceof TDQReportItem) {
            TDQReportItem tdqReportItem = (TDQReportItem) item;
            List<ContextType> contextTypeList = new ArrayList<ContextType>();
            if (tdqReportItem.getReport() != null) {
                contextTypeList.addAll(((TdReport) tdqReportItem.getReport()).getContext());
            }
            return ContextLinkService.saveContextLink(contextTypeList, item.getProperty().getId());
        }
        return false;
    }

}
