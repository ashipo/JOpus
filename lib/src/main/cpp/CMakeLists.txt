cmake_minimum_required(VERSION 3.18.1)
project(jopus)

add_subdirectory(opus)

add_library(${CMAKE_PROJECT_NAME}
        SHARED
        jopus.cpp
)

# Use version script to control symbol visibility
# https://developer.android.com/ndk/guides/symbol-visibility
target_link_options(${CMAKE_PROJECT_NAME}
        PRIVATE
        -Wl,--version-script,${CMAKE_SOURCE_DIR}/libjopus.map.txt
        -Wl,--no-undefined-version
)

set_target_properties(${CMAKE_PROJECT_NAME}
        PROPERTIES
        LINK_DEPENDS ${CMAKE_SOURCE_DIR}/libjopus.map.txt
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        opus
        log
)
