package com.amazonaws.cloudcasino;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;
import software.amazon.cloudwatchlogs.emf.model.DimensionSet;
import software.amazon.cloudwatchlogs.emf.model.Unit;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.logging.LoggingUtils;
import software.amazon.lambda.powertools.metrics.Metrics;
import software.amazon.lambda.powertools.metrics.MetricsUtils;

import java.io.IOException;
import java.util.UUID;

public class RouletteHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final String RNG_SERVICE_URL = System.getProperty("RNG_SERVICE_URL");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LogManager.getLogger();
    private final MetricsLogger metricsLogger = MetricsUtils.metricsLogger();

    @Override
    @Logging(logEvent = true)
    @Metrics(namespace = "CloudCasino", service = "RouletteService")
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {

        RouletteRequest rouletteRequest;
        try {
            rouletteRequest = objectMapper.readValue(input.getBody(), RouletteRequest.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(403)
                    .build();
        }

        String betId = UUID.randomUUID().toString();
        LoggingUtils.appendKey("betId", betId);
        recordBetRequest(rouletteRequest, betId);

        try {
            String response = Request.Get(RNG_SERVICE_URL + "/rng?lower=0&upper=36")
                    .execute()
                    .returnContent()
                    .asString();
            log.info("RNG Response: " + response);
        } catch (IOException e) {
            log.error(e.getMessage());
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .build();
        }

        try {
            String body = objectMapper.writeValueAsString(new RouletteResponse(betId, 0));
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

    private void recordBetRequest(RouletteRequest rouletteRequest, String betId) {
        metricsLogger.putMetric("RouletteBet", 1, Unit.COUNT);
        metricsLogger.putDimensions(DimensionSet.of("Games", "Roulette"));
        metricsLogger.putProperty("PlayerId", rouletteRequest.getPlayerId());
        metricsLogger.putProperty("Stake", rouletteRequest.getStakeAmount());
        metricsLogger.putProperty("BetId", betId);
    }
}
