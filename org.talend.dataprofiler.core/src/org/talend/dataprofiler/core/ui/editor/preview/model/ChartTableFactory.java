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
package org.talend.dataprofiler.core.ui.editor.preview.model;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginChecker;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.actions.CreatePatternAction;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IDatabaseJobService;
import org.talend.dataprofiler.core.service.IJobService;
import org.talend.dataprofiler.core.ui.utils.TableUtils;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.indicators.DatePatternFreqIndicator;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.PatternFreqIndicator;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.util.IndicatorsSwitch;
import org.talend.dq.analysis.explore.IDataExplorer;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.pattern.PatternTransformer;
import org.talend.resource.ResourceManager;

/**
 * DOC zqin class global comment. Detailled comment
 */
public final class ChartTableFactory {

    private ChartTableFactory() {
    }

    public static void addMenuAndTip(final TableViewer tbViewer, final IDataExplorer explorer, final Analysis analysis) {

        ExecutionLanguage currentEngine = analysis.getParameters().getExecutionLanguage();
        final boolean isJAVALanguage = ExecutionLanguage.JAVA == currentEngine;

        final Table table = tbViewer.getTable();
        final TdDataProvider tdDataProvider = (TdDataProvider) analysis.getContext().getConnection();
        final boolean isMDMAnalysis = ConnectionUtils.isMdmConnection(tdDataProvider);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                // MOD xqliu 2009-05-11 bug 6561
                if (table.getMenu() != null) {
                    table.getMenu().setVisible(false);
                }

                if (e.button == 3) {

                    StructuredSelection selection = (StructuredSelection) tbViewer.getSelection();
                    final ChartDataEntity dataEntity = (ChartDataEntity) selection.getFirstElement();
                    final Indicator indicator = dataEntity != null ? dataEntity.getIndicator() : null;

                    if (indicator != null && dataEntity != null) {
                        Menu menu = new Menu(table.getShell(), SWT.POP_UP);
                        table.setMenu(menu);
                        if (!isJAVALanguage) {
                            MenuItemEntity[] itemEntities = ChartTableMenuGenerator.generate(explorer, analysis, dataEntity);
                            for (final MenuItemEntity itemEntity : itemEntities) {
                                MenuItem item = new MenuItem(menu, SWT.PUSH);
                                item.setText(itemEntity.getLabel());
                                item.setImage(itemEntity.getIcon());

                                item.addSelectionListener(new SelectionAdapter() {

                                    @Override
                                    public void widgetSelected(SelectionEvent e) {
                                        String query = itemEntity.getQuery();
                                        String editorName = indicator.getName();
                                        CorePlugin.getDefault().runInDQViewer(tdDataProvider, query, editorName);
                                    }
                                });

                                if (isPatternFrequencyIndicator(indicator)) {
                                    MenuItem itemCreatePatt = new MenuItem(menu, SWT.PUSH);
                                    itemCreatePatt.setText(DefaultMessagesImpl
                                            .getString("ChartTableFactory.GenerateRegularPattern")); //$NON-NLS-1$
                                    itemCreatePatt.setImage(ImageLib.getImage(ImageLib.PATTERN_REG));
                                    itemCreatePatt.addSelectionListener(new SelectionAdapter() {

                                        @Override
                                        public void widgetSelected(SelectionEvent e) {
                                            DbmsLanguage language = DbmsLanguageFactory.createDbmsLanguage(analysis);
                                            PatternTransformer pattTransformer = new PatternTransformer(language);
                                            createPattern(analysis, itemEntity, pattTransformer);
                                        }

                                    });
                                }
                            }

                            if (PluginChecker.isTDCPLoaded() && !isMDMAnalysis) {
                                final IDatabaseJobService service = (IDatabaseJobService) GlobalServiceRegister.getDefault()
                                        .getService(IJobService.class);
                                if (service != null) {
                                    service.setIndicator(indicator);
                                    service.setAnalysis(analysis);

                                    MenuItem item = null;
                                    if (isDUDIndicator(indicator)) {
                                        item = new MenuItem(menu, SWT.PUSH);
                                        item.setText(DefaultMessagesImpl.getString("ChartTableFactory.RemoveDuplicate")); //$NON-NLS-1$
                                    } else if (isPatternMatchingIndicator(indicator)) {
                                        item = new MenuItem(menu, SWT.PUSH);
                                        item.setText("Generate jobs");
                                    }

                                    if (item != null) {
                                        item.setImage(ImageLib.getImage(ImageLib.ICON_PROCESS));
                                        item.addSelectionListener(getAdapter(service));
                                    }
                                }
                            }
                            // MOD by zshen feature 11574:add menu "Generate regular pattern" to date pattern
                        } else if (isDatePatternFrequencyIndicator(indicator) && isJAVALanguage) {
                            final DatePatternFreqIndicator dateIndicator = (DatePatternFreqIndicator) indicator;
                            MenuItem itemCreatePatt = new MenuItem(menu, SWT.PUSH);
                            itemCreatePatt.setText(DefaultMessagesImpl.getString("ChartTableFactory.GenerateRegularPattern")); //$NON-NLS-1$
                            itemCreatePatt.setImage(ImageLib.getImage(ImageLib.PATTERN_REG));
                            itemCreatePatt.addSelectionListener(new SelectionAdapter() {

                                @Override
                                public void widgetSelected(SelectionEvent e) {
                                    DbmsLanguage language = DbmsLanguageFactory.createDbmsLanguage(analysis);
                                    IFolder folder = ResourceManager.getPatternRegexFolder();
                                    String model = dataEntity.getLabel();
                                    String regex = dateIndicator.getRegex(model);
                                    new CreatePatternAction(
                                            folder,
                                            ExpressionType.REGEXP,
                                            "'" + regex + "'", model == null ? "" : "match \"" + model + "\"", language.getDbmsName()).run(); //$NON-NLS-1$ //$NON-NLS-2$
                                }

                            });
                        }
                        // ~11574
                        menu.setVisible(true);
                    }
                }
            }

