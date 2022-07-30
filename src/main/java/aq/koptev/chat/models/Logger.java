package aq.koptev.chat.models;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Logger {

    public static final String LOG_PATH = "src/main/resources/aq/koptev/chat/log/chat-log.txt";
    private final String DELIMITER = "#message_delimiter";

    public List<String> read(File file) {
        if(file.exists()) {
            try(BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                int character;
                while ((character = br.read()) != -1) {
                    sb.append((char) character);
                }
                String[] tmp = get100LastMessages(sb);
                List<String> strings = new ArrayList<>(Arrays.asList(tmp));
                return strings;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private String[] getMessages(StringBuilder sb) {
        return sb.toString().trim().split(DELIMITER);
    }

    private String[] get100LastMessages(StringBuilder sb) {
        String[] tmp = sb.toString().trim().split(DELIMITER);
        if(tmp.length >= 100) {
            return Arrays.copyOfRange(tmp, tmp.length - 100, tmp.length);
        }
        return tmp;
    }

    public void write(File file, List<String> strings) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for(String s : strings) {
                bw.write(s);
                bw.write(String.format("%s", DELIMITER));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
