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
package com.qcadoo.customTranslation.internal;

import static com.qcadoo.customTranslation.constants.CustomTranslationFields.ACTIVE;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.KEY;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.LOCALE;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.PLUGIN_IDENTIFIER;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.PROPERTIES_TRANSLATION;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.MethodUtils;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qcadoo.customTranslation.api.CustomTranslationManagementService;
import com.qcadoo.customTranslation.constants.CustomTranslationContants;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchRestrictions;

@Service
public class CustomTranslationManagementServiceImpl implements CustomTranslationManagementService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void addCustomTranslations(final String pluginIdentifier, final String locale, final Map<String, String> translations) {
        DataDefinition customTranslationDD = getCustomTranslationDD();
        Session currentSession = getCurrentSession(customTranslationDD);

        List<String> existingKeys = currentSession
                .createQuery(
                        "SELECT key FROM com.qcadoo.model.beans.qcadooCustomTranslation.QcadooCustomTranslationCustomTranslation "
                                + "WHERE pluginIdentifier = :pluginIdentifier AND locale = :locale")
                .setString("pluginIdentifier", pluginIdentifier).setString("locale", locale).list();

        for (Entry<String, String> translation : translations.entrySet()) {
            String key = translation.getKey();
            String value = translation.getValue();

            if (existingKeys.contains(key)) {
                continue;
            }

            Object entity = getInstanceForEntity(customTranslationDD);

            FieldUtils.setProtectedFieldValue(PLUGIN_IDENTIFIER, entity, pluginIdentifier);
            FieldUtils.setProtectedFieldValue(KEY, entity, key);
            FieldUtils.setProtectedFieldValue(PROPERTIES_TRANSLATION, entity, value);
            FieldUtils.setProtectedFieldValue(LOCALE, entity, locale);
            FieldUtils.setProtectedFieldValue(ACTIVE, entity, false);

            currentSession.save(entity);
        }
    }

    @Override
    @Transactional
    public void removeCustomTranslations(final String pluginIdentifier) {
        DataDefinition customTranslationDD = getCustomTranslationDD();
        Session currentSession = getCurrentSession(customTranslationDD);

        currentSession
                .createQuery(
                        "UPDATE com.qcadoo.model.beans.qcadooCustomTranslation.QcadooCustomTranslationCustomTranslation "
                                + "SET active = false WHERE pluginIdentifier = :pluginIdentifier AND active = true")
                .setString("pluginIdentifier", pluginIdentifier).executeUpdate();
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<String> getCustomTranslationKeys(final String pluginIdentifier) {
        DataDefinition customTranslationDD = getCustomTranslationDD();
        Session currentSession = getCurrentSession(customTranslationDD);

        return currentSession
                .createQuery(
                        "SELECT key FROM com.qcadoo.model.beans.qcadooCustomTranslation.QcadooCustomTranslationCustomTranslation "
                                + "WHERE pluginIdentifier = :pluginIdentifier").setString("pluginIdentifier", pluginIdentifier)
                .list();
    }

    @Override
    public Entity getCustomTranslation(final String pluginIdentifier, final String locale, final String key) {
        return getCustomTranslationDD().find().add(SearchRestrictions.eq(PLUGIN_IDENTIFIER, pluginIdentifier))
                .add(SearchRestrictions.eq(LOCALE, locale)).add(SearchRestrictions.eq(KEY, key)).setMaxResults(1).uniqueResult();
    }

    @Override
    public List<Entity> getCustomTranslations(final String locale) {
        return getCustomTranslationDD().find().add(SearchRestrictions.eq(LOCALE, locale)).list().getEntities();
    }

    @Override
    public List<Entity> getCustomTranslations() {
        return getCustomTranslationDD().find().list().getEntities();
    }

    @Override
    public DataDefinition getCustomTranslationDD() {
        return dataDefinitionService.get(CustomTranslationContants.PLUGIN_IDENTIFIER,
                CustomTranslationContants.MODEL_CUSTOM_TRANSLATION);
    }

    private Session getCurrentSession(final DataDefinition dataDefinition) {
        Object dataAccessService = FieldUtils.getProtectedFieldValue("dataAccessService", dataDefinition);
        Object hibernateService = FieldUtils.getProtectedFieldValue("hibernateService", dataAccessService);

        try {
            return (Session) MethodUtils.invokeExactMethod(hibernateService, "getCurrentSession", new Object[0]);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Object getInstanceForEntity(final DataDefinition dataDefinition) {
        try {
            return MethodUtils.invokeExactMethod(dataDefinition, "getInstanceForEntity", new Object[0]);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}