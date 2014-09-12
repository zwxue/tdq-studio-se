// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions.predefined;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.ontology.repository.enrichment.AnalysisGenerator;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * created by scorreia on Oct 1, 2013 Detailled comment
 * 
 */
public class SuggestAnalysisAction extends Action {

    private static Logger log = Logger.getLogger(SuggestAnalysisAction.class);

    private MetadataTable set;

    public SuggestAnalysisAction(MetadataTable set) {
        this();
        this.set = set;
    }

    public SuggestAnalysisAction() {
        super("Suggest Analysis"); // TODO externalize
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EXPLORE_IMAGE)); // TODO
        // change
        // icon
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        // TODO scorreia implement me

        Analysis analyses = generateAnalysis(this.set);
        // generate property file
        // IFolder folder = analysis.eResource().getURI();
        AnalysisWriter analysisWriter = ElementWriterFactory.getInstance().createAnalysisWrite();

        // for (Analysis analysis : analyses) {
        // add resource to save analysis
        // File outfile = new
        // File("C:/runtime-top.product/TOP_DEFAULT_PRJ/TDQ_Data Profiling/Analyses/"+analysis.getName()+".ana");//
        // save analysis
        // EMFUtil emfUtil = new EMFUtil();
        // ResourceSet rs = emfUtil.getResourceSet();
        // Resource outResource =
        // rs.createResource(URI.createFileURI(outfile.getAbsolutePath()));
        // outResource.getContents().add(analysis);
        // analysisWriter.save(analysis, true);

        TypedReturnCode<Object> code = analysisWriter.create(analyses, ResourceManager.getAnalysisFolder(), false);

        analysisWriter.save((Item) code.getObject(), true);
        // }
    }

    /**
     * DOC scorreia Comment method "generateAnalysis".
     * 
     * @param table
     * @return
     */
    private Analysis generateAnalysis(MetadataTable table) {
        try {
            // initialize path parameters
            // add parameter to AnalysisGenerator constructor (pathref,
            // pathMetaSem)
            AnalysisGenerator gen = new AnalysisGenerator();
            return gen.handleTableItem(table);

        } catch (FileNotFoundException e) {
            log.error(e, e);
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

}
