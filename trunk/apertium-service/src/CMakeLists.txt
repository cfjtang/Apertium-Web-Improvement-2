# ########## Project setup ##########
PROJECT(src)
CMAKE_MINIMUM_REQUIRED(VERSION 2.4.5)

# ######### General setup ##########
INCLUDE_DIRECTORIES(${CMAKE_SOURCE_DIR})

# ########## src executable ##########
# Sources:
SET(src_executable_SRCS
    main.cpp
)

# Headers:
SET(src_executable_HDRS
)

CONFIGURE_FILE(config.h.in ${CMAKE_BINARY_DIR}/config.h)
CONFIGURE_FILE(Makefile.in ${CMAKE_BINARY_DIR}/Makefile)


# actual target:
ADD_EXECUTABLE(src ${src_executable_SRCS})

# add install target:
INSTALL(TARGETS src DESTINATION bin)
