package com.market.stock.service.impl;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.market.stock.model.po.Message;
import com.market.stock.model.po.Robot;
import com.market.stock.service.MessageService;
import com.market.stock.service.RobotService;
import com.market.stock.util.HttpUtil;
import com.market.stock.util.StockConsts;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private RobotService robotService;

    @Autowired
    private CloseableHttpClient httpClient;

    @Override
    public void send(String body) throws Exception {
        Robot robot = robotService.getSystem();
        if (robot == null) {
            MessageServiceImpl.logger.error("system robot not config");
            return;
        }
        String target = robot.getWebhook();
        sendDingding(null, body, getUrl(target), DingDingMessageType.Text);
    }

    @Override
    public void sendMd(String title, String body) throws Exception {
        Robot robot = robotService.getSystem();
        if (robot == null) {
            MessageServiceImpl.logger.error("system robot not config");
            return;
        }
        String target = robot.getWebhook();
        sendDingding(title, body, getUrl(target), DingDingMessageType.Markdown);
    }

    private String getUrl(String target) throws Exception {
        Long timestamp = System.currentTimeMillis();
        String secret = "SEC1e17f8404aa8150dedf536f28ccf203e02c023bf0aaa189aeb911ec2cfffd111";
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        Base64.Encoder encoder = Base64.getEncoder();
        String sign = URLEncoder.encode(new String(encoder.encodeToString(signData)),"UTF-8");
        return target + "&timestamp=" + timestamp.toString() + "&sign=" + sign;
    }

    private void sendDingding(String title, String body, String target, DingDingMessageType type) {
        try {
            Message message = new Message(StockConsts.MessageType.Message.value(), target, body, new Date());
            Map<String, Object> params = type == DingDingMessageType.Text
                    ? buildTextMessageParams(message.getBody())
                    : buildMarkdownMessageParams(title, message.getBody());
            MessageServiceImpl.logger.info("send message content: {}", params);
            MessageServiceImpl.logger.info("send message message: {}", message.getTarget());
            String result = HttpUtil.sendPostJson(httpClient, message.getTarget(), params);
            MessageServiceImpl.logger.info("send message result: {}", result);
        } catch (Exception e) {
            MessageServiceImpl.logger.error("send message error", e);
        }
    }

    private Map<String, Object> buildTextMessageParams(String content) {
        HashMap<String, Object> text = new HashMap<>();
        text.put("content", content);

        HashMap<String, Object> params = new HashMap<>();
        params.put("msgtype", "text");
        params.put("text", text);

        return params;
    }

    private Map<String, Object> buildMarkdownMessageParams(String title, String text) {
        HashMap<String, Object> markdown = new HashMap<>();
        markdown.put("title", title);
        markdown.put("content", text);
        markdown.put("text", text);

        HashMap<String, Object> params = new HashMap<>();
        params.put("msgtype", "markdown");
        params.put("markdown", markdown);

        return params;
    }

    private enum DingDingMessageType {
        Text, Markdown
    }

}
