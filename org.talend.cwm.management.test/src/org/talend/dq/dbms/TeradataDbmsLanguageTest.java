package org.talend.dq.dbms;

import junit.framework.Assert;

import org.junit.Test;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;

public class TeradataDbmsLanguageTest {

	@Test
	public void testExtractQuarter() {
        String expectedResult = "(((CAST( EXTRACT(month FROM name) AS BYTEINT)-1)/3)+1)"; //$NON-NLS-1$
        TeradataDbmsLanguage teradataDbmsLanguage = (TeradataDbmsLanguage) DbmsLanguageFactory
                .createDbmsLanguage(SupportDBUrlType.TERADATADEFAULTURL);
        String query = teradataDbmsLanguage.extractQuarter("name"); //$NON-NLS-1$
        Assert.assertEquals(expectedResult, query);

	}

	@Test
	public void testExtractWeek() {
        String expectedResult = "((name- (( EXTRACT(year FROM name) - 1900) * 10000 + 0101 (DATE))) - ((name- DATE '0001-01-07') MOD 7)  + 13) / 7"; //$NON-NLS-1$
        TeradataDbmsLanguage teradataDbmsLanguage = (TeradataDbmsLanguage) DbmsLanguageFactory
                .createDbmsLanguage(SupportDBUrlType.TERADATADEFAULTURL);
        String query = teradataDbmsLanguage.extractWeek("name"); //$NON-NLS-1$
        Assert.assertEquals(expectedResult, query);
	}

}
