package com.jesse.autotrack;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReplaceBuildConfig {

    private static final String TAG = "ReplaceBuildConfig";

    private final String mReplaceListDir;
    private final List<ReplaceMent> mReplaceMents;

    public ReplaceBuildConfig(String mReplaceListDir) {
        this.mReplaceListDir = mReplaceListDir;
        mReplaceMents = new ArrayList<>();
    }

    public List<ReplaceMent> getmReplaceMents() {
        return mReplaceMents;
    }

    public void parseReplaceFile() {
        if (Util.isNullOrNil(mReplaceListDir)) {
            Log.w(TAG, "replaceListDir not config");
            return;
        }
        File replaceConfigFile = new File(mReplaceListDir);
        if (!replaceConfigFile.exists()) {
            Log.w(TAG, "replace config file not exist %s", replaceConfigFile.getAbsoluteFile());
            return;
        }
        String replaceStr = Util.readFileAsString(replaceConfigFile.getAbsolutePath());
        String[] replaceArray = replaceStr.split("\n");

        for (String replace : replaceArray) {
            replace = replace.trim();
            if (replace.length() == 0) {
                continue;
            }
            if (!replace.contains("=")) {
                continue;
            }
            String replaceMentStr[] = replace.split("=");
            if (replaceMentStr.length != 2) {
                continue;
            }
            mReplaceMents.add(getReplaceMent(replaceMentStr));
        }
    }

    private ReplaceMent getReplaceMent(String[] replaceMentStr) {
        ReplaceMent replaceMent = new ReplaceMent();
        if (replaceMentStr.length == 2) {
            //解析被替换的方法
            String[] srcStr = replaceMentStr[0].split("\\.");
            if (srcStr.length == 2) {
                replaceMent.setSrcClass(srcStr[0]);
                String[] srcMethodStr = srcStr[1].split("\\(");
                if (srcMethodStr.length == 2) {
                    replaceMent.setSrcMethodName(srcMethodStr[0]);
                    replaceMent.setSrcMethodDesc("(" + srcMethodStr[1]);
                }
            }
            //解析替换的方法
            String[] dstStr = replaceMentStr[1].split("\\.");
            if (dstStr.length == 2) {
                replaceMent.setDstClass(dstStr[0]);
                String[] dstMethodStr = dstStr[1].split("\\(");
                if (dstMethodStr.length == 2) {
                    replaceMent.setDstMethodName(dstMethodStr[0]);
                    replaceMent.setDstMethodDesc("(" + dstMethodStr[1]);
                }
            }
        }
        return replaceMent;
    }

    public static class ReplaceMent {
        private String srcClass;
        private String srcMethodName;
        private String srcMethodDesc;
        private String dstClass;
        private String dstMethodName;
        private String dstMethodDesc;

        public String getSrcClass() {
            return srcClass;
        }

        public void setSrcClass(String srcClass) {
            this.srcClass = srcClass;
        }

        public String getSrcMethodName() {
            return srcMethodName;
        }

        public void setSrcMethodName(String srcMethodName) {
            this.srcMethodName = srcMethodName;
        }

        public String getSrcMethodDesc() {
            return srcMethodDesc;
        }

        public void setSrcMethodDesc(String srcMethodDesc) {
            this.srcMethodDesc = srcMethodDesc;
        }

        public String getDstClass() {
            return dstClass;
        }

        public void setDstClass(String dstClass) {
            this.dstClass = dstClass;
        }

        public String getDstMethodName() {
            return dstMethodName;
        }

        public void setDstMethodName(String dstMethodName) {
            this.dstMethodName = dstMethodName;
        }

        public String getDstMethodDesc() {
            return dstMethodDesc;
        }

        public void setDstMethodDesc(String dstMethodDesc) {
            this.dstMethodDesc = dstMethodDesc;
        }

        @Override
        public String toString() {
            return "ReplaceMent{" +
                    "srcClass='" + srcClass + '\'' +
                    ", srcMethodName='" + srcMethodName + '\'' +
                    ", srcMethodDesc='" + srcMethodDesc + '\'' +
                    ", dstClass='" + dstClass + '\'' +
                    ", dstMethodName='" + dstMethodName + '\'' +
                    ", dstMethodDesc='" + dstMethodDesc + '\'' +
                    '}';
        }
    }
}
