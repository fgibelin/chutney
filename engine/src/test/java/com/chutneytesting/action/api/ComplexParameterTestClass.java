package com.chutneytesting.action.api;

import com.chutneytesting.action.spi.injectable.Input;

public class ComplexParameterTestClass {

    private String firstParameter;
    private Integer secondParameter;
    private SubComplexObject thirdParameter;

    public ComplexParameterTestClass(@Input("first") String firstParameter, @Input("second") Integer secondParameter, @Input("third") SubComplexObject thirdParameter) {
        this.firstParameter = firstParameter;
        this.secondParameter = secondParameter;
        this.thirdParameter = thirdParameter;
    }

    public static class SubComplexObject {
    }
}
