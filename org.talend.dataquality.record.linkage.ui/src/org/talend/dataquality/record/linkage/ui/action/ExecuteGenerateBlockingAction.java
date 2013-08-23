// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.talend.dataquality.record.linkage.genkey.AbstractGenerateKey;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class ExecuteGenerateBlockingAction extends Action {

    private List<Object[]> inputData = new ArrayList<Object[]>();

    private AbstractGenerateKey generateKeyAPI = new AbstractGenerateKey();

    private List<Map<String, String>> BlockKeyDefinitions = null;

    protected Map<String, String> columnIndexMap = null;

    /**
     * Getter for inputData.
     * 
     * @return the inputData
     */
    public List<Object[]> getInputData() {
        return this.inputData;
    }

    /**
     * Sets the inputData.
     * 
     * @param inputData the inputData to set
     */
    public void setInputData(List<Object[]> inputData) {
        this.inputData = inputData;
    }

    public ExecuteGenerateBlockingAction(List<Map<String, String>> BlockKeyDefinitions, Map<String, String> columnMap) {
        this.BlockKeyDefinitions = BlockKeyDefinitions;
        this.columnIndexMap = columnMap;
    }

    @Override
    public void run() {
        for (Object[] inputObject : this.inputData) {
            String[] inputString = new String[inputObject.length];
            int index = 0;
            for (Object obj : inputObject) {
                inputString[index++] = obj == null ? null : obj.toString();
            }
            Map<String, String> ColumnValueMap = new HashMap<String, String>();
            for (String columnName : columnIndexMap.keySet()) {
                ColumnValueMap.put(columnName, inputString[Integer.parseInt(columnIndexMap.get(columnName))]);
            }
            generateKeyAPI.generateKey(BlockKeyDefinitions, ColumnValueMap, inputString);
        }
    }

    public List<Map<String, String>> getResultData() {
        return BlockKeyDefinitions;
    }

    public List<String[]> getResultDatas() {
        return generateKeyAPI.getResultList();
    }

}
