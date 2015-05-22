package org.talend.datascience.common.inference.type;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.datascience.common.inference.semantic.SemanticInferExecutorTest;

@RunWith(Suite.class)
@SuiteClasses({ DataTypeInferExecutorTest.class,
		SemanticAndDataTypeInferExecutorTest.class,
		InferenceHubTest.class, TypeInferenceUtilsTest.class,SemanticInferExecutorTest.class })
public class SemanticAndDataTypeAllTests {

}