            private SelectionAdapter getAdapter(final IDatabaseJobService service) {
                return new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        service.executeJob();
                    }
                };
            }
        });

        // add tool tip
        TableUtils.addTooltipOnTableItem(table);
    }

    /**
     * DOC bZhou Comment method "createPattern".
     * 
     * @param analysis
     * @param itemEntity
     * @param pattTransformer
     */
    public static void createPattern(Analysis analysis, MenuItemEntity itemEntity, final PatternTransformer pattTransformer) {
        String language = pattTransformer.getDbmsLanguage().getDbmsName();
        String query = itemEntity.getQuery();
        String regex = pattTransformer.getRegexp(query.substring(query.indexOf('=') + 3, query.lastIndexOf(')') - 1));
        IFolder folder = ResourceManager.getPatternRegexFolder();
        new CreatePatternAction(folder, ExpressionType.REGEXP, "'" + regex + "'", language).run(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * DOC bZhou Comment method "isDUDIndicator".
     * 
     * @param indicator
     * @return false if the indicator is not Duplicated,Uniqure,Distinct indicator.
     */
    public static boolean isDUDIndicator(Indicator indicator) {
        IndicatorsSwitch<Indicator> iSwitch = new IndicatorsSwitch<Indicator>() {

            public Indicator caseDuplicateCountIndicator(DuplicateCountIndicator object) {
                return object;
            };

            public Indicator caseUniqueCountIndicator(UniqueCountIndicator object) {
                return object;
            };

            public Indicator caseDistinctCountIndicator(DistinctCountIndicator object) {
                return object;
            };
        };

        return iSwitch.doSwitch(indicator) != null;
    }

    /**
     * DOC bZhou Comment method "isPatternMatchingIndicator".
     * 
     * @param indicator
     * @return false if the indicator is not pattern matching indicator.
     */
    public static boolean isPatternMatchingIndicator(Indicator indicator) {
        IndicatorsSwitch<Indicator> iSwitch = new IndicatorsSwitch<Indicator>() {

            @Override
            public Indicator casePatternMatchingIndicator(PatternMatchingIndicator object) {
                return object;
            }
        };

        return iSwitch.doSwitch(indicator) != null;
    }

    /**
     * DOC bZhou Comment method "isPatternFrequencyIndicator".
     * 
     * @param indicator
     * @return false if the indicator is not pattern frequency indicator.
     */
    public static boolean isPatternFrequencyIndicator(Indicator indicator) {
        IndicatorsSwitch<Indicator> iSwitch = new IndicatorsSwitch<Indicator>() {

            @Override
            public Indicator casePatternFreqIndicator(PatternFreqIndicator object) {
                return object;
            }

            @Override
            public Indicator casePatternLowFreqIndicator(PatternLowFreqIndicator object) {
                return object;
            }
        };

        return iSwitch.doSwitch(indicator) != null;
    }

    /**
     * DOC zshen Comment method "isDatePatternFrequencyIndicator".
     * 
     * @param indicator
     * @return false if the indicator is not Date pattern frequency indicator.
     */
    public static boolean isDatePatternFrequencyIndicator(Indicator indicator) {
        IndicatorsSwitch<Indicator> iSwitch = new IndicatorsSwitch<Indicator>() {

            @Override
            public Indicator caseDatePatternFreqIndicator(DatePatternFreqIndicator object) {
                return object;
            }
        };

        return iSwitch.doSwitch(indicator) != null;
    }
}
