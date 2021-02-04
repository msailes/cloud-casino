package com.amazonaws.cloudcasino;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.logging.LoggingUtils;
import software.amazon.lambda.powertools.metrics.Metrics;

import java.security.SecureRandom;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class RNGHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final SecureRandom random = new SecureRandom();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LogManager.getLogger();

    @Override
    @Logging(logEvent = true)
    @Metrics(namespace = "CloudCasino", service = "RNGService")
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        String lower = input.getQueryStringParameters().get("lower");
        String upper = input.getQueryStringParameters().get("upper");

        IntStream ints = random.ints(parseInt(lower), parseInt(upper));
        int randomNumber = ints.iterator().nextInt();
        RandomNumberResponse randomNumberResponse = new RandomNumberResponse(randomNumber);
        logRandomNumber(lower, upper, randomNumber);

        try {
            String body = objectMapper.writeValueAsString(randomNumberResponse);
            log.info(body);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(body)
                    .build();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .build();
        }
    }

    private void logRandomNumber(String lower, String upper, int randomNumber) {
        LoggingUtils.appendKey("LowerBound", lower);
        LoggingUtils.appendKey("UpperBound", upper);
        log.info("RandomNumber: " + randomNumber);
    }
}
