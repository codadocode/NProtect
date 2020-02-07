package br.com.nareba.nprotect;

import java.util.UUID;

public class BaseId {
    private String value;
    public BaseId()   {
        this.value = UUID.randomUUID().toString();
    }
    public BaseId(String value)   {
        this.value = value;
    }
    public String getValue()   {
        return this.value;
    }
    @Override
    public String toString()   {
        return this.value;
    }
}
