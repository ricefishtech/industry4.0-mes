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
package com.qcadoo.model.internal.definitionconverter;

import com.google.common.collect.Lists;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DictionaryService;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.Cascadeable;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.model.constants.VersionableConstants;
import com.qcadoo.model.internal.AbstractModelXmlConverter;
import com.qcadoo.model.internal.DataDefinitionImpl;
import com.qcadoo.model.internal.FieldDefinitionImpl;
import com.qcadoo.model.internal.MasterModel;
import com.qcadoo.model.internal.api.*;
import com.qcadoo.model.internal.hooks.EntityHookDefinitionImpl;
import com.qcadoo.model.internal.hooks.FieldHookDefinitionImpl;
import com.qcadoo.model.internal.hooks.HookInitializationException;
import com.qcadoo.model.internal.types.*;
import com.qcadoo.model.internal.utils.ClassNameUtils;
import com.qcadoo.model.internal.validators.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkState;
import static com.qcadoo.model.internal.AbstractModelXmlConverter.FieldsTag.PRIORITY;
import static com.qcadoo.model.internal.AbstractModelXmlConverter.OtherTag.IDENTIFIER;
import static com.qcadoo.model.internal.AbstractModelXmlConverter.OtherTag.MASTERMODEL;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
import static org.springframework.util.StringUtils.hasText;

@Service
public final class ModelXmlToDefinitionConverterImpl extends AbstractModelXmlConverter implements ModelXmlToDefinitionConverter {

    private static final Logger LOG = LoggerFactory.getLogger(ModelXmlToDefinitionConverterImpl.class);

    private static final String L_PARSE_ERROR = "Error while parsing model.xml: ";

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private InternalDataDefinitionService dataDefinitionService;

    @Autowired
    private DataAccessService dataAccessService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TranslationService translationService;

    @Transactional
    @Override
    public Collection<DataDefinition> convert(final Resource... resources) {
        List<DataDefinition> dataDefinitions = new ArrayList<DataDefinition>();

        for (Resource resource : resources) {
            if (resource.isReadable()) {
                LOG.info("Creating dataDefinition from " + resource);

                try {
                    dataDefinitions.add(parse(resource.getInputStream()));
                } catch (HookInitializationException | IOException | ModelXmlParsingException | XMLStreamException
                        | javax.xml.stream.FactoryConfigurationError e) {
                    throw new IllegalStateException(L_PARSE_ERROR + e.getMessage(), e);
                }
            }
        }

        return dataDefinitions;
    }

    private DataDefinition parse(final InputStream stream) throws HookInitializationException, ModelXmlParsingException,
            XMLStreamException {
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
        DataDefinition dataDefinition = null;

        while (reader.hasNext() && reader.next() > 0) {
            if (isTagStarted(reader, TAG_MODEL)) {
                dataDefinition = getDataDefinition(reader, getPluginIdentifier(reader));
                break;
            }
        }

        reader.close();

        return dataDefinition;
    }

    private DataDefinition getDataDefinition(final XMLStreamReader reader, final String pluginIdentifier)
            throws XMLStreamException, HookInitializationException, ModelXmlParsingException {
        DataDefinitionImpl dataDefinition = getModelDefinition(reader, pluginIdentifier);

        LOG.info("Creating dataDefinition " + dataDefinition);

        parseElementChildren(reader, TAG_MODEL, childTag -> {
            if (TAG_FIELDS.equals(getTagStarted(reader))) {
                parseFields(reader, dataDefinition);
            }

            if (TAG_HOOKS.equals(getTagStarted(reader))) {
                parseHooks(reader, dataDefinition);
            }

            String tag = getTagStarted(reader);
            if (tag != null) {
                addOtherElement(reader, dataDefinition, tag);
            }
        });

        dataDefinitionService.save(dataDefinition);

        return dataDefinition;
    }

    private void addAuditFields(final DataDefinitionImpl dataDefinition) {
        dataDefinition.withField(getAuditFieldDefinition(dataDefinition, "createDate", new DateTimeType()));
        dataDefinition.withField(getAuditFieldDefinition(dataDefinition, "updateDate", new DateTimeType()));
        dataDefinition.withField(getAuditFieldDefinition(dataDefinition, "createUser", new StringType()));
        dataDefinition.withField(getAuditFieldDefinition(dataDefinition, "updateUser", new StringType()));
    }

