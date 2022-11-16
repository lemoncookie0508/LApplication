package lepl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

public class Registrar {
    public static String getRegData(String key, String name, String datatype) {
        BufferedInputStream in = null;
        String regData = "";

        try {
            String strCmd = "reg query \"" + key + "\" /v \"" + name + "\"";

            Process process = Runtime.getRuntime().exec(strCmd);
            in = new BufferedInputStream(process.getInputStream());

            // Data가 들어오는 동안 0.2초정도 대기
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            byte[] buff = new byte[in.available()];
            in.read(buff);

            regData = new String(buff);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(in).close();
            } catch (IOException ignored) {}
        }

        if (!regData.contains(datatype)) return "";

        return regData.substring(regData.indexOf(datatype) + datatype.length()).trim();
    }
    public static String getRegData(String key, String name) {
        return getRegData(key, name, "REG_SZ");
    }

    public static void addRegKey(String key, String name, String data) {
        String cmd = "reg add \"" + key + "\" /v \"" + name + "\" /d \"" + data + "\" /f";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
