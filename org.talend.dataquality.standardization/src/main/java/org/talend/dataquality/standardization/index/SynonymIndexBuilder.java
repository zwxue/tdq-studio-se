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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

/**
 * @author scorreia A class to create an index with synonyms.
 */
public class SynonymIndexBuilder {

    public static final String F_WORD = "word";

    public static final String F_SYN = "syn";

    private Directory indexDir;

    private IndexSearcher searcher;

    private char separator;

    private Analyzer analyzer;

    private IndexWriter writer;

    private boolean usingCreateMode = false;

    private int numTopDoc;

    private SynonymAnalyzer synonymAnalyzer;

    private String path;

    /**
     * instantiate an index builder
     * 
     * DOC sizhaoliu SynonymIndexBuilder constructor comment.
     */
    public SynonymIndexBuilder() {
        numTopDoc = 5;
    }

    public boolean isUsingCreateMode() {
        return usingCreateMode;
    }

    /**
     * if the builder is using create mode. it will clear the old index files first before any other operation. This is
     * sometimes dangerous and will be replaced by manual segment check.
     * 
     * DOC sizhaoliu Comment method "isUsingCreateMode".
     * 
     * @return
     */
    public void setUsingCreateMode(boolean usingCreateMode) {
        this.usingCreateMode = usingCreateMode;
    }

    public Directory getIndexDir() {
        return indexDir;
    }

    /**
     * set a separator for a string which contains synonyms
     * 
     * DOC sizhaoliu Comment method "setSynonymSeparator".
     * 
     * @param synonymSeparator
     */
    public void setSynonymSeparator(char synonymSeparator) {
        this.separator = synonymSeparator;
    }

    public void initIndexInRAM() throws IOException {
        indexDir = new RAMDirectory();
    }

    public void initIndexInFS(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("Path does not exist, please create " + file.getAbsolutePath() + " first!");
        }

        // System.out.println("index:" + path.getAbsolutePath());
        this.path = path;

