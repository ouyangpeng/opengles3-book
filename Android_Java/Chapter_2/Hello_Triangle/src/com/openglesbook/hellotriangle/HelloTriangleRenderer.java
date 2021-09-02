// The MIT License (MIT)
//
// Copyright (c) 2013 Dan Ginsburg, Budirijanto Purnomo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

//
// Book:      OpenGL(R) ES 3.0 Programming Guide, 2nd Edition
// Authors:   Dan Ginsburg, Budirijanto Purnomo, Dave Shreiner, Aaftab Munshi
// ISBN-10:   0-321-93388-5
// ISBN-13:   978-0-321-93388-1
// Publisher: Addison-Wesley Professional
// URLs:      http://www.opengles-book.com
//            http://my.safaribooksonline.com/book/animation-and-3d/9780133440133
//

// Hello_Triangle
//
//    This is a simple example that draws a single triangle with
//    a minimal vertex/fragment shader.  The purpose of this
//    example is to demonstrate the basic concepts of
//    OpenGL ES 3.0 rendering.

package com.openglesbook.hellotriangle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

// 可以参考这篇讲解： https://learnopengl-cn.github.io/01%20Getting%20started/04%20Hello%20Triangle/
public class HelloTriangleRenderer implements GLSurfaceView.Renderer {
    // Member variables
    private int mProgramObject;
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private static String TAG = "HelloTriangleRenderer";

    // 我们在OpenGL中指定的所有坐标都是3D坐标（x、y和z）
    // 由于我们希望渲染一个三角形，我们一共要指定三个顶点，每个顶点都有一个3D位置。
    // 我们会将它们以标准化设备坐标的形式（OpenGL的可见区域）定义为一个float数组。
    // https://learnopengl-cn.github.io/img/01/04/ndc.png
    private final float[] mVerticesData = {
            0.0f, 0.5f, 0.0f,            // 右上角
            -0.5f, -0.5f, 0.0f,          // 右下角
            0.5f, -0.5f, 0.0f            // 左上角
    };

    ///
    // Constructor
    //
    public HelloTriangleRenderer(Context context) {
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
    }

    ///
    // Create a shader object, load the shader source, and
    // compile the shader.
    //
    private int LoadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        // 调用glCreateShader将根据传入的type参数插件一个新的顶点着色器或者片段着色器
        shader = GLES30.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        // glShaderSource函数把要编译的着色器对象作为第一个参数。第二参数 着色器真正的源码
        GLES30.glShaderSource(shader, shaderSrc);

        // Compile the shader
        // 编译着色器
        GLES30.glCompileShader(shader);

        // Check the compile status
        // 检测编译时的状态，是编译错误还是编译成功

        // pname: 获得信息的参数，可以为
        //      GL_COMPILE_STATUS
        //      GL_DELETE_STATUS
        //      GL_INFO_LOG_LENGTH
        //      GL_SHADER_SOURCE_LENGTH
        //      GL_SHADER_TYPE
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        // 如果着色器编译成功，结果将是GL_TRUE。如果编译失败，结果将为GL_FALSE，编译错误将写入信息日志
        if (compiled[0] == GLES30.GL_FALSE) {
            // 用glGetShaderInfoLog检索信息日志
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            // 删除着色器对象
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        /*
         * 顶点着色器
              // 表示OpenGL ES着色器语言V3.00
              #version 300 es

              // 使用in关键字，在顶点着色器中声明所有的输入顶点属性(Input Vertex Attribute)。
              // 声明一个输入属性数组：一个名为vPosition的4分量向量
              // 在图形编程中我们经常会使用向量这个数学概念，因为它简明地表达了任意空间中的位置和方向，并且它有非常有用的数学属性。
              // 在GLSL中一个向量有最多4个分量，每个分量值都代表空间中的一个坐标，它们可以通过vec.x、vec.y、vec.z和vec.w来获取。
              //注意vec.w分量不是用作表达空间中的位置的（我们处理的是3D不是4D），而是用在所谓透视除法(Perspective Division)上。
              in vec4 vPosition;
              void main()
              {
                // 为了设置顶点着色器的输出，我们必须把位置数据赋值给预定义的gl_Position变量，它在幕后是vec4类型的。
                // 将vPosition输入属性拷贝到名为gl_Position的特殊输出变量
                // 每个顶点着色器必须在gl_Position变量中输出一个位置，这个位置传递到管线下一个阶段的位置
                gl_Position = vPosition;
              }
         */
        String vShaderStr =
                "#version 300 es 			  \n"
                        + "in vec4 vPosition;           \n"
                        + "void main()                  \n"
                        + "{                            \n"
                        + "   gl_Position = vPosition;  \n"
                        + "}                            \n";

        /* 片段着色器

        片段着色器(Fragment Shader)是第二个也是最后一个我们打算创建的用于渲染三角形的着色器。片段着色器所做的是计算像素最后的颜色输出。
        为了让事情更简单，我们的片段着色器将会一直输出橘黄色。

        在计算机图形中颜色被表示为有4个元素的数组：红色、绿色、蓝色和alpha(透明度)分量，通常缩写为RGBA。
        当在OpenGL或GLSL中定义一个颜色的时候，我们把颜色每个分量的强度设置在0.0到1.0之间。这三种颜色分量的不同调配可以生成超过1600万种不同的颜色！

               // 表示OpenGL ES着色器语言V3.00
               #version 300 es
               // 声明着色器中浮点变量的默认精度
               precision mediump float;
               // 声明一个输出变量fragColor，这是一个4分量的向量，
               // 写入这个变量的值将被输出到颜色缓冲器
               out vec4 fragColor;
               void main()
               {
                   // 所有片段的着色器输出都是红色( 1.0, 0.0, 0.0, 1.0 )
                   fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );

                   // 会输出橘黄色
                   // fragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
               }
         */
        String fShaderStr =
                "#version 300 es		 			          	\n"
                        + "precision mediump float;					  	\n"
                        + "out vec4 fragColor;	 			 		  	\n"
                        + "void main()                                  \n"
                        + "{                                            \n"
                        + "  fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );	\n"
                        + "}                                            \n";

        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders

        // 由于我们正在创建一个顶点着色器，传递的参数是GL_VERTEX_SHADER。
        vertexShader = LoadShader(GLES30.GL_VERTEX_SHADER, vShaderStr);
        // 由于我们正在创建一个片段着色器，传递的参数是GL_FRAGMENT_SHADER
        fragmentShader = LoadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr);