    private void addVersionFields(final DataDefinitionImpl dataDefinition) {
        FieldDefinitionImpl fieldDefinition = new FieldDefinitionImpl(dataDefinition, VersionableConstants.VERSION_FIELD_NAME);
        fieldDefinition.withReadOnly(false);
        fieldDefinition.setPersistent(true);
        fieldDefinition.withType(new LongType(false));

        dataDefinition.withField(fieldDefinition);
    }

    private void parseFields(final XMLStreamReader reader, final DataDefinitionImpl dataDefinition) throws XMLStreamException,
            HookInitializationException, ModelXmlParsingException {
        parseElementChildren(reader, TAG_FIELDS, childTag -> addFieldElement(reader, dataDefinition, childTag));
    }

    private void parseHooks(final XMLStreamReader reader, final DataDefinitionImpl dataDefinition) throws XMLStreamException,
            HookInitializationException, ModelXmlParsingException {
        parseElementChildren(reader, TAG_HOOKS, childTag -> addHookElement(reader, dataDefinition, childTag));
    }

    public interface ParseElementChildrenAction {

        void apply(final String childTag) throws XMLStreamException, HookInitializationException, ModelXmlParsingException;
    }

    private void parseElementChildren(final XMLStreamReader reader, final String tag, final ParseElementChildrenAction strategy)
            throws XMLStreamException, HookInitializationException, ModelXmlParsingException {
        while (reader.hasNext() && reader.next() > 0) {
            if (isTagEnded(reader, tag)) {
                break;
            }

            String childTag = getTagStarted(reader);
            if (childTag != null) {
                strategy.apply(childTag);
            }
        }
    }

