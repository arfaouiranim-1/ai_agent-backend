package com.example.aiagent.Engine;

import com.example.aiagent.entities.Node;
import com.example.aiagent.entities.NotificationNode;
import com.example.aiagent.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationNodeProcessor implements NodeProcessorStrategy {

    @Autowired(required = false)
    private NotificationService notificationService;

    @Override
    public boolean supports(Node node) {
        return node instanceof NotificationNode;
    }

    @Override
    public NodeResult process(Node node, Object input) {
        NotificationNode notifNode = (NotificationNode) node;

        String message = notifNode.getTemplate() != null && input != null
                ? notifNode.getTemplate().replace("{input}", input.toString())
                : notifNode.getTemplate();

        String channel  = notifNode.getChannel();
        String recipient = notifNode.getRecipient();

        try {
            if (notificationService != null && channel != null && recipient != null) {
                switch (channel.toUpperCase()) {
                    case "SMS"   -> notificationService.sendSms(recipient, message);
                    case "EMAIL" -> notificationService.sendEmail(recipient, "Notification", message);
                }
            }
        } catch (Exception e) {
            // Log l'erreur mais continue l'exécution
            System.err.println("[NotificationNode] Erreur envoi: " + e.getMessage());
        }

        return NodeResult.done(message);
    }
}