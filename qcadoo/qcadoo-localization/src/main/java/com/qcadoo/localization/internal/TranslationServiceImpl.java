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
package com.qcadoo.localization.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public final class TranslationServiceImpl implements InternalTranslationService {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationServiceImpl.class);

    private static final Logger TRANSLATION_LOG = LoggerFactory.getLogger("TRANSLATION");

    private static final Map<String, Set<String>> GROUP_MESSAGES = new HashMap<>();

    @Value("${ignoreMissingTranslations}")
    private boolean ignoreMissingTranslations;

    @Value("${maxUploadSize:5242880}")
    private int maxUploadSize;

    private long lastHotDeployClearCache;

    private final Map<String, String> locales = new HashMap<>();

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TranslationModuleService translationModuleService;

    @Autowired
    private ConfigUtil configUtil;
    
    @Override
    public String translate(final String code, final Locale locale, final String... args) {
        String message = translateWithError(code, locale, args);

        if (message != null) {
            return message.trim();
        }

        TRANSLATION_LOG.warn("Missing translation " + code + " for locale " + locale);

        if (ignoreMissingTranslations) {
            return DEFAULT_MISSING_MESSAGE;
        } else {
            return code;
        }
    }

    private String translate(final List<String> messageCodes, final Locale locale, final String... args) {
        for (String messageCode : messageCodes) {
            String message = translateWithError(messageCode, locale, args);
            if (message != null) {
                return message.trim();
            }
        }

        TRANSLATION_LOG.warn("Missing translation " + messageCodes + " for locale " + locale);

        if (ignoreMissingTranslations) {
            return DEFAULT_MISSING_MESSAGE;
        } else {
            return messageCodes.toString();
        }
    }

    @Override
    public String translate(final String code, final String secondCode, final Locale locale, final String... args) {
        return translate(Lists.newArrayList(code, secondCode), locale, args);
    }

    @Override
    public String translate(final String code, final String secondCode, final String thirdCode, final Locale locale,
            final String... args) {
        return translate(Lists.newArrayList(code, secondCode, thirdCode), locale, args);
    }

    private String translateWithError(final String messageCode, final Locale locale, final String[] args) {
        if (configUtil.isHotDeploy() && this.messageSource instanceof ReloadableResourceBundleMessageSource) {
            ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = (ReloadableResourceBundleMessageSource) this.messageSource;

            if ((System.currentTimeMillis() - lastHotDeployClearCache) >= 2500) {
                reloadableResourceBundleMessageSource.clearCache();
                lastHotDeployClearCache = System.currentTimeMillis();
            }

            return reloadableResourceBundleMessageSource.getMessage(messageCode, args, null, locale);

        } else {
            return messageSource.getMessage(messageCode, args, null, locale);
        }
    }

    @Override
    public Map<String, String> getMessagesGroup(final String group, final Locale locale) {
        if (!GROUP_MESSAGES.containsKey(group)) {
            return Collections.emptyMap();
        }

        Map<String, String> commonsTranslations = new HashMap<>();

        for (String commonMessage : GROUP_MESSAGES.get(group)) {
            commonsTranslations.put(commonMessage, translate(commonMessage, locale));
        }

        return commonsTranslations;
    }

    @Override
    public void prepareMessagesGroup(final String group, final String prefix) {
        Set<String> messages = new HashSet<>();
        GROUP_MESSAGES.put(group, messages);
        getMessagesByPrefix(prefix, messages);
    }

    private void getMessagesByPrefix(final String prefix, final Set<String> messages) {
        try {
            for (Resource resource : translationModuleService.getLocalizationResources()) {
                getMessagesByPrefix(prefix, messages, resource.getInputStream());
            }
        } catch (IOException e) {
            LOG.error("Can not read messages file", e);
        }
        LOG.info("Messages for " + prefix + ": " + messages);
    }

    private void getMessagesByPrefix(final String prefix, final Set<String> messages, final InputStream inputStream)
            throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        for (Object property : properties.keySet()) {
            if (String.valueOf(property).startsWith(prefix)) {
                messages.add((String) property);
            }
        }
    }

    @Override
    public void addLocaleToList(final String locale, final String label) {
        locales.put(locale, label);
    }

    @Override
    public void removeLocaleToList(final String locale) {
        locales.remove(locale);
    }

    @Override
    public Map<String, String> getLocales() {
        return locales;
    }

    @Override
    public int getMaxUploadSize() {
        return maxUploadSize/1048576;
    }
}
