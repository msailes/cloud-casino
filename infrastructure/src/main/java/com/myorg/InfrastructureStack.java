package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpApiProps;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.PayloadFormatVersion;
import software.amazon.awscdk.services.apigatewayv2.integrations.LambdaProxyIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.LambdaProxyIntegrationProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

public class InfrastructureStack extends Stack {
    public InfrastructureStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfrastructureStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function rngService = new Function(this, "RngService", FunctionProps.builder()
                .runtime(Runtime.JAVA_8_CORRETTO)
                .code(Code.fromAsset("../software/rng-service/target/rng-service.jar"))
                .handler("com.amazonaws.cloudcasino.RNGHandler")
                .memorySize(1024)
                .timeout(Duration.seconds(10))
                .build());

        HttpApi httpApi = new HttpApi(this, "cloud-casino-api", HttpApiProps.builder()
                .apiName("cloud-casino-api")
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/rng")
                .methods(singletonList(HttpMethod.GET))
                .integration(new LambdaProxyIntegration(LambdaProxyIntegrationProps.builder()
                        .handler(rngService)
                        .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                        .build()))
                .build());

        Map<String, String> rouletteEnvVars = new HashMap<>();
        rouletteEnvVars.put("RNG_SERVICE_URL",httpApi.getApiEndpoint());
        rouletteEnvVars.put("LOG_LEVEL", "INFO");
        rouletteEnvVars.put("POWERTOOLS_SERVICE_NAME", "RouletteService");

        Function rouletteService = new Function(this, "RouletteService", FunctionProps.builder()
                .runtime(Runtime.JAVA_8_CORRETTO)
                .code(Code.fromAsset("../software/roulette-service/target/roulette-service.jar"))
                .handler("com.amazonaws.cloudcasino.RouletteHandler")
                .memorySize(1024)
                .timeout(Duration.seconds(10))
                .environment(rouletteEnvVars)
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/roulette")
                .methods(singletonList(HttpMethod.POST))
                .integration(new LambdaProxyIntegration(LambdaProxyIntegrationProps.builder()
                        .handler(rouletteService)
                        .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                        .build()))
                .build());
    }
}