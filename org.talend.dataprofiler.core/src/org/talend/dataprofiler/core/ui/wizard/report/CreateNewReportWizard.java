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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.Wizard;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.RepResourceFileHelper;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.report.provider.AnalysisEntity;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.analysis.ReportBuilder;
import org.talend.dq.analysis.ReportWriter;
import org.talend.dq.analysis.parameters.ReportParameter;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class CreateNewReportWizard extends Wizard {

    private static Logger log = Logger.getLogger(AbstractAnalysisWizard.class);

    private ReportParameter reportParameter;

    private NewReportMetadataWizardPage metadataPage;

    private NewReportParameterWizardPage parameterPage;

    /**
     * DOC zqin CreateNewReportWizard constructor comment.
     */
    public CreateNewReportWizard() {

        reportParameter = new ReportParameter();
        AbstractWizardPage.setConnectionParams(reportParameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        if (reportParameter == null) {
            System.out.println("Exception: parameter is null!");
            return false;
        }

        List<Analysis> selectedAnalysises = new ArrayList<Analysis>();
        for (AnalysisEntity entity : parameterPage.getSomeAnalysises()) {

            selectedAnalysises.add(entity.getAnalysis());
        }

        reportParameter.setAnalysises(selectedAnalysises);

        // build an tdReport Object
        try {
            ReportBuilder builder = new ReportBuilder();
            if (builder.initializeTdReport(reportParameter.getName())) {
                TdReport report = builder.getReport();

                // set metada information
                String purpose = reportParameter.getPurpose();
                String description = reportParameter.getDescription();
                String status = reportParameter.getStatus();
                String author = reportParameter.getAuthor();

                TaggedValueHelper.setAuthor(report, author);
                TaggedValueHelper.setDevStatus(report, DevelopmentStatus.get(status));
                TaggedValueHelper.setPurpose(purpose, report);
                TaggedValueHelper.setDescription(description, report);

                // set report information
                String header = reportParameter.getHeader();
                String footer = reportParameter.getFooter();
                boolean refresh = reportParameter.isRefresh();
                ReportHelper.setHeader(report, header);
                ReportHelper.setFooter(report, footer);
                ReportHelper.mustRefreshAllAnalyses(report, refresh);

                ReportHelper.addAnalyses(reportParameter.getAnalysises(), report);

                // write this object to file
                ReportWriter writer = new ReportWriter();

                String path = DqRepositoryViewService.createFilename(reportParameter.getFolderProvider().getFolder()
                        .getAbsolutePath(), reportParameter.getName(), PluginConstant.REP_SUFFIX);
                File file = new File(path);

                if (file.exists()) {
                    return false;
                } else {
                    ReturnCode saved = writer.save(report, file);
                    if (saved.isOk()) {
                        log.info("Saved in  " + file.getAbsolutePath());
                        Resource resource = report.eResource();
                        RepResourceFileHelper.getInstance().register(resource.getURI().toFileString(), resource);
                        // for (Analysis analysis : reportParameter.getAnalysises()) {
                        // TaggedValueHelper.setTaggedValue(analysis, reportParameter.getName() +
                        // PluginConstant.REP_SUFFIX,
                        // PluginConstant.EMPTY_STRING);
                        // ReturnCode save = AnaResourceFileHelper.getInstance().save(analysis);
                        // if (save.isOk()) {
                        // log.info("Saved in " + analysis.getUrl() + " successful");
                        // } else {
                        // throw new DataprofilerCoreException("Problem saving file: " + analysis.getUrl() + ": "
                        // + saved.getMessage());
                        // }
                        // }
                    } else {
                        throw new DataprofilerCoreException("Problem saving file: " + file.getAbsolutePath() + ": "
                                + saved.getMessage());
                    }

                    CorePlugin.getDefault().refreshWorkSpace();
                    ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();
                }
            }
        } catch (Exception e) {
            ExceptionHandler.process(e, Level.ERROR);
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {

        try {
            metadataPage = new NewReportMetadataWizardPage();
            parameterPage = new NewReportParameterWizardPage();

            addPage(metadataPage);
            addPage(parameterPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
