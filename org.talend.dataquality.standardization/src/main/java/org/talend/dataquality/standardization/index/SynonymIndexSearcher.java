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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * @author scorreia A class to create an index with synonyms.
 */
public class SynonymIndexSearcher {

    public static final String F_WORD = "word";

    public static final String F_SYN = "syn";


    private IndexSearcher searcher;

    private int topDocLimit = 1;



    /**
     * instantiate an index builder
     */
    public SynonymIndexSearcher() {
    }

    public SynonymIndexSearcher(String indexPath) {
        try {
            initIndexInFS(indexPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public void initIndexInRAM() throws IOException {
    // // indexDir = new RAMDirectory();
    // // FIXME this method is not tested and cannot work because the IndexSearcher is only a FS IndexSearcher.
    // }

    public void initIndexInFS(String path) throws IOException {
        FSDirectory indexDir = FSDirectory.open(new File(path));
        CheckIndex check = new CheckIndex(indexDir);
        Status status = check.checkIndex();
        if (status.missingSegments) {
            System.err.println("Cannot initialize searcher: Segments file not found. ");
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
        Query query = new TermQuery(new Term(F_WORD, word));
        TopDocs docs = null;
        try {
            docs = this.searcher.search(query, topDocLimit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docs;
    }

    /**
     * search a document by one of the synonym (which may be the word).
     * 
     * @param synonym
     * @return
     * @throws IOException
     */
    public TopDocs searchDocumentBySynonym(String synonym) {
        Query query = new TermQuery(new Term(F_SYN, synonym.toLowerCase()));
        TopDocs docs = null;
        try {
            docs = this.searcher.search(query, topDocLimit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docs;
    }

    // TODO remove this code?
    // /**
    // * Getter for analyzer.
    // *
    // * @return the analyzer
    // * @throws IOException
    // */
    // private Analyzer getAnalyzer() throws IOException {
    // if (analyzer == null) {
    // // the entry and the synonyms are indexed as provided
    // // analyzer = new KeywordAnalyzer();
    //
    // analyzer = new StandardAnalyzer(Version.LUCENE_30);
    //
    // // analyzer = new SynonymAnalyzer();
    // }
    // return analyzer;
    // }

    /**
     * Count synonyms of a document
     * 
     * DOC sizhaoliu Comment method "getSynonymCount".
     * 
     * @param str
     * @return
     */
    public int getSynonymCount(String str) {
        Query query = new TermQuery(new Term("syn", str.toLowerCase()));
        TopDocs docs;
        try {
            docs = this.searcher.search(query, topDocLimit);
            if (docs.totalHits > 0) {
                Document doc = this.searcher.doc(docs.scoreDocs[0].doc);
                String[] synonyms = doc.getValues(F_SYN);
                return synonyms.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * get a document from search result by the number
     * 
     * DOC sizhaoliu Comment method "getDoc".
     * 
     * @param i
     * @return
     */
    public Document getDocument(int i) {
        Document doc = null;
        try {
            doc = this.searcher.doc(i);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public String getWordByDocNumber(int docNo) {
        return getDocument(docNo).getValues(F_WORD)[0];
    }

    public String[] getSynonymsByDocNumber(int docNo) {
        return getDocument(docNo).getValues(F_SYN);
    }

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

}
