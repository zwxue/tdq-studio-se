package org.talend.dataquality.standardization.query.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.standardization.constant.PluginConstant;
import org.talend.dataquality.standardization.query.FirstNameStandardize;

public class FirstNameStandardizeTest extends TestCase {

//    private String filename = "./data/TalendGivenNames.TXT";

    private String indexfolder = "./data/TalendGivenNames_index";

    private IndexSearcher searcher = null;

    private Analyzer searchAnalyzer = null;

    public void testStandardizeStringString() throws IOException, ParseException {
        Directory dir = FSDirectory.open(new File(indexfolder));
        searcher = new IndexSearcher(dir);
        searchAnalyzer = new SimpleAnalyzer();
        String inputName = "Philippe";
        Map<String, String> information2value = new HashMap<String, String>();
        information2value.put("country", "mosikou");
        information2value.put("gender", "0");
        getReplaceSearchResult(indexfolder, inputName, information2value, false);
        searcher.close();
    }

    private void getReplaceSearchResult(String folderName, String inputName, Map<String, String> information2value,
            boolean fuzzyQuery) throws IOException, ParseException {
        if (inputName != null && folderName != null) {
            FirstNameStandardize stdname = new FirstNameStandardize(searcher, searchAnalyzer, 10);
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
            if (countryText == null && genderText == null) {
                try {
                    stdname.replaceName(inputName, fuzzyQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (countryText != null && genderText != null) {
                try {
                    stdname.replaceNameWithCountryGenderInfo(inputName, countryText, genderText, fuzzyQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (countryText != null && genderText == null) {
                try {
                    stdname.replaceNameWithCountryInfo(inputName, countryText, fuzzyQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (countryText == null && genderText != null) {
                try {
                    stdname.replaceNameWithGenderInfo(inputName, genderText, fuzzyQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
