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
package org.talend.dq.helper.resourcehelper;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.utils.security.CryptoHelper;
import orgomg.cwm.foundation.softwaredeployment.impl.DataProviderImpl;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * @author hcheng
 * 
 * A class that helps to encrypt and decrypt password.
 * 
 * @deprecated do not use this class for password encryption/decryption. Note: this class could be used (adapted) for
 * the migration task.
 */
public class PasswordHelper extends CryptoHelper { // MOD scorreia 2008-01-08 extend CryptoHelper and use passphrase

    private static final String PASSPHRASE = DataProviderHelper.PASSPHRASE;

    private static final int ENCRYPT = 0;

    private static final int DECRYPT = 1;

    /**
     * PasswordHelper constructor.
     * 
     * Uses a static passphrase to encrypt and decrypt strings.
     */
    public PasswordHelper() {
        super(PASSPHRASE);
    }

    /**
     * @deprecated TODO hcheng remove this method
     */
    public static Resource encryptResource(Resource resource) {
        return dealResource(resource, ENCRYPT);
    }

    /**
     * @deprecated TODO hcheng remove this method
     */
    public static Resource decryptResource(Resource resource) {
        return dealResource(resource, DECRYPT);
    }

    /**
     * @deprecated TODO hcheng remove this method
     */
    private static Resource dealResource(Resource resource, int type) {
        EList el = resource.getContents();
        if (el != null) {
            for (int i = 0; i < el.size(); ++i) {
                Object obj = el.get(i);
                if (obj != null && obj instanceof DataProviderImpl) {
                    DataProviderImpl dp = (DataProviderImpl) obj;
                    // Connections
                    EList elConn = dp.getResourceConnection();
                    if (elConn != null) {
                        for (int k = 0; k < elConn.size(); ++k) {
                            TdProviderConnection tpc = (TdProviderConnection) elConn.get(k);
                            EList elTV = tpc.getTaggedValue();
                            if (elTV != null) {
                                for (int j = 0; j < elTV.size(); ++j) {
                                    TaggedValue tv = (TaggedValue) elTV.get(j);
                                    if (TaggedValueHelper.PASSWORD.equals(tv.getTag())) {
                                        String val = tv.getValue();
                                        switch (type) {
                                        case ENCRYPT:
                                            val = CryptoHelper.getCryptoHelper().encrypt(val);
                                            break;
                                        case DECRYPT:
                                            val = CryptoHelper.getCryptoHelper().decrypt(val);
                                            break;
                                        }
                                        tv.setValue(val);
                                    }
                                }
                            }
                        }
                    }
                    // TaggedValues
                    EList el2 = dp.getTaggedValue();
                    if (el2 != null) {
                        for (int j = 0; j < el2.size(); ++j) {
                            TaggedValue tv = (TaggedValue) el2.get(j);
                            if (TaggedValueHelper.PASSWORD.equals(tv.getTag())) {
                                String val = tv.getValue();
                                switch (type) {
                                case ENCRYPT:
                                    val = CryptoHelper.getCryptoHelper().encrypt(val);
                                    break;
                                case DECRYPT:
                                    val = CryptoHelper.getCryptoHelper().decrypt(val);
                                    break;
                                }
                                tv.setValue(val);
                            }
                        }
                    }
                }
            }
        }
        return resource;
    }

    public static TdDataProvider encryptDataProvider2(TdDataProvider dataProvider) {
        // DataProvider password encrypt
        EList dpList = dataProvider.getTaggedValue();
        if (dpList != null && dpList.size() > 0) {
            int dpSize = dpList.size();
            for (int j = 0; j < dpSize; ++j) {
                Object dpObj = dpList.get(j);
                if (dpObj != null) {
                    TaggedValue dp = (TaggedValue) dpObj;
                    if (TaggedValueHelper.PASSWORD.equals(dp.getTag())) {
                        dp.setValue(CryptoHelper.getCryptoHelper().encrypt(dp.getValue()));
                    }
                }
            }
        }

        // Connection password encrypt
        EList elist = dataProvider.getResourceConnection();
        for (int i = 0; i < elist.size(); ++i) {
            Object obj = elist.get(i);
            if (obj != null) {
                TdProviderConnection tdpConn = (TdProviderConnection) obj;
                EList tvList = tdpConn.getTaggedValue();
                if (tvList != null && tvList.size() > 0) {
                    int tvSize = tvList.size();
                    for (int j = 0; j < tvSize; ++j) {
                        Object tvObj = tvList.get(j);
                        if (tvObj != null) {
                            TaggedValue tv = (TaggedValue) tvObj;
                            if (TaggedValueHelper.PASSWORD.equals(tv.getTag())) {
                                tv.setValue(CryptoHelper.getCryptoHelper().encrypt(tv.getValue()));
                            }
                        }
                    }
                }
            }
        }

        return dataProvider;
    }
}
