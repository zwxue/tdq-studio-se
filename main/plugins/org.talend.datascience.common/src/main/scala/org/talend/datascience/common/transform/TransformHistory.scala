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
package org.talend.datascience.common.transform

import org.talend.datascience.common.data.ElasticData
import scala.collection.mutable.ArrayBuffer

/**
 * Data transform history that can facilitates undo and redo operations.
 * @since 1.0
 * @author mzhao
 */
class TransformHistory extends Serializable{

  private[transform] val operationStack = ArrayBuffer[ElasticData[Any]]()

  /**
   * Append the data into the array stack.
   * @since 1.0
   * @author mzhao
   * @param elasticData The data after the transformation.
   */
  def append(elasticData:ElasticData[Any])={
    operationStack.append(elasticData)
  }
  
  /**
   * Backward to the status of previous operation.
   * @since 1.0
   * @author mzhao
   * @param currentStep The current step.
   * @return the elastic data of previous step.
   */
  def backward(currentStep: Int): ElasticData[Any] = {
    if (currentStep > 0)
      operationStack(currentStep - 1)
    else
      operationStack(currentStep)
  }

  /**
   * Forward to the status of next operation if and only if the backward operation happens.
   * @since 1.0
   * @author mzhao
   * @param currentStep the current step.
   * @return the elastic data of next step.
   */
  def forward(currentStep: Int): ElasticData[Any] = {
    if (currentStep < operationStack.size-1)
      operationStack(currentStep + 1)
    else
      operationStack(currentStep)
  }
  
  /**
   * Go to the certain step of the history.
   * @since 1.0
   * @author mzhao
   * @param currentStep the current step.
   * @param idx the step to go.
   * @return the elastic data of the step to go.
   */
  def go(currentStep: Int, idx: Int): ElasticData[Any] = {
    if (0 <= idx && idx < operationStack.size)
      operationStack(idx)
    else
      operationStack(currentStep)
  }

}