package app.web.spsquared.config;

public record Config(boolean enforce) {
    public Config() {
        this(true);
    }
}
