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
package org.talend.dataquality.standardization.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.standardization.index.IndexBuilder;
import org.talend.dataquality.standardization.query.FirstNameStandardize;

/**
 * DOC klliu class global comment.
 */
public class HandleLuceneImpl implements HandleLucene {
	private final int hitsPerPage = 10;
	private String indexfolder;
	private ArrayList<String[]> hits = new ArrayList<String[]>();

	/**
	 * Input filename to be indexed once for all and indexfolder to store the
	 * files of indexing.
	 * 
	 * @param filename
	 * @param indexfolder
	 * @return
	 */
	public boolean createIndex(String filename, String indexfolder) {
		setIndexfolder(indexfolder);
		IndexBuilder idxBuilder = getIndexBuilder();
		int[] columnsToBeIndexed = new int[] { 0, 1, 2, 3 };
		try {
			idxBuilder.initializeIndex(filename, columnsToBeIndexed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Expect that by accepting parameter and returns the correct result, if not
	 * correspond with the result of searchwords, do a fuzzy query, returns the
	 * result of the similar.
	 * 
	 * @param searchWords
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public ArrayList<String[]> getSearchResult(String[] searchWords)
			throws IOException, ParseException {
		// TODO Auto-generated method stub

		IndexSearcher firtNameIs = getIndexSearcher();

		Analyzer searchAnalyzer = getAnalyzer();

		FirstNameStandardize stdname = new FirstNameStandardize(firtNameIs,
				searchAnalyzer, hitsPerPage);

		for (int searchCount = 0; searchCount < searchWords.length; searchCount++) {
			ScoreDoc[] docs = stdname.standardize(searchWords[searchCount]);
			treatSearchResult(docs);
		}
		firtNameIs.close();

		return getHits();
	}

	private void treatSearchResult(ScoreDoc[] docs) {

		for (int i = 0; i < docs.length; ++i) {
			int docId = docs[i].doc;
			Document d = null;
			try {
				d = getIndexSearcher().doc(docId);
				float sd = docs[i].score;
				String name = d.get("name");
				String alias = d.get("alias");
				String count = d.get("count");
				String[] doc = new String[] { name, alias,
						Float.toHexString(sd), count };
				hits.add(doc);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private ArrayList<String[]> getHits() {
		return hits;
	}

	private void setHits(ArrayList<String[]> hits) {
		this.hits = hits;
	}

	private String getIndexfolder() {
		return indexfolder;
	}

	private void setIndexfolder(String indexfolder) {
		this.indexfolder = indexfolder;
	}

	private Analyzer getAnalyzer() {
		return new SimpleAnalyzer();
	}

	private IndexBuilder getIndexBuilder() {
		String tt = getIndexfolder();
		return new IndexBuilder(tt);
	}

	private IndexSearcher getIndexSearcher() throws CorruptIndexException,
			IOException {
		Directory dir = null;
		try {
			dir = FSDirectory.open(new File(getIndexfolder()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new IndexSearcher(dir);
	}

}
