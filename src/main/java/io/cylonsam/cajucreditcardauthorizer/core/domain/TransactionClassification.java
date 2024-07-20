package io.cylonsam.cajucreditcardauthorizer.core.domain;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum TransactionClassification {
    FOOD("5411", "5412"),
    MEAL("5811", "5812"),
    CASH;

    private final String[] codes;
    private static final Map<String, TransactionClassification> codeMap;

    static {
        final Map<String, TransactionClassification> map = new HashMap<>();
        for (TransactionClassification classification : TransactionClassification.values()) {
            for (String code : classification.getCodes()) {
                map.put(code, classification);
            }
        }
        codeMap = Collections.unmodifiableMap(map);
    }

    TransactionClassification(String... codes) {
        this.codes = codes;
    }

    public static TransactionClassification getClassification(final String code) {
        TransactionClassification classification = codeMap.get(code);
        if (classification == null) {
            return CASH;
        }
        return classification;
    }
}
