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

import static com.qcadoo.security.constants.QcadooSecurityConstants.ROLE_ADMIN;
import static com.qcadoo.security.constants.QcadooSecurityConstants.ROLE_SUPERADMIN;
import static com.qcadoo.security.constants.QcadooSecurityConstants.ROLE_USER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.security.api.SecurityService;
import com.qcadoo.security.constants.GroupFields;
import com.qcadoo.security.constants.QcadooSecurityConstants;
import com.qcadoo.security.constants.RoleFields;
import com.qcadoo.security.constants.UserFields;

public class UserRoleValidationServiceTest {

    private UserRoleValidationService userRoleValidationService;

    @Mock
    private SecurityService securityService;

    @Mock
    private DataDefinition userDataDefMock;

    @Mock
    private FieldDefinition userRoleFieldDefMock;

    @Mock
    private Entity userEntityMock, currentUserEntityMock, existingUserEntityMock, userGroupMock, existingUserGroupMock;

    @Mock
    private SecurityContext securityContext;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);

        given(securityService.getCurrentUserId()).willReturn(1L);

        given(userDataDefMock.getField(UserFields.GROUP)).willReturn(userRoleFieldDefMock);
        given(userDataDefMock.get(1L)).willReturn(currentUserEntityMock);

        given(userEntityMock.getId()).willReturn(1000L);
        given(userDataDefMock.get(1000L)).willReturn(existingUserEntityMock);

        SecurityContextHolder.setContext(securityContext);

        userRoleValidationService = new UserRoleValidationService();
        ReflectionTestUtils.setField(userRoleValidationService, "securityService", securityService);
    }

    private void stubRoleTransition(final String from, final String to) {
        Entity fromRole = mock(Entity.class);
        given(fromRole.getStringField(RoleFields.IDENTIFIER)).willReturn(from);
        given(existingUserGroupMock.getManyToManyField(GroupFields.ROLES)).willReturn(Lists.newArrayList(fromRole));
        given(existingUserEntityMock.getBelongsToField(UserFields.GROUP)).willReturn(from == null ? null : existingUserGroupMock);
        given(securityService.hasRole(existingUserEntityMock, QcadooSecurityConstants.ROLE_SUPERADMIN)).willReturn(
                QcadooSecurityConstants.ROLE_SUPERADMIN.equals(from));

        Entity toRole = mock(Entity.class);
        given(toRole.getStringField(RoleFields.IDENTIFIER)).willReturn(to);
        given(userGroupMock.getManyToManyField(GroupFields.ROLES)).willReturn(Lists.newArrayList(toRole));
        given(userEntityMock.getBelongsToField(UserFields.GROUP)).willReturn(to == null ? null : userGroupMock);
        given(securityService.hasRole(userEntityMock, QcadooSecurityConstants.ROLE_SUPERADMIN)).willReturn(
                QcadooSecurityConstants.ROLE_SUPERADMIN.equals(to));
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
    public final void shouldMarkTransitionFromNullToSuperadminAsInvalidWhenPerformedByNonSuperadminDuringCreation() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_ADMIN);
        stubRoleTransition(null, ROLE_SUPERADMIN);

        given(userEntityMock.getId()).willReturn(null);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertFalse(isValid);
        verify(userEntityMock).addError(Mockito.eq(userRoleFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldMarkTransitionFromNullToSuperadminAsValidWhenPerformedBySuperadminDuringCreation() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_SUPERADMIN);
        stubRoleTransition(null, ROLE_SUPERADMIN);

        given(userEntityMock.getId()).willReturn(null);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromNullToSuperadminAsValidWhenPerformedByShopDuringCreation() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_SUPERADMIN);
        stubRoleTransition(null, ROLE_SUPERADMIN);

        given(userEntityMock.getId()).willReturn(null);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromNullToSuperadminAsInvalidWhenPerformedByNonSuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_ADMIN);
        stubRoleTransition(null, ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertFalse(isValid);
        verify(userEntityMock).addError(Mockito.eq(userRoleFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldMarkTransitionFromNullToSuperadminAsValidWhenPerformedBySuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_SUPERADMIN);
        stubRoleTransition(null, ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromNullToSuperadminAsValidWhenPerformedByShop() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_SUPERADMIN);
        stubRoleTransition(null, ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromBlankToSuperadminAsInvalidWhenPerformedByNonSuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_ADMIN);
        stubRoleTransition(" ", ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertFalse(isValid);
        verify(userEntityMock).addError(Mockito.eq(userRoleFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldMarkTransitionFromBlankToSuperadminAsValidWhenPerformedBySuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_SUPERADMIN);
        stubRoleTransition(" ", ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromBlankToSuperadminAsValidWhenPerformedByShop() {
        stubRoleTransition(" ", ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromSuperadminToSuperadminAsValidWhenPerformedByNonSuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_ADMIN);
        stubRoleTransition(ROLE_SUPERADMIN, ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromSuperadminToSuperadminAsValidWhenPerformedBySuperadmin() {
        // given
        stubSecurityContextWithAuthentication();
        stubCurrentUserRole(ROLE_SUPERADMIN);
        stubRoleTransition(ROLE_SUPERADMIN, ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromSuperadminToSuperadminAsValidWhenPerformedByShop() {
        stubRoleTransition(ROLE_SUPERADMIN, ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromSuperadminToAnyAsInvalid() {
        // given
        stubSecurityContextWithAuthentication();
        stubRoleTransition(ROLE_SUPERADMIN, "99anyOtherRole");

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertFalse(isValid);
        verify(userEntityMock).addError(Mockito.eq(userRoleFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldMarkTransitionFromNullToRegularAdminAsValid() {
        // given
        stubSecurityContextWithAuthentication();
        stubRoleTransition(null, ROLE_ADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromBlankToRegularAdminAsValid() {
        // given
        stubSecurityContextWithAuthentication();
        stubRoleTransition(" ", ROLE_ADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromNonSuperadminToNonSuperadminAsValid() {
        // given
        stubSecurityContextWithAuthentication();
        stubRoleTransition("99anyOtherRole", "89anyOther");

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromNonSuperadminToSuperadminAsInvalid() {
        // given
        stubSecurityContextWithAuthentication();
        stubRoleTransition("99anyOtherRole", ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertFalse(isValid);
        verify(userEntityMock).addError(Mockito.eq(userRoleFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldMarkTransitionFromNonSuperadminToSuperadminAsValidWhenPerformedByShop() {
        // given
        stubRoleTransition("99anyOtherRole", ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromNonSuperadminToSuperadminAsValidWhenPerformedBySuperAdmin() {
        // given
        stubCurrentUserRole(ROLE_SUPERADMIN);
        stubRoleTransition("99anyOtherRole", ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertTrue(isValid);
    }

    @Test
    public final void shouldMarkTransitionFromAdminToSuperadminAsInvalid() {

        // given
        stubSecurityContextWithAuthentication();
        stubRoleTransition(ROLE_ADMIN, ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertFalse(isValid);
        verify(userEntityMock).addError(Mockito.eq(userRoleFieldDefMock), Mockito.anyString());
    }

    @Test
    public final void shouldMarkTransitionFromUserToSuperadminAsInvalid() {
        // given
        stubSecurityContextWithAuthentication();
        stubRoleTransition(ROLE_USER, ROLE_SUPERADMIN);

        // when
        final boolean isValid = userRoleValidationService.checkUserCreatingSuperadmin(userDataDefMock, userEntityMock);

        // then
        assertFalse(isValid);
        verify(userEntityMock).addError(Mockito.eq(userRoleFieldDefMock), Mockito.anyString());
    }

}
