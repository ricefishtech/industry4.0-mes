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
package com.qcadoo.model;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;

public final class TransactionManagerStubbingUtil {

    private PlatformTransactionManager originalTransactionManager;

    public void mockAndStubTxManager() {
        final TransactionStatus txStatusMock = mock(TransactionStatus.class);
        given(txStatusMock.isRollbackOnly()).willReturn(false);
        final PlatformTransactionManager txManagerMock = mock(PlatformTransactionManager.class);
        given(txManagerMock.getTransaction((TransactionDefinition) Mockito.anyObject())).willReturn(txStatusMock);

        final AnnotationTransactionAspect txAspect = AnnotationTransactionAspect.aspectOf();
        originalTransactionManager = txAspect.getTransactionManager();
        txAspect.setTransactionManager(txManagerMock);
    }

    public void restorePrevTxManager() {
        final AnnotationTransactionAspect txAspect = AnnotationTransactionAspect.aspectOf();
        txAspect.setTransactionManager(originalTransactionManager);
    }

}
