package com.isc.npsd.sharif.node;

import com.isc.npsd.common.util.MessagesUtil;
import com.isc.npsd.sharif.node.adapter.SharedObjectsContainer;
import com.isc.npsd.sharif.node.entities.NodeProperties;
import com.isc.npsd.sharif.node.services.Initializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        Map properties = new HashMap<>();
        properties.put(NodeProperties.SHARIF_DB_URL.name(), MessagesUtil.getConfigurationMessage("sharif.db.url"));
        properties.put(NodeProperties.SHARIF_DB_USER.name(), MessagesUtil.getConfigurationMessage("sharif.db.user"));
        properties.put(NodeProperties.SHARIF_DB_PASS.name(), MessagesUtil.getConfigurationMessage("sharif.db.password"));
        properties.put(NodeProperties.SHARIF_DB_RESOURCE.name(), MessagesUtil.getConfigurationMessage("sharif.db.resource.name"));
        properties.put(NodeProperties.SHARIF_DB_POOL_SIZE.name(), MessagesUtil.getConfigurationMessage("sharif.db.pool.size"));
        properties.put(NodeProperties.SHARIF_DB_CONNECTION_TIMEOUT.name(), MessagesUtil.getConfigurationMessage("sharif.db.connection.timeout"));
        properties.put(NodeProperties.SHARIF_HIBERNATE_SHOW_SQL.name(), MessagesUtil.getConfigurationMessage("sharif.hibernate.showsql"));
        properties.put(NodeProperties.SHARIF_MID_SEQ_CACHE_SIZE.name(), MessagesUtil.getConfigurationMessage("sharif.mid.sequence.cache.size"));
        properties.put(NodeProperties.SHARIF_MSG_ID_SEQ_CACHE_SIZE.name(), MessagesUtil.getConfigurationMessage("sharif.msg.id.sequence.cache.size"));
        properties.put(NodeProperties.SHARIF_BATCH_SIZE.name(), MessagesUtil.getConfigurationMessage("sharif.db.batch.size"));
        properties.put(NodeProperties.SHARIF_TRANSACTION_TIMEOUT.name(), MessagesUtil.getConfigurationMessage("sharif.transaction.timeout"));

        java.util.Properties props = new java.util.Properties();
        props.putAll(properties);
        SharedObjectsContainer.properties.putAll(props);
        Initializer initializer = (Initializer) AppConfig.getContext().getBean(Initializer.class);
        initializer.init();
        ServiceRunner.run(24264960L);
    }
}