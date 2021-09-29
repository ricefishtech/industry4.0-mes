/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.customTranslation.internal.aop;

import java.util.Set;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.qcadoo.customTranslation.constants.CustomTranslationContants;
import com.qcadoo.localization.api.TranslationPropertiesHolder;
import com.qcadoo.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(CustomTranslationContants.PLUGIN_IDENTIFIER)
public class TranslationModuleOverrideAspect {

    @Autowired
    private TranslationModuleOverrideUtil translationModuleOverrideUtil;

    @Pointcut("execution(public void com.qcadoo.localization.internal.module.TranslationModule.multiTenantEnable(..)) "
            + "&& target(translationPropertiesHolder)")
    public void enableExecution(final TranslationPropertiesHolder translationPropertiesHolder) {
    }

    @AfterReturning("enableExecution(translationPropertiesHolder)")
    public void afterReturningEnableExecution(final TranslationPropertiesHolder translationPropertiesHolder) throws Throwable {
        if (translationModuleOverrideUtil.shouldOverride()) {
            String pluginIdentifier = translationPropertiesHolder.getPluginIdentifier();
            Set<String> basenames = translationPropertiesHolder.getParsedBasenames();

            translationModuleOverrideUtil.addTranslationKeysForPlugin(pluginIdentifier, basenames);
        }
    }

    @Pointcut("execution(public void com.qcadoo.localization.internal.module.TranslationModule.multiTenantDisable(..)) "
            + "&& this(translationPropertiesHolder)")
    public void disableExecution(final TranslationPropertiesHolder translationPropertiesHolder) {
    }

    @AfterReturning("disableExecution(translationPropertiesHolder)")
    public void afterReturningDisableExecution(final TranslationPropertiesHolder translationPropertiesHolder) throws Throwable {
        if (translationModuleOverrideUtil.shouldOverride()) {
            String pluginIdentifier = translationPropertiesHolder.getPluginIdentifier();

            translationModuleOverrideUtil.removeTranslationKeysForPlugin(pluginIdentifier);
        }
    }

}
