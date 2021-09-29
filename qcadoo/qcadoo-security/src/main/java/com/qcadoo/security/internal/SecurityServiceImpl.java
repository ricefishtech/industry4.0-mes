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
package com.qcadoo.security.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.aop.Monitorable;
import com.qcadoo.model.api.search.SearchOrders;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.security.constants.GroupFields;
import com.qcadoo.security.constants.PersistentTokenFields;
import com.qcadoo.security.constants.QcadooSecurityConstants;
import com.qcadoo.security.constants.RoleFields;
import com.qcadoo.security.constants.UserFields;
import com.qcadoo.security.internal.api.InternalSecurityService;
import com.qcadoo.security.internal.api.QcadooUser;

@Service("userDetailsService")
public class SecurityServiceImpl implements InternalSecurityService, UserDetailsService, PersistentTokenRepository,
        ApplicationListener<AbstractAuthenticationEvent> {

    private static final String L_TARGET_ROLE_IDENTIFIER_MUST_BE_GIVEN = "targetRoleIdetifier must be given";

    private static final String L_USER_ENTITY_MUST_BE_GIVEN = "User entity must be given";;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private SecurityRolesService securityRolesService;

    @Autowired(required = false)
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(final AbstractAuthenticationEvent event) {
        if (!(event instanceof AbstractAuthenticationFailureEvent)) {
            UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();

            Entity user = dataDefinitionService
                    .get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_USER).find()
                    .add(SearchRestrictions.eq(UserFields.USER_NAME, userDetails.getUsername())).uniqueResult();

            Calendar now = Calendar.getInstance();
            now.add(Calendar.DAY_OF_YEAR, -1);

            if ((user.getField(UserFields.LAST_ACTIVITY) == null)
                    || now.getTime().after(user.getDateField(UserFields.LAST_ACTIVITY))) {
                user.setField(UserFields.LAST_ACTIVITY, new Date());

                if(Objects.nonNull(request)){
                    user.setField(UserFields.IP_ADDRESS, getClientIP());
                }

                dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_USER).save(
                        user);
            }
        }
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(xfHeader)){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Override
    @Monitorable
    public String getCurrentUserName() {
        if ((SecurityContextHolder.getContext() == null) || (SecurityContextHolder.getContext().getAuthentication() == null)
                || (SecurityContextHolder.getContext().getAuthentication().getName() == null)) {
            return null;
        }

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Entity user = getUserEntity(userName);

        checkNotNull(user, "Current user with login %s cannot be found", userName);

        return user.getStringField(UserFields.USER_NAME);
    }

    @Override
    public List<QcadooUser> getUsers() {
        List<Entity> users = dataDefinitionService
                .get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_USER).find()
                .addOrder(SearchOrders.asc(UserFields.USER_NAME)).list().getEntities();

        List<QcadooUser> qcadooUsers = Lists.newLinkedList();

        for (Entity user : users) {
            qcadooUsers.add(new QcadooUser(user));
        }

        return qcadooUsers;
    }

    @Override
    public Entity getUserEntity(final String userName) {
        return dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_USER).find()
                .add(SearchRestrictions.eq(UserFields.USER_NAME, userName)).setMaxResults(1).uniqueResult();
    }

    @Override
    @Monitorable
    public Long getCurrentUserId() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        Entity user = getUserEntity(userName);

        checkNotNull(user, "Current user with login %s cannot be found", userName);

        return user.getId();
    }

    @Override
    @Monitorable
    public UserDetails loadUserByUsername(final String username) {
        Entity user = getUserEntity(username);

        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }

        return convertEntityToUserDetails(user);
    }

    protected UserDetails convertEntityToUserDetails(final Entity user) {
        List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();

        for (Entity role : user.getBelongsToField(UserFields.GROUP).getManyToManyField(GroupFields.ROLES)) {
            SecurityRole securityRole = securityRolesService.getRoleByIdentifier(role.getStringField(RoleFields.IDENTIFIER));

            checkState(securityRole != null, "Role '%s' not defined", role.getStringField(RoleFields.IDENTIFIER));

            grantedAuthorities.add(new GrantedAuthorityImpl(securityRole.getRoleIdentifier()));
        }

        checkState(!grantedAuthorities.isEmpty(), "Current user with login %s cannot be found",
                user.getStringField(UserFields.USER_NAME));

        return new User(user.getStringField(UserFields.USER_NAME), user.getStringField(UserFields.PASSWORD), true, true, true,
                true, grantedAuthorities);
    }

    @Override
    public void createNewToken(final PersistentRememberMeToken persistentRememberMeToken) {
        Entity persistentToken = dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER,
                QcadooSecurityConstants.MODEL_PERSISTENT_TOKEN).create();

        persistentToken.setField(PersistentTokenFields.USER_NAME, persistentRememberMeToken.getUsername());
        persistentToken.setField(PersistentTokenFields.SERIES, persistentRememberMeToken.getSeries());
        persistentToken.setField(PersistentTokenFields.TOKEN, persistentRememberMeToken.getTokenValue());
        persistentToken.setField(PersistentTokenFields.LAST_USED, persistentRememberMeToken.getDate());

        dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_PERSISTENT_TOKEN)
                .save(persistentToken);
    }

    @Override
    public void updateToken(final String series, final String token, final Date lastUsed) {
        Entity persistentToken = getPersistentToken(series);

        if (persistentToken != null) {
            persistentToken.setField(PersistentTokenFields.TOKEN, token);
            persistentToken.setField(PersistentTokenFields.LAST_USED, lastUsed);

            dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_PERSISTENT_TOKEN)
                    .save(persistentToken);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(final String series) {
        Entity persistentToken = getPersistentToken(series);

        if ((persistentToken == null) || (getUserEntity(persistentToken.getStringField(PersistentTokenFields.USER_NAME)) == null)) {
            return null;
        }

        return new PersistentRememberMeToken(persistentToken.getStringField(PersistentTokenFields.USER_NAME),
                persistentToken.getStringField(PersistentTokenFields.SERIES),
                persistentToken.getStringField(PersistentTokenFields.TOKEN),
                persistentToken.getDateField(PersistentTokenFields.LAST_USED));
    }

    @Override
    public void removeUserTokens(final String username) {
        List<Entity> persistentTokens = dataDefinitionService
                .get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_PERSISTENT_TOKEN).find()
                .add(SearchRestrictions.eq(PersistentTokenFields.USER_NAME, username)).list().getEntities();

        for (Entity persistentToken : persistentTokens) {
            dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_PERSISTENT_TOKEN)
                    .delete(persistentToken.getId());
        }
    }

    private Entity getPersistentToken(final String series) {
        List<Entity> persistentTokens = dataDefinitionService
                .get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_PERSISTENT_TOKEN).find()
                .add(SearchRestrictions.eq(PersistentTokenFields.SERIES, series)).list().getEntities();

        if (persistentTokens.size() == 1) {
            return persistentTokens.get(0);
        } else {
            return null;
        }
    }

    @Override
    public boolean hasRole(Entity user, String targetRoleIdetifier) {
        checkNotNull(targetRoleIdetifier, L_TARGET_ROLE_IDENTIFIER_MUST_BE_GIVEN);
        checkNotNull(user, L_USER_ENTITY_MUST_BE_GIVEN);

        List<Entity> roles = user.getBelongsToField(UserFields.GROUP).getManyToManyField(GroupFields.ROLES);

        for (Entity role : roles) {
            if (targetRoleIdetifier.equals(role.getStringField(RoleFields.IDENTIFIER))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasCurrentUserRole(String targetRoleIdentifier) {
        checkNotNull(targetRoleIdentifier, L_TARGET_ROLE_IDENTIFIER_MUST_BE_GIVEN);
        Entity user = getUserEntity(getCurrentUserName());
        List<Entity> roles = user.getBelongsToField(UserFields.GROUP).getManyToManyField(GroupFields.ROLES);

        for (Entity role : roles) {
            if (targetRoleIdentifier.equals(role.getStringField(RoleFields.IDENTIFIER))) {
                return true;
            }
        }

        return false;
    }
}
