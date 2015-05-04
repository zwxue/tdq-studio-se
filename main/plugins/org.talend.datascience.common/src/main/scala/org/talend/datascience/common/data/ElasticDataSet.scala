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
 * The trait is a representation of a dataset with context. <br> the context is environment variable with probably several parameters , such as spark's SparkContext with HDFS storage path , or JobConf in map/reduce model.
 * @author mzhao
 */
trait ElasticDataSet extends Serializable {
  def stop()

  /**
   * Emit an unit data from data set. <br> The data emitted is confined to the computation model.
   * @since 1.0
   * @author mzhao
   * @return A unit of elastic data.
   */
  def emit(): ElasticData[Any]
  
  /**
   * Set the path of the data source
   * @since 1.0
   * @author mzhao
   * @param path The data source path that can be a file path locally or a HDFS path remotely. 
   */
  def setDataSourcePath(path: String)
  
  
  /**
   * Set the data field with path.
   */
  def setJsonRecordPath(jsonFieldPath:String)
  /**
   * Set the data input format.
   * @since 1.0
   * @author zhao
   * @param data input format (json, csv etc. )
   */
  def setDataInputFormat(format:String)
  
  /**
   * Set the field seperator
   * @since 1.0
   * @author mzhao
   * @param seperator set the field split seperator..
   */
  def setFieldSeperator(seprator:String)
  
  
  /**
   * Set the data set context.
   * @since 1.0
   * @author mzhao
   * @param url context URL
   * @param name context name.
   */
  def setContext(url:String,name:String)
}

object ElasticDataSet {
  /**
   * Create a data set instance with data set class name. The class with the specified name must be present in classpath.
   * @since 1.0
   * @author mzhao
   * @param ElasticDataSetClassName Full qualified class name to be instantiated.
   * @return Elastic data set instance.
   */
  def createDataSet(elasticDataSetClassName: String): ElasticDataSet = {
    val aclass = Class.forName(elasticDataSetClassName)
    aclass.newInstance().asInstanceOf[ElasticDataSet]
  }
 
}