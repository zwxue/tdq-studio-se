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

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.service.ISemanticStudioService;

/**
 * created by scorreia on Oct 1, 2013 Detailled comment
 * 
 */
public class SuggestAnalysisAction extends Action {

    private static Logger log = Logger.getLogger(SuggestAnalysisAction.class);

    private ISemanticStudioService service;

    private MetadataTable metadataTable;

    public SuggestAnalysisAction(MetadataTable set) {
        this();
        this.metadataTable = set;
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

        ISemanticStudioService service = CorePlugin.getDefault().getSemanticStudioService();

        service.recommandAnalysis(this.metadataTable);

    }

}
