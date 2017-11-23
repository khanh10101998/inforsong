LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := jniLibs
LOCAL_SRC_FILES := libACRCloudEngine.so

include $(BUILD_SHARED_LIBRARY)