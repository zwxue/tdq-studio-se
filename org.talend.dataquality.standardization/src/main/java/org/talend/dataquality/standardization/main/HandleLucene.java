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

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;


/**
 * DOC klliu class global comment.
 */
public interface HandleLucene {
	public ArrayList<String[] > getSearchResult(String[] searchWords ) throws IOException, ParseException;
	public boolean createIndex(String filename,String indexfolder);
	//public IndexSearcher getIndexSearcher()throws CorruptIndexException, IOException;
}