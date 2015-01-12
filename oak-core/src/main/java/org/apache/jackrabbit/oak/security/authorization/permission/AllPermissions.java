/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.security.authorization.permission;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.core.ImmutableRoot;
import org.apache.jackrabbit.oak.plugins.tree.impl.ImmutableTree;
import org.apache.jackrabbit.oak.spi.security.authorization.permission.RepositoryPermission;
import org.apache.jackrabbit.oak.spi.security.authorization.permission.TreePermission;
import org.apache.jackrabbit.oak.spi.security.privilege.PrivilegeConstants;

/**
 * Implementation of the {@code CompiledPermissions} interface that grants full
 * permission everywhere.
 */
public final class AllPermissions implements CompiledPermissions {

    private static final CompiledPermissions INSTANCE = new AllPermissions();

    private AllPermissions() {
    }

    public static CompiledPermissions getInstance() {
        return INSTANCE;
    }

    @Override
    public void refresh(@Nonnull ImmutableRoot root, @Nonnull String workspaceName) {
        // nop
    }

    @Override
    public RepositoryPermission getRepositoryPermission() {
        return RepositoryPermission.ALL;
    }

    @Override
    public TreePermission getTreePermission(@Nonnull ImmutableTree tree, @Nonnull TreePermission parentPermission) {
        return TreePermission.ALL;
    }

    @Override
    public boolean isGranted(ImmutableTree tree, PropertyState property, long permissions) {
        return true;
    }

    @Override
    public boolean isGranted(String path, long permissions) {
        return true;
    }

    @Override
    public Set<String> getPrivileges(ImmutableTree tree) {
        return Collections.singleton(PrivilegeConstants.JCR_ALL);
    }

    @Override
    public boolean hasPrivileges(ImmutableTree tree, String... privilegeNames) {
        return true;
    }
}
