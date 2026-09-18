package com.kh.khedu.error;

public class PayrollCalculationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;

    public PayrollCalculationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}