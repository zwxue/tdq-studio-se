package org.talend.datascience.common.transform

import org.talend.datascience.common.data.ElasticData
import scala.collection.mutable.ArrayBuffer

class TransformHistory extends Serializable{

  private[transform] val operationStack = ArrayBuffer[ElasticData[Any]]()

  def append(elasticData:ElasticData[Any])={
    operationStack.append(elasticData)
  }
  
  def backward(currentStep: Int): ElasticData[Any] = {
    if (currentStep > 0)
      operationStack(currentStep - 1)
    else
      operationStack(currentStep)
  }

  def forward(currentStep: Int): ElasticData[Any] = {
    if (currentStep < operationStack.size-1)
      operationStack(currentStep + 1)
    else
      operationStack(currentStep)
  }

  def go(currentStep: Int, idx: Int): ElasticData[Any] = {
    if (0 <= idx && idx < operationStack.size)
      operationStack(idx)
    else
      operationStack(currentStep)
  }

}