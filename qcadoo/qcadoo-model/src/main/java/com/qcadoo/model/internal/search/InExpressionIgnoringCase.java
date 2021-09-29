package com.qcadoo.model.internal.search;

import java.util.ArrayList;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

public class InExpressionIgnoringCase implements Criterion {

    private final String propertyName;

    private final Object[] values;

    public InExpressionIgnoringCase(String propertyName, Object[] values) {
        this.propertyName = propertyName;
        this.values = values;
    }

    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String[] columns = criteriaQuery.findColumns(this.propertyName, criteria);
        final String[] wrappedLowerColumns = wrapLower(columns);
        String cols;
        if (!criteriaQuery.getFactory().getDialect().supportsRowValueConstructorSyntaxInInList() && columns.length > 1) {
            cols = " ( " + StringHelper.join(" = lower(?) and ", wrappedLowerColumns) + "= lower(?)) ";
            cols = this.values.length > 0 ? StringHelper.repeat(cols + "or ", this.values.length - 1) + cols : "";
            cols = " ( " + cols + " ) ";
            return cols;
        } else {
            cols = StringHelper.repeat("lower(?), ", columns.length - 1) + "lower(?)";
            if (columns.length > 1) {
                cols = '(' + cols + ')';
            }

            String params = this.values.length > 0 ? StringHelper.repeat(cols + ", ", this.values.length - 1) + cols : "";
            String cols1 = StringHelper.join(", ", wrappedLowerColumns);
            if (columns.length > 1) {
                cols1 = '(' + cols1 + ')';
            }

            return cols1 + " in (" + params + ')';
        }
    }

    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        ArrayList list = new ArrayList();
        Type type = criteriaQuery.getTypeUsingProjection(criteria, this.propertyName);
        if (type.isComponentType()) {
            CompositeType actype = (CompositeType) type;
            Type[] types = actype.getSubtypes();

            for (int j = 0; j < this.values.length; ++j) {
                for (int i = 0; i < types.length; ++i) {
                    Object subval = this.values[j] == null ? null : actype.getPropertyValues(this.values[j], EntityMode.POJO)[i];
                    list.add(new TypedValue(types[i], subval, EntityMode.POJO));
                }
            }
        } else {
            for (int j = 0; j < this.values.length; ++j) {
                list.add(new TypedValue(type, this.values[j], EntityMode.POJO));
            }
        }

        return (TypedValue[]) ((TypedValue[]) list.toArray(new TypedValue[list.size()]));
    }

    public String toString() {
        return this.propertyName + " in (" + StringHelper.toString(this.values) + ')';
    }

    private String[] wrapLower(final String[] columns) {
        final String[] wrappedColumns = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            wrappedColumns[i] = "lower(" + columns[i] + ")";
        }
        return wrappedColumns;
    }
}
