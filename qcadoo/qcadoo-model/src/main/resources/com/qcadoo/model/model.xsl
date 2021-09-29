<?xml version="1.0" encoding="UTF-8"?>
<!--

    ***************************************************************************
    Copyright (c) 2010 Qcadoo Limited
    Project: Qcadoo Framework
    Version: 1.4

    This file is part of Qcadoo.

    Qcadoo is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation; either version 3 of the License,
    or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty
    of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    ***************************************************************************

-->
<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:qcd="http://schema.qcadoo.org/model"
	xsi:schemaLocation="http://schema.qcadoo.org/model http://schema.qcadoo.org/model.xsd"
	exclude-result-prefixes="qcd xsi">

	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		doctype-system="http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd"
		doctype-public="-//Hibernate/Hibernate Mapping DTD 3.0//EN" />

	<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
	<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

	<xsl:template name="entityName">
		<xsl:param name="modelName" />
		<xsl:param name="pluginName" />
		<xsl:attribute name="class">
			<xsl:value-of
			select="concat('com.qcadoo.model.beans.', $pluginName, '.', translate(substring($pluginName, 1, 1),  $smallcase, $uppercase), substring($pluginName, 2), translate(substring($modelName, 1, 1),  $smallcase, $uppercase), substring($modelName, 2))" />
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="joinTableAttribute">
		<xsl:param name="firstModel" />
		<xsl:param name="secondModel" />
		<xsl:choose>
			<xsl:when test="compare($firstModel, $secondModel) &lt; 0">
				<xsl:attribute name="table">
					<xsl:value-of select="concat('joinTable_', $firstModel, '_', $secondModel)" />
				</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="table">
					<xsl:value-of select="concat('joinTable_', $secondModel, '_', $firstModel)" />
				</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="precision">
		<xsl:param name="unscaledValue" />

		<xsl:attribute name="precision">
			<xsl:value-of select="$unscaledValue" />
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="precisionAndScale">
		<xsl:param name="unscaledValue" />
		<xsl:param name="scale" />

		<xsl:attribute name="precision">
			<xsl:value-of select="$unscaledValue + $scale" />
		</xsl:attribute>
		<xsl:attribute name="scale">
			<xsl:value-of select="$scale" />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="//qcd:model">
		<hibernate-mapping>
			<class>
				<xsl:variable name="table_name">
					<xsl:value-of select="concat(/qcd:model/@plugin, '_', @name)" />
				</xsl:variable>
				<xsl:attribute name="table">
				    <xsl:value-of select="$table_name" />
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of
					select="concat('com.qcadoo.model.beans.', @plugin, '.', translate(substring(@plugin, 1, 1),  $smallcase, $uppercase), substring(@plugin, 2), translate(substring(@name, 1, 1),  $smallcase, $uppercase), substring(@name, 2))" />
				</xsl:attribute>
				<xsl:if test="@cacheable='true'">
					<cache usage="read-write"/>
				</xsl:if>
				<id column="id" name="id" type="long">
					<generator class="sequence">
						<param name="sequence">
							<xsl:value-of select="substring(concat(/qcd:model/@plugin, '_', @name, '_id_seq'),0, 64)" />
						</param>
					</generator>
				</id>
				<xsl:if test="@versionable='true'">
					<version column="entityVersion" name="entityVersion" type="long"></version>
				</xsl:if>
				<xsl:apply-templates />
				<xsl:if test="@activable='true'">
					<property>
						<xsl:attribute name="type">boolean</xsl:attribute>
						<xsl:attribute name="name">active</xsl:attribute>
						<xsl:attribute name="not-null">true</xsl:attribute>
						<column>
							<xsl:attribute name="name">active</xsl:attribute>
							<xsl:attribute name="default">true</xsl:attribute>
						</column>
					</property>
				</xsl:if>
				<xsl:if test="@auditable='true'">
					<property>
						<xsl:attribute name="type">timestamp</xsl:attribute>
						<xsl:attribute name="name">createDate</xsl:attribute>
					</property>
					<property>
						<xsl:attribute name="type">timestamp</xsl:attribute>
						<xsl:attribute name="name">updateDate</xsl:attribute>
					</property>
					<property>
						<xsl:attribute name="type">string</xsl:attribute>
						<xsl:attribute name="name">createUser</xsl:attribute>
					</property>
					<property>
						<xsl:attribute name="type">string</xsl:attribute>
						<xsl:attribute name="name">updateUser</xsl:attribute>
					</property>
				</xsl:if>
				<xsl:if test="@insertable='false'">
					<sql-insert>
						<xsl:value-of
							select="concat('insert must not be executed on ', $table_name)" />
					</sql-insert>
				</xsl:if>
				<xsl:if test="@updatable='false'">
					<sql-update>
						<xsl:value-of
							select="concat('update must not be executed on ', $table_name)" />
					</sql-update>
				</xsl:if>
				<xsl:if test="@deletable='false'">
					<sql-delete>
						<xsl:value-of
							select="concat('delete must not be executed on ', $table_name)" />
					</sql-delete>
				</xsl:if>
			</class>
		</hibernate-mapping>
	</xsl:template>

	<xsl:template name="property">
		<xsl:attribute name="name">
			    <xsl:value-of select="@name" />
			</xsl:attribute>
		<xsl:attribute name="not-null">
				<xsl:choose>
					<xsl:when test="@required='true'">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:when test="local-name()='priority'">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		<xsl:if test="@unique='true'">
			<xsl:attribute name="unique">true</xsl:attribute>
		</xsl:if>
		<column>
			<xsl:attribute name="name">
				<xsl:choose>
				<xsl:when test="local-name()='belongsTo'">
			    	<xsl:value-of select="concat(@name, '_id')" />
			    </xsl:when>
			    <xsl:otherwise>
			    	<xsl:value-of select="@name" />
			    </xsl:otherwise>
			    </xsl:choose>
			</xsl:attribute>
			<xsl:if test="@name='tenantId'">
				<xsl:attribute name="index">
					<xsl:value-of select="concat(concat(/qcd:model/@plugin, '_', /qcd:model/@name), '_tenantId')" />
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="./qcd:validatesLength[@is]">
					<xsl:attribute name="length">
							<xsl:value-of select="./qcd:validatesLength/@is" />
						</xsl:attribute>
				</xsl:when>
				<xsl:when test="./qcd:validatesLength[@max]">
					<xsl:attribute name="length">
							<xsl:value-of select="./qcd:validatesLength/@max" />
						</xsl:attribute>
				</xsl:when>
			</xsl:choose>

			<xsl:choose>
				<xsl:when test="local-name()='integer'">
					<xsl:call-template name="precision">
						<xsl:with-param name="unscaledValue">
							<xsl:choose>
								<xsl:when test="./qcd:validatesUnscaledValue[@is]">
									<xsl:value-of select="./qcd:validatesUnscaledValue/@is" />
								</xsl:when>
								<xsl:when test="./qcd:validatesUnscaledValue[@max]">
									<xsl:value-of select="./qcd:validatesUnscaledValue/@max" />
								</xsl:when>
								<xsl:otherwise>10</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="local-name()='decimal'">
					<xsl:call-template name="precisionAndScale">
						<xsl:with-param name="unscaledValue">
							<xsl:choose>
								<xsl:when test="./qcd:validatesUnscaledValue[@is]">
									<xsl:value-of select="./qcd:validatesUnscaledValue/@is" />
								</xsl:when>
								<xsl:when test="./qcd:validatesUnscaledValue[@max]">
									<xsl:value-of select="./qcd:validatesUnscaledValue/@max" />
								</xsl:when>
								<xsl:otherwise>7</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="scale">
							<xsl:choose>
								<xsl:when test="./qcd:validatesScale[@is]">
									<xsl:value-of select="./qcd:validatesScale/@is" />
								</xsl:when>
								<xsl:when test="./qcd:validatesScale[@max]">
									<xsl:value-of select="./qcd:validatesScale/@max" />
								</xsl:when>
								<xsl:otherwise>5</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="@default">
				<xsl:attribute name="default">
				    	<xsl:value-of select="concat(&quot;'&quot;, @default, &quot;'&quot;)" />
					</xsl:attribute>
			</xsl:if>
		</column>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:integer[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">integer</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template match="//qcd:model/qcd:fields/qcd:priority">
		<property>
			<xsl:attribute name="type">integer</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:string[not(@expression) and not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">string</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:password[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">string</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:file[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">string</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:text[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">text</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:decimal[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">big_decimal</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:datetime[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">timestamp</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:date[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">date</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:boolean[not(@persistent='false') and not(@name='active' and /qcd:model/@activable='true')]">
		<property>
			<xsl:attribute name="type">boolean</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:enum[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">string</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:dictionary[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">string</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:long[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">long</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:short[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">short</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:float[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">float</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:double[not(@persistent='false')]">
		<property>
			<xsl:attribute name="type">double</xsl:attribute>
			<xsl:call-template name="property" />
		</property>
	</xsl:template>

	<xsl:template
		match="//qcd:model/qcd:fields/qcd:hasMany[not(@persistent='false')] | //qcd:model/qcd:fields/qcd:tree[not(@persistent='false')]">
		<set>
			<xsl:attribute name="inverse">
				<xsl:text>true</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="cascade">
				<xsl:choose>
					<xsl:when test="@cascade='delete'">delete</xsl:when>
					<xsl:otherwise>none</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="lazy">true</xsl:attribute>
			<xsl:attribute name="name">
			    <xsl:value-of select="@name" />
			</xsl:attribute>
			<key>
				<xsl:attribute name="column">
            	<xsl:value-of select="concat(@joinField, '_id')" />
            </xsl:attribute>
			</key>
			<one-to-many>
				<xsl:choose>
					<xsl:when test="@plugin">
						<xsl:call-template name="entityName">
							<xsl:with-param name="modelName" select="@model" />
							<xsl:with-param name="pluginName" select="@plugin" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="entityName">
							<xsl:with-param name="modelName" select="@model" />
							<xsl:with-param name="pluginName" select="/qcd:model/@plugin" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</one-to-many>
		</set>
	</xsl:template>

    <xsl:template
            match="//qcd:model/qcd:fields/qcd:manyToMany[not(@persistent='false')]">
        <set>
            <xsl:attribute name="cascade">
                <xsl:choose>
                    <xsl:when test="@cascade='delete'">delete</xsl:when>
                    <xsl:otherwise>none</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="lazy">true</xsl:attribute>
            <xsl:attribute name="name">
                <xsl:value-of select="@name"/>
            </xsl:attribute>
            <xsl:call-template name="joinTableAttribute">
                <xsl:with-param name="firstModel" select="/qcd:model/@name"/>
                <xsl:with-param name="secondModel" select="@model"/>
            </xsl:call-template>
            <key>
                <!--
                <xsl:attribute name="column">
                    <xsl:choose>
                        <xsl:when test="@columnName">
                            <xsl:value-of select="@columnName"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat(/qcd:model/@name, '_id')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                -->
                <xsl:attribute name="column">
                    <xsl:value-of select="concat(/qcd:model/@name, '_id')"/>
                </xsl:attribute>
            </key>
            <many-to-many>
                <xsl:attribute name="column">
                    <xsl:choose>
                        <xsl:when test="@columnName">
                            <xsl:value-of select="concat(@columnName, '_id')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat(@model, '_id')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:choose>
                    <xsl:when test="@plugin">
                        <xsl:call-template name="entityName">
                            <xsl:with-param name="modelName" select="@model"/>
                            <xsl:with-param name="pluginName" select="@plugin"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="entityName">
                            <xsl:with-param name="modelName" select="@model"/>
                            <xsl:with-param name="pluginName" select="/qcd:model/@plugin"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </many-to-many>
        </set>
    </xsl:template>
	<xsl:template
		match="//qcd:model/qcd:fields/qcd:belongsTo[not(@persistent='false')]">
		<many-to-one>
			<xsl:choose>
				<xsl:when test="@plugin">
					<xsl:call-template name="entityName">
						<xsl:with-param name="modelName" select="@model" />
						<xsl:with-param name="pluginName" select="@plugin" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="entityName">
						<xsl:with-param name="modelName" select="@model" />
						<xsl:with-param name="pluginName" select="/qcd:model/@plugin" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:attribute name="cascade">
				<xsl:choose>
					<xsl:when test="@cascade='delete'">delete</xsl:when>
					<xsl:otherwise>none</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="lazy">
				<xsl:choose>
					<xsl:when test="@lazy='false'">false</xsl:when>
					<xsl:otherwise>proxy</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:call-template name="property" />
		</many-to-one>
	</xsl:template>

</xsl:stylesheet>