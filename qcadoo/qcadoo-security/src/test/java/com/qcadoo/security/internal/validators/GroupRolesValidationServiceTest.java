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
package com.qcadoo.security.internal.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.security.api.SecurityService;
import com.qcadoo.security.constants.GroupFields;
import com.qcadoo.security.constants.QcadooSecurityConstants;
import com.qcadoo.security.constants.RoleFields;
import com.qcadoo.security.constants.UserFields;

public class GroupRolesValidationServiceTest {

    private GroupRolesValidationService groupRolesValidationService;

    @Mock
    private SecurityService securityService;

    @Mock
    private DataDefinitionService dataDefinitionService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Entity userGroupMock, existingUserGroupMock, currentUserEntityMock;

    @Mock
    private FieldDefinition groupRolesFieldDefMock;

    @Mock
    private DataDefinition userDataDefMock;

    @Mock
    private DataDefinition groupDataDefMock;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);

        given(securityService.getCurrentUserId()).willReturn(1L);

        SecurityContextHolder.setContext(securityContext);

        given(userDataDefMock.get(1L)).willReturn(currentUserEntityMock);

        given(groupDataDefMock.getField(GroupFields.ROLES)).willReturn(groupRolesFieldDefMock);
        given(userGroupMock.getId()).willReturn(1000L);
        given(groupDataDefMock.get(1000L)).willReturn(existingUserGroupMock);

        given(dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_USER))
                .willReturn(userDataDefMock);

        groupRolesValidationService = new GroupRolesValidationService();
        ReflectionTestUtils.setField(groupRolesValidationService, "securityService", securityService);
        ReflectionTestUtils.setField(groupRolesValidationService, "dataDefinitionService", dataDefinitionService);
    }

    private void stubGroupRoles(Entity group, String... roles) {

        List<Entity> rolesEntity = Lists.newArrayList();
        for (String role : roles) {
            Entity roleEntity = mock(Entity.class);
            given(roleEntity.getStringField(RoleFields.IDENTIFIER)).willReturn(role);
            rolesEntity.add(roleEntity);
        }

        given(group.getManyToManyField(GroupFields.ROLES)).willReturn(rolesEntity);
    }

    private void stubCurrentUserRole(final String role) {
        Entity roleEntity = mock(Entity.class);
        given(roleEntity.getStringField(RoleFields.IDENTIFIER)).willReturn(role);
        given(currentUserEntityMock.getManyToManyField(UserFields.GROUP)).willReturn(Lists.newArrayList(roleEntity));
        given(securityService.hasRole(currentUserEntityMock, QcadooSecurityConstants.ROLE_SUPERADMIN)).willReturn(
                QcadooSecurityConstants.ROLE_SUPERADMIN.equals(role));
    }

    private void stubSecurityContextWithAuthentication() {
        final Authentication authentication = mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
    }

    @Test
    public final void shouldMarkAddingRoleSuperadminAsInvalidWhenPerformedByNonSuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(QcadooSecurityConstants.ROLE_USER);
        stubGroupRoles(existingUserGroupMock, QcadooSecurityConstants.ROLE_ADMIN);
        stubGroupRoles(userGroupMock, QcadooSecurityConstants.ROLE_ADMIN, QcadooSecurityConstants.ROLE_SUPERADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertFalse(isValid);
        verify(userGroupMock).addError(Mockito.eq(groupRolesFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldMarkRemovingRoleSuperadminAsInvalidWhenPerformedByNonSuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(QcadooSecurityConstants.ROLE_USER);
        stubGroupRoles(existingUserGroupMock, QcadooSecurityConstants.ROLE_ADMIN, QcadooSecurityConstants.ROLE_SUPERADMIN);
        stubGroupRoles(userGroupMock, QcadooSecurityConstants.ROLE_ADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertFalse(isValid);
        verify(userGroupMock).addError(Mockito.eq(groupRolesFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldAlloveAddingRoleSuperadminWhenPerformedBySuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(QcadooSecurityConstants.ROLE_SUPERADMIN);
        stubGroupRoles(existingUserGroupMock, QcadooSecurityConstants.ROLE_ADMIN);
        stubGroupRoles(userGroupMock, QcadooSecurityConstants.ROLE_ADMIN, QcadooSecurityConstants.ROLE_SUPERADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldAlloveRemovingRoleSuperadminWhenPerformedBySuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(QcadooSecurityConstants.ROLE_SUPERADMIN);
        stubGroupRoles(existingUserGroupMock, QcadooSecurityConstants.ROLE_ADMIN, QcadooSecurityConstants.ROLE_SUPERADMIN);
        stubGroupRoles(userGroupMock, QcadooSecurityConstants.ROLE_ADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldAlloveAddingRoleSuperadminWhenPerformedByShop() {
        // given
        stubGroupRoles(existingUserGroupMock, QcadooSecurityConstants.ROLE_ADMIN);
        stubGroupRoles(userGroupMock, QcadooSecurityConstants.ROLE_ADMIN, QcadooSecurityConstants.ROLE_SUPERADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldAlloveRemovingRoleSuperadminWhenPerformedByShop() {
        // given
        stubGroupRoles(existingUserGroupMock, QcadooSecurityConstants.ROLE_ADMIN, QcadooSecurityConstants.ROLE_SUPERADMIN);
        stubGroupRoles(userGroupMock, QcadooSecurityConstants.ROLE_ADMIN);
        // when

        final boolean isValid = groupRolesValidationService.checkUserAddingRoleSuperadmin(groupDataDefMock, userGroupMock);

        // then
        assertTrue(isValid);
    }

}
