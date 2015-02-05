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
package org.talend.datascience.common.preprocess

/**
 * @author zhao
 * @param label, the class of which the record belongs to
 * @param count, the count of records within this class (label)
 * @param segmentedFeatures segmented feature vector, e.g. Vector(List(2, 1, 0),List(3, 0), List(2, 1), List(2, 1, 0), List(3, 0))
 */
class AggregatedFeature(val label:String,val count: Long, val segmentedFeatures:Vector[List[Double]]) extends Serializable{

}