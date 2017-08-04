/*
 * Copyright (C) 2011 Tatsuhiro Tsujikawa
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.sourceforge.argparse4j.inf;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class holds attributes added by
 * {@link ArgumentParser#parseArgs(String[])}.
 * </p>
 * <p>
 * It is just a wrapper of {@link Map} object which stores actual attributes.
 * {@link Map} object can be retrieved using {@link #getAttrs()}. This class
 * provides several shortcut methods to get attribute values.
 * {@link #toString()} provides nice textual representation of stored
 * attributes.
 * </p>
 */
public class Namespace {

    private Map<String, Object> attrs_;

    /**
     * Construct this object using given {@code attrs}.
     * 
     * @param attrs
     *            The attributes
     */
    public Namespace(Map<String, Object> attrs) {
        attrs_ = attrs;
    }

    /**
     * Returns attribute with given attribute name {@code dest}.
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value, or {@code null} if it is not found.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String dest) {
        return (T) attrs_.get(dest);
    }

    /**
     * Returns attribute as {@link String} with given attribute name
     * {@code dest}. This method calls {@link Object#toString()} method of a
     * found object to get string representation unless object is {@code null}.
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value casted to {@link String}, or {@code null} if
     *         is not found.
     */
    public String getString(String dest) {
        Object o = get(dest);

        if(o == null) {
            return null;
        }

        return o.toString();
    }

    /**
     * Returns attribute as {@link Byte} with given attribute name {@code dest}.
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value casted to {@link Byte}, or {@code null} if it
     *         is not found.
     */
    public Byte getByte(String dest) {
        return get(dest);
    }

    /**
     * Returns attribute as {@link Short} with given attribute name {@code dest}
     * .
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value casted to {@link Short}, or {@code null} if
     *         it is not found.
     */
    public Short getShort(String dest) {
        return get(dest);
    }

    /**
     * Returns attribute as {@link Integer} with given attribute name
     * {@code dest}.
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value casted to {@link Integer}, or {@code null} if
     *         it is not found.
     */
    public Integer getInt(String dest) {
        return get(dest);
    }

    /**
     * Returns attribute as {@link Long} with given attribute name {@code dest}.
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value casted to {@link Long}, or {@code null} if it
     *         is not found.
     */
    public Long getLong(String dest) {
        return get(dest);
    }

    /**
     * Returns attribute as {@link Float} with given attribute name {@code dest}
     * .
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value casted to {@link Float}, or {@code null} if
     *         it is not found.
     */
    public Float getFloat(String dest) {
        return get(dest);
    }

    /**
     * Returns attribute as {@link Double} with given attribute name
     * {@code dest}.
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value casted to {@link Double}, or {@code null} if
     *         it is not found.
     */
    public Double getDouble(String dest) {
        return get(dest);
    }

    /**
     * Returns attribute as {@link Boolean} with given attribute name
     * {@code dest}.
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value casted to {@link Boolean}, or {@code null} if
     *         it is not found.
     */
    public Boolean getBoolean(String dest) {
        return get(dest);
    }

    /**
     * Returns attribute as {@link List} with given attribute name {@code dest}.
     * 
     * @param dest
     *            The attribute name
     * @return The attribute value casted to {@link List}, or {@code null} if it
     *         is not found.
     */
    public <E> List<E> getList(String dest) {
        return get(dest);
    }

    /**
     * <p>
     * Returns {@link Map} object holding attribute values.
     * </p>
     * <p>
     * The application code can freely use returned object.
     * </p>
     * 
     * @return {@link Map} object holding attribute values.
     */
    public Map<String, Object> getAttrs() {
        return attrs_;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append("(");
        for (Map.Entry<String, Object> entry : attrs_.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue())
                    .append(", ");
        }
        if (!attrs_.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.append(")").toString();
    }

}
