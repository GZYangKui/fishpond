package cn.navclub.fishpond.server.pattern;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class PathMatter {
    /**
     * 单级
     */
    private static final String SINGLE_LEAVE = "*";
    /**
     * 多级
     */
    private static final String MULTI_LEVEL = "**";

    private final Map<String, Pattern> map;

    public PathMatter() {
        this.map = new ConcurrentHashMap<>();
    }

    public boolean matcher(String path, String pattern, boolean cached) {
        //判断是否精确匹配
        if (!(pattern.contains(SINGLE_LEAVE) || pattern.contains(MULTI_LEVEL))) {
            return path.equals(pattern);
        }
        Pattern ptn;
        var added = false;
        if (!cached || (added = ((ptn = this.map.get(pattern)) == null))) {
            ptn = pattern(pattern);
        }
        if (cached && added) {
            this.map.put(pattern, ptn);
        }
        return ptn.matcher(path).matches();
    }

    /**
     * 动态生成正则表达式
     */
    private Pattern pattern(String pattern) {
        var arr = pattern.split("/");
        var sb = new StringBuilder();
        for (int i = 1; i < arr.length; i++) {
            var s = arr[i].trim();
            var multi = s.equals(MULTI_LEVEL);
            if (multi) {
                sb.append("(/?\\w/?)*");
            } else {
                sb.append("/");
                if (s.equals(SINGLE_LEAVE)) {
                    sb.append("(\\w)+");
                } else {
                    sb.append(s);
                }
            }
        }
        return Pattern.compile(sb.toString());
    }
}
