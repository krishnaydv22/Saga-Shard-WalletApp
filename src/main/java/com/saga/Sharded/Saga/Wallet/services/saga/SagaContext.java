package com.saga.Sharded.Saga.Wallet.services.saga;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;

@Data
@NoArgsConstructor
public class SagaContext {

    private HashMap<String, Object> data;

    public SagaContext(HashMap<String, Object> data){

        this.data = data != null ? data : new HashMap<>();

    }
    public void put(String key, Object value){
        data.put(key, value);
    }

    public Object get(String key){
        return data.get(key);
    }

    public Long getLong(String key){
        Object value = get(key);

        if(value instanceof Number){
            return ((Number) value).longValue();
        }

        return null;
    }

    public BigDecimal getBigDecimal(String key){
        Object value = get(key);
        if(value instanceof Number){
            return  BigDecimal.valueOf(((Number) value).doubleValue());
        }

        return null;

    }

    public String getString(String key) {
        Object value = get(key);
        if ( value instanceof String) {
            return (String) value;
        }
        return null;
    }
}
