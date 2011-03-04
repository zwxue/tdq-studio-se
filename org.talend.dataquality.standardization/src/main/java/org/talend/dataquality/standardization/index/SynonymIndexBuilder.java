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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @author scorreia A class to create an index with synonyms.
 */
public class SynonymIndexBuilder {

    public static final String F_WORD = "word";

    public static final String F_SYN = "syn";

    private Directory indexDir;

    /**
     * Default synonym separator is '|'.
     */
    private char separator = '|';

    private Analyzer analyzer;

    private IndexWriter writer;

    private Error error = new Error();


    /**
     * instantiate an index builder.
     */
    public SynonymIndexBuilder() {
    }


    /**
     * Method "getError".
     * 
     * @return the last error
     */
    public Error getError(){
    	return this.error;
    }

    /**
     * set a separator for a string which contains synonyms
     * 
     * @param synonymSeparator
     */
    public void setSynonymSeparator(char synonymSeparator) {
        this.separator = synonymSeparator;
    }

    // FIXME not used yet. Need to be implemented
    // public void initIndexInRAM() {
    // indexDir = new RAMDirectory();
    // }

    /**
     * Method "initIndexInFS" initializes the lucene index folder.
     * 
     * @param path the path of the index (will be created if it does not exist)
     */
    public void initIndexInFS(String path) {

        try {
            File file = new File(path);

            if (!file.exists()) {
                file.mkdirs();
            }
            indexDir = FSDirectory.open(file);
        } catch (IOException e) {
            error.set(false, e.getMessage());
            e.printStackTrace();
        }
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
            // insert document without duplication verification
            getWriter().addDocument(generateDocument(word, synonyms));
    }

    /**
     * insert an entire document into index if it does not already exists.
     * 
     * @param word the reference string
     * @param synonyms the synonyms
     * @return true if inserted, false otherwise
     * @throws IOException
     */
    public boolean insertDocumentIfNotExists(String word, String synonyms) throws IOException {
        if (searchDocumentByWord(word).totalHits == 0) {
            getWriter().addDocument(generateDocument(word, synonyms));
            return true;
        } // else
        return false;
    }

    /**
     * Update an entire synonym document if and only if it exists and it's unique.
     * 
     * WARNING If some changes in the index are not committed, this may cause trouble to find the document to update.
     * Make sure that a commit has been done before calling this method except if you know exactly what you are doing.
     * 
     * WARNING Beware that if several documents match the word, nothing will be done.
     * 
     * @param word
     * @param synonyms
     * @param separator
     * @throws IOException
     */
    public int updateDocument(String word, String synonyms) throws IOException {
        int nbUpdatedDocuments = 0;
        TopDocs docs = searchDocumentByWord(word);
        switch (docs.totalHits) {
        case 0:
            // FIXME should be create an insertOrUpdate method?
        	error.set(false, "<" + word + "> doesn't exist.");
            break;
        case 1:
            getWriter().updateDocument(new Term(F_WORD, word), generateDocument(word, synonyms));
            nbUpdatedDocuments = 1;
            break;
        default:
            // FIXME maybe we need to avoid deleting several documents when we just want to update one document (to be
            // tested)
            error.set(false, "" + docs.totalHits + " matched the given reference word: " + word + ". No change is done.");
            break;
        }
        return nbUpdatedDocuments;

    }

    /**
     * delete an entire document by word.
     * 
     * @param word
     * @throws IOException
     */
    public int deleteDocumentByWord(String word) throws IOException {
        TopDocs docs = searchDocumentByWord(word);
        switch (docs.totalHits) {
        case 0:
            error.set(false, "<" + word + "> doesn't exist.");
            return 0;
        case 1:
            getWriter().deleteDocuments(new Term(F_WORD, word));
            // TODO do we really need to commit here?
            getWriter().commit();
            //System.out.println("The document named <" + word + "> has been deleted.");
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
        // getWriter().commit();
    }

    /**
     * Add a synonym to an existing document. If several documents are found given the input word, nothing is done.
     * 
     * @param word a word
     * @param newSynonym the new synonym to add to the list of synonyms
     * @return 1 if added or 0 if no change has been done
     * @throws IOException
     */
    public int addSynonymToDocument(String word, String newSynonym) throws IOException {
        if (newSynonym == null || newSynonym.length() == 0) {
            return 0;
        }

        // reuse related synonym index search instead of created a new search
        SynonymIndexSearcher idxSearcher = getNewSynIdxSearcher();
        TopDocs docs = idxSearcher.searchDocumentByWord(word);

        if (docs.totalHits == 1) { // don't do anything if several documents match
            Document doc = idxSearcher.getDocument(docs.scoreDocs[0].doc);

            String[] synonyms = doc.getValues(F_SYN);
            for (String str : synonyms) {
                if (str.toLowerCase().equals(newSynonym.toLowerCase())) {
                    error.set(false, "The synonym <" + newSynonym + "> is similar to <" + str + ">. Ignored.");
                    return 0;
                }
            }
            // create a new document and replace the original one
            doc.add(createSynField(newSynonym));
            getWriter().updateDocument(new Term(F_WORD, word), doc);
            //System.out.println("The synonym <" + newSynonym + "> is added to word.");
            return 1;
        } else {
            if (docs.totalHits == 0) {
                error.set(false, "The word <" + word + "> doesn't exist. Cannot add.");
            } else {
                error.set(false, "Several documents have been found given the word <" + word + ">. Cannot add.");
            }
            return 0;
        }
    }

    /**
     * remove a synonym from the document to which it belongs.
     * 
     * @param synonymToDelete
     * @return the number of deleted synonyms
     * @throws IOException
     */
    public int removeSynonymFromDocument(String word, String synonymToDelete) throws IOException {
        if (synonymToDelete.toLowerCase().equals(word.toLowerCase())) {
            error.set(false, "The synonym <" + synonymToDelete + "> is similar to the word and will not be removed");
            return 0;
        }
        int deleted = 0;

        SynonymIndexSearcher newSynIdxSearcher = getNewSynIdxSearcher();
        TopDocs docs = newSynIdxSearcher.searchDocumentByWord(word);
        if (docs.totalHits == 1) { // don't do anything if more than one document is found
            Document doc = newSynIdxSearcher.getDocument(docs.scoreDocs[0].doc);
            String[] synonyms = doc.getValues(F_SYN);
            List<String> synonymList = new ArrayList<String>();

            for (String str : synonyms) {
                if (str.equals(word)) {
                    // do nothing. because the word will be added to the document
                    // automatically in the method generateDocument().
                } else if (str.toLowerCase().equals(synonymToDelete.toLowerCase())) {
                    // we don't require the synonymToDelete to be case sensitive.
                    // System.out.println("The synonym <" + synonymToDelete + "> is removed from the word.");
                    deleted++;
                } else {
                    synonymList.add(str);
                }
            }

            // if the value of deleted is 0, we can know that the synonymToDelete doesn't exist
            if (deleted == 0) {
            	error.set(false, "The synonym <" + synonymToDelete + "> doesn't exist in the document. Ignored.");
            } else {
                Document newDoc = generateDocument(word, synonymList);
                getWriter().updateDocument(new Term(F_WORD, word), newDoc);
            }

        } else {
        	error.set(false, "The word <" + word + "> doesn't exist. Cannot remove.");
            return 0;
        }
        return deleted;
    }

    public boolean deleteIndexFromFS(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            System.err.println("file not found");
            return false;
        }
        if (folder.isDirectory()) {
            File[] filelist = folder.listFiles();
            for (File f : filelist) {
                deleteIndexFromFS(f.getAbsolutePath());
            }
        } // else folder is a file
        return folder.delete();
    }

