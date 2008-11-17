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
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.SoftwareSystemManager;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IDetailViewSwitchService;
import org.talend.dataprofiler.core.service.IService;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class RespositoryDetailView extends ViewPart implements ISelectionListener {

    private Group gContainer;

    private Group tContainer;

    private boolean switchFlag = false;

    /**
     * DOC qzhang RespositoryDetailView constructor comment.
     */
    public RespositoryDetailView() {
        List<IService> filterList = GlobalServiceRegister.getDefault().getServiceGroup(IDetailViewSwitchService.class);
        for (IService service : filterList) {
            if (service instanceof IDetailViewSwitchService) {
                IDetailViewSwitchService switchService = (IDetailViewSwitchService) service;
                switchFlag = switchService.isShow();
            }
        }
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
        scomp.setMinHeight(300);
        scomp.setContent(composite);

        gContainer = new Group(composite, SWT.NONE);
        gContainer.setText(DefaultMessagesImpl.getString("RespositoryDetailView.group.General"));
        GridLayout layout = new GridLayout(2, false);
        GridData data = new GridData(GridData.FILL_BOTH);
        gContainer.setLayout(layout);
        gContainer.setLayoutData(data);

        // create extend group
        if (switchFlag) {
            tContainer = new Group(composite, SWT.NONE);
            tContainer.setText(DefaultMessagesImpl.getString("RespositoryDetailView.group.Technical"));
            tContainer.setLayout(new GridLayout(2, false));
            tContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

            createExtDefault();
        }

        createDefault();
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);

    }

    private void createTechnicalDetail(EObject fe) {
        Label idLab = new Label(tContainer, SWT.NONE);
        idLab.setText(DefaultMessagesImpl.getString("RespositoryDetailView.group.Identifier"));
        newText(tContainer, ResourceHelper.getUUID(fe));

        Label pathLab = new Label(tContainer, SWT.NONE);
        pathLab.setText(DefaultMessagesImpl.getString("RespositoryDetailView.group.FilePath"));
        newText(tContainer, fe.eResource().getURI().path());
    }

    private void createTechnicalDetail(IFile fe) {
        EObject object = getEObject(fe);

        if (object != null) {
            createTechnicalDetail(object);
        }
    }

    /**
     * DOC qzhang Comment method "createDefault".
     */
    private void createDefault() {
        newText(gContainer, DefaultMessagesImpl.getString("RespositoryDetailView.noAvailable")); //$NON-NLS-1$
    }

    private void createExtDefault() {
        newText(tContainer, DefaultMessagesImpl.getString("RespositoryDetailView.noAvailable"));
    }

    @Override
    public void setFocus() {
        gContainer.setFocus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
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
            } else if ((fe instanceof TdTable) || fe instanceof TdView) {
                ModelElement element = (ModelElement) fe;
                createTdTVDetail(element);
                is = false;
            } else if (fe instanceof TdColumn) {
                TdColumn column = (TdColumn) fe;
                createTdColumn(column);
                is = false;
            }

            if (switchFlag) {
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
     * DOC qzhang Comment method "createFileDetail".
     * 
     * @param is
     * @param fe2
     * @return
     */
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

    /**
     * DOC qzhang Comment method "createPatternDetail".
     * 
     * @param pattern
     */
    private void createPatternDetail(Pattern pattern) {
        createName(pattern);
        createPurpose(pattern);
        createDescription(pattern);
        Label label;

        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.type")); //$NON-NLS-1$

        EList<PatternComponent> components = pattern.getComponents();
        StringBuilder description = new StringBuilder();
        for (PatternComponent poc : components) {
            if (poc instanceof RegularExpression) {
                RegularExpression expression = (RegularExpression) poc;
                description.append("  ").append(expression.getExpression().getLanguage()); //$NON-NLS-1$
            }
        }
        newText(gContainer, description.toString());
    }

    /**
     * DOC qzhang Comment method "createAnaysisDetail".
     * 
     * @param ana
     */
    private void createAnaysisDetail(Analysis ana) {
        createName(ana);
        createPurpose(ana);
        createDescription(ana);
        Label label;
        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.otherType")); //$NON-NLS-1$
        String description = ana.getParameters().getAnalysisType().getLiteral();
        newText(gContainer, description);

        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.numberOfAnalyzedElements")); //$NON-NLS-1$
        AnalysisContext context = ana.getContext();
        int numn = context.getAnalysedElements().size();
        newText(gContainer, String.valueOf(numn));

        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.connection")); //$NON-NLS-1$
        DataManager connection = context.getConnection();
        if (connection == null) {
            description = null;
        } else {
            description = connection.getName();
        }
        newText(gContainer, description);
    }

    private void newText(Composite composite, String inputText) {
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

    /**
     * DOC qzhang Comment method "createSqlFileDetail".
     * 
     * @param fe2
     */
    private void createSqlFileDetail(IFile fe2) {
        Label label;
        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.filename")); //$NON-NLS-1$
        newText(gContainer, fe2.getFullPath().toPortableString());

        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.modificationDate")); //$NON-NLS-1$

        // MODSCA 20080728 changed to getLocalTimeStamp() because modificationStamp was 1 or 2 (=> year 1970)
        // long modificationStamp = fe2.getModificationStamp();
        long modificationStamp = fe2.getLocalTimeStamp();
        newText(gContainer, new Date(modificationStamp).toString());
    }

    /**
     * DOC qzhang Comment method "createReportDetail".
     * 
     * @param rep
     */
    private void createReportDetail(TdReport rep) {
        createName(rep);
        createPurpose(rep);
        createDescription(rep);
        Label label;
        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.numberOfAnalyses")); //$NON-NLS-1$
        int description = ReportHelper.getAnalyses(rep).size();
        newText(gContainer, String.valueOf(description));
    }

    /**
     * DOC qzhang Comment method "createTdColumn".
     * 
     * @param column
     */
    private void createTdColumn(TdColumn column) {
        createTdTVDetail(column);
        Label label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.typex")); //$NON-NLS-1$
        newText(gContainer, column.getSqlDataType().getName());

        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.nullable")); //$NON-NLS-1$
        String purpose = column.getIsNullable().isNullable();
        newText(gContainer, purpose);
    }

    /**
     * DOC qzhang Comment method "createTdTVDetail".
     * 
     * @param element
     */
    private void createTdTVDetail(ModelElement element) {
        Label label;
        createName(element);

        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.remarks")); //$NON-NLS-1$
        String purpose = TaggedValueHelper.getComment(element);
        newText(gContainer, purpose);
    }

    /**
     * DOC qzhang Comment method "createName".
     * 
     * @param element
     */
    private void createName(ModelElement element) {
        Label label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.name")); //$NON-NLS-1$
        newText(gContainer, element.getName());
    }

    /**
     * DOC qzhang Comment method "createTdSchemaDetail".
     * 
     * @param schema
     */
    private void createTdSchemaDetail(TdSchema schema) {
        createName(schema);
    }

    /**
     * DOC qzhang Comment method "createTdCatalogDetail".
     * 
     * @param catalog
     */
    private void createTdCatalogDetail(TdCatalog catalog) {
        createName(catalog);
    }

    /**
     * DOC qzhang Comment method "createDataProviderDetail".
     */
    private void createDataProviderDetail(TdDataProvider dataProvider) {
        createName(dataProvider);

        Label label;
        createPurpose(dataProvider);
        createDescription(dataProvider);

        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.URL")); //$NON-NLS-1$
        String connectionString = DataProviderHelper.getTdProviderConnection(dataProvider).getObject().getConnectionString();
        newText(gContainer, connectionString);

        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.type2")); //$NON-NLS-1$
        TdSoftwareSystem softwareSystem = DataProviderHelper.getSoftwareSystem(dataProvider);
        if (softwareSystem == null) {
            softwareSystem = SoftwareSystemManager.getInstance().getSoftwareSystem(dataProvider);
        }
        String subtype = (softwareSystem == null) ? "" : softwareSystem.getSubtype();
        newText(gContainer, subtype);
    }

    /**
     * DOC qzhang Comment method "createDescription".
     * 
     * @param dataProvider
     */
    private void createDescription(ModelElement dataProvider) {
        Label label;
        label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.description")); //$NON-NLS-1$
        String description = TaggedValueHelper.getDescription(dataProvider);
        newText(gContainer, description);
    }

    /**
     * DOC qzhang Comment method "createPurpose".
     * 
     * @param dataProvider
     */
    private void createPurpose(ModelElement dataProvider) {
        Label label = new Label(gContainer, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("RespositoryDetailView.purpose")); //$NON-NLS-1$
        String purpose = TaggedValueHelper.getPurpose(dataProvider);
        newText(gContainer, purpose);
    }

    /**
     * DOC qzhang Comment method "clearContainer".
     */
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

}
