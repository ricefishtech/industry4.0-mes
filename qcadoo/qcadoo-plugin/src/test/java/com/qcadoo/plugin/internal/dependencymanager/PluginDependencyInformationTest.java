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
package com.qcadoo.plugin.internal.dependencymanager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qcadoo.plugin.api.PluginDependencyInformation;
import com.qcadoo.plugin.api.Version;
import com.qcadoo.plugin.api.VersionOfDependency;

public class PluginDependencyInformationTest {

    private static final Logger LOG = LoggerFactory.getLogger(PluginDependencyInformationTest.class);

    PluginDependencyInformation dependencyInformation1;

    PluginDependencyInformation dependencyInformation2;

    PluginDependencyInformation dependencyInformation3;

    PluginDependencyInformation dependencyInformation4;

    PluginDependencyInformation dependencyInformation5;

    @Before
    public void init() {
        dependencyInformation1 = new PluginDependencyInformation("testPlugin1", new VersionOfDependency("[1,1.2.01)"));
        dependencyInformation2 = new PluginDependencyInformation("testPlugin2", new VersionOfDependency("2.2.01]"));
        dependencyInformation3 = new PluginDependencyInformation("testPlugin3", new VersionOfDependency("[1.2.10"));
        dependencyInformation4 = new PluginDependencyInformation("testPlugin4", new VersionOfDependency("(3.0.1,4.2]"));
        dependencyInformation5 = new PluginDependencyInformation("testPlugin5", new VersionOfDependency("[3.0.1,3.0.1]"));
    }

    @Test
    public void shouldThrowExceptionWhenWrongVersions() throws Exception {
        // given

        // when
        try {
            new PluginDependencyInformation("", new VersionOfDependency("[a1,1)"));
            Assert.fail();
        } catch (Exception e) {
            LOG.info("empty catch");
        }
        try {
            new PluginDependencyInformation("", new VersionOfDependency("[1,2s)"));
            Assert.fail();
        } catch (Exception e) {
            LOG.info("empty catch");
        }
        try {
            new PluginDependencyInformation("", new VersionOfDependency("[1.2.3.4,2s)"));
            Assert.fail();
        } catch (Exception e) {
            LOG.info("empty catch");
        }
        try {
            new PluginDependencyInformation("", new VersionOfDependency("[2,1.2.3.4)"));
            Assert.fail();
        } catch (Exception e) {
            LOG.info("empty catch");
        }
        try {
            new PluginDependencyInformation("", new VersionOfDependency("[1.1.1,1.1.0)"));
            Assert.fail();
        } catch (Exception e) {
            LOG.info("empty catch");
        }
        try {
            new PluginDependencyInformation("", new VersionOfDependency("(1.0.0,1]"));
            Assert.fail();
        } catch (Exception e) {
            LOG.info("empty catch");
        }

        // then
    }

    @Test
    public void shouldReturnTrueWhenVersionIsSattisfied() throws Exception {
        // given
        Version v1 = new Version("1.1");
        Version v2 = new Version("0.9");
        Version v3 = new Version("1.2.10");
        Version v4 = new Version("4.2.0");
        Version v5 = new Version("3.0.1");

        // when
        boolean res1 = dependencyInformation1.contains(v1);
        boolean res2 = dependencyInformation2.contains(v2);
        boolean res3 = dependencyInformation3.contains(v3);
        boolean res4 = dependencyInformation4.contains(v4);
        boolean res5 = dependencyInformation5.contains(v5);

        // then
        Assert.assertTrue(res1);
        Assert.assertTrue(res2);
        Assert.assertTrue(res3);
        Assert.assertTrue(res4);
        Assert.assertTrue(res5);
    }

    @Test
    public void shouldReturnTrueWhenVersionIsNotSattisfied() throws Exception {
        // given
        Version v1 = new Version("0.9");
        Version v2 = new Version("2.3");
        Version v3 = new Version("1.2.09");
        Version v4 = new Version("3.0.1");
        Version v5 = new Version("3.0.2");

        // when
        boolean res1 = dependencyInformation1.contains(v1);
        boolean res2 = dependencyInformation2.contains(v2);
        boolean res3 = dependencyInformation3.contains(v3);
        boolean res4 = dependencyInformation4.contains(v4);
        boolean res5 = dependencyInformation5.contains(v5);

        // then
        Assert.assertFalse(res1);
        Assert.assertFalse(res2);
        Assert.assertFalse(res3);
        Assert.assertFalse(res4);
        Assert.assertFalse(res5);
    }

}
