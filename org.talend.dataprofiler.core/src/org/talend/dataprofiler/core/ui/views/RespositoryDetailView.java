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
package org.talend.dataprofiler.core.ui.views;

import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.management.api.SoftwareSystemManager;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dataprofiler.core.PluginChecker;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.PrimaryKey;
import orgomg.cwm.resource.relational.Table;

/**
 * @author qzhang
 * 
 * Detail view of the Data profiler.
 */
public class RespositoryDetailView extends ViewPart implements ISelectionListener {

    private Group gContainer;

    private Group tContainer;

    /**
     * DOC qzhang RespositoryDetailView constructor comment.
     */
    public RespositoryDetailView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new FillLayout());
        ScrolledComposite scomp = new ScrolledComposite(comp, SWT.H_SCROLL | SWT.V_SCROLL);
        scomp.setLayout(new FillLayout());

        Composite composite = new Composite(scomp, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        scomp.setExpandHorizontal(true);
        scomp.setExpandVertical(true);
        scomp.setMinWidth(400);
        scomp.setMinHeight(350);
        scomp.setContent(composite);

        gContainer = new Group(composite, SWT.NONE);
        gContainer.setText(DefaultMessagesImpl.getString("RespositoryDetailView.group.General")); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        GridData data = new GridData(GridData.FILL_BOTH);
        gContainer.setLayout(layout);
        gContainer.setLayoutData(data);

        // create extend group
        if (PluginChecker.isTDQLoaded()) {
            tContainer = new Group(composite, SWT.NONE);
            tContainer.setText(DefaultMessagesImpl.getString("RespositoryDetailView.group.Technical")); //$NON-NLS-1$
            tContainer.setLayout(new GridLayout(2, false));
            tContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

            createExtDefault();
        }

        createDefault();
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        initializeToolBar();

    }

    private void createTechnicalDetail(EObject fe) {
        newLabelAndText(tContainer,
                DefaultMessagesImpl.getString("RespositoryDetailView.group.Identifier"), ResourceHelper.getUUID(fe)); //$NON-NLS-1$

        // MOD xqliu 2009-4-27 bug 6507
        // newLabelAndText(
        // tContainer,
        // DefaultMessagesImpl
        //						.getString("RespositoryDetailView.group.FilePath"), fe.eResource() //$NON-NLS-1$
        // .getURI().toPlatformString(false));
        newLabelAndText(tContainer, DefaultMessagesImpl.getString("RespositoryDetailView.group.FilePath"), //$NON-NLS-1$
                fe.eResource() == null ? "" : fe.eResource().getURI().toPlatformString(false)); //$NON-NLS-1$
        // ~
    }

    private void createTechnicalDetail(IFile fe) {
        EObject object = getEObject(fe);

        if (object != null) {
            createTechnicalDetail(object);
        }
    }

    private void createDefault() {
        newText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.noAvailable")); //$NON-NLS-1$
    }

    private void createExtDefault() {
        newText(tContainer, DefaultMessagesImpl.getString("RespositoryDetailView.noAvailable")); //$NON-NLS-1$
    }

    @Override
    public void setFocus() {
        gContainer.setFocus();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui. IWorkbenchPart,
     * org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        clearContainer();
        boolean is = true;
        if (part instanceof DQRespositoryView) {
            StructuredSelection sel = (StructuredSelection) selection;
            Object fe = sel.getFirstElement();
            if (fe instanceof IFile) {
                IFile fe2 = (IFile) fe;
                is = createFileDetail(is, fe2);
            } else if (fe instanceof TdCatalog) {
                TdCatalog catalog = (TdCatalog) fe;
                createTdCatalogDetail(catalog);
                is = false;
            } else if (fe instanceof TdSchema) {
                TdSchema schema = (TdSchema) fe;
                createTdSchemaDetail(schema);
                is = false;
            } else if (fe instanceof TdTable) {
                ModelElement element = (ModelElement) fe;
                createTableDetail((Table) element);
                is = false;
            } else if (fe instanceof TdView) {
                ModelElement element = (ModelElement) fe;
                createNameCommentDetail(element);
                is = false;
            } else if (fe instanceof TdColumn) {
                TdColumn column = (TdColumn) fe;
                createTdColumn(column);
                is = false;
            } else if (fe instanceof IEcosComponent) {
                IEcosComponent component = (IEcosComponent) fe;
                createEcosComponent(component);
                is = false;
            } else if (fe instanceof RegularExpression) {
                // MOD mzhao 2009-04-20,Bug 6349.
                RegularExpression regularExpression = (RegularExpression) fe;
                createRegularExpression(regularExpression);
                is = false;
            }

            if (PluginChecker.isTDQLoaded()) {
                if (fe instanceof EObject) {
                    createTechnicalDetail((EObject) fe);
                } else if (fe instanceof IFile) {
                    createTechnicalDetail((IFile) fe);
                } else {
                    createExtDefault();
                }
            }
        } else if (part instanceof CommonFormEditor) {
            CommonFormEditor editor = (CommonFormEditor) part;
            IEditorInput editorInput = editor.getEditorInput();
            if (editorInput instanceof IFileEditorInput) {
                IFileEditorInput input = (IFileEditorInput) editorInput;
                IFile file = input.getFile();
                is = createFileDetail(is, file);
            }
        }

        if (is) {
            createDefault();
        }

        gContainer.layout();
        if (tContainer != null) {
            tContainer.layout();
        }
    }

    /**
     * DOC bZhou Comment method "createEcosComponent".
     * 
     * @param component
     */
    private void createEcosComponent(IEcosComponent component) {
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.Name"), component.getName()); //$NON-NLS-1$
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.Author"), component.getAuthor()); //$NON-NLS-1$
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.Description"), component.getDescription()); //$NON-NLS-1$
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.Type"), component.getCategry().getName()); //$NON-NLS-1$

    }

    private void createRegularExpression(RegularExpression regularExpression) {
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.Expression"), regularExpression.getExpression().getBody()); //$NON-NLS-1$
    }

    private void createTableDetail(Table table) {
        createNameCommentDetail(table);
        List<PrimaryKey> primaryKeys = TableHelper.getPrimaryKeys(table);
        newLabelAndText(
                gContainer,
                DefaultMessagesImpl.getString("RespositoryDetailView.PrimaryKeys"), primaryKeys.isEmpty() ? null : String.valueOf(primaryKeys.size())); //$NON-NLS-1$
        List<ForeignKey> foreignKeys = TableHelper.getForeignKeys(table);
        newLabelAndText(
                gContainer,
                DefaultMessagesImpl.getString("RespositoryDetailView.Foreignkeys"), foreignKeys.isEmpty() ? null : String.valueOf(foreignKeys.size())); //$NON-NLS-1$
    }

    private boolean createFileDetail(boolean is, IFile fe2) {
        if (fe2.getFileExtension().equals(FactoriesUtil.PROV)) {
            TypedReturnCode<TdDataProvider> tdProvider = PrvResourceFileHelper.getInstance().findProvider(fe2);
            TdDataProvider dataProvider = tdProvider.getObject();
            createDataProviderDetail(dataProvider);
            is = false;
        } else if (fe2.getFileExtension().equals(FactoriesUtil.PATTERN)) {
            Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(fe2);
            createPatternDetail(pattern);
            is = false;
        } else if (fe2.getFileExtension().equals(FactoriesUtil.ANA)) {
            Analysis ana = AnaResourceFileHelper.getInstance().findAnalysis(fe2);
            createAnaysisDetail(ana);
            is = false;
        } else if (fe2.getFileExtension().equals(FactoriesUtil.REP)) {
            TdReport rep = RepResourceFileHelper.getInstance().findReport(fe2);
            createReportDetail(rep);
            is = false;
        } else if (fe2.getFileExtension().equals(FactoriesUtil.SQL)) {
            createSqlFileDetail(fe2);
            is = false;
        }
        return is;
    }

    private EObject getEObject(IFile fe2) {
        EObject object = null;

        if (fe2.getFileExtension().equals(FactoriesUtil.PROV)) {
            TypedReturnCode<TdDataProvider> tdProvider = PrvResourceFileHelper.getInstance().findProvider(fe2);
            TdDataProvider dataProvider = tdProvider.getObject();
            object = dataProvider;
        } else if (fe2.getFileExtension().equals(FactoriesUtil.PATTERN)) {
            object = PatternResourceFileHelper.getInstance().findPattern(fe2);
        } else if (fe2.getFileExtension().equals(FactoriesUtil.ANA)) {
            object = AnaResourceFileHelper.getInstance().findAnalysis(fe2);
        } else if (fe2.getFileExtension().equals(FactoriesUtil.REP)) {
            object = RepResourceFileHelper.getInstance().findReport(fe2);
        }

        return object;
    }

    private void createPatternDetail(Pattern pattern) {
        createName(pattern);
        createPurpose(pattern);
        createDescription(pattern);

        EList<PatternComponent> components = pattern.getComponents();
        StringBuilder description = new StringBuilder();
        for (PatternComponent poc : components) {
            if (poc instanceof RegularExpression) {
                RegularExpression expression = (RegularExpression) poc;
                description.append("  ").append(expression.getExpression().getLanguage()); //$NON-NLS-1$
            }
        }
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.type"), description.toString().trim()); //$NON-NLS-1$
    }

    private void createAnaysisDetail(Analysis ana) {
        createName(ana);
        createPurpose(ana);
        createDescription(ana);

        String description = ana.getParameters().getAnalysisType().getLiteral();
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.otherType"), description); //$NON-NLS-1$

        AnalysisContext context = ana.getContext();
        int numn = context.getAnalysedElements().size();
        newLabelAndText(gContainer,
                DefaultMessagesImpl.getString("RespositoryDetailView.numberOfAnalyzedElements"), String.valueOf(numn)); //$NON-NLS-1$

        DataManager connection = context.getConnection();
        if (connection == null) {
            description = null;
        } else {
            description = connection.getName();
        }
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.connection"), description); //$NON-NLS-1$
    }

    private void newText(Composite composite, String inputText) {
        newText(composite, inputText, DefaultMessagesImpl.getString("RespositoryDetailView.none")); //$NON-NLS-1$
    }

    private void newLabelAndText(Composite composite, String labelString, String inputText) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(labelString);
        newText(composite, inputText, DefaultMessagesImpl.getString("RespositoryDetailView.none")); //$NON-NLS-1$
    }

    private void newText(Composite composite, String inputText, String defaultText) {
        Text text = new Text(composite, SWT.NONE);
        text.setEditable(false);
        if (inputText == null || inputText.trim().length() == 0) {
            text.setForeground(text.getDisplay().getSystemColor(SWT.COLOR_RED));
            text.setText(defaultText);
        } else {
            text.setText(inputText);
        }
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
    }

    private void createSqlFileDetail(IFile fe2) {
        newLabelAndText(gContainer,
                DefaultMessagesImpl.getString("RespositoryDetailView.filename"), fe2.getFullPath().toPortableString()); //$NON-NLS-1$

        // MODSCA 20080728 changed to getLocalTimeStamp() because
        // modificationStamp was 1 or 2 (=> year 1970)
        // long modificationStamp = fe2.getModificationStamp();
        long modificationStamp = fe2.getLocalTimeStamp();
        newLabelAndText(gContainer,
                DefaultMessagesImpl.getString("RespositoryDetailView.modificationDate"), new Date(modificationStamp).toString()); //$NON-NLS-1$
    }

    private void createReportDetail(TdReport rep) {
        createName(rep);
        createPurpose(rep);
        createDescription(rep);
        int description = ReportHelper.getAnalyses(rep).size();
        newLabelAndText(gContainer,
                DefaultMessagesImpl.getString("RespositoryDetailView.numberOfAnalyses"), String.valueOf(description)); //$NON-NLS-1$
    }

    private void createTdColumn(TdColumn column) {
        createNameCommentDetail(column);
        newLabelAndText(gContainer,
                DefaultMessagesImpl.getString("RespositoryDetailView.typex"), column.getSqlDataType().getName()); //$NON-NLS-1$
        String purpose = column.getIsNullable().isNullable();
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.nullable"), purpose); //$NON-NLS-1$
        final Expression initialValue = column.getInitialValue();
        String defValueText = (initialValue != null) ? initialValue.getBody() : null;
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.DefaultValue"), defValueText); //$NON-NLS-1$
        newLabelAndText(gContainer,
                DefaultMessagesImpl.getString("RespositoryDetailView.Size"), String.valueOf(column.getLength())); //$NON-NLS-1$
    }

    private void createNameCommentDetail(ModelElement element) {
        createName(element);
        String purpose = ColumnHelper.getComment(element);
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.remarks"), purpose); //$NON-NLS-1$
    }

    private void createName(ModelElement element) {
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.name"), element.getName()); //$NON-NLS-1$
    }

    private void createTdSchemaDetail(TdSchema schema) {
        createName(schema);
    }

    private void createTdCatalogDetail(TdCatalog catalog) {
        createName(catalog);
    }

    private void createDataProviderDetail(TdDataProvider dataProvider) {
        createName(dataProvider);
        createPurpose(dataProvider);
        createDescription(dataProvider);
        // MOD mzhao xmldb have no actual connection.
        // TODO Handle details view.
        TypedReturnCode<TdProviderConnection> proConn = DataProviderHelper.getTdProviderConnection(dataProvider);
        if (proConn != null) {
            String connectionString = proConn.getObject().getConnectionString();
            newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.URL"), connectionString); //$NON-NLS-1$
        }
        TdSoftwareSystem softwareSystem = DataProviderHelper.getSoftwareSystem(dataProvider);
        if (softwareSystem == null) {
            softwareSystem = SoftwareSystemManager.getInstance().getSoftwareSystem(dataProvider);
        }
        String subtype = (softwareSystem == null) ? "" : softwareSystem.getSubtype(); //$NON-NLS-1$
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.type2"), subtype); //$NON-NLS-1$
        String version = (softwareSystem == null) ? "" : softwareSystem.getVersion(); //$NON-NLS-1$
        newLabelAndText(gContainer, "Version: ", version); //$NON-NLS-1$

    }

    private void createDescription(ModelElement dataProvider) {
        String description = MetadataHelper.getDescription(dataProvider);
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.description"), description); //$NON-NLS-1$
    }

    private void createPurpose(ModelElement dataProvider) {
        String purpose = MetadataHelper.getPurpose(dataProvider);
        newLabelAndText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.purpose"), purpose); //$NON-NLS-1$
    }

    private void clearContainer() {
        if (gContainer != null && !gContainer.isDisposed()) {
            Control[] children = gContainer.getChildren();
            for (Control control : children) {
                control.dispose();
            }
        }

        if (tContainer != null && !tContainer.isDisposed()) {
            Control[] children = tContainer.getChildren();
            for (Control control : children) {
                control.dispose();
            }
        }
    }

    private void initializeToolBar() {
        getViewSite().getActionBars().getToolBarManager();
    }

}
