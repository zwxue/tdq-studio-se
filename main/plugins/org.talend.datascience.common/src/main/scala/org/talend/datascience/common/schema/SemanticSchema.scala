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
 * A schema trait that contains all fields. Schema is implementor specific class .
 * @author zhao
 * @since 1.0
 */
trait SemanticSchema[+Schema] extends Serializable {
  val fields: Seq[SemanticField]
  /**
   * Get the underlying schema provided by implementor.
   * @since 1.0
   * @author mzhao
   * @return the implementor specific schema.
   */
  private[datascience] def getSchema: Schema
  
  /**
   * Get json string given schema information
   * @since 1.0
   * @return json representation of the schema.
   */
  def getJson:String
  
  /**
   * Get json string for "group" of data (grouped by columns ) given schema information
   * @since 1.0
   * @return json string adapted to the grouped data. 
   */
  def getJsonGroupBy:String
}