// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.standardization.query;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;
import org.talend.dataquality.standardization.constant.PluginConstant;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class FirstNameStandardize {

    private Analyzer analyzer;

    private IndexSearcher searcher;

    private int hits;

    public FirstNameStandardize(IndexSearcher indexSearcher, Analyzer analyzer, int hitsPerPage) throws IOException {
        assert analyzer != null;
        assert indexSearcher != null;
        this.analyzer = analyzer;
        this.searcher = indexSearcher;
        this.hits = hitsPerPage;
    }

    public ScoreDoc[] standardize(String searchType,String input) throws ParseException, IOException {
     
    	if (input == null || input.length() == 0) {
            return new ScoreDoc[0];
        }
        Query q = new QueryParser(Version.LUCENE_30, searchType, analyzer).parse(input); // TODO do not harcode field name
        Query qalias = new  QueryParser(Version.LUCENE_30, PluginConstant.FIRST_NAME_STANDARDIZE_ALIAS, analyzer).parse(input); // TODO do not harcode field
        // name
        
        q = q.combine(new Query[] { q, qalias });

        // TODO do we need to create the doc collector every time?
        TopDocsCollector<?> collector = createTopDocsCollector();

        searcher.search(q, collector);

        // TODO find how to do a fuzzy search
        // A fuzzy search should be done if the normal search gives no result.
        int hits=collector.topDocs().scoreDocs.length;
        if(hits==0){
        	try {
				getFuzzySearch(input,collector);
				return collector.topDocs().scoreDocs;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return collector.topDocs().scoreDocs;

    }

	public void getFuzzySearch(String input, TopDocsCollector<?> collector)
			throws Exception {
		Query q = new FuzzyQuery(new Term(
				PluginConstant.FIRST_NAME_STANDARDIZE_NAME, input));
		Query qalias = new FuzzyQuery(new Term(
				PluginConstant.FIRST_NAME_STANDARDIZE_ALIAS, input));
		q = q.combine(new Query[] { q, qalias });
		searcher.search(q, collector);
	}

    /**
     * Method "replaceName".
     * 
     * @param input a first name
     * @return the standardized first name
     * @throws ParseException
     * @throws IOException
     */
    public String replaceName(String searchType,String input) throws ParseException, IOException {
        ScoreDoc[] results = standardize(searchType,input);
        return results.length == 0 ? input : searcher.doc(results[0].doc).get("name");
    }

    // FIXME this variable is only for tests
    public static final boolean SORT_WITH_COUNT = true;

    private TopDocsCollector<?> createTopDocsCollector() throws IOException {
        // TODO the goal is to sort the result in descending order according to the "count" field
        if (SORT_WITH_COUNT) { // TODO enable this when it works correctly
            SortField sortfield = new SortField(PluginConstant.FIRST_NAME_STANDARDIZE_COUNT, SortField.INT); // TODO do not harcode field name
            Sort sort = new Sort(sortfield);
            // results are sorted according to a score and then to the count value
            return TopFieldCollector.create(sort, hits, false, false, false, false);
        } else {
            return TopScoreDocCollector.create(hits, false);
        }
    }
}
