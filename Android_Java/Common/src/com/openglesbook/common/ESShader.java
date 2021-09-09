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

// ESShader
//
//    Utility functions for loading GLSL ES 3.0 shaders and creating program objects.
//

package com.openglesbook.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

public class ESShader {
    /**
     * brief Read a shader source into a String
     * @param context    context
     * @param fileName  fileName Name of shader file
     * @return  A String object containing shader source, otherwise null
     */
    private static String readShader(Context context, String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * brief Load a shader, check for compile errors, print error messages to output log
     * @param type   Type of shader (GL_VERTEX_SHADER or GL_FRAGMENT_SHADER)
     * @param shaderSrc shaderSrc Shader source string
     * @return  A new shader object on success, 0 on failure
     */
    public static int loadShader(int type, String shaderSrc) {
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
        if (compiled[0] == 0) {
            // 用glGetShaderInfoLog检索信息日志
            Log.e("ESShader", GLES30.glGetShaderInfoLog(shader));
            // 删除着色器对象
            GLES30.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    /**
     * brief Load a vertex and fragment shader, create a program object, link program. Errors output to log.
     * @param vertShaderSrc  Vertex shader source code
     * @param fragShaderSrc  Fragment shader source code
     * @return   A new program object linked with the vertex/fragment shader pair, 0 on failure
     */
    public static int loadProgram(String vertShaderSrc, String fragShaderSrc) {
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertShaderSrc);

        if (vertexShader == 0) {
            return 0;
        }

        fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragShaderSrc);

        if (fragmentShader == 0) {
            GLES30.glDeleteShader(vertexShader);
            return 0;
        }

        // Create the program object
        programObject = GLES30.glCreateProgram();

        if (programObject == 0) {
            return 0;
        }

        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);

        // Link the program
        GLES30.glLinkProgram(programObject);

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e("ESShader", "Error linking program:");
            Log.e("ESShader", GLES30.glGetProgramInfoLog(programObject));
            GLES30.glDeleteProgram(programObject);
            return 0;
        }

        // Free up no longer needed shader resources
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);

        return programObject;
    }

    /**
     * brief Load a vertex and fragment shader from "assets", create a program object, link program.  Errors output to log.
     * @param context context
     * @param vertexShaderFileName  Vertex shader source file name
     * @param fragShaderFileName    Fragment shader source file name
     * @return A new program object linked with the vertex/fragment shader pair, 0 on failure
     */
    public static int loadProgramFromAsset(Context context, String vertexShaderFileName, String fragShaderFileName) {
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        String vertShaderSrc = null;
        String fragShaderSrc = null;

        // Read vertex shader from assets
        vertShaderSrc = readShader(context, vertexShaderFileName);
        System.out.println("  vertShaderSrc = " + vertShaderSrc);
        if (vertShaderSrc == null) {
            return 0;
        }

        // Read fragment shader from assets
        fragShaderSrc = readShader(context, fragShaderFileName);
        System.out.println("  fragShaderSrc = " + fragShaderSrc);
        if (fragShaderSrc == null) {
            return 0;
        }

        // Load the vertex shader
        vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertShaderSrc);
        if (vertexShader == 0) {
            GLES30.glDeleteShader(vertexShader);
            return 0;
        }

        // Load the fragment shader
        fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragShaderSrc);
        if (fragmentShader == 0) {
            GLES30.glDeleteShader(fragmentShader);
            return 0;
        }

        // Create the program object
        programObject = GLES30.glCreateProgram();
        if (programObject == 0) {
            return 0;
        }

        // 在OpenGL ES3.0中，每个程序对象必须连接一个顶点着色器和一个片段着色器
        // 把之前编译的着色器附加到程序对象上
        // 着色器可以在任何时候连接-----在连接到程序之前不一定需要编译，甚至可以没有源代码。
        // 唯一要求是：每个程序对象必须有且只有一个顶点着色器和一个片段着色器与之连接
        // 除了连接着色器之外，你还可以用glDetachShader断开着色器的连接
        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);

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
            Log.e("ESShader", "Error linking program:");
            // 获取着色器对象的信息日志
            Log.e("ESShader", GLES30.glGetProgramInfoLog(programObject));
            // 删除一个程序对象
            GLES30.glDeleteProgram(programObject);
            return 0;
        }

        // Free up no longer needed shader resources
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);

        return programObject;
    }
}
