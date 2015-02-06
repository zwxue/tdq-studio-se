package org.talend.datascience.common.inference

import org.talend.datascience.common.data.ElasticDataSet
import org.talend.datascience.common.schema.SemanticSchema

trait TypeInferExecutor {

  def inferTypes(eds: ElasticDataSet[Any], transformParameter: TransformParameter):SemanticSchema[Any]

}
object TypeInferExecutor {
  def createInferExcutor(exectorClassName: String): TypeInferExecutor = {
    val aclass = Class.forName(exectorClassName)
    aclass.newInstance().asInstanceOf[TypeInferExecutor]
  }
}