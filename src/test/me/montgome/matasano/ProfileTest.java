package me.montgome.matasano;

import static org.junit.Assert.assertEquals;
import lombok.val;

import org.junit.Test;

public class ProfileTest {
    private static final String TOKEN = "email=foo@bar.com&uid=10&role=user";

    @Test
    public void parse() {
        val profile = Profile.parse(TOKEN);
        assertEquals("foo@bar.com", profile.getEmail());
        assertEquals(10, profile.getUid());
        assertEquals("user", profile.getRole());
    }

    @Test
    public void toString_() {
        Profile profile = new Profile("foo@bar.com", 10, "user");
        assertEquals(TOKEN, profile.toString());
    }

    @Test
    public void forEmail() {
        assertEquals(TOKEN, Profile.forEmail("foo@bar.com").toString());
    }
}
