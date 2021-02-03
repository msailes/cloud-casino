package com.amazonaws.cloudcasino;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class RNGHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final SecureRandom random = new SecureRandom();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        String lower = input.getQueryStringParameters().get("lower");
        String upper = input.getQueryStringParameters().get("upper");

        IntStream ints = random.ints(parseInt(lower), parseInt(upper));
        RandomNumberResponse randomNumberResponse = new RandomNumberResponse(ints.iterator().nextInt());

        try {
            String body = objectMapper.writeValueAsString(randomNumberResponse);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(body)
                    .build();
        } catch (JsonProcessingException e) {
            context.getLogger().log(e.getMessage());

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .build();
        }
    }
}
