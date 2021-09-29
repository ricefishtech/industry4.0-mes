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

import static com.qcadoo.testing.model.EntityTestUtils.*;
import static junit.framework.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.internal.DefaultEntity;
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
import com.qcadoo.view.internal.security.SecurityViewDefinitionRoleResolver;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PluginUtils.class)
public class MenuServiceImplTest {

    private static final String PLUGIN_IDENTIFIER = "somePlugin";

    private static final String DISABLED_PLUGIN_IDENTIFIER = "someDisabledPlugin";

    private static final String ROLE_VISIBLE = "ROLE_VISIBLE";

    private static final String ROLE_INVISIBLE = "ROLE_INVISIBLE";

    private InternalMenuService menuService;

    @Mock
    private MenuCrudService menuCrudService;

    @Mock
    private SecurityRolesService securityRolesService;

    @Mock
    private SecurityViewDefinitionRoleResolver viewDefinitionRoleResolver;

    @Mock
    private TranslationUtilsService translationUtilsService;

    @Mock
    private ViewDefinitionService viewDefinitionService;

    @Captor
    private ArgumentCaptor<Entity> entityCaptor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(PluginUtils.class);

        menuService = new MenuServiceImpl();

        ReflectionTestUtils.setField(menuService, "menuCrudService", menuCrudService);
        ReflectionTestUtils.setField(menuService, "securityRolesService", securityRolesService);
        ReflectionTestUtils.setField(menuService, "viewDefinitionRoleResolver", viewDefinitionRoleResolver);
        ReflectionTestUtils.setField(menuService, "translationUtilsService", translationUtilsService);
        ReflectionTestUtils.setField(menuService, "viewDefinitionService", viewDefinitionService);

        stubSecurityRole(ROLE_VISIBLE, true);
        stubSecurityRole(ROLE_INVISIBLE, false);
        stubSecurityRole("ROLE_USER", true);

