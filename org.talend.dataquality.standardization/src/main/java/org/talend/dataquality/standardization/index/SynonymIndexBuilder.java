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
package org.talend.dataquality.standardization.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

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

    public Directory getIndexDir() {
        return indexDir;
    }

    public void setSynonymSeparator(char synonymSeparator) {
        this.separator = synonymSeparator;
    }

    public SynonymIndexBuilder() {
    }

    public void initIndexInRAM() throws IOException {
        indexDir = new RAMDirectory();
    }

    public void initIndexInfile(File path) throws IOException {
        // choose the best index implementation
        indexDir = FSDirectory.open(path);
    }

    /**
     * search a document by the word.
     * 
     * @param word
     * @return
     * @throws IOException
     */
    public TopDocs searchDocumentByWord(String word) throws IOException {
        Query query = new TermQuery(new Term(F_WORD, word));
        TopDocs docs = getSearcher().search(query, 5);
        return docs;
    }

    public TopDocs searchDocumentBySynonym(String synonym) throws IOException {
        Query query = new TermQuery(new Term(F_SYN, synonym.toLowerCase()));
        TopDocs docs = getSearcher().search(query, 5);
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
    public void insertSynonymDocument(String word, String synonyms) throws IOException {
        int hits = searchDocumentByWord(word).totalHits;
        if (hits == 0) {
            getWriter().addDocument(generateDocument(word, synonyms));
            System.out.println("The document <" + word + "> has been inserted.");
        } else {
            System.out.println("The document <" + word + "> already exists and is ignored.");
        }
    }

    /**
     * add a synonym to an existing document.
     * 
     * @param word
     * @param newSynonym
     * @throws IOException
     */
    public int addSynonymToWord(String word, String newSynonym) throws IOException {

        Query query = new TermQuery(new Term(F_WORD, word));
        TopDocs docs = getSearcher().search(query, 10);
        if (docs.totalHits > 0) {
            Document doc = getSearcher().doc(docs.scoreDocs[0].doc);

            String[] synonyms = doc.getValues(F_SYN);
            for (String str : synonyms) {
                if (str.equals(newSynonym)) {
                    System.out.println("The synonym <" + newSynonym + "> already exists. Ignored.");
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
            System.out.println("The word <" + word + "> doesn't exist. Cannot add.");

            return 0;
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
    public int updateSynonymDocument(String word, String synonyms) throws IOException {

        TopDocs docs = searchDocumentByWord(word);
        switch (docs.totalHits) {
        case 0:
            System.out.println("The document <" + word + "> doesn't exist.");
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
     * @param synonym
     * @throws IOException
     */
    public int deleteSynonymDocument(String synonym) throws IOException {
        TopDocs docs = searchDocumentBySynonym(synonym);
        switch (docs.totalHits) {
        case 0:
            System.out.println("The document containing <" + synonym + "> doesn't exist.");
            return 0;
        case 1:
            getWriter().deleteDocuments(new Term(F_SYN, synonym));
            getWriter().commit();
            System.out.println("The document containing <" + synonym + "> has been deleted.");
            return 1;
        default:
            break;
        }
        return 0;
    }

    /**
     * delete a synonym from the document to which it belongs.
     * 
     * @param synonymToDelete
     * @throws IOException
     */
    public int deleteSynonymFromWord(String word, String synonymToDelete) throws IOException {
        Query query = new TermQuery(new Term(F_WORD, word));
        TopDocs docs = getSearcher().search(query, 10);
        if (docs.totalHits > 0) {
            Document doc = getSearcher().doc(docs.scoreDocs[0].doc);

            String[] words = doc.getValues(F_WORD);
            String[] synonyms = doc.getValues(F_SYN);
            List<String> synonymList = new ArrayList<String>();
            for (String str : synonyms) {
                // the synonym equal to the word will not be deleted
                if (str.equals(words[0]))
                    continue;
                if (!str.equals(synonymToDelete)) {
                    synonymList.add(str);
                } else {
                    System.out.println("The synonym <" + str + "> has been deleted from the word.");

                    return 1;
                }
            }
            Document newDoc = generateDocument(words[0], synonymList);
            getWriter().updateDocument(new Term(F_SYN, synonymToDelete.toLowerCase()), newDoc);
            getWriter().commit();
            // printSynonymDocument(newDoc);
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

    /**
     * Getter for analyzer.
     * 
     * @return the analyzer
     */
    private Analyzer getAnalyzer() {
        if (analyzer == null) {
            // the entry and the synonyms are indexed as provided
            // analyzer = new KeywordAnalyzer();
            analyzer = new StandardAnalyzer(Version.LUCENE_30);
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
            writer = new IndexWriter(indexDir, this.getAnalyzer(), false, IndexWriter.MaxFieldLength.UNLIMITED);
        }
        return this.writer;
    }

    IndexSearcher getSearcher() throws IOException {
        if (searcher == null) {
            searcher = new IndexSearcher(indexDir);
        }
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

}
