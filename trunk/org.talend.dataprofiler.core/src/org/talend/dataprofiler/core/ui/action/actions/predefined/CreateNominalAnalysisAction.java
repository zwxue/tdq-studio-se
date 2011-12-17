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
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewLabelProvider;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sql.TalendTypeConvert;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class CreateNominalAnalysisAction extends AbstractPredefinedAnalysisAction {

    private boolean addTextIndicator;

    public CreateNominalAnalysisAction() {
        super(DefaultMessagesImpl.getString("CreateNominalAnalysisAction.nominalAnalysis"), null); //$NON-NLS-1$
    }

    @Override
    public ModelElementIndicator[] getPredefinedColumnIndicator() {

        // MOD qiongli 2011-3-31,bug 19810.if contain none-nominal data,don't add TextIndicator.
        List<IndicatorEnum> allwedEnumeLs = new ArrayList<IndicatorEnum>();
        allwedEnumeLs.add(IndicatorEnum.CountsIndicatorEnum);
        allwedEnumeLs.add(IndicatorEnum.FrequencyIndicatorEnum);
        if (addTextIndicator) {
            allwedEnumeLs.add(IndicatorEnum.TextIndicatorEnum);
        }
        IndicatorEnum[] allwedEnumeArray = (IndicatorEnum[]) allwedEnumeLs.toArray(new IndicatorEnum[allwedEnumeLs.size()]);

        return composePredefinedColumnIndicator(allwedEnumeArray);
    }

    @Override
    protected boolean isAllowed() {

        return true;
    }

    @Override
    protected boolean preDo() {
        List<MetadataColumn> tempList = new ArrayList<MetadataColumn>();
        addTextIndicator = true;
        for (IRepositoryNode repositoryNode : getColumns()) {
            MetadataColumn column = ((MetadataColumnRepositoryObject) repositoryNode.getObject()).getTdColumn();
            int javaSQLType = TalendTypeConvert.convertToJDBCType(column.getTalendType());
            if (!Java2SqlType.isTextInSQL(javaSQLType)) {
                tempList.add(column);
                addTextIndicator = false;
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
                for (MetadataColumn column : tempList) {
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
