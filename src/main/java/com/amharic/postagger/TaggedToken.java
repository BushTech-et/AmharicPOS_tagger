package com.amharic.postagger;

/**
 * A single token paired with its POS tag.
 */
public final class TaggedToken {

    private final String token;
    private final POSTag tag;

    public TaggedToken(String token, POSTag tag) {
        this.token = token;
        this.tag   = tag;
    }

    public String getToken() { return token; }
    public POSTag getTag()   { return tag; }

    @Override
    public String toString() {
        return token + "/" + tag.label();
    }
}
