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

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.SoftwareSystemManager;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dataprofiler.core.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
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

    private Composite container;

    /**
     * DOC qzhang RespositoryDetailView constructor comment.
     */
    public RespositoryDetailView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        GridData data = new GridData(GridData.FILL_BOTH);
        container.setLayout(layout);
        container.setLayoutData(data);
        createDefault();
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
    }

    /**
     * DOC qzhang Comment method "createDefault".
     */
    private void createDefault() {
        newText(container, "No detail available");
    }

    @Override
    public void setFocus() {
        container.setFocus();
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
        container.layout();
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
        
        label = new Label(container, SWT.NONE);
        label.setText("Type: ");
        
        EList<PatternComponent> components = pattern.getComponents();
        StringBuilder description = new StringBuilder();
        for (PatternComponent poc : components) {
            if (poc instanceof RegularExpression) {
                RegularExpression expression = (RegularExpression) poc;
                description.append("  ").append(expression.getExpression().getLanguage());
            }
        }
        newText(container, description.toString());        
    }

    /**
     * DOC qzhang Comment method "createAnaysisDetail".
     * 
     * @param ana
     */
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
        label = new Label(container, SWT.NONE);
        label.setText("Type: ");
        String description = ana.getParameters().getAnalysisType().getLiteral();
        newText(container, description);

        label = new Label(container, SWT.NONE);
        label.setText("Number of analyzed elements: ");
        AnalysisContext context = ana.getContext();
        int numn = context.getAnalysedElements().size();
        newText(container, String.valueOf(numn));

        label = new Label(container, SWT.NONE);
        label.setText("Connection: ");
        DataManager connection = context.getConnection();
        if (connection == null) {
            description = null;
        } else {
            description = connection.getName();
        }
        newText(container, description);
    }

    private void newText(Composite composite, String inputText) {
       newText(composite, inputText, "none");
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
        label = new Label(container, SWT.NONE);
        label.setText("Filename: ");
        newText(container, fe2.getFullPath().toPortableString());

        label = new Label(container, SWT.NONE);
        label.setText("Modification date: ");
        
        // MODSCA 20080728 changed to getLocalTimeStamp() because modificationStamp was 1 or 2 (=> year 1970)
        // long modificationStamp = fe2.getModificationStamp();
        long modificationStamp = fe2.getLocalTimeStamp();
        newText(container, new Date(modificationStamp).toString());
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
        label = new Label(container, SWT.NONE);
        label.setText("Number of analyses: ");
        int description = ReportHelper.getAnalyses(rep).size();
        newText(container, String.valueOf(description));
    }

    /**
     * DOC qzhang Comment method "createTdColumn".
     * 
     * @param column
     */
    private void createTdColumn(TdColumn column) {
        createTdTVDetail(column);
        Label label = new Label(container, SWT.NONE);
        label.setText("Type: ");
        newText(container, column.getSqlDataType().getName());

        label = new Label(container, SWT.NONE);
        label.setText("Nullable: ");
        String purpose = column.getIsNullable().isNullable();
        newText(container, purpose);
    }

    /**
     * DOC qzhang Comment method "createTdTVDetail".
     * 
     * @param element
     */
    private void createTdTVDetail(ModelElement element) {
        Label label;
        createName(element);

        label = new Label(container, SWT.NONE);
        label.setText("Remarks: ");
        String purpose = TaggedValueHelper.getComment(element);        
        newText(container, purpose);
    }

    /**
     * DOC qzhang Comment method "createName".
     * 
     * @param element
     */
    private void createName(ModelElement element) {
        Label label = new Label(container, SWT.NONE);
        label.setText("Name: ");
        newText(container, element.getName());
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

        label = new Label(container, SWT.NONE);
        label.setText("URL: ");
        String connectionString = DataProviderHelper.getTdProviderConnection(dataProvider).getObject().getConnectionString();
        newText(container, connectionString);

        label = new Label(container, SWT.NONE);
        label.setText("Type: ");
        TdSoftwareSystem softwareSystem = SoftwareSystemManager.getInstance().getSoftwareSystem(dataProvider);
        String subtype = softwareSystem.getSubtype();
        newText(container, subtype);
    }

    /**
     * DOC qzhang Comment method "createDescription".
     * 
     * @param dataProvider
     */
    private void createDescription(ModelElement dataProvider) {
        Label label;
        label = new Label(container, SWT.NONE);
        label.setText("Description: ");
        String description = TaggedValueHelper.getDescription(dataProvider);
        newText(container, description);
    }

    /**
     * DOC qzhang Comment method "createPurpose".
     * 
     * @param dataProvider
     */
    private void createPurpose(ModelElement dataProvider) {
        Label label = new Label(container, SWT.NONE);
        label.setText("Purpose: ");
        String purpose = TaggedValueHelper.getPurpose(dataProvider);
        newText(container, purpose);
    }

    /**
     * DOC qzhang Comment method "clearContainer".
     */
    private void clearContainer() {
        if (container != null && !container.isDisposed()) {
            Control[] children = container.getChildren();
            for (Control control : children) {
                control.dispose();
            }
        }
    }

}
