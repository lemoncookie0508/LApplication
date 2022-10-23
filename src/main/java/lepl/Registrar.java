package lepl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

public class Registrar {
    public static String getRegistryData(String regDir, String key, String datatype) {
        BufferedInputStream in = null;
        String regData = "";

        try {
            String strCmd = "reg query \"" + regDir + "\" /v \"" + key + "\"";

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
    public static String getRegistryData(String regDir, String key) {
        return getRegistryData(regDir, key, "REG_SZ");
    }
}
