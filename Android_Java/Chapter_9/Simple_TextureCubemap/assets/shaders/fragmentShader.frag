#version 300 es              						 
precision mediump float;
// 纹理坐标是3个分量
in vec3 v_normal;
layout(location = 0) out vec4 outColor;
// 采样器必须是samplerCube
uniform samplerCube s_texture;
void main()
{
    // 使用内建函数texture，以法线作为纹理坐标从立方图中读取
    // 内建函数采用如下形式：
    // vec4  texture(samplerCube sampler, vec3 coord [,float bias]);
    // @param sampler 采样器绑定到一个纹理单元，指定纹理为读取来源
    // @param coord 用于从立方图读取的3D纹理坐标
    // @param bias  可选参数，提供用于纹理读取的mip贴图偏置。这允许着色器明确地偏置用于mip贴图选择的LOD的计算值
    outColor = texture(s_texture, v_normal);
}                                              