    private void addHookElement(final XMLStreamReader reader, final DataDefinitionImpl dataDefinition, final String tag)
            throws XMLStreamException, HookInitializationException, ModelXmlParsingException {

        HooksTag hooksTag;
        try {
            hooksTag = HooksTag.valueOf(tag.toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            throw new ModelXmlParsingException("Illegal type of model's hook '" + tag + "'");
        }

        EntityHookDefinition hookDefinition = getHookDefinition(reader);
        if (HooksTag.VALIDATESWITH.equals(hooksTag)) {
            hookDefinition = new CustomEntityValidator(hookDefinition);
        }

        dataDefinition.addHook(hooksTag, hookDefinition);
    }

    private void addOtherElement(final XMLStreamReader reader, final DataDefinitionImpl dataDefinition, final String tag)
            throws XMLStreamException {
        OtherTag otherTag = OtherTag.valueOf(tag.toUpperCase(Locale.ENGLISH));
        if (otherTag == IDENTIFIER) {
            dataDefinition.setIdentifierExpression(getIdentifierExpression(reader));
        } else if(otherTag == MASTERMODEL){
            dataDefinition.setMasterModel(getMasterModel(reader));
        }
    }

    private void addFieldElement(final XMLStreamReader reader, final DataDefinitionImpl dataDefinition, final String tag)
            throws XMLStreamException, HookInitializationException, ModelXmlParsingException {
        FieldsTag fieldTag = FieldsTag.valueOf(tag.toUpperCase(Locale.ENGLISH));
        if (fieldTag == PRIORITY) {
            dataDefinition.addPriorityField(getPriorityFieldDefinition(reader, dataDefinition));
        } else {
            dataDefinition.withField(getFieldDefinition(reader, dataDefinition, fieldTag));
        }
    }

    private DataDefinitionImpl getModelDefinition(final XMLStreamReader reader, final String pluginIdentifier) {
        String modelName = getStringAttribute(reader, "name");

        LOG.info("Reading model " + modelName + " for plugin " + pluginIdentifier);

        DataDefinitionImpl dataDefinition = new DataDefinitionImpl(pluginIdentifier, modelName, dataAccessService);
        dataDefinition.setDeletable(getBooleanAttribute(reader, "deletable", true));
        dataDefinition.setInsertable(getBooleanAttribute(reader, "insertable", true));
        dataDefinition.setUpdatable(getBooleanAttribute(reader, "updatable", true));
        dataDefinition.setActivable(getBooleanAttribute(reader, "activable", false));
        dataDefinition.setAuditable(getBooleanAttribute(reader, "auditable", false));
        if (dataDefinition.isAuditable()) {
            addAuditFields(dataDefinition);
        }
        dataDefinition.setVersionable(getBooleanAttribute(reader, VersionableConstants.VERSIONABLE_ATTRIBUTE_NAME, false));
        if (dataDefinition.isVersionable()) {
            addVersionFields(dataDefinition);
        }
        dataDefinition.setFullyQualifiedClassName(ClassNameUtils.getFullyQualifiedClassName(pluginIdentifier, modelName));
        return dataDefinition;
    }

    private FieldType getDictionaryType(final XMLStreamReader reader) {
        String dictionaryName = getStringAttribute(reader, "dictionary");
        checkState(hasText(dictionaryName), "Dictionary name is required");
        return new DictionaryType(dictionaryName, dictionaryService, getBooleanAttribute(reader, "copyable", true));
    }

    private FieldType getEnumType(final XMLStreamReader reader, final boolean copyable, final String translationPath)
            throws XMLStreamException {
        String values = getStringAttribute(reader, "values");
        if (hasText(values)) {
            return new EnumType(translationService, translationPath, copyable, values.split(","));
        } else {
            return new EnumType(translationService, translationPath, copyable);
        }
    }

    private FieldType getHasManyType(final XMLStreamReader reader, final String pluginIdentifier) {
        CollectionTypeCommonParams params = new CollectionTypeCommonParams(reader, pluginIdentifier);
        return new HasManyEntitiesType(params.getPluginName(), params.getModelName(), params.getJoinFieldName(),
                params.getCascade(), params.isCopyable(), dataDefinitionService);
    }

    private FieldType getManyToManyType(final XMLStreamReader reader, final String pluginIdentifier) {
        CollectionTypeCommonParams params = new CollectionTypeCommonParams(reader, pluginIdentifier);
        return new ManyToManyEntitiesType(params.getPluginName(), params.getModelName(), params.getJoinFieldName(),
                params.getCascade(), params.isCopyable(), params.isLazyLoading(), dataDefinitionService);
    }

    private FieldType getTreeType(final XMLStreamReader reader, final String pluginIdentifier) {
        CollectionTypeCommonParams params = new CollectionTypeCommonParams(reader, pluginIdentifier);
        return new TreeEntitiesType(params.getPluginName(), params.getModelName(), params.getJoinFieldName(),
                params.getCascade(), params.isCopyable(), dataDefinitionService);
    }

    private final class CollectionTypeCommonParams {

        private final String pluginName;

        private final String modelName;

        private final String joinFieldName;

        private final Cascadeable.Cascade cascade;

        private final boolean isCopyable;

        private final boolean isLazyLoading;

        private CollectionTypeCommonParams(final XMLStreamReader reader, final String pluginIdentifier) {
            pluginName = getStringAttribute(reader, TAG_PLUGIN, pluginIdentifier);
            modelName = getStringAttribute(reader, TAG_MODEL);
            joinFieldName = getStringAttribute(reader, TAG_JOIN_FIELD);
            cascade = Cascadeable.Cascade.parse(getStringAttribute(reader, "cascade"));
            isCopyable = getBooleanAttribute(reader, "copyable", false);
            isLazyLoading = getBooleanAttribute(reader, "lazy", false);
        }

        public String getPluginName() {
            return pluginName;
        }

        public String getModelName() {
            return modelName;
        }

        public String getJoinFieldName() {
            return joinFieldName;
        }

        public Cascadeable.Cascade getCascade() {
            return cascade;
        }

        public boolean isCopyable() {
            return isCopyable;
        }

        public boolean isLazyLoading() {
            return isLazyLoading;
        }
    }

    private FieldType getBelongsToType(final XMLStreamReader reader, final String pluginIdentifier) {
        String pluginName = getStringAttribute(reader, TAG_PLUGIN, pluginIdentifier);
        String modelName = getStringAttribute(reader, TAG_MODEL);
        boolean lazy = getBooleanAttribute(reader, "lazy", true);
        boolean isCopyable = getBooleanAttribute(reader, "copyable", true);
        return new BelongsToEntityType(pluginName, modelName, dataDefinitionService, lazy, isCopyable);
    }

    private FieldDefinition getAuditFieldDefinition(final DataDefinitionImpl dataDefinition, final String name,
            final FieldType type) {
        FieldDefinitionImpl fieldDefinition = new FieldDefinitionImpl(dataDefinition, name);
        fieldDefinition.withReadOnly(false);
        fieldDefinition.setPersistent(true);
        fieldDefinition.withType(type);
        return fieldDefinition;
    }

    private FieldDefinition getFieldDefinition(final XMLStreamReader reader, final DataDefinitionImpl dataDefinition,
            final FieldsTag fieldTag) throws XMLStreamException, HookInitializationException, ModelXmlParsingException {
        String fieldType = reader.getLocalName();
        String name = getStringAttribute(reader, "name");
        FieldDefinitionImpl fieldDefinition = new FieldDefinitionImpl(dataDefinition, name);
        fieldDefinition.withReadOnly(getBooleanAttribute(reader, "readonly", false));
        fieldDefinition.withDefaultValue(getStringAttribute(reader, "default"));
        fieldDefinition.setPersistent(getBooleanAttribute(reader, "persistent", true));
        fieldDefinition.setExpression(getStringAttribute(reader, "expression"));
        FieldType type = getFieldType(reader, dataDefinition, name, fieldTag, fieldType);
        fieldDefinition.withType(type);

        if (getBooleanAttribute(reader, "required", false)) {
            fieldDefinition.withValidator(getValidatorDefinition(reader, new RequiredValidator()));
        }
        if (getBooleanAttribute(reader, "unique", false)) {
            if (type.isCopyable() && !fieldDefinition.canBeBothCopyableAndUnique()) {
                String message = String
                        .format("Unique field can not have the copyable attribute set to true. Add 'copyable=\"false\"' to #%s_%s.%s to fix it.",
                                dataDefinition.getPluginIdentifier(), dataDefinition.getName(), name);
                throw new IllegalStateException(message);
            }
            fieldDefinition.withValidator(getValidatorDefinition(reader, new UniqueValidator()));
        }

        parseFieldValidators(reader, fieldType, fieldDefinition).forEach(fieldDefinition::withValidator);
        fieldDefinition.withMissingDefaultValidators();

        return fieldDefinition;
    }

    private Collection<FieldHookDefinition> parseFieldValidators(final XMLStreamReader reader, final String fieldType,
            final FieldDefinition fieldDefinition) throws XMLStreamException, HookInitializationException,
            ModelXmlParsingException {
        List<FieldHookDefinition> fieldValidators = Lists.newArrayList();
        while (reader.hasNext() && reader.next() > 0) {
            if (isTagEnded(reader, fieldType)) {
                break;
            }
            String tag = getTagStarted(reader);
            if (tag == null) {
                continue;
            }
            fieldValidators.add(createFieldElement(reader, fieldDefinition, tag));
        }
        return fieldValidators;
    }

    private FieldHookDefinition createFieldElement(final XMLStreamReader reader, final FieldDefinition fieldDefinition,
            final String tag) throws HookInitializationException, ModelXmlParsingException {
        FieldHookDefinition fieldHookDefinition;
        switch (FieldTag.valueOf(tag.toUpperCase(Locale.ENGLISH))) {
            case VALIDATESLENGTH:
                fieldHookDefinition = getValidatorDefinition(reader, new LengthValidator(getIntegerAttribute(reader, "min"),
                        getIntegerAttribute(reader, "is"), getIntegerAttribute(reader, "max")));
                break;
            case VALIDATESUNSCALEDVALUE:
                fieldHookDefinition = getValidatorDefinition(reader,
                        new UnscaledValueValidator(getIntegerAttribute(reader, "min"), getIntegerAttribute(reader, "is"),
                                getIntegerAttribute(reader, "max")));
                break;
            case VALIDATESSCALE:
                fieldHookDefinition = getValidatorDefinition(reader, new ScaleValidator(getIntegerAttribute(reader, "min"),
                        getIntegerAttribute(reader, "is"), getIntegerAttribute(reader, "max")));
                break;
            case VALIDATESRANGE:
                FieldType type = fieldDefinition.getType();
                Object from = getRangeForType(getStringAttribute(reader, "from"), type);
                Object to = getRangeForType(getStringAttribute(reader, "to"), type);
                boolean exclusively = getBooleanAttribute(reader, "exclusively", false);
                fieldHookDefinition = getValidatorDefinition(reader, new RangeValidator(from, to, exclusively));
                break;
            case VALIDATESWITH:
                fieldHookDefinition = getValidatorDefinition(reader, new CustomValidator(getFieldHookDefinition(reader)));
                break;
            case VALIDATESREGEX:
                fieldHookDefinition = getValidatorDefinition(reader, new RegexValidator(getStringAttribute(reader, "pattern")));
                break;
            default:
                throw new ModelXmlParsingException("Illegal type of field's validator '" + tag + "'");
        }
        return fieldHookDefinition;
    }

    private FieldType getFieldType(final XMLStreamReader reader, final DataDefinition dataDefinition, final String fieldName,
            final FieldsTag fieldTag, final String fieldType) throws XMLStreamException, ModelXmlParsingException {
        // TODO DEV_TEAM consider move default value resolving from converter into concrete field type's constructor.
        Boolean isCopyable = getBooleanAttribute(reader, "copyable", true);
        switch (fieldTag) {
            case INTEGER:
                return new IntegerType(isCopyable);
            case STRING:
                return new StringType(isCopyable);
            case FILE:
                return new FileType(isCopyable);
            case TEXT:
                return new TextType(isCopyable);
            case DECIMAL:
                return new DecimalType(isCopyable);
            case DATETIME:
                return new DateTimeType(isCopyable);
            case DATE:
                return new DateType(isCopyable);
            case BOOLEAN:
                return new BooleanType(isCopyable);
            case BELONGSTO:
                return getBelongsToType(reader, dataDefinition.getPluginIdentifier());
            case HASMANY:
                return getHasManyType(reader, dataDefinition.getPluginIdentifier());
            case MANYTOMANY:
                return getManyToManyType(reader, dataDefinition.getPluginIdentifier());
            case TREE:
                return getTreeType(reader, dataDefinition.getPluginIdentifier());
            case ENUM:
                String translationPath = dataDefinition.getPluginIdentifier() + "." + dataDefinition.getName() + "." + fieldName;
                return getEnumType(reader, isCopyable, translationPath);
            case DICTIONARY:
                return getDictionaryType(reader);
            case PASSWORD:
                return new PasswordType(passwordEncoder, isCopyable);
            default:
                throw new ModelXmlParsingException("Illegal type of field '" + fieldType + "'");
        }
    }

    private Object getRangeForType(final String range, final FieldType type) throws ModelXmlParsingException {
        if (range == null) {
            return null;
        } else if (type instanceof DateTimeType) {
            try {
                return new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT, getLocale()).parse(range);
            } catch (ParseException e) {
                throw new ModelXmlParsingException("Range '" + range + "' has invalid datetime format, should match "
                        + DateUtils.L_DATE_TIME_FORMAT, e);
            }
        } else if (type instanceof DateType) {
            try {
                return new SimpleDateFormat(DateUtils.L_DATE_FORMAT, getLocale()).parse(range);
            } catch (ParseException e) {
                throw new ModelXmlParsingException("Range '" + range + "' has invalid date format, should match "
                        + DateUtils.L_DATE_FORMAT, e);
            }
        } else if (type instanceof DecimalType) {
            return new BigDecimal(range);
        } else if (type instanceof IntegerType) {
            return Integer.parseInt(range);
        } else {
            return range;
        }
    }

