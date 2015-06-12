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

package org.talend.dataquality.datamasking;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.duplicating.RandomWrapper;

public abstract class DataMasker<TIn, TOut> {

    private static RandomWrapper rnd = null;

    public DataMasker() {
        setRnd(new RandomWrapper());
    }

    public DataMasker(long seed) {
        setRnd(new RandomWrapper(seed));
    }

    protected abstract TOut generateOutput(TIn v, boolean isOriginal);

    public List<TOut> process(TIn v, boolean keepOriginal) {
        List<TOut> reslutList = new ArrayList<TOut>();

        if (keepOriginal) {
            reslutList.add(generateOutput(v, true));
        }
        reslutList.add(generateOutput(v, false));

        return reslutList;
    }

    public static RandomWrapper getRnd() {
        return rnd;
    }

    public static void setRnd(RandomWrapper rnd) {
        DataMasker.rnd = rnd;
    }
}