    // FIXME remove this method?
    // private void printSynonymDocument(Document doc) {
    // String[] word = doc.getValues("word");
    // for (String string : word) {
    // System.out.println("word=" + string);
    // }
    // String[] values = doc.getValues("syn");
    // for (String string : values) {
    // System.out.println("syn=" + string);
    // }
    // System.out.println();
    //
    // }

    /**
     * ADDED BY ytao 2011/02/11 If only need to initialize the index, do nothing after fold open, but just invoke this
     * method at the end, index will be reset.
     * 
     * (Ensure that usingCreateMode is true) // TODO where is it ensured? who wrote this sentence?
     * 
     * Not sure that the index is deleted and recreated, may be just delete all documents of index since the index files
     * are "_1a.cfs" and "segments.gen" and "segments_1e" currently, if these files are not exists, API will not work.
     * something need to TODO
     * 
     * TODO closing an index should not delete the documents!
     */
    public void closeIndex() {
        try {
            this.getWriter().optimize();
            this.getWriter().close();
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Commits all pending changes.
     */
    public void commit() {
        try {
            this.getWriter().commit();
        } catch (CorruptIndexException e) {
        	error.set(false, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
        	error.set(false, e.getMessage());
        	e.printStackTrace();
        }
    }

    /**
     * Getter for analyzer.
     * 
     * @return the analyzer
     * @throws IOException
     */
    public Analyzer getAnalyzer() throws IOException {
        if (analyzer == null) {
            // the entry and the synonyms are indexed as provided
            // analyzer = new KeywordAnalyzer();

            // most used analyzer in lucene
            analyzer = new StandardAnalyzer(Version.LUCENE_30);

            // analyzer = new SynonymAnalyzer();
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
            writer = new IndexWriter(indexDir, this.getAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        }
        return this.writer;
    }

    /**
     * Method "getNumDocs"
     * 
     * @return the number of documents or -1 if an error happened
     */
    public int getNumDocs() {
        try {
            return this.getWriter().numDocs();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get a new read-only searcher at each call.
     * 
     * @return
     * @throws IOException
     */
    private IndexSearcher getNewIndexSearcher() throws IOException {
        // FIXME optimization could be done if we use the IndexReader.reopen() method instead of creating a new object
        // at each call.
        return new IndexSearcher(indexDir);
    }

    private SynonymIndexSearcher getNewSynIdxSearcher() throws IOException {
        return new SynonymIndexSearcher(getNewIndexSearcher());
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
        field.setBoost(1.5F); // increase the importance of the reference word
        doc.add(field);
        if (synonyms != null) {
            // --- store entry also in synonym list so that we can search for it too
            // without the need to search in the word field (will be tokenized given the analyzer)
            doc.add(createSynField(word));
            for (String syn : synonyms) {
                doc.add(createSynField(syn));
            }
        }
        return doc;
    }


    private Field createSynField(String synonym) {
        return new Field(F_SYN, synonym, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES);
    }

    /**
     * search a document by the word. use only inside the builder.
     * 
     * @param word
     * @return
     * @throws IOException
     */
    private TopDocs searchDocumentByWord(String word) {
        TopDocs docs = null;
        try {
            SynonymIndexSearcher newSynIdxSearcher = getNewSynIdxSearcher();
            docs = newSynIdxSearcher.searchDocumentByWord(word);
        } catch (IOException e) {
            error.set(false, e.getMessage());
            e.printStackTrace();
        }
        return docs;
    }

}
