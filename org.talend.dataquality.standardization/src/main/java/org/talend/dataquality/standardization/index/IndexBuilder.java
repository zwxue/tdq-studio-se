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
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import com.csvreader.CsvReader;

/**
 * DOC scorreia  class global comment. Detailled comment
 */
public class IndexBuilder {

    String directoryPath;

    private Directory index;

    /**
     * Getter for index.
     * 
     * @return the index
     */
    public Directory getIndex() {
        return this.index;
    }

    public IndexBuilder(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public boolean initializeIndex(String csvFileToIndex, int[] columnsToBeIndexed) throws IOException {
        assert csvFileToIndex != null;
          if (!new File(csvFileToIndex).exists() || !new File(directoryPath).isDirectory()) {  
               throw new IOException(csvFileToIndex + " does not exist or"+directoryPath+" is not a directory");
           }  
        index = new MMapDirectory(new File(directoryPath));
        // TODO not sure about the best directory to choose, please investigate
       // index = new MMapDirectory(new File("./data/TalendGivenNames_index"));

        // 0. Specify the analyzer for tokenizing text.
        // The same analyzer should be used for indexing and searching
        // Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
        Analyzer analyzer = new SimpleAnalyzer();

        // the boolean arg in the IndexWriter ctor means to
        // create a new index, overwriting any existing index
        IndexWriter w = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
        
        // read the data (this will be the input data of a component called tFirstNameStandardize)
        CsvReader csvReader = new CsvReader(new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(
                csvFileToIndex.toString()), "windows-1252")), ',');
        csvReader.setTextQualifier('"');
        csvReader.setUseTextQualifier(true);

       csvReader.readHeaders();
        while (csvReader.readRecord()) {
            // String id = csvReader.get(0);
            String name = csvReader.get(columnsToBeIndexed[0]);
            String correctedName = csvReader.get(columnsToBeIndexed[1]);
            if (correctedName != null && correctedName.length() > 0) {
                // replace name by corrected name
                // TODO not sure that we don't need to index the name too. Probably not because of some wrong data in
                // the file to be indexed.
                name = correctedName;
            }
            String count = csvReader.get(columnsToBeIndexed[2]);
            String relatedNames = csvReader.get(columnsToBeIndexed[3]);
            addDoc(w, name, relatedNames, count,csvFileToIndex);
        }
        csvReader.close();
        w.optimize();
        w.close();

        return true;
    }

    private static void addDoc(IndexWriter w, String name, String synonyms, String count,String filePath) throws IOException {
        Document doc = new Document();
        Field field = new Field("name", name, Field.Store.YES, Field.Index.ANALYZED, TermVector.NO);
        doc.add(field);
        if (synonyms != null && synonyms.length() > 0) {
            doc.add(new Field("alias", synonyms, Field.Store.YES, Field.Index.ANALYZED));
        }
        doc.add(new Field("count", count, Field.Store.YES, Field.Index.NO));
        doc.add(new Field("filePath", filePath, Field.Store.YES, Field.Index.NO));
        //w.addDocument(doc);
        w.updateDocument(new Term("filePath", filePath), doc);

    }

	private boolean updateIndex(String indexDir, String fileName,IndexWriter indexWriter) throws IOException {
		File file = new File(fileName);
		Document doc = new Document();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equals("time.txt")) {
					doc.add(new Field("contents", new FileReader(files[i])));
					doc.add(new Field("filename", files[i].getName(),
							Field.Store.YES, Field.Index.NOT_ANALYZED));
				}
			}
		}
		indexWriter.updateDocument(new Term("filename", "time.txt"), doc);
		return false;

	}

}
