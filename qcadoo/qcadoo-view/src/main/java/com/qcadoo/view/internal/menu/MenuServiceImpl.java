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
package com.qcadoo.view.internal.menu;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.aop.Monitorable;
import com.qcadoo.plugin.api.PluginUtils;
import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.utils.TranslationUtilsService;
import com.qcadoo.view.constants.MenuCategoryFields;
import com.qcadoo.view.constants.MenuItemFields;
import com.qcadoo.view.constants.QcadooViewConstants;
import com.qcadoo.view.constants.ViewFields;
import com.qcadoo.view.internal.api.InternalMenuService;
import com.qcadoo.view.internal.api.ViewDefinitionService;
import com.qcadoo.view.internal.menu.definitions.MenuCategoryDefinition;
import com.qcadoo.view.internal.menu.definitions.MenuItemDefinition;
import com.qcadoo.view.internal.menu.items.UrlMenuItem;
import com.qcadoo.view.internal.menu.items.ViewDefinitionMenuItemItem;
import com.qcadoo.view.internal.security.SecurityViewDefinitionRoleResolver;

@Service
public final class MenuServiceImpl implements InternalMenuService {

    private static final String ADMINISTRATION_CATEGORY = "administration";

    private static final String HOME_CATEGORY = "home";

    @Autowired
    private MenuCrudService menuCrudService;

    @Autowired
    private SecurityRolesService securityRolesService;

    @Autowired
    private SecurityViewDefinitionRoleResolver viewDefinitionRoleResolver;

    @Autowired
    private TranslationUtilsService translationUtilsService;

    @Autowired
    private ViewDefinitionService viewDefinitionService;

    @Override
    @Transactional(readOnly = true)
    @Monitorable
    public MenuDefinition getMenu(final Locale locale) {
        MenuDefinition menuDefinition = new MenuDefinition();

        List<Entity> menuCategories = menuCrudService.getSortedMenuCategories();
        for (Entity menuCategory : menuCategories) {
            String authRoleIdentifier = menuCategory.getStringField(MenuCategoryFields.AUTH_ROLE);
            if (!currentUserHasPrivilegesOf(authRoleIdentifier)) {
                continue;
            }

            MenuItemsGroup category = buildCategoryWithItems(menuCategory, locale);
            if (ADMINISTRATION_CATEGORY.equals(category.getName())) {
                menuDefinition.setAdministrationCategory(category);
            } else if (HOME_CATEGORY.equals(category.getName())) {
                menuDefinition.setHomeCategory(category);
            } else if (!category.getItems().isEmpty()) {
                menuDefinition.addItem(category);
            }
        }

        return menuDefinition;
    }

    private MenuItemsGroup buildCategoryWithItems(final Entity menuCategory, final Locale locale) {
        MenuItemsGroup category = buildMenuCategory(menuCategory, locale);
        Iterable<Entity> menuItems = menuCrudService.getSortedMenuCategoryItems(menuCategory);
        for (Entity menuItem : menuItems) {
            if (!menuItem.getBooleanField(MenuItemFields.ACTIVE)
                    || !currentUserHasPrivilegesOf(menuItem.getStringField(MenuItemFields.AUTH_ROLE))) {
                continue;
            }
            Entity menuView = menuItem.getBelongsToField(MenuItemFields.VIEW);
            if (canAccessView(menuView.getStringField(ViewFields.PLUGIN_IDENTIFIER), menuView.getStringField(ViewFields.NAME))) {
                category.addItem(buildMenuItem(menuItem, locale, menuView));
            }
        }
        return category;
    }

    private MenuItemsGroup buildMenuCategory(final Entity menuCategory, final Locale locale) {
        String categoryName = menuCategory.getStringField(MenuCategoryFields.NAME);
        String categoryDescription = translationUtilsService.getCategoryDescriptionTranslation(menuCategory, locale);
        String categoryLabel = categoryName;
        if (menuCategory.getStringField(MenuCategoryFields.PLUGIN_IDENTIFIER) != null) {
            categoryLabel = translationUtilsService.getCategoryTranslation(menuCategory, locale);
        }

        return new MenuItemsGroup(categoryName, categoryLabel, categoryDescription);
    }

    private SecurityRole getAuthorizationRole(final String roleIdentifierOrNull) {
        String roleIdentifierOrDefault = (String) ObjectUtils.defaultIfNull(roleIdentifierOrNull, "ROLE_USER");
        SecurityRole role = securityRolesService.getRoleByIdentifier(roleIdentifierOrDefault);
        Preconditions.checkState(role != null, String.format("No such role: '%s'", roleIdentifierOrDefault));
        return role;
    }

    private String getItemLabel(final Entity menuItem, final Locale locale) {
        String itemLabel = menuItem.getStringField(MenuItemFields.NAME);
        if (menuItem.getStringField(MenuItemFields.PLUGIN_IDENTIFIER) != null) {
            itemLabel = translationUtilsService.getItemTranslation(menuItem, locale);
        }
        return itemLabel;
    }

    private MenuItem buildMenuItem(final Entity menuItem, final Locale locale, final Entity menuView) {
        String itemDescription = translationUtilsService.getItemDescriptionTranslation(menuItem, locale);
        String itemLabel = getItemLabel(menuItem, locale);
        String itemName = menuItem.getStringField(MenuItemFields.NAME);
        if (menuView.getStringField(ViewFields.URL) == null) {
            String viewPlugin = menuView.getStringField(ViewFields.PLUGIN_IDENTIFIER);
            String viewName = menuView.getStringField(ViewFields.NAME);
            return new ViewDefinitionMenuItemItem(itemName, itemLabel, itemDescription, viewPlugin, viewName);
        } else {
            String viewUrl = menuView.getStringField(ViewFields.URL);
            return new UrlMenuItem(itemName, itemLabel, itemDescription, null, viewUrl);
        }
    }

