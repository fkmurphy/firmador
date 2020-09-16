package org.openjfx.token;

public interface SecurityProvider {
    public void setTypeProvider(String type);
    public String getTypeProvider();
    public void setConfig(String pathConfig);
}
