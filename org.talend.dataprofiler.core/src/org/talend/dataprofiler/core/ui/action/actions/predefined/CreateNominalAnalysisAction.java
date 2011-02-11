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
package org.talend.dataprofiler.core.ui.action.actions.predefined;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewLabelProvider;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class CreateNominalAnalysisAction extends AbstractPredefinedAnalysisAction {

    public CreateNominalAnalysisAction() {
        super(DefaultMessagesImpl.getString("CreateNominalAnalysisAction.nominalAnalysis"), null); //$NON-NLS-1$
    }

    @Override
    public ColumnIndicator[] getPredefinedColumnIndicator() {

        IndicatorEnum[] allwedEnumes = new IndicatorEnum[3];
        allwedEnumes[0] = IndicatorEnum.CountsIndicatorEnum;
        allwedEnumes[1] = IndicatorEnum.TextIndicatorEnum;
        allwedEnumes[2] = IndicatorEnum.FrequencyIndicatorEnum;

        return composePredefinedColumnIndicator(allwedEnumes);
    }

    @Override
    protected boolean isAllowed() {

        return true;
    }

    @Override
    protected boolean preDo() {
        List<TdColumn> tempList = new ArrayList<TdColumn>();

        for (IRepositoryNode repositoryNode : getColumns()) {
            TdColumn column = (TdColumn) ((MetadataColumnRepositoryObject) repositoryNode.getObject()).getTdColumn();
            if (!Java2SqlType.isTextInSQL(column.getSqlDataType().getJavaDataType())) {
                tempList.add(column);
            }
        }

        if (!tempList.isEmpty()) {
            ElementListSelectionDialog dialog = new ElementListSelectionDialog(null, new DQRepositoryViewLabelProvider());
            dialog.setElements(tempList.toArray());
            dialog.setTitle(DefaultMessagesImpl.getString("CreateNominalAnalysisAction.dataTypeWarning")); //$NON-NLS-1$
            dialog.setMessage(DefaultMessagesImpl.getString("CreateNominalAnalysisAction.string")); //$NON-NLS-1$
            dialog.setSize(80, 20);
            dialog.create();

            if (Window.OK == dialog.open()) {
                // zqin get the column and change their datamining type to "Nominal"
                // use MetadataHelper
                for (TdColumn column : tempList) {
                    MetadataHelper.setDataminingType(DataminingType.NOMINAL, column);
                }
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    protected WizardDialog getPredefinedDialog() {

        return null;
    }

}
