package com.isc.npsd.sharif.node;


import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
/**
 * Created by A_Ahmady on 06/12/2016.
 */
public class JTAPlatform extends AbstractJtaPlatform {

    public static TransactionManager transactionManager;
    public static UserTransaction userTransaction;
    @Override
    protected TransactionManager locateTransactionManager() {
        return transactionManager;
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return userTransaction;
    }
}