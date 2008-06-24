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
package org.talend.dataprofiler.core.ui.wizard.report;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizardPage;
import org.talend.dataprofiler.core.ui.wizard.report.provider.AnalysisEntity;
import org.talend.dataprofiler.core.ui.wizard.report.provider.ReportTableContentProvider;
import org.talend.dataprofiler.core.ui.wizard.report.provider.ReportTableLabelProvider;
import org.talend.dq.analysis.parameters.ReportParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class NewReportParameterWizardPage extends AbstractAnalysisWizardPage {

    private final String pageTitle = "New Report Step 2/2";

    private final String pageMessage = "Editor header and footer.";

    private Text headerText, footerText;

    private Button checkRefresh;

    private CCombo formatSelection;

    private ListViewer analysisFromDQ, analysisForReport;

    private Button addAnalysis, delAnalysis, moveToUp, moveToDown;

    private List listDQ, listReport;

    private java.util.List<AnalysisEntity> allAnalysises = new java.util.ArrayList<AnalysisEntity>();

    private java.util.List<AnalysisEntity> someAnalysises = new java.util.ArrayList<AnalysisEntity>();

    /**
     * DOC zqin NewReportParameterWizardPage constructor comment.
     */
    public NewReportParameterWizardPage() {
        setTitle(pageTitle);
        setMessage(pageMessage);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizardPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 50;

        // header
        Label headerLabel = new Label(container, SWT.NONE);
        headerLabel.setText("Header");
        headerText = new Text(container, SWT.BORDER | SWT.MULTI);
        headerText.setLayoutData(gd);

        // middle
        GridData gdList = new GridData(GridData.FILL_HORIZONTAL);
        gdList.heightHint = 100;
        Label analysisLabel = new Label(container, SWT.NONE);
        analysisLabel.setText("Analysis");
        // //left of middle
        Composite listComp = new Composite(container, SWT.NONE);
        listComp.setLayout(new GridLayout(3, false));
        listComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        analysisFromDQ = new ListViewer(listComp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        analysisFromDQ.setContentProvider(new ReportTableContentProvider());
        analysisFromDQ.setLabelProvider(new ReportTableLabelProvider());
        listDQ = analysisFromDQ.getList();
        listDQ.setLayoutData(gdList);
        try {
            createAnalysisTable();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // //middle of middle
        Composite midSubComp = new Composite(listComp, SWT.NONE);
        midSubComp.setLayout(new GridLayout());
        moveToUp = new Button(midSubComp, SWT.ARROW | SWT.UP);
        addAnalysis = new Button(midSubComp, SWT.ARROW | SWT.RIGHT);
        delAnalysis = new Button(midSubComp, SWT.ARROW | SWT.LEFT);
        moveToDown = new Button(midSubComp, SWT.ARROW | SWT.DOWN);

        // //right of middle
        analysisForReport = new ListViewer(listComp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        analysisForReport.setContentProvider(new ReportTableContentProvider());
        analysisForReport.setLabelProvider(new ReportTableLabelProvider());
        listReport = analysisForReport.getList();
        listReport.setLayoutData(gdList);

        // checkbox
        GridData grid = new GridData();
        grid.horizontalSpan = 2;
        checkRefresh = new Button(container, SWT.CHECK);
        checkRefresh.setText("Refresh all analysis");
        checkRefresh.setLayoutData(grid);
        // buttom
        Label footerLabel = new Label(container, SWT.NONE);
        footerLabel.setText("Footer");
        footerText = new Text(container, SWT.BORDER | SWT.MULTI);
        footerText.setLayoutData(gd);

        Label formatLabel = new Label(container, SWT.NONE);
        formatLabel.setText("Format");
        formatSelection = new CCombo(container, SWT.BORDER);
        formatSelection.setText("pdf");
        formatSelection.setEditable(false);
        GridData gdd = new GridData();
        gdd.widthHint = 150;
        formatSelection.setLayoutData(gdd);

        String[] formatOptions = new String[] { "pdf", "xml", "html" };
        // TODO zqin move this class in org.talend.dataprofiler.core.tdq plugin
        // TODO zqin and use JasperReportBuilder.OUTPUT_FORMAT for formatOptions
        for (String str : formatOptions) {
            formatSelection.add(str);
        }

        addToolTips();
        addListeners();
        setControl(container);
    }

    protected void addListeners() {

        addAnalysis.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                Iterator<IStructuredSelection> it = ((IStructuredSelection) analysisFromDQ.getSelection()).iterator();

                while (it.hasNext()) {
                    AnalysisEntity entity = (AnalysisEntity) it.next();
                    someAnalysises.add(entity);
                    allAnalysises.remove(entity);
                }
                analysisForReport.setInput(someAnalysises);

                analysisForReport.refresh();
                analysisFromDQ.refresh();
            }

        });

        delAnalysis.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                Iterator<IStructuredSelection> it = ((IStructuredSelection) analysisForReport.getSelection()).iterator();

                while (it.hasNext()) {
                    AnalysisEntity entity = (AnalysisEntity) it.next();
                    someAnalysises.remove(entity);
                    allAnalysises.add(entity);
                }

                analysisFromDQ.refresh();
                analysisForReport.refresh();
            }
        });

        moveToUp.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                Iterator<IStructuredSelection> it = ((IStructuredSelection) analysisForReport.getSelection()).iterator();
                int size = ((IStructuredSelection) analysisForReport.getSelection()).size();

                while (it.hasNext()) {
                    AnalysisEntity entity = (AnalysisEntity) it.next();

                    int index = someAnalysises.indexOf(entity);
                    if (index == 0) {
                        break;
                    }
                    someAnalysises.remove(index);
                    someAnalysises.add(index - 1, entity);
                }

                analysisForReport.refresh();
            }
        });

        moveToDown.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                Iterator<IStructuredSelection> it = ((IStructuredSelection) analysisForReport.getSelection()).iterator();
                int size = ((IStructuredSelection) analysisForReport.getSelection()).size();

                while (it.hasNext()) {
                    AnalysisEntity entity = (AnalysisEntity) it.next();

                    int index = someAnalysises.indexOf(entity);
                    if (index + size < someAnalysises.size()) {
                        someAnalysises.remove(index);
                        someAnalysises.add(index + size, entity);
                    }
                }

                analysisForReport.refresh();
            }
        });

        headerText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                ((ReportParameter) getConnectionParams()).setHeader(headerText.getText());
            }

        });

        footerText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                ((ReportParameter) getConnectionParams()).setFooter(footerText.getText());
            }

        });

        checkRefresh.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {

                ((ReportParameter) getConnectionParams()).setRefresh(checkRefresh.getSelection());
            }

        });

        formatSelection.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                ((ReportParameter) getConnectionParams()).setFormat(formatSelection.getText());
            }

        });
    }

    protected void addToolTips() {
        moveToUp.setToolTipText("move the item in the right list up");
        moveToDown.setToolTipText("move the item in the right list down");
    }

    private void createAnalysisTable() {
        // IFolder defaultAnalysFolder = ResourcesPlugin.getWorkspace().getRoot().
        // getProject(PluginConstant.DATA_PROFILING_PROJECTNAME).getFolder(DQStructureManager.ANALYSIS);
        Collection<AnalysisEntity> allAnalysis = AnaResourceFileHelper.getInstance().getAllAnalysis();
        analysisFromDQ.setInput(allAnalysis);
        allAnalysises.addAll(allAnalysis);
    }

    // public java.util.List<AnalysisEntity> searchAllAnalysis(IFolder folder) {
    //
    // for (File file : folder.getLocation().toFile().listFiles()) {
    // if (file.isDirectory()) {
    // searchAllAnalysis(folder.getFolder(file.getName()));
    // }
    //
    // AnalysisEntity entity = new AnalysisEntity(AnaResourceFileHelper.getInstance().getAnalysis(file));
    //
    // allAnalysises.add(entity);
    // }
    //
    // return allAnalysises;
    // }

    /**
     * Getter for someAnalysises.
     * 
     * @return the someAnalysises
     */
    public java.util.List<AnalysisEntity> getSomeAnalysises() {
        return this.someAnalysises;
    }
}
