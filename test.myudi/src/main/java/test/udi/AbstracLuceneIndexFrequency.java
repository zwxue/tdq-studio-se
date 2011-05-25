// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package test.udi;

import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.JavaUDIIndicatorParameter;
import org.talend.dataquality.indicators.sql.impl.UserDefIndicatorImpl;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * DOC sizhaoliu class global comment. Detailled comment
 */
class AbstracLuceneIndexFrequency extends UserDefIndicatorImpl {

    protected SynonymIndexSearcher searcher;

    @Override
    public boolean prepare() {

        String path = "";
        if (parameters != null) {
            final Domain dataValidDomain = parameters.getIndicatorValidDomain();
            if (dataValidDomain != null) {
                for (JavaUDIIndicatorParameter param : dataValidDomain.getJavaUDIIndicatorParameter()) {
                    if (param != null) {
                        if ("index_path".equals(param.getKey())) {
                            path = param.getValue();
                            System.err.println("index path: " + path);
                        }
                    }
                }
            }
        } else {
            System.err.println("parameters is null");
        }

        searcher = new SynonymIndexSearcher();
        try {
            // searcher.setAnalyzer(builder.getAnalyzer());
            searcher.openIndexInFS(path);
            searcher.setTopDocLimit(1);
            searcher.setAnalyzer(new StandardAnalyzer(Version.LUCENE_30));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.prepare();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.Indicator#finalizeComputation()
     */
    public boolean finalizeComputation() {
        searcher.close();
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.Indicator#reset()
     */
    public boolean reset() {
        super.reset(); // reset the number of count
        this.valueToFreq = new HashMap<Object, Long>(); // should be done in super class
        return true;
    }

}
