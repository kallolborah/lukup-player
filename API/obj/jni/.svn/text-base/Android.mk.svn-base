#REFSW_PATH :=vendor/broadcom/bcm${BCHP_CHIP}/brcm_nexus
REFSW_PATH :=/home/lukup/JB-14.2-X/Android-4.2.2/bcmstb_jb20131125/AppLibs/opensource/android/src/mips-jb/vendor/broadcom/bcm7429/brcm_nexus
LIB_PATH :=/home/lukup/JB-14.2-X/Android-4.2.2/bcmstb_jb20131125/AppLibs/opensource/android/src/mips-jb/out/target/product/bcm7429/system/lib

EXTRA_PATH:=/home/lukup/JB-14.2-X/Android-4.2.2/bcmstb_jb20131125/AppLibs/opensource/android/src/mips-jb/frameworks/av/include/

LOCAL_PATH:= $(call my-dir)
APP_ABI := mips
include $(REFSW_PATH)/bin/include/platform_app.inc

include $(CLEAR_VARS)
LOCAL_PRELINK_MODULE := false
#LOCAL_SHARED_LIBRARIES := liblog libcutils libutils libbinder libnexusfrontendservice
LOCAL_SHARED_LIBRARIES := liblog libcutils libutils libbinder libnexusservice -lnexusipcclient -llog -lcutils -lutils -lbinder -lnexusservice -L$(LIB_PATH)
LOCAL_SHARED_LIBRARIES += libnexusipcclient

LOCAL_LDFLAGS := -lnexus -L$(REFSW_PATH)/bin -lnexusipcclient -lnexus -L$(REFSW_PATH)/bin -llog -L$(LIB_PATH)
LOCAL_C_INCLUDES += $(REFSW_PATH)/bin/include $(JNI_H_INCLUDE) $(EXTRA_PATH) 
#LOCAL_C_INCLUDES += $(REFSW_PATH)/../libnexusfrontendservice
#LOCAL_C_INCLUDES += $(REFSW_PATH)/bin/include \
#$(REFSW_PATH)/../libnexusservice
LOCAL_C_INCLUDES += $(REFSW_PATH)/../libnexusservice
LOCAL_C_INCLUDES += $(REFSW_PATH)/../libnexusipc

LOCAL_SRC_FILES := jni_hdmicvbs.cpp 

LOCAL_CFLAGS:= $(NEXUS_CFLAGS) -DANDROID
LOCAL_CFLAGS += -DANDROID_SUPPORTS_NEXUS_IPC_CLIENT_FACTORY
LOCAL_CFLAGS += -DLOGD=ALOGD -DLOGE=ALOGE -DLOGW=ALOGW -DLOGV=ALOGV -DLOGI=ALOGI
LOCAL_MODULE := jni_hdmicvbs 
LOCAL_MODULE_TAGS :=eng
include $(BUILD_SHARED_LIBRARY)
