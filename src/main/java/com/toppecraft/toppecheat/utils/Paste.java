package com.toppecraft.toppecheat.utils;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Paste {

    private static final String PASTEBIN_URL = "http://pastebin.com/api/api_post.php";

    private List<String> lines;
    private ExpireDate expire;
    private String name;

    public Paste(List<String> lines, ExpireDate expire, String name) {
        this.lines = lines;
        this.expire = expire;
        this.name = name;
    }

    public Paste() {
        lines = new ArrayList<String>();
        name = "untitled";
        expire = ExpireDate.WEEK;
    }

    private static String getContent(List<String> lines) {
        String comma = "";
        StringBuilder all = new StringBuilder();
        for (String s : lines) {
            all.append(comma);
            all.append(s);
            comma = "\n";
        }
        return all.toString();
    }

    //sender.sendMessage(ChatColor.RED + "Pastebin API key is missing. You must put one in the config to use the pastebin feature.");

    public void createPaste(LinkCallback callback) {
        paste(lines, expire, name, callback);
    }

    private void paste(final List<String> lines, final ExpireDate expire, final String name, final LinkCallback callback) {
        new BukkitRunnable() {

            @Override
            public void run() {
                URL urls;
                try {
                    urls = new URL(PASTEBIN_URL);
                    HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
                    conn.setInstanceFollowRedirects(false);
                    conn.setDoOutput(true);
                    OutputStream out = conn.getOutputStream();
                    String content = getContent(lines);
                    String data = ("api_option=paste" +
                            "&api_dev_key=" + URLEncoder.encode(ToppeCheat.getInstance().getConfig().getString("pastebin-api-key"), "utf-8") +
                            "&api_paste_code=" + URLEncoder.encode(content, "utf-8") +
                            "&api_paste_private=0" +
                            "&api_paste_name=" + URLEncoder.encode(name, "utf-8") +
                            "&api_paste_expire_date=" + URLEncoder.encode(expire.toString(), "utf-8") +
                            "&api_paste_format=text" +
                            "&api_user_key=" + URLEncoder.encode("", "utf-8"));
                    out.write(data.getBytes());
                    out.flush();
                    out.close();
                    InputStream is = conn.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                    }
                    rd.close();
                    String result = response.toString();
                    if (result.contains("http://")) {
                        callback.onSuccess(result.trim());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(null);
            }
        }.runTaskAsynchronously(ToppeCheat.getInstance());

    }

    /**
     * @return the lines
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     * @param lines the lines to set
     */
    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    /**
     * @return the expire
     */
    public ExpireDate getExpire() {
        return expire;
    }

    /**
     * @param expire the expire to set
     */
    public void setExpire(ExpireDate expire) {
        this.expire = expire;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public enum ExpireDate {
        NEVER("N"),
        TEN_MINUTES("10M"),
        HOUR("1H"),
        DAY("1D"),
        WEEK("1W"),
        TWO_WEEKS("2W"),
        MONTH("1M");

        private final String name;

        ExpireDate(String keyValue) {
            this.name = keyValue;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public abstract class LinkCallback {

        public abstract void onSuccess(String link);
    }
}
