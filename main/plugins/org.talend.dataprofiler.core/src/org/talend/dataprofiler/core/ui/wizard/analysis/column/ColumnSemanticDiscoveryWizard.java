// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC msjian class global comment. Detailled comment
 */
public class ColumnSemanticDiscoveryWizard extends ColumnWizard {

    public ColumnSemanticDiscoveryWizard(AnalysisParameter parameter) {
        super(parameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnWizard#getPredefinedColumnIndicator()
     */
    @Override
    protected ModelElementIndicator[] getPredefinedColumnIndicator() {
        List<IndicatorEnum> allwedEnumeLs = new ArrayList<IndicatorEnum>();
        IndicatorEnum[] allwedEnumeArray = allwedEnumeLs.toArray(new IndicatorEnum[allwedEnumeLs.size()]);
        return composePredefinedColumnIndicator(allwedEnumeArray);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnWizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());
        selectionPage = new ColumnAnalysisDOSelectionPage();
        addPage(selectionPage);

        // ISemanticStudioService service = CorePlugin.getDefault().getSemanticStudioService();
        // if (service != null) {
        // Wizard semanticDiscoveryWizard = service.getSemanticDiscoveryWizard(null);
        // IWizardPage[] pages = semanticDiscoveryWizard.getPages();
        // this.setExtenalPages(pages);
        // }
        //
        // for (IWizardPage page : getExtenalPages()) {
        // addPage(page);
        // }

        // if (((AnalysisLabelParameter) parameter).isSemanticDiscoveryAnalysis()) {
        // selectedWizard.setForcePreviousAndNextButtons(true);
        // ISemanticStudioService service = CorePlugin.getDefault().getSemanticStudioService();
        // if (service != null) {
        // Wizard semanticDiscoveryWizard = service.getSemanticDiscoveryWizard(null);
        // IWizardPage[] pages = semanticDiscoveryWizard.getPages();
        // ((ColumnWizard) selectedWizard).setExtenalPages(pages);
        // }
        // }
        //
        // ISemanticStudioService service = CorePlugin.getDefault().getSemanticStudioService();
        // if (service != null) {
        // SemanticDiscoveryWizard semanticDiscoveryWizard = (SemanticDiscoveryWizard)
        // service.getSemanticDiscoveryWizard(null);
        //
        // }

        // addPage(new ColumnAnalysisDOSelectionPage());
        //
        //        ExploreDataPage _1_exploreDataPage = new ExploreDataPage("Explore Data", metadataTable); //$NON-NLS-1$
        //
        //        MapOntologyPage _2_mapOntologyPage = new MapOntologyPage("Map Ontology", metadataTable); //$NON-NLS-1$
        //
        //        EnrichOntRepoPage _3_enrichOntRepoPage = new EnrichOntRepoPage("Enrich Ontology Repository", metadataTable); //$NON-NLS-1$

        //                RecommendAnalysisPage _4_recommendAnalysisPage = new RecommendAnalysisPage(metadataTable); //$NON-NLS-1$

        // addPage(_1_exploreDataPage);
        // addPage(_2_mapOntologyPage);
        // addPage(_3_enrichOntRepoPage);
        // addPage(_4_recommendAnalysisPage);
        //
        // // // when from the right menu, no need to show select data wizard
        // // if (parameter.getConnectionRepNode() == null) {
        // ISemanticStudioService service = CorePlugin.getDefault().getSemanticStudioService();
        // if (service != null) {
        // selectionPage = service.getSemanticDiscoveryWizard(null);
        // }
        // addPage(selectionPage);
        // // }
        for (IWizardPage page : getExtenalPages()) {
            addPage(page);
        }
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see org.talend.dataprofiler.core.ui.wizard.AbstractWizard#performFinish()
    // */
    // @Override
    // public boolean performFinish() {
    // // super.performFinish();
    // // this.setForcePreviousAndNextButtons(true);
    // // this.selectionPage.nodes
    // ISemanticStudioService service = CorePlugin.getDefault().getSemanticStudioService();
    // if (service != null) {
    // int openSemanticDiscoveryWizard = service.openSemanticDiscoveryWizard(null);
    // // Wizard semanticDiscoveryWizard = service.getSemanticDiscoveryWizard(null);
    // // IWizardPage[] pages = semanticDiscoveryWizard.getPages();
    // // this.setExtenalPages(pages);
    // }
    // return true;
    // }
}
