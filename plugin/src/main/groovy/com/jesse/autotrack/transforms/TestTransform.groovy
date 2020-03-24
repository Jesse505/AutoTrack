package com.jesse.autotrack.transforms


class TestTransform extends BaseTransform {

    @Override
    boolean isShouldModify(String className) {
        return false
    }

    @Override
    byte[] modifyClass(byte[] srcClass) throws IOException {
        return srcClass
    }

    @Override
    String getName() {
        return "TestTransform"
    }
}