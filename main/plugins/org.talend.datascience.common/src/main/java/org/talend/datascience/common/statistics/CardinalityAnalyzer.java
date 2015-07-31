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
package org.talend.datascience.common.statistics;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.BaseStream;

import org.talend.datascience.common.inference.Analyzer;

/**
 * Be caution that this implementation will lead to serious memory issues when data becoming large. Use {
 * {@link #CardinalityAnalyzer()} instead by loose the precision.
 * 
 * @author zhao
 *
 */
public class CardinalityAnalyzer implements Analyzer<CardinalityStatistics> {

    private BaseStream dataStream = null;
    @Override
    public void init() {
        dataStream = new BaseStream(){

            @Override
            public Iterator iterator() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Spliterator spliterator() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isParallel() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public BaseStream sequential() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public BaseStream parallel() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public BaseStream unordered() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public BaseStream onClose(Runnable closeHandler) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void close() {
                // TODO Auto-generated method stub
                
            }
            
        };
    }

    @Override
    public boolean analyze(String... record) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void end() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<CardinalityStatistics> getResult() {
        // TODO Auto-generated method stub
        return null;
    }

}
