OpenGL ES 3.0 Programming Guide
===============================

《OpenGL ES 3.0 编程指南》书中使用 AndroidSdk API 编写的样板代码。

# 本仓库做的修改

1. [原仓库](https://github.com/danginsburg/opengles3-book) Android_Java 工程基于 Eclipse 构建，本仓库增加 Gradle 脚本配置，可以直接使用 Android Studio 打开工程并运行相关示例。
2. 本仓库，将 顶点着色器和片段着色器，都以单独的文件抽取出去到assets/shaders目录下，分别名为：
+ 顶点着色器 shaders\vertexShader.vert
+ 片段着色器 shaders\fragmentShader.frag
3. 本仓库，添加了部分注释，便于更好的理解OpenGL ES
 