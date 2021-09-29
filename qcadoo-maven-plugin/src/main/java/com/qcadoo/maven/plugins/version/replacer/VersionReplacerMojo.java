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
package com.qcadoo.maven.plugins.version.replacer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author mady
 * @goal version-replace
 * @phase validate
 */
public class VersionReplacerMojo extends AbstractMojo {

    /**
     * 
     * @parameter default-value="${project}"
     * @required
     * @readonly
     * 
     */
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String version = project.getVersion();
        String trimmedVersion = "";

        Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+");
        Matcher matcher = pattern.matcher(version);
        if (matcher.find()) {
            trimmedVersion = matcher.group();
        }

        project.getProperties().setProperty("version.replacer", trimmedVersion);
    }

    public void setProject(final MavenProject project) {
        this.project = project;
    }

}
