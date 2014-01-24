LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES := bouncycastle telephony-common
LOCAL_STATIC_JAVA_LIBRARIES := android-support-v13 android-support-v4

LOCAL_JAVA_LIBRARIES += org.cyanogenmod.hardware

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res

LOCAL_AAPT_FLAGS := --auto-add-overlay

LOCAL_PACKAGE_NAME := CarbonFibers
LOCAL_CERTIFICATE := platform
LOCAL_PRIVIELEGED_MODULE := true

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
