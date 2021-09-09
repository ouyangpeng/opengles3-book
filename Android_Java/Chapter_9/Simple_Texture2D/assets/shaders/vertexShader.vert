#version 300 es
uniform float u_offset;
layout(location = 0) in vec4 a_position;
layout(location = 1) in vec2 a_texCoord;
out vec2 v_texCoord;
void main()
{
	gl_Position = a_position;
	// 顶点着色器以一个二分量纹理坐标作为顶点输入，并将其作为输出传递给片段着色器
	v_texCoord = a_texCoord;
}