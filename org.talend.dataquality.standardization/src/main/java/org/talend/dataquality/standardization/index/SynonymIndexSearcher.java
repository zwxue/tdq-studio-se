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
package org.talend.dataquality.standardization.index;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @author scorreia A class to create an index with synonyms.
 */
public class SynonymIndexSearcher {

    public static final String F_WORD = "word";

    public static final String F_SYN = "syn";

    private IndexSearcher searcher;

    private int topDocLimit = 1;

    private Analyzer analyzer;

    /**
     * by default, the slop factor is zero, meaning an exact phrase match.
     */
    private int slop = 0;

    /**
     * instantiate an index searcher. A call to the index initialization method such as {@link #openIndexInFS(String)}
     * is required before using any other method.
     */
    public SynonymIndexSearcher() {
    }

    /**
     * SynonymIndexSearcher constructor creates this searcher and initializes the index.
     * 
     * @param indexPath the path to the index.
     */
    public SynonymIndexSearcher(String indexPath) {
        try {
            openIndexInFS(indexPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    SynonymIndexSearcher(IndexSearcher indexSearcher) {
        this.searcher = indexSearcher;
    }

    // public void initIndexInRAM() throws IOException {
    // // indexDir = new RAMDirectory();
    // // FIXME this method is not tested and cannot work because the IndexSearcher is only a FS IndexSearcher.
    // }

    /**
     * Method "openIndexInFS" opens a FS folder index.
     * 
     * @param path the path of the index folder
     * @throws IOException if file does not exist, or any other problem
     */
    public void openIndexInFS(String path) throws IOException {
        FSDirectory indexDir = FSDirectory.open(new File(path));
        CheckIndex check = new CheckIndex(indexDir);
        Status status = check.checkIndex();
        if (status.missingSegments) {
            System.err.println("Failed to load index. Please make sure it's not empty.\n");
        }
        this.searcher = new IndexSearcher(indexDir);
    }

    /**
     * search a document by the word.
     * 
     * @param word
     * @return
     * @throws IOException
     */
    public TopDocs searchDocumentByWord(String word) {
        if (word == null) {
            return null;
        }
        TopDocs docs = null;
        try {
            Query query = createWordQueryFor(word);
            docs = this.searcher.search(query, topDocLimit);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return docs;
    }

    /**
     * search a document by one of the synonym (which may be the word).
     * 
     * @param synonym
     * @return
     * @throws ParseException if the given synonym cannot be parsed as a lucene query.
     * @throws IOException
     */
    public TopDocs searchDocumentBySynonym(String synonym) throws ParseException, IOException {
        Query query = createQueryFor(synonym, F_SYN);
        return this.searcher.search(query, topDocLimit);
    }

    /**
     * Count the synonyms of the first document found by a query on word.
     * 
     * @param word
     * @return the number of synonyms
     */
    public int getSynonymCount(String word) {
        try {
            Query query = createWordQueryFor(word);
            TopDocs docs;
            docs = this.searcher.search(query, topDocLimit);
            if (docs.totalHits > 0) {
                Document doc = this.searcher.doc(docs.scoreDocs[0].doc);
                String[] synonyms = doc.getValues(F_SYN);
                return synonyms.length - 1; // we don't count the word which is added as the first synonym.
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get a document from search result by its document number.
     * 
     * @param docNum the doc number
     * @return the document (can be null if any problem)
     */
    public Document getDocument(int docNum) {
        Document doc = null;
        try {
            doc = this.searcher.doc(docNum);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Method "getWordByDocNumber".
     * 
     * @param docNo the document number
     * @return the document or null
     */
    public String getWordByDocNumber(int docNo) {
        Document document = getDocument(docNo);
        return document != null ? document.getValues(F_WORD)[0] : null;
    }

    /**
     * Method "getSynonymsByDocNumber".
     * 
     * @param docNo the doc number
     * @return the synonyms or null if no document is found
     */
    public String[] getSynonymsByDocNumber(int docNo) {
        Document document = getDocument(docNo);
        return document != null ? document.getValues(F_SYN) : null;
    }

    /**
     * Method "getNumDocs".
     * 
     * @return the number of documents in the index
     */
    public int getNumDocs() {
        return this.searcher.getIndexReader().numDocs();
    }

    /**
     * Getter for topDocLimit.
     * 
     * @return the topDocLimit
     */
    public int getTopDocLimit() {
        return this.topDocLimit;
    }

    /**
     * Method "setTopDocLimit" set the maximum number of documents to return after a search.
     * 
     * @param topDocLimit the limit
     */
    public void setTopDocLimit(int topDocLimit) {
        this.topDocLimit = topDocLimit;
    }

    /**
     * Getter for slop. The slop is the maximum number of moves allowed to put the terms in order.
     * 
     * @return the slop
     */
    public int getSlop() {
        return this.slop;
    }

    /**
     * Sets the slop.
     * 
     * @param slop the slop to set
     */
    public void setSlop(int slop) {
        this.slop = slop;
    }

    /**
     * Method "setAnalyzer".
     * 
     * @param analyzer the analyzer to use in searches.
     */
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * 
     * @return the analyzer used in searches (StandardAnalyzer by default)
     */
    public Analyzer getAnalyzer() {
        if (analyzer == null) {
            analyzer = new StandardAnalyzer(Version.LUCENE_30);
        }
        return this.analyzer;
    }

    private Query createWordQueryFor(String stringToSearch) throws ParseException {
        PhraseQuery query = new PhraseQuery();
        query.add(new Term(F_WORD, stringToSearch));
        return query;
    }

    private Query createQueryFor(String stringToSearch, String field) throws ParseException {
        QueryParser parser = new QueryParser(Version.LUCENE_30, field, this.getAnalyzer());
        parser.setPhraseSlop(slop);
        return parser.parse(stringToSearch);

        // old code
        // return new TermQuery(new Term(field, stringToSearch.toLowerCase()));

    }

    public void close() {
        try {
            searcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
