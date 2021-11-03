package com.myorg;

import software.amazon.awscdk.core.App;

public class InfrastructureApp {
    public static void main(final String[] args) {
        App app = new App();

        new InfrastructureStack(app, "CloudCasinoStack");

        app.synth();
    }
}
