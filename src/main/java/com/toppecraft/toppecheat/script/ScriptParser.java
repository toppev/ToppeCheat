package com.toppecraft.toppecheat.script;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ScriptParser {

    private File file;
    private List<String> function = new ArrayList<String>();

    public ScriptParser(File file) {
        this.file = file;
    }

    public void parse() {
        for (String s : getAllLines(file)) {
            if (!s.startsWith("on event:") || !s.startsWith("on violation event:")) {
                getFunction().add(s);
            }
        }
    }

    private List<String> getAllLines(File file) {
        URI uri = file.toURI();
        try {
            return Files.readAllLines(Paths.get(uri));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getFunction() {
        return function;
    }

}
