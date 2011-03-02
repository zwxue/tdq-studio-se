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
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
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
import org.apache.lucene.util.Version;

/**
 * @author scorreia A class to create an index with synonyms.
 */
public class SynonymIndexBuilder {

    public static final String F_WORD = "word";

    public static final String F_SYN = "syn";

    private Directory indexDir;

    private IndexSearcher searcher;

    /**
     * Default synonym separator is '|'.
     */
    private char separator = '|';

    private Analyzer analyzer;

    private IndexWriter writer;

    private boolean usingCreateMode = false;

    private boolean verifyDuplication = true;

    private Error error = new Error();

    private int topDocLimit;



    /**
     * instantiate an index builder
     * 
     * DOC sizhaoliu SynonymIndexBuilder constructor comment.
     */
    public SynonymIndexBuilder() {
        topDocLimit = 5;
    }

    public void setVerifyDuplication(boolean verifyDuplication) {
        this.verifyDuplication = verifyDuplication;
    }

    /**
     * if the builder is using create mode. it will clear the old index files first before any other operation. This is
     * sometimes dangerous and will be replaced by manual segment check.
     * 
     * @return
     */
    public void setUsingCreateMode(boolean usingCreateMode) {
        this.usingCreateMode = usingCreateMode; // FIXME why do we need this?
    }

    public Error getError(){
    	return this.error;
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

    public void initIndexInRAM() {
        indexDir = new RAMDirectory();
    }

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
            CheckIndex check = new CheckIndex(indexDir);
            Status status = check.checkIndex();
            if (status.missingSegments) {
                
                if (usingCreateMode) {
                    // initialize the segment of index with a simple commit.
                    error.set(true, "Initialize an index...");
                    commit();
                } else {
                    // propose to use create mode.
                    error.set(false, "Segments file not found, initialize an index first");
                    System.out.println(error.getMessage());
                }
            }
        } catch (IOException e) {
            error.set(false, e.getMessage());
            e.printStackTrace();
        }

        // After using create mode for segment creation, we should reset this value to false.
        // If we don't do this, when the Writer is closed and needs to be re-opened,
        // the builder will re-initialize the Writer in create mode, which will erase everything.
        usingCreateMode = false;

    }

    /**
     * search a document by the word. use only inside the builder.
     * 
     * @param word
     * @return
     * @throws IOException
     */
    private TopDocs searchDocumentByWord(String word) {
    	if (word == null) {
    		return null;
    	}
        Query query = new TermQuery(new Term(F_WORD, word));
        TopDocs docs = null;
        try {
            docs = getSearcher().search(query, topDocLimit);
        } catch (IOException e) {
        	error.set(false, e.getMessage());
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
        if (verifyDuplication) {
            if (searchDocumentByWord(word).totalHits == 0) {
                getWriter().addDocument(generateDocument(word, synonyms));
                getWriter().commit(); // FIXME could we do a bulk commit instead of committing each document?
                // System.out.println("The document <" + word + "> is now inserted.");
            } else {
                error.set(false, "<" + word + "> already exists and is ignored.");
            }

        } else {
            // insert document without duplication verification
            getWriter().addDocument(generateDocument(word, synonyms));
            getWriter().commit();

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
        	error.set(false, "<" + word + "> doesn't exist.");
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
        // System.out.println("The document named <" + word + "> has been updated.");
        return 1;
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
        TopDocs docs = getSearcher().search(query, topDocLimit);
        if (docs.totalHits > 0) {
            Document doc = getSearcher().doc(docs.scoreDocs[0].doc);

            String[] synonyms = doc.getValues(F_SYN);
            for (String str : synonyms) {
                if (str.toLowerCase().equals(newSynonym.toLowerCase())) {
                    error.set(false, "The synonym <" + newSynonym + "> is similar to <" + str + ">. Ignored.");
                    return 0;
                }
            }
            // create a new document and replace the original one
            doc.add(new Field(F_SYN, newSynonym, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES));
            getWriter().updateDocument(new Term(F_WORD, word), doc);
            getWriter().commit();
            //System.out.println("The synonym <" + newSynonym + "> is added to word.");
            return 1;
        } else {
        	error.set(false, "The word <" + word + "> doesn't exist. Cannot add.");
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
        if (synonymToDelete.toLowerCase().equals(word.toLowerCase())) {
            error.set(false, "The synonym <" + synonymToDelete + "> is similar to the word and will not be removed");
            return 0;
        }
        int deleted = 0;
        Query query = new TermQuery(new Term(F_WORD, word));
        TopDocs docs = getSearcher().search(query, topDocLimit);
        if (docs.totalHits > 0) {
            Document doc = getSearcher().doc(docs.scoreDocs[0].doc);
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
                getWriter().commit();
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

    /**
     * ADDED BY ytao 2011/02/11 If only need to initialize the index, do nothing after fold open, but just invoke this
     * method at the end, index will be reset.
     * 
     * (Ensure that usingCreateMode is true)
     * 
     * Not sure that the index is deleted and recreated, may be just delete all documents of index since the index files
     * are "_1a.cfs" and "segments.gen" and "segments_1e" currently, if these files are not exists, API will not work.
     * something need to TODO
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
    private Analyzer getAnalyzer() throws IOException {
        if (analyzer == null) {
            // the entry and the synonyms are indexed as provided
            // analyzer = new KeywordAnalyzer();

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
}