    private FieldHookDefinition getValidatorDefinition(final XMLStreamReader reader, final FieldHookDefinition validator) {
        String customMessage = getStringAttribute(reader, "message");
        if (StringUtils.hasText(customMessage) && validator instanceof ErrorMessageDefinition) {
            ((ErrorMessageDefinition) validator).setErrorMessage(customMessage);
        }
        return validator;
    }

    private EntityHookDefinition getHookDefinition(final XMLStreamReader reader) throws HookInitializationException {
        return getHookDefinition(reader, null);
    }

    private EntityHookDefinition getHookDefinition(final XMLStreamReader reader, final String pluginIdentifier)
            throws HookInitializationException {
        String className = getStringAttribute(reader, "class");
        String methodName = getStringAttribute(reader, "method");
        return new EntityHookDefinitionImpl(className, methodName, pluginIdentifier, applicationContext);
    }

    private FieldHookDefinition getFieldHookDefinition(final XMLStreamReader reader) throws HookInitializationException {
        return getFieldHookDefinition(reader, null);
    }

    private FieldHookDefinition getFieldHookDefinition(final XMLStreamReader reader, final String pluginIdentifier)
            throws HookInitializationException {
        String className = getStringAttribute(reader, "class");
        String methodName = getStringAttribute(reader, "method");
        return new FieldHookDefinitionImpl(className, methodName, pluginIdentifier, applicationContext);
    }

