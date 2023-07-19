package com.zdk.mai.common.rabbit.constant;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/21 19:36
 */
public class RabbitConstants {

    /**
     * MySQL 用户同步队列名称
     */
    public static final String SYNC_DB_QUEUE_USER = "SYNC-DB-QUEUE-USER";

    /**
     * MySQL 贴子同步队列名称
     */
    public static final String SYNC_DB_QUEUE_POST = "SYNC-DB-QUEUE-POST";

    /**
     * MySQL 评论同步队列名称
     */
    public static final String SYNC_DB_QUEUE_COMMENT = "SYNC-DB-QUEUE-COMMENT";

    /**
     * ES同步队列名称
     */
    public static final String SYNC_ES_QUEUE_USER = "SYNC-ES-QUEUE-USER";

    /**
     * ES同步队列名称
     */
    public static final String SYNC_ES_QUEUE_POST = "SYNC-ES-QUEUE-POST";

    /**
     * ES同步队列名称
     */
    public static final String SYNC_ES_QUEUE_COMMENT = "SYNC-ES-QUEUE-COMMENT";

    /**
     * 消息通知模块队列
     */
    public static final String MESSAGE_DB_QUEUE = "MESSAGE-DB-QUEUE";


    /**
     * 同步数据交换机名称
     */
    public static final String SYNC_DATA_EXCHANGE = "SYNC-DATA-EXCHANGE";

    /**
     * 同步MySQL user routingKey
     */
    public static final String SYNC_DB_ROUTING_KEY_USER = "sync.db.user";

    /**
     * 同步MySQL user routingKey
     */
    public static final String SYNC_DB_ROUTING_KEY_POST = "sync.db.post";

    /**
     * 同步MySQL user routingKey
     */
    public static final String SYNC_DB_ROUTING_KEY_COMMENT = "sync.db.comment";

    /**
     * 同步ES routingKey
     */
    public static final String SYNC_ES_ROUTING_KEY_USER = "sync.es.user";

    /**
     * 同步ES routingKey
     */
    public static final String SYNC_ES_ROUTING_KEY_POST = "sync.es.post";

    /**
     * 同步ES routingKey
     */
    public static final String SYNC_ES_ROUTING_KEY_COMMENT = "sync.es.comment";

    /**
     * 消息通知 routingKey
     */
    public static final String MESSAGE_DB_ROUTING_KEY = "sync.db.message";






}
