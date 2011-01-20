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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @author scorreia A class to create an index with synonyms.
 */
public class SynonymIndexBuilder {

    public static final String F_WORD = "word";

    public static final String F_SYN = "syn";

    private Directory index;

    private Analyzer analyzer;

    private IndexWriter writer;

    public SynonymIndexBuilder(String filePath) throws IOException {
        this.initIndex(new File(filePath));
    }

    private void initIndex(File path) throws IOException {
        // choose the best index implementation
        index = FSDirectory.open(path);
    }


    public void index(Map<String, List<String>> entry2synonyms) throws IOException {
        for (String entry : entry2synonyms.keySet()) {
            index(entry, entry2synonyms.get(entry));
        }
        closeIndex();
    }

    public void index(String entry, String synonyms, char separator) throws IOException {
        // TODO: do we need to lowercase?
        String[] split = StringUtils.split(synonyms, separator);
        addDocToIndex(entry, Arrays.asList(split));
    }

    
    public void index(String entry, List<String> synonyms) throws IOException {
        addDocToIndex(entry, synonyms);
    }

    private void addDocToIndex(String entry, List<String> synonyms) throws IOException {
        Document doc = new Document();
        Field field = new Field(F_WORD, entry, Field.Store.YES, Field.Index.NO, TermVector.NO);
        doc.add(field);
        if (synonyms != null) {
            // --- store entry also in synonym list so that we can search for it too
            // without the need to search in the word field
            // TODO scorreia can we avoid this?
            doc.add(new Field(F_SYN, entry, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES));
            for (String syn : synonyms) {
                doc.add(new Field(F_SYN, syn, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES));
            }
        }
        this.getWriter().addDocument(doc);
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
    private IndexWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new IndexWriter(index, this.getAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
        }
        return this.writer;
    }

}
