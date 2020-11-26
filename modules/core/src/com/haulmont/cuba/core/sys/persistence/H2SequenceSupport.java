/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.cuba.core.sys.persistence;

import com.haulmont.bali.util.Preconditions;

/**
 * @author zhoupan
 */
public class H2SequenceSupport implements SequenceSupport {

    @Override
    public String sequenceExistsSql(String sequenceName) {
        return String.format("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES  WHERE SEQUENCE_NAME='%s'", sequenceName);
    }


    @Override
    public String createSequenceSql(String sequenceName, long startValue, long increment) {
        Preconditions.checkNotNullArgument(sequenceName, "sequenceName is null");
        return String.format("CREATE SEQUENCE %s START WITH %s INCREMENT BY %s MINVALUE %s", sequenceName, startValue, increment, 0);
    }

    @Override
    public String modifySequenceSql(String sequenceName, long startWith) {
        Preconditions.checkNotNullArgument(sequenceName, "sequenceName is null");
        return String.format("ALTER SEQUENCE %S RESTART WITH %S", sequenceName, startWith);
    }

    @Override
    public String deleteSequenceSql(String sequenceName) {
        Preconditions.checkNotNullArgument(sequenceName, "sequenceName is null");
        return String.format("DROP SEQUENCE %s", sequenceName);
    }

    @Override
    public String getNextValueSql(String sequenceName) {
        Preconditions.checkNotNullArgument(sequenceName, "sequenceName is null");
        return String.format("SELECT NEXTVAL ('%s')", sequenceName);
    }

    @Override
    public String getCurrentValueSql(String sequenceName) {
        Preconditions.checkNotNullArgument(sequenceName, "sequenceName is null");
        return String.format("SELECT CURRVAL ('%s')", sequenceName);
    }
}