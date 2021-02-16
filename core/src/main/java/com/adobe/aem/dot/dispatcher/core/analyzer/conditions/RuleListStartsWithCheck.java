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

import com.adobe.aem.dot.common.analyzer.Check;
import com.adobe.aem.dot.common.analyzer.CheckResult;
import com.adobe.aem.dot.common.analyzer.Condition;
import com.adobe.aem.dot.dispatcher.core.model.ConfigurationValue;
import com.adobe.aem.dot.dispatcher.core.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Check that a Rule list starts with a particular Rule.
 */
public class RuleListStartsWithCheck extends Check {
  private static final Logger logger = LoggerFactory.getLogger(RuleListStartsWithCheck.class);

  @Override
  public Condition getCondition() {
    return Condition.RULE_LIST_STARTS_WITH;
  }

  @SuppressWarnings("unchecked")
  @Override
  public CheckResult performCheck(Object configurationValue) {
    if (!(configurationValue instanceof ConfigurationValue<?>)) {
      // configurationValue must be defined and of the correct type to proceed.
      return new CheckResult(this.processFailIf(false));
    }

    try {
      ConfigurationValue<List<Rule>> wrappedConfigRules = (ConfigurationValue<List<Rule>>) configurationValue;
      List<Rule> configRules = wrappedConfigRules.getValue();
      Rule checkRule = this.getRuleValue();
      return new CheckResult(this.processFailIf(configRules != null && !configRules.isEmpty() &&
              checkRule.equals(configRules.get(0))),
              wrappedConfigRules.getConfigurationSource());
    } catch(ClassCastException ccEx) {
      logger.error("Value could not be cast to a rule list. Value=\"{}\"", configurationValue);
    }

    return CheckResult.failWithoutContext();
  }
}
