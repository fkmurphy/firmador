package org.openjfx.token.models;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;

public final class ConfigureProvider {
    public ArrayList<LocalProvider> getProviders(String os) {
        ArrayList<LocalProvider> result = new ArrayList<LocalProvider>();
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("providers-" + os);
            Enumeration<String> keys = bundle.getKeys();
            ArrayList<String> temp = new ArrayList<String>();

            // get the keys and add them in a temporary ArrayList
            for (Enumeration<String> e = keys; keys.hasMoreElements();) {
                String key = e.nextElement();
                temp.add(key);
            }

            // store the bundle Strings in the StringArray
            for (int i = 0; i < temp.size(); i++) {
                LocalProvider localProvider = new LocalProvider();
                localProvider.setName(temp.get(i));
                localProvider.setLibrary(bundle.getString(temp.get(i)));
                result.add(localProvider);
            }
        } catch (Exception e) {

        }

        return result;
    }
}