        stubPluginEnabled(PLUGIN_IDENTIFIER, true);
        stubPluginEnabled(DISABLED_PLUGIN_IDENTIFIER, false);
    }

    private void stubPluginEnabled(final String pluginIdentifier, final boolean isEnabled) {
        given(PluginUtils.isEnabled(pluginIdentifier)).willReturn(isEnabled);
    }

    @Test
    public final void shouldCreateItem() {
        // given
        MenuItemDefinition menuItemDefinition = MenuItemDefinition.create(PLUGIN_IDENTIFIER, "itemName", "categoryName",
                ROLE_VISIBLE, true).forView(PLUGIN_IDENTIFIER, "viewName");
        given(menuCrudService.getItem(menuItemDefinition)).willReturn(null);

        Entity categoryEntity = mockCategory(PLUGIN_IDENTIFIER, "categoryName", ROLE_VISIBLE, Collections.<Entity> emptyList());
        given(menuCrudService.getCategory(menuItemDefinition)).willReturn(categoryEntity);

        Entity viewEntity = mockViewEntity(PLUGIN_IDENTIFIER, "viewName");
        given(menuCrudService.getView(menuItemDefinition)).willReturn(viewEntity);

        given(menuCrudService.createEntity(QcadooViewConstants.MODEL_ITEM)).willAnswer(new Answer<Entity>() {

            @Override
            public Entity answer(final InvocationOnMock invocation) throws Throwable {
                DataDefinition dataDefinition = mock(DataDefinition.class);
                return new DefaultEntity(dataDefinition);
            }
        });

        // when
        menuService.createItem(menuItemDefinition);

        // then
        verify(menuCrudService).save(entityCaptor.capture());
        Entity savedItem = entityCaptor.getValue();
        assertEquals(PLUGIN_IDENTIFIER, savedItem.getStringField(MenuItemFields.PLUGIN_IDENTIFIER));
        assertEquals("itemName", savedItem.getStringField(MenuItemFields.NAME));
        assertTrue(savedItem.getBooleanField(MenuItemFields.ACTIVE));
        assertEquals(viewEntity, savedItem.getField(MenuItemFields.VIEW));
        assertEquals(categoryEntity, savedItem.getField(MenuItemFields.CATEGORY));
        assertEquals(1, savedItem.getField(MenuItemFields.SUCCESSION));
        assertEquals(ROLE_VISIBLE, savedItem.getStringField(MenuItemFields.AUTH_ROLE));
    }

    @Test
    public final void shouldCreateDeactivatedItem() {
        // given
        MenuItemDefinition menuItemDefinition = MenuItemDefinition.create(PLUGIN_IDENTIFIER, "itemName", "categoryName",
                ROLE_VISIBLE, false).forView(PLUGIN_IDENTIFIER, "viewName");
        given(menuCrudService.getItem(menuItemDefinition)).willReturn(null);

        Entity categoryEntity = mockCategory(PLUGIN_IDENTIFIER, "categoryName", ROLE_VISIBLE, Collections.<Entity> emptyList());
        given(menuCrudService.getCategory(menuItemDefinition)).willReturn(categoryEntity);

        Entity viewEntity = mockViewEntity(PLUGIN_IDENTIFIER, "viewName");
        given(menuCrudService.getView(menuItemDefinition)).willReturn(viewEntity);

        given(menuCrudService.createEntity(QcadooViewConstants.MODEL_ITEM)).willAnswer(new Answer<Entity>() {

            @Override
            public Entity answer(final InvocationOnMock invocation) throws Throwable {
                DataDefinition dataDefinition = mock(DataDefinition.class);
                return new DefaultEntity(dataDefinition);
            }
        });

        // when
        menuService.createItem(menuItemDefinition);

        // then
        verify(menuCrudService).save(entityCaptor.capture());
        Entity savedItem = entityCaptor.getValue();
        assertEquals(PLUGIN_IDENTIFIER, savedItem.getStringField(MenuItemFields.PLUGIN_IDENTIFIER));
        assertEquals("itemName", savedItem.getStringField(MenuItemFields.NAME));
        assertFalse(savedItem.getBooleanField(MenuItemFields.ACTIVE));
        assertEquals(viewEntity, savedItem.getField(MenuItemFields.VIEW));
        assertEquals(categoryEntity, savedItem.getField(MenuItemFields.CATEGORY));
        assertEquals(1, savedItem.getField(MenuItemFields.SUCCESSION));
        assertEquals(ROLE_VISIBLE, savedItem.getStringField(MenuItemFields.AUTH_ROLE));
    }

    @Test
    public final void shouldUpdateItemIfExists() {
        // given
        MenuItemDefinition menuItemDefinition = MenuItemDefinition.create(PLUGIN_IDENTIFIER, "itemName", "categoryName",
                ROLE_VISIBLE, true).forView(PLUGIN_IDENTIFIER, "viewName");

        Entity itemEntity = mock(Entity.class);
        given(menuCrudService.getItem(menuItemDefinition)).willReturn(itemEntity);

        Entity categoryEntity = mockCategory(PLUGIN_IDENTIFIER, "categoryName", ROLE_VISIBLE, Collections.<Entity> emptyList());
        given(menuCrudService.getCategory(menuItemDefinition)).willReturn(categoryEntity);

        Entity viewEntity = mockViewEntity(PLUGIN_IDENTIFIER, "viewName");
        given(menuCrudService.getView(menuItemDefinition)).willReturn(viewEntity);

        // when
        menuService.createItem(menuItemDefinition);

        // then
        verify(menuCrudService).save(entityCaptor.capture());
        Entity savedItem = entityCaptor.getValue();
        verify(savedItem).setField(MenuItemFields.VIEW, viewEntity);
        verify(savedItem).setField(MenuItemFields.CATEGORY, categoryEntity);
        verify(savedItem).setField(MenuItemFields.SUCCESSION, 1);
        verify(savedItem).setField(MenuItemFields.AUTH_ROLE, ROLE_VISIBLE);
    }

    @Test
    public final void shouldDoNothingIfItemExistsAndHasTheSameView() {
        // given
        MenuItemDefinition menuItemDefinition = MenuItemDefinition.create(PLUGIN_IDENTIFIER, "itemName", "categoryName",
                ROLE_VISIBLE, true).forView(PLUGIN_IDENTIFIER, "viewName");

        Entity itemEntity = mock(Entity.class);
        given(menuCrudService.getItem(menuItemDefinition)).willReturn(itemEntity);

        Entity categoryEntity = mockCategory(PLUGIN_IDENTIFIER, "categoryName", ROLE_VISIBLE, Collections.<Entity> emptyList());
        given(menuCrudService.getCategory(menuItemDefinition)).willReturn(categoryEntity);

        Entity viewEntity = mockViewEntity(PLUGIN_IDENTIFIER, "viewName");
        given(menuCrudService.getView(menuItemDefinition)).willReturn(viewEntity);

        stubBelongsToField(itemEntity, MenuItemFields.VIEW, viewEntity);

        // when
        menuService.createItem(menuItemDefinition);

        // then
        verify(menuCrudService, never()).save(any(Entity.class));
    }

    @Test
    public final void shouldCreateCategory() {
        // given
        MenuCategoryDefinition menuCategoryDefinition = new MenuCategoryDefinition(PLUGIN_IDENTIFIER, "categoryName",
                ROLE_VISIBLE);
        given(menuCrudService.getCategory(menuCategoryDefinition)).willReturn(null);

        given(menuCrudService.createEntity(QcadooViewConstants.MODEL_CATEGORY)).willAnswer(new Answer<Entity>() {

            @Override
            public Entity answer(final InvocationOnMock invocation) throws Throwable {
                DataDefinition dataDefinition = mock(DataDefinition.class);
                return new DefaultEntity(dataDefinition);
            }
        });

        given(menuCrudService.getTotalNumberOfCategories()).willReturn(3);

        // when
        menuService.createCategory(menuCategoryDefinition);

        // then
        verify(menuCrudService).save(entityCaptor.capture());
        Entity savedItem = entityCaptor.getValue();
        assertEquals(PLUGIN_IDENTIFIER, savedItem.getStringField(MenuCategoryFields.PLUGIN_IDENTIFIER));
        assertEquals("categoryName", savedItem.getStringField(MenuCategoryFields.NAME));
        assertEquals(3, savedItem.getField(MenuCategoryFields.SUCCESSION));
        assertEquals(ROLE_VISIBLE, savedItem.getStringField(MenuCategoryFields.AUTH_ROLE));
    }

    @Test
    public final void shouldDoNothingIfCategoryAlreadyExists() {
        // given
        MenuCategoryDefinition menuCategoryDefinition = new MenuCategoryDefinition(PLUGIN_IDENTIFIER, "categoryName",
                ROLE_VISIBLE);
        Entity categoryEntity = mock(Entity.class);
        given(menuCrudService.getCategory(menuCategoryDefinition)).willReturn(categoryEntity);

        // when
        menuService.createCategory(menuCategoryDefinition);

        // then
        verify(menuCrudService, never()).save(any(Entity.class));
    }

    @Test
    public final void shouldNotRemoveCategoryIfHasAnyItems() {
        // given
        MenuCategoryDefinition menuCategoryDefinition = new MenuCategoryDefinition(PLUGIN_IDENTIFIER, "categoryName",
                ROLE_VISIBLE);
        Entity itemEntity = mock(Entity.class);
        Entity categoryEntity = mockCategory(PLUGIN_IDENTIFIER, "categoryName", ROLE_VISIBLE, Lists.newArrayList(itemEntity));
        given(menuCrudService.getCategory(menuCategoryDefinition)).willReturn(categoryEntity);

        // when
        menuService.removeCategory(menuCategoryDefinition);

        // then
        verify(menuCrudService, never()).delete(categoryEntity);
    }

    @Test
    public final void shouldRemoveCategory() {
        // given
        MenuCategoryDefinition menuCategoryDefinition = new MenuCategoryDefinition(PLUGIN_IDENTIFIER, "categoryName",
                ROLE_VISIBLE);
        Entity categoryEntity = mockCategory(PLUGIN_IDENTIFIER, "categoryName", ROLE_VISIBLE, Collections.<Entity> emptyList());
        given(menuCrudService.getCategory(menuCategoryDefinition)).willReturn(categoryEntity);

        // when
        menuService.removeCategory(menuCategoryDefinition);

        // then
        verify(menuCrudService).delete(categoryEntity);
    }

    @Test
    public final void shouldCreateViewForViewItem() {
        // given
        MenuItemDefinition menuViewItemDefinition = MenuItemDefinition.create(PLUGIN_IDENTIFIER, "itemName", "categoryName",
                ROLE_VISIBLE, true).forView("viewPlugin", "viewName");
        given(menuCrudService.getView(menuViewItemDefinition)).willReturn(null);
        given(viewDefinitionService.viewExists("viewPlugin", "viewName")).willReturn(true);
        given(menuCrudService.createEntity(QcadooViewConstants.MODEL_VIEW)).willAnswer(new Answer<Entity>() {

            @Override
            public Entity answer(final InvocationOnMock invocation) throws Throwable {
                DataDefinition dataDefinition = mock(DataDefinition.class);
                return new DefaultEntity(dataDefinition);
            }
        });

        // when
        menuService.addView(menuViewItemDefinition);

        // then
        verify(menuCrudService).save(entityCaptor.capture());
        Entity savedView = entityCaptor.getValue();
        assertEquals("viewPlugin", savedView.getStringField(ViewFields.PLUGIN_IDENTIFIER));
        assertEquals("viewName", savedView.getStringField(ViewFields.NAME));
        assertEquals("viewName", savedView.getStringField(ViewFields.VIEW));
        assertNull(savedView.getStringField(ViewFields.URL));
    }

    @Test
    public final void shouldCreateViewForUrlItem() {
        // given
        MenuItemDefinition menuUrlItemDefinition = MenuItemDefinition.create(PLUGIN_IDENTIFIER, "itemName", "categoryName",
                ROLE_VISIBLE, true).forUrl("someUrl");
        given(menuCrudService.getView(menuUrlItemDefinition)).willReturn(null);
        given(menuCrudService.createEntity(QcadooViewConstants.MODEL_VIEW)).willAnswer(new Answer<Entity>() {

            @Override
            public Entity answer(final InvocationOnMock invocation) throws Throwable {
                DataDefinition dataDefinition = mock(DataDefinition.class);
                return new DefaultEntity(dataDefinition);
            }
        });

        // when
        menuService.addView(menuUrlItemDefinition);

        // then
        verify(menuCrudService).save(entityCaptor.capture());
        Entity savedView = entityCaptor.getValue();
        assertEquals("somePlugin", savedView.getStringField(ViewFields.PLUGIN_IDENTIFIER));
        assertEquals("itemName", savedView.getStringField(ViewFields.NAME));
        assertNull(savedView.getStringField(ViewFields.VIEW));
        assertEquals("someUrl", savedView.getStringField(ViewFields.URL));
    }

    @Test
    public final void shouldAddViewThrowsAnExceptionIfViewDoesNotExistAndUrlIsNull() {
        // given
        MenuItemDefinition menuViewItemDefinition = MenuItemDefinition.create(PLUGIN_IDENTIFIER, "itemName", "categoryName",
                ROLE_VISIBLE, true).forView("viewPlugin", "viewName");
        given(menuCrudService.getView(menuViewItemDefinition)).willReturn(null);
        given(viewDefinitionService.viewExists(PLUGIN_IDENTIFIER, "viewName")).willReturn(false);

        // when & then
        try {
            menuService.addView(menuViewItemDefinition);
            Assert.fail();
        } catch (IllegalStateException ignored) {
            // SUccess
        } catch (Exception ignored) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldReturnEmptyMenu() {
        // when
        MenuDefinition menuDefinition = menuService.getMenu(Locale.ENGLISH);

        // then
        Assert.assertNull(menuDefinition.getAdministrationCategory());
        Assert.assertNull(menuDefinition.getHomeCategory());
        assertEquals(0, menuDefinition.getItems().size());
    }

    @Test
    public final void shouldReturnMenu() {
        // given
        Entity homeItem = mockItem(PLUGIN_IDENTIFIER, "homeItem", true, null, PLUGIN_IDENTIFIER, "homeView");
        Iterable<Entity> homeItems = Lists.newArrayList(homeItem);
        Entity homeCategory = mockCategory(PLUGIN_IDENTIFIER, "home", null, homeItems);

        Entity administrationItem = mockItem(PLUGIN_IDENTIFIER, "administrationItem", true, null, PLUGIN_IDENTIFIER,
                "administrationView");
        Iterable<Entity> administrationItems = Lists.newArrayList(administrationItem);
        Entity administrationCategory = mockCategory(PLUGIN_IDENTIFIER, "administration", null, administrationItems);

        Entity cat1item1 = mockItem(PLUGIN_IDENTIFIER, "cat1item1Name", true, ROLE_VISIBLE, PLUGIN_IDENTIFIER, "view1");
        Entity cat1item2 = mockItem(PLUGIN_IDENTIFIER, "cat1item2Name", false, ROLE_VISIBLE, PLUGIN_IDENTIFIER, "view2");
        Entity cat1item3 = mockItem(PLUGIN_IDENTIFIER, "cat1item3Name", true, ROLE_INVISIBLE, PLUGIN_IDENTIFIER, "view3");
        Entity cat1item4 = mockItem(PLUGIN_IDENTIFIER, "cat1item4Name", false, ROLE_INVISIBLE, PLUGIN_IDENTIFIER, "view4");
        Entity cat1item5 = mockItem(PLUGIN_IDENTIFIER, "cat1item5Name", false, ROLE_VISIBLE, DISABLED_PLUGIN_IDENTIFIER, "view5");
        Entity cat1item6 = mockItem(PLUGIN_IDENTIFIER, "cat1item6Name", false, ROLE_VISIBLE, PLUGIN_IDENTIFIER, "view6");
        stubRoleForView(PLUGIN_IDENTIFIER, "view6", false);
        List<Entity> cat1items = Lists.newArrayList(cat1item1, cat1item2, cat1item3, cat1item4, cat1item5, cat1item6);

        Entity cat1 = mockCategory(PLUGIN_IDENTIFIER, "cat1", ROLE_VISIBLE, cat1items);

        Entity cat2item1 = mockItem(PLUGIN_IDENTIFIER, "cat2item1Name", true, ROLE_VISIBLE, PLUGIN_IDENTIFIER, "view7");
        Entity cat2item2 = mockItem(PLUGIN_IDENTIFIER, "cat2item2Name", true, ROLE_VISIBLE, PLUGIN_IDENTIFIER, "view8");
        List<Entity> cat2items = Lists.newArrayList(cat2item1, cat2item2);

        Entity cat2 = mockCategory(PLUGIN_IDENTIFIER, "cat2", ROLE_INVISIBLE, cat2items);

        Entity cat3item1 = mockItem(PLUGIN_IDENTIFIER, "cat3item1Name", true, ROLE_VISIBLE, DISABLED_PLUGIN_IDENTIFIER, "view9");
        List<Entity> cat3items = Lists.newArrayList(cat3item1);
        Entity cat3 = mockCategory(DISABLED_PLUGIN_IDENTIFIER, "cat3", ROLE_VISIBLE, cat3items);

        stubGetSortedMenuCategories(Lists.newArrayList(homeCategory, cat1, cat2, cat3, administrationCategory));
        stubGetSortedMenuCategoryItems(homeCategory, homeItems);
        stubGetSortedMenuCategoryItems(cat1, cat1items);
        stubGetSortedMenuCategoryItems(cat2, cat2items);
        stubGetSortedMenuCategoryItems(cat3, cat3items);
        stubGetSortedMenuCategoryItems(administrationCategory, administrationItems);

        // when
        MenuDefinition menuDefinition = menuService.getMenu(Locale.ENGLISH);

        // then
        assertCategory(menuDefinition.getHomeCategory(), "home", Lists.newArrayList("homeItem"));
        Assert.assertEquals(1, menuDefinition.getItems().size());
        assertCategory(menuDefinition.getItems().get(0), "cat1", Lists.newArrayList("cat1item1Name"));
        assertCategory(menuDefinition.getAdministrationCategory(), "administration", Lists.newArrayList("administrationItem"));
    }

    private void assertCategory(final MenuItemsGroup menuGroup, final String expectedName, final List<String> expectedItemNames) {
        assertEquals(expectedName, menuGroup.getName());
        List<MenuItem> menuItems = menuGroup.getItems();
        assertEquals(expectedItemNames.size(), menuItems.size());
        List<String> itemNames = Lists.newArrayList();
        for (MenuItem item : menuItems) {
            itemNames.add(item.getName());
        }
        assertEquals(expectedItemNames, itemNames);
    }

    private Entity mockCategory(final String pluginIdentifier, final String name, final String roleIdentifier,
            final Iterable<Entity> items) {
        Entity category = mock(Entity.class);
        stubStringField(category, MenuCategoryFields.PLUGIN_IDENTIFIER, pluginIdentifier);
        stubStringField(category, MenuCategoryFields.NAME, name);
        stubStringField(category, MenuCategoryFields.AUTH_ROLE, roleIdentifier);
        stubHasManyField(category, MenuCategoryFields.ITEMS, items);
        return category;
    }

    private Entity mockItem(final String pluginIdentifier, final String name, final boolean active, final String roleIdentifier,
            final String viewPlugin, final String viewName) {
        Entity item = mock(Entity.class);
        stubStringField(item, MenuItemFields.PLUGIN_IDENTIFIER, pluginIdentifier);
        stubStringField(item, MenuItemFields.NAME, name);
        stubBooleanField(item, MenuItemFields.ACTIVE, active);
        stubStringField(item, MenuItemFields.AUTH_ROLE, roleIdentifier);
        Entity view = mockViewEntity(viewPlugin, viewName);
        stubBelongsToField(item, MenuItemFields.VIEW, view);
        return item;
    }

    private Entity mockViewEntity(final String pluginIdentifier, final String name) {
        Entity view = mock(Entity.class);
        stubStringField(view, ViewFields.PLUGIN_IDENTIFIER, pluginIdentifier);
        stubStringField(view, ViewFields.NAME, name);
        stubRoleForView(pluginIdentifier, name, true);
        return view;
    }

    private void stubRoleForView(final String pluginIdentifier, final String viewName, final boolean canAccess) {
        SecurityRole role = mock(SecurityRole.class);
        given(viewDefinitionRoleResolver.getRoleForView(pluginIdentifier, viewName)).willReturn(role);
        given(securityRolesService.canAccess(role)).willReturn(canAccess);
    }

    private void stubGetSortedMenuCategories(final Iterable<Entity> categories) {
        given(menuCrudService.getSortedMenuCategories()).willAnswer(new Answer<List<Entity>>() {

            @Override
            public List<Entity> answer(final InvocationOnMock invocation) throws Throwable {
                return Lists.newArrayList(categories);
            }
        });
    }

    private void stubGetSortedMenuCategoryItems(final Entity category, final Iterable<Entity> items) {
        given(menuCrudService.getSortedMenuCategoryItems(category)).willAnswer(new Answer<List<Entity>>() {

            @Override
            public List<Entity> answer(final InvocationOnMock invocation) throws Throwable {
                return Lists.newArrayList(items);
            }
        });
    }

    private void stubSecurityRole(final String roleIdentifier, final boolean canAccess) {
        SecurityRole role = mock(SecurityRole.class);
        given(securityRolesService.getRoleByIdentifier(roleIdentifier)).willReturn(role);
        given(securityRolesService.canAccess(role)).willReturn(canAccess);
        given(securityRolesService.canAccess(roleIdentifier)).willReturn(canAccess);
    }

}
