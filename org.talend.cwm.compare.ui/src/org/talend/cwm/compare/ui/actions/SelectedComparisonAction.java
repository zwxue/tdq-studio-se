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
package org.talend.cwm.compare.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.ImageLib;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;

/**
 * DOC mzhao class global comment. Compare selected model elements action.
 */
public class SelectedComparisonAction extends Action {
	private static Logger log = Logger
			.getLogger(SelectedComparisonAction.class);

	private Object selectedObj1, selectedObj2;

	public SelectedComparisonAction(String menuText) {
		super(menuText);
		setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.UPDATE_IMAGE));

	}

	/**
	 * 
	 * DOC mzhao Refrech selected object when selection changed.
	 * 
	 * @param selectedObj1
	 * @param selectedObj2
	 */
	public void refreshSelectedObj(Object selectedObj1, Object selectedObj2) {
		this.selectedObj1 = selectedObj1;
		this.selectedObj2 = selectedObj2;
	}

	@Override
	public void run() {
		final Shell shell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();

		IRunnableWithProgress op = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				final IComparisonLevel creatComparisonLevel = ComparisonLevelFactory
						.creatComparisonLevel(selectedObj1, selectedObj2);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							creatComparisonLevel
									.popComparisonUI(new UiHandler());
						} catch (ReloadCompareException e) {
							log.error(e, e);
						}

					}
				});
			}
		};
		try {
			ProgressUI.popProgressDialog(op, shell);
			// ((DQRespositoryView)
			// CorePlugin.getDefault().findView(DQRespositoryView
			// .ID)).getCommonViewer().refresh();
		} catch (InvocationTargetException e) {
			MessageDialog
					.openInformation(
							shell,
							Messages
									.getString("PopComparisonUIAction.connectionFailure"), Messages.getString("PopComparisonUIAction.checkConnectionFailure") + e.getCause().getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			log.error(e, e);
		} catch (InterruptedException e) {
			log.error(e, e);
		}

	}

}
