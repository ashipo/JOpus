cmake_minimum_required(VERSION 3.18.1)
project(jopus)

add_subdirectory(opus)
add_library(${CMAKE_PROJECT_NAME} SHARED jopus.cpp)
target_link_libraries(${CMAKE_PROJECT_NAME} opus log)
