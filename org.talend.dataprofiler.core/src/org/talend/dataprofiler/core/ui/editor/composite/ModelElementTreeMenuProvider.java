// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.composite;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IDatabaseJobService;
import org.talend.dataprofiler.core.service.IJobService;
import org.talend.dataprofiler.core.ui.action.actions.TdAddTaskAction;
import org.talend.dataprofiler.core.ui.action.actions.predefined.PreviewColumnAction;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC yyi class global comment. Detailled comment
 */
public abstract class ModelElementTreeMenuProvider {

    private static Logger log = Logger.getLogger(ModelElementTreeMenuProvider.class);

    protected Tree tree;

    public ModelElementTreeMenuProvider(Tree tree) {
        this.tree = tree;
    }

    /**
     * DOC qzhang Comment method "createTreeMenu".
     * 
     * @param newTree
     * @param containEdit
     */
    public void createTreeMenu() {
        Menu oldMenu = tree.getMenu();
        if (oldMenu != null && !oldMenu.isDisposed()) {
            oldMenu.dispose();
        }
        Menu menu = new Menu(tree);

        if (isSelectedColumn(tree.getSelection())) {
            MenuItem previewMenuItem = new MenuItem(menu, SWT.CASCADE);
            previewMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.previewDQElement")); //$NON-NLS-1$
            previewMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
            previewMenuItem.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected (org.eclipse .swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    previewSelectedElements(tree);
                }

            });

