package com.market.stock.service;

public interface MessageService {

    void send(String body) throws Exception;

    void sendMd(String title, String body) throws Exception;

}
