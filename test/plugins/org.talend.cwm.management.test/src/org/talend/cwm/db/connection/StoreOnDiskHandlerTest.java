// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.db.connection;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.rules.AppliedBlockKey;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dataquality.rules.RulesPackage;
import org.talend.designer.components.lookup.persistent.IPersistentLookupManager;
import org.talend.dq.analysis.persistent.BlockKey;
import org.talend.dq.analysis.persistent.MatchRow;

/**
 * created by zhao on Oct 16, 2013 Detailled comment
 * 
 */
@SuppressWarnings("nls")
public class StoreOnDiskHandlerTest {

    private static String[][] data = new String[][] {
            { "mzhao", "Talend BJ", "mzhao@talend.com", "China", "Jianguomen wai Avenue" },
            { "zshen", "Talend BJ", "zshen@talend.com", "China", "Jianguomen wai Avenue,22" },
            { "scorreia", "Talend FR", "scorreia@talend.com", "France", "freedom street" },
            { "yyin", "Talend BJ", "yyin@talend.com", "China", "Jianguomen wai Avenue" },
            { "sizhaoliu", "Talend FR", "sizhaoliu@talend.com", "France", "freedom street" } };

    private static Map<MetadataColumn, String> columnMap = new HashMap<MetadataColumn, String>();
    static {
        MetadataColumn col1 = ConnectionFactory.eINSTANCE.createMetadataColumn();
        col1.setName("name");
        columnMap.put(col1, "0");
        MetadataColumn col2 = ConnectionFactory.eINSTANCE.createMetadataColumn();
        col2.setName("company");
        columnMap.put(col2, "1");
        MetadataColumn col3 = ConnectionFactory.eINSTANCE.createMetadataColumn();
        col3.setName("email");
        columnMap.put(col3, "2");
        MetadataColumn col4 = ConnectionFactory.eINSTANCE.createMetadataColumn();
        col4.setName("country");
        columnMap.put(col4, "3");
        MetadataColumn col5 = ConnectionFactory.eINSTANCE.createMetadataColumn();
        col5.setName("address");
        columnMap.put(col5, "4");
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.StoreOnDiskHandler#handleRow(java.lang.Object[])} .
     * 
     * @throws Exception
     */
    @Test
    public void testHandleRow() throws Exception {
        // ------------ Testing one block key ------------------
        RecordMatchingIndicator recordMatchingIndicator = ColumnsetPackage.eINSTANCE.getColumnsetFactory()
                .createRecordMatchingIndicator();
        MatchRuleDefinition matchRuleDef = RulesPackage.eINSTANCE.getRulesFactory().createMatchRuleDefinition();
        AppliedBlockKey appliedBlockKey = RulesPackage.eINSTANCE.getRulesFactory().createAppliedBlockKey();
        appliedBlockKey.setColumn("country");
        appliedBlockKey.setName("country");
        matchRuleDef.getAppliedBlockKeys().add(appliedBlockKey);
        recordMatchingIndicator.setBuiltInMatchRuleDefinition(matchRuleDef);

        Map<String, List<String[]>> blockToRowList = doBlockWithStoreOnDisk(recordMatchingIndicator);
        // Assert block count 2
        assertEquals(2, blockToRowList.size());
        // Assert 3 records in block "China"
        assertEquals(3, blockToRowList.get("China").size());
        assertArrayEquals(new String[][] { data[0], data[1], data[3] }, blockToRowList.get("China").toArray(new String[3][5]));
        // Assert 2 records in block "France"
        assertEquals(2, blockToRowList.get("France").size());
        assertArrayEquals(new String[][] { data[2], data[4] }, blockToRowList.get("France").toArray(new String[2][5]));

        // -----------------------Testing NO blocking key--------------------
        recordMatchingIndicator = ColumnsetPackage.eINSTANCE.getColumnsetFactory().createRecordMatchingIndicator();
        matchRuleDef = RulesPackage.eINSTANCE.getRulesFactory().createMatchRuleDefinition();
        recordMatchingIndicator.setBuiltInMatchRuleDefinition(matchRuleDef);
        blockToRowList = doBlockWithStoreOnDisk(recordMatchingIndicator);
        // Assert the records are 5 with no blocking key specified.
        assertArrayEquals(data, blockToRowList.get("").toArray(new String[5][5]));

        // -----------------------Testing with 2 blocking key--------------------

        recordMatchingIndicator = ColumnsetPackage.eINSTANCE.getColumnsetFactory().createRecordMatchingIndicator();
        matchRuleDef = RulesPackage.eINSTANCE.getRulesFactory().createMatchRuleDefinition();
        appliedBlockKey = RulesPackage.eINSTANCE.getRulesFactory().createAppliedBlockKey();
        appliedBlockKey.setColumn("company");
        appliedBlockKey.setName("company");
        matchRuleDef.getAppliedBlockKeys().add(appliedBlockKey);
        appliedBlockKey = RulesPackage.eINSTANCE.getRulesFactory().createAppliedBlockKey();
        appliedBlockKey.setColumn("address");
        appliedBlockKey.setName("address");
        matchRuleDef.getAppliedBlockKeys().add(appliedBlockKey);
        recordMatchingIndicator.setBuiltInMatchRuleDefinition(matchRuleDef);
        blockToRowList = doBlockWithStoreOnDisk(recordMatchingIndicator);
        // Assert 2 records
        assertArrayEquals(new String[][] { data[0], data[3] },
                blockToRowList.get("Talend BJJianguomen wai Avenue").toArray(new String[2][5]));
        // Assert 1 record
        assertArrayEquals(new String[][] { data[1] },
                blockToRowList.get("Talend BJJianguomen wai Avenue,22").toArray(new String[1][5]));
        // Assert 1 record
        assertArrayEquals(new String[][] { data[2], data[4] },
                blockToRowList.get("Talend FRfreedom street").toArray(new String[1][5]));
    }

    /**
     * DOC zhao Comment method "doBlockWithStoreOnDisk".
     * 
     * @param recordMatchingIndicator
     * @return
     * @throws IOException
     * @throws Exception
     */
    private Map<String, List<String[]>> doBlockWithStoreOnDisk(RecordMatchingIndicator recordMatchingIndicator) throws Exception {

        URL fileUrl = this.getClass().getResource("storeOnDisk"); //$NON-NLS-1$
        String container = null;
        try {
            container = FileLocator.toFileURL(fileUrl).toURI().getPath().toString();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        int buffSize = 2;

        StoreOnDiskHandler storeOnDiskSQLExecutor = new StoreOnDiskHandler(recordMatchingIndicator, columnMap, container,
                buffSize);
        storeOnDiskSQLExecutor.beginQuery();
        for (Object[] d : data) {
            storeOnDiskSQLExecutor.handleRow(d);
        }
        storeOnDiskSQLExecutor.endQuery();

        IPersistentLookupManager<MatchRow> persistentLookupManager = storeOnDiskSQLExecutor.getPersistentLookupManager();
        persistentLookupManager.initGet();
        Map<BlockKey, String> blockKeyList = storeOnDiskSQLExecutor.getBlockKeys();
        Map<String, List<String[]>> blockToRowList = new HashMap<String, List<String[]>>();
        Iterator<BlockKey> it = blockKeyList.keySet().iterator();
        Boolean isBKEmpty = blockKeyList.isEmpty();
        MatchRow mr = null;
        while (it.hasNext() || isBKEmpty) {
            if (isBKEmpty) {
                isBKEmpty = Boolean.FALSE;
                mr = new MatchRow(columnMap.size(), 0);
            } else {
                BlockKey blockKey = it.next();
                mr = new MatchRow(columnMap.size(), blockKey.getBlockKey().size());
                mr.setKey(blockKey.getBlockKey());
            }
            mr.hashCodeDirty = true;
            persistentLookupManager.lookup(mr);
            // Find the block records from a block
            if (mr.getKey().size() > 0) {
                System.out.print("Block: ");
                for (String bkUnit : mr.getKey()) {
                    System.out.print(bkUnit + ",");
                }
                System.out.println();
            } else {
                System.out.println("Block: [EMPTY]");
            }
            List<String[]> rows = new ArrayList<String[]>();
            while (persistentLookupManager.hasNext()) {
                MatchRow row = persistentLookupManager.next();
                System.out.println(row);
                rows.add(row.getRow().toArray(new String[row.getRow().size()]));
            }
            if (mr.getKey().size() > 0) {
                blockToRowList.put(StringUtils.join(mr.getKey().toArray(new String[mr.getKey().size()])), rows);
            } else {
                blockToRowList.put("", rows);

            }
        }
        persistentLookupManager.endGet();
        return blockToRowList;
    }
}
