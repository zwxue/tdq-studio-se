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
 * A data type trait represents the type of a field. <br> It's a generic type definition which the implementor can add further type information.<p> The type T is used internally only that can be a type from implementor's library.
 * @author mzhao
 */
trait DataType[+T] extends Serializable {

  /**
   * Get the underlying type T, this function is intended to be called inside the API.
   * @since 1.0
   * @author mzhao
   * @return internal type T
   */
  private[datascience] def getType: T
  /**
   * Get the type name as string
   * @since 1.0
   * @author mzhao
   * @return a type name as string
   */
  def getTypeName: String
  
  /**
   * Get the java (jvm) type as integer
   * @since 1.0
   * @author mzhao
   * @return a java type as integer
   */
  def getJavaType: Int
  
  /**
   * Set the underlying type which intended to be used from API internally.
   * @since 1.0
   * @author mzhao 
   * @param the underlying implementor's specific type .
   */
  private[datascience] def setType(dType: Any)
  
}