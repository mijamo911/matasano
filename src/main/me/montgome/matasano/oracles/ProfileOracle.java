package me.montgome.matasano.oracles;

import lombok.AllArgsConstructor;
import me.montgome.matasano.Profile;
import me.montgome.matasano.Strings;

@AllArgsConstructor
public class ProfileOracle implements Oracle {
    private final Oracle delegate;

    @Override
    public byte[] encrypt(byte[] plaintext) {
        String email = Strings.newString(plaintext);
        Profile profile = Profile.forEmail(email);
        byte[] token = Strings.getBytes(profile.toString());
        return delegate.encrypt(token);
    }
}
