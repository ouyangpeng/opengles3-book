#version 300 es              				
layout(location = 0) in vec4 a_position;
layout(location = 1) in vec3 a_normal;
out vec3 v_normal;
void main()
{
    // 取得一个位置和法线作为顶点输入。
    // 法线保存在球面的每个顶点上，用作纹理坐标，并传递给片段着色器
    gl_Position = a_position;
    v_normal = a_normal;
}