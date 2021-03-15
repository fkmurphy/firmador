package org.openjfx.Main.file;

import org.openjfx.Main.file.exceptions.BadPasswordTokenException;
import org.openjfx.token.models.Token;

public interface FileRepository {
    public String getPath();
    public String representativeName();
    public Boolean sign(Token token) throws BadPasswordTokenException;
    public String getDescription();
}
