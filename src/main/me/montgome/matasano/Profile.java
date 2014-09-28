package me.montgome.matasano;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@AllArgsConstructor
@Getter
public class Profile {
    private final String email;
    private final int uid;
    private final String role;

    @Override
    public String toString() {
        String s = String.format(
            "email=%s&uid=%s&role=%s",
            email, uid, role);
        System.out.println(s);
        return s;
    }

    public static Profile parse(String s) {
        System.out.println(s);
        val profile = new HashMap<String, String>();

        int seen = 0;
        while (seen < s.length()) {
            String key = readUntil(s, seen, "=");
            seen += key.length() + 1;
            String value = readUntil(s, seen, "&");
            seen += value.length() + 1;
            profile.put(key, value);
        }

        return new Profile(
            profile.get("email"),
            Integer.parseInt(profile.get("uid")),
            profile.get("role"));
    }

    private static String readUntil(String s, int start, String stop) {
        int end = s.indexOf(stop, start);
        boolean notFound = (end == -1);
        return notFound
            ? s.substring(start)
            : s.substring(start, end);
    }

    public static Profile forEmail(String email ) {
        return new Profile(sanitize(email), 10, "user");
    }

    private static String sanitize(String s) {
        return s
            .replace("&", "")
            .replace("=", "");
    }
}
