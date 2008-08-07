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
import org.eclipse.swt.widgets.Display;
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
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.helper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.helper.RepResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;
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
        Text text = new Text(container, SWT.NONE);
        text.setEditable(false);
        text.setText("No detail available");
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
            TypedReturnCode<TdDataProvider> tdProvider = PrvResourceFileHelper.getInstance().getTdProvider(fe2);
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
        Text text;
        GridData data;
        label = new Label(container, SWT.NONE);
        label.setText("Type: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        EList<PatternComponent> components = pattern.getComponents();
        String description = "";
        for (PatternComponent poc : components) {
            if (poc instanceof RegularExpression) {
                RegularExpression expression = (RegularExpression) poc;
                description += "  " + expression.getExpression().getLanguage();
            }
        }
        boolean b = description.length() == 0;
        if (b) {
            text.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        }
        text.setText(b ? "none" : description);
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
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
        Text text;
        GridData data;
        label = new Label(container, SWT.NONE);
        label.setText("Type: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        String description = ana.getParameters().getAnalysisType().getName();
        text.setText(description == null ? "" : description);
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);

        label = new Label(container, SWT.NONE);
        label.setText("Number of analyzed elements: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        AnalysisContext context = ana.getContext();
        int numn = context.getAnalysedElements().size();
        text.setText("" + numn);
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);

        label = new Label(container, SWT.NONE);
        label.setText("Connection: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        DataManager connection = context.getConnection();
        if (connection == null) {
            description = null;
        } else {
            description = connection.getName();
        }
        text.setText(description == null ? "" : description);
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
    }

    /**
     * DOC qzhang Comment method "createSqlFileDetail".
     * 
     * @param fe2
     */
    private void createSqlFileDetail(IFile fe2) {
        Label label;
        Text text;
        GridData data;
        label = new Label(container, SWT.NONE);
        label.setText("Filename: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        text.setText(fe2.getFullPath().toPortableString());
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);

        label = new Label(container, SWT.NONE);
        label.setText("Modification date: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        // MODSCA 20080728 changed to getLocalTimeStamp() because modificationStamp was 1 or 2 (=> year 1970)
        // long modificationStamp = fe2.getModificationStamp();
        long modificationStamp = fe2.getLocalTimeStamp();
        text.setText(new Date(modificationStamp).toString());
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
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
        Text text;
        GridData data;
        label = new Label(container, SWT.NONE);
        label.setText("Number of analyses: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        int description = ReportHelper.getAnalyses(rep).size();
        text.setText("" + description);
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
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
        Text text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        text.setText(column.getSqlDataType().getName());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);

        label = new Label(container, SWT.NONE);
        label.setText("Nullable: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        String purpose = column.getIsNullable().isNullable();
        boolean b = purpose == null;
        if (b) {
            text.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        }
        text.setText(b ? "null" : purpose);
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
    }

    /**
     * DOC qzhang Comment method "createTdTVDetail".
     * 
     * @param element
     */
    private void createTdTVDetail(ModelElement element) {
        Label label;
        Text text;
        createName(element);

        label = new Label(container, SWT.NONE);
        label.setText("Comment: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        String purpose = TaggedValueHelper.getComment(element);
        boolean b = (purpose == null || purpose.trim().length() == 0);
        if (b) {
            text.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        }
        text.setText(b ? "No comment" : purpose);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
    }

    /**
     * DOC qzhang Comment method "createName".
     * 
     * @param element
     */
    private void createName(ModelElement element) {
        GridData data;
        Label label = new Label(container, SWT.NONE);
        label.setText("Name: ");
        Text text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        text.setText(element.getName());
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
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
        Text text;
        GridData data;
        createPurpose(dataProvider);
        createDescription(dataProvider);

        label = new Label(container, SWT.NONE);
        label.setText("URL: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        String connectionString = DataProviderHelper.getTdProviderConnection(dataProvider).getObject().getConnectionString();
        text.setText(connectionString == null ? "" : connectionString);
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);

        label = new Label(container, SWT.NONE);
        label.setText("Type: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        TdSoftwareSystem softwareSystem = SoftwareSystemManager.getInstance().getSoftwareSystem(dataProvider);
        String subtype = softwareSystem.getSubtype();
        text.setText(subtype == null ? "" : subtype);
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
    }

    /**
     * DOC qzhang Comment method "createDescription".
     * 
     * @param dataProvider
     */
    private void createDescription(ModelElement dataProvider) {
        Label label;
        Text text;
        GridData data;
        label = new Label(container, SWT.NONE);
        label.setText("Description: ");
        text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        String description = TaggedValueHelper.getDescription(dataProvider);
        text.setText(description == null ? "" : description);
        data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
    }

    /**
     * DOC qzhang Comment method "createPurpose".
     * 
     * @param dataProvider
     */
    private void createPurpose(ModelElement dataProvider) {
        Label label = new Label(container, SWT.NONE);
        label.setText("Purpose: ");
        Text text = new Text(container, SWT.WRAP);
        text.setEditable(false);
        String purpose = TaggedValueHelper.getPurpose(dataProvider);
        text.setText(purpose == null ? "" : purpose);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);
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
