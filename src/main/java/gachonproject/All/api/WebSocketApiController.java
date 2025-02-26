package gachonproject.All.api;

import gachonproject.All.service.UserCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class WebSocketApiController {

    @Autowired
    private UserCountService userCountService;



    @MessageMapping("/connect")
    public void handleConnect() {
        userCountService.incrementAndGetUserCount();

    }

    @MessageMapping("/disconnect")
    public void handleDisconnect() {
        userCountService.decrementAndGetUserCount();

    }


}