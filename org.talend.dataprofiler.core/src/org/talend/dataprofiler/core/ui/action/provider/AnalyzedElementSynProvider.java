// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.provider;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;

/**
 * 
 * DOC mzhao feature 5887.
 */
public class AnalyzedElementSynProvider extends CommonActionProvider {

	protected static Logger log = Logger
			.getLogger(AnalyzedElementSynProvider.class);

	public void fillContextMenu(IMenuManager menu) {
		TreeSelection selection = (TreeSelection) this.getContext()
				.getSelection();
		if (!selection.isEmpty()) {
			Analysis fistAna = (Analysis) AnaResourceFileHelper.getInstance()
					.findAnalysis((IFile) selection.getFirstElement());
			try {
				// CreatReportByAnalysisAction createReportAction = new
				// CreatReportByAnalysisAction(fistAna.getName(), anaLs);
				//				menu.add(createReportAction); //$NON-NLS-1$
				AnalyzedElementSynAction analyzedEleSynAction = new AnalyzedElementSynAction(
						fistAna);
				menu.add(analyzedEleSynAction);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	/**
	 * 
	 * DOC mzhao analyzed element syn action.
	 */
	private class AnalyzedElementSynAction extends Action {
		private Analysis analysis = null;

		public AnalyzedElementSynAction(Analysis analysis) {
			super("Analyzed element synchronization");
			setImageDescriptor(ImageLib
					.getImageDescriptor(ImageLib.ANALYSIS_OBJECT));
			this.analysis = analysis;
		}

		@Override
		public void run() {

			// Open synchronized dialog.
			// AnalyzedElementSynDialog anaEleSynDialog = new
			// AnalyzedColumnsSynDialog(
			// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			// .getShell(), analysis);
			// int returnCode = anaEleSynDialog.open();
			// if (returnCode == Window.OK) {
			//
			// }

		}
	}
}
