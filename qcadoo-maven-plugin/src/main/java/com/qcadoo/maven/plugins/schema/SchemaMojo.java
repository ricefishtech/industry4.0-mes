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
package com.qcadoo.maven.plugins.schema;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.maven.model.Profile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.FileUtils;

/**
 * @goal schema
 * @execute phase="package"
 */
public class SchemaMojo extends AbstractMojo {

    /**
     * @parameter default-value="${basedir}/../"
     * @readonly
     */
    private File baseDirectory;

    /**
     * @parameter default-value="${basedir}/target/schema/"
     * @readonly
     */
    private File workingDirectory;

    /**
     * @parameter default-value="${basedir}/target/schema/modules"
     * @readonly
     */
    private File modulesWorkingDirectory;

    /**
     * @parameter default-value="${basedir}/target/schema/common"
     * @readonly
     */
    private File commonWorkingDirectory;

    /**
     * @parameter default-value="${basedir}/target/schema/view/components"
     * @readonly
     */
    private File viewComponentsWorkingDirectory;

    /**
     * @parameter default-value="${basedir}/target/${project.artifactId}.zip"
     * @readonly
     */
    private File target;

    /**
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="zip"
     */
    private ZipArchiver zipArchiver;

    @Override
    @SuppressWarnings({ "unchecked" })
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            prepareWorkingDirectory();

            boolean ignoreWithoutVersion = isIgnoreWithoutVersionProfileActive();

            String version = prepareVersion();

            copySchemaFiles(ignoreWithoutVersion, version, "*/src/main/resources/com/qcadoo/*/*.xsd", workingDirectory);
            copySchemaFiles(ignoreWithoutVersion, version, "*/src/main/resources/com/qcadoo/*/modules/*.xsd",
                    modulesWorkingDirectory);
            copySchemaFiles(ignoreWithoutVersion, version, "*/src/main/resources/com/qcadoo/*/common/*.xsd",
                    commonWorkingDirectory);
            // Temporary
            copySchemaFiles(ignoreWithoutVersion, version, "*/src/main/resources/com/qcadoo/view/view/components/*.xsd",
                    viewComponentsWorkingDirectory);

            createArchive();
            registerArtifact();

        } catch (ArchiverException e) {
            throw new MojoExecutionException("Exception while creating zip", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Exception while creating zip", e);
        }
    }

    private void copySchemaFiles(final boolean ignoreWithoutVersion, final String version, final String includePath,
            final File targetDirectory) throws IOException {
        for (File file : (Collection<File>) FileUtils.getFiles(baseDirectory, includePath, null)) {
            FileUtils.copyFile(file, prepareDestinationFile(file, version, false, targetDirectory));
            if (!ignoreWithoutVersion) {
                FileUtils.copyFile(file, prepareDestinationFile(file, version, true, targetDirectory));
            }
        }
    }

    private File prepareDestinationFile(final File file, final String version, final boolean ignoreWithoutVersion,
            final File targetDirectory) {
        if (ignoreWithoutVersion) {
            return new File(targetDirectory, file.getName().replaceAll(".xsd", "") + "-" + version + ".xsd");
        } else {
            return new File(targetDirectory, file.getName());
        }
    }

    private String prepareVersion() {
        String version = project.getVersion();
        String[] splittedVersion = version.split("\\.");
        return splittedVersion[0] + "." + splittedVersion[1];
    }

    private void registerArtifact() {
        project.getArtifact().setFile(target);
    }

    @SuppressWarnings("unchecked")
    private boolean isIgnoreWithoutVersionProfileActive() {
        for (Profile profile : ((List<Profile>) project.getActiveProfiles())) {
            if ("ignoreWithoutVersion".equals(profile.getId())) {
                return true;
            }
        }
        return false;
    }

    private void createArchive() throws ArchiverException, IOException {
        zipArchiver.setDestFile(target);
        zipArchiver.setDirectoryMode(493);
        zipArchiver.setFileMode(420);

        DefaultFileSet fileSet = new DefaultFileSet();
        fileSet.setDirectory(workingDirectory);

        zipArchiver.addFileSet(fileSet);
        zipArchiver.createArchive();
    }

    private void prepareWorkingDirectory() throws IOException {
        FileUtils.forceMkdir(workingDirectory);

        FileUtils.cleanDirectory(workingDirectory);

        FileUtils.forceMkdir(modulesWorkingDirectory);
        FileUtils.forceMkdir(commonWorkingDirectory);
    }

}
