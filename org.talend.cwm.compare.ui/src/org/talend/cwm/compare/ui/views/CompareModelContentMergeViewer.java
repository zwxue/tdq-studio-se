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

package org.talend.cwm.compare.ui.views;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.internal.ChangePropertyAction;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.compare.ui.AbstractCompareAction;
import org.eclipse.emf.compare.ui.ModelCompareInput;
import org.eclipse.emf.compare.ui.viewer.content.ModelContentMergeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.talend.cwm.compare.ui.actions.ReloadDatabaseAction;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.AbstractDatabaseFolderNode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
@SuppressWarnings("restriction")
public class CompareModelContentMergeViewer extends ModelContentMergeViewer {
	private static Logger log = Logger
			.getLogger(CompareModelContentMergeViewer.class);
	private Object selectedOjbect = null;

	public CompareModelContentMergeViewer(Composite parent,
			CompareConfiguration config, Object selObj) {
		super(parent, config);
		selectedOjbect = selObj;
	}

	@Override
	protected void createToolItems(ToolBarManager tbm) {

		// NEXT DIFF
		final Action nextDiff = new AbstractCompareAction(ResourceBundle
				.getBundle(BUNDLE_NAME), "action.NextDiff.") { //$NON-NLS-1$

			@Override
			public void run() {
				navigate(true);
			}
		};
		final ActionContributionItem nextDiffContribution = new ActionContributionItem(
				nextDiff);
		nextDiffContribution.setVisible(true);
		tbm.appendToGroup("navigation", nextDiffContribution); //$NON-NLS-1$
		// PREVIOUS DIFF
		final Action previousDiff = new AbstractCompareAction(ResourceBundle
				.getBundle(BUNDLE_NAME), "action.PrevDiff.") { //$NON-NLS-1$

			@Override
			public void run() {
				navigate(false);
			}
		};
		final ActionContributionItem previousDiffContribution = new ActionContributionItem(
				previousDiff);
		previousDiffContribution.setVisible(true);
		tbm.appendToGroup("navigation", previousDiffContribution); //$NON-NLS-1$

		// ~ MOD mzhao 2009-03-09 remove no necessity actions.
		IContributionItem[] icItems = tbm.getItems();
		for (IContributionItem conbItem : icItems) {

			if (conbItem instanceof ActionContributionItem) {
				// ChangePropertyAction
				IAction a = ((ActionContributionItem) conbItem).getAction();
				if (a != null && a instanceof ChangePropertyAction) {
					tbm.remove(conbItem);
					conbItem.dispose();
					continue;
				}
				// Action
				if (((ActionContributionItem) conbItem).getAction() != null
						&& ((ActionContributionItem) conbItem).getAction()
								.getActionDefinitionId() != null) {
					if (((ActionContributionItem) conbItem).getAction()
							.getActionDefinitionId().equals(
									COPY_LEFT_TO_RIGHT_ID)) {
						tbm.remove(conbItem);
						conbItem.dispose();
					}
				}
			}

		}
		tbm.update(true);
		// ~

	}

	@Override
	protected void copy(boolean leftToRight) {
		// First check dependencies.
		ModelElement modelElement = null;
		IFile resourceFile = null;
		// File
		if (selectedOjbect instanceof IFile) {
			TypedReturnCode<TdDataProvider> returnValue = PrvResourceFileHelper
					.getInstance().findProvider((IFile) selectedOjbect);
			modelElement = returnValue.getObject();
		} else {
			// Folder
			Package ctatlogSwtich = SwitchHelpers.PACKAGE_SWITCH
					.doSwitch(((AbstractDatabaseFolderNode) selectedOjbect)
							.getParent());

			if (ctatlogSwtich != null) {
				resourceFile = PrvResourceFileHelper.getInstance()
						.findCorrespondingFile(
								DataProviderHelper
										.getTdDataProvider(ctatlogSwtich));
				modelElement = DataProviderHelper
						.getTdDataProvider(ctatlogSwtich);
			}
			ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH
					.doSwitch(((AbstractDatabaseFolderNode) selectedOjbect)
							.getParent());
			if (columnSet != null) {
				resourceFile = PrvResourceFileHelper.getInstance()
						.findCorrespondingFile(
								DataProviderHelper.getDataProvider(columnSet));
				modelElement = DataProviderHelper.getDataProvider(columnSet);
			}
		}
		if (modelElement != null && resourceFile != null) {
			String titleMessage = DefaultMessagesImpl
					.getString("CompareModelContentMergeViewer.ImpactAnalyses");

			int showDialog = DeleteModelElementConfirmDialog
					.showElementImpactDialog(null,
							new ModelElement[] { modelElement }, titleMessage,
							titleMessage);
			if (showDialog == Window.OK) {
				EObjectHelper
						.removeDependencys(new IResource[] { resourceFile });
			} else {
				return;
			}
		}

		int diffItemsCount = ((ModelCompareInput) getInput()).getDiffAsList()
				.size();
		try {
			super.copy(leftToRight);
			// MOD mzhao 2009-03-11 copy from right to left.need reload the
			// currently selected element.
			if (!leftToRight && diffItemsCount > 0) {
				new ReloadDatabaseAction(selectedOjbect, null).run();
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

	}

	private static final String COPY_LEFT_TO_RIGHT_ID = "org.eclipse.compare.copyAllLeftToRight";
}