        // Create the program object  创建一个程序对象
        programObject = GLES30.glCreateProgram();

        if (programObject == 0) {
            return;
        }

        // 在OpenGL ES3.0中，每个程序对象必须连接一个顶点着色器和一个片段着色器
        // 把之前编译的着色器附加到程序对象上
        // 着色器可以在任何时候连接-----在连接到程序之前不一定需要编译，甚至可以没有源代码。
        // 唯一要求是：每个程序对象必须有且只有一个顶点着色器和一个片段着色器与之连接
        // 除了连接着色器之外，你还可以用glDetachShader断开着色器的连接
        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);

        // Bind vPosition to attribute 0
        GLES30.glBindAttribLocation(programObject, 0, "vPosition");

        // Link the program
        // 链接操作负责生成最终的可执行的程序。
        // 一般来说，链接阶段是生成在硬件上运行的最终硬件指令的时候
        GLES30.glLinkProgram(programObject);

        // Check the link status  检测链接着色器程序是否失败
        // pname 获取信息的参数，可以是
        //      GL_ACTIVE_ATTRIBUTES        返回顶点着色器中活动属性的数量
        //      GL_ACTIVE_ATTRIBUTE_MAX_LENGTH      返回最大属性名称的最大长度（以字符数表示），这一信息用于确定存储属性名字符串所需的内存量
        //      GL_ACTIVE_UNIFORM_BLOCK     返回包含活动统一变量的程序中的统一变量块数量
        //      GL_ACTIVE_UNIFORM_BLOCK_MAX_LENGTH        返回包含活动统一变量的程序中的统一变量块名称的最大长度
        //      GL_ACTIVE_UNIFORMS      返回活动统一变量的数量
        //      GL_ACTIVE_UNIFORM_MAX_LENGTH     返回最大统一变量名称的最大长度
        //      GL_ATTACHED_SHADERS   返回连接到程序对象的着色器数量
        //      GL_DELETE_STATUS   查询返回程序对象是否已经标记为删除
        //      GL_LINK_STATUS      检查链接是否成功
        //      GL_INFO_LOG_LENGTH   程序对象存储的信息日志长度
        //      GL_LINK_STATUS          链接是否成功
        //      GL_PROGRAM_BINARY_RETRIEVABLE_HINT  返回一个表示程序目前是否启用二进制检索提示的值
        //      GL_TRANSFORM_FEEDBACK_BUFFER_MODE   返回GL_SEPARATE_ATTRIBS 或 GL_INTERLEAVED_ATTRIBS 表示变化反馈启用时的缓冲区模式
        //      GL_TRANSFORM_FEEDBACK_VARYINGS     返回程序的变化反馈模式中捕捉的输出变量
        //      GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH        返回程序的变化反馈模式中捕捉的输出变量名称的最大长度
        //      GL_VALIDATE_STATUS  查询最后一个校验操作的状态
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            // 获取着色器对象的信息日志
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject));
            // 删除一个程序对象
            GLES30.glDeleteProgram(programObject);
            return;
        }

        // Store the program object
        // 得到的结果就是一个程序对象，我们可以调用glUseProgram函数，用刚创建的程序对象作为它的参数，以激活这个程序对象
        mProgramObject = programObject;

        // 设置清除颜色
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused) {
        // Set the viewport
        // 通知OpenGL ES 用于绘制的2D渲染表面的原点、宽度和高度。
        // 在OpenGL ES 中，视口(Viewport) 定义所有OpenGL ES 渲染操作最终显示的2D矩形
        // 视口(Viewport) 由原点坐标(x,y)和宽度(width) 、高度(height)定义。
        GLES30.glViewport(0, 0, mWidth, mHeight);

        // Clear the color buffer
        // 清除屏幕
        // 在OpenGL ES中，绘图中涉及多种缓冲区类型：颜色、深度、模板。
        // 这个例子，绘制三角形，只向颜色缓冲区中绘制图形。在每个帧的开始，我们用glClear函数清除颜色缓冲区
        // 缓冲区将用glClearColor指定的颜色清除。
        // 这个例子，我们调用了GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f); 因此屏幕清为白色。
        // 清除颜色应该由应用程序在调用颜色缓冲区的glClear之前设置。
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        // Use the program object
        // 在glUseProgram函数调用之后，每个着色器调用和渲染调用都会使用这个程序对象（也就是之前写的着色器)了。
        // 当我们渲染一个物体时要使用着色器程序 , 将其设置为活动程序。这样就可以开始渲染了
        GLES30.glUseProgram(mProgramObject);

        //  顶点着色器允许我们指定任何以顶点属性为形式的输入。这使其具有很强的灵活性的同时，它还的确意味着我们必须手动指定输入数据的哪一个部分对应顶点着色器的哪一个顶点属性。所以，我们必须在渲染前指定OpenGL该如何解释顶点数据。
        //  我们的顶点缓冲数据会被解析为下面这样子：https://learnopengl-cn.github.io/img/01/04/vertex_attribute_pointer.png
        //   . 位置数据被储存为32位（4字节）浮点值。
        //   . 每个位置包含3个这样的值。
        //   . 在这3个值之间没有空隙（或其他值）。这几个值在数组中紧密排列(Tightly Packed)。
        //   . 数据中第一个值在缓冲开始的位置。

        // 有了这些信息我们就可以使用glVertexAttribPointer函数告诉OpenGL该如何解析顶点数据（应用到逐个顶点属性上）了：
        // Load the vertex data

        // 第一个参数指定我们要配置的顶点属性。因为我们希望把数据传递到这一个顶点属性中，所以这里我们传入0。
        // 第二个参数指定顶点属性的大小。顶点属性是一个vec3，它由3个值组成，所以大小是3。
        // 第三个参数指定数据的类型，这里是GL_FLOAT(GLSL中vec*都是由浮点数值组成的)。
        // 第四个参数定义我们是否希望数据被标准化(Normalize)。如果我们设置为GL_TRUE，所有数据都会被映射到0（对于有符号型signed数据是-1）到1之间。我们把它设置为GL_FALSE。
        // 第五个参数叫做步长(Stride)，它告诉我们在连续的顶点属性组之间的间隔。我们设置为0来让OpenGL决定具体步长是多少（只有当数值是紧密排列时才可用）。
        //      一旦我们有更多的顶点属性，我们就必须更小心地定义每个顶点属性之间的间隔，
        //      （译注: 这个参数的意思简单说就是从这个属性第二次出现的地方到整个数组0位置之间有多少字节）。
        // 最后一个参数的类型是void*，所以需要我们进行这个奇怪的强制类型转换。它表示位置数据在缓冲中起始位置的偏移量(Offset)。
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, mVertices);

        // 现在我们已经定义了OpenGL该如何解释顶点数据，我们现在应该使用glEnableVertexAttribArray，以顶点属性位置值作为参数，启用顶点属性；顶点属性默认是禁用的。
        GLES30.glEnableVertexAttribArray(0);

        // glDrawArrays函数第一个参数是我们打算绘制的OpenGL图元的类型。我们希望绘制的是一个三角形，这里传递GL_TRIANGLES给它。
        // 第二个参数指定了顶点数组的起始索引，我们这里填0。
        // 最后一个参数指定我们打算绘制多少个顶点，这里是3（我们只从我们的数据中渲染一个三角形，它只有3个顶点长）。
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
    }

    // /
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
    }
}
