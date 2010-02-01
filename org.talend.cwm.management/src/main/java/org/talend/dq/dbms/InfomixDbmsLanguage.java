// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.utils.ProductVersion;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class InfomixDbmsLanguage extends DbmsLanguage {

    static final String REPLACE_COLUMN_ALIASE = "replace_column";

    static final String SOUNDEX_COLUMN_ALIASE = "soundex_column_result";

    InfomixDbmsLanguage() {
        super(DbmsLanguage.INFOMIX);
    }

    InfomixDbmsLanguage(String dbmsType) {
        super(dbmsType);
    }

    InfomixDbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        super(dbmsType, dbVersion);
    }

    @Override
    public String getDelimiter() {
        return DbmsLanguage.COLON;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#getTopNQuery(java.lang.String, int)
     */
    @Override
    public String getTopNQuery(String query, int n) {

        return query.replaceFirst("SELECT", "SELECT FIRST " + n); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#getPatternFinderDefaultFunction(java.lang.String)
     */
    @Override
    public String getPatternFinderDefaultFunction(String expression) {
        return StringUtils.repeat("REPLACE(", 59) + expression //$NON-NLS-1$
                + ",'B','A'),'C','A'),'D','A'),'E','A'),'F','A'),'G','A'),'H','A')" //$NON-NLS-1$
                + ",'I','A'),'J','A'),'K','A'),'L','A'),'M','A'),'N','A'),'O','A')" //$NON-NLS-1$
                + ",'P','A'),'Q','A'),'R','A'),'S','A'),'T','A'),'U','A'),'V','A')" //$NON-NLS-1$
                + ",'W','A'),'X','A'),'Y','A'),'Z','A'),'b','a'),'c','a'),'d','a')" //$NON-NLS-1$
                + ",'e','a'),'f','a'),'g','a'),'h','a'),'i','a'),'j','a'),'k','a')" //$NON-NLS-1$
                + ",'l','a'),'m','a'),'n','a'),'o','a'),'p','a'),'q','a'),'r','a')" //$NON-NLS-1$
                + ",'s','a'),'t','a'),'u','a'),'v','a'),'w','a'),'x','a'),'y','a')" //$NON-NLS-1$
                + ",'z','a'),'1','9'),'2','9'),'3','9'),'4','9'),'5','9'),'6','9')" + ",'7','9'),'8','9'),'0','9') as " + REPLACE_COLUMN_ALIASE; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected String getPatternFinderFunction(String expression, String charsToReplace, String replacementChars) {
        assert charsToReplace != null && replacementChars != null && charsToReplace.length() == replacementChars.length();
        for (int i = 0; i < charsToReplace.length(); i++) {
            final char charToReplace = charsToReplace.charAt(i);
            final char replacement = replacementChars.charAt(i);
            expression = replaceOneChar(expression, charToReplace, replacement);
        }
        return expression + " as " + REPLACE_COLUMN_ALIASE;
    }

    @Override
    public String fillGenericQueryWithColumnTableAndAlias(String genericQuery, String columns, String table, String groupByAliases) {
        if (columns.indexOf(this.REPLACE_COLUMN_ALIASE) > -1) {
            String fromCaluse = "(SELECT " + columns + " FROM " + table + ")";
            return new GenericSQLHandler(genericQuery).replaceColumnTableAlias(this.REPLACE_COLUMN_ALIASE, fromCaluse,
                    this.REPLACE_COLUMN_ALIASE).getSqlString();
        } else if (table.indexOf(this.SOUNDEX_COLUMN_ALIASE) > -1) {
            while (genericQuery.toUpperCase().indexOf("SOUNDEX(" + GenericSQLHandler.COLUMN_NAMES + ")") > -1) {
                genericQuery = genericQuery.toUpperCase().replace("SOUNDEX(" + GenericSQLHandler.COLUMN_NAMES + ")",
                        this.SOUNDEX_COLUMN_ALIASE);
            }
        } else {
            groupByAliases = computeAliasesIndex(columns, groupByAliases);
        }
        return super.fillGenericQueryWithColumnTableAndAlias(genericQuery, columns, table, groupByAliases);

    }

    private String computeAliasesIndex(String columns, String groupByAliases) {
        if (null == columns || columns.equals("*") || columns.equals("")) {
            return groupByAliases;

        } else if (columns.indexOf(groupByAliases) > -1) {
            String[] columnArray = columns.split(",");
            for (int i = 0; i < columnArray.length; i++) {
                if (columnArray[i].equals(groupByAliases)) {
                    return String.valueOf(i + 1);
                }
            }
        }
        return groupByAliases;

    }

    @Override
    public String getSoundexFunction(String table, String colName) {
        String tableName = table;
        tableName = "(select "
                + colName
                + ",first_char||second_char||rpad(replace_str2,1,'0')||rpad(replace(substring(rpad(replace_str2,100,'0') from 2),substring(rpad(replace_str2,100,'0') from 1 for 1),''),1,'0') as "
                + this.SOUNDEX_COLUMN_ALIASE
                + " from (select "
                + colName
                + ", first_char,rpad(replace_str,1,'0') as second_char,replace(substring(replace_str from 2),substring(replace_str from 1 for 1),'') as replace_str2 from(select "
                + colName
                + ", substring(UPPER(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace("
                + colName
                + ",'1',''),'2',''),'3',''),'4',''),'5',''),'6',''),'7',''),'8',''),'9',''),'0','')) from 1 for 1)as first_char,rpad(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(substring(rpad(UPPER(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace("
                + colName
                + ",'1',''),'2',''),'3',''),'4',''),'5',''),'6',''),'7',''),'8',''),'9',''),'0','')),100,'0') from 2),'A','0'),'E','0'),'H','0'),'I','0'),'O','0'),'U','0'),'W','0'),'Y','0'),'B','1'),'F','1'),'P','1'),'V','1'),'C','2'),'G','2'),'J','2'),'K','2'),'Q','2'),'S','2'),'X','2'),'Z','2'),'D','3'),'T','3'),'L','4'),'M','5'),'N','5'),'R','6'),'0',''),100,'0') as replace_str from "
                + table + ")))";
        return tableName;
    }

    @Override
    public String getFreqRowsStatement(String colName, String table, String key) {
        String sqlStatment = "select t3.* from (select  "
                + colName
                + " ,first_char||second_char||rpad(replace_str2,1,'0')||rpad(replace(substring(rpad(replace_str2,100,'0') from 2),substring(rpad(replace_str2,100,'0') from 1 for 1),''),1,'0') as soundex_column_result from (select  name , first_char,rpad(replace_str,1,'0') as second_char,replace(substring(replace_str from 2),substring(replace_str from 1 for 1),'') as replace_str2 from(select  "
                + colName
                + " , substring(UPPER(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace( "
                + colName
                + " ,'1',''),'2',''),'3',''),'4',''),'5',''),'6',''),'7',''),'8',''),'9',''),'0','')) from 1 for 1)as first_char,rpad(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(substring(rpad(UPPER(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace( "
                + colName
                + " ,'1',''),'2',''),'3',''),'4',''),'5',''),'6',''),'7',''),'8',''),'9',''),'0','')),100,'0') from 2),'A','0'),'E','0'),'H','0'),'I','0'),'O','0'),'U','0'),'W','0'),'Y','0'),'B','1'),'F','1'),'P','1'),'V','1'),'C','2'),'G','2'),'J','2'),'K','2'),'Q','2'),'S','2'),'X','2'),'Z','2'),'D','3'),'T','3'),'L','4'),'M','5'),'N','5'),'R','6'),'0',''),100,'0') as replace_str from  "
                + table
                + " ))) as t1,(select  "
                + colName
                + " ,first_char||second_char||rpad(replace_str2,1,'0')||rpad(replace(substring(rpad(replace_str2,100,'0') from 2),substring(rpad(replace_str2,100,'0') from 1 for 1),''),1,'0') as soundex_column_result from (select  "
                + colName
                + " , first_char,rpad(replace_str,1,'0') as second_char,replace(substring(replace_str from 2),substring(replace_str from 1 for 1),'') as replace_str2 from(select  "
                + colName
                + " , substring(UPPER(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace( "
                + colName
                + " ,'1',''),'2',''),'3',''),'4',''),'5',''),'6',''),'7',''),'8',''),'9',''),'0','')) from 1 for 1)as first_char,rpad(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(substring(rpad(UPPER(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace( "
                + colName
                + " ,'1',''),'2',''),'3',''),'4',''),'5',''),'6',''),'7',''),'8',''),'9',''),'0','')),100,'0') from 2),'A','0'),'E','0'),'H','0'),'I','0'),'O','0'),'U','0'),'W','0'),'Y','0'),'B','1'),'F','1'),'P','1'),'V','1'),'C','2'),'G','2'),'J','2'),'K','2'),'Q','2'),'S','2'),'X','2'),'Z','2'),'D','3'),'T','3'),'L','4'),'M','5'),'N','5'),'R','6'),'0',''),100,'0') as replace_str from  "
                + table + " ))) as t2 ,test_talend : test1 as t3 where t1." + colName + "='" + key
                + "' and t2.soundex_column_result=t1.soundex_column_result and t2." + colName + "=t3." + colName + "";
        return sqlStatment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#extract(org.talend.dataquality.indicators.DateGrain,
     * java.lang.String)
     */
    @Override
    protected String extract(DateGrain dateGrain, String colName) {
        return dateGrain.getName() + surroundWith('(', colName, ')');
    }

}
