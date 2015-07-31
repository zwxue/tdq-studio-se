package org.talend.datascience.common.inference;

import java.util.List;
import java.util.stream.Stream;

/**
 * Implements analysis on array of Strings ("row" of values). Implementations are expected to be:
 * <ul>
 *     <li>Stateful.</li>
 *     <li>Not thread safe (no need to enforce thread safety in implementations).</li>
 * </ul>
 * To combine several {@link Analyzer} together see {@link Analyzers}.
 * @param <T> The type of results built by the implementation.
 */
public interface Analyzer<T> {

    /**
     * Prepare implementation for analysis. Implementations may perform various tasks like internal initialization or
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
     * Get the analysis result based on values submitted in {@link #analyze(String[])}.
     *
     * @return The analysis result for each columns in records, item <i> n </i> in list corresponds to <i> nth </i>
     * column in record.
     */
    List<T> getResult();
}
