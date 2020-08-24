package token;

import java.security.Provider;

public interface Token {
    public String[] getInfo();
    public String getDriverPath();
    public Provider getProvider();
}
