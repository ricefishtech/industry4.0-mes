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
package com.qcadoo.plugin.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * Version of the required plugin - contains lower and upper boundaries with information if boundary is included.
 * 
 * @since 0.4.0
 */
public class VersionOfDependency {

    private static final Pattern PATTERN = Pattern
            .compile("((\\(|\\[)?(\\d+(.\\d+(.\\d+)?)?))??,?((\\d+(.\\d+(.\\d+)?)?)(\\)|\\])?)??");

    private final Version minVersion;

    private final boolean includeMinVersion;

    private final Version maxVersion;

    private final boolean includeMaxVersion;

    /**
     * Creates version from string. It contains one or two (separated by comma) version strings. It also can contain, at the
     * beginning and end of, parenthesis (means that boundary is excluded) or bracket (by default, means that boundary is
     * included).
     * 
     * Examples:
     * 
     * <ul>
     * <li>2.3.4: equals to 2.3.4</li>
     * <li>2.3: equals to 2.3.0</li>
     * <li>2: equals to 2.0.0</li>
     * <li>[2.3.4: greater than or equals to 2.3.4</li>
     * <li>(2.3.4: greater than 2.3.4</li>
     * <li>2.3.4]: lower than or equals to 2.3.4</li>
     * <li>2.3.4): lower than 2.3.4</li>
     * <li>[2.3.4,2.5): greater than or equals to 2.3.4 and lower that 2.5.0</li>
     * <li>(2.3.4,3]: greater than 2.3.4 and lower than or equals to 3.0.0</li>
     * <li>2,3: greater than or equals to 2.0.0 and lower than or equals to 3.0.0</li>
     * </ul>
     * 
     * @param version
     *            version
     * @throws IllegalStateException
     *             if string doesn't match the pattern
     * @throws IllegalStateException
     *             if any of the version is not valid
     * @throws IllegalStateException
     *             if min version is greater that max version
     * @throws IllegalStateException
     *             if range is empty
     * @throws NumberFormatException
     *             if any of the number is not valid integer
     * @see Version
     */
    public VersionOfDependency(final String version) {
        if (StringUtils.hasText(version)) {
            Matcher matcher = PATTERN.matcher(version);

            if (!matcher.matches()) {
                throw new IllegalStateException("Version " + version + " is invalid");
            }

            if (matcher.group(3) != null && matcher.group(2) == null && matcher.group(7) == null && matcher.group(10) == null) {
                minVersion = new Version(matcher.group(3));
                includeMinVersion = true;
                maxVersion = minVersion;
                includeMaxVersion = true;
            } else {
                if (matcher.group(3) == null) {
                    minVersion = null;
                } else {
                    minVersion = new Version(matcher.group(3));
                }

                if (matcher.group(7) == null) {
                    maxVersion = null;
                } else {
                    maxVersion = new Version(matcher.group(7));
                }

                includeMinVersion = !"(".equals(matcher.group(2));
                includeMaxVersion = !"(".equals(matcher.group(10));
            }

            if (this.minVersion != null && this.maxVersion != null) {
                int compareResult = this.minVersion.compareTo(this.maxVersion);
                if (compareResult > 0) {
                    throw new IllegalStateException("Version " + version + " is invalid: min version is larger than max version");
                } else if (compareResult == 0 && !(includeMinVersion && includeMaxVersion)) {
                    throw new IllegalStateException("Version " + version + " is invalid: range is empty");
                }
            }
        } else {
            minVersion = null;
            includeMinVersion = false;
            maxVersion = null;
            includeMaxVersion = false;
        }
    }

    /**
     * Returns minimum version of required plugin.
     * 
     * @return min version or null if not specified
     */
    public Version getMinVersion() {
        return minVersion;
    }

    /**
     * Returns maximum version of required plugin.
     * 
     * @return max version or null if not specified
     */
    public Version getMaxVersion() {
        return maxVersion;
    }

    /**
     * Returns true if version contains the given one.
     * 
     * @param version
     *            version
     * @return true if version contains the given one
     */
    public boolean contains(final Version version) {
        if (minVersion != null) {
            int minComparationResult = minVersion.compareTo(version);
            if (minComparationResult > 0) {
                return false;
            } else if (minComparationResult == 0 && !includeMinVersion) {
                return false;
            }
        }

        if (maxVersion != null) {
            int maxComparationResult = maxVersion.compareTo(version);
            if (maxComparationResult < 0) {
                return false;
            } else if (maxComparationResult == 0 && !includeMaxVersion) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (includeMaxVersion ? 1231 : 1237);
        result = prime * result + (includeMinVersion ? 1231 : 1237);
        result = prime * result + ((maxVersion == null) ? 0 : maxVersion.hashCode());
        result = prime * result + ((minVersion == null) ? 0 : minVersion.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof VersionOfDependency)) {
            return false;
        }
        VersionOfDependency other = (VersionOfDependency) obj;
        if (includeMaxVersion != other.includeMaxVersion) {
            return false;
        }
        if (includeMinVersion != other.includeMinVersion) {
            return false;
        }
        if (maxVersion == null) {
            if (other.maxVersion != null) {
                return false;
            }
        } else if (!maxVersion.equals(other.maxVersion)) {
            return false;
        }
        if (minVersion == null) {
            if (other.minVersion != null) {
                return false;
            }
        } else if (!minVersion.equals(other.minVersion)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (minVersion == null) {
            if (maxVersion == null) {
                return "0.0.0";
            } else {
                return maxVersion.toString() + (includeMaxVersion ? "]" : ")");
            }
        } else {
            if (maxVersion == null) {
                return (includeMinVersion ? "[" : "(") + minVersion.toString();
            } else {
                if (minVersion.equals(maxVersion)) {
                    return minVersion.toString();
                } else {
                    return (includeMinVersion ? "[" : "(") + minVersion.toString() + "," + maxVersion.toString()
                            + (includeMaxVersion ? "]" : ")");
                }
            }
        }
    }

}
