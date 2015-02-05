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

import org.talend.datascience.common.data.ElasticData

trait DataAggregation[ Context] {

  def aggregateFeatures(row: ElasticData[Any], context: Context): ElasticData[Any]
  
  def collect(model: ElasticData[Any]): Array[AggregatedFeature]

}
