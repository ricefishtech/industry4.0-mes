@REM
@REM ***************************************************************************
@REM Copyright (c) 2010 Qcadoo Limited
@REM Project: Qcadoo Framework
@REM Version: 1.4
@REM
@REM This file is part of Qcadoo.
@REM
@REM Qcadoo is free software; you can redistribute it and/or modify
@REM it under the terms of the GNU Affero General Public License as published
@REM by the Free Software Foundation; either version 3 of the License,
@REM or (at your option) any later version.
@REM
@REM This program is distributed in the hope that it will be useful,
@REM but WITHOUT ANY WARRANTY; without even the implied warranty
@REM of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
@REM See the GNU Affero General Public License for more details.
@REM
@REM You should have received a copy of the GNU Affero General Public License
@REM along with this program; if not, write to the Free Software
@REM Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
@REM ***************************************************************************
@REM

set "JAVA_OPTS=%JAVA_OPTS% -DQCADOO_CONF="%CATALINA_HOME%/qcadoo""
set "JAVA_OPTS=%JAVA_OPTS% -DQCADOO_PLUGINS_PATH="%CATALINA_HOME%/webapps/ROOT/WEB-INF/lib""
set "JAVA_OPTS=%JAVA_OPTS% -DQCADOO_PLUGINS_TMP_PATH="%CATALINA_HOME%/webapps/ROOT/tmp""
set "JAVA_OPTS=%JAVA_OPTS% -DQCADOO_WEBAPP_PATH="%CATALINA_HOME%/webapps/ROOT""
set "JAVA_OPTS=%JAVA_OPTS% -DQCADOO_LOG="%CATALINA_HOME%/logs""
set "JAVA_OPTS=%JAVA_OPTS% -javaagent:%CATALINA_HOME%/lib/aspectjweaver-1.8.2.jar"
set "JAVA_OPTS=%JAVA_OPTS% -XX:MaxMetaspaceSize=256m"
set "CATALINA_OPTS=%CATALINA_OPTS% -server -Djava.awt.headless=true"