            MenuItem showLocationMenuItem = new MenuItem(menu, SWT.CASCADE);
            showLocationMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.showDQElement")); //$NON-NLS-1$
            showLocationMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
            showLocationMenuItem.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @seeorg.eclipse.swt.events.SelectionAdapter# widgetSelected(org.eclipse .swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    showSelectedElements(tree);
                }

            });
        }

        if (isSelectedIndicator(tree.getSelection()) && !isMdmSelected(tree.getSelection())) {
            // MOD 2009-01-04 mzhao
            MenuItem showQueryMenuItem = new MenuItem(menu, SWT.CASCADE);
            showQueryMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.viewQuery")); //$NON-NLS-1$
            showQueryMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
            showQueryMenuItem.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected (org.eclipse .swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    viewQueryForSelectedElement(tree);

                }
            });

        }

        if (isSelectedPatternIndicator(tree.getSelection())) {
            MenuItem editPatternMenuItem = new MenuItem(menu, SWT.CASCADE);
            editPatternMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.editPattern")); //$NON-NLS-1$
            editPatternMenuItem.setImage(ImageLib.getImage(ImageLib.PATTERN_REG));
            editPatternMenuItem.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @seeorg.eclipse.swt.events.SelectionAdapter# widgetSelected(org .eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    editPattern(tree);
                }

            });
        }
        // MOD zshen 2010-06-02 featrue 12919
        // judge to indicator whether is frequency
        if (isSelectedFrequencyIndicator(tree.getSelection())) {
            MenuItem editPatternMenuItem = new MenuItem(menu, SWT.CASCADE);
            editPatternMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.generateJob")); //$NON-NLS-1$
            editPatternMenuItem.setImage(ImageLib.getImage(ImageLib.ICON_PROCESS));
            editPatternMenuItem.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    final IDatabaseJobService service = (IDatabaseJobService) GlobalServiceRegister.getDefault().getService(
                            IJobService.class);
                    if (service != null) {
                        service.setIndicator(getSelectedIndicator(tree.getSelection()));
                        service.setAnalysis(getAnalysis2());
                        service.executeJob();
                    }
                }
            });
        }
        if (isSelectedUDIndicator(tree.getSelection())) {
            MenuItem editPatternMenuItem = new MenuItem(menu, SWT.CASCADE);
            editPatternMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.editUDIndicator")); //$NON-NLS-1$
            editPatternMenuItem.setImage(ImageLib.getImage(ImageLib.IND_DEFINITION));
            editPatternMenuItem.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @seeorg.eclipse.swt.events.SelectionAdapter# widgetSelected(org .eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    editUDIndicator(tree);
                }

            });
        }
        // add common menu to the tree
        MenuItem addTaskItem = new MenuItem(menu, SWT.CASCADE);
        addTaskItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.AddTask")); //$NON-NLS-1$
        addTaskItem.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addTaskItem.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org .eclipse .swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                TreeItem[] selection = tree.getSelection();
                if (selection.length > 0) {
                    TreeItem treeItem = selection[0];
                    ModelElementIndicator meIndicator = (ModelElementIndicator) treeItem
                            .getData(AbstractColumnDropTree.MODELELEMENT_INDICATOR_KEY);
                    ModelElement me = meIndicator.getModelElement();
                    ModelElement ana = getAnalysis2();
                    ana.setName(me.getName());
                    if (me instanceof ModelElement) {
                        (new TdAddTaskAction(tree.getShell(), ana)).run();
                    }
                }

            }
        });

        MenuItem deleteMenuItem = new MenuItem(menu, SWT.CASCADE);
        deleteMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.removeElement")); //$NON-NLS-1$
        deleteMenuItem.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        deleteMenuItem.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org .eclipse .swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedElements2(tree);
            }

        });

        tree.setMenu(menu);
    }

    /**
     * DOC yyi Comment method "removeSelectedElements".
     * 
     * @param tree
     */
    protected abstract void removeSelectedElements2(Tree tree);

    protected abstract Analysis getAnalysis2();

    private void editPattern(Tree tree) {
        TreeItem[] selection = tree.getSelection();
        if (selection.length > 0) {
            TreeItem treeItem = selection[0];
            IndicatorUnit indicatorUnit = (IndicatorUnit) treeItem.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY);
            PatternMatchingIndicator indicator = (PatternMatchingIndicator) indicatorUnit.getIndicator();
            Pattern pattern = indicator.getParameters().getDataValidDomain().getPatterns().get(0);
            IFolder patternFolder = ResourceManager.getPatternFolder();
            IFolder sqlPatternFolder = ResourceManager.getPatternSQLFolder();
            IFile file = PatternResourceFileHelper.getInstance().getPatternFile(pattern,
                    new IFolder[] { patternFolder, sqlPatternFolder });
            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            try {
                activePage.openEditor(new FileEditorInput(file), "org.talend.dataprofiler.core.ui.editor.pattern.PatternEditor"); //$NON-NLS-1$
            } catch (PartInitException e1) {
                log.error(e1, e1);
            }
        }
    }

    /**
     * DOC yyi Comment method "editUDIndicator" 2009-09-04.
     * 
     * @param tree
     */
    private void editUDIndicator(Tree tree) {
        TreeItem[] selection = tree.getSelection();
        if (selection.length > 0) {
            TreeItem treeItem = selection[0];
            IndicatorUnit indicatorUnit = (IndicatorUnit) treeItem.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY);
            UserDefIndicator indicator = (UserDefIndicator) indicatorUnit.getIndicator();
            IFile file = IndicatorResourceFileHelper.getInstance().getIndicatorFile(indicator.getIndicatorDefinition());
            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            try {
                activePage.openEditor(new FileEditorInput(file),
                        "org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditor"); //$NON-NLS-1$
            } catch (PartInitException e1) {
                log.error(e1, e1);
            }
        }
    }

    /**
     * DOC Zqin Comment method "previewSelectedElements".
     * 
     * @param newTree
     */
    private void previewSelectedElements(Tree newTree) {
        TreeItem[] items = newTree.getSelection();
        ModelElement[] mes = new ModelElement[items.length];

        for (int i = 0; i < items.length; i++) {
            ModelElementIndicator meIndicator = (ModelElementIndicator) items[i]
                    .getData(AbstractColumnDropTree.MODELELEMENT_INDICATOR_KEY);
            ModelElement me = meIndicator.getModelElement();
            mes[i] = me;
        }

        new PreviewColumnAction(mes).run();
    }

    /**
     * 
     * DOC mzhao Comment method "viewQueryForSelectedElement".
     * 
     * @param newTree
     */
    private void viewQueryForSelectedElement(Tree newTree) {
        TreeItem[] selection = newTree.getSelection();
        for (TreeItem item : selection) {
            ModelElementIndicator meIndicator = (ModelElementIndicator) item
                    .getData(AbstractColumnDropTree.MODELELEMENT_INDICATOR_KEY);
            ModelElement me = meIndicator.getModelElement();
            TdDataProvider dataprovider = ModelElementHelper.getTdDataProvider(me);
            IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY);
            DbmsLanguage dbmsLang = DbmsLanguageFactory.createDbmsLanguage(dataprovider);
            Expression expression = dbmsLang.getInstantiatedExpression(indicatorUnit.getIndicator());
            if (expression == null) {
                MessageDialogWithToggle
                        .openWarning(
                                null,
                                DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.Warn"), DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.NoQueryDefined")); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }

            CorePlugin.getDefault().openInSqlEditor(dataprovider, expression.getBody(), me.getName());
        }
    }

    /**
     * DOC Zqin Comment method "showSelectedElements".MOD 2009-01-07 mzhao.
     * 
     * @param newTree
     */
    private void showSelectedElements(Tree newTree) {
        TreeItem[] selection = newTree.getSelection();

        DQRespositoryView dqview = CorePlugin.getDefault().getRepositoryView();
        if (selection.length == 1) {
            try {
                ModelElementIndicator meIndicator = (ModelElementIndicator) selection[0]
                        .getData(AbstractColumnDropTree.MODELELEMENT_INDICATOR_KEY);
                ModelElement me = meIndicator.getModelElement();
                dqview.showSelectedElements(me);

            } catch (Exception e) {
                log.error(e, e);
            }
        }
    }

    private boolean isSelectedColumn(TreeItem[] items) {
        for (TreeItem item : items) {
            if (item.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY) != null
                    || item.getData(AbstractColumnDropTree.DATA_PARAM) != null) {
                return false;
            }
        }

        return true;
    }

    private boolean isSelectedIndicator(TreeItem[] items) {

        if (isSelectedColumn(items)) {
            return false;
        }

        for (TreeItem item : items) {
            if (item.getData(AbstractColumnDropTree.DATA_PARAM) != null) {
                return false;
            }
        }

        return true;
    }

    private boolean isSelectedPatternIndicator(TreeItem[] items) {
        if (!isSelectedIndicator(items)) {
            return false;
        }

        for (TreeItem item : items) {
            IndicatorUnit unit = (IndicatorUnit) item.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY);
            if (unit != null) {

                Indicator indicator = unit.getIndicator();
                if (!(indicator instanceof PatternMatchingIndicator)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 
     * DOC zshen Comment method "isSelectedFrequencyIndicator". judge to indicator whether is frequency
     * 
     * @param items
     * @return
     */
    private boolean isSelectedFrequencyIndicator(TreeItem[] items) {
        if (!isSelectedIndicator(items)) {
            return false;
        }

        for (TreeItem item : items) {
            IndicatorUnit unit = (IndicatorUnit) item.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY);
            if (unit != null) {

                Indicator indicator = unit.getIndicator();
                if (!(indicator instanceof FrequencyIndicator)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Indicator getSelectedIndicator(TreeItem[] items) {
        assert items.length == 1;
        if (!isSelectedIndicator(items)) {
            return null;
        }
        for (TreeItem item : items) {
            IndicatorUnit unit = (IndicatorUnit) item.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY);
            if (unit != null) {
                return unit.getIndicator();
            }
        }
        return null;
    }

    /**
     * DOC xqliu Comment method "isMdmSelected".
     * 
     * @param items
     * @return
     */
    private boolean isMdmSelected(TreeItem[] items) {
        for (TreeItem item : items) {
            Object data = item.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY);
            if (data != null) {
                if (data instanceof IndicatorUnit) {
                    IndicatorUnit iu = (IndicatorUnit) data;
                    return iu.isXmlElement();
                }
            }
        }
        return false;
    }

    /**
     * DOC yyi Comment method "isSelectedUDIndicator" 2009-09-04.
     * 
     * @param items
     * @return
     */
    private boolean isSelectedUDIndicator(TreeItem[] items) {
        if (!isSelectedIndicator(items)) {
            return false;
        }

        for (TreeItem item : items) {
            IndicatorUnit unit = (IndicatorUnit) item.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY);
            if (unit != null) {

                Indicator indicator = unit.getIndicator();
                if (!(indicator instanceof UserDefIndicator)) {
                    return false;
                }
            }
        }

        return true;
    }

}
