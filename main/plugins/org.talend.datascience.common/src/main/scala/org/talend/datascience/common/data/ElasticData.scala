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

/**
 * The trait is generic data representation with computation model.<br> The computation model is an abstract concept that could be a computation model such as RDD from spark, Mappper/Reducer from batch processer or Spout/Bolt from storm.
 * @author mzhao
 */
trait ElasticData[+ComputeModel] extends Serializable {
  /**
   * Return the computation model. This function is proposed to be used internally from API in order to hide different technique implementation details
   * @since 1.0
   */
  private[datascience] def getModel: ComputeModel

  /**
   * Process data by passing each to function. <br> Function f must be defined with one parameter without a return value.
   * @since 1.0
   * @author mzhao
   * @param f function processes each data
   */
  def foreach[U](f: Any => Unit): Unit

  /**
   * Get a whole collection of data.
   * @since 1.0
   * @author mzhao
   * @return Array representation of data sets.
   */
  def collect(): Array[Any]

  /**
   * Takes first num data.
   * @since 1.0
   * @author mzhao
   * @param num the number data to be taken from dataset.
   * @return First num array representation of data sets
   */
  def take(num: Int): Array[Any]

  /**
   * Take the very first row of data.
   * @since 1.0
   * @author mzhao
   * @return The row data indexed 1 from dataset.
   */
  def first(): Any
  
  def isEmpty():Boolean
  
  /**
   * Take the column of data given field index.
   * @since 1.0
   * @auth mzhao
   * @return the column data given field index from dataset.
   */
  def select(fieldIdx:Int):ElasticData[Any]
}