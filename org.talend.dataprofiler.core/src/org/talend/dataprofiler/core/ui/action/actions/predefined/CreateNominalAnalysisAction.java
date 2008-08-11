// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewLabelProvider;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class CreateNominalAnalysisAction extends AbstractPredefinedAnalysisAction {

    public CreateNominalAnalysisAction() {
        super("Nominal value analysis", null);
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

        for (TdColumn column : getColumns()) {
            if (!Java2SqlType.isTextInSQL(column.getJavaType())) {
                tempList.add(column);
            }
        }

        if (!tempList.isEmpty()) {
            ElementListSelectionDialog dialog = new ElementListSelectionDialog(null, new DQRepositoryViewLabelProvider());
            dialog.setElements(tempList.toArray());
            dialog.setTitle("Data type warning!");
            dialog.setMessage("The following columns do not seem to be of nominal type.\n"
                    + "Here is the list of non textual columns. \n"
                    + "If you continue, these columns will be marked as of nominal type\n"
                    + "and you will be allowed to run nominal analyses on these columns.\n" + "Do you still want to continue? ");
            dialog.setSize(80, 20);
            dialog.create();

            if (Window.OK == dialog.open()) {
                // TODO zqin get the column and change their datamining type to "Nominal"
                // use MetadataHelper
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    protected WizardDialog getPredefinedDialog() {

        return getStandardAnalysisWizardDialog();
    }

}
