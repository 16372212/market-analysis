package com.market.stock.service;

public interface MessageService {

    void send(String body);

    void sendMd(String title, String body);

}
