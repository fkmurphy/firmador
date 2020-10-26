package org.openjfx.token.models;

import java.security.Provider;
import java.util.Map;

public interface Token {
    public Map<String,String> getInfo();
    public String getDriverPath();
    public Provider getProvider();
    public void sign(String src, String dst);
    public void signWithPositionStamper(String src, String dst, int posX, int posY);
}
