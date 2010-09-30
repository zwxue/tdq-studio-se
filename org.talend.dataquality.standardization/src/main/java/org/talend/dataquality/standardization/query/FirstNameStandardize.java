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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import org.apache.lucene.search.TopDocs;
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

    private int hitsPerPage;

    public FirstNameStandardize(IndexSearcher indexSearcher, Analyzer analyzer, int hitsPerPage) throws IOException {
        assert analyzer != null;
        assert indexSearcher != null;
        this.analyzer = analyzer;
        this.searcher = indexSearcher;
        this.hitsPerPage = hitsPerPage;
    }

    private ScoreDoc[] standardize(String input, boolean fuzzyQuery) throws ParseException, IOException {

        if (input == null || input.length() == 0) {
            return new ScoreDoc[0];
        }
        Query q = new QueryParser(Version.LUCENE_30, PluginConstant.FIRST_NAME_STANDARDIZE_NAME, analyzer).parse(input);

        TopDocsCollector<?> collector = createTopDocsCollector();
        searcher.search(q, collector);

        if (fuzzyQuery) {
            try {

                return getFuzzySearch(input).scoreDocs;
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return collector.topDocs().scoreDocs;

    }

    public void getFuzzySearch(String input, TopDocsCollector<?> collector) throws Exception {
        Query q = new FuzzyQuery(new Term(PluginConstant.FIRST_NAME_STANDARDIZE_NAME, input));
        Query qalias = new FuzzyQuery(new Term(PluginConstant.FIRST_NAME_STANDARDIZE_ALIAS, input));
        q = q.combine(new Query[] { q, qalias });
        searcher.search(q, collector);
    }

    public TopDocs getFuzzySearch(String input) throws Exception {
        Query q = new FuzzyQuery(new Term("name", input), 0.5f, 0);
        TopDocs matches = searcher.search(q, 10);
        return matches;
    }



    // FIXME this variable is only for tests
    public static final boolean SORT_WITH_COUNT = true;

    public ScoreDoc[] standardize(String inputName, Map<String, String> information2value, boolean fuzzySearch) throws Exception {
        if (inputName == null || inputName.length() == 0) {
            return new ScoreDoc[0];
        }
        // // DOC set get county and gender fields value
        String countryText = null;
        String genderText = null;
        Set<String> indexKinds = information2value.keySet();
        for (String indexKind : indexKinds) {
            if (indexKind.equals(PluginConstant.FIRST_NAME_STANDARDIZE_COUNTRY)) {
                countryText = information2value.get(indexKind);
            } else if (indexKind.equals(PluginConstant.FIRST_NAME_STANDARDIZE_GENDER)) {
                genderText = information2value.get(indexKind);
            }
        }
        // create all Term for query
        Term termName = new Term(PluginConstant.FIRST_NAME_STANDARDIZE_NAME, inputName);
        Term termCountry = new Term(PluginConstant.FIRST_NAME_STANDARDIZE_COUNTRY, countryText);
        Term ternGender = new Term(PluginConstant.FIRST_NAME_STANDARDIZE_GENDER, genderText);
        // many field to query for reposity
        Query nameQuery = new QueryParser(Version.LUCENE_30, PluginConstant.FIRST_NAME_STANDARDIZE_NAME, analyzer)
                .parse(inputName);
        Query countryQuery = null;
        Query genderQuery = null;
        if (countryText != null && !countryText.equals("")) {
            countryQuery = new QueryParser(Version.LUCENE_30, PluginConstant.FIRST_NAME_STANDARDIZE_COUNTRY, analyzer)
                    .parse(countryText);
            nameQuery = nameQuery.combine(new Query[] { nameQuery, countryQuery });
            if (genderText != null && !genderText.equals("")) {
                genderQuery = new QueryParser(Version.LUCENE_30, PluginConstant.FIRST_NAME_STANDARDIZE_GENDER, analyzer)
                        .parse(genderText);
                nameQuery = nameQuery.combine(new Query[] { nameQuery, countryQuery, genderQuery });
            }
        }

        TopDocs matches = searcher.search(nameQuery, 10);

        if (fuzzySearch) {
            Query name = new FuzzyQuery(termName);
            Query country = null;
            Query gender = null;
            if (countryText != null && !countryText.equals("")) {
                country = new FuzzyQuery(termCountry);
                name = name.combine(new Query[] { name, country });
                if (genderText != null && !genderText.equals("")) {
                    gender = new FuzzyQuery(ternGender);
                    name = name.combine(new Query[] { name, country, gender });
                }
            }

            matches = searcher.search(name, 10);
        }
        return matches.scoreDocs;
    }

    private TopDocsCollector<?> createTopDocsCollector() throws IOException {
        // TODO the goal is to sort the result in descending order according to the "count" field
        if (SORT_WITH_COUNT) { // TODO enable this when it works correctly
            SortField sortfield = new SortField(PluginConstant.FIRST_NAME_STANDARDIZE_COUNT, SortField.INT); // TODO do
            Sort sort = new Sort(sortfield);
            // results are sorted according to a score and then to the count value
            return TopFieldCollector.create(sort, hitsPerPage, false, false, false, false);
        } else {
            return TopScoreDocCollector.create(hitsPerPage, false);
        }
    }

    /**
     * Method "replaceName".
     * 
     * @param input a first name
     * @return the standardized first name
     * @throws ParseException
     * @throws IOException
     */
    public String replaceName(String inputName, boolean fuzzyQuery) throws ParseException, IOException {

        ScoreDoc[] results = null;
        try {
            results = standardize(inputName, fuzzyQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (results == null || results.length == 0) ? "" : searcher.doc(results[0].doc).get("name");
    }
    public String replaceNameWithCountryGenderInfo(String inputName, String inputCountry, String inputGender, boolean fuzzyQuery)
            throws Exception {
        Map<String, String> indexFields = new HashMap<String, String>();
        indexFields.put("country", inputCountry);
        indexFields.put("gender", inputGender);
        ScoreDoc[] results = standardize(inputName, indexFields, fuzzyQuery);
        return results.length == 0 ? "" : searcher.doc(results[0].doc).get("name");
    }

    public String replaceNameWithCountryInfo(String inputName, String inputCountry, boolean fuzzyQuery) throws Exception {
        Map<String, String> indexFields = new HashMap<String, String>();
        indexFields.put("country", inputCountry);
        ScoreDoc[] results = standardize(inputName, indexFields, fuzzyQuery);
        return results.length == 0 ? "" : searcher.doc(results[0].doc).get("name");
    }

    public String replaceNameWithGenderInfo(String inputName, String inputGender, boolean fuzzyQuery) throws Exception {
        Map<String, String> indexFields = new HashMap<String, String>();
        indexFields.put("gender", inputGender);
        ScoreDoc[] results;

        results = standardize(inputName, indexFields, fuzzyQuery);

        return results.length == 0 ? "" : searcher.doc(results[0].doc).get("name");
    }
}
