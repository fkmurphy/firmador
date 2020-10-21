package org.openjfx.Main.file;

import org.openjfx.token.models.Token;

public interface FileRepository {
    public String getPath();
    public String representativeName();
    public Boolean sign(Token token);
    public String getDescription();
}
