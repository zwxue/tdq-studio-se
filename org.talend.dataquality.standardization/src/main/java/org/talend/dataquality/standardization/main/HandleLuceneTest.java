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

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;

/**
 * DOC klliu class global comment.
 */
public class HandleLuceneTest {

	public HandleLuceneTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filename = "./data/TalendGivenNames.TXT";
		String indexfolder = "./data/TalendGivenNames_index";
		String[] firstnames = new String[] { "jeants", "rémy", "jean-philippe",
				"sebastiao", "r*my*" };
		HandleLucene hl = new HandleLuceneImpl();
		System.out.print(hl.createIndex(filename, indexfolder));

		try {
			ArrayList<String[]> hits = hl.getSearchResult(firstnames);
			for (int h = 0; h < hits.size(); ++h) {
				String[] docs = hits.get(h);
				for (int i = 0; i < docs.length; i++) {
					System.out.println(docs[i]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
