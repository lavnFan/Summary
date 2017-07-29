
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)


OpenCV_INSTALL_MODULES := off
OpenCV_CAMERA_MODULES := off


LOCAL_C_INCLUDES := external/stlport/stlport/
LOCAL_C_INCLUDES += bionic
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../native/jni/include/
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../native/jni/include/opencv/

OPENCV_LIB_TYPE :=STATIC


#ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
#include $(LOCAL_PATH)/../../native/jni/OpenCV.mk
#else
#include $(OPENCV_MK_PATH)
#endif
PATHD=$(shell pwd)

LOCAL_MODULE := libOpenCV
LOCAL_SRC_FILES := com_aliyun_utils_OpenCVHelper.cpp

prebuilt_stdcxx_PATH := prebuilts/ndk/current/sources/cxx-stl/gnu-libstdc++
prebuilt_stdcxx_PATH1 := prebuilts/ndk/current/sources/cxx-stl/stlport
#prebuilt_stdcxx_PATH2 := prebuilts/ndk/current/sources/cxx-stl/stlport
#/gnu-libstdc++


LOCAL_LDFLAGS += -L$(prebuilt_stdcxx_PATH)/libs/armeabi-v7a -lgnustl_shared -lsupc++
LOCAL_LDFLAGS += -L$(prebuilt_stdcxx_PATH1)/libs/armeabi-v7a -lstlport_shared 
# -l$(prebuilt_stdcxx_PATH)/libs/armeabi-v7a/libgnustl_static.a -l$(prebuilt_stdcxx_PATH)/libs/armeabi-v7a/libsupc++.a
LOCAL_LDLIBS +=  -lm -llog  -ldl -lz
#LOCAL_LDLIBS += $(foreach lib,$(LOCAL_PATH)/lib/, -l$(lib))
#LOCAL_LDLIBS += -L$(PATHD)/$(LOCAL_PATH)/lib  -l$(PATHD)/$(LOCAL_PATH)/lib/opencv_core
CV_LIBS = $(foreach x,$(LOCAL_PATH)/lib/,\
                    $(wildcard  \
                    $(addprefix  ${x}*,.a) ) )

LOCAL_LDFLAGS +=-L$(PATHD)/$(LOCAL_PATH)/lib/  -l$(PATHD)/$(LOCAL_PATH)/lib/libopencv_core.a \
				-l$(PATHD)/$(LOCAL_PATH)/lib/libopencv_imgproc.a\
				-l$(PATHD)/$(LOCAL_PATH)/lib/libopencv_highgui.a\
				-l$(PATHD)/$(LOCAL_PATH)/lib/libopencv_imgcodecs.a \
				-l$(PATHD)/$(LOCAL_PATH)/lib/libIlmImf.a  \
				-l$(PATHD)/$(LOCAL_PATH)/lib/liblibjpeg.a  \
				-l$(PATHD)/$(LOCAL_PATH)/lib/liblibwebp.a  \
				-l$(PATHD)/$(LOCAL_PATH)/lib/liblibtiff.a  \
                -l$(PATHD)/$(LOCAL_PATH)/lib/liblibpng.a  \
				-l$(PATHD)/$(LOCAL_PATH)/lib/liblibjasper.a   \
				-l$(PATHD)/$(LOCAL_PATH)/lib/libtbb.a  \
				-l$(PATHD)/$(LOCAL_PATH)/lib/libopencv_java3.so \
				-lstdc++

LOCAL_MULTILIB := 32
LOCAL_MODULE_TARGET_ARCH := arm


#$(warning $(prebuilt_stdcxx_PATH))
#$(warning $(LOCAL_LDFLAGS))

include $(BUILD_SHARED_LIBRARY)
