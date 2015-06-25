package org.talend.dataprofiler.service;

import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.dataquality.analysis.Analysis;

public interface ISemanticStudioService {

    void addAnalysisToRef(Analysis analysis);

    int openSemanticDiscoveryWizard(MetadataTable metadataTable);

}
