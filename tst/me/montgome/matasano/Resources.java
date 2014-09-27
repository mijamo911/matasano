package me.montgome.matasano;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;

public class Resources {
    public static Iterable<String> readLines(String resource) {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            LinkedList<String> lines = new LinkedList<>();
            
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readFileStripNewlines(String resource) {
        StringBuilder builder = new StringBuilder();
        for (String line : readLines(resource)) {
            builder.append(line);
        }
        return builder.toString();
    }

    public static String readFileKeepNewlines(String resource) {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            transfer(stream, bytes);
            return Strings.newString(bytes.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void transfer(InputStream in, OutputStream out) {
        byte[] buffer = new byte[4096];

        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(String s, String resource) {
        try (FileOutputStream output = new FileOutputStream(resource)) {
            ByteArrayInputStream input = new ByteArrayInputStream(Strings.getBytes(s));
            transfer(input, output);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
