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
package com.qcadoo.model.beans.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

public class SampleSimpleDatabaseObject {

    private Long id;

    private String readOnly;

    private String name;

    private Integer age;

    private Integer priority;

    private BigDecimal money;

    private Boolean retired;

    private Date birthDate;

    private SampleParentDatabaseObject belongsTo;

    private SampleSimpleDatabaseObject belongsToSimple;

    private SampleParentDatabaseObject lazyBelongsTo;

    private Set<SampleParentDatabaseObject> manyToMany;

    public SampleSimpleDatabaseObject() {
    }

    public SampleSimpleDatabaseObject(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(final String readOnly) {
        this.readOnly = readOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public SampleParentDatabaseObject getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(final SampleParentDatabaseObject belongsTo) {
        this.belongsTo = belongsTo;
    }

    public SampleSimpleDatabaseObject getBelongsToSimple() {
        return belongsToSimple;
    }

    public void setBelongsToSimple(final SampleSimpleDatabaseObject belongsToSimple) {
        this.belongsToSimple = belongsToSimple;
    }

    public SampleParentDatabaseObject getLazyBelongsTo() {
        return lazyBelongsTo;
    }

    public void setLazyBelongsTo(final SampleParentDatabaseObject lazyBelongsTo) {
        this.lazyBelongsTo = lazyBelongsTo;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(final Integer priority) {
        this.priority = priority;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(final BigDecimal money) {
        this.money = money;
    }

    public Boolean getRetired() {
        return retired;
    }

    public void setRetired(final Boolean retired) {
        this.retired = retired;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(final Date birthDate) {
        this.birthDate = birthDate;
    }

    public Set<SampleParentDatabaseObject> getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(Set<SampleParentDatabaseObject> manyToMany) {
        this.manyToMany = manyToMany;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
