package org.openjfx.token;

public class PKCS11Provider implements SecurityProvider  {
    protected String type;
    protected String pathConfig;
    private static final String SUN_PKCS11_PROVIDERNAME = "SunPKCS11";

    public PKCS11Provider(String pathConfig){
        this.type = SUN_PKCS11_PROVIDERNAME;
        this.pathConfig = pathConfig;
    }

    @Override
    public void setTypeProvider(String type) {
        this.type = type;
    }

    @Override
    public String getTypeProvider() {
        return this.type;
    }

    @Override
    public void setConfig(String pathConfig) {
        this.pathConfig = pathConfig;
    }

}
