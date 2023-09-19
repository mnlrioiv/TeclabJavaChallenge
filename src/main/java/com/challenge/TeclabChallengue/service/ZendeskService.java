package com.challenge.TeclabChallengue.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Service
public class ZendeskService {
    private final String zendeskBaseUrl;
    private final String zendeskUser;
    private final String zendeskPassword;

    public ZendeskService(@Value("${zendesk.base-url}") String zendeskBaseUrl,
                          @Value("${zendesk.username}") String zendeskUser,
                          @Value("${zendesk.password}") String zendeskPassword)
    {
        this.zendeskBaseUrl = zendeskBaseUrl;
        this.zendeskUser = zendeskUser;
        this.zendeskPassword = zendeskPassword;
    }

    /**
     * Obtiene los comentarios de un ticket usanbdi la API de Zendesk.
     *
     * @param ticketId Identificdor del ticket.
     * @return La Response del metodo get tickets
     * @throws IOException si existe un error cuando se realiza el request
     */
    public Response getTicketComments(long ticketId) throws IOException {
        String zendeskApiUrl = zendeskBaseUrl + "/api/v2/tickets/" + ticketId + "/comments";

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(zendeskApiUrl)
                .newBuilder()
                .addQueryParameter("include", "")
                .addQueryParameter("include_inline_images", "");

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", Credentials.basic(zendeskUser, zendeskPassword))
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            System.out.println("Response: " + responseBody);
        } else {
            System.out.println("Request failed with code: " + response.code());
        }
        return response;
    }
    /*
    De acuerdo con la documentación:
    Ticket comments, including voice comments, are created with the Tickets API, not the Ticket Comments API described in this document. The Tickets Comments API has no endpoint to create comments.
    Ticket comments are created by including a comment object in the ticket object when creating or updating the ticket.
    Se usa la modificación del ticket para agregar un comentario
     */

    /**
     * Genera un comentario en un ticket existente en Zendesk.
     *
     * @param ticketId Identificador del ticket de Zendesk
     * @param comment  Nuevo comentario que se agregara en el ticket
     * @throws IOException Se presenta un error al actualizar el ticket mediante el POST
     */
    public void postCommentToTicket(long ticketId, String comment) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String apiUrl = zendeskBaseUrl + "/api/v2/tickets/" + ticketId;

        //Esto se podría hacer de otra manera pero se respeta el código provisto por Zendesk
        //https://developer.zendesk.com/api-reference/ticketing/tickets/tickets/#code-samples-6

        String originalBody="""
        {
          \"ticket\": {
            \"comment\": {
              \"body\": \"Thanks for choosing Acme Jet Motors.\"
            },
         }
        }""";

        String modifiedBody = originalBody.replace("Thanks for choosing Acme Jet Motors.", comment);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), modifiedBody);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(apiUrl).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .method("PUT", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", Credentials.basic(zendeskUser, zendeskPassword))
                .build();
        Response response = client.newCall(request).execute();

    }
}

