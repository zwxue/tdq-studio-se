package org.talend.dataprofiler.service;

import java.util.List;
import java.util.Map;

import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.dataprofiler.service.semantic.SemanticCategory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.utils.exceptions.TalendException;

public interface ISemanticStudioService {

    boolean suggestAnalysis(MetadataTable metadataTable);

    void addAnalysisToRef(Analysis analysis);

    /**
     * 
     * DOC zhao inferring the sementic category given an array of record. <br>
     * {{@link #getSemanticCategory()} is intended to be called after all data is passed to get the finally semantic
     * categories.
     * 
     * @param record
     * @return true if inferring is successfully done, false otherwise.
     * @throws TalendException thown when exceptional cases occur.
     */
    boolean inferCategory(Object[] record) throws TalendException;

    /**
     * 
     * DOC zhao get semantic category <br>
     * This method must be called after {{@link #inferCategory(Object[])}
     * 
     * @return A column index to category map , <Column index,List<Semantic category>>
     */
    Map<Integer, List<SemanticCategory>> getSemanticCategory();

}
