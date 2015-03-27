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
package org.talend.datascience.common.inference

import org.talend.datascience.common.data.ElasticDataSet
import org.talend.datascience.common.schema.SemanticSchema
import org.talend.datascience.common.data.ElasticData

/**
 * The trait is type inference executor which guess the schema given underlying data from dataset and perform suggested transformation on demand.
 * @author mzhao
 */
trait TypeInferExecutor {

  /**
   * Inferring types given a data set.
   * @since 1.0
   * @author mzhao
   * @param eds Elastic data set without schemas
   * @return Semantic schemas.
   */
  def inferTypes(eds: ElasticDataSet): SemanticSchema[Any]

  /**
   * Set transformation parameters.
   * @since 1.0
   * @author mzhao
   * @param transformParameter The transform parameters
   */
  def setParameter(transformParameter: TransformParameter)

  /**
   * Transform the data without user's interference with suggested resolution
   * @since 1.0
   * @author mzhao
   * @return Elastic data with constraints of underlying computation model.
   */
  def suggestTransform(): ElasticData[Any]

}
object TypeInferExecutor {
  /**
   * Creating inference executor instance given class name. The class of the specified name must be presented in class path.
   * @since 1.0
   * @author mzhao
   * @param executorClassName The full qualified executor class name.
   * @return Type inference executor instance.
   */
  def createInferExcutor(executorClassName: String): TypeInferExecutor = {
    val aclass = Class.forName(executorClassName)
    aclass.newInstance().asInstanceOf[TypeInferExecutor]
  }
}