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
package com.qcadoo.view.internal;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ContextualHelpPropertyConfigurer extends PropertyPlaceholderConfigurer {

    private Map<String, String> urlsByComponentPathMap;

    public ContextualHelpPropertyConfigurer() {
        super();
        urlsByComponentPathMap = new HashMap<String, String>();
        setIgnoreResourceNotFound(true);
        setIgnoreUnresolvablePlaceholders(true);
        setLocation(new ClassPathResource("help.properties"));
    }

    @Override
    protected void processProperties(final ConfigurableListableBeanFactory beanFactory, final Properties props) {
        super.processProperties(beanFactory, props);
        for (Object key : props.keySet()) {
            String componentPath = key.toString();
            urlsByComponentPathMap.put(componentPath, resolvePlaceholder(componentPath, props));
        }
    }

    public String getProperty(final String code, final Locale locale) {
        return urlsByComponentPathMap.get(code + '_' + locale.getLanguage());
    }
}
