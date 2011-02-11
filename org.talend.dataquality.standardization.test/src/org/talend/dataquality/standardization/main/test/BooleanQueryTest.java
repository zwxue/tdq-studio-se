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

package org.talend.dataquality.standardization.main.test;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class BooleanQueryTest extends TestCase {

    public void testAnd() throws Exception {
        String indexfolder = "./data/TalendGivenNames_index";
        Directory dir = FSDirectory.open(new File(indexfolder));
        Term searchingBooks = new Term("name", "Christian"); // #1
        Term term = new Term("country", "french");
        Term gender = new Term("gender", "");
        BooleanQuery searchingBooks2004 = new BooleanQuery(); // #3
        searchingBooks2004.add(new FuzzyQuery(searchingBooks), BooleanClause.Occur.MUST); // #3
        searchingBooks2004.add(new FuzzyQuery(term), BooleanClause.Occur.MUST); // #3
        searchingBooks2004.add(new FuzzyQuery(gender), BooleanClause.Occur.MUST); // #3
        IndexSearcher searcher = new IndexSearcher(dir);
        TopDocs matches = searcher.search(searchingBooks2004, 10);

        for (int i = 0; i < matches.totalHits; i++) {
            Document doc = searcher.doc(matches.scoreDocs[i].doc);

            System.out.println("title '" + doc.get("gender") + "' found");

        }
        searcher.close();
    }

}