        try {
            indexDir = FSDirectory.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            docs = getSearcher().search(query, numTopDoc);
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
            docs = getSearcher().search(query, numTopDoc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docs;
    }

    /**
     * insert an entire document into index.
     * 
     * @param word
     * @param synonyms
     * @param separator
     * @throws IOException
     */
    public void insertDocument(String word, String synonyms) throws IOException {
        if (usingCreateMode) {
            getWriter().addDocument(generateDocument(word, synonyms));
            System.out.println("The document <" + word + "> is now inserted.");
        } else {
            if (searchDocumentByWord(word).totalHits == 0) {
                getWriter().addDocument(generateDocument(word, synonyms));
                System.out.println("The document <" + word + "> is now inserted.");

            } else {
                System.err.println("The document <" + word + "> already exists and is ignored.");
            }
        }
    }

    /**
     * update an entire synonym document.
     * 
     * @param word
     * @param synonyms
     * @param separator
     * @throws IOException
     */
    public int updateDocument(String word, String synonyms) throws IOException {

        TopDocs docs = searchDocumentByWord(word);
        switch (docs.totalHits) {
        case 0:
            System.err.println("The document <" + word + "> doesn't exist.");
            return 0;
        case 1:
            break;
        default:
            break;
        }

        getWriter().updateDocument(new Term(F_WORD, word), generateDocument(word, synonyms));
        // lucene allow user to roll back the deletions
        // so we need to commit the change to bring our deletions into effect
        // as an Update action contains a Delete action, we commit here as well
        // however, we don't need to commit for Add actions
        getWriter().commit();
        System.out.println("The document <" + word + "> has been updated.");
        return 1;
    }

    /**
     * delete an entire document by one of the synonyms.
     * 
     * @param word
     * @throws IOException
     */
    public int deleteDocumentByWord(String word) throws IOException {
        TopDocs docs = searchDocumentByWord(word);
        switch (docs.totalHits) {
        case 0:
            System.err.println("The document containing <" + word + "> doesn't exist.");
            return 0;
        case 1:
            getWriter().deleteDocuments(new Term(F_WORD, word));
            getWriter().commit();
            System.out.println("The document <" + word + "> has been deleted.");
            return 1;
        default:
            break;
        }
        return 0;
    }

    /**
     * delete all synonym documents.
     * 
     * @throws IOException
     */
    public void deleteAllDocuments() throws IOException {
        getWriter().deleteAll();
        getWriter().commit();
    }

    /**
     * add a synonym to an existing document.
     * 
     * @param word
     * @param newSynonym
     * @throws IOException
     */
    public int addSynonymToDocument(String word, String newSynonym) throws IOException {

        Query query = new TermQuery(new Term(F_WORD, word));
        TopDocs docs = getSearcher().search(query, numTopDoc);
        if (docs.totalHits > 0) {
            Document doc = getSearcher().doc(docs.scoreDocs[0].doc);

            String[] synonyms = doc.getValues(F_SYN);
            for (String str : synonyms) {
                if (str.toLowerCase().equals(newSynonym.toLowerCase())) {
                    System.err.println("The synonym <" + newSynonym + "> is similar to <" + str + ">. Ignored.");
                    return 0;
                }
            }
            // create a new document and replace the original one
            doc.add(new Field(F_SYN, newSynonym, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES));
            getWriter().updateDocument(new Term(F_WORD, word), doc);
            getWriter().commit();

            System.out.println("The synonym <" + newSynonym + "> is added to word.");
            return 1;
        } else {
            System.err.println("The word <" + word + "> doesn't exist. Cannot add.");

            return 0;
        }
    }

    /**
     * remove a synonym from the document to which it belongs.
     * 
     * @param synonymToDelete
     * @throws IOException
     */
    public int removeSynonymFromDocument(String word, String synonymToDelete) throws IOException {
        int deleted = 0;
        Query query = new TermQuery(new Term(F_WORD, word));
        TopDocs docs = getSearcher().search(query, numTopDoc);
        if (docs.totalHits > 0) {
            Document doc = getSearcher().doc(docs.scoreDocs[0].doc);

            String[] synonyms = doc.getValues(F_SYN);
            List<String> synonymList = new ArrayList<String>();
            for (String str : synonyms) {
                // the synonym equal to the word will not be deleted
                if (str.equals(word)) {
                    continue;
                } else if (str.equals(synonymToDelete)) {
                    System.out.println("The synonym <" + synonymToDelete + "> has been deleted from the word.");
                    deleted++;
                } else {
                    synonymList.add(str);
                }
            }
            Document newDoc = generateDocument(word, synonymList);
            getWriter().updateDocument(new Term(F_WORD, word), newDoc);
            getWriter().commit();

            // printSynonymDocument(newDoc);
        }
        return deleted;
    }

    private Document generateDocument(String word, String synonyms) {
        String[] split = StringUtils.split(synonyms, separator);
        return generateDocument(word, Arrays.asList(split));
    }

    /**
     * generate a document
     * 
     * @param word
     * @param synonyms
     * @return
     */
    private Document generateDocument(String word, List<String> synonyms) {

        Document doc = new Document();
        Field field = new Field(F_WORD, word, Field.Store.YES, Field.Index.NOT_ANALYZED, TermVector.NO);
        doc.add(field);
        if (synonyms != null) {
            // --- store entry also in synonym list so that we can search for it too
            // without the need to search in the word field
            // TODO scorreia can we avoid this?
            doc.add(new Field(F_SYN, word, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES));
            for (String syn : synonyms) {
                doc.add(new Field(F_SYN, syn, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES));
            }
        }
        return doc;
    }

    public void closeIndex() throws IOException {
        this.getWriter().optimize();
        this.getWriter().close();
    }

    public void commit() throws IOException {
        this.getWriter().commit();
    }

    /**
     * Getter for analyzer.
     * 
     * @return the analyzer
     * @throws IOException
     */
    private Analyzer getAnalyzer() throws IOException {
        if (analyzer == null) {
            // the entry and the synonyms are indexed as provided
            // analyzer = new KeywordAnalyzer();

            // analyzer = new StandardAnalyzer(Version.LUCENE_30);

            analyzer = new SynonymAnalyzer();
        }
        return this.analyzer;
    }

    /**
     * Getter for writer.
     * 
     * @return the writer
     * @throws IOException
     * @throws
     */
    IndexWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new IndexWriter(indexDir, this.getAnalyzer(), usingCreateMode, IndexWriter.MaxFieldLength.UNLIMITED);
        }
        return this.writer;
    }

    /**
     * Getter for searcher a new searcher is instantiated every time.
     * 
     * DOC sizhaoliu Comment method "getSearcher".
     * 
     * @return
     * @throws IOException
     */
    IndexSearcher getSearcher() throws IOException {
        searcher = new IndexSearcher(indexDir);
        return searcher;
    }

    private void printSynonymDocument(Document doc) {
        String[] word = doc.getValues("word");
        for (String string : word) {
            System.out.println("word=" + string);
        }
        String[] values = doc.getValues("syn");
        for (String string : values) {
            System.out.println("syn=" + string);
        }
        System.out.println();

    }

    /**
     * get an indicated document from search result
     * 
     * DOC sizhaoliu Comment method "getDoc".
     * 
     * @param docs
     * @param i
     * @return
     */
    public Document getDoc(TopDocs docs, int i) {
        Document doc = null;
        try {
            doc = getSearcher().doc(docs.scoreDocs[i].doc);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

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
            docs = getSearcher().search(query, numTopDoc);
            if (docs.totalHits > 0) {
                Document doc = getSearcher().doc(docs.scoreDocs[0].doc);
                String[] synonyms = doc.getValues("syn");
                return synonyms.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
