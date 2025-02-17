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

// Example_6_3
//
//    This example demonstrates using client-side vertex arrays
//    and a constant vertex attribute
//

package com.openglesbook.example6_3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.openglesbook.common.ESShader;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

public class Example6_3Renderer implements GLSurfaceView.Renderer {
    private Context mContext;
    // Handle to a program object
    private int mProgramObject;

    // Additional member variables
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;

    private final float[] mVerticesData =
            {
                    0.0f, 0.5f, 0.0f,   // v0
                    -0.5f, -0.5f, 0.0f, // v1
                    0.5f, -0.5f, 0.0f   // v2
            };

    ///
    // Constructor
    //
    public Example6_3Renderer(Context context) {
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Load the shaders and get a linked program object
        // 得到的结果就是一个程序对象，我们可以调用glUseProgram函数，用刚创建的程序对象作为它的参数，以激活这个程序对象
        mProgramObject = ESShader.loadProgramFromAsset(mContext,
                "shaders/vertexShader.vert",
                "shaders/fragmentShader.frag");

        // 设置清除颜色
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused) {
        // Set the viewport
        GLES30.glViewport(0, 0, mWidth, mHeight);

        // Clear the color buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        // Set the vertex color to red
        // 设置顶点的颜色值
        // 加载index指定的通用顶点属性，加载(x,y,z,w)
        // opengl各个坐标系理解与转换公式 https://blog.csdn.net/grace_yi/article/details/109341926
        // x，y，z，w：指的不是四维，其中w指的是缩放因子
        // X轴为水平方向，Y轴为垂直方向，X和Y相互垂直
        // Z轴同时垂直于X和Y轴。Z轴的实际意义代表着三维物体的深度
        GLES30.glVertexAttrib4f(0, 1.0f, 0.0f, 0.0f, 1.0f);

        // Load the vertex position
        mVertices.position(0);

        //  指定通用顶点属性数组
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, 0, mVertices);

        // 启用 通用顶点属性数组
        GLES30.glEnableVertexAttribArray(1);

        // glDrawArrays函数第一个参数是我们打算绘制的OpenGL图元的类型。我们希望绘制的是一个三角形，这里传递GL_TRIANGLES给它。
        // 第二个参数指定了顶点数组的起始索引，我们这里填0。
        // 最后一个参数指定我们打算绘制多少个顶点，这里是3（我们只从我们的数据中渲染一个三角形，它只有3个顶点长）。
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        // 禁用 通用顶点属性数组
        GLES30.glDisableVertexAttribArray(1);
    }

    ///
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
    }
}