    private boolean canAccessView(final String pluginIdentifier, final String viewName) {
        if (!PluginUtils.isEnabled(pluginIdentifier)) {
            return false;
        }
        SecurityRole viewRole = viewDefinitionRoleResolver.getRoleForView(pluginIdentifier, viewName);
        return securityRolesService.canAccess(viewRole);
    }

    private boolean currentUserHasPrivilegesOf(final String roleIdentifier) {
        SecurityRole authRole = getAuthorizationRole(roleIdentifier);
        return securityRolesService.canAccess(authRole);
    }

    @Override
    @Transactional
    public void addView(final MenuItemDefinition itemDefinition) {
        Entity menuView = menuCrudService.getView(itemDefinition);

        if (menuView != null) {
            return;
        }

        boolean hasUrlOrViewExists = itemDefinition.getUrl() != null
                || viewDefinitionService.viewExists(itemDefinition.getViewPluginIdentifier(), itemDefinition.getViewName());

        Preconditions.checkState(hasUrlOrViewExists, String.format("View %s/%s does not exist",
                itemDefinition.getViewPluginIdentifier(), itemDefinition.getViewName()));

        menuView = menuCrudService.createEntity(QcadooViewConstants.MODEL_VIEW);
        menuView.setField(ViewFields.PLUGIN_IDENTIFIER, itemDefinition.getViewPluginIdentifier());
        menuView.setField(ViewFields.NAME, itemDefinition.getViewName());
        if (itemDefinition.getUrl() == null) {
            menuView.setField(ViewFields.VIEW, itemDefinition.getViewName());
        } else {
            menuView.setField(ViewFields.URL, itemDefinition.getUrl());
        }
        menuCrudService.save(menuView);
    }

    @Override
    @Transactional
    public void removeView(final MenuItemDefinition itemDefinition) {
        Entity menuView = menuCrudService.getView(itemDefinition);
        if (menuView == null) {
            return;
        }
        menuCrudService.delete(menuView);
    }

    @Override
    @Transactional
    public void createCategory(final MenuCategoryDefinition menuCategoryDefinition) {
        Entity menuCategory = menuCrudService.getCategory(menuCategoryDefinition);
        if (menuCategory != null) {
            return;
        }

        menuCategory = menuCrudService.createEntity(QcadooViewConstants.MODEL_CATEGORY);
        menuCategory.setField(MenuCategoryFields.PLUGIN_IDENTIFIER, menuCategoryDefinition.getPluginIdentifier());
        menuCategory.setField(MenuCategoryFields.NAME, menuCategoryDefinition.getName());
        menuCategory.setField("accessible", true);
        menuCategory.setField(MenuCategoryFields.SUCCESSION, menuCrudService.getTotalNumberOfCategories());
        menuCategory.setField(MenuCategoryFields.AUTH_ROLE, menuCategoryDefinition.getAuthRole());
        menuCrudService.save(menuCategory);
    }

    @Override
    @Transactional
    public void removeCategory(final MenuCategoryDefinition menuCategoryDefinition) {
        Entity menuCategory = menuCrudService.getCategory(menuCategoryDefinition);
        if (menuCategory == null) {
            return;
        }
        if (menuCategory.getHasManyField(MenuCategoryFields.ITEMS).size() == 0) {
            menuCrudService.delete(menuCategory);
        }
    }

    @Override
    @Transactional
    public void createItem(final MenuItemDefinition itemDefinition) {
        Entity menuItem = menuCrudService.getItem(itemDefinition);

        Entity menuCategory = menuCrudService.getCategory(itemDefinition);
        Entity menuView = menuCrudService.getView(itemDefinition);

        Preconditions.checkState(
                menuCategory != null,
                String.format("Cannot find menu category %s for item %s.%s", itemDefinition.getCategoryName(),
                        itemDefinition.getPluginIdentifier(), itemDefinition.getName()));

        Preconditions.checkState(menuView != null, String.format("Cannot find menu view %s.%s for item %s.%s",
                itemDefinition.getViewPluginIdentifier(), itemDefinition.getViewName(), itemDefinition.getPluginIdentifier(),
                itemDefinition.getName()));

        if (menuItem == null) {
            menuItem = menuCrudService.createEntity(QcadooViewConstants.MODEL_ITEM);
            menuItem.setField(MenuItemFields.PLUGIN_IDENTIFIER, itemDefinition.getPluginIdentifier());
            menuItem.setField(MenuItemFields.NAME, itemDefinition.getName());
            menuItem.setField(MenuItemFields.ACTIVE, itemDefinition.isActive());
        }

        if (menuItem == null || !menuView.equals(menuItem.getField(MenuItemFields.VIEW))) {
            menuItem.setField(MenuItemFields.VIEW, menuView);
            menuItem.setField(MenuItemFields.CATEGORY, menuCategory);
            menuItem.setField(MenuItemFields.SUCCESSION, menuCategory.getHasManyField(MenuCategoryFields.ITEMS).size() + 1);
            menuItem.setField(MenuItemFields.AUTH_ROLE, itemDefinition.getAuthRoleIdentifier());
            menuCrudService.save(menuItem);
        }
    }

    @Override
    @Transactional
    public void removeItem(final MenuItemDefinition itemDefinition) {
        Entity menuItem = menuCrudService.getItem(itemDefinition);
        if (menuItem == null) {
            return;
        }
        menuCrudService.delete(menuItem);
    }

}
