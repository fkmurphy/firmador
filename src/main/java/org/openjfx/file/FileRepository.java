package org.openjfx.file;

import org.openjfx.token.models.Token;

import java.io.InputStream;

public interface FileRepository {
    public String getPath();
    public String representativeName();
    public Boolean sign(Token token);
    public String getDescription();
}