    private FieldDefinition getPriorityFieldDefinition(final XMLStreamReader reader, final DataDefinitionImpl dataDefinition) {
        String scopeAttribute = getStringAttribute(reader, "scope");
        FieldDefinition scopedField = null;
        if (scopeAttribute != null) {
            scopedField = dataDefinition.getField(scopeAttribute);
        }
        return new FieldDefinitionImpl(dataDefinition, getStringAttribute(reader, "name"))
                .withType(new PriorityType(scopedField));
    }

    protected MasterModel getMasterModel(final XMLStreamReader reader) {
        return new MasterModel(getStringAttribute(reader, "plugin"), getStringAttribute(reader, "model"));
    }

    @Aspect
    static class PluginIdentifierInjectionAspect {

        @Pointcut("execution(* ModelXmlToDefinitionConverterImpl.getDataDefinition(javax.xml.stream.XMLStreamReader, String)) && args(*, pluginIdentifier)")
        public void execGetDataDefinition(final String pluginIdentifier) {
        }

        @Pointcut("execution((com.qcadoo.model.internal.api.EntityHookDefinition || com.qcadoo.model.internal.api.FieldHookDefinition) ModelXmlToDefinitionConverterImpl.*(javax.xml.stream.XMLStreamReader, String)) && args(reader, *)")
        public void execHookDefinitionGetter(final XMLStreamReader reader) {
        }

