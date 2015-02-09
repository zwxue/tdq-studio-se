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
package org.talend.datascience.common.schema

import scala.collection.mutable.ListBuffer
import org.talend.datascience.common.statistics.FieldStatistics

trait Field[StructField] extends Serializable{
  val name: String
  var dataType: DataType[Any]
  val nullable: Boolean = true
  val size: Int
  val description: String
  val pattern: String
  var index: Int
  def getStructField: StructField
  def setDataType(dType:DataType[Any])
  var suggestedType:(DataType[Any], ListBuffer[Map[DataType[Any], (Long,FieldStatistics)]])//(data type name, type infer details)
}