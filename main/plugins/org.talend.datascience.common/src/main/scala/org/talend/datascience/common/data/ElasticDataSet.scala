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
package org.talend.datascience.common.data

trait ElasticDataSet[Context] extends Serializable {
  def emit(): ElasticData[Any]
  def setDataSource(path:String)
  def getContext: Context
}

object ElasticDataSet {
  def createDataSet(elasticDataSetClassName: String): ElasticDataSet[Any] = {
    val aclass = Class.forName(elasticDataSetClassName)
    aclass.newInstance().asInstanceOf[ElasticDataSet[Any]]
  }
}