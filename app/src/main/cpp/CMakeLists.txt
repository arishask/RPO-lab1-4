# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

project("laba1")

add_library(
        laba1
        SHARED
        native-lib.cpp)

add_library(spdlog STATIC IMPORTED)

add_library(mbedcrypto SHARED IMPORTED)
set_target_properties(mbedcrypto PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../nativeLibs/${ANDROID_ABI}/libmbedcrypto.so)

target_include_directories(laba1 PUBLIC
        /Users/arinaaskhabova/AndroidStudioProjects/libs/spdlog/spdlog/include
        /Users/arinaaskhabova/AndroidStudioProjects/libs/mbedtls/mbedtls/include
        )

set_target_properties(spdlog PROPERTIES IMPORTED_LOCATION
        /Users/arinaaskhabova/AndroidStudioProjects/libs/spdlog/build/${ANDROID_ABI}/libspdlog.a)

target_include_directories(laba1 PUBLIC
        /Users/arinaaskhabova/AndroidStudioProjects/libs/spdlog/spdlog/include)
# Declares and names the project.





find_library(
        log-lib
        log)

target_link_libraries(
        laba1
        spdlog
        mbedcrypto
        ${log-lib})