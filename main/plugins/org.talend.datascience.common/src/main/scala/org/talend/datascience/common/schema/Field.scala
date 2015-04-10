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

/**
 * A field trait that represents a field of a record (or row , data etc.).
 * @author mzhao
 */
trait Field[StructField] extends Serializable{
  val name: String
  private[schema] var dataType: DataType[Any]
  val nullable: Boolean = true
  val size: Int
  var description: String
  /**
   * The index of this field in a record.
   */
  var index: Int = 0
  /**
   * Get the strut field that the implementor provided. This function is intended to be called from API internally.
   * @since 1.0
   * @author mzhao
   * @return struct field of underlying implemented class.
   */
  private[datascience] def getStructField: StructField
  
  /**
   * Set the field's data type.
   * @since 1.0
   * @author mzhao
   * @param the data type set for this field.
   */
  private[datascience]  def setDataType(dType:DataType[Any])
  
  /**
   * Get the field's data type.
   * @since 1.0
   * @author mzhao
   * @return data type of this field.
   */
  def getDataType=dataType
  
  def isNumericalField:Boolean
}