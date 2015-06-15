package org.talend.dataprofiler.service;

import org.talend.core.model.metadata.builder.connection.MetadataTable;

public interface ISemanticStudioService {

    // void addAnalysisToRef(Analysis analysis);

    int openSemanticDiscoveryWizard(MetadataTable metadataTable);

}
