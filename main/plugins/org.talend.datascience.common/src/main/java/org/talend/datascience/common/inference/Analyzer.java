package org.talend.datascience.common.inference;

import java.util.List;

public interface Analyzer<T> {

    /**
     * Prepare implementation for inferring. Implementations may perform various tasks like internal initialization or
     * connection establishment. This method should only be called once.
     */
    void init();

    /**
     * Analyze a single record (row).
     * 
     * @param record A record (row) with a value in each column.
     * @return <code>true</code> if analyze was ok, <code>false</code> otherwise.
     */
    boolean analyze(String... record);

    /**
     * Ends the analysis (implementations may perform result optimizations after the repeated call to
     * {@link #analyze(String[])}).
     */
    void end();

    /**
     * Get the inferring result, this method should be called once and only once after {{@link #analyze(String[])}
     * method .
     *
     * @return The analysis result for each columns in records, item <i> n </i> in list corresponds to <i> nth </i>
     * column in record.
     */
    List<T> getResult();
}
