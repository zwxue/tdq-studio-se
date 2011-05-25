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
package test.udi;

import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TopDocs;

/**
 * @author sizhaoliu
 * 
 * Java implementation of a user defined indicator who counts number of each distinct record. The records are sorted by
 * unmatched times in descend order.
 */

public class LuceneUnmatchFrequency extends AbstracLuceneIndexFrequency {

    @Override
    public boolean handle(Object data) {
        super.handle(data); // check null, increment count

        try {
            TopDocs docs;
            docs = searcher.searchDocumentBySynonym(data.toString());
            if (docs.totalHits <= 0) {
                // increment frequency of data
                Long c = this.valueToFreq.get(data);
                if (c == null) {
                    // add value to map
                    this.valueToFreq.put(data, 1L);
                } else {
                    // already exists: increment number of occurences
                    c++;
                    this.valueToFreq.put(data, c);
                }
            }
        } catch (ParseException e) {
            System.err.println("Parse failed for <" + data + ">");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
