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
package com.qcadoo.model.internal.dictionaries;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.model.TransactionMockAwareTest;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.DictionaryService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchOrders;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.internal.DefaultEntity;

public class DictionaryServiceTest extends TransactionMockAwareTest {

    private final DataDefinitionService dataDefinitionService = mock(DataDefinitionService.class, RETURNS_DEEP_STUBS);

    private DictionaryService dictionaryService = null;

    @Before
    public void init() {
        dictionaryService = new DictionaryServiceImpl();
        ReflectionTestUtils.setField(dictionaryService, "dataDefinitionService", dataDefinitionService);
    }

    @Test
    public void shouldReturnListOfDictionaries() throws Exception {
        // given
        Entity dict1 = new DefaultEntity(null);
        dict1.setField("name", "Dict1");
        dict1.setField("active", true);
        Entity dict2 = new DefaultEntity(null);
        dict2.setField("name", "Dict2");
        dict2.setField("active", true);
        Entity dict3 = new DefaultEntity(null);
        dict3.setField("name", "Dict3");
        dict3.setField("active", true);

        given(
                dataDefinitionService.get("qcadooModel", "dictionary").find().addOrder(SearchOrders.asc("name")).list()
                        .getEntities()).willReturn(newArrayList(dict1, dict2, dict3));

        // when
        Set<String> dictionaries = dictionaryService.getDictionaries();

        // then
        assertThat(dictionaries.size(), equalTo(3));
        assertThat(dictionaries, hasItems("Dict1", "Dict2", "Dict3"));
    }

    @Test
    public void shouldReturnSortedListOfDictionaryValues() throws Exception {
        // given
        Entity item1 = new DefaultEntity(null);
        item1.setField("name", "aaa");
        Entity item2 = new DefaultEntity(null);
        item2.setField("name", "ccc");
        Entity item3 = new DefaultEntity(null);
        item3.setField("name", "bbb");

        given(
                dataDefinitionService.get("qcadooModel", "dictionaryItem").find().createAlias("dictionary", "dictionary")
                        .add(SearchRestrictions.eq("dictionary.name", "dict")).add(SearchRestrictions.eq("active", true))
                        .addOrder(SearchOrders.asc("name")).list().getEntities()).willReturn(newArrayList(item1, item3, item2));

        // when
        Map<String, String> values = dictionaryService.getValues("dict", Locale.ENGLISH);

        // then
        assertThat(values.size(), equalTo(3));
        assertThat(values.get("aaa"), equalTo("aaa"));
        assertThat(values.get("bbb"), equalTo("bbb"));
        assertThat(values.get("ccc"), equalTo("ccc"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrownAnExceptionIfDictionaryNameIsNull() throws Exception {
        // when
        dictionaryService.getValues(null, Locale.ENGLISH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrownAnExceptionIfDictionaryNameIsEmpty() throws Exception {
        // when
        dictionaryService.getValues("", Locale.ENGLISH);
    }

}
