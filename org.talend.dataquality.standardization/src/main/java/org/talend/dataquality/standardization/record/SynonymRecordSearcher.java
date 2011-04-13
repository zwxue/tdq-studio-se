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
package org.talend.dataquality.standardization.record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * @author scorreia
 * 
 * This class searches in a list of indexes.
 */
public class SynonymRecordSearcher {

    private SynonymIndexSearcher[] searchers;

    private int recordSize = 0;

    // FIXME can be removed
    static int counts[];

    /**
     * SynonymRecordSearcher constructor.
     * 
     * @param size the size of the record which will be searched
     */
    public SynonymRecordSearcher(int size) {
        this.recordSize = size;
        searchers = new SynonymIndexSearcher[recordSize];
        counts = new int[size];
    }

    /**
     * SynonymRecordSearcher represents a match in the index with its score and the input
     */
    static class WordResult {

        /**
         * the input word searched in the index
         */
        String input;

        /**
         * The word found in the index that matches the input
         */
        String word;

        /**
         * The score of the match
         */
        float score;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "WordResult=" + this.input + "->" + this.word + "; score=" + score;
        }

    } // EOC WordResult

    /**
     * 
     * @author scorreia
     * 
     * RecordResult: a class that transforms an input record into a list of output records.
     * 
     */
    static class RecordResult {

        /**
         * The input record.
         */
        String[] record;

        /**
         * The results of a search.
         */
        List<List<WordResult>> wordResults = new ArrayList<List<WordResult>>();

        /**
         * @deprecated
         * 
         * Method "computeOutputRows" computes the output rows
         * 
         * @return the list of OutputRecords
         */
        public List<OutputRecord> computeOutputRows3() {
            // compute how many rows will be output
            int[] limits = new int[wordResults.size()];
            int[] counts = new int[wordResults.size()];
            for (int i = 0; i < wordResults.size(); i++) {
                limits[i] = wordResults.get(i).size();
                counts[i] = 0;
            }

            Set<OutputRecord> outputRecords = new HashSet<OutputRecord>();
            outputRecords = incrementalOutput(limits, outputRecords);

            return new ArrayList<OutputRecord>(outputRecords);
        }

        public Set<OutputRecord> incrementalOutput(int[] limits, Set<OutputRecord> outputRecords) {

            OutputRecord outputRec = new OutputRecord(limits.length);
            float score = 0;
            StringBuffer scores = new StringBuffer();
            for (int j = 0; j < limits.length; j++) { // field
                List<WordResult> listwrec = wordResults.get(j);
                int wResIdx = counts[j];
                WordResult wordResult = listwrec.get(wResIdx);
                outputRec.record[j] = wordResult.word;
                score += wordResult.score; // TODO add multiplicative weight here if needed
                scores.append("|").append(wordResult.score);
            }
            outputRec.scores = new String(scores.deleteCharAt(0));
            outputRec.score = score / limits.length;
            // add the record to the set
            outputRecords.add(outputRec);

            counts[0]++;
            for (int i = 0; i < limits.length; i++) {
                if (counts[i] == limits[i]) {
                    if (i == limits.length - 1) {
                        return outputRecords;
                    } else {
                        counts[i] = 0;
                        counts[i + 1]++;
                        continue;
                    }
                }
            }
            return incrementalOutput(limits, outputRecords);
        }

        /**
         * @deprecated
         * 
         * Old code
         * 
         * @return
         */
        public List<OutputRecord> computeOutputRows2() {
            // compute how many rows will be output
            int nb = 1;
            for (List<WordResult> wResults : wordResults) {
                nb = wResults.size() * nb;
            }

            // build each row
            Set<OutputRecord> outputRecords = new HashSet<OutputRecord>();
            int recordLength = record.length;

            for (int i = 0; i < nb; i++) { // row
                OutputRecord outputRec = new OutputRecord(recordLength);

                float score = 0;
                StringBuffer scores = new StringBuffer();
                for (int j = 0; j < recordLength; j++) { // field
                    List<WordResult> listwrec = wordResults.get(j);
                    // System.out.println(listwrec);
                    int wResIdx = i % listwrec.size();
                    WordResult wordResult = listwrec.get(wResIdx);
                    // System.out.println(wordResult.word);
                    outputRec.record[j] = wordResult.word;
                    score += wordResult.score; // TODO add multiplicative weight here if needed
                    scores.append("|").append(wordResult.score);
                    // System.out.println((i + 1) + ": " + Arrays.asList(outputRec.getRecord()));
                    // System.out.println("");
                }
                outputRec.scores = new String(scores.deleteCharAt(0));
                outputRec.score = score / recordLength;
                // add the record to the set
                outputRecords.add(outputRec);
            }

            return new ArrayList<OutputRecord>(outputRecords);
        }

        /**
         * Method "computeOutputRows" computes the output rows
         * 
         * @return the list of OutputRecords
         */
        public List<OutputRecord> computeOutputRows() {
            Set<OutputRecord> outputRecords = new HashSet<OutputRecord>();
            computeOutputRows(record.length, new ArrayList<WordResult>(), wordResults, outputRecords);
            return new ArrayList<OutputRecord>(outputRecords);
        }

        /**
         * Method "computeOutputRows".
         * 
         * @param recordLength the record length
         * @param foundWords the list of word result that constitute the begining of the current output record
         * @param wrs the list of remaining word results
         * @param outputRows the output records (updated each time this method is called)
         */
        private void computeOutputRows(int recordLength, List<WordResult> foundWords, List<List<WordResult>> wrs,
                Set<OutputRecord> outputRows) {
            assert !wrs.isEmpty();
            // handle last vector of word results
            if (wrs.size() == 1) {
                List<WordResult> lastWR = wrs.get(0);
                for (WordResult wordResult : lastWR) {
                    OutputRecord outputRec = new OutputRecord(recordLength);
                    for (int i = 0; i < foundWords.size(); i++) {
                        updateOutputRec(outputRec, i, foundWords.get(i));
                    }
                    updateOutputRec(outputRec, foundWords.size(), wordResult);

                    outputRows.add(outputRec);
                }
            } else { // recusive call on a sublist
                List<WordResult> firstWR = wrs.get(0);
                List<List<WordResult>> sublist = wrs.subList(1, wrs.size());
                for (WordResult wordResult : firstWR) {
                    List<WordResult> wr = new ArrayList<WordResult>(foundWords);
                    wr.add(wordResult);
                    computeOutputRows(recordLength, wr, sublist, outputRows);
                }
            }
        }

        private void updateOutputRec(OutputRecord outputRec, int idx, WordResult wordResult) {
            outputRec.record[idx] = wordResult.word;
            outputRec.score += wordResult.score; // TODO add multiplicative weight here if needed
            outputRec.scores += "|" + wordResult.score;
        }

    }

    /**
     * method "search".
     * 
     * @param maxNbOutputResults the number of results to return
     * @param record a list of fields that will be search in the indexes (all fields of the record will be searched)
     * @throws IOException
     * @throws ParseException if the searched field cannot be parsed as a query
     */
    public List<OutputRecord> search(int maxNbOutputResults, String[] record) throws ParseException, IOException {
        assert record != null;

        // List<RecordResult> recResults = new ArrayList<SynonymRecordSearcher.RecordResult>();
        RecordResult recRes = new RecordResult();

        // search each field in the appropriate index
        for (int i = 0; i < record.length; i++) {
            List<WordResult> wResults = new ArrayList<WordResult>();
            String field = record[i];

            // search this field in one index
            SynonymIndexSearcher searcher = searchers[i];
            TopDocs docs = searcher.searchDocumentBySynonym(field);
            int nbDocs = Math.min(searcher.getTopDocLimit(), docs.totalHits);

            // store all found words in a list of results for this field
            for (int j = 0; j < nbDocs; j++) {
                ScoreDoc scoreDoc = docs.scoreDocs[j];
                String foundWord = searcher.getWordByDocNumber(scoreDoc.doc);
                WordResult wordRes = new WordResult();
                wordRes.input = field;
                wordRes.word = foundWord;
                wordRes.score = scoreDoc.score;
                wResults.add(wordRes);
            }

            // create
            recRes.record = record;
            recRes.wordResults.add(wResults);
        }
        List<OutputRecord> outputRecords = recRes.computeOutputRows();
        Collections.sort(outputRecords);
        maxNbOutputResults = Math.min(outputRecords.isEmpty() ? 0 : outputRecords.size(), maxNbOutputResults);
        return outputRecords.subList(0, maxNbOutputResults);
    }

    /**
     * Method "addSearcher" add a searcher to the list of searchers at the given column index.
     * 
     * @param searcher the searcher
     * @param columnIndex the column index
     */
    public void addSearcher(SynonymIndexSearcher searcher, int columnIndex) {
        assert columnIndex < this.recordSize;
        if (searcher == null) {
            throw new NullPointerException("tried to set a null Synonym index searcher at " + columnIndex);
        }
        searchers[columnIndex] = searcher;
    }

    /**
     * Method "getSearcher".
     * 
     * @param columnIndex the column index
     * @return the SynonymIndexSearcher added at this column index.
     */
    public SynonymIndexSearcher getSearcher(int columnIndex) {
        return searchers[columnIndex];
    }
}
