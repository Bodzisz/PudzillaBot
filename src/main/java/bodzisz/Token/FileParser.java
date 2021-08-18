package bodzisz.Token;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileParser {

    public static String readFileAsString(String path) throws Exception
    {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(path)));
        return data;
    }
}
