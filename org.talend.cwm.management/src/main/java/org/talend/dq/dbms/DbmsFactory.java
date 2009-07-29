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

import java.util.HashMap;

import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.cwm.dburl.SupportDBUrlType;


/**
 * DOC jet  this is a factory.<p>
 * it will create difference type of {@linkplain DbmsLanguage}.
 */
public class DbmsFactory {

    HashMap<SupportDBUrlType, DbmsLanguage> dbmsBuffer = null;

    public static DbmsFactory instance = new DbmsFactory();
    
    /**
     * DOC jet DbmsFactory is a sigle model.
     */
    private DbmsFactory() {
        super();
        
    }

    public DbmsLanguage getDbmsLanguage(String dataType){
        SupportDBUrlType dbType = SupportDBUrlStore.getInstance().getDBUrlType(dataType);
        return getDbmsLanguage(dbType);
    }
    
    /**
     * DOC jet Comment method "getDbmsLanguage".
     * @param dbType
     * @return
     */
    public DbmsLanguage getDbmsLanguage(SupportDBUrlType dbType) {
        
        DbmsLanguage result = null;
        
        if(dbType == null){
            return new DbmsLanguage();
        }
        
        switch (dbType) {
            case DB2ZOSDEFAULTURL:
                result = new DB2DbmsLanguage();
                break;
                
            case ORACLEWITHSERVICENAMEDEFAULTURL:
            case ORACLEWITHSIDDEFAULTURL:
                result = new OracleDbmsLanguage();
                break;
                
            case SYBASEDEFAULTURL:
                result = new SybaseASEDbmsLanguage();
                break;
                
            case MSSQLDEFAULTURL:
                result = new MSSqlDbmsLanguage();
                break;
                
            case MYSQLDEFAULTURL:
            default:
                result = new DbmsLanguage();
        }
        return result;
    }

    public HashMap<SupportDBUrlType, DbmsLanguage> getDbmsBuffer() {
        if(dbmsBuffer == null){
            dbmsBuffer = new HashMap<SupportDBUrlType, DbmsLanguage>();
        }
        return dbmsBuffer;
    }
    
}
