// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.dbms;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.indicators.DateGrain;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class MSSqlDbmsLanguage extends DbmsLanguage {

    /**
     * DOC scorreia MSSqlDbmsLanguage constructor comment.
     */
    public MSSqlDbmsLanguage() {
        super(DbmsLanguage.MSSQL);
    }

    /**
     * DOC scorreia MSSqlDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param majorVersion
     * @param minorVersion
     */
    public MSSqlDbmsLanguage(String dbmsType, int majorVersion, int minorVersion) {
        super(dbmsType, majorVersion, minorVersion);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#initDbmsFunctions(java.lang.String)
     */
    @Override
    protected Map<String, Integer> initDbmsFunctions(String dbms) {
        Map<String, Integer> functions = super.initDbmsFunctions(dbms);
        functions.put("LTRIM", 1);
        functions.put("RTRIM", 1);
        // Note: datalength gives the length of any object, but in the case of unicode, this length should be
        // divided by 2 in order to get the actual number of characters.
        // Do not use "LEN" since it right-trims the strings.
        functions.put("DATALENGTH", 1);
        functions.put("REPLACE", 3);
        return functions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#toQualifiedName(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String toQualifiedName(String catalog, String schema, String table) {
        schema = quote("dbo");
        // Bug fixed: 5118. ZQL parser does not understand statement like
        // select count(*) from Talend.dbo.departement
        // hence remove catalog and try statement like
        // select count(*) from dbo.departement
        // catalog = "dbo";

        return super.toQualifiedName(catalog, schema, table);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#getPatternFinderDefaultFunction(java.lang.String)
     */
    @Override
    public String getPatternFinderDefaultFunction(String expression) {
        return StringUtils.repeat("REPLACE(", 59) + expression
                + ",'B','A'),'C','A'),'D','A'),'E','A'),'F','A'),'G','A'),'H','A')"
                + ",'I','A'),'J','A'),'K','A'),'L','A'),'M','A'),'N','A'),'O','A')"
                + ",'P','A'),'Q','A'),'R','A'),'S','A'),'T','A'),'U','A'),'V','A')"
                + ",'W','A'),'X','A'),'Y','A'),'Z','A'),'b','a'),'c','a'),'d','a')"
                + ",'e','a'),'f','a'),'g','a'),'h','a'),'i','a'),'j','a'),'k','a')"
                + ",'l','a'),'m','a'),'n','a'),'o','a'),'p','a'),'q','a'),'r','a')"
                + ",'s','a'),'t','a'),'u','a'),'v','a'),'w','a'),'x','a'),'y','a')"
                + ",'z','a'),'1','9'),'2','9'),'3','9'),'4','9'),'5','9'),'6','9')" + ",'7','9'),'8','9'),'0','9')";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#isNotBlank(java.lang.String)
     */
    @Override
    public String isNotBlank(String colName) {
        return " LTRIM(RTRIM(" + colName + ")) " + notEqual() + " '' ";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#extract(org.talend.dataquality.indicators.DateGrain,
     * java.lang.String)
     */
    @Override
    protected String extract(DateGrain dateGrain, String colName) {
        return "DATEPART(" + dateGrain.getName() + " , " + colName + ") ";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#supportNonIntegerConstantInGroupBy()
     */
    @Override
    public boolean supportNonIntegerConstantInGroupBy() {
        return false;
    }

}
