package com.qcadoo.model.api.exception;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.validators.ErrorMessage;

import java.util.List;
import java.util.Map;

public class EntityRuntimeException extends RuntimeException {

    private Map<String, ErrorMessage> errors = Maps.newHashMap();
    private List<ErrorMessage> globalErrors = Lists.newArrayList();
    private Entity entity;

    public EntityRuntimeException(final Entity entity) {
        this.errors = entity.getErrors();
        this.globalErrors = entity.getGlobalErrors();
        this.entity = entity;
    }

    public Map<String, ErrorMessage> getErrors() {
        return errors;
    }

    public List<ErrorMessage> getGlobalErrors() {
        return globalErrors;
    }

    public Entity getEntity() {
        return entity;
    }
}
