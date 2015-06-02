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
package org.talend.datascience.common.recordlinkage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * String clustering bean.
 * 
 * @author zhao
 *
 */
public class StringsCluster {
	//Note that the key order is reorgnized from comparator, so the map.get(key) will return null, use getSurvior(key) instead .
	private Map<CountKey, String> groupsize2Survivor = null;
	private Map<CountKey, List<String>> groupsize2All = null;

	public StringsCluster() {
		groupsize2Survivor = new TreeMap<CountKey, String>(
				new StringsClusterComparator());
		groupsize2All = new TreeMap<CountKey, List<String>>(
				new StringsClusterComparator());
	}

	public void put(Integer count, String survivor) {
		groupsize2Survivor.put(new CountKey(count), survivor);
	}

	public void put(Integer count, List<String> all) {
		groupsize2All.put(new CountKey(count), all);
	}

	public String getSurvior(CountKey key) {
		Set<Map.Entry<CountKey, String>> entrySet = groupsize2Survivor
				.entrySet();
		for (Map.Entry<CountKey, String> entry : entrySet) {
			if (entry.getKey() == key) {
				return entry.getValue();
			}
		}
		return null;
	}
	public List<String> getClusterOfStrings(CountKey key) {
		Set<Map.Entry<CountKey, List<String>>> entrySet = groupsize2All
				.entrySet();
		for (Map.Entry<CountKey, List<String>> entry : entrySet) {
			if (entry.getKey() == key) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	public Set<CountKey> getCountKeySet(){
		return groupsize2Survivor.keySet();
	}

	class StringsClusterComparator implements Comparator<CountKey> {
		@Override
		public int compare(CountKey o1, CountKey o2) {
			int diff = o2.getCount() - o1.getCount();
			if (diff == 0) {
				// Otherwise the previous identical item will be removed.
				diff = -1;
			}
			return diff;
		}
	}

	/**
	 * The count key class to avoid same count (integer) key issue in a map.
	 * 
	 * @author zhao
	 *
	 */
	class CountKey {
		private int count = 0;

		public CountKey(int count) {
			this.count = count;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

	}
}
