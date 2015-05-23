package org.talend.datascience.common.inference;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.datascience.common.inference.composite.CompositeAnalyzerTest;
import org.talend.datascience.common.inference.quality.ValueQualityAnalyzerTest;
import org.talend.datascience.common.inference.semantic.SemanticAnalyzerTest;
import org.talend.datascience.common.inference.type.DataTypeAnalyzerTest;
import org.talend.datascience.common.inference.type.TypeInferenceUtilsTest;

@RunWith(Suite.class)
@SuiteClasses({ CompositeAnalyzerTest.class, SemanticAnalyzerTest.class, DataTypeAnalyzerTest.class, ValueQualityAnalyzerTest.class, TypeInferenceUtilsTest.class })
public class Tests {
}
