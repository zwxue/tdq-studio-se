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
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;

/**
 * DOC klliu class global comment.
 */
public interface HandleLucene {
	/**
	 * Expect that by accepting parameter and returns the correct result, if not
	 * correspond with the result of searchwords, do a fuzzy query, returns the
	 * result of the similar.
	 * 
	 * @param searchType
	 * @param searchWords
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public Map<String, String[]> getSearchResult(String searchType,
			String[] searchWords) throws IOException, ParseException;

	/**
	 * Input filename to be indexed once for all and indexfolder to store the
	 * files of indexing.
	 * 
	 * @param filename
	 * @param indexfolder
	 * @return
	 */
	public boolean createIndex(String filename, String indexfolder);

}