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
package org.talend.datascience.common.inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.datascience.common.inference.type.DataType;

public class Analyzers implements Analyzer<Analyzers.Result> {

    private final Analyzer[] executors;

    private final List<Analyzers.Result> results = new ArrayList<Result>();

    private Analyzers(Analyzer... executors) {
        this.executors = executors;
    }

    public static Analyzer<Analyzers.Result> with(Analyzer... analyzers) {
        return new Analyzers(analyzers);
    }

    /**
     * Resize (if needed) the list of data types (a {@link DataType data type} per column in row). Method does not
     * support scale down.
     *
     * @param size The new size (must be > 0).
     */
    private void resize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be a positive number.");
        }
        final int missing = size - results.size();
        for (int i = 0; i < missing; i++) {
            results.add(new Result());
        }
    }

    public void init() {
        for (Analyzer executor : executors) {
            executor.init();
        }
    }

    public boolean analyze(String... record) {
        boolean result = true;
        resize(record.length);
        for (Analyzer executor : executors) {
            result &= executor.analyze(record);
        }
        return result;
    }

    public void end() {
        for (Analyzer executor : executors) {
            executor.end();
        }
    }

    public List<Result> getResult() {
        for (Analyzer executor : executors) {
            final List executorResult = executor.getResult();
            for (int i = 0; i < executorResult.size(); i++) {
                for (int j = 0; j < executorResult.size(); j++) {
                    results.get(j).add(executorResult.get(j));
                }
            }
        }
        return results;
    }

    public static class Result {

        private final Map<Class<?>, Object> results = new HashMap<Class<?>, Object>();

        public <T> T get(Class<T> clazz) {
            if (results.containsKey(clazz)) {
                return clazz.cast(results.get(clazz));
            }
            throw new IllegalArgumentException("No result of type '" + clazz.getName() + "'.");
        }

        public void add(Object result) {
            results.put(result.getClass(), result);
        }
    }
}
