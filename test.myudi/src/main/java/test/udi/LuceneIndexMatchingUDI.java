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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.JavaUDIIndicatorParameter;
import org.talend.dataquality.indicators.sql.impl.UserDefIndicatorImpl;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * @author sizhaoliu
 * 
 * Java implementation of a user defined indicator who returns matching count from a lucene index.
 */
public class LuceneIndexMatchingUDI extends UserDefIndicatorImpl {

    private SynonymIndexSearcher searcher;

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
            searcher.openIndexInFS(path);
            searcher.setTopDocLimit(1);
            searcher.setAnalyzer(new StandardAnalyzer(Version.LUCENE_30));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.prepare();
    }

    @Override
    public boolean reset() {
        super.reset();
        this.matchingValueCount = 0L;
        this.notMatchingValueCount = NOT_MATCHING_VALUE_COUNT_EDEFAULT;
        return true;
    }

    @Override
    public boolean handle(Object data) {
        super.handle(data); // check null, increment count
        try {
            // verify the existence of the data in a lucene index.
            TopDocs docs;
            docs = searcher.searchDocumentBySynonym(data.toString());
            if (docs.totalHits > 0) {
                matchingValueCount++;
            }
        } catch (ParseException e) {
            System.err.println("Parse failed for <" + data + ">");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.Indicator#finalizeComputation()
     */
    public boolean finalizeComputation() {
        System.err.println(matchingValueCount + " records matched.");
        // compute non matching value
        this.notMatchingValueCount = count - matchingValueCount;
        System.err.println(notMatchingValueCount + " records unmatched.");
        searcher.close();
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.sql.impl.UserDefIndicatorImpl#getUserCount()
     */
    @Override
    public Long getUserCount() {
        return this.matchingValueCount;
    }

}
