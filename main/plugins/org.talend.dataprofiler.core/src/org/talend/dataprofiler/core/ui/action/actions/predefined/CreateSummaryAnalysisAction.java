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
package org.talend.dataprofiler.core.ui.action.actions.predefined;

import org.eclipse.jface.wizard.WizardDialog;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sql.TalendTypeConvert;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class CreateSummaryAnalysisAction extends AbstractPredefinedAnalysisAction {

    /**
     * DOC Zqin CreateSummaryAnalysisAction constructor comment.
     * 
     */
    public CreateSummaryAnalysisAction() {
        super(DefaultMessagesImpl.getString("CreateSummaryAnalysisAction.summaryAnalysis"), null); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#getPredefinedColumnIndicator()
     */
    @Override
    protected ModelElementIndicator[] getPredefinedColumnIndicator() {
        IndicatorEnum[] allowedEnumes = new IndicatorEnum[3];
        allowedEnumes[0] = IndicatorEnum.BoxIIndicatorEnum;
        allowedEnumes[1] = IndicatorEnum.RowCountIndicatorEnum;
        allowedEnumes[2] = IndicatorEnum.NullCountIndicatorEnum;

        return composePredefinedColumnIndicator(allowedEnumes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#getPredefinedDialog()
     */
    @Override
    protected WizardDialog getPredefinedDialog() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#isAllowed()
     */
    @Override
    protected boolean isAllowed() {

        for (IRepositoryNode repositoryNode : getColumns()) {
            MetadataColumn column = ((MetadataColumnRepositoryObject) repositoryNode.getObject()).getTdColumn();

            int javaSQLType = TalendTypeConvert.convertToJDBCType(column.getTalendType());

            if (!Java2SqlType.isNumbericInSQL(javaSQLType)) {
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#preDo()
     */
    @Override
    protected boolean preDo() {

        return true;
    }

}
