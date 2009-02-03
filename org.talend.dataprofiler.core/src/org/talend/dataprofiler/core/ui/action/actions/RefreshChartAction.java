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
package org.talend.dataprofiler.core.ui.action.actions;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnCorrelationNominalAndIntervalMasterPage;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataquality.analysis.AnalysisType;

/**
 * DOC zqin class global comment. Detailled comment MOD mzhao 2009-02-03 Handle
 * case of cheat sheats.
 */
public class RefreshChartAction extends Action implements ICheatSheetAction {

	public RefreshChartAction() {
		super(DefaultMessagesImpl.getString("RefreshChartAction.refreshChart")); //$NON-NLS-1$
		setImageDescriptor(ImageLib
				.getImageDescriptor(ImageLib.SECTION_PREVIEW));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
	 * org.eclipse.ui.cheatsheets.ICheatSheetManager)
	 */
	public void run(String[] params, ICheatSheetManager manager) {
		if (params == null || params.length == 0) {
			return;
		}
		Integer analysisCatigory = null;
		if (NumberUtils.isNumber(params[0])) {
			analysisCatigory = NumberUtils.toInt(params[0]);
		}
		if (analysisCatigory != null) {
			AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			if (editor == null) {
				return;
			}
			switch (analysisCatigory) {
			case AnalysisType.MULTIPLE_COLUMN_VALUE:
				ColumnMasterDetailsPage page = (ColumnMasterDetailsPage) editor
						.getMasterPage();
				if (page.isDirty()) {
					try {
						page.saveAnalysis();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				page.refreshChart();
				break;
			case AnalysisType.COLUMN_CORRELATION_VALUE:
				ColumnCorrelationNominalAndIntervalMasterPage page2 = (ColumnCorrelationNominalAndIntervalMasterPage) editor
						.getMasterPage();
				if (page2.isDirty()) {
					try {
						page2.saveAnalysis();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				page2.refreshChart();
				break;
			default:
				break;
			}

		}
	}

	@Override
	public void run() {
	}

}
