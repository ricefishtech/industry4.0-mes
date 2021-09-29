#
# ***************************************************************************
# Copyright (c) 2010 Qcadoo Limited
# Project: Qcadoo Framework
# Version: 1.4
#
# This file is part of Qcadoo.
#
# Qcadoo is free software; you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published
# by the Free Software Foundation; either version 3 of the License,
# or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty
# of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
# ***************************************************************************
#

JAVA_OPTS="$JAVA_OPTS -DQCADOO_CONF=$CATALINA_HOME/qcadoo"
JAVA_OPTS="$JAVA_OPTS -DQCADOO_PLUGINS_PATH=$CATALINA_HOME/webapps/ROOT/WEB-INF/lib"
JAVA_OPTS="$JAVA_OPTS -DQCADOO_PLUGINS_TMP_PATH=$CATALINA_HOME/webapps/ROOT/tmp"
JAVA_OPTS="$JAVA_OPTS -DQCADOO_WEBAPP_PATH=$CATALINA_HOME/webapps/ROOT"
JAVA_OPTS="$JAVA_OPTS -DQCADOO_LOG=$CATALINA_HOME/logs"
JAVA_OPTS="$JAVA_OPTS -javaagent:$CATALINA_HOME/lib/aspectjweaver-1.8.2.jar"
JAVA_OPTS="$JAVA_OPTS -XX:MaxMetaspaceSize=256m"
CATALINA_OPTS="$CATALINA_OPTS -server -Djava.awt.headless=true"
CATALINA_PID="$CATALINA_HOME/catalina.pid"
