package com.qcadoo.model.internal;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.JoinType;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.types.ManyToManyType;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProxyList implements List<Entity> {

    private final ManyToManyType manyToManyType;
    private final DataDefinition referencedDataDefinition;
    private final Long parentId;
    private final FieldDefinition fieldDefinition;
    private final Entity performer;

    private AtomicReference<List<Entity>> list = new AtomicReference<List<Entity>>(null);

    public ProxyList(final FieldDefinition fieldDefinition, Long parentId, final Entity performer) {
        this.fieldDefinition = fieldDefinition;
        this.parentId = parentId;
        this.performer = performer;

        this.manyToManyType = (ManyToManyType) fieldDefinition.getType();
        this.referencedDataDefinition = manyToManyType.getDataDefinition();
    }

    private List<Entity> getList() {
        if (list.get() == null) {
            SearchCriteriaBuilder searchCriteriaBuilder = referencedDataDefinition.find().createAlias(manyToManyType.getJoinFieldName(), manyToManyType.getJoinFieldName(), JoinType.INNER).add(SearchRestrictions.eq(manyToManyType.getJoinFieldName()+".id", parentId));

            List<Entity> entities = searchCriteriaBuilder.list().getEntities();

            list.compareAndSet(null, entities);
            checkNotNull(list.get(), "Proxy can't load list");
        }
        return list.get();
    }

    public int size() {
        return getList().size();
    }

    public boolean isEmpty() {
        return getList().isEmpty();
    }

    public boolean contains(Object o) {
        return getList().contains(o);
    }

    public Iterator<Entity> iterator() {
        return getList().iterator();
    }

    public Object[] toArray() {
        return getList().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return getList().toArray(a);
    }

    public boolean add(Entity entity) {
        return getList().add(entity);
    }

    public boolean remove(Object o) {
        return getList().remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return getList().containsAll(c);
    }

    public boolean addAll(Collection<? extends Entity> c) {
        return getList().addAll(c);
    }

    public boolean addAll(int index, Collection<? extends Entity> c) {
        return getList().addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return getList().removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return getList().retainAll(c);
    }

    public void clear() {
        getList().clear();
    }

    public Entity get(int index) {
        return getList().get(index);
    }

    public Entity set(int index, Entity element) {
        return getList().set(index, element);
    }

    public void add(int index, Entity element) {
        getList().add(index, element);
    }

    public Entity remove(int index) {
        return getList().remove(index);
    }

    public int indexOf(Object o) {
        return getList().indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return getList().lastIndexOf(o);
    }

    public ListIterator<Entity> listIterator() {
        return getList().listIterator();
    }

    public ListIterator<Entity> listIterator(int index) {
        return getList().listIterator(index);
    }

    public List<Entity> subList(int fromIndex, int toIndex) {
        return getList().subList(fromIndex, toIndex);
    }
}