        @Pointcut("execution(com.qcadoo.model.api.FieldDefinition ModelXmlToDefinitionConverterImpl.getFieldDefinition(javax.xml.stream.XMLStreamReader, com.qcadoo.model.internal.DataDefinitionImpl, com.qcadoo.model.internal.AbstractModelXmlConverter.FieldsTag)) && args(reader, ..)")
        public void execFieldDefinitionGetter(final XMLStreamReader reader) {
        }

        @Around("execHookDefinitionGetter(reader) && cflow(execGetDataDefinition(pluginIdentifier))")
        public Object appendPluginIdentifierToHook(final ProceedingJoinPoint pjp, final XMLStreamReader reader,
                final String pluginIdentifier) throws Throwable {
            Object[] args = pjp.getArgs();
            args[1] = getSourcePluginName(reader, pluginIdentifier);
            return pjp.proceed(args);
        }

        @Around("execFieldDefinitionGetter(reader) && cflow(execGetDataDefinition(pluginIdentifier))")
        public Object appendPluginIdentifierToField(final ProceedingJoinPoint pjp, final XMLStreamReader reader,
                final String pluginIdentifier) throws Throwable {
            String sourcePluginIdentifier = getSourcePluginName(reader, pluginIdentifier);
            FieldDefinitionImpl fieldDefinition = (FieldDefinitionImpl) pjp.proceed();
            fieldDefinition.setPluginIdentifier(sourcePluginIdentifier);
            return fieldDefinition;
        }

        private String getSourcePluginName(final XMLStreamReader reader, final String targetPluginName) {
            String sourcePluginIdentifier = reader.getAttributeValue(null, "sourcePluginIdentifier");
            if (sourcePluginIdentifier == null && targetPluginName == null) {
                throw new IllegalStateException("Missing plugin identifier");
            } else if (sourcePluginIdentifier == null) {
                return targetPluginName;
            } else {
                return sourcePluginIdentifier;
            }
        }

    }

}
