package com.challenge.TeclabChallengue.controllers;
import com.challenge.TeclabChallengue.service.ZendeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/zendesk")
public class ZendeskController {
    private final ZendeskService zendeskService;

    @Autowired
    public ZendeskController(ZendeskService zendeskService) {
        this.zendeskService = zendeskService;
    }

    @GetMapping("/ticket/{ticketId}/comments")
    public String getTicketComments(@PathVariable long ticketId) throws IOException {
        return zendeskService.getTicketComments(ticketId).toString();
    }

    @PostMapping("/ticket/{ticketId}/comment")
    public void postCommentToTicket(@PathVariable long ticketId, @RequestBody String comment) throws IOException {
        zendeskService.postCommentToTicket(ticketId, comment);
    }
}

