package org.talend.datascience.common.inference

import org.talend.datascience.common.data.ElasticDataSet
import org.talend.datascience.common.schema.SemanticSchema
import org.talend.datascience.common.data.ElasticData

trait TypeInferExecutor {

  def inferTypes(eds: ElasticDataSet[Any]): SemanticSchema[Any]
  def setParameter(transformParameter:TransformParameter)
  def suggestTransform(): ElasticData[Any]

}
object TypeInferExecutor {
  def createInferExcutor(exectorClassName: String): TypeInferExecutor = {
    val aclass = Class.forName(exectorClassName)
    aclass.newInstance().asInstanceOf[TypeInferExecutor]
  }
}