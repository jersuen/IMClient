package com.jersuen.im.service.aidl;

interface IXmppConnection {

        /**建立连接*/
        boolean connect();

        /**登陆*/
        boolean login();

        /**关闭连接*/
        boolean disconnect();

}