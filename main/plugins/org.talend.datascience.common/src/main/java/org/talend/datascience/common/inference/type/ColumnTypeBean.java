// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.datascience.common.inference.type;

import java.util.HashMap;
import java.util.Map;

/**
 * Column type bean.
 * 
 * @author zhao
 *
 */
public class ColumnTypeBean {

	private int columnIdx;
	private Map<String, Long> typeToCountMap = new HashMap<String, Long>();
	private Map<String, Long> semanticNameToCount = new HashMap<String, Long>();

	public Integer getColumnIdx() {
		return columnIdx;
	}

	public void setColumnIdx(Integer idx) {
		this.columnIdx = idx;
	}

	public void putTypeToCount(String type, Long count) {
		typeToCountMap.put(type, count);
	}

	public void putSemanticNameToCount(String semanticName, Long count) {
		semanticNameToCount.put(semanticName, count);
	}

	public Map<String,Long> getSemanticNameToCountMap(){
		return semanticNameToCount;
	}
	
	public Long getSemanticTypeCount(String semanticCategoryName){
		return semanticNameToCount.get(semanticCategoryName);
	}
	
	/**
	 * get the count given data type name
	 * 
	 * @param typeName
	 * @return
	 */
	public Long getDataTypeCount(String typeName) {
		return typeToCountMap.get(typeName);
	}

	public Map<String, Long> getTypeToCountMap() {
		return typeToCountMap;
	}
}
