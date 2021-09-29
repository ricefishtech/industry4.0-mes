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
package com.qcadoo.commons.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * The decorator (wrapper) for AsyncTaskExecutor which wraps given tasks execution in try-catch statement. This is workaround for
 * missing functionality in Spring Framework 3.x (https://jira.springsource.org/browse/SPR-8995)
 * 
 * @since 1.2.1
 */
public class DefaultAsyncTaskExecutorWrapper implements AsyncTaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAsyncTaskExecutorWrapper.class);

    private final AsyncTaskExecutor asyncTaskExecutor;

    /**
     * Creates new instance with given underlying executor.
     * 
     * @param asyncTaskExecutor
     *            executor to be wrapped.
     */
    public DefaultAsyncTaskExecutorWrapper(final AsyncTaskExecutor asyncTaskExecutor) {
        this.asyncTaskExecutor = asyncTaskExecutor;
    }

    @Override
    public void execute(final Runnable task) {
        asyncTaskExecutor.execute(createWrappedTask(task));
    }

    @Override
    public void execute(final Runnable task, final long startTimeout) {
        asyncTaskExecutor.execute(createWrappedTask(task), startTimeout);
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return asyncTaskExecutor.submit(createWrappedTask(task));
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return asyncTaskExecutor.submit(createWrappedTask(task));
    }

    /**
     * This method is performed whenever executed task throws uncaught exception.
     * 
     * @param ex
     *            exception thrown by task
     */
    protected void onException(final Exception ex) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("An unexpected exception occured", ex);
        }
    }

    private <T> Callable<T> createWrappedTask(final Callable<T> task) {
        return new Callable<T>() {

            @Override
            public T call() throws Exception {
                try {
                    return task.call();
                } catch (Exception ex) {
                    onException(ex);
                    throw ex;
                }
            }
        };
    }

    private Runnable createWrappedTask(final Runnable task) {
        return new Runnable() {

            @Override
            public void run() {
                try {
                    task.run();
                } catch (Exception ex) {
                    onException(ex);
                    throw new RuntimeException(ex);
                }
            }
        };
    }

}
