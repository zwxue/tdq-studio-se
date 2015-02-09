package org.talend.datascience.common.transform

object MissingValueResolutionOption extends Enumeration{
  type FillWith=Value
  
  val fillWithMean,fillWithMedian,fillWithMax,fillWithMin,fillWithGaussian,fillWithPoisson,fillZero=Value
}