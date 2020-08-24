package token;

import sample.Main;

import java.security.Provider;
import java.security.Security;

public class GemaltoToken implements Token {
    protected String driverPath;
    protected Provider provider;
    public GemaltoToken(){
        this.driverPath = "";
        Provider prototype = Security.getProvider("SunPKCS11");
        this.provider = prototype.configure(getConfig());
        Security.addProvider(provider);
    }
    @Override
    public String[] getInfo() {

        return new String[0];
    }

    @Override
    public String getDriverPath() {
        return null;
    }

    @Override
    public Provider getProvider() {
        return this.provider;
    }

    protected String getConfig(){
        return Main.class.getClassLoader().getResource("pkcs11.cfg").getFile();
    }

}
