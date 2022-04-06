package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.protocol.model.TProMessage

interface MessageService {
    /**
     * 持久化消息内容
     */
    suspend fun save(tPro: TProMessage)
}