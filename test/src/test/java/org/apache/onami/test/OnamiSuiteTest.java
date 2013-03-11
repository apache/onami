package org.apache.onami.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(OnamiSuite.class)
@SuiteClasses({ InjectDependingMockObjectTestCase.class, InjectFromSuperClassTestCase.class })
public class OnamiSuiteTest {

}
