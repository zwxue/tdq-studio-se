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
package org.talend.dataprofiler.core.ui.action.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.helper.FolderNodeHelper;
import org.talend.dataprofiler.core.ui.dialog.AnalyzedColumnSetsSynDialog;
import org.talend.dataprofiler.core.ui.dialog.AnalyzedColumnsSynDialog;
import org.talend.dataprofiler.core.ui.dialog.AnalyzedElementSynDialog;
import org.talend.dataprofiler.core.ui.dialog.AnalyzedPackageSynDialog;
import org.talend.dataprofiler.core.ui.dialog.AnalyzedElementSynDialog.SynTreeModel;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class ChangeConnectionAction extends Action implements ICheatSheetAction {
	private static Logger log = Logger.getLogger(ChangeConnectionAction.class);
	private TdDataProvider oldDataProvider;
	private TdDataProvider newDataProvider;
	private Analysis synAnalysis = null;
	private AnalyzedElementSynDialog anaEleSynDialog = null;
	private ReturnCode changeActionStatus;

	public ChangeConnectionAction(AbstractAnalysisMetadataPage masterPage,
			TdDataProvider tdProvider) {
		this.newDataProvider = (TdDataProvider) masterPage.getConnCombo()
				.getData(masterPage.getConnCombo().getSelectionIndex() + "");
		this.oldDataProvider = tdProvider;
		this.synAnalysis = masterPage.getAnalysis();
		changeActionStatus = new ReturnCode(Boolean.FALSE);
	}

	public ChangeConnectionAction(TdDataProvider oldDataProvider,
			TdDataProvider newDataProvider, Analysis analysis) {
		this.oldDataProvider = oldDataProvider;
		this.newDataProvider = newDataProvider;
		this.synAnalysis = analysis;
		changeActionStatus = new ReturnCode(Boolean.FALSE);
	}

	public void run(String[] params, ICheatSheetManager manager) {
		run();
	}

	@Override
	public void run() {
		try {
			changeActionStatus = changedDatabaseConnection();
		} catch (ReloadCompareException e) {
			log.error(e, e);
		}
	}

	private ReturnCode changedDatabaseConnection()
			throws ReloadCompareException {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();

		final EList<ModelElement> analyzedElements = synAnalysis.getContext()
				.getAnalysedElements();
		if (analyzedElements == null || analyzedElements.size() == 0) {
			return new ReturnCode(Boolean.TRUE);
		}

		// Open synchronized dialog.
		boolean retCode = MessageDialog
				.openQuestion(
						shell,
						"Change connection ?",
						"This action may cause asynchronization problems of analyzed elements, Do you want to continue ?");
		if (retCode) {

			if (analyzedElements.get(0) instanceof Column) {
				anaEleSynDialog = new AnalyzedColumnsSynDialog(shell,
						synAnalysis, newDataProvider, analyzedElements);
			} else if (analyzedElements.get(0) instanceof ColumnSet) {
				anaEleSynDialog = new AnalyzedColumnSetsSynDialog(shell,
						synAnalysis, newDataProvider, analyzedElements);
			} else if (analyzedElements.get(0) instanceof Package) {
				anaEleSynDialog = new AnalyzedPackageSynDialog(shell,
						synAnalysis, newDataProvider, analyzedElements);
			}

			final List<SynTreeModel> treeModelLs = anaEleSynDialog
					.getSynInputModel();
			if (treeModelLs != null && treeModelLs.size() > 0) {
				// Make attempt to reload from db before showing asyned
				// message.
				boolean isReload = MessageDialog
						.openQuestion(
								shell,
								"Reload from database ?",
								"It seems that there exist some analyzed element(s) are asynchronous,Do you want to reload them(it) from database ?");
				if (isReload) {
					ModelElement newDataProviderModel = treeModelLs.get(0)
							.getNewDataProvElement();
					if (newDataProviderModel != null
							&& (newDataProviderModel instanceof ColumnSet || newDataProviderModel instanceof Package)) {
						if (newDataProviderModel instanceof Package) {

							IRunnableWithProgress op = new IRunnableWithProgress() {
								public void run(IProgressMonitor monitor)
										throws InvocationTargetException,
										InterruptedException {
									try {
										reloadByColumnSetFolderLevel(
												treeModelLs, anaEleSynDialog,
												newDataProvider);

										if (analyzedElements.get(0) instanceof Column) {
											// Reload column folder
											reloadByColumnFolderLevel(
													treeModelLs,
													anaEleSynDialog,
													newDataProvider);
										}
									} catch (ReloadCompareException e) {
										log.error(e, e);
									}
								}

							};

							try {
								ProgressUI.popProgressDialog(op);
							} catch (InvocationTargetException e) {
								log.error(e, e);
							} catch (InterruptedException e) {
								log.error(e, e);
							}

						} else if (newDataProviderModel instanceof ColumnSet) {
							IRunnableWithProgress op = new IRunnableWithProgress() {
								public void run(IProgressMonitor monitor)
										throws InvocationTargetException,
										InterruptedException {
									try {
									    reloadByColumnFolderLevel(treeModelLs,
												anaEleSynDialog,
												newDataProvider);
									} catch (ReloadCompareException e) {
										log.error(e, e);
									}
								}

							};

							try {
								ProgressUI.popProgressDialog(op);
							} catch (InvocationTargetException e) {
								log.error(e, e);
							} catch (InterruptedException e) {
								log.error(e, e);
							}

						}
					}

				}

			}
			// Open asyned dialog.
			if (treeModelLs != null && treeModelLs.size() > 0) {
				// Open confirmation dialog to see whether user want to
				// continue or not.
				int returnCode = anaEleSynDialog.open();
				if (returnCode != Window.OK) {
					return new ReturnCode(Boolean.FALSE);
				}
			}
			// Synchronize analyzed elements.
			boolean isExistSynedElement = synAnalyzedElements(anaEleSynDialog,
					synAnalysis, oldDataProvider, newDataProvider);
			// Add new dependencies.
			if (isExistSynedElement) {
				DependenciesHandler.getInstance().setDependencyOn(synAnalysis,
						newDataProvider);
				PrvResourceFileHelper.getInstance().save(newDataProvider);
			}
			// Refresh analysis editor viewer.
			AnaResourceFileHelper.getInstance().save(synAnalysis);
		} else {
			return new ReturnCode(Boolean.FALSE);
		}
		return new ReturnCode(Boolean.TRUE);
	}

	private void reloadByColumnSetFolderLevel(List<SynTreeModel> treeModelLs,
			AnalyzedElementSynDialog anaEleSynDialog, TdDataProvider newDataProv)
			throws ReloadCompareException {
		ModelElement oldDataProviderModel = treeModelLs.get(0).getOldDataProvElement();
		// Reload columnSet folder
        ColumnSet columnset = null;
        if (oldDataProviderModel instanceof ColumnSet) {
            columnset = (ColumnSet) oldDataProviderModel;
        } else if (oldDataProviderModel instanceof Column) {
            columnset = ColumnHelper.getColumnSetOwner((Column) oldDataProviderModel);
        } else {
            return;
        }
		
		IFolderNode reloadFolder = FolderNodeHelper.getFolderNode(
		        treeModelLs.get(0).getNewDataProvElement(),
		        columnset);
		if (reloadFolder != null) {
			IComparisonLevel creatComparisonLevel = ComparisonLevelFactory
					.creatComparisonLevel(reloadFolder);
			newDataProv = creatComparisonLevel.reloadCurrentLevelElement();
			// Recompute after reload
			treeModelLs = anaEleSynDialog.getSynInputModel();
		}

	}

	private void reloadByColumnFolderLevel(List<SynTreeModel> treeModelLs,
			AnalyzedElementSynDialog anaEleSynDialog, TdDataProvider newDataProv)
			throws ReloadCompareException {
		ModelElement newDataProviderModel = treeModelLs.get(0)
				.getNewDataProvElement();
		// If schema or catalog changed, we did not load anymore.
		// Reload column folder
        ColumnSet columnset = null;
        if(newDataProviderModel instanceof ColumnSet){
            columnset=(ColumnSet)newDataProviderModel;
        } else if (newDataProviderModel instanceof Column) {
            columnset = ColumnHelper.getColumnSetOwner((Column) newDataProviderModel);
        } else {
            return;
        }
		IFolderNode reloadFolder = FolderNodeHelper.getFolderNode(
				 newDataProviderModel, columnset);

		if (reloadFolder != null) {
			IComparisonLevel creatComparisonLevel = ComparisonLevelFactory
					.creatComparisonLevel(reloadFolder);
			newDataProv = creatComparisonLevel.reloadCurrentLevelElement();
			// Recompute after reload
			treeModelLs = anaEleSynDialog.getSynInputModel();
		}
	}

	private boolean synAnalyzedElements(
			AnalyzedElementSynDialog anaEleSynDialog, Analysis synAnalysis,
			TdDataProvider oldDataProvider, TdDataProvider newDataProv) {
		// Change connection uuid.
		Map<ModelElement, ModelElement> synEleMap = anaEleSynDialog
				.getSynedEleMap();
		AnalysisBuilder anaBuilder = new AnalysisBuilder();
		anaBuilder.setAnalysis(synAnalysis);
		synAnalysis.getContext().setConnection(newDataProv);
		// Remove old dependencies.
		List<ModelElement> tempList = new ArrayList<ModelElement>();
		tempList.add(oldDataProvider);
		DependenciesHandler.getInstance().removeDependenciesBetweenModels(
				synAnalysis, tempList);
		PrvResourceFileHelper.getInstance().save(oldDataProvider);
		// Synchronize analysis result.
		EList<Indicator> indcList = synAnalysis.getResults().getIndicators();
		Indicator[] copiedIndArray = new Indicator[indcList.size()];
		System.arraycopy(indcList.toArray(), 0, copiedIndArray, 0, indcList
				.size());
		synAnalysis.getContext().getAnalysedElements().clear();
		synAnalysis.getResults().getIndicators().clear();

		boolean isExistSynedElement = false;
		for (Indicator indicator : copiedIndArray) {

			// Add new analyzed element contained in new
			// connection.
			if (indicator instanceof ColumnSetMultiValueIndicator) {
				ColumnSetMultiValueIndicator compositeInd = (ColumnSetMultiValueIndicator) indicator;
				ModelElement[] mes = new ModelElement[compositeInd
						.getAnalyzedColumns().size()];
				((ColumnSetMultiValueIndicator) indicator).getAnalyzedColumns()
						.toArray(mes);
				compositeInd.getAnalyzedColumns().clear();
				for (ModelElement me : mes) {
					if (synEleMap.get(me) != null) {
						TdColumn newColumn = (TdColumn) synEleMap.get(me);
						DataminingType dataminingType = MetadataHelper
								.getDataminingType((TdColumn) me);
						if (dataminingType == null) {
							dataminingType = MetadataHelper
									.getDefaultDataminingType(((TdColumn) me)
											.getJavaType());
						}
						MetadataHelper.setDataminingType(dataminingType,
								newColumn);
						compositeInd.getAnalyzedColumns().add(newColumn);
						anaBuilder.addElementToAnalyze(newColumn, indicator);
						isExistSynedElement = true;
					}
				}

			} else if (indicator instanceof ColumnsCompareIndicator) {
				// Correlation compare
				ColumnsCompareIndicator compInd = (ColumnsCompareIndicator) indicator;
				if ((compInd.getColumnSetA() == null || compInd.getColumnSetA()
						.size() == 0)
						|| (compInd.getColumnSetB() == null || compInd
								.getColumnSetB().size() == 0)) {
					return false;
				}
				// Column set(Columns)
				ModelElement[] mesA = new ModelElement[compInd.getColumnSetA()
						.size()];
				compInd.getColumnSetA().toArray(mesA);
				compInd.getColumnSetA().clear();
				for (ModelElement me : mesA) {
					if (synEleMap.get(me) != null) {
						TdColumn newColumn = (TdColumn) synEleMap.get(me);
						compInd.getColumnSetA().add(newColumn);
						anaBuilder.addElementToAnalyze(newColumn, indicator);
						isExistSynedElement = true;
					}
				}

				ModelElement[] mesB = new ModelElement[compInd.getColumnSetB()
						.size()];
				compInd.getColumnSetB().toArray(mesB);
				compInd.getColumnSetB().clear();
				for (ModelElement me : mesB) {
					if (synEleMap.get(me) != null) {
						TdColumn newColumn = (TdColumn) synEleMap.get(me);
						compInd.getColumnSetB().add(newColumn);
						anaBuilder.addElementToAnalyze(newColumn, indicator);
						isExistSynedElement = true;
					}
				}
				// Analyzed element(Table)
				ModelElement oldAnaEle = compInd.getAnalyzedElement();
				compInd.setAnalyzedElement(null);
				ColumnSet oldColSetA = ColumnHelper
						.getColumnSetOwner((Column) mesA[0]);
				ColumnSet oldColSetB = ColumnHelper
						.getColumnSetOwner((Column) mesB[0]);
				if (oldColSetA == oldAnaEle) {
					compInd
							.setAnalyzedElement(ColumnHelper
									.getColumnSetOwner((Column) synEleMap
											.get(mesA[0])));
				}
				if (oldColSetB == oldAnaEle) {
					compInd
							.setAnalyzedElement(ColumnHelper
									.getColumnSetOwner((Column) synEleMap
											.get(mesB[0])));
				}

			} else {
				ModelElement me = indicator.getAnalyzedElement();
				if (synEleMap.get(me) != null) {
					indicator.setAnalyzedElement(synEleMap.get(me));
					anaBuilder
							.addElementToAnalyze(synEleMap.get(me), indicator);
					isExistSynedElement = true;
				}
			}
		}
		return isExistSynedElement;
	}

	public ReturnCode getStatus() {
		return changeActionStatus;
	}

}
