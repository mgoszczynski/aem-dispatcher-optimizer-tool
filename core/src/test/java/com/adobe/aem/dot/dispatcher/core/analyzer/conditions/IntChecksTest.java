/*
 *    Copyright 2021 Adobe. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.adobe.aem.dot.dispatcher.core.analyzer.conditions;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.adobe.aem.dot.common.ConfigurationException;
import com.adobe.aem.dot.common.analyzer.CheckResult;
import com.adobe.aem.dot.common.analyzer.ViolationVerbosity;
import com.adobe.aem.dot.common.helpers.AssertHelper;
import com.adobe.aem.dot.common.helpers.DispatcherConfigTestHelper;
import com.adobe.aem.dot.common.parser.ConfigurationParseResults;
import com.adobe.aem.dot.common.util.PathUtil;
import com.adobe.aem.dot.dispatcher.core.DispatcherConstants;
import com.adobe.aem.dot.dispatcher.core.model.DispatcherConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class IntChecksTest {
  private static DispatcherConfigTestHelper helper;
  private static ListAppender<ILoggingEvent> listAppender;

  @BeforeClass
  public static void before() {
    helper = new DispatcherConfigTestHelper();
    listAppender = AssertHelper.getLogAppender(IntCheck.class);
  }

  @Test
  public void emptyConfigValueCase() {
    IntCheck check = new IntEqualsCheck();
    CheckResult checkResult = check.performCheck(null);
    assertFalse("null value should be negative", checkResult.isPassed());

    checkResult = check.performCheck("");
    assertFalse("empty value should be negative", checkResult.isPassed());

    IntGreaterOrEqualCheck check2 = new IntGreaterOrEqualCheck();
    CheckResult checkResult2 = check2.performCheck(null);
    assertFalse("null value should be negative", checkResult2.isPassed());

    checkResult2 = check2.performCheck("");
    assertFalse("empty value should be negative", checkResult2.isPassed());
  }

  @Test
  public void differentConfigurationValueCase() {
    String absPath = DispatcherConfigTestHelper.getConfigFileAbsolutePath(this.getClass(),
            "../../model/auth_checker/" + DispatcherConstants.DISPATCHER_ANY);
    String anyPath = PathUtil.stripLastPathElement(absPath);

    try {
      ConfigurationParseResults<DispatcherConfiguration> results = helper.loadDispatcherConfiguration(absPath);
      DispatcherConfiguration config = results.getConfiguration();
      assertNotNull("Config should not be null", config);
      assertEquals(0, results.getViolations(ViolationVerbosity.MINIMIZED).size());
      assertNotNull("Config's farms should not be null", config.getFarms());
      assertEquals("Should be 1 farm", 1, config.getFarms().size());

      IntGreaterOrEqualCheck check = new IntGreaterOrEqualCheck();
      CheckResult checkResult = check.performCheck(config.getFarms().get(0));
      assertFalse("bad cast", checkResult.isPassed());

      List<ILoggingEvent> logsList = listAppender.list;
      assertEquals("Value could not be compared as an integer. Value=\"{}\"", logsList.get(0).getMessage());
      assertEquals("Severity should be ERROR.", Level.ERROR, logsList.get(0).getLevel());
    } catch(ConfigurationException dcEx) {
      Assert.fail("Failure: " + dcEx.getLocalizedMessage());
    }
  }